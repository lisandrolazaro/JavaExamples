package lazlu.com.pdfCreator;

import be.quodlibet.boxable.BaseTable;
import be.quodlibet.boxable.Cell;
import be.quodlibet.boxable.HorizontalAlignment;
import be.quodlibet.boxable.Row;
import be.quodlibet.boxable.VerticalAlignment;
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
import java.util.List;
import java.util.stream.Collectors;

public class BillPdfGenerator {


    private static final String HEADER_FORMAT = "%-45s %58s %20s %20s";
    private static final String ITEMS_FORMAT = "%-45s %42s %25s %20s";
    private static final String SUBTOTAL_FORMAT = "%-45s %90s";
    private static final String TOTAL_SUMMARY_FORMAT = "%-45s %69s";
    private static final String IVA_FORMAT = "%13s %20s";
    private static final float X_MARGIN = 30;
    private static final PDFont NORMAL_FONT = PDType1Font.HELVETICA;
    private static final PDFont BOLD = PDType1Font.HELVETICA_BOLD;
    private static final int NORMAL_FONT_SIZE = 10;

    private final Order order;
    private final Owner owner;
    private final PDDocument document;
    private final PDPageContentStream contentStream;
    private final PDPage firstPage;
    private final DecimalFormat currencyFormat = new DecimalFormat("#.00 €");


    public BillPdfGenerator(Order order, Owner owner) throws IOException {
        this.order = order;
        this.owner = owner;
        document = new PDDocument();
        firstPage = new PDPage();
        document.addPage(firstPage);
        contentStream = new PDPageContentStream(document, firstPage);
    }

    public void build() throws IOException {

        buildOwnerDetails();
        buildCustomerDetails(firstPage);
        buildItemsBody();
        buildAccountNumberDisplay();

        contentStream.close();
        document.save("factura"+order.getId()+".pdf");
        document.close();
    }

    private void buildItemsBody() throws IOException {
        contentStream.beginText();
        buildItemTable(firstPage);
        printLine(buildItemHeader(), BOLD, NORMAL_FONT_SIZE, 60f, 520f);
        buildItems();
        buildItemsSummary(firstPage);
        contentStream.endText();
    }

    private void buildItemsSummary(PDPage firstPage) throws IOException {
        buildSubTotal();
        buildIVA();
        buildIRPF();
        buildTotalSummary();
        buildTotal(firstPage);
    }

    private void buildTotalSummary() throws IOException {
        String totalSummary = String.format(
                TOTAL_SUMMARY_FORMAT,
                "TOTAL",
                currencyFormat.format(order.getTotal())
        );
        printLine(totalSummary ,  BOLD, 12 , -290, -20f);
    }

    private void buildSubTotal() throws IOException {
        String subTotal = String.format(
                SUBTOTAL_FORMAT,
                "B.Imponible",
                currencyFormat.format(order.getSubtotal())

        );
        printLine(subTotal, BOLD, NORMAL_FONT_SIZE, 60f, -80f);
    }

    private void buildIVA() throws IOException {
        String lineItem = String.format(
                IVA_FORMAT,
                "IVA +" + order.getIva() + "%",
                currencyFormat.format(order.getSubtotalWithIva())

        );
        printLine(lineItem, NORMAL_FONT, NORMAL_FONT_SIZE, 290f, -40f);
    }

    private void buildIRPF() throws IOException {
        String lineItem = String.format(
                IVA_FORMAT,
                "IRPF -" + order.getIRPF() + "%",
                "-" + currencyFormat.format(order.getSubtotalWithIRPF())

        );
        printLine(lineItem, NORMAL_FONT, NORMAL_FONT_SIZE, 0, -10f);
    }

    private void buildItemTable(PDPage firstPage) throws IOException {

        float yPosition = 525;
        float highTable = 350;

        BaseTable table = createTable(firstPage, X_MARGIN, yPosition);
        Row<PDPage> headerRow = table.createRow(highTable);
        Cell<PDPage> cell = headerRow.createCell(51, "");
        setDefaultValues(cell);

        cell = headerRow.createCell(12, "");
        setDefaultValues(cell);

        cell = headerRow.createCell(12, "");
        setDefaultValues(cell);

        cell = headerRow.createCell(12, "");
        setDefaultValues(cell);

        table.addHeaderRow(headerRow);
        table.draw();
    }

    private void setDefaultValues(Cell<PDPage> cell) {

        cell.setFontSize(NORMAL_FONT_SIZE);
        cell.setValign(VerticalAlignment.MIDDLE);
        cell.setAlign(HorizontalAlignment.CENTER);
    }

    private BaseTable createTable(PDPage firstPage, float xPosition, float yPosition) throws IOException {
        float yStartNewPage = firstPage.getMediaBox().getHeight() - (2 * xPosition);
        float tableWidth = firstPage.getMediaBox().getWidth();
        boolean drawContent = true;
        float bottomMargin = 10;
        return new BaseTable(yPosition, yStartNewPage,
                bottomMargin, tableWidth, xPosition, document, firstPage, true, drawContent);
    }

    private void buildAccountNumberDisplay() throws IOException {
        contentStream.beginText();
        contentStream.newLineAtOffset(30, 175f);
        contentStream.setFont(NORMAL_FONT, NORMAL_FONT_SIZE);
        twoNewLines();
        contentStream.showText("Dades de cobrament:");
        contentStream.setFont(BOLD, NORMAL_FONT_SIZE);
        twoNewLines();
        contentStream.showText(owner.getBankAccountName());
        twoNewLines();
        contentStream.showText(owner.getBankAccountNumber());
        contentStream.endText();
    }

    private void twoNewLines() throws IOException {
        contentStream.newLine();
        contentStream.newLine();
    }

    private void buildTotal(PDPage firstPage) throws IOException {
        float xPosition = 434;
        float yPosition = 165;
        float highTable = 50;
        BaseTable table = createTable(firstPage, xPosition, yPosition);
        Row<PDPage> headerRow = table.createRow(highTable);
        Cell<PDPage> cell = headerRow.createCell(21, "Total a pagar: " + currencyFormat.format(order.getTotal()));
        cell.setFont(BOLD);
        setDefaultValues(cell);
        table.addHeaderRow(headerRow);
        table.draw();
    }

    private void buildItems() throws IOException {
        contentStream.setFont(NORMAL_FONT, NORMAL_FONT_SIZE);

        List<String> lineItems = order.getBill().getBillingItems().stream()
                .map(this::toString)
                .collect(Collectors.toList());

        for (String lineItem : lineItems) {
            twoNewLines();
            contentStream.showText(lineItem);
        }
    }

    private void buildOwnerDetails() throws IOException {
        contentStream.beginText();
        this.contentStream.setLeading(10);
        printLine(owner.getCompleteName(), BOLD, NORMAL_FONT_SIZE, X_MARGIN, 750f);
        float ty = -5f;
        printLine(owner.getAddressLine1(), NORMAL_FONT, NORMAL_FONT_SIZE, 0, ty);
        printLine(owner.getAddressLine2(), NORMAL_FONT, NORMAL_FONT_SIZE, 0, ty);
        printLine(owner.getNif(), NORMAL_FONT, NORMAL_FONT_SIZE, 0, -9f);
        contentStream.endText();
    }

    private void printLine(String text, PDFont font, int fontSize, float xPosition, float yPosition) throws IOException {
        contentStream.newLine();
        contentStream.setFont(font, fontSize);
        contentStream.newLineAtOffset(xPosition, yPosition);
        contentStream.showText(text);
    }

    private void buildCustomerDetails(PDPage firstPage)
            throws IOException {

        contentStream.beginText();
        float xPosition = getCenterPosition(firstPage);

        printLine(buildCustomerName(), BOLD, NORMAL_FONT_SIZE, xPosition, 670f);
        printLine(order.getCustomer().getCif(), NORMAL_FONT, NORMAL_FONT_SIZE, 0, -5);

        MailingAddress address = order.getCustomer().getMailingAddress();
        printLine(address.getAddressLine1(), NORMAL_FONT, NORMAL_FONT_SIZE, 0, -5);
        printLine(address.getAddressLine2(), NORMAL_FONT, NORMAL_FONT_SIZE, 0, -5);

        buildBillNumberAndDate(firstPage, xPosition);
        contentStream.endText();
    }

    private void buildBillNumberAndDate(PDPage firstPage, float xPosition) throws IOException {
        BaseTable table = createTable(firstPage, xPosition, 590);
        Row<PDPage> row = table.createRow(40);

        Cell<PDPage> cell = row.createCell(21, "Num. Fra. : " + order.getId());
        setDefaultValues(cell);

        cell = row.createCell(21, order.getDateFormatted());
        setDefaultValues(cell);

        table.addHeaderRow(row);
        table.draw();
    }

    private String buildCustomerName() {
        Customer customer = order.getCustomer();
        String completeName = customer.getFirstName();
        if (StringUtils.isNotEmpty(customer.getLastName())) {
            completeName = completeName + " " + customer.getLastName();
        }
        return completeName;
    }

    private float getCenterPosition(PDPage page) {
        PDRectangle pageSize = page.getMediaBox();
        return pageSize.getWidth() / 2F;
    }

    private String buildItemHeader() {

        return String.format(
                HEADER_FORMAT, "Concepte", "Quantitat", "Tarifa", "Import"
        );
    }

    private String toString(BillItem item) {
        String lineItem;
        lineItem = String.format(
                ITEMS_FORMAT,
                item.getName(),
                item.getQuantity(),
                currencyFormat.format(item.getPrice()),
                currencyFormat.format(item.getTotal())
        );
        return lineItem;
    }
}