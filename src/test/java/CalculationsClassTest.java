import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import projects.CalculationsClass;
import projects.JsonFileReader;
import projects.Order;
import projects.PaymentMethod;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

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

    // test whether CalculationsClass.chooseBestOption() throws exception when there are insufficient funds for orders to be paid
//    @Test
//    public void testChooseBestOptionThrowsExceptionWhenInsufficientFunds() {
//        CalculationsClass calculationsClassObject1 = new CalculationsClass("src/main/resources/data/orders.json",
//                "src/test/data/insufficientFunds.json");
//        assertThrows(RuntimeException.class, calculationsClassObject1::chooseBestOption);
//    }

    // further tests checking whether costs calculated by CalculationsClass.chooseBestOption() are correct
    @Test
    public void testChooseBestOptionCalculatesCostsCorrectlyTest1() {
        CalculationsClass calculationsClassObject1 = new CalculationsClass("src/test/data/valueTests/test1/orders.json",
                "src/test/data/valueTests/test1/paymentmethods.json");
        calculationsClassObject1.calculateCosts();
        HashMap<String, Double> costs = new HashMap<>(Map.of("PUNKTY", 150.0, "BankY", 76.0, "BankX", 244.5));
        assertEquals(costs, calculationsClassObject1.getCosts());
    }

    // test case where there are enough points to cover all orders
    @Test
    public void testChooseBestOptionCalculatesCostsCorrectlyTest2() {
        CalculationsClass calculationsClassObject1 = new CalculationsClass("src/test/data/valueTests/test2/orders.json",
                "src/test/data/valueTests/test2/paymentmethods.json");
        calculationsClassObject1.calculateCosts();
        HashMap<String, Double> costs = new HashMap<>(Map.of("PUNKTY", 153.00, "VisaGold", 0.0));
        assertEquals(costs, calculationsClassObject1.getCosts());
    }

    // test case where there are not enough points to cover at least 10% of any order, but for every order card discount is available
    @Test
    public void testChooseBestOptionCalculatesCostsCorrectlyTest3() {
        CalculationsClass calculationsClassObject1 = new CalculationsClass("src/test/data/valueTests/test3/orders.json",
                "src/test/data/valueTests/test3/paymentmethods.json");
        calculationsClassObject1.calculateCosts();
        HashMap<String, Double> costs = new HashMap<>(Map.of("PUNKTY", 0.0, "CardA", 225.0));
        assertEquals(costs, calculationsClassObject1.getCosts());
    }

    // test case where there are not enough funds to cover all orders
    @Test
    public void testChooseBestOptionCalculatesCostsCorrectlyTest4() {
        CalculationsClass calculationsClassObject1 = new CalculationsClass("src/test/data/valueTests/test4/orders.json",
                "src/test/data/valueTests/test4/paymentmethods.json");
        calculationsClassObject1.calculateCosts();
        HashMap<String, Double> costs = new HashMap<>(Map.of("PUNKTY", 42.5, "BankX", 135.0));
        assertEquals(costs, calculationsClassObject1.getCosts());
    }

    // test case where there are not enough funds to cover all orders
    @Test
    public void testChooseBestOptionCalculatesCostsCorrectlyTest5() {
        CalculationsClass calculationsClassObject1 = new CalculationsClass("src/test/data/valueTests/test5/orders.json",
                "src/test/data/valueTests/test5/paymentmethods.json");
        calculationsClassObject1.calculateCosts();
        HashMap<String, Double> costs = new HashMap<>(Map.of("PUNKTY", 0.0, "CardGold", 90.0));
        assertEquals(costs, calculationsClassObject1.getCosts());
    }

    // test case where there are not enough funds to cover all orders
    @Test
    public void testChooseBestOptionCalculatesCostsCorrectlyTest6() {
        CalculationsClass calculationsClassObject1 = new CalculationsClass("src/test/data/valueTests/test6/orders.json",
                "src/test/data/valueTests/test6/paymentmethods.json");
        calculationsClassObject1.calculateCosts();
        HashMap<String, Double> costs = new HashMap<>(Map.of("PUNKTY", 0.0, "BankA", 90.0));
        assertEquals(costs, calculationsClassObject1.getCosts());
    }

    // test case where there are enough points to cover all orders, and the discount obtained by using points is greater than the one by card
    @Test
    public void testChooseBestOptionCalculatesCostsCorrectlyTest7() {
        CalculationsClass calculationsClassObject1 = new CalculationsClass("src/test/data/valueTests/test7/orders.json",
                "src/test/data/valueTests/test7/paymentmethods.json");
        calculationsClassObject1.calculateCosts();
        HashMap<String, Double> costs = new HashMap<>(Map.of("PUNKTY", 153.0, "VisaGold", 0.0));
        assertEquals(costs, calculationsClassObject1.getCosts());
    }

    // test case where there are enough funds to cover all orders by card, but not enough points to do the same or to cover at least 10% of any order
    @Test
    public void testChooseBestOptionCalculatesCostsCorrectlyTest8() {
        CalculationsClass calculationsClassObject1 = new CalculationsClass("src/test/data/valueTests/test8/orders.json",
                "src/test/data/valueTests/test8/paymentmethods.json");
        calculationsClassObject1.calculateCosts();
        HashMap<String, Double> costs = new HashMap<>(Map.of("PUNKTY", 0.0, "CardA", 225.0));
        assertEquals(costs, calculationsClassObject1.getCosts());
    }

    // test to check whether when discounts are the same for partial payment and payment wholly by card, points will be prioritised and will all of them be spent
    @Test
    public void testChooseBestOptionCalculatesCostsCorrectlyTest9() {
        CalculationsClass calculationsClassObject1 = new CalculationsClass("src/test/data/valueTests/test9/orders.json",
                "src/test/data/valueTests/test9/paymentmethods.json");
        calculationsClassObject1.calculateCosts();
        HashMap<String, Double> costs = new HashMap<>(Map.of("PUNKTY", 50.0, "CardA", 200.0));
        assertEquals(costs, calculationsClassObject1.getCosts());
    }

}
