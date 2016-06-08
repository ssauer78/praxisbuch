package ssd.app.helper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import ssd.app.dao.DbHelper;
import ssd.app.model.Patient;

public class PatientsHelper {

	private static final PatientsHelper INSTANCE = new PatientsHelper();
	
	public static PatientsHelper getInstance(){
		return INSTANCE;
	}
	
	private PatientsHelper(){
	}
	
	@SuppressWarnings("unchecked")
	public List<Patient> getPatients() throws SQLException{
		List<Patient> patients = new ArrayList<Patient>();
		Session session = DbHelper.getInstance().openSession();
		Transaction tx = null;
		try{
			tx = session.beginTransaction();
			Query query = session.createQuery("FROM Patient WHERE removed=false");
			patients = (List<Patient>)query.list();
			tx.commit();
		}catch (HibernateException e) {
			if(tx != null){
				tx.rollback();
			}
			e.printStackTrace(); 
		}finally {
			session.close(); 
		}
		return patients;
	}
	
	/**
	 * In a way this is the same as getPatients. I could have added a parameter, 
	 * but decided to separate the two. 
	 * @return
	 * @throws SQLException
	 */
	@SuppressWarnings("unchecked")
	public List<Patient> getDeletedPatients() throws SQLException{
		List<Patient> patients = new ArrayList<Patient>();
		Session session = DbHelper.getInstance().openSession();
		Transaction tx = null;
		try{
			tx = session.beginTransaction();
			Query query = session.createQuery("FROM Patient WHERE removed=true");
			patients = (List<Patient>)query.list();
			tx.commit();
		}catch (HibernateException e) {
			if(tx != null){
				tx.rollback();
			}
			e.printStackTrace(); 
		}finally {
			session.close(); 
		}
		return patients;
	}
	
	public Patient getPatient(Long patientId){
		Patient patient = new Patient();
		Session session = DbHelper.getInstance().openSession();
		Transaction tx = null;
		try{
			tx = session.beginTransaction();
			patient = (Patient) session.get(Patient.class, patientId); 
			tx.commit();
		}catch (HibernateException e) {
			if(tx != null){
				tx.rollback();
			}
			e.printStackTrace(); 
		}finally {
			session.close(); 
		}
		return patient;
	}
	
	public Boolean validatePatient(Patient p){
		if(p.getName() == ""){
			return false;
		}
		if(p.getInsurance() == ""){
			return false;
		}
		if(p.getEmail() == ""){
			return false;
		}else{
			String regex = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(p.getEmail());
			if(!matcher.matches()){
				return false;
			}
		}
		if(p.getPhone() == ""){
			return false;
		}
		return true;
	}

	public void initDb() throws SQLException {
//		Patient patient = new Patient();
//		patient.setName("Mustermann");
//		patient.setGivenName("Max");
//		Date dateobj = new GregorianCalendar(1992, Calendar.JANUARY, 1).getTime();
//		//patient.setBirthday(dateobj);
//		patient.save();
//		//long p1 = patient.getId();
//		
//		patient = null;
//		patient = new Patient();
//		patient.setName("Sauer");
//		patient.setGivenName("Sajoscha");
//		dateobj = new GregorianCalendar(1978, Calendar.JULY, 30).getTime();
//		//patient.setBirthday(dateobj);
//		patient.save();
//		//long p2 = patient.getId();
//		
//		patient = null;
//		patient = new Patient();
//		patient.setName("Test");
//		patient.setGivenName("Mark");
//		dateobj = new GregorianCalendar(1944, Calendar.APRIL, 4).getTime();
//		// patient.setBirthday(dateobj);
//		patient.save();
	}
	
}
