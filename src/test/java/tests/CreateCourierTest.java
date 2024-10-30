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

public class CreateCourierTest {
    private CourierApi courierApi;
    private Integer courierId;
    private String login;
    private String password;

    @Before
    public void setUp() {
        courierApi = new CourierApi();
        login = "courier_" + System.currentTimeMillis();
        password = "password_" + System.currentTimeMillis();
    }

    @After
    public void tearDown() {
        deleteCourierIfExists();
    }

    @Test
    @DisplayName("Успешное создание курьера")
    @Description("Проверка успешного создания курьера")
    public void successfulCourierCreationTest() {
        CourierData courierData = new CourierData(login, password);
        Response response = createCourier(courierData);
        response.then().statusCode(SC_CREATED).body("ok", equalTo(true));
        courierId = response.path("id");
    }

    @Test
    @DisplayName("Дублирующее создание курьера")
    @Description("Проверка на создание курьера с уже существующим логином")
    public void duplicateCourierCreationTest() {
        CourierData courierData = new CourierData(login, password);
        Response firstResponse = createCourier(courierData);
        firstResponse.then().statusCode(SC_CREATED);
        courierId = firstResponse.path("id");

        Response duplicateResponse = createCourier(courierData);
        duplicateResponse.then().statusCode(SC_CONFLICT)
                .body("message", equalTo("Этот логин уже используется"));
    }

    @Test
    @DisplayName("Создание курьера без обязательных полей")
    @Description("Проверка создания курьера без логина или пароля")
    public void courierCreationWithoutRequiredFieldsTest() {
        CourierData courierDataWithNoLogin = new CourierData(null, password);
        Response noLoginResponse = createCourier(courierDataWithNoLogin);
        noLoginResponse.then().statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));

        CourierData courierDataWithNoPassword = new CourierData(login, null);
        Response noPasswordResponse = createCourier(courierDataWithNoPassword);
        noPasswordResponse.then().statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Step("Создание курьера с данными: {courierData}")
    private Response createCourier(CourierData courierData) {
        return courierApi.createCourier(courierData);
    }

    @Step("Удаление курьера, если он был создан")
    private void deleteCourierIfExists() {
        if (courierId != null) {
            courierApi.deleteCourier(courierId).then().statusCode(SC_OK);
            courierId = null;
        }
    }
}
