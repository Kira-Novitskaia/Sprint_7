package tests;

import tests.api.OrderApi;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.notNullValue;

@RunWith(Parameterized.class)
public class CreateOrderTest {

    private final Map<String, Object> orderParams;
    private final OrderApi orderApi = new OrderApi();

    public CreateOrderTest(Map<String, Object> orderParams) {
        this.orderParams = orderParams;
    }

    @Parameterized.Parameters(name = "Тестовые данные: {0}")
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
        Response response = orderApi.createOrder(orderParams);
        response.then()
                .statusCode(201)
                .body("track", notNullValue());
    }
}
