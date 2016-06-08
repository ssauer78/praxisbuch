package ssd.app.model;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ssd.app.helper.AppointmentHelper;
import ssd.app.model.Patient;

public class AppointmentTest {
	private static final Logger log = LoggerFactory.getLogger(AppointmentTest.class);
	
	private Patient p1 = new Patient();
	private Patient p2 = new Patient();
	@Before
	public void init() throws SQLException{
		log.debug("Initialize test user");
		p1.setName("Mustermann");
		p1.setGivenName("Test");
		Date dateobj = new GregorianCalendar(1992, Calendar.JANUARY, 1).getTime();
		p1.setBirthday(new java.sql.Timestamp(dateobj.getTime()));
		p1.save();
		
		p2.setName("Mustermann");
		p2.setGivenName("Max");
		dateobj = new GregorianCalendar(1980, Calendar.JUNE, 12).getTime();
		p2.setBirthday(new java.sql.Timestamp(dateobj.getTime()));
		p2.save();
	}
	
	@After
	public void close(){
		
	}
	
	@Test
	public void testAdd() throws SQLException{
		log.debug("Test add");
		Appointment a1 = new Appointment();
		a1.setCreated(new Date());
		a1.setModified(new Date());
		Date dateobj = new GregorianCalendar(2015, Calendar.JULY, 16, 15, 0).getTime(); // 16. July 2015 15:00
		a1.setDate(new Timestamp(dateobj.getTime()));
		a1.setPatient(p1);
		
		a1.save();
		
		List<Appointment> apps = AppointmentHelper.getAppointments(p1);
		Assert.assertEquals(1, apps.size());
		
		Appointment a2 = apps.get(0);	// get the 
		Assert.assertEquals(a1.toString(), a2.toString());
	}
	
	@Test
	public void testDelete() throws SQLException{
		List<Appointment> apps = AppointmentHelper.getAppointments(p1);
		int appointments = apps.size();
		
		Appointment a1 = new Appointment();
		a1.setCreated(new Date());
		a1.setModified(new Date());
		Date dateobj = new GregorianCalendar(2015, Calendar.JULY, 16, 16, 0).getTime(); // 16. July 2015 16:00
		a1.setDate(new Timestamp(dateobj.getTime()));
		a1.setPatient(p1);
		
		a1.save();
		
		apps = AppointmentHelper.getAppointments(p1);
		Assert.assertEquals(appointments + 1, apps.size());
		
		Appointment a2 = apps.get(0);	// get the 
		Assert.assertEquals(a1.toString(), a2.toString());
		
		// appointment was added, now we remove it
		a1.delete();
		apps = AppointmentHelper.getAppointments(p1);
		Assert.assertEquals(appointments, apps.size());	// no new appointment should be set
	}
	
	@Test
	public void testGet() throws SQLException{
		List<Appointment> apps = AppointmentHelper.getAppointments(p1);
		int appointments1 = apps.size();
		
		apps = AppointmentHelper.getAppointments(p2);
		int appointments2 = apps.size();
		
		Appointment a1 = new Appointment();
		a1.setCreated(new Date());
		a1.setModified(new Date());
		Date dateobj = new GregorianCalendar(2015, Calendar.JULY, 16, 16, 0).getTime(); // 16. July 2015 16:00
		a1.setDate(new Timestamp(dateobj.getTime()));
		a1.setPatient(p1);
		a1.save();
		
		a1 = new Appointment();
		a1.setCreated(new Date());
		a1.setModified(new Date());
		dateobj = new GregorianCalendar(2015, Calendar.JULY, 16, 16, 0).getTime(); // 16. July 2015 16:00
		a1.setDate(new Timestamp(dateobj.getTime()));
		a1.setPatient(p2);
		a1.save();
		
		a1 = new Appointment();
		a1.setCreated(new Date());
		a1.setModified(new Date());
		dateobj = new GregorianCalendar(2015, Calendar.JULY, 16, 15, 0).getTime(); // 16. July 2015 15:00
		a1.setDate(new Timestamp(dateobj.getTime()));
		a1.setPatient(p2);
		a1.save();
		
		apps = AppointmentHelper.getAppointments(p1);
		Assert.assertEquals(appointments1 + 1, apps.size());
		
		apps = AppointmentHelper.getAppointments(p2);
		Assert.assertEquals(appointments2 + 2, apps.size());	// no new appointment should be set
	}
}
