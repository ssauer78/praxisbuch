package ssd.app.helper;

import static org.junit.Assert.*;

import java.time.LocalDate;

import org.junit.Test;

import javafx.util.StringConverter;

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

}
