package ssd.app.helper;

import static org.junit.Assert.*;

import java.time.LocalDate;

import org.junit.Test;

import javafx.util.StringConverter;
import ssd.app.model.Patient;

@SuppressWarnings("restriction")
public class ApplicationHelperTest {

	@Test
	public void stringConverterLocalDate() throws Exception{
		StringConverter<LocalDate> converter = ApplicationHelper.getStringConverterLocalDate();
		String string1 = "29.04.2013";	// that's the date pattern. needs refactoring if the pattern changes
		
		LocalDate date1 = converter.fromString(string1);
		assertTrue(date1.getClass().equals(LocalDate.class));	// useless?
		
		assertEquals(string1, converter.toString(date1));
		
		String string2 = "31.04.2013";
		
		LocalDate date2 = converter.fromString(string2);
		assertTrue(date2.getClass().equals(LocalDate.class));	// useless?
		
		assertNotEquals(string2, converter.toString(date2));	// should fail because there is no 31 of April
	}
	
	
	@Test
	public void getAppointmentsForYear() throws Exception{
		Patient patient = new Patient();
		patient.setName("Mustermann");
		patient.setGivenName("Max");
		patient.setEmail("max@mustermann.de");
		patient.save();
		
		// TODO
	}
	
	@Test
	public void getAppointmentsForPatient() throws Exception{
		Patient patient = new Patient();
		patient.setName("Mustermann");
		patient.setGivenName("Max");
		patient.setEmail("max@mustermann.de");
		patient.save();
		
		long patientId = patient.getId();
		
		// TODO
	}

}
