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

            // if they are the same, sort them in order of decreasing discount
            return Double.compare(b.discount(), a.discount());
        });

        // set for storing IDs of orders already assigned to a particular payment method
        Set<String> assignedOrders = new HashSet<>();

        // for each assignment
        for (PaymentAssignmentOption assignment : assignments) {

            Order order = assignment.order();

            // skip if the order has been already paid or the order has been already assigned to some payment method
            if (order.isPaid() || assignedOrders.contains(order.getId())) continue;

            // get a payment method assigned to this assignment and funds available for it
            PaymentMethod method = paymentMethodsById.get(assignment.paymentMethodId());
            double available = method.getLimit();

            // if there are enough available funds for given payment method to cover whole order
            if (available >= assignment.usedAmount()) {

                // decrease payment method's available funds (limit)
                method.setLimit(available - assignment.usedAmount());

                // add spent funds to the overall payment method's cost
                costs.merge(method.getId(), assignment.usedAmount(), Double::sum);

                // set order as paid
                order.setPaid(true);

                // add order's id to the set of already assigned orders
                assignedOrders.add(order.getId());
            }
        }

        // code handling orders which were not paid yet
        // for every order
        for (Order order : orders) {

            // skip if the order has already been paid
            if (order.isPaid()) continue;

            // get value of order and initialize variable storing amount left to pay
            double orderValue = order.getValue();
            double leftToPay = orderValue;

            // choose "PUNKTY" as the first payment method to cover as much as possible using points
            PaymentMethod points = paymentMethodsById.get("PUNKTY");

            // if there are points left
            if (points.getLimit() > 0) {

                // determine how many points will be spent
                double usedPoints = Math.min(points.getLimit(), leftToPay);

                // check whether more than 10% of the order's value can be paid with points
                if (usedPoints >= orderValue * 0.1) {

                    // if yes, apply a 10% discount to the amount left to pay
                    leftToPay -= orderValue * 0.1;
                }

                // update available points
                points.setLimit(points.getLimit() - usedPoints);

                // add spent points to the overall payment method's value
                costs.merge("PUNKTY", usedPoints, Double::sum);

                // update the value left to pay
                leftToPay -= usedPoints;
            }

            // pay the remaining cost using other payment methods
            // for every payment method
            for (PaymentMethod method : paymentMethods) {

                // skip if the payment method is "PUNKTY"
                if (method.getId().equals("PUNKTY")) continue;

                // break if the order is already paid off
                if (leftToPay <= 0.0001) break;

                // get the amount still available for the given payment method
                double available = method.getLimit();

                // if this amount is greater than 0
                if (available > 0) {

                    // determine the used number of funds
                    double used = Math.min(available, leftToPay);

                    // subtract used number of funds from the amount still left for the given payment method
                    method.setLimit(available - used);

                    // add spent funds to the overall payment method's cost
                    costs.merge(method.getId(), used, Double::sum);

                    // subtract used funds from the amount still left to pay
                    leftToPay -= used;
                }
            }

            if (leftToPay > 0.0001) {
                throw new RuntimeException("Cannot pay for: " + order.getId() + " insufficient amount of funds available!");
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
        costs.forEach((key, value) -> System.out.println(key + " " + String.format("%.2f", value)));
    }

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
