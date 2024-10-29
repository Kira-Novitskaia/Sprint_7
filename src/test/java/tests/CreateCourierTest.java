package tests;

import tests.api.CourierApi;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import io.restassured.response.Response;

import static org.apache.http.HttpStatus.*;

public class CreateCourierTest {
    private CourierApi courierApi;
    private Integer courierId;
    private String login;
    private String password;

    @Before
    public void setUp() {
        courierApi = new CourierApi();
        login = "courier_" + System.currentTimeMillis();
        password = "password123";
    }

    @After
    public void tearDown() {
        if (courierId != null) {
            courierApi.deleteCourier(courierId)
                    .then()
                    .statusCode(SC_OK); // SC_OK вместо 200
            courierId = null;
        }
    }

    @Test
    @DisplayName("Успешное создание курьера")
    @Description("Проверка успешного создания курьера, ожидание код 201 и 'ok': true")
    public void successfulCourierCreationTest() {
        Response response = courierApi.createCourier(login, password);
        courierApi.verifyResponse(response, SC_CREATED, null); // SC_CREATED вместо 201
        courierId = response.path("id");
    }

    @Test
    @DisplayName("Дублирующее создание курьера")
    @Description("Проверка на дубляж создания курьера, ожидание код 409 Conflict")
    public void duplicateCourierCreationTest() {
        Response firstResponse = courierApi.createCourier(login, password);
        courierApi.verifyResponse(firstResponse, SC_CREATED, null); // SC_CREATED вместо 201
        courierId = firstResponse.path("id");

        Response duplicateResponse = courierApi.createCourier(login, password);
        courierApi.verifyResponse(duplicateResponse, SC_CONFLICT, "Этот логин уже используется"); // SC_CONFLICT вместо 409
    }

    @Test
    @DisplayName("Создание курьера без обязательных полей")
    @Description("Проверка создания курьера без логина или пароля, ожидание код 400 Bad Request")
    public void courierCreationWithoutRequiredFieldsTest() {
        // Создание курьера без логина
        Response noLoginResponse = courierApi.createCourier(null, password); // Передаем null для логина
        courierApi.verifyResponse(noLoginResponse, SC_BAD_REQUEST, "Недостаточно данных для создания учетной записи");

        // Создание курьера без пароля
        Response noPasswordResponse = courierApi.createCourier(login, null); // Передаем null для пароля
        courierApi.verifyResponse(noPasswordResponse, SC_BAD_REQUEST, "Недостаточно данных для создания учетной записи");

        // Создание курьера без обоих полей
        Response noCredentialsResponse = courierApi.createCourier(null, null); // Передаем null для обоих полей
        courierApi.verifyResponse(noCredentialsResponse, SC_BAD_REQUEST, "Недостаточно данных для создания учетной записи");
    }
}
