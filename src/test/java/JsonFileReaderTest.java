import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import projects.JsonFileReader;
import projects.Order;
import projects.PaymentMethod;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class JsonFileReaderTest {

    private JsonFileReader jsonFileReader;

    @BeforeEach
    @Test
    public void setUp() {
        jsonFileReader = new JsonFileReader("src/main/resources/data/orders.json", "src/main/resources/data/paymentmethods.json");
    }

    // test checking whether the size of ArrayList returned by JsonFileReader.readOrders() is correct
    @Test
    public void testReadingOrdersListLength() {
        ArrayList<Order> orders = jsonFileReader.getOrders();
        assertEquals(4, orders.size());
    }

    // test checking whether the size of ArrayList returned by JsonFileReader.readPaymentMethods() is correct
    @Test
    public void testReadingPaymentMethodsListLength() {
        ArrayList<PaymentMethod> paymentMethods = jsonFileReader.getPaymentMethods();
        assertEquals(3, paymentMethods.size());
    }

    // test checking whether the contents of ArrayList returned by JsonFileReader.readOrders() are correct
    @Test
    public void testReadingOrdersListContent() {
        ArrayList<Order> orders = jsonFileReader.getOrders();
        assertEquals(new Order("ORDER1", 100.0, new ArrayList<>(List.of("mZysk", "PUNKTY"))), orders.getFirst());
        assertEquals(new Order("ORDER2", 200.0, new ArrayList<>(List.of("BosBankrut", "PUNKTY"))), orders.get(1));
        assertEquals(new Order("ORDER3", 150.0, new ArrayList<>(List.of("mZysk", "BosBankrut", "PUNKTY"))), orders.get(2));
        assertEquals(new Order("ORDER4", 50.0, new ArrayList<>(List.of("PUNKTY"))), orders.getLast());
    }

    // test checking whether the contents of ArrayList returned by JsonFileReader.readPaymentMethods() are correct
    @Test
    public void testReadingPaymentMethodsListContent() {
        ArrayList<PaymentMethod> paymentMethods = jsonFileReader.getPaymentMethods();
        assertEquals(new PaymentMethod("PUNKTY", 15, 100.0), paymentMethods.get(0));
        assertEquals(new PaymentMethod("mZysk", 10, 180.0), paymentMethods.get(1));
        assertEquals(new PaymentMethod("BosBankrut", 5, 200.0), paymentMethods.get(2));
    }

    // test checking whether JsonFileReader constructor throws RuntimeException when the file does not exist
    @Test
    public void testJsonFileReaderThrowsExceptionWithNonExistingFile() {
        assertThrows(RuntimeException.class, () -> new JsonFileReader("src/main/resources/data/empty.json", "src/main/resources/data/empty.json"));
    }

    // test checking whether JsonFileReader.readOrders() returns empty ArrayList when the file is empty
    @Test
    public void testReadingOrdersListContentWithEmptyFileReturnsEmptyArrayList() {
        JsonFileReader jsonFileReader1 = new JsonFileReader("src/test/data/empty.json", "src/test/data/empty.json");
        assertTrue(() -> jsonFileReader1.getOrders().isEmpty());
    }

    // test checking whether JsonFileReader.paymentMethods() returns empty ArrayList when the file is empty
    @Test
    public void testReadingPaymentMethodsListContentWithEmptyFileReturnsEmptyArrayList() {
        JsonFileReader jsonFileReader1 = new JsonFileReader("src/test/data/empty.json", "src/test/data/empty.json");
        assertTrue(() -> jsonFileReader1.getPaymentMethods().isEmpty());
    }

    // test to check whether JsonFileReader.readOrders() will throw exception if a file of a wrong type is passed
    @Test
    public void testReadingOrdersFileWithWrongTypeThrowsException() {
        assertThrows(RuntimeException.class, () -> new JsonFileReader("scr/test/data/text.txt", "src/test/data/text.txt"));
    }
}
