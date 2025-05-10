package projects;

import java.util.Objects;

public class PaymentAssignmentOptions {
    private Order order;
    private String paymentMethodId;
    private AssignmentType assignmentType;
    private double usedAmount;
    private double discount;
    private double cost;

    public PaymentAssignmentOptions(Order order, String paymentMethodId, AssignmentType assignmentType, double usedAmount, double discount, double cost) {
        this.order = order;
        this.paymentMethodId = paymentMethodId;
        this.assignmentType = assignmentType;
        this.usedAmount = usedAmount;
        this.discount = discount;
        this.cost = cost;
    }

    public double getDiscount() {
        return discount;
    }

    public Order getOrder() {
        return order;
    }

    public String getPaymentMethodId() {
        return paymentMethodId;
    }

    public AssignmentType getAssignmentType() {
        return assignmentType;
    }

    public double getUsedAmount() {
        return usedAmount;
    }

    public double getCost() {
        return cost;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public void setPaymentMethodId(String paymentMethodId) {
        this.paymentMethodId = paymentMethodId;
    }

    public void setAssignmentType(AssignmentType assignmentType) {
        this.assignmentType = assignmentType;
    }

    public void setUsedAmount(double usedAmount) {
        this.usedAmount = usedAmount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        PaymentAssignmentOptions that = (PaymentAssignmentOptions) o;
        return Double.compare(usedAmount, that.usedAmount) == 0 && Double.compare(discount, that.discount) == 0 && Double.compare(cost, that.cost) == 0 && Objects.equals(order, that.order) && Objects.equals(paymentMethodId, that.paymentMethodId) && assignmentType == that.assignmentType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(order, paymentMethodId, assignmentType, usedAmount, discount, cost);
    }

    @Override
    public String toString() {
        return "PaymentAssignmentOptions{" +
                "order=" + order +
                ", paymentMethodId='" + paymentMethodId + '\'' +
                ", assignmentType=" + assignmentType +
                ", usedAmount=" + usedAmount +
                ", discount=" + discount +
                ", cost=" + cost +
                '}';
    }
}
