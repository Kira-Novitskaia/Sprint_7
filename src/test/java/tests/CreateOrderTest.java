package tests;

import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Map;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

@RunWith(Parameterized.class)
public class CreateOrderTest {

    private final Map<String, Object> orderParams;

    public CreateOrderTest(Map<String, Object> orderParams) {
        this.orderParams = orderParams;
    }

    @Parameterized.Parameters
    public static Object[] getOrderData() {
        return new Object[]{
                Map.of("color", List.of("BLACK")),
                Map.of("color", List.of("GREY")),
                Map.of("color", List.of("BLACK", "GREY")),
                Map.of() // без указания цвета
        };
    }

    @Test
    @DisplayName("post + ручка /api/v1/orders") // имя теста
    @Description("Проверка создания заказа с выбором цвета самоката, ожидание код 201 Created + track") // описание теста
    @Step("Тестирование создания заказа с параметрами: {orderParams}")
    public void createOrderTest() {
        Response response = createOrder(orderParams);
        response.then()
                .statusCode(201)
                .body("track", notNullValue());
    }

    @Step("Создание заказа с параметрами: {params}")
    private Response createOrder(Map<String, Object> params) {
        Response response = given()
                .contentType(ContentType.JSON)
                .body(params)
                .post("https://qa-scooter.praktikum-services.ru/api/v1/orders");

        logResponse(response);  // Логируем ответ для отладки
        return response;
    }

    @Step("Ответ от сервера: {response}")
    private void logResponse(Response response) {
        response.prettyPrint();
    }
}
