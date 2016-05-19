package ssd.app.helper;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import ssd.app.dao.DbHelper;
import ssd.app.model.Appointment;

public class AppointmentHelper {

	private static final AppointmentHelper INSTANCE = new AppointmentHelper();
	
	public static AppointmentHelper getInstance(){
		return INSTANCE;
	}
	
	private AppointmentHelper(){
	}
	
	/**
	 * Get all appointments. If the year is given and greater than 1970, 
	 * only appointments of that year will be returned. 
	 * @param year	The year to search for or 0 to get all appointments
	 * @return	Found appointmentss
	 * @throws SQLException	
	 * @throws ParseException
	 */
	@SuppressWarnings("unchecked")
	public List<Appointment> getAppointments(int year) throws SQLException, ParseException{
		List<Appointment> appointments = new ArrayList<Appointment>();
		Session session = DbHelper.getInstance().openSession();
		Transaction tx = null;
		try{
			tx = session.beginTransaction();
			Query query = null;
			if(year > 1970){	// is it a year we can work with?
				DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
				Date date = dateFormat.parse("01/01/"  + year);	// start
				long time = date.getTime();
				Timestamp start  = new Timestamp(time);
				
				date = dateFormat.parse("31/12/"  + year);
				time = date.getTime();
				Timestamp end = new Timestamp(time);
				query = session.createQuery("FROM Appointment WHERE date BETWEEN :start AND :end").setParameter("start", start).setParameter("end", end);
			}
			if(query == null){
				query = session.createQuery("FROM Appointment");
			}
			appointments = (List<Appointment>)query.list(); 
			tx.commit();
		}catch (HibernateException e) {
			if(tx != null){
				tx.rollback();
			}
			e.printStackTrace(); 
		}finally {
			session.close(); 
		}
		return appointments;
	}
	
	public Appointment getPatient(Long appointmentId){
		Appointment appointment = new Appointment();
		Session session = DbHelper.getInstance().openSession();
		Transaction tx = null;
		try{
			tx = session.beginTransaction();
			appointment = (Appointment) session.get(Appointment.class, appointmentId); 
			tx.commit();
		}catch (HibernateException e) {
			if(tx != null){
				tx.rollback();
			}
			e.printStackTrace(); 
		}finally {
			session.close(); 
		}
		return appointment;
	}

	public void initDb() throws SQLException {
		
	}
	
}
