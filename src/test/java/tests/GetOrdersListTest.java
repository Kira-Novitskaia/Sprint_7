package tests;

import tests.api.OrderApi;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import org.junit.Test;
import io.restassured.response.Response;

import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.notNullValue;

public class GetOrdersListTest {

    private final OrderApi orderApi = new OrderApi();

    @Test
    @DisplayName("Получение списка заказов")
    @Description("Проверка получения списка заказов")
    public void getOrdersListTest() {
        Response response = getOrdersList();
        response.then().statusCode(SC_OK).body("orders", notNullValue());
    }

    @Step("Получение списка заказов")
    private Response getOrdersList() {
        return orderApi.getOrdersList();
    }
}
