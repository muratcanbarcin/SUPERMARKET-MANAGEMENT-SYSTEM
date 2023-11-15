import java.util.ArrayList;

public class HashTable {
    private static final int INITIAL_CAPACITY = 10;
    private static final double LOAD_FACTOR_THRESHOLD = 0.7;

    private ArrayList<Customer>[] table;
    private int size;

    public HashTable() {
        this.table = new ArrayList[INITIAL_CAPACITY];
        this.size = 0;
    }

    public void put(String customerId, String customerName, String date, String productName) {
        int index = simpleHashFunction(customerId);

        while (table[index] != null) {
            index++;
        }
        table[index] = new ArrayList<>();
        Customer customer = new Customer(customerId, customerName);
        customer.addPurchase(date, productName);
        table[index].add(customer);
        size++;

        if (size / (double) table.length > LOAD_FACTOR_THRESHOLD) {
            resize();
        }
    }

    public void get(String customerId) {
        int index = simpleHashFunction(customerId);

        if (table[index] != null) {
            for (Customer customer : table[index]) {
                if (customer.getCustomerId().equals(customerId)) {
                    System.out.println("Customer ID: " + customer.getCustomerId());
                    System.out.println("Customer Name: " + customer.getCustomerName());

                    System.out.println("Purchases (Reverse Chronological Order):");
                    for (int i = customer.getPurchases().size() - 1; i >= 0; i--) {
                        Purchase purchase = customer.getPurchases().get(i);
                        System.out.println("Date: " + purchase.getDate() + ", Product: " + purchase.getProductName());
                    }
                    return;
                }
            }
        }

        System.out.println("Customer not found.");
    }

    public void remove(String customerId) {
        int index = simpleHashFunction(customerId);

        if (table[index] != null) {
            table[index].removeIf(customer -> customer.getCustomerId().equals(customerId));
            size--;
        }
    }

    private int simpleHashFunction(String key) {
        int hash = 0;
        for (char ch : key.toCharArray()) {
            hash += ch;
        }
        return hash % table.length;
    }

    private void resize() {
        ArrayList<Customer>[] newTable = new ArrayList[table.length * 2];
        for (ArrayList<Customer> customers : table) {
            if (customers != null) {
                for (Customer customer : customers) {
                    int newIndex = simpleHashFunction(customer.getCustomerId());
                    if (newTable[newIndex] == null) {
                        newTable[newIndex] = new ArrayList<>();
                    }
                    newTable[newIndex].add(customer);
                }
            }
        }
        table = newTable;
    }
}
