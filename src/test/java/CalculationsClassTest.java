import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import projects.CalculationsClass;
import projects.JsonFileReader;
import projects.Order;
import projects.PaymentMethod;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CalculationsClassTest {

    private  CalculationsClass calculationsClassObject;
    private final String ordersPath = "src/main/resources/data/orders.json";
    private final String paymentMethodsPath = "src/main/resources/data/paymentmethods.json";

    @BeforeEach
    public void setUp() {
        calculationsClassObject = new CalculationsClass(ordersPath, paymentMethodsPath);
    }

    // test whether the constructor of CalculationsClass works properly
    @Test
    public void setupTest() {
        JsonFileReader jsonFileReader = new JsonFileReader(ordersPath, paymentMethodsPath);
        assertEquals(jsonFileReader.getOrders(), calculationsClassObject.getOrders());
        assertEquals(jsonFileReader.getOrdersByPromotion(), calculationsClassObject.getOrdersByPromotion());
        assertEquals(jsonFileReader.getPaymentMethods(), calculationsClassObject.getPaymentMethods());
        assertEquals(jsonFileReader.getPaymentMethodsById(), calculationsClassObject.getPaymentMethodsById());
    }

    // test whether CalculationsClass.generateAssignments() generates correct number of assignments
    @Test
    public void testGenerateAssignmentsAddsCorrectNumberOfAssignments() {
        calculationsClassObject.generateAssignments();
        assertEquals(8, calculationsClassObject.getAssignments().size());
    }

    // test whether after running CalculationsClass.chooseBestOption() all orders are paid off as expected
    @Test
    public void testChooseBestOptionPaysAllOrdersOff() {
        calculationsClassObject.chooseBestOption();
        for (Order order : calculationsClassObject.getOrders()) {
            assertTrue(order.isPaid());
        }
    }

    // test whether funds left for every payment method are equal to the expected value after running CalculationsClass.chooseBestOption()
    @Test
    public void testChooseBestOptionUsesFundsAsExpected() {
        calculationsClassObject.chooseBestOption();
        HashMap<String, PaymentMethod> paymentMethodsById = calculationsClassObject.getPaymentMethodsById();
        assertEquals(0.0, paymentMethodsById.get("PUNKTY").getLimit());
        assertEquals(15.0, paymentMethodsById.get("mZysk").getLimit());
        assertEquals(10.0, paymentMethodsById.get("BosBankrut").getLimit());
    }

    // test whether CalculationsClass.chooseBestOption() correctly calculates used funds for given payment methods
    @Test
    public void testChooseBestOptionCalculatesUsedFundsCorrectly() {
        calculationsClassObject.chooseBestOption();
        HashMap<String, Double> costs = calculationsClassObject.getCosts();
        assertEquals(100.0, costs.get("PUNKTY"));
        assertEquals(165.0, costs.get("mZysk"));
        assertEquals(190.0, costs.get("BosBankrut"));
    }
}
