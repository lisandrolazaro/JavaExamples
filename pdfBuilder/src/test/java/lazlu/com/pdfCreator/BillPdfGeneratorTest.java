package lazlu.com.pdfCreator;

import lazlu.com.pdfCreator.model.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Calendar;

public class BillPdfGeneratorTest {

    private BillPdfGenerator pdfCreatorTest;

    @Test
    public void starting() throws IOException {

        Order order = new Order();
        Calendar cal = Calendar.getInstance();
        order.setDate(cal.getTime());
        order.setId("12345");
        Bill bill = new Bill();
        BillItem billItem = new BillItem();
        billItem.setName("Modulo cirugia capilar - 13 de octubre 2020");
        billItem.setPrice( new Double(20) );
        billItem.setQuantity(2);
        bill.getBillingItems().add(billItem);

        BillItem billItem2 = new BillItem();
        billItem2.setName("Item number 2");
        billItem2.setPrice( new Double(20) );
        billItem2.setQuantity(1);
        bill.getBillingItems().add(billItem2);


        BillItem billItem3 = new BillItem();
        billItem3.setName("Item number 3");
        billItem3.setPrice( new Double(20) );
        billItem3.setQuantity(1);
        bill.getBillingItems().add(billItem3);

        BillItem billItem4 = new BillItem();
        billItem4.setName("Item number 3");
        billItem4.setPrice( new Double(20) );
        billItem4.setQuantity(1);
        bill.getBillingItems().add(billItem4);

        order.setBill(bill);

        MailingAddress mailingAddress = new MailingAddress();
        mailingAddress.setAddressLine1("Address Line 1");
        mailingAddress.setAddressLine2("080535 Granada");

        mailingAddress.setCity("Barcelona");
        Customer customer = new Customer();
        customer.setFirstName("Cliente 1 asociados");
        customer.setLastName(" , SCP");
        customer.setCif("DNI 412343124");
        customer.setMailingAddress(mailingAddress);
        order.setCustomer(customer);
        order.setBill(bill);

        Owner owner = new Owner();
        owner.setCompleteName("Juan Perez Perez");
        owner.setAddressLine1("Av diagonal 315, piso 1-2");
        owner.setAddressLine2("08017, Barcelona");
        owner.setNif("DNI: 12344123");
        owner.setBankAccountName("La Caixa bank");
        owner.setBankAccountNumber("1234 1234214 4312412 4132414 ");

        pdfCreatorTest = new BillPdfGenerator(order, owner);

        pdfCreatorTest.build();

    }


}
