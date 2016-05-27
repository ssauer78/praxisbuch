package ssd.app.helper;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import ssd.app.model.Appointment;

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
	public static void exportForYear(int year, String to){
		List<Appointment> appointments = new ArrayList<Appointment>();
		try {
			appointments = AppointmentHelper.getInstance().getAppointments(year); // get all appointments
			writeToExcel(appointments, to);
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
	 */
	public static void writeToExcel(List<Appointment> appointments, String path){
        // Using XSSF for xlsx format, for xls use HSSF
        Workbook workbook = new XSSFWorkbook();

        Sheet apointmentSheet = workbook.createSheet("Export");
        
        // write header line
        Row row = apointmentSheet.createRow(0);
        row.createCell(0).setCellValue("Datum");
        row.createCell(1).setCellValue("Beschreibung");
        row.createCell(2).setCellValue("Dauer");
        row.createCell(3).setCellValue("Preis");
        row.createCell(4).setCellValue("Leistung");
        row.createCell(5).setCellValue("Patient");
        row.createCell(6).setCellValue("Geburtsdatum");

        int rowIndex = 1;
        for(Appointment appointment : appointments){
            row = apointmentSheet.createRow(rowIndex++);
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
            row.createCell(cellIndex++).setCellValue(appointment.getDuration() * appointment.getService().getCostPerUnit());
            // fourth place in row is the service
            row.createCell(cellIndex++).setCellValue(appointment.getService().getName());
            // fifth place in row is the patient
            row.createCell(cellIndex++).setCellValue(appointment.getPatient().toString());
            // fifth place in row is the patient
            cal.setTimeInMillis(appointment.getPatient().getBirthday().getTime());
            String bdate = LocalDate.of(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH)).toString();
            row.createCell(cellIndex++).setCellValue(bdate);
        }

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
}
