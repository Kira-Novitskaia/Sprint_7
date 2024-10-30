package tests;

import tests.api.CourierApi;
import tests.models.CourierData;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
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
    private Integer courierId;
    private String login;
    private String password;

    @Before
    public void setUp() {
        courierApi = new CourierApi();
        login = "existingCourier_" + System.currentTimeMillis();
        password = "password123";
        courierId = createCourier(login, password);
    }

    @After
    public void tearDown() {
        deleteCourierIfExists();
    }

    @Test
    @DisplayName("Успешная авторизация курьера")
    @Description("Проверка авторизации созданного курьера")
    public void successfulCourierLoginTest() {
        CourierData courierData = new CourierData(login, password);
        Response loginResponse = loginCourier(courierData);
        loginResponse.then().statusCode(SC_OK).body("id", notNullValue());
    }

    @Test
    @DisplayName("Авторизация с отсутствующим логином")
    public void courierLoginWithoutLoginTest() {
        Response response = loginCourier(new CourierData(null, password));
        response.then().statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Авторизация с отсутствующим паролем")
    public void courierLoginWithoutPasswordTest() {
        Response response = loginCourier(new CourierData(login, null));
        response.then().statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Step("Создание курьера с логином: {login} и паролем: {password}")
    private Integer createCourier(String login, String password) {
        CourierData courierData = new CourierData(login, password);
        Response response = courierApi.createCourier(courierData);
        response.then().statusCode(SC_CREATED);
        return response.path("id");
    }

    @Step("Авторизация курьера с данными: {courierData}")
    private Response loginCourier(CourierData courierData) {
        return courierApi.loginCourier(courierData);
    }

    @Step("Удаление курьера, если он был создан")
    private void deleteCourierIfExists() {
        if (courierId != null) {
            courierApi.deleteCourier(courierId).then().statusCode(SC_OK);
            courierId = null;
        }
    }
}
