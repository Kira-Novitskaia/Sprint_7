package tests;

import tests.api.OrderApi;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.Test;

import static org.hamcrest.Matchers.notNullValue;

public class GetOrdersListTest {

    private final OrderApi orderApi = new OrderApi();

    @Test
    @DisplayName("get + ручка /api/v1/orders") // имя теста
    @Description("Проверка получения списка заказов") // описание теста
    @Step("Получение списка заказов")
    public void getOrdersListTest() {
        Response response = orderApi.getOrders();
        response.then()
                .statusCode(200)
                .body("orders", notNullValue());
    }
}
