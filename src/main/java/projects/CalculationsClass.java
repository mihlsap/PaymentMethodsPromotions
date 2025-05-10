package projects;

import java.util.ArrayList;
import java.util.HashMap;

public class CalculationsClass {
    private ArrayList<Order> orders;
    private HashMap<String, ArrayList<Order>> ordersByPromotion;
    private ArrayList<PaymentMethod> paymentMethods;
    private HashMap<String, Double> costs;

    public CalculationsClass(String ordersPath, String paymentMethodsPath) {
        JsonFileReader jsonFileReader = new JsonFileReader(ordersPath, paymentMethodsPath);
        this.orders = jsonFileReader.getOrders();
        this.ordersByPromotion = jsonFileReader.getOrdersByPromotion();
        this.paymentMethods = jsonFileReader.getPaymentMethods();
        this.costs = new HashMap<>();
    }

    void showAll() {
        System.out.println("Orders:");
        orders.forEach(System.out::println);

        System.out.println("\nOrdersByPromotion:");
        for (String key : ordersByPromotion.keySet()) {
            System.out.println(key + " " + ordersByPromotion.get(key));
        }

        System.out.println("\nPayment methods:");
        paymentMethods.forEach(System.out::println);
    }
}
