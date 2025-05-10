package projects;

import java.util.Objects;

public class PaymentMethod {
    private final String id;
    private final double discount;
    private double limit;

    public PaymentMethod(String id, double discount, double limit) {
        this.id = id;
        this.discount = discount;
        this.limit = limit;
    }

    public String getId() {
        return id;
    }

    public double getDiscount() {
        return discount;
    }

    public double getLimit() {
        return limit;
    }

    public void setLimit(double limit) {
        this.limit = limit;
    }

    @Override
    public String toString() {
        return "PaymentMethod{" +
                "id='" + id + '\'' +
                ", discount=" + discount +
                ", limit=" + limit +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        PaymentMethod that = (PaymentMethod) o;
        return Double.compare(discount, that.discount) == 0 && Double.compare(limit, that.limit) == 0 && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, discount, limit);
    }
}
