package lazlu.com.pdfCreator.model;

import java.text.SimpleDateFormat;
import java.util.Date;
public class Order {
    public static final String IVA = "21";
    public static final String IRPF = "15";

    private Customer customer;
    private String id;
    private Bill bill;
    private Date date;



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

    public Double getSubtotal() {
        return bill.getTotal();
    }

    public String getIva() {
        return IVA;
    }

    public double getSubtotalWithIva() {
        return  (Double.parseDouble(IVA)/ 100) * bill.getTotal();
    }

    public String getIRPF() {
        return IRPF;
    }

    public double getSubtotalWithIRPF() {
        return  (Double.parseDouble(IRPF)/ 100) * bill.getTotal();
    }

    public Object getTotal() {
        return getSubtotal() + getSubtotalWithIva() - getSubtotalWithIRPF() ;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDateFormatted() {
        SimpleDateFormat format1 = new SimpleDateFormat("dd/MM/yyyy");
        return date != null ? format1.format(date) : "";
    }


}
