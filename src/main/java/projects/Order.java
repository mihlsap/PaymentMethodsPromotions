package projects;

import java.util.List;
import java.util.Objects;

public class Order {
    private final String id;
    private final double value;
    private final List<String> promotions;

    public Order(String id, double value, List<String> promotions) {
        this.id = id;
        this.value = value;
        this.promotions = promotions;
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

    @Override
    public String toString() {
        return "Order{" +
                "id='" + id + '\'' +
                ", value='" + value + '\'' +
                ", promotions=" + promotions.toString() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Double.compare(value, order.value) == 0 && Objects.equals(id, order.id) && Objects.equals(promotions, order.promotions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, value, promotions);
    }
}
