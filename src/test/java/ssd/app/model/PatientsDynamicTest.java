package ssd.app.model;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.Session;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ssd.app.dao.DbHelper;
import ssd.app.helper.PatientsHelper;
import ssd.app.model.Patient;

public class PatientsDynamicTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(PatientsDynamicTest.class);
	
	
	@Before
	public void init() throws SQLException{
		
	}
	
	@Test
	public void testAdd() throws SQLException{
		LOGGER.debug("Run test to save dynamic values");
		List<Patient> patients = PatientsHelper.getInstance().getPatients();
		
		Assert.assertNotNull(patients);
		
		Patient patient = new Patient();
		patient.setName("Mustermann");
		patient.setGivenName("Max");
		patient.setEmail("max@mustermann.de");
		patient.save();
		
		PatientDynamic pd = new PatientDynamic();
		pd.setPatient(patient);
		pd.setFieldname("Mobil2");
		String phone = "123 4567890";
		pd.setValue(phone);
		
		pd.addDynamic();
		
		List<PatientDynamic> pdlist = PatientDynamic.getDynamics(patient);
		Assert.assertEquals(1, pdlist.size());
		
		Assert.assertEquals(phone, PatientDynamic.getDynamic(patient, "Mobil2").getValue());
	}
	
	@Test
	public void testGet() throws SQLException{
		LOGGER.debug("Run test to get dynamic values");
		Patient patient = new Patient();
		patient.setName("Mustermann");
		patient.setGivenName("Max");
		patient.setEmail("max@mustermann.de");
		patient.save();
		long pid = patient.getId();

		PatientDynamic pd = new PatientDynamic();
		pd.setPatient(patient);
		pd.setFieldname("Mobil2");
		String phone = "123 4567890";
		pd.setValue(phone);
		pd.addDynamic();
		
		patient = null;
		patient = PatientsHelper.getInstance().getPatient(pid);
		
		PatientDynamic pdyn = PatientDynamic.getDynamic(patient, "Mobil2");
		
		Assert.assertEquals(phone, pdyn.getValue());
		
		pd = null;
		pd = new PatientDynamic();
		pd.setPatient(patient);
		pd.setFieldname("Mobil2");
		pd.setValue("new mobil");
		boolean success = pd.addDynamic();	// this should fail 
		Assert.assertFalse(success);
	}
	
	@Test
	public void testGetDynamics() throws SQLException{
		LOGGER.debug("Run test to get dynamic values");
		Patient patient = new Patient();
		patient.setName("Mustermann");
		patient.setGivenName("Max");
		patient.setEmail("max@mustermann.de");
		patient.save();
		
		List<PatientDynamic> dynamics = PatientDynamic.getDynamics(patient);
		Assert.assertEquals(dynamics.size(), 0);

		PatientDynamic pd = new PatientDynamic();
		pd.setPatient(patient);
		pd.setFieldname("Mobil2");
		String phone = "123 4567890";
		pd.setValue(phone);
		pd.addDynamic();
		
		pd = new PatientDynamic();
		pd.setPatient(patient);
		pd.setFieldname("Email 3");
		pd.setValue("test@test.de");
		pd.addDynamic();
		
		dynamics = PatientDynamic.getDynamics(patient);
		Assert.assertEquals(dynamics.size(), 2);
	}
	
	
}
