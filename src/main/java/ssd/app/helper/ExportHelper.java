package ssd.app.helper;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import ssd.app.model.Appointment;
import ssd.app.model.Expense;

@SuppressWarnings("restriction")
public class ExportHelper {
	private static final Logger LOGGER = LoggerFactory.getLogger(ExportHelper.class);
	
	// get a single instance
	private static final ExportHelper INSTANCE = new ExportHelper();
	public static ExportHelper getInstance() {
		return INSTANCE;
	}

	/*
	 * Export the data for year 'year' and store the export file to 'to'. 
	 */
	public static void exportForYear(Boolean exportToXLS, int year, String to){
		List<Appointment> appointments = new ArrayList<Appointment>();
		List<Expense> expenses = new ArrayList<Expense>();
		try {
			appointments = AppointmentHelper.getInstance().getAppointments(year); // get all appointments
			expenses = ExpensesHelper.getInstance().getExpenses(year);
			if(exportToXLS){
				writeToExcel(appointments, expenses, to);
			}else{
				writeToCSV(appointments, expenses, to);
			}
		} catch (SQLException e1) {
			// ignore => just show nothing
			LOGGER.error(e1.getMessage());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		LOGGER.debug(Integer.toString(appointments.size()));
		for (Appointment app : appointments) {
			LOGGER.debug(app.toString());
		}
	}

	/**
	 * Write the appointments to an Excel file.
	 * @param appointments
	 * @param path
	 * @throws IOException 
	 */
	private static void writeToCSV(List<Appointment> appointments, List<Expense> expenses, String path){
    	BufferedWriter writer = null;
    	Path _path = Paths.get(path, "export.csv");
    	try{
	        writer = new BufferedWriter(new FileWriter(_path.toString()));
	        
	        // write header line
	        writer.write("Datum\t");
	        writer.write("Preis\t");
	        writer.write("Leistung\t");
	        writer.write("Patient\t");
	        writer.write("Geburtsdatum\n");
	
	        double income = 0;
	        for(Appointment appointment : appointments){
	            double _income = 0;
            	if (appointment != null && appointment.getService() != null) {
            		_income = appointment.getDuration() * appointment.getService().getCostPerUnit();
            	}
	            if(_income <= 0 || appointment.getPatient().toString().contains("Urlaub ;-)")){
	            	continue;
	            }
	            income += _income;
	            
	            //first place in row is date
	            Calendar cal = Calendar.getInstance();
	            cal.setTimeInMillis(appointment.getDate().getTime());
	            String date = LocalDate.of(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH)).toString();
	            writer.write(date + "\t");
	            // third place in row is the price
	            writer.write(String.valueOf(_income) + "\t");
	            // fourth place in row is the service
	            writer.write(appointment.getService().getName() + "\t");
	            // fifth place in row is the patient
	            writer.write(appointment.getPatient().toString() + "\t");
	            // fifth place in row is the patient
	            cal.setTimeInMillis(appointment.getPatient().getBirthday().getTime());
	            String bdate = LocalDate.of(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH)).toString();
	            writer.write(bdate + "\n");
	        }
	        writer.write("\n\n");
	        writer.write("Gesamte Einnahmen: \t");
	        writer.write(String.valueOf(income) + "\n\n\n");
	        
	        double overallcost = 0;
	        for(Expense expense : expenses){
	            // first place in row is date
	            Calendar cal = Calendar.getInstance();
	            cal.setTimeInMillis(expense.getDate().getTime());
	            String date = LocalDate.of(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH)).toString();
	            writer.write(date + "\t");
	            // second row is the cost
	            overallcost += expense.getCost();
	            writer.write(String.valueOf(expense.getCost()) + "\t");
	            // third row is the name
	            writer.write(expense.getName() + "\t");
	            // forth row is the description
	            writer.write(expense.getDescription() + "\n");
	        }
	        
	        writer.write("\n\n");
	        writer.write("Gesamte Ausgaben: \t");
	        writer.write(String.valueOf(overallcost) + "\n\n\n");
	        
	        writer.write("Einnamen - Ausgaben: \t");
	        writer.write(String.valueOf(income - overallcost));
    	}catch(IOException e){
    		Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Export fehlgeschlagen");
            alert.setHeaderText("Der Export war nicht erfolgreich");
            alert.setContentText(e.getMessage());

            alert.showAndWait();
    	}finally {
    		try {
				writer.close();
			} catch (IOException e) {}
		}
        
		Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Export erfolgreich");
        alert.setHeaderText("Der Export war erfolgreich");
        alert.setContentText("Die Datei kann unter '" + _path.toString() + "' gefunden werden. ");

        alert.showAndWait();
    }
    
	/**
	 * Write the appointments to an Excel file.
	 * @param appointments
	 * @param path
	 */
	private static void writeToExcel(List<Appointment> appointments, List<Expense> expenses, String path){
        // Using XSSF for xlsx format, for xls use HSSF
        Workbook workbook = new XSSFWorkbook();

        Sheet incomeSheet = workbook.createSheet("Export");
        
        // write header line
        Row row = incomeSheet.createRow(0);
        row.createCell(0).setCellValue("Datum");
        row.createCell(1).setCellValue("Beschreibung");
        row.createCell(2).setCellValue("Dauer");
        row.createCell(3).setCellValue("Preis");
        row.createCell(4).setCellValue("Leistung");
        row.createCell(5).setCellValue("Patient");
        row.createCell(6).setCellValue("Geburtsdatum");

        double income = 0;
        int rowIndex = 1;
        for(Appointment appointment : appointments){
            row = incomeSheet.createRow(rowIndex++);
            int cellIndex = 0;
            //first place in row is date
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(appointment.getDate().getTime());
            String date = LocalDate.of(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH)).toString();
            row.createCell(cellIndex++).setCellValue(date);
            // second place in row is the description
            row.createCell(cellIndex++).setCellValue(appointment.getDescription());
            // third place in row is the duration
            row.createCell(cellIndex++).setCellValue(appointment.getDuration());
            // third place in row is the price
            double _income = appointment.getDuration() * appointment.getService().getCostPerUnit();
            income += _income;
            row.createCell(cellIndex++).setCellValue(_income);
            // fourth place in row is the service
            row.createCell(cellIndex++).setCellValue(appointment.getService().getName());
            // fifth place in row is the patient
            row.createCell(cellIndex++).setCellValue(appointment.getPatient().toString());
            // fifth place in row is the patient
            cal.setTimeInMillis(appointment.getPatient().getBirthday().getTime());
            String bdate = LocalDate.of(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH)).toString();
            row.createCell(cellIndex++).setCellValue(bdate);
        }
        
        row = incomeSheet.createRow(rowIndex++);
        row.createCell(0).setCellValue("Gesamte Einnahmen: ");
        row.createCell(1).setCellValue(income);
        
        row = incomeSheet.createRow(rowIndex++);	// blank line
        
        double overallcost = 0;
        for(Expense expense : expenses){
        	row = incomeSheet.createRow(rowIndex++);
            int cellIndex = 0;
            // first place in row is date
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(expense.getDate().getTime());
            String date = LocalDate.of(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH)).toString();
            row.createCell(cellIndex++).setCellValue(date);
            // second row is the cost
            overallcost += expense.getCost();
            row.createCell(cellIndex++).setCellValue(expense.getCost());
            // third row is the name
            row.createCell(cellIndex++).setCellValue(expense.getName());
            // forth row is the description
            row.createCell(cellIndex++).setCellValue(expense.getDescription());
        }
        
        row = incomeSheet.createRow(rowIndex++);	// blank line
        row = incomeSheet.createRow(rowIndex++);
        row.createCell(0).setCellValue("Gesamte Ausgaben: ");
        row.createCell(1).setCellValue(overallcost);
        
        row = incomeSheet.createRow(rowIndex++);
        row.createCell(0).setCellValue("Einnamen - Ausgaben: ");
        row.createCell(1).setCellValue(income - overallcost);
        
        
        //write this workbook in excel file.
    	Path _path = Paths.get(path, "export.xlsx");
        try {
            FileOutputStream fos = new FileOutputStream(_path.toFile());
            workbook.write(fos);
            fos.close();

            System.out.println(_path.toString() + " is successfully written");
            workbook.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
		Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Export erfolgreich");
        alert.setHeaderText("Der Export war erfolgreich");
        alert.setContentText("Die Datei kann unter '" + _path.toString() + "' gefunden werden. ");

        alert.showAndWait();
    }
	
	/**
	 * Create a PDF invoice of the given appointments
	 * 
	 * @param appointments
	 * @return
	 */
	public static Boolean createInvoice(List<Appointment> appointments){
		Document document = new Document(PageSize.A4);
		try {
			PdfWriter.getInstance(document, new FileOutputStream("test.pdf"));
			document.open();
			for (Appointment appointment : appointments) {
				document.add(new Paragraph(appointment.getService().getName() + ": " + appointment.getDate()));
			}
			document.close();
			return true;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
}
