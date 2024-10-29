package tests.api;

import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class CourierApi {
    private static final String BASE_URL = "https://qa-scooter.praktikum-services.ru/api/v1/courier";

    @Step("Создание курьера с логином: {login} и паролем: {password}")
    public Response createCourier(String login, String password) {
        // Замена null на пустые строки
        login = login != null ? login : "";
        password = password != null ? password : "";

        return given()
                .contentType(ContentType.JSON)
                .body(Map.of("login", login, "password", password))
                .post(BASE_URL);
    }

    @Step("Удаление курьера с ID: {courierId}")
    public Response deleteCourier(int courierId) {
        return given()
                .delete(BASE_URL + "/" + courierId);
    }

    @Step("Аутентификация курьера с логином: {login} и паролем: {password}")
    public Response loginCourier(String login, String password) {
        // Замена null на пустые строки
        login = login != null ? login : "";
        password = password != null ? password : "";

        return given()
                .contentType(ContentType.JSON)
                .body(Map.of("login", login, "password", password))
                .post(BASE_URL + "/login");
    }

    @Step("Проверка успешного ответа с кодом {expectedStatus}")
    public void verifyResponse(Response response, int expectedStatus, String expectedMessage) {
        response.then().statusCode(expectedStatus);
        if (expectedMessage != null) {
            response.then().body("message", equalTo(expectedMessage));
        }
    }
}
