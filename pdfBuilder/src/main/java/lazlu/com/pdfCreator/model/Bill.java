package lazlu.com.pdfCreator.model;

import java.util.ArrayList;
import java.util.List;

public class Bill {
    private List<BillItem> billingItems;
    private double total;

    public Iterable<? extends BillItem> getCharges() {
        if(billingItems == null) {
            billingItems = new ArrayList<>();
        }
        return billingItems;
    }

    public double getTotal() {
        return total;
    }
}
