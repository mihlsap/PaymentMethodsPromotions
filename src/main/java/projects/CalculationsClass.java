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

                    if (order.getValue() <= paymentMethod.getLimit()) {

                        double discount = order.getValue() * (paymentMethod.getDiscount() / 100.0);
                        double finalCost = order.getValue() - discount;
                        assignments.add(new PaymentAssignmentOptions(order, paymentMethod.getId(), AssignmentType.POINTS, finalCost, discount, finalCost));

                    } else if (paymentMethod.getLimit() >= order.getValue() * 0.1) {

                        double discount = order.getValue() * 0.1;
                        double finalCost = order.getValue() - discount;
                        double usedAmount = order.getValue() * 0.1; //  TODO ewentualna zmiana
                        assignments.add(new PaymentAssignmentOptions(order, paymentMethod.getId(), AssignmentType.PARTIAL, usedAmount, discount, finalCost));

                    }


                }

            } else {

                for (Order order : ordersByPromotion.get(paymentMethod.getId())) {
                    if (!order.isPaid() && order.getValue() <= paymentMethod.getLimit()) {
                        double discount = order.getValue() * (paymentMethod.getDiscount() / 100.0);
                        double finalCost = order.getValue() - discount;
                        assignments.add(new PaymentAssignmentOptions(order, paymentMethod.getId(), AssignmentType.CARD, finalCost, discount, finalCost));
                    }
                }
            }
        }
    }

    void chooseBestOption() {

        generateAssignments();

        assignments.sort(Comparator.comparing(PaymentAssignmentOptions::getDiscount).reversed());

        for (PaymentAssignmentOptions assignment : assignments) {

            System.out.println(assignment);

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

                    double usedPoints = Math.min(paymentMethodsById.get("PUNKTY").getLimit(), leftToPay);
                    // add spent points to the value of all points spent
                    costs.merge("PUNKTY", usedPoints, Double::sum);

                    leftToPay -= usedPoints;

                    // set the left points value to 0
                    paymentMethodsById.get("PUNKTY").setLimit(paymentMethodsById.get("PUNKTY").getLimit() - usedPoints);

                }

                // pay what's left with other payment methods
                for (PaymentMethod paymentMethod : paymentMethods) {

                    if (leftToPay <= 0.0001)
                        break;

                    // if there are still funds available for a given payment method
                    if (paymentMethod.getLimit() > 0) {

                        double used = Math.min(paymentMethod.getLimit(), leftToPay);
                        paymentMethod.setLimit(paymentMethod.getLimit() - used);
                        costs.merge(paymentMethod.getId(), used, Double::sum);
                        leftToPay -= used;
                    }
                }

                // if there is still amount to pay left and no funds left for any payment method
                if (leftToPay > 0.0001) {

                    // throw exception
                    throw new RuntimeException("Insufficient funds for order " + order.getId());
                } else {
                    // if the whole order has been paid, set the order as paid and break the loop, else try paying with other payment methods
                    order.setPaid(true);
                }
            }
        }
    }

    void calculateCosts() {
        chooseBestOption();
        costs.forEach((key, value) -> System.out.println(key + " " + value));
    }

}
