package tests.api;

import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import tests.models.CourierData;

import static io.restassured.RestAssured.given;

public class CourierApi {
    private static final String BASE_URL = "https://qa-scooter.praktikum-services.ru/api/v1/courier";

    @Step("Создание курьера")
    public Response createCourier(CourierData courierData) {
        return given()
                .contentType(ContentType.JSON)
                .body(courierData) // Автоматическая сериализация CourierData
                .post(BASE_URL);
    }

    @Step("Логин курьера")
    public Response loginCourier(CourierData courierData) {
        return given()
                .contentType(ContentType.JSON)
                .body(courierData)
                .post(BASE_URL + "/login");
    }

    @Step("Удаление курьера по ID")
    public Response deleteCourier(Integer courierId) {
        String json = String.format("{\"id\": %d}", courierId);
        return given()
                .contentType(ContentType.JSON)
                .body(json)
                .delete(BASE_URL);
    }
}
