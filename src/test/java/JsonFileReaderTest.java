import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import projects.JsonFileReader;
import projects.Order;
import projects.PaymentMethod;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public class JsonFileReaderTest {

    private JsonFileReader jsonFileReader;

    @BeforeEach
    @Test
    public void setUp() {
        jsonFileReader = new JsonFileReader("src/main/resources/data/orders.json",
                "src/main/resources/data/paymentmethods.json");
    }

    // test checking whether the size of ArrayList containing orders returned by JsonFileReader is correct
    @Test
    public void testReadingOrdersListLength() {
        ArrayList<Order> orders = jsonFileReader.getOrders();
        assertEquals(4, orders.size());
    }

    // test checking whether the size of ArrayList containing payment methods returned by JsonFileReader is correct
    @Test
    public void testReadingPaymentMethodsListLength() {
        ArrayList<PaymentMethod> paymentMethods = jsonFileReader.getPaymentMethods();
        assertEquals(3, paymentMethods.size());
    }

    // test checking whether the contents of ArrayList containing orders returned by JsonFileReader are correct
    @Test
    public void testReadingOrdersListContent() {
        ArrayList<Order> orders = jsonFileReader.getOrders();
        assertEquals(new Order("ORDER1", 100.0), orders.getFirst());
        assertEquals(new Order("ORDER2", 200.0), orders.get(1));
        assertEquals(new Order("ORDER3", 150.0), orders.get(2));
        assertEquals(new Order("ORDER4", 50.0), orders.getLast());
    }

    // test checking whether the contents of ArrayList containing payment methods returned by JsonFileReader are correct
    @Test
    public void testReadingPaymentMethodsListContent() {
        ArrayList<PaymentMethod> paymentMethods = jsonFileReader.getPaymentMethods();
        assertEquals(new PaymentMethod("PUNKTY", 15, 100.0), paymentMethods.get(0));
        assertEquals(new PaymentMethod("mZysk", 10, 180.0), paymentMethods.get(1));
        assertEquals(new PaymentMethod("BosBankrut", 5, 200.0), paymentMethods.get(2));
    }

    // test checking whether the contents of ArrayList containing payment methods returned by JsonFileReader are correct
    @Test
    public void testReadingOrdersByPromotionHashMapContent() {
        HashMap<String, ArrayList<Order>> ordersByPromotion = jsonFileReader.getOrdersByPromotion();
        assertEquals(new Order("ORDER1", 100.0), ordersByPromotion.get("PUNKTY").getFirst());
        assertEquals(new Order("ORDER3", 150.0), ordersByPromotion.get("mZysk").getLast());
        assertEquals(new Order("ORDER2", 200.0), ordersByPromotion.get("BosBankrut").getFirst());
    }

    // test checking whether the contents of ArrayList containing payment methods returned by JsonFileReader are correct
    @Test
    public void testReadingPaymentMethodsByIdHashMapContent() {
        HashMap<String, PaymentMethod> paymentMethodsById = jsonFileReader.getPaymentMethodsById();
        assertEquals(new PaymentMethod("PUNKTY", 15.0, 100.0), paymentMethodsById.get("PUNKTY"));
        assertEquals(new PaymentMethod("mZysk", 10.0, 180.0), paymentMethodsById.get("mZysk"));
        assertEquals(new PaymentMethod("BosBankrut", 5.0, 200.0), paymentMethodsById.get("BosBankrut"));
    }

    // test checking whether JsonFileReader constructor throws RuntimeException when one or both of the files do not exist
    @Test
    public void testJsonFileReaderThrowsExceptionWithNonExistingFile() {
        assertThrows(RuntimeException.class, () -> new JsonFileReader("src/main/resources/data/orders.json",
                "src/main/resources/data/empty.json"));
        assertThrows(RuntimeException.class, () -> new JsonFileReader("src/main/resources/data/empty.json",
                "src/main/resources/data/orders.json"));
        assertThrows(RuntimeException.class, () -> new JsonFileReader("src/main/resources/data/empty.json",
                "src/main/resources/data/empty.json"));
    }

    // test checking whether JsonFileReader constructor returns empty ArrayLists and HashMaps when the files are empty
    @Test
    public void testReadingOrdersListContentWithEmptyFileReturnsEmptyArrayList() {
        JsonFileReader jsonFileReader1 = new JsonFileReader("src/test/data/empty.json",
                "src/test/data/empty.json");
        assertTrue(() -> jsonFileReader1.getOrders().isEmpty());
        assertTrue(() -> jsonFileReader1.getPaymentMethods().isEmpty());
        assertTrue(() -> jsonFileReader1.getOrdersByPromotion().isEmpty());
        assertTrue(() -> jsonFileReader1.getPaymentMethodsById().isEmpty());
    }

    // test to check whether JsonFileReader constructor will throw exception if a file of a wrong type is passed
    @Test
    public void testReadingOrdersFileWithWrongTypeThrowsException() {
        assertThrows(RuntimeException.class, () -> new JsonFileReader("scr/test/data/text.txt",
                "src/test/data/text.txt"));
        assertThrows(RuntimeException.class, () -> new JsonFileReader("src/main/resources/data/orders.json",
                "src/test/data/text.txt"));
        assertThrows(RuntimeException.class, () -> new JsonFileReader("scr/test/data/text.txt",
                "src/main/resources/data/orders.json"));
    }
}
