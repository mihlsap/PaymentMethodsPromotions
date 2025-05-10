package projects;

import java.util.Objects;

// class for storing orders
public class Order {
    private final String id;
    private final double value;
    private boolean paid;

    public Order(String id, double value) {
        this.id = id;
        this.value = value;
        this.paid = false;
    }

    public String getId() {
        return id;
    }

    public double getValue() {
        return value;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Double.compare(value, order.value) == 0 && paid == order.paid && Objects.equals(id, order.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, value, paid);
    }
}
