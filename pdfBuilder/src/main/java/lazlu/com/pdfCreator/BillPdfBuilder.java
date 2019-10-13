package lazlu.com.pdfCreator;

import lazlu.com.pdfCreator.model.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class BillPdfBuilder {

    private final DecimalFormat currencyFormat = new DecimalFormat("€#.00");
    private static final String HEADER_FORMAT = "%-45s %9s %10s %13s";

    private final Order order;
    private final Owner owner;
    private final List<String> lineItems = new ArrayList<>();
    private final PDDocument document;

    private static final PDFont NORMAL_FONT = PDType1Font.COURIER;
    private static final PDFont BOLD = PDType1Font.COURIER_BOLD;

    private static final int NORMAL_FONT_SIZE = 10;

    public BillPdfBuilder(Order order, Owner owner) {
        this.order = order;
        this.owner = owner;
        generateLineItems();
        document = new PDDocument();
    }

    public void build() throws IOException {

        PDPage firstPage = new PDPage();
        document.addPage(firstPage);
        PDPageContentStream contentStream = new PDPageContentStream(document, firstPage);

        buildBillHeader(firstPage, contentStream);
        buildBillInfoText(firstPage, contentStream);
        buildItemHeader(contentStream);
        buildItems(contentStream);
        buildTotal(contentStream);
        buildAccountNumberDisplay(contentStream);

        contentStream.close();
        document.save("facturaXXX.pdf");
        document.close();

    }

    private void buildAccountNumberDisplay(PDPageContentStream contentStream) throws IOException {
        contentStream.newLine();
        contentStream.newLine();
        contentStream.showText("Account Number : XXXXXXXX");
        contentStream.endText();
    }

    private void buildTotal(PDPageContentStream contentStream) throws IOException {
        contentStream.newLine();
        String billTotalLine = getBillTotalLine();
        contentStream.setFont(PDType1Font.COURIER_BOLD, NORMAL_FONT_SIZE);
        contentStream.newLine();
        contentStream.showText(billTotalLine);
    }

    private void buildItems(PDPageContentStream contentStream) throws IOException {
        contentStream.setFont(NORMAL_FONT, NORMAL_FONT_SIZE);
        for(String lineItem : lineItems) {
            contentStream.newLine();
            contentStream.showText(lineItem);
        }
    }

    private void buildItemHeader(PDPageContentStream contentStream) throws IOException {

        contentStream.setFont(PDType1Font.COURIER_BOLD, NORMAL_FONT_SIZE);
        contentStream.newLine();
        contentStream.showText(buildItemHeader());
    }

    private void buildBillHeader(PDPage firstPage, PDPageContentStream contentStream)
            throws IOException {

        float offsetXName = buildOwnerNameAndGetOffsetX(firstPage, contentStream);
        buildOwnerAddressLine1(firstPage, contentStream, offsetXName);
    }

    private void buildOwnerAddressLine1(PDPage firstPage, PDPageContentStream contentStream, float offsetXName) throws IOException {
        PDFont currentFont;
        int currentFontSize;
        currentFont = PDType1Font.COURIER_BOLD;
        currentFontSize = 12;
        contentStream.setFont(currentFont, currentFontSize);
        float offsetX2 = getCenteredTextXPos(firstPage, owner.getAddressLine1(), currentFont, currentFontSize);
        contentStream.newLineAtOffset(-offsetXName + offsetX2, -5f);
        contentStream.newLine();
        contentStream.showText(owner.getAddressLine1());
        contentStream.endText();
    }

    private float buildOwnerNameAndGetOffsetX(PDPage firstPage, PDPageContentStream contentStream) throws IOException {
        PDFont currentFont;
        int currentFontSize;
        contentStream.setLeading(10);
        currentFont = BOLD;
        currentFontSize = 14;
        contentStream.setFont(currentFont, currentFontSize);
        contentStream.beginText();
        float offsetX = getCenteredTextXPos(firstPage, owner.getCompleteName(), currentFont, currentFontSize);
        contentStream.newLineAtOffset(offsetX, 750f);
        contentStream.showText(owner.getCompleteName());
        return offsetX;
    }

    private void buildBillInfoText(PDPage firstPage, PDPageContentStream contentStream)
            throws IOException {
        Customer customer = order.getCustomer();
        String guestName = customer.getLastName() + ", " + customer.getFirstName();
        MailingAddress address = customer.getMailingAddress();
        String country = address.getCountry();
        String city = address.getCity();
        UsState state = address.getState();
        String postalCode = address.getPostalCode();
        String addressLine1 = address.getAddressLine1();
        String addressLine2 = address.getAddressLine2();

        contentStream.beginText();

        contentStream.newLineAtOffset(80f, 710f);
        contentStream.setLeading(10);
        contentStream.setFont(BOLD, NORMAL_FONT_SIZE);
        contentStream.showText("Customer name: ");
        contentStream.setFont(NORMAL_FONT, NORMAL_FONT_SIZE);
        contentStream.showText(guestName);

        contentStream.newLine();
        contentStream.setFont(BOLD, NORMAL_FONT_SIZE);
        contentStream.showText("Billing Address: ");
        contentStream.setFont(NORMAL_FONT, NORMAL_FONT_SIZE);
        contentStream.newLine();
        contentStream.showText(addressLine1);
        if(StringUtils.isNotEmpty(addressLine2)) {
            contentStream.showText(",");
            contentStream.showText(addressLine2);
        }
        contentStream.newLine();
        contentStream.showText(city);
        if(state != null) {
            contentStream.showText(", " + state.getAbbreviation());
        }
        contentStream.showText(" " + postalCode);

        if(StringUtils.isNotEmpty(country)) {
            contentStream.showText(" - ");
            contentStream.showText(country);

        }

        contentStream.newLine();
        contentStream.setFont(BOLD, NORMAL_FONT_SIZE);
        contentStream.showText("Order Number: ");
        contentStream.setFont(NORMAL_FONT, NORMAL_FONT_SIZE);
        contentStream.showText(order.getId());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE, MMMM d, u '-' H:m:s zz");
        String formattedDate = formatter.format(ZonedDateTime.now(ZoneId.systemDefault()));
        contentStream.newLine();
        contentStream.setFont(BOLD, NORMAL_FONT_SIZE);
        contentStream.showText("Bill Printed On: ");
        contentStream.setFont(NORMAL_FONT, NORMAL_FONT_SIZE);
        contentStream.showText(formattedDate);

        contentStream.endText();

        contentStream.beginText();
        contentStream.newLineAtOffset(60f, 640f);

    }

    private float getCenteredTextXPos(PDPage page, String text, PDFont font, int fontSize)
            throws IOException {
        float textWidth = getStringWidth(text, font, fontSize);
        PDRectangle pageSize = page.getMediaBox();
        float pageCenterX = pageSize.getWidth() / 2F;
        float textX = pageCenterX - textWidth/2F;
        return textX;
    }

    private String buildItemHeader() {

        return String.format(
                HEADER_FORMAT, "Concepte", "Quantitat", "Tarifa", "Import"
        );
    }

    private void generateLineItems() {
        for(BillItem billItem : order.getBill().getCharges()) {
            String lineItem = convertBillItemToLineItem(billItem);
            lineItems.add(lineItem);
        }
    }

    private String getBillTotalLine() {
        double billTotal = order.getBill().getTotal();
        return String.format(
                "%80s", ("Total: " + currencyFormat.format(billTotal))
        );
    }


    private String convertBillItemToLineItem(BillItem item) {
        String lineItem;
        double totalPrice = item.getPrice() * item.getQuantity();
        lineItem = String.format(
                HEADER_FORMAT,
                item.getName(),
                item.getQuantity(),
                currencyFormat.format(item.getPrice()),
                currencyFormat.format(totalPrice)
        );
        return lineItem;
    }

    private float getStringWidth(String text, PDFont font, int fontSize) throws IOException {
        return font.getStringWidth(text) * fontSize / 1000F;
    }

}