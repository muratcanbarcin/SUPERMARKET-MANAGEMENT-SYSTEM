public class Purchase {
    private String date;
    private String productName;

    public Purchase(String date, String productName) {
        this.date = date;
        this.productName = productName;
    }

    public String getDate() {
        return date;
    }

    public String getProductName() {
        return productName;
    }
}
