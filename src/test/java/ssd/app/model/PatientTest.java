package ssd.app.model;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ssd.app.helper.PatientsHelper;
import ssd.app.helper.PatientsHelperTest;
import ssd.app.model.Patient;

public class PatientTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(PatientsHelperTest.class);
	
	private int initialNumberOfPatients = 0;
	private long idOfPatient2 = 0L;
	
	@Before
	public void init() throws SQLException{
		List<Patient> patients = PatientsHelper.getInstance().getPatients();
		initialNumberOfPatients = patients.size();
		
		this.populate();
	}
	
	@After
	public void close(){
	}
	
	@Test
	public void testAdd() throws SQLException{
		List<Patient> patients = PatientsHelper.getInstance().getPatients();
		Assert.assertEquals(initialNumberOfPatients + 3, patients.size());
		
		Patient patient = PatientsHelper.getInstance().getPatient(idOfPatient2);
		Assert.assertEquals(patient.getName(), "Sauer");
	}
	
	@Test
	public void testDelete() throws SQLException{
		List<Patient> patients = PatientsHelper.getInstance().getPatients();
		Assert.assertEquals(initialNumberOfPatients + 3, patients.size());
		
		Patient patient = PatientsHelper.getInstance().getPatient(idOfPatient2);
		patient.delete();
		
		patients = PatientsHelper.getInstance().getPatients();
		Assert.assertEquals(initialNumberOfPatients + 2, patients.size());
	}
	
	@Test
	public void testGet() throws SQLException{
		List<Patient> patients = PatientsHelper.getInstance().getPatients();
		Assert.assertEquals(initialNumberOfPatients + 3, patients.size());
		for (Iterator<Patient> iterator = patients.iterator(); iterator.hasNext();){
			Patient patient = (Patient) iterator.next(); 
			System.out.print("First Name: " + patient.getName()); 
			System.out.print("  Last Name: " + patient.getGivenName()); 
			System.out.println("  Birthday: " + patient.getBirthday()); 
		}
	}
	
	/**
	 * Add three patients and return ID of patient 2 -> Sauer
	 * @return
	 * @throws SQLException
	 */
	public long populate() throws SQLException{
		Patient patient = new Patient();
		patient.setName("Mustermann");
		patient.setGivenName("Max");
		Date dateobj = new GregorianCalendar(1992, Calendar.JANUARY, 1).getTime();
		patient.setBirthday(new java.sql.Timestamp(dateobj.getTime()));
		patient.save();
		
		patient = null;
		patient = new Patient();
		patient.setName("Sauer");
		patient.setGivenName("Sajoscha");
		dateobj = new GregorianCalendar(1978, Calendar.JULY, 30).getTime();
		patient.setBirthday(new java.sql.Timestamp(dateobj.getTime()));
		patient.save();
		idOfPatient2 = patient.getId();
		
		patient = null;
		patient = new Patient();
		patient.setName("Test");
		patient.setGivenName("Mark");
		dateobj = new GregorianCalendar(1944, Calendar.APRIL, 4).getTime();
		patient.setBirthday(new java.sql.Timestamp(dateobj.getTime()));
		patient.save();
		
		return idOfPatient2;
	}
}
