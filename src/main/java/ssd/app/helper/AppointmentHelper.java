package ssd.app.helper;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import ssd.app.dao.DbHelper;
import ssd.app.model.Appointment;
import ssd.app.model.Patient;

public class AppointmentHelper {
	private static final Logger LOGGER = LoggerFactory.getLogger(AppointmentHelper.class);
	private static final AppointmentHelper INSTANCE = new AppointmentHelper();
	
	public static AppointmentHelper getInstance(){
		return INSTANCE;
	}
	
	private AppointmentHelper(){
	}
	
	/**
	 * Get a filteredlist object with all data initially. 
	 * @param filter
	 * @return
	 */
	public static FilteredList<Appointment> initializAppointmentList(String filter){
		List<Appointment> appointments = new ArrayList<Appointment>();
		try {
			appointments = AppointmentHelper.getInstance().getAppointments(0);
		} catch (SQLException e1) {
			e1.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		ObservableList<Appointment> data = FXCollections.observableList(appointments);
		FilteredList<Appointment> filteredData = new FilteredList<>(data, p -> true);
		return filteredData;
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
				query = session.createQuery("FROM Appointment WHERE date BETWEEN :start AND :end ORDER BY date").setParameter("start", start).setParameter("end", end);
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
	
	/**
	 * Get all appointments of a specific day. 
	 * 
	 * From the given date a start and end Timestamp are created. The end is actually the beginning of the next
	 * day, but that should be good enough for the purpose. 
	 * 
	 * @param date	The date object which is used to get the day.
	 * @return	Found appointments
	 * @throws SQLException	
	 * @throws ParseException
	 */
	@SuppressWarnings("unchecked")
	public List<Appointment> getAppointments(Date date) throws SQLException, ParseException{
		List<Appointment> appointments = new ArrayList<Appointment>();
		Session session = DbHelper.getInstance().openSession();
		Transaction tx = null;
		
		Instant day = date.toInstant();
		ZoneId zid = ZoneId.of("Europe/Berlin");	// TODO get the zone from somewhere else...
		ZonedDateTime zdt = ZonedDateTime.ofInstant(day, zid);
		ZonedDateTime _start = zdt.toLocalDate().atStartOfDay(zid);
		ZonedDateTime _end = _start.plusDays(1);	// beginning of next day
		Timestamp tstart = new Timestamp(_start.toInstant().getEpochSecond() * 1000L);
		Timestamp tend = new Timestamp(_end.toInstant().getEpochSecond() * 1000L);
		try{
			tx = session.beginTransaction();
			Query query = null;
			query = session.createQuery("FROM Appointment WHERE date BETWEEN :start AND :end ORDER BY date")
					.setParameter("start", tstart)
					.setParameter("end", tend);
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
	
	/**
	 * Get all appointments for a specific patient
	 * @param patient	The patient object
	 * @return	Found appointments
	 * @throws SQLException	
	 * @throws ParseException
	 */
	@SuppressWarnings("unchecked")
	public static List<Appointment> getAppointments(Patient patient){
		List<Appointment> appointments = new ArrayList<Appointment>();
		Session session = DbHelper.getInstance().openSession();
		Transaction tx = null;
		try{
			tx = session.beginTransaction();
			Query query = session.createQuery("FROM Appointment WHERE patient = :patient ORDER BY date").setParameter("patient", patient);
			
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

	/**
	 * Get all appointments of today and following
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static ObservableList<Appointment> getAllFutureAppointments() {
		List<Appointment> appointments = new ArrayList<Appointment>();
		Session session = DbHelper.getInstance().openSession();
		Transaction tx = null;
		try{
			LocalDateTime localDate = LocalDateTime.now();
			Timestamp timestamp = Timestamp.valueOf(localDate);
			LOGGER.debug(timestamp.toString());
			tx = session.beginTransaction();
			Query query = null;
			query = session.createQuery("FROM Appointment WHERE date >= :date ORDER BY date DESC")
					.setParameter("date", timestamp);
			appointments = (List<Appointment>)query.list();
			LOGGER.debug("number appointments: " + appointments.size());
			tx.commit();
		}catch (HibernateException e) {
			if(tx != null){
				tx.rollback();
			}
			e.printStackTrace(); 
		}finally {
			session.close(); 
		}
		ObservableList<Appointment> _appointments = FXCollections.observableArrayList(appointments);
		return _appointments;
	}
	
	
	/**
	 * Get #count appointments from either before or after the current date 
	 * and return as an ArrayList sorted by date. 
	 * @param count	Number of appointments
	 * @param previous	Search before or after the current date/time
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static ObservableList<Appointment> getAppointments(int count, boolean previous) {
		List<Appointment> appointments = new ArrayList<Appointment>();
		Session session = DbHelper.getInstance().openSession();
		Transaction tx = null;
		try{
			LocalDateTime localDate = LocalDateTime.now();
			Timestamp timestamp = Timestamp.valueOf(localDate);
			LOGGER.debug(timestamp.toString());
			tx = session.beginTransaction();
			Query query = null;
			if(previous){
				query = session.createQuery("FROM Appointment WHERE date < :date ORDER BY date DESC")
						.setParameter("date", timestamp)
						.setMaxResults(count);
			}else{
				query = session.createQuery("FROM Appointment WHERE date > :date ORDER BY date ASC")
						.setParameter("date", timestamp)
						.setMaxResults(count);
			}
			
			appointments = (List<Appointment>)query.list();
			
			LOGGER.debug("number appointments: " + appointments.size());
			tx.commit();
		}catch (HibernateException e) {
			if(tx != null){
				tx.rollback();
			}
			e.printStackTrace(); 
		}finally {
			session.close(); 
		}
		ObservableList<Appointment> _appointments = FXCollections.observableArrayList(appointments);
		return _appointments;
	}
	
	/**
	 * Get latest appointments. 
	 * @param count	Number of appointments to return
	 * @return
	 */
	public static ObservableList<Appointment> getLatestAppointments(int count){
		return AppointmentHelper.getAppointments(count, true);
	}
	/**
	 * Get next appointments.
	 * @param count	Number of appointments to return
	 * @return
	 */
	public static ObservableList<Appointment> getNextAppointments(int count){
		return AppointmentHelper.getAppointments(count, false);
	}
	
}
