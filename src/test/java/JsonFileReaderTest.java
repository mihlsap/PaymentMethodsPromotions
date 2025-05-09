import org.junit.jupiter.api.Test;
import projects.JsonFileReader;
import projects.Order;
import projects.PaymentMethod;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class JsonFileReaderTest {

    // test checking whether the size of ArrayList returned by JsonFileReader.readOrders() is correct
    @Test
    public void testReadingOrdersListLength() {
        JsonFileReader reader = new JsonFileReader();
        ArrayList<Order> orders = reader.readOrders("src/main/resources/data/orders.json");
        assertEquals(4, orders.size());
    }

    // test checking whether the size of ArrayList returned by JsonFileReader.readPaymentMethods() is correct
    @Test
    public void testReadingPaymentMethodsListLength() {
        JsonFileReader reader = new JsonFileReader();
        ArrayList<PaymentMethod> paymentMethods = reader.readPaymentMethods("src/main/resources/data/paymentmethods.json");
        assertEquals(3, paymentMethods.size());
    }

    // test checking whether the contents of ArrayList returned by JsonFileReader.readOrders() are correct
    @Test
    public void testReadingOrdersListContent() {
        JsonFileReader reader = new JsonFileReader();
        ArrayList<Order> orders = reader.readOrders("src/main/resources/data/orders.json");
        assertEquals(new Order("ORDER1", 100.0, new ArrayList<>(List.of("\"mZysk\""))), orders.getFirst());
        assertEquals(new Order("ORDER2", 200.0, new ArrayList<>(List.of("\"BosBankrut\""))), orders.get(1));
        assertEquals(new Order("ORDER3", 150.0, new ArrayList<>(List.of("\"mZysk\"", "\"BosBankrut\""))), orders.get(2));
        assertEquals(new Order("ORDER4", 50.0, new ArrayList<>()), orders.get(3));
    }

    // test checking whether the contents of ArrayList returned by JsonFileReader.readPaymentMethods() are correct
    @Test
    public void testReadingPaymentMethodsListContent() {
        JsonFileReader reader = new JsonFileReader();
        ArrayList<PaymentMethod> paymentMethods = reader.readPaymentMethods("src/main/resources/data/paymentmethods.json");
        assertEquals(new PaymentMethod("PUNKTY", 15, 100.0), paymentMethods.getFirst());
    }

    // test checking whether JsonFileReader.readOrders() returns empty ArrayList when the file does not exist
    @Test
    public void testReadingOrdersListContentWithNonExistingFile() {
        JsonFileReader reader = new JsonFileReader();
        assertTrue(() -> reader.readOrders("src/main/resources/data/orders_empty.json").isEmpty());
    }
}
