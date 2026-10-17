import java.io.Serializable;
import java.util.Objects;

public class Product implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String productId;
    private String productName;
    private int quantity;
    private double unitPrice;

    public Product(String productId, String productName,
                   int quantity, double unitPrice) {

        if (productId == null || productId.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Product ID cannot be empty.");
        }

        if (productName == null || productName.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Product name cannot be empty.");
        }

        if (quantity < 0) {
            throw new IllegalArgumentException(
                    "Quantity cannot be negative.");
        }

        if (unitPrice < 0) {
            throw new IllegalArgumentException(
                    "Unit price cannot be negative.");
        }

        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public String getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public boolean isInStock() {
        return quantity > 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Product)) {
            return false;
        }

        Product other = (Product) obj;
        return Objects.equals(productId, other.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId);
    }

    @Override
    public String toString() {
        return "Product ID: " + productId
                + ", Product Name: " + productName
                + ", Quantity: " + quantity
                + ", Unit Price: $"
                + String.format("%.2f", unitPrice);
    }
}
