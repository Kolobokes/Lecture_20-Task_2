import com.codeborne.selenide.SelenideElement;
import com.sun.tools.javac.util.Assert;
import io.github.bonigarcia.wdm.managers.SeleniumServerStandaloneManager;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.graalvm.compiler.nodes.graphbuilderconf.InvocationPlugins;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import java.time.Duration;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.withText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static io.restassured.RestAssured.given;

public class AppIbankTest {
    private static RequestSpecification requestSpec = new RequestSpecBuilder()
            .setBaseUri("http://localhost")
            .setPort(9999)
            .setAccept(ContentType.JSON)
            .setContentType(ContentType.JSON)
            .log(LogDetail.ALL)
            .build();

    static User testUsers (boolean active){

        if (active) {
            User testUser = new User("IvanovII", "1230", "active");

            return testUser;
        }
        else {
            User testUser = new User("PetrovTT", "1231", "blocked");

            return testUser;
        }
    }

    @BeforeAll
    static void setUpAll() {

        User testUserActive = testUsers(true);
        User testUserBlocked = testUsers(false);
        // сам запрос
        given() // "дано"
                .spec(requestSpec) // указываем, какую спецификацию используем
                .body(testUserActive) // передаём в теле объект, который будет преобразован в JSON
                .when() // "когда"
                .post("/api/system/users") // на какой путь, относительно BaseUri отправляем запрос
                .then() // "тогда ожидаем"
                .statusCode(200); // код 200 OK

        // сам запрос
        given() // "дано"
                .spec(requestSpec) // указываем, какую спецификацию используем
                .body(testUserBlocked) // передаём в теле объект, который будет преобразован в JSON
                .when() // "когда"
                .post("/api/system/users") // на какой путь, относительно BaseUri отправляем запрос
                .then() // "тогда ожидаем"
                .statusCode(200); // код 200 OK
    }

    @Test
    public void LoginActiveUserTest(){

        User testUserActive = testUsers(true);
        User testUserBlocked = testUsers(false);

        open("http://localhost:9999");
        SelenideElement form = $("#root");
        form.$("[name='login']").setValue(testUserActive.login);
        form.$("[name='password']").setValue(testUserActive.password);
        form.$(withText("Продолжить")).click();
        $(withText("Личный кабинет")).shouldBe(visible, Duration.ofSeconds(15));
    }

    @Test
    public void LoginBlockedUserTest(){

        User testUserBlocked = testUsers(false);

        open("http://localhost:9999");
        SelenideElement form = $("#root");
        form.$("[name='login']").setValue(testUserBlocked.login);
        form.$("[name='password']").setValue(testUserBlocked.password);
        form.$(withText("Продолжить")).click();
        $("[data-test-id='error-notification']").shouldBe(visible);
    }

}

