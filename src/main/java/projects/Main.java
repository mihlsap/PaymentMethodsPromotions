package projects;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        // jar version
        String ordersPath = args[0];
        String paymentMethodsPath = args[1];

        // test version
//        String ordersPath = "src/main/resources/data/orders.json";
//        String paymentMethodsPath = "src/main/resources/data/paymentmethods.json";

        JsonFileReader jsonFileReader = new JsonFileReader();
        ArrayList<Order> orders = jsonFileReader.readOrders(ordersPath);
        orders.forEach(System.out::println);

        ArrayList<PaymentMethod> paymentMethods = jsonFileReader.readPaymentMethods(paymentMethodsPath);
        paymentMethods.forEach(System.out::println);
    }
}