package lazlu.com.pdfCreator.model;

public class Customer {
    private String lastName;
    private String firstName;
    private MailingAddress mailingAddress;

    public String getLastName() {
        return lastName;
    }

    public String getFirstName() {
       return firstName;
    }

    public MailingAddress getMailingAddress() {
        return mailingAddress;

    }

    public void setMailingAddress(MailingAddress mailingAddress) {
        this.mailingAddress = mailingAddress;
    }
}
