package projects;

import java.util.Objects;

// class for storing information about assignment for a given payment method
public record PaymentAssignmentOption(Order order, String paymentMethodId, AssignmentType assignmentType,
                                      double usedAmount, double discount, double cost) {

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        PaymentAssignmentOption that = (PaymentAssignmentOption) o;
        return Double.compare(usedAmount, that.usedAmount) == 0 && Double.compare(discount, that.discount) == 0 && Double.compare(cost, that.cost) == 0 && Objects.equals(order, that.order) && Objects.equals(paymentMethodId, that.paymentMethodId) && assignmentType == that.assignmentType;
    }

}
