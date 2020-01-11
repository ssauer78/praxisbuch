package ssd.app.helper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.CMYKColor;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

import ssd.app.model.Appointment;

public class WriteInvoice {
	Font fontNormal = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.NORMAL, new CMYKColor(255, 255, 255, 255));
	Font fontBold = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.BOLD, new CMYKColor(255, 255, 255, 255));
	
	private Appointment appointment;
	
	public static final String RESULT = "/Users/sauer/Downloads/test.pdf";
	
	
	
	/** Inner class to add a header and a footer. */
    class HeaderFooter extends PdfPageEventHelper {
        /** Alternating phrase for the header. */
        Phrase header = new Phrase();
        /** Current page number (will be reset for every chapter). */
        int pagenumber = 0;
 
        /**
         * Initialize one of the headers.
         * @see com.itextpdf.text.pdf.PdfPageEventHelper#onOpenDocument(
         *      com.itextpdf.text.pdf.PdfWriter, com.itextpdf.text.Document)
         */
        public void onOpenDocument(PdfWriter writer, Document document) {
            // header = new Phrase("Praxis für osteopathische und ganzheitliche Therapie");
        }
 
        /**
         * Increase the page number.
         * @see com.itextpdf.text.pdf.PdfPageEventHelper#onStartPage(
         *      com.itextpdf.text.pdf.PdfWriter, com.itextpdf.text.Document)
         */
        public void onStartPage(PdfWriter writer, Document document) {
            pagenumber++;
        }
 
        /**
         * Adds the header and the footer.
         * @see com.itextpdf.text.pdf.PdfPageEventHelper#onEndPage(
         *      com.itextpdf.text.pdf.PdfWriter, com.itextpdf.text.Document)
         */
        public void onEndPage(PdfWriter writer, Document document) {
            Rectangle rect = writer.getBoxSize("art");
            ColumnText.showTextAligned(writer.getDirectContent(),
                    Element.ALIGN_LEFT, header,
                    rect.getLeft(), rect.getTop(), 0);
            
            // Phrase footer = new Phrase(String.format("Seite %d\n", pagenumber));
            Phrase footer = new Phrase();
            footer.add(getBankAccount());
            ColumnText.showTextAligned(writer.getDirectContent(),
                    Element.ALIGN_LEFT, footer,
                    36, rect.getBottom() - 18, 0);
        }
        
        private String getBankAccount(){
    		return "Volksbank Rhein-Neckar - IBAN: DE94 6709 0000 0030 1233 01 - BIC: GENODE61MA2";
    	}
    }
    
	public WriteInvoice(Appointment appointment) {
		this.appointment = appointment;
	}
	
	public void writeInvoiceToPDF() throws IOException, DocumentException{
		Document document = new Document();
		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(RESULT));
		
		Rectangle rect = new Rectangle(36, 54, 559, 788);
		rect.setBorder(Rectangle.BOX);
        rect.setBorderWidth(2);
        rect.setBorderColor(BaseColor.BLACK);
		writer.setBoxSize("art", rect);
		writer.setPageEvent(new HeaderFooter());
        // setting page size, margins and mirrered margins
        document.setPageSize(PageSize.A4);
        document.setMargins(36, 36, 54, 54);
        document.setMarginMirroringTopBottom(false);
        document.open();
        document.add(new Paragraph("Praxis für osteopathische und ganzheitliche Therapie", fontBold));
        document.add(new Paragraph(
            "Caroline Wenzel \n" +
            "Heilpraktikerin \n", fontBold));
        document.add(new Paragraph(
            "Drechsler Straße 2 \n" +
            "68535 Edingen-Neckarhausen \n" + 
            "Tel.: 0621/430 292 51", fontNormal));
        
        document.add(new Paragraph(""));
        
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.addCell(getCell("Max Mustermann\nMusterstraße 1\n123456 Musterstadt", PdfPCell.ALIGN_LEFT));
        table.addCell(getCell(getInvoiceDetails(), PdfPCell.ALIGN_RIGHT));
        document.add(table);
        
        Paragraph paragraph = new Paragraph();
        paragraph.setAlignment(Element.ALIGN_JUSTIFIED);
        for (int i = 0; i < 70; i++) {
            paragraph.add("Hello World! Hello People! " +
                "Hello Sky! Hello Sun! Hello Moon! Hello Stars!");
        }
        document.add(paragraph);
        document.add(new Paragraph("The top margin 180pt (2.5 inch)."));
        // step 5
        document.close();
	}
	
	private PdfPCell getCell(String text, int alignment) {
	    PdfPCell cell = new PdfPCell(new Phrase(text));
	    cell.setPadding(0);
	    cell.setHorizontalAlignment(alignment);
	    cell.setBorder(PdfPCell.NO_BORDER);
	    return cell;
	}
	
	private String getInvoiceDetails(){
		StringBuilder invoiceDetails = new StringBuilder();
		Date today = Calendar.getInstance().getTime();
		SimpleDateFormat todayFormatted = new SimpleDateFormat("dd.MM.yyyy");
		invoiceDetails.append("Edingen, ").append(todayFormatted.format(today).toString());
		invoiceDetails.append(System.getProperty("line.separator"));
		invoiceDetails.append("Steuer Nr: 37414/25006");
		invoiceDetails.append(System.getProperty("line.separator"));
		invoiceDetails.append("Rechg. Nr: 05.16");
		invoiceDetails.append(System.getProperty("line.separator"));
		
		return invoiceDetails.toString();
	}
	
	
	
}
