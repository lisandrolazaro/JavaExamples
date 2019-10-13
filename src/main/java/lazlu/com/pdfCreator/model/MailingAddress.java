package lazlu.com.pdfCreator.model;

public class MailingAddress {
    private String country;
    private String city;
    private UsState state;
    private String postalCode;
    private String addressLine1;
    private String addressLine2;

    public String getCountry() {
        return country;
    }

    public String getCity() {
        return city;
    }

    public UsState getState() {
        return state;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getAddressLine1() {
    return addressLine1;
    }

    public String getAddressLine2() {
     return addressLine2;
    }

    public void setAddressLine1(String addressLine1) {
       this.addressLine1 = addressLine1;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setState(UsState state) {
        this.state = state;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }
}
