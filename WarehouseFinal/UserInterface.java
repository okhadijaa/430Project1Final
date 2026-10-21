import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class UserInterface {

    private static UserInterface userInterface;
    private final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    private static Warehouse warehouse;

    // Command Codes
    private static final int EXIT = 0;
    private static final int ADD_CLIENT = 1;
    private static final int ADD_PRODUCT = 2;
    private static final int ADD_TO_WISHLIST = 3;
    private static final int DISPLAY_WISHLIST = 4;
    private static final int PROCESS_ORDER = 5;
    private static final int RECEIVE_SHIPMENT = 6;
    private static final int RECEIVE_PAYMENT = 7;
    private static final int DISPLAY_CLIENTS = 8;
    private static final int DISPLAY_PRODUCTS = 9;
    private static final int DISPLAY_INVOICES = 10;
    private static final int DISPLAY_TRANSACTIONS = 11;
    private static final int DISPLAY_WAITLIST = 12;
    private static final int DISPLAY_OUTSTANDING_BALANCES = 13;
    private static final int SAVE = 14;
    private static final int RETRIEVE = 15;
    private static final int HELP = 16;

    private UserInterface() {
        if (yesOrNo("Look for saved data and use it?")) {
            retrieve();
        } else {
            warehouse = Warehouse.instance();
        }
    }

    public static UserInterface instance() {
        if (userInterface == null) {
            userInterface = new UserInterface();
        }
        return userInterface;
    }

    // --- Input Helper Methods ---

    public String getToken(String prompt) {
        do {
            try {
                System.out.println(prompt);
                String line = reader.readLine();
                if (line == null) {
                    System.exit(0);
                }
                StringTokenizer tokenizer = new StringTokenizer(line, "\n\r\f");
                if (tokenizer.hasMoreTokens()) {
                    return tokenizer.nextToken().trim();
                }
            } catch (IOException ioe) {
                System.exit(0);
            }
        } while (true);
    }

    private boolean yesOrNo(String prompt) {
        String more = getToken(prompt + " (Y|y)[es] or anything else for no");
        return more.charAt(0) == 'y' || more.charAt(0) == 'Y';
    }

    public int getNumber(String prompt) {
        do {
            try {
                String item = getToken(prompt);
                return Integer.parseInt(item);
            } catch (NumberFormatException nfe) {
                System.out.println("Please input an integer.");
            }
        } while (true);
    }

    public double getDouble(String prompt) {
        do {
            try {
                String item = getToken(prompt);
                double val = Double.parseDouble(item);
                if (Double.isFinite(val) && val >= 0) {
                    return val;
                }
                System.out.println("Please input a valid non-negative decimal value.");
            } catch (NumberFormatException nfe) {
                System.out.println("Please input a valid decimal number.");
            }
        } while (true);
    }

    public int getCommand() {
        do {
            try {
                int value = Integer.parseInt(getToken("Enter command (" + HELP + " for help):"));
                if (value >= EXIT && value <= HELP) {
                    return value;
                }
                System.out.println("Invalid command range.");
            } catch (NumberFormatException nfe) {
                System.out.println("Enter a number.");
            }
        } while (true);
    }

    public void help() {
        System.out.println("\n================ COMMAND MENU ================");
        System.out.println(EXIT + "  to Exit");
        System.out.println(ADD_CLIENT + "  to Add a Client");
        System.out.println(ADD_PRODUCT + "  to Add a Product");
        System.out.println(ADD_TO_WISHLIST + "  to Add/Update Product in Wishlist");
        System.out.println(DISPLAY_WISHLIST + "  to Display a Client's Wishlist");
        System.out.println(PROCESS_ORDER + "  to Process an Order for a Client");
        System.out.println(RECEIVE_SHIPMENT + "  to Receive an Incoming Shipment");
        System.out.println(RECEIVE_PAYMENT + "  to Record a Client Payment");
        System.out.println(DISPLAY_CLIENTS + "  to Display All Clients");
        System.out.println(DISPLAY_PRODUCTS + "  to Display All Products (with waitlist counts)");
        System.out.println(DISPLAY_INVOICES + " to Display Invoices for a Client");
        System.out.println(DISPLAY_TRANSACTIONS + " to Display Transactions for a Client");
        System.out.println(DISPLAY_WAITLIST + " to Display Waitlist for a Product");
        System.out.println(DISPLAY_OUTSTANDING_BALANCES + " to Display Clients with Outstanding Balances");
        System.out.println(SAVE + " to Save Warehouse Data to File");
        System.out.println(RETRIEVE + " to Retrieve Warehouse Data from File");
        System.out.println(HELP + " for Help");
        System.out.println("==============================================\n");
    }

    // --- Action Methods ---

    public void addClient() {
        String name = getToken("Enter client name:");
        String address = getToken("Enter client address:");
        Client result = warehouse.addClient(name, address);
        if (result == null) {
            System.out.println("Could not add client. Verify name and address are not blank.");
            return;
        }
        System.out.println("Client added successfully:\n" + result);
    }

    public void addProduct() {
        do {
            String name = getToken("Enter product name:");
            int quantity = getNumber("Enter initial stock quantity:");
            double price = getDouble("Enter unit price:");

            Product result = warehouse.addProduct(name, quantity, price);
            if (result != null) {
                System.out.println("Product added successfully:\n" + result);
            } else {
                System.out.println("Product could not be added. Check input values.");
            }

            if (!yesOrNo("Add another product?")) {
                break;
            }
        } while (true);
    }

    public void addToWishlist() {
        String clientId = getToken("Enter client ID:");
        if (warehouse.searchClient(clientId) == null) {
            System.out.println("Client not found.");
            return;
        }

        do {
            String productId = getToken("Enter product ID:");
            int quantity = getNumber("Enter quantity desired:");

            int status = warehouse.addToWishlist(clientId, productId, quantity);
            switch (status) {
                case Warehouse.CLIENT_NOT_FOUND:
                    System.out.println("Client not found.");
                    return;
                case Warehouse.PRODUCT_NOT_FOUND:
                    System.out.println("Product not found.");
                    break;
                case Warehouse.OPERATION_FAILED:
                    System.out.println("Invalid quantity. Must be positive.");
                    break;
                case Warehouse.OPERATION_COMPLETED:
                    System.out.println("Product added/updated in wishlist.");
                    break;
                default:
                    System.out.println("An unknown error occurred.");
            }

            if (!yesOrNo("Add another item to this wishlist?")) {
                break;
            }
        } while (true);
    }

    public void displayWishlist() {
        String clientId = getToken("Enter client ID:");
        warehouse.displayWishlist(clientId);
    }

    public void processOrder() {
        String clientId = getToken("Enter client ID:");
        Client client = warehouse.searchClient(clientId);
        if (client == null) {
            System.out.println("Client not found.");
            return;
        }

        Wishlist wishlist = client.getWishlist();
        Map<String, Integer> selectedItems = new LinkedHashMap<>();

        // 1. Review existing wishlist items
        if (!wishlist.isEmpty()) {
            System.out.println("Reviewing current wishlist items for order:");
            for (Map.Entry<String, Integer> entry : wishlist.getItems().entrySet()) {
                String pId = entry.getKey();
                int defaultQty = entry.getValue();
                Product p = warehouse.searchProduct(pId);
                String pName = (p != null) ? p.getProductName() : pId;

                if (yesOrNo("Include " + pName + " (ID: " + pId + ", Wishlist Qty: " + defaultQty + ")?")) {
                    int qty = getNumber("Enter quantity to order (max desired is " + defaultQty + "):");
                    if (qty > 0) {
                        selectedItems.put(pId, qty);
                    }
                }
            }
        }

        // 2. Allow adding extra products directly to the order
        if (yesOrNo("Would you like to add additional products not currently on the wishlist?")) {
            do {
                String pId = getToken("Enter product ID:");
                if (warehouse.searchProduct(pId) == null) {
                    System.out.println("Product does not exist.");
                } else {
                    int qty = getNumber("Enter quantity to order:");
                    if (qty > 0) {
                        int existing = selectedItems.getOrDefault(pId, 0);
                        selectedItems.put(pId, Math.addExact(existing, qty));
                    }
                }
                if (!yesOrNo("Add another additional product?")) {
                    break;
                }
            } while (true);
        }

        if (selectedItems.isEmpty()) {
            System.out.println("No items selected. Order canceled.");
            return;
        }

        // 3. Coordinate fulfillment through the warehouse
        Invoice invoice = warehouse.processOrder(clientId, selectedItems);
        if (invoice == null) {
            System.out.println("Order processing failed.");
            return;
        }

        System.out.println("\nOrder processed successfully.");
        if (invoice.isEmpty()) {
            System.out.println("Notice: No stock was available to ship immediately. All items moved to the waitlist.");
        } else {
            invoice.displayInvoice();
        }
    }

    public void receiveShipment() {
        do {
            String productId = getToken("Enter product ID:");
            int quantity = getNumber("Enter quantity received:");

            int status = warehouse.receiveShipment(productId, quantity);
            switch (status) {
                case Warehouse.PRODUCT_NOT_FOUND:
                    System.out.println("Product not found.");
                    break;
                case Warehouse.OPERATION_FAILED:
                    System.out.println("Invalid shipment quantity.");
                    break;
                case Warehouse.OPERATION_COMPLETED:
                    System.out.println("Shipment processed. Outstanding waitlists fulfilled and remaining stock added.");
                    break;
                default:
                    System.out.println("An unknown error occurred.");
            }

            if (!yesOrNo("Receive another product shipment?")) {
                break;
            }
        } while (true);
    }

    public void receivePayment() {
        String clientId = getToken("Enter client ID:");
        Client client = warehouse.searchClient(clientId);
        if (client == null) {
            System.out.println("Client not found.");
            return;
        }

        System.out.println("Current outstanding balance: $" + String.format("%.2f", client.getBalance()));
        double amount = getDouble("Enter payment amount:");

        int status = warehouse.receivePayment(clientId, amount);
        switch (status) {
            case Warehouse.CLIENT_NOT_FOUND:
                System.out.println("Client not found.");
                break;
            case Warehouse.OPERATION_FAILED:
                System.out.println("Payment amount must be greater than zero.");
                break;
            case Warehouse.OPERATION_COMPLETED:
                System.out.println("Payment recorded successfully.");
                System.out.println("Updated balance: $" + String.format("%.2f", client.getBalance()));
                break;
            default:
                System.out.println("An unknown error occurred.");
        }
    }

    public void displayClients() {
        System.out.println("\n--- All Clients ---");
        warehouse.displayClients();
    }

    public void displayProducts() {
        System.out.println("\n--- All Products ---");
        warehouse.displayProducts();
    }

    public void displayInvoices() {
        String clientId = getToken("Enter client ID:");
        warehouse.displayInvoices(clientId);
    }

    public void displayTransactions() {
        String clientId = getToken("Enter client ID:");
        warehouse.displayTransactions(clientId);
    }

    public void displayWaitlist() {
        String productId = getToken("Enter product ID:");
        warehouse.displayWaitlist(productId);
    }

    public void displayOutstandingBalances() {
        System.out.println("\n--- Clients with Outstanding Balances ---");
        warehouse.displayOutstandingBalances();
    }

    private void save() {
        if (Warehouse.save()) {
            System.out.println("The warehouse has been successfully saved to WarehouseData.\n");
        } else {
            System.out.println("There was an error saving warehouse data.\n");
        }
    }

    private void retrieve() {
        try {
            Warehouse tempWarehouse = Warehouse.retrieve();
            if (tempWarehouse != null) {
                System.out.println("The warehouse has been successfully retrieved from WarehouseData.\n");
                warehouse = tempWarehouse;
            } else {
                System.out.println("File does not exist or corrupted; creating new warehouse instance.\n");
                warehouse = Warehouse.instance();
            }
        } catch (Exception e) {
            e.printStackTrace();
            warehouse = Warehouse.instance();
        }
    }

    public void process() {
        int command;
        help();
        while ((command = getCommand()) != EXIT) {
            switch (command) {
                case ADD_CLIENT:
                    addClient();
                    break;
                case ADD_PRODUCT:
                    addProduct();
                    break;
                case ADD_TO_WISHLIST:
                    addToWishlist();
                    break;
                case DISPLAY_WISHLIST:
                    displayWishlist();
                    break;
                case PROCESS_ORDER:
                    processOrder();
                    break;
                case RECEIVE_SHIPMENT:
                    receiveShipment();
                    break;
                case RECEIVE_PAYMENT:
                    receivePayment();
                    break;
                case DISPLAY_CLIENTS:
                    displayClients();
                    break;
                case DISPLAY_PRODUCTS:
                    displayProducts();
                    break;
                case DISPLAY_INVOICES:
                    displayInvoices();
                    break;
                case DISPLAY_TRANSACTIONS:
                    displayTransactions();
                    break;
                case DISPLAY_WAITLIST:
                    displayWaitlist();
                    break;
                case DISPLAY_OUTSTANDING_BALANCES:
                    displayOutstandingBalances();
                    break;
                case SAVE:
                    save();
                    break;
                case RETRIEVE:
                    retrieve();
                    break;
                case HELP:
                    help();
                    break;
            }
        }
        System.out.println("Warehouse management system terminated.");
    }

    public static void main(String[] args) {
        UserInterface.instance().process();
    }
}
