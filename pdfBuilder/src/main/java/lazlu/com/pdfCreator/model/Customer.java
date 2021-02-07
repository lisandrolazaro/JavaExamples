package lazlu.com.pdfCreator.model;

public class Customer {
    private String lastName;
    private String firstName;
    private String cif;
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

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getCif() {
        return cif;
    }

    public void setCif(String cif) {
        this.cif = cif;
    }
}
