package tests;

import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class CreateCourierTest {
    private Integer courierId;
    private String login;
    private String password;

    @Before
    public void setUp() {
        login = "courier_" + System.currentTimeMillis();
        password = "password123";
    }

    @After
    public void tearDown() {
        if (courierId != null) {
            deleteCourier(courierId);
            courierId = null;
        }
    }

    @Test
    @DisplayName("post + ручка /api/v1/courier") // имя теста
    @Description("Проверка успешного создания курьера, ожидание код 200 и \"ok\": true") // описание теста
    @Step("Успешное создание курьера")
    public void successfulCourierCreationTest() {
        courierId = createCourier(login, password)
                .then()
                .statusCode(201)
                .body("ok", equalTo(true))
                .extract()
                .path("id");
    }

    @Test
    @DisplayName("post + ручка /api/v1/courier") // имя теста
    @Description("Проверка на дубляж создания курьера, ожидание код 409 Сonflict") // описание теста
    @Step("Создание дубликата курьера")
    public void duplicateCourierCreationTest() {
        courierId = createCourier(login, password)
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        createCourier(login, password)
                .then()
                .statusCode(409)
                .body("message", equalTo("Этот логин уже используется"));
    }

    @Test
    @DisplayName("post + ручка /api/v1/courier") // имя теста
    @Description("Проверка создания курьера без логина или пароля, ожидание код 400 Bad Request") // описание теста
    @Step("Создание курьера с пропущенными обязательными полями")
    public void courierCreationWithoutRequiredFieldsTest() {
        // Пропускаем поле логина, передавая в login значение null
        createCourierWithOptionalFields(null, password)
                .then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));

        // Пропускаем поле пароля, передавая в password значение null
        createCourierWithOptionalFields(login, null)
                .then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    // Метод создания курьера с полным набором данных
    @Step("Создание курьера с логином: {login} и паролем: {password}")
    private Response createCourier(String login, String password) {
        Map<String, String> body = Map.of("login", login, "password", password);
        return given()
                .contentType(ContentType.JSON)
                .body(body)
                .post("https://qa-scooter.praktikum-services.ru/api/v1/courier");
    }

    // Метод создания курьера с возможностью пропустить обязательные поля
    @Step("Создание курьера с неполными данными: логин={login}, пароль={password}")
    private Response createCourierWithOptionalFields(String login, String password) {
        Map<String, String> body = new HashMap<>();
        if (login != null) {
            body.put("login", login);
        }
        if (password != null) {
            body.put("password", password);
        }
        return given()
                .contentType(ContentType.JSON)
                .body(body)
                .post("https://qa-scooter.praktikum-services.ru/api/v1/courier");
    }

    @Step("Удаление курьера с ID: {courierId}")
    private void deleteCourier(int courierId) {
        given()
                .delete("https://qa-scooter.praktikum-services.ru/api/v1/courier/" + courierId)
                .then()
                .statusCode(200);
    }
}
