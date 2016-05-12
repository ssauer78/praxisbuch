package ssd.app.helper;

import java.sql.SQLException;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import ssd.app.helper.PatientsHelper;
import ssd.app.model.Patient;

public class PatientsHelperTest {
	
	@Test
	public void testSavePatient() throws SQLException{
		List<Patient> patients = PatientsHelper.getInstance().getPatients();
		
		Assert.assertNotNull(patients);
		int numberOfPatients = patients.size();
		
		Patient patient = new Patient();
		patient.setName("Mustermann");
		patient.setGivenName("Max");
		patient.setEmail("max@mustermann.de");
		patient.save();
		
		long patientIdMax = patient.getId();
		
		patient = new Patient();
		patient.setName("Mustermann");
		patient.setGivenName("Maxine");
		patient.setEmail("maxine@mustermann.de");
		patient.save();
		
		patient = new Patient();
		patient.setName("Mustermann");
		patient.setGivenName("Test");
		patient.setEmail("test@mustermann.de");
		patient.save();
		
		long patientIdTest = patient.getId();

		patients = PatientsHelper.getInstance().getPatients();
		Assert.assertNotNull(patients);
		Assert.assertEquals(numberOfPatients + 3, patients.size());
		
		patient = null;
		patient = patients.get(patients.size() - 3); // get Max Mustermann
		Assert.assertNotNull(patient);
		Assert.assertEquals(patientIdMax, patient.getId());
		Assert.assertEquals("Mustermann", patient.getName());
		Assert.assertEquals("max@mustermann.de", patient.getEmail());
		
		patient = patients.get(patients.size() - 1);	// get Test Mustermann
		Assert.assertNotNull(patient);
		Assert.assertEquals(patientIdTest, patient.getId());
		Assert.assertEquals("Mustermann", patient.getName());
		Assert.assertEquals("test@mustermann.de", patient.getEmail());
	}
	
	@Test
	public void testGetPatient() throws SQLException{
		Patient patient = new Patient();
		patient.setName("Mustermann");
		patient.setGivenName("Max");
		patient.setEmail("max@mustermann.de");
		patient.save();
		long patientId = patient.getId();
		
		patient = null;
		patient = PatientsHelper.getInstance().getPatient(patientId);
		
		Assert.assertTrue(Patient.class.isInstance(patient));
		
		Assert.assertEquals(patient.getEmail(), "max@mustermann.de");
	}
}
