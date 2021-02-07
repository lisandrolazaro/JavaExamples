package lazlu.com.pdfCreator.model;

import java.util.ArrayList;
import java.util.List;

public class Bill {
    private List<BillItem> billingItems;

    public List<BillItem> getBillingItems() {
        if(billingItems == null) {
            billingItems = new ArrayList<>();
        }
        return billingItems;
    }

    public Double getTotal() {
       return billingItems.stream()
                .mapToDouble(BillItem::getTotal)
                .sum();
    }
}
