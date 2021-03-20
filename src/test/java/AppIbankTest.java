import com.codeborne.selenide.SelenideElement;
import com.sun.tools.javac.util.Assert;
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

    @BeforeAll
    static void setUpAll() {
        // сам запрос
        given() // "дано"
                .spec(requestSpec) // указываем, какую спецификацию используем
                .body(new User("vasya", "123", "active")) // передаём в теле объект, который будет преобразован в JSON
                .when() // "когда"
                .post("/api/system/users") // на какой путь, относительно BaseUri отправляем запрос
                .then() // "тогда ожидаем"
                .statusCode(200); // код 200 OK
    }

    @Test
    public void LoginActiveUserTest(){

        open("http://localhost:9999");
        SelenideElement form = $("#root");
        form.$("[data-test-id='login'] .input__inner").setValue("vasya");
        form.$("[data-test-id='password'] .input__inner").setValue("123");
        form.$(withText("Продолжить")).click();
        $("[data-test-id='success-notification']").shouldBe(visible, Duration.ofSeconds(15));
 //       $("[data-test-id='success-notification'] .notification__content")
 //               .shouldHave(exactText("Встреча успешно запланирована на " + correctDate));
    }

}

