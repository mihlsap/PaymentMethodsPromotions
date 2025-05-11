package projects;

import java.util.*;

public class CalculationsClass {

    // ArrayList for storing orders
    private final ArrayList<Order> orders;

    // HashMap for storing orders with promotion as a key
    private final HashMap<String, ArrayList<Order>> ordersByPromotion;

    // ArrayList for storing payment methods
    private final ArrayList<PaymentMethod> paymentMethods;

    // HashMap for storing payment methods with IDs as a key
    private final HashMap<String, PaymentMethod> paymentMethodsById;

    // HashMap for storing costs for a given payment method
    private final HashMap<String, Double> costs;

    // ArrayLists for storing possible assignments for a given payment method
    private final ArrayList<PaymentAssignmentOption> assignments;

    public CalculationsClass(String ordersPath, String paymentMethodsPath) {
        JsonFileReader jsonFileReader = new JsonFileReader(ordersPath, paymentMethodsPath);
        this.orders = jsonFileReader.getOrders();
        this.ordersByPromotion = jsonFileReader.getOrdersByPromotion();
        this.paymentMethods = jsonFileReader.getPaymentMethods();
        this.paymentMethodsById = jsonFileReader.getPaymentMethodsById();
        this.costs = new HashMap<>();
        this.assignments = new ArrayList<>();
    }

    // method for generating possible assignments for each payment method
    public void generateAssignments() {

        // for each payment method
        for (PaymentMethod paymentMethod : paymentMethods) {

            String methodId = paymentMethod.getId();

            // if the payment method is "PUNKTY"
            if (methodId.equalsIgnoreCase("PUNKTY")) {

                // for each order
                for (Order order : orders) {

                    double orderValue = order.getValue();

                    // if there are enough points to pay fully with them for this order
                    if (orderValue <= paymentMethod.getLimit()) {

                        // calculate the discount and cost for that order
                        double discount = orderValue * (paymentMethod.getDiscount() / 100.0);
                        double finalCost = orderValue - discount;

                        // add this assignment option to assignments ArrayList
                        assignments.add(new PaymentAssignmentOption(order, methodId, AssignmentType.POINTS, finalCost, discount, finalCost));

                        // if there aren't enough points to pay fully, but enough to qualify for a 10% discount for paying partially with them
                    } else if (paymentMethod.getLimit() >= orderValue * 0.10) {

                        // calculate the discount and final cost of the order, use just enough points to qualify for discount
                        double discount = orderValue * 0.10;
                        double finalCost = orderValue - discount;
                        double usedAmount = orderValue * 0.10;

                        // add this assignment option to assignments ArrayList
                        assignments.add(new PaymentAssignmentOption(order, methodId, AssignmentType.PARTIAL, usedAmount, discount, finalCost));
                    }
                }

                // if the payment method is other than "PUNKTY"
            } else {

                // for each order, that can use the given payment method
                for (Order order : ordersByPromotion.getOrDefault(methodId, new ArrayList<>())) {

                    double orderValue = order.getValue();

                    // if there are enough funds for the given payment method to pay fully for this order
                    if (orderValue <= paymentMethod.getLimit()) {

                        // calculate discount and final cost of the order
                        double discount = orderValue * (paymentMethod.getDiscount() / 100.0);
                        double finalCost = orderValue - discount;

                        // add this assignment option to assignments ArrayList
                        assignments.add(new PaymentAssignmentOption(order, methodId, AssignmentType.CARD, finalCost, discount, finalCost));
                    }
                }
            }
        }
    }

    public void chooseBestOption() {

        // generate possible assignments
        generateAssignments();

        // sort assignments in order of the payment method's priority (firstly wholly by points, then wholly by card,
        // lastly partially by points and by card) and then in order of decreasing discount
        assignments.sort((a, b) -> {

            // check priority of payment's type
            int typePriority = getTypePriority(a.assignmentType()) - getTypePriority(b.assignmentType());

            // if types of payment are different, sort them in order of priority
            if (typePriority != 0)
                return typePriority;
            return Double.compare(b.discount(), a.discount());
        });

        // set for soring assigned orders
        Set<String> assignedOrders = new HashSet<>();

        // for each assignment option
        for (PaymentAssignmentOption assignment : assignments) {

            Order order = assignment.order();

            // skip if the order is already paid or has been handled
            if (order.isPaid() || assignedOrders.contains(order.getId()))
                continue;

            // get payment method and it's available funds
            PaymentMethod method = paymentMethodsById.get(assignment.paymentMethodId());
            double available = method.getLimit();

            // if the assignment is for a partial payment, check if there are enough funds for it
            if (assignment.assignmentType() == AssignmentType.PARTIAL) {

                double leftToPay = assignment.cost() - assignment.usedAmount();

                // calculate funds left for all payment methods except "PUNKTY"
                double otherFunds = paymentMethods.stream().filter(pm -> !pm.getId().equals("PUNKTY"))
                        .mapToDouble(PaymentMethod::getLimit).sum();

                // skip if there are not enough funds for the partial payment
                if (leftToPay > otherFunds)
                    continue;
            }

            // if there are more funds left for this payment method than there is to pay for that order
            if (available >= assignment.usedAmount()) {

                // update method's payment limit
                method.setLimit(available - assignment.usedAmount());

                // add cost to the sum of funds spent for specific payment method
                costs.merge(method.getId(), assignment.usedAmount(), Double::sum);

                // set order as paid
                order.setPaid(true);

                // add order's id to set of assigned orders
                assignedOrders.add(order.getId());
            }
        }

        // code handling orders which were not paid yet
        for (Order order : orders) {

            // first check whether the order can be paid partially by points and other payment methods

            // skip if the order is already paid
            if (order.isPaid()) continue;

            double orderValue = order.getValue();
            double tentativeLeftToPay = orderValue;
            double tentativeUsedPoints = 0.0;

            // calculate funds left for all payment methods except "PUNKTY"
            double fundsLeft = paymentMethods.stream()
                    .filter(pm -> !pm.getId().equals("PUNKTY"))
                    .mapToDouble(PaymentMethod::getLimit)
                    .sum();

            PaymentMethod points = paymentMethodsById.get("PUNKTY");

            // if there are still points left
            if (points.getLimit() > 0) {

                // calculate how many points can be used for this order
                tentativeUsedPoints = Math.min(points.getLimit(), tentativeLeftToPay);

                // if there are enough points to cover at least 10% of the order's value, apply discount
                if (tentativeUsedPoints >= orderValue * 0.1) {
                    tentativeLeftToPay -= orderValue * 0.1;
                }
            }

            double totalFundsAvailable = tentativeUsedPoints + fundsLeft;

            // skip if the amount left to pay is bigger than funds left, which means that the order cannot be covered
            if (tentativeLeftToPay > totalFundsAvailable) {
                continue;
            }

            double leftToPay = orderValue;

            // if there are still points left
            if (points.getLimit() > 0) {

                // calculate how many points can be used for this order
                double usedPoints = Math.min(points.getLimit(), leftToPay);

                // check whether a discount can be applied
                boolean applyDiscount = usedPoints >= orderValue * 0.1;

                // if it can be, then apply it
                if (applyDiscount)
                    leftToPay -= orderValue * 0.1;

                // deduct used points from the payment method's limit and add them to the costs HashMap
                points.setLimit(points.getLimit() - usedPoints);
                costs.merge("PUNKTY", usedPoints, Double::sum);

                // deduct used points from the remaining amount to pay
                leftToPay -= usedPoints;
            }

            // try paying for the remaining amount with other payment methods
            // for each payment method, except "PUNKTY"
            for (PaymentMethod method : paymentMethods) {
                if (method.getId().equals("PUNKTY")) continue;

                // skip if the order has already been paid
                if (leftToPay <= 0.0001) break;

                double available = method.getLimit();

                // if there are still funds left for this payment method, use them for this order
                if (available > 0) {

                    // calculate how many funds can be used for this order
                    double used = Math.min(available, leftToPay);

                    // deduct used funds from the payment method's limit and add them to the costs HashMap'
                    method.setLimit(available - used);
                    costs.merge(method.getId(), used, Double::sum);

                    // deduct used funds from the remaining amount to pay
                    leftToPay -= used;
                }
            }

            // if there are still funds left to pay for this order, then it cannot be covered by any payment method
            // throw exception and stop the loop
            if (leftToPay > 0.0001) {
                throw new RuntimeException("Cannot pay for " + order.getId() + " - insufficient amount of funds!");

            // else mark order as paid
            } else {
                order.setPaid(true);
            }
        }
    }

    // return priority of a given payment type
    private int getTypePriority(AssignmentType type) {
        return switch (type) {
            case POINTS -> 0;
            case CARD -> 1;
            case PARTIAL -> 2;
        };
    }

    // method printing to the standard output values of used funds for each payment method
    public void calculateCosts() {
        chooseBestOption();

        // adding unused payment methods to the costs HashMap
        for (PaymentMethod paymentMethod : paymentMethods) {
            costs.putIfAbsent(paymentMethod.getId(), 0.0);
        }

        // printing costs for each payment method to the standard output
        costs.forEach((key, value) -> System.out.println(key + " " + String.format("%.2f", value)));

        // if the order still has not been paid, print an error message
        for (Order order : orders) {
            if (!order.isPaid()) {
                System.err.println("Insufficient amount of funds to pay for all orders!");
                break;
            }
        }
    }

    // getters for the used collections
    public ArrayList<Order> getOrders() {
        return orders;
    }

    public HashMap<String, ArrayList<Order>> getOrdersByPromotion() {
        return ordersByPromotion;
    }

    public ArrayList<PaymentMethod> getPaymentMethods() {
        return paymentMethods;
    }

    public HashMap<String, PaymentMethod> getPaymentMethodsById() {
        return paymentMethodsById;
    }

    public HashMap<String, Double> getCosts() {
        return costs;
    }

    public ArrayList<PaymentAssignmentOption> getAssignments() {
        return assignments;
    }

}
