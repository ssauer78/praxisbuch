package ssd.app.helper;

import java.sql.SQLException;
import java.util.ArrayList;
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
	
	@SuppressWarnings("unchecked")
	public List<Appointment> getAppointments() throws SQLException{
		List<Appointment> appointments = new ArrayList<Appointment>();
		Session session = DbHelper.getInstance().openSession();
		Transaction tx = null;
		try{
			tx = session.beginTransaction();
			Query query = session.createQuery("FROM Appointment");
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
