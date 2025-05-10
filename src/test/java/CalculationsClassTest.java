import org.junit.jupiter.api.Test;
import projects.CalculationsClass;
import projects.Order;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CalculationsClassTest {

    private final String ordersPath = "src/main/resources/data/orders.json";
    private final String paymentMethodsPath = "src/main/resources/data/paymentmethods.json";

    private CalculationsClass calculationsClass = new CalculationsClass(ordersPath, paymentMethodsPath);

//    @Test
//    public void testCalculatingBestOptionCorrectValue() {
//        Order order = new Order("1", 1, new ArrayList<>());
//        double discount = calculationsClass.calculateBestOption(order);
//        assertEquals(15.0, discount);
//    }
}
