package projects;

import java.util.List;
import java.util.Objects;

public class Order {
    private final String id;
    private final double value;
    private final List<String> promotions; //  TODO delete this
    private boolean paid;

    public Order(String id, double value, List<String> promotions) {
        this.id = id;
        this.value = value;
        this.promotions = promotions;
        this.paid = false;
    }

    public String getId() {
        return id;
    }

    public double getValue() {
        return value;
    }

    public List<String> getPromotions() {
        return promotions;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id='" + id + '\'' +
                ", value=" + value +
                ", promotions=" + promotions +
                ", paid=" + paid +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Double.compare(value, order.value) == 0 && paid == order.paid && Objects.equals(id, order.id) && Objects.equals(promotions, order.promotions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, value, promotions, paid);
    }
}
