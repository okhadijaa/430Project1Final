import java.io.Serializable;
import java.util.Objects;

public class Client implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String clientId;
    private String clientName;
    private double balance;

    public Client(String clientId, String clientName) {

        if (clientId == null || clientId.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Client ID cannot be empty.");
        }

        if (clientName == null || clientName.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Client name cannot be empty.");
        }

        this.clientId = clientId;
        this.clientName = clientName;
        this.balance = 0.0;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientName() {
        return clientName;
    }

    public double getBalance() {
        return balance;
    }

    public void addDebit(double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException(
                    "Debit amount cannot be negative.");
        }

        balance += amount;
    }

    public void addCredit(double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException(
                    "Credit amount cannot be negative.");
        }

        balance -= amount;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Client)) {
            return false;
        }

        Client other = (Client) obj;
        return Objects.equals(clientId, other.clientId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clientId);
    }

    @Override
    public String toString() {
        return "Client ID: " + clientId
                + ", Client Name: " + clientName
                + ", Balance: $"
                + String.format("%.2f", balance);
    }
}
