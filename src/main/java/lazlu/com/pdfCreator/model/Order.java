package lazlu.com.pdfCreator.model;

public class Order {
    private Customer customer;
    private String id;
    private Bill bill;


    public Customer getCustomer() {
        
        return customer;
    }

    public String getId() {
        return id;
    }

    public Bill getBill() {
        return bill;
    }

    public void setBill(Bill bill) {
        this.bill = bill;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public void setId(String id) {
        this.id = id;
    }
}
