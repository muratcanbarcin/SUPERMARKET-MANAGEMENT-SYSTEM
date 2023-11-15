import java.util.ArrayList;

public class Customer {
    private String customerId;
    private String customerName;
    private ArrayList<Purchase> purchases;

    public Customer(String customerId, String customerName) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.purchases = new ArrayList<>();
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public ArrayList<Purchase> getPurchases() {
        return purchases;
    }

    public void addPurchase(String date, String productName) {
        purchases.add(new Purchase(date, productName));
    }
}


