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
import static org.hamcrest.Matchers.notNullValue;

public class LoginCourierTest {

    private String login;
    private String password;
    private Integer courierId;

    @Before
    public void setUp() {
        login = "existingCourier_" + System.currentTimeMillis(); // Уникальный логин для каждого теста
        password = "password123";

        // Создаем курьера перед тестами
        courierId = createCourier(login, password)
                .then()
                .statusCode(201) // Проверка, что курьер создан успешно
                .extract()
                .path("id");
    }

    @After
    public void tearDown() {
        // Удаляем курьера после тестов, если он был создан
        if (courierId != null) {
            deleteCourier(courierId);
        }
    }

    @Test
    @DisplayName("post + ручка /api/v1/courier/login") // имя теста
    @Description("Проверка ввода логина созданного курьера") // описание теста
    @Step("Успешная авторизация курьера")
    public void successfulCourierLoginTest() {
        // Авторизуемся под созданным курьером и проверяем успешность авторизации
        loginCourier(login, password)
                .then()
                .statusCode(200)
                .body("id", notNullValue()); // Проверка, что возвращается ID курьера
    }

    @Test
    @DisplayName("post + ручка /api/v1/courier/login") // имя теста
    @Description("Проверка ввода логина созданного курьера с НЕ верными данными, ожидание 404") // описание теста
    @Step("Авторизация с неверными данными")
    public void courierLoginWithInvalidDataTest() {
        // Запрос с неверными данными для авторизации
        loginCourier("wrongLogin", "wrongPassword")
                .then()
                .statusCode(404) // Ошибка 404 для случая несуществующего пользователя
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("post + ручка /api/v1/courier/login") // имя теста
    @Description("Проверка ввода логина созданного курьера без логина или пароля, ожидание 400 Bad Request") // описание теста
    @Step("Авторизация без обязательных полей")
    public void courierLoginWithoutRequiredFieldsTest() {
        // Запрос с пустым логином
        loginCourier("", password)
                .then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для входа"));

        // Запрос с пустым паролем
        loginCourier(login, "")
                .then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    // Методы для работы с API

    @Step("Создание курьера с логином: {login} и паролем: {password}")
    private Response createCourier(String login, String password) {
        Map<String, String> body = Map.of("login", login, "password", password);
        return given()
                .contentType(ContentType.JSON)
                .body(body)
                .post("https://qa-scooter.praktikum-services.ru/api/v1/courier");
    }

    @Step("Логин курьера с логином: {login} и паролем: {password}")
    private Response loginCourier(String login, String password) {
        // Используем пустые строки вместо null, чтобы избежать NullPointerException
        Map<String, String> body = new HashMap<>();
        body.put("login", login != null ? login : "");
        body.put("password", password != null ? password : "");
        return given()
                .contentType(ContentType.JSON)
                .body(body)
                .post("https://qa-scooter.praktikum-services.ru/api/v1/courier/login");
    }

    @Step("Удаление курьера с ID: {courierId}")
    private void deleteCourier(int courierId) {
        given()
                .delete("https://qa-scooter.praktikum-services.ru/api/v1/courier/" + courierId)
                .then()
                .statusCode(200);
    }
}
