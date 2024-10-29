package tests;

import tests.api.CourierApi;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class LoginCourierTest {

    private CourierApi courierApi;
    private String login;
    private String password;
    private Integer courierId;

    @Before
    public void setUp() {
        courierApi = new CourierApi();
        login = "existingCourier_" + System.currentTimeMillis();
        password = "password123";

        // Создаем курьера перед тестами
        Response createResponse = courierApi.createCourier(login, password);
        createResponse.then().statusCode(SC_CREATED);
        courierId = createResponse.path("id");
    }

    @After
    public void tearDown() {
        // Удаляем курьера после тестов, если он был создан
        if (courierId != null) {
            courierApi.deleteCourier(courierId).then().statusCode(SC_OK);
        }
    }

    @Test
    @DisplayName("Успешная авторизация курьера")
    @Description("Проверка ввода логина созданного курьера")
    public void successfulCourierLoginTest() {
        // Авторизуемся под созданным курьером и проверяем успешность авторизации
        Response loginResponse = courierApi.loginCourier(login, password);
        loginResponse.then().statusCode(SC_OK).body("id", notNullValue());
    }

    @Test
    @DisplayName("Авторизация с неверными данными")
    @Description("Проверка ввода логина созданного курьера с НЕ верными данными, ожидание 404")
    public void courierLoginWithInvalidDataTest() {
        // Запрос с неверными данными для авторизации
        Response invalidLoginResponse = courierApi.loginCourier("wrongLogin", "wrongPassword");
        invalidLoginResponse.then().statusCode(SC_NOT_FOUND)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Авторизация без обязательных полей")
    @Description("Проверка ввода логина созданного курьера без логина или пароля, ожидание 400 Bad Request")
    public void courierLoginWithoutRequiredFieldsTest() {
        // Запрос с пустым логином
        Response emptyLoginResponse = courierApi.loginCourier("", password);
        emptyLoginResponse.then().statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для входа"));

        // Запрос с пустым паролем
        Response emptyPasswordResponse = courierApi.loginCourier(login, "");
        emptyPasswordResponse.then().statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для входа"));
    }
}
