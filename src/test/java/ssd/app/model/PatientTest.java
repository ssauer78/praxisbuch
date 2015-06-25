package ssd.app.model;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ssd.app.dao.DbHelper;
import ssd.app.helper.PatientsHelper;
import ssd.app.helper.PatientsHelperTest;
import ssd.app.model.Patient;

public class PatientTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(PatientsHelperTest.class);
	
	@Before
	public void init() throws SQLException{
		LOGGER.debug("Truncate table patient");
		Session session = DbHelper.getInstance().openSession();
		session.createSQLQuery("TRUNCATE TABLE Patient").executeUpdate();
	}
	
	@After
	public void close(){
	}
	
	@Test
	public void testAdd() throws SQLException{
		long p2 = this.populate();
		
		List<Patient> patients = PatientsHelper.getInstance().getPatients();
		Assert.assertEquals(3, patients.size());
		
		Patient patient = PatientsHelper.getInstance().getPatient(p2);
		Assert.assertEquals(patient.getName(), "Sauer");
	}
	
	@Test
	public void testDelete() throws SQLException{
		long p2 = this.populate();
		
		List<Patient> patients = PatientsHelper.getInstance().getPatients();
		Assert.assertEquals(3, patients.size());
		
		Patient patient = PatientsHelper.getInstance().getPatient(p2);
		patient.delete();
		
		patients = PatientsHelper.getInstance().getPatients();
		Assert.assertEquals(2, patients.size());
	}
	
	@Test
	public void testGet() throws SQLException{
		
		List<Patient> patients = PatientsHelper.getInstance().getPatients();
		for (Iterator<Patient> iterator = patients.iterator(); iterator.hasNext();){
			Patient patient = (Patient) iterator.next(); 
			System.out.print("First Name: " + patient.getName()); 
			System.out.print("  Last Name: " + patient.getGivenName()); 
			System.out.println("  Birthday: " + patient.getBirthday()); 
		}
	}
	
	public long populate() throws SQLException{
		Patient patient = new Patient();
		patient.setName("Mustermann");
		patient.setGivenName("Max");
		Date dateobj = new GregorianCalendar(1992, Calendar.JANUARY, 1).getTime();
		//patient.setBirthday(dateobj);
		patient.save();
		//long p1 = patient.getId();
		
		patient = null;
		patient = new Patient();
		patient.setName("Sauer");
		patient.setGivenName("Sajoscha");
		dateobj = new GregorianCalendar(1978, Calendar.JULY, 30).getTime();
		//patient.setBirthday(dateobj);
		patient.save();
		long p2 = patient.getId();
		
		patient = null;
		patient = new Patient();
		patient.setName("Test");
		patient.setGivenName("Mark");
		dateobj = new GregorianCalendar(1944, Calendar.APRIL, 4).getTime();
		//patient.setBirthday(dateobj);
		patient.save();
		
		return p2;
	}
}
