package ssd.app.model;

import java.sql.SQLException;
import java.sql.Timestamp;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ssd.app.dao.DbHelper;

public class Appointment extends Item {
	private static final Logger LOGGER = LoggerFactory.getLogger(Appointment.class);
	
	private Patient patient;
	private Timestamp date;
	private int duration;
	private Service service;
	private String description;
	
	public Patient getPatient() {
		return patient;
	}
	public void setPatient(Patient patient) {
		this.patient = patient;
	}	
	
	public Timestamp getDate(){
		return date;
	}
	public void setDate(Timestamp date) {
		this.date = date;
	}
	public int getDuration() {
		return duration;
	}
	public void setDuration(int duration) {
		this.duration = duration;
	}
	public Service getService() {
		return service;
	}
	public void setService(Service service) {
		this.service = service;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public void save() throws SQLException{
		Session session = DbHelper.getInstance().openSession();
		Transaction tx = null;
		tx = session.beginTransaction();
		if(this.getId() == -1){	// INSERT new appointment
			this.setId((Long) session.save(this)); 
			tx.commit();
		}else{	// UPDATE existing appointment
			session.update(this); 
			tx.commit();
		}
	}
	
	public void delete() throws SQLException{
		Session session = DbHelper.getInstance().openSession();
		Transaction tx = null;
		if(this.getId() != -1){
			long id = this.getId();
			try{
				tx = session.beginTransaction();
				session.delete(this); 
				tx.commit();
				LOGGER.warn("Removed the appointment with the ID " + id + ".");
			}catch (HibernateException e) {
				if (tx!=null) tx.rollback();
				e.printStackTrace(); 
			}finally {
				session.close(); 
			}
		}else{
			LOGGER.warn("It was tried to delete a non existing appointment.");
		}
	}
	
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append(patient.toString() + ":" + date.toString());
		return sb.toString();
	}
}
