package lazlu.com.pdfCreator;

import lazlu.com.pdfCreator.model.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class BillPdfBuilderTest {

    private BillPdfBuilder pdfCreatorTest;

    @Test
    public void starting() throws IOException {

        Order order = new Order();
        order.setId("id");

        Bill bill = new Bill();
        MailingAddress mailingAddress = new MailingAddress();
        mailingAddress.setAddressLine1("Address Line 1");
        mailingAddress.setCity("Barcelona");
        Customer customer = new Customer();
        customer.setMailingAddress(mailingAddress);
        order.setCustomer(customer);
        order.setBill(bill);

        Owner owner = new Owner();
        owner.setCompleteName("Juan Peres");
        owner.setAddressLine1("Av diagonal 315, piso 1-2 , 08017, Barcelona");
        pdfCreatorTest = new BillPdfBuilder(order, owner);

        pdfCreatorTest.build();

    }


}
