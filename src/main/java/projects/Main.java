package projects;

public class Main {
    public static void main(String[] args) {

        // path to orders.json file
        String ordersPath = args[0];

        // path to paymentmethods.json file
        String paymentMethodsPath = args[1];

        CalculationsClass calculationsClass = new CalculationsClass(ordersPath, paymentMethodsPath);
        calculationsClass.calculateCosts();
    }
}