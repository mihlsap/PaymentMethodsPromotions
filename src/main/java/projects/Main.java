package projects;

public class Main {
    public static void main(String[] args) {
        // jar version
        // path to orders.json file
//        String ordersPath = args[0];
        // path to paymentmethods.json file
//        String paymentMethodsPath = args[1];

        // test version
        String ordersPath = "src/main/resources/data/orders.json";
        String paymentMethodsPath = "src/main/resources/data/paymentmethods.json";


        CalculationsClass calculationsClass = new CalculationsClass(ordersPath, paymentMethodsPath);
        calculationsClass.calculateCosts();
    }

}