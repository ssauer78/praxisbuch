package ssd.app.helper;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.collections.ObservableList;
import ssd.app.model.Appointment;
import ssd.app.model.Patient;

public class AppointmentHelperTest {
	private static final Logger log = LoggerFactory.getLogger(AppointmentHelperTest.class);
	
	private static Patient p1 = new Patient();
	private static Patient p2 = new Patient();
	private static Appointment a0 = new Appointment();
	private static Appointment a1 = new Appointment();
	private static Appointment a2 = new Appointment();
	private static Appointment a3 = new Appointment();
	private static Appointment a4 = new Appointment();
	private static Appointment a5 = new Appointment();
	private static Appointment a6 = new Appointment();
	private static Appointment a7 = new Appointment();
	private static Appointment a8 = new Appointment();
	private static Appointment a9 = new Appointment();
	
	@BeforeClass
	public static void init() throws SQLException{
		log.debug("Initialize test user");
		if(p1.getId() > 0){	// already initialized
			return;
		}
		p1.setName("Mustermann");
		p1.setGivenName("Maxine");
		Date dateobj = new GregorianCalendar(1992, Calendar.JANUARY, 1).getTime();
		p1.setBirthday(new java.sql.Timestamp(dateobj.getTime()));
		p1.save();
		
		p2.setName("Mustermann");
		p2.setGivenName("Max");
		dateobj = new GregorianCalendar(1980, Calendar.JUNE, 12).getTime();
		p2.setBirthday(new java.sql.Timestamp(dateobj.getTime()));
		p2.save();
		
		try {
			List<Appointment> app = AppointmentHelper.getInstance().getAppointments(2016);
			if (app.size() == 0){
				
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		log.debug("Initialize test appointments for test users");
		
		LocalDateTime date = LocalDateTime.now();
		LocalDateTime _date = date.plusHours(1);
		a0.setDate(Timestamp.valueOf(_date));
		a0.setPatient(p1);
		a0.setDescription("Description a0");
		a0.setDuration(1);
		a0.save();
		
		_date = date.plusHours(2);
		a1.setDate(Timestamp.valueOf(_date));
		a1.setPatient(p2);
		a1.setDescription("Description a1");
		a1.setDuration(1);
		a1.setCreated(new Date());
		a1.setModified(new Date());
		a1.save();

		_date = date.plusHours(3);
		a2.setDate(Timestamp.valueOf(_date));
		a2.setPatient(p1);
		a2.setDescription("Description a2");
		a2.setDuration(2);
		a2.save();
		
		_date = date.plusHours(12);
		a3.setDate(Timestamp.valueOf(_date));
		a3.setPatient(p2);
		a3.setDescription("Description a3");
		a3.setDuration(1);
		a3.save();
		
		_date = date.minusHours(1);
		a4.setDate(Timestamp.valueOf(_date));
		a4.setPatient(p2);
		a4.setDescription("Description a4");
		a4.setDuration(1);
		a4.save();

		_date = date.minusHours(2);
		a5.setDate(Timestamp.valueOf(_date));
		a5.setPatient(p1);
		a5.setDescription("Description a5");
		a5.setDuration(1);
		a5.save();
		
		_date = date.minusHours(3);
		a6.setDate(Timestamp.valueOf(_date));
		a6.setPatient(p2);
		a6.setDescription("Description a6");
		a6.setDuration(1);
		a6.save();
		
		_date = date.minusHours(24);
		a7.setDate(Timestamp.valueOf(_date));
		a7.setPatient(p1);
		a7.setDescription("Description a7");
		a7.setDuration(1);
		a7.save();
		
		_date = date.minusHours(26);
		a8.setDate(Timestamp.valueOf(_date));
		a8.setPatient(p2);
		a8.setDescription("Description a8");
		a8.setDuration(1);
		a8.save();
		
		_date = date.minusHours(28);
		a9.setDate(Timestamp.valueOf(_date));
		a9.setPatient(p2);
		a9.setDescription("Description a9");
		a9.setDuration(1);
		a9.save();
	}
	
	@Test
	public void getLatest3() throws Exception{
		
		ObservableList<Appointment> appointments = AppointmentHelper.getLatestAppointments(3);
		
		assertEquals(3, appointments.size());
		
		assertEquals(a4.getId(), appointments.get(0).getId());
	}
	
	@Test
	public void getNext3() throws Exception{
		
		ObservableList<Appointment> appointments = AppointmentHelper.getNextAppointments(3);
		
		assertEquals(3, appointments.size());
		
		//assertEquals(a0.getId(), appointments.get(0).getId());
	}

}
