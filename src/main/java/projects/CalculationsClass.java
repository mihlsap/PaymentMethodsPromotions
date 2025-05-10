package projects;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

public class CalculationsClass {
    private ArrayList<Order> orders;
    private HashMap<String, ArrayList<Order>> ordersByPromotion;
    private ArrayList<PaymentMethod> paymentMethods;
    private HashMap<String, PaymentMethod> paymentMethodsById;
    private HashMap<String, Double> costs;
    private ArrayList<PaymentAssignmentOptions> assignments;

    public CalculationsClass(String ordersPath, String paymentMethodsPath) {
        JsonFileReader jsonFileReader = new JsonFileReader(ordersPath, paymentMethodsPath);
        this.orders = jsonFileReader.getOrders();
        this.ordersByPromotion = jsonFileReader.getOrdersByPromotion();
        this.paymentMethods = jsonFileReader.getPaymentMethods();
        this.paymentMethodsById = jsonFileReader.getPaymentMethodsById();
        this.costs = new HashMap<>();
        this.assignments = new ArrayList<>();
    }

    void generateAssignments() {

        for (PaymentMethod paymentMethod : paymentMethods) {

            if (paymentMethod.getId().equalsIgnoreCase("PUNKTY")) {

                for (Order order : orders) {

                    if (!order.isPaid()) {

                        if (order.getValue() <= paymentMethod.getLimit()) {

                            double usedAmount = order.getValue() * (1 - paymentMethod.getDiscount() / 100);
                            double discount = order.getValue() - usedAmount;
                            assignments.add(new PaymentAssignmentOptions(order, paymentMethod.getId(), AssignmentType.POINTS, usedAmount, discount, usedAmount));

                        } else if (paymentMethod.getLimit() >= order.getValue() * 0.1) {

                            double finalCost = order.getValue() * 0.9;
                            double usedAmount = Math.min(finalCost, paymentMethod.getLimit());
                            double discount = order.getValue() * 0.1;
                            assignments.add(new PaymentAssignmentOptions(order, paymentMethod.getId(), AssignmentType.PARTIAL, usedAmount, discount, finalCost));
                        }
                    }

                }

            } else {
                for (Order order : ordersByPromotion.get(paymentMethod.getId())) {
                    if (!order.isPaid() && order.getValue() <= paymentMethod.getLimit()) {
                        double usedAmount = order.getValue() * (1 - paymentMethod.getDiscount() / 100);
                        double discount = order.getValue() - usedAmount;
                        assignments.add(new PaymentAssignmentOptions(order, paymentMethod.getId(), AssignmentType.CARD, usedAmount, discount, usedAmount));
                    }
                }
            }
        }
    }

    void chooseBestOption() {

        generateAssignments();

        assignments.sort(Comparator.comparing(PaymentAssignmentOptions::getDiscount).reversed());

//        System.out.println("Assignments:");
//        assignments.forEach(System.out::println);
//        System.out.println("\nPayments:");
//        paymentMethods.forEach(System.out::println);
//        System.out.println();

        for (PaymentAssignmentOptions assignment : assignments) {

            if (!assignment.getOrder().isPaid()) {

                PaymentMethod paymentMethod = paymentMethodsById.get(assignment.getPaymentMethodId());

                if (paymentMethod.getLimit() >= assignment.getUsedAmount()) {
                    costs.merge(paymentMethod.getId(), assignment.getUsedAmount(), Double::sum);
                    assignment.getOrder().setPaid(true);
                    paymentMethod.setLimit(paymentMethod.getLimit() - assignment.getUsedAmount());
                }
            }
        }

        // for each order
        for (Order order : orders) {

            // if the order is not paid
            if (!order.isPaid()) {

                // initialize the value of the amount left to pay
                double leftToPay = order.getValue();

                // if there are still points left to pay for the order
                if (paymentMethodsById.get("PUNKTY").getLimit() > 0) {

                    // add spent points to the value of all points spent
                    costs.merge("PUNKTY", order.getValue() - paymentMethodsById.get("PUNKTY").getLimit(), Double::sum);

                    leftToPay = order.getValue() - paymentMethodsById.get("PUNKTY").getLimit();

                    // set the left points value to 0
                    paymentMethodsById.get("PUNKTY").setLimit(0);

                }

                // pay what's left with other payment methods
                for (PaymentMethod paymentMethod : paymentMethods) {

                    // if there are still funds available for a given payment method
                    if (paymentMethod.getLimit() > 0) {

                        // if there is enough money to pay the cost left
                        if (paymentMethod.getLimit() >= leftToPay) {

                            // subtract the cost from the payment method's limit
                            paymentMethod.setLimit(paymentMethod.getLimit() - leftToPay);

                            // add used funds to cost of given payment method
                            costs.merge(paymentMethod.getId(), leftToPay, Double::sum);

                            // set the leftToPay value to 0
                            leftToPay = 0;

                        } else {

                            // subtract all money left on a given payment method from the leftToPay value
                            leftToPay = leftToPay - paymentMethod.getLimit();

                            // set the given payment method's funds left to 0
                            paymentMethod.setLimit(0);

                            // add used funds to cost of given payment method
                            costs.merge(paymentMethod.getId(), leftToPay, Double::sum);
                        }
                    }

                    // if the whole order has been paid, set the order as paid and break the loop, else try paying with other payment methods
                    if (leftToPay == 0) {
                        order.setPaid(true);
                        break;
                    }
                }

                // if there is still amount to pay left and no funds left for any payment method
                if (leftToPay > 0) {

                    // throw exception
                    throw new RuntimeException("Insufficient funds for order " + order.getId());
                }
            }
        }


//        assignments.forEach(System.out::println);
//        System.out.println();
//        costs.forEach((key, value) -> System.out.println(key + " " + value));
    }

    void calculateCosts() {
        chooseBestOption();
        costs.forEach((key, value) -> System.out.println(key + " " + value));
    }

}
