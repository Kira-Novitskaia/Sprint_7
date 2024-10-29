package tests.api;

import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

import java.util.Map;

public class OrderApi {

    @Step("Получить список заказов")
    public Response getOrders() {
        Response response = given()
                .contentType(ContentType.JSON)
                .get("https://qa-scooter.praktikum-services.ru/api/v1/orders");

        logResponse(response);
        return response;
    }

    @Step("Создать заказ с параметрами: {params}")
    public Response createOrder(Map<String, Object> params) {
        Response response = given()
                .contentType(ContentType.JSON)
                .body(params)
                .post("https://qa-scooter.praktikum-services.ru/api/v1/orders");

        logResponse(response);
        return response;
    }

    @Step("Ответ от сервера: {response}")
    private void logResponse(Response response) {
        response.prettyPrint();
    }
}
