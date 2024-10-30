package tests.api;

import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import tests.models.OrderData;

import static io.restassured.RestAssured.given;

public class OrderApi {
    private static final String BASE_URL = "https://qa-scooter.praktikum-services.ru/api/v1/orders";

    @Step("Создание заказа")
    public Response createOrder(OrderData orderData) {
        return given()
                .contentType(ContentType.JSON)
                .body(orderData) // Автоматическая сериализация OrderData
                .post(BASE_URL);
    }

    @Step("Получение списка заказов")
    public Response getOrdersList() {
        return given()
                .contentType(ContentType.JSON)
                .get(BASE_URL);
    }
}
