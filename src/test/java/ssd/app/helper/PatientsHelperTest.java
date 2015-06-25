package ssd.app.helper;

import java.sql.SQLException;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import ssd.app.helper.PatientsHelper;
import ssd.app.model.Patient;

public class PatientsHelperTest {
//	@Before
//	public void init() throws SQLException{
//		DbHelper.getInstance().init();
//		try(Connection connection = DbHelper.getConnection(); Statement stmt = connection.createStatement()){
//			stmt.execute("TRUNCATE TABLE patients");
//			stmt.execute("ALTER TABLE patients ALTER COLUMN id RESTART WITH 1");
//		}
//	}
	
//	@After
//	public void close(){
//		DbHelper.getDbHelper().close();
//	}
	
	@Test
	public void testLoad() throws SQLException{
		List<Patient> patients = PatientsHelper.getInstance().getPatients();
		
		Assert.assertNotNull(patients);
		Assert.assertTrue(patients.isEmpty());
		
		Patient patient = new Patient();
		patient.setName("Mustermann");
		patient.setGivenName("Max");
		patient.setEmail("max@mustermann.de");
		patient.save();
		
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

		patients = PatientsHelper.getInstance().getPatients();
		Assert.assertNotNull(patients);
		Assert.assertEquals(3, patients.size());
		
		patient = null;
		patient = patients.get(0);
		Assert.assertNotNull(patient);
		Assert.assertEquals(1L, patient.getId());
		Assert.assertEquals("Mustermann", patient.getName());
		Assert.assertEquals("max@mustermann.de", patient.getEmail());
		
		patient = patients.get(2);
		Assert.assertNotNull(patient);
		Assert.assertEquals(3L, patient.getId());
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
		
		patient = null;
		patient = PatientsHelper.getInstance().getPatient(1L);
		
		Assert.assertTrue(Patient.class.isInstance(patient));
		
		Assert.assertEquals(patient.getEmail(), "max@mustermann.de");
	}
}
