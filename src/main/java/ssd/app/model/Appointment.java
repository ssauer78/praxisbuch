package ssd.app.model;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jfxtras.scene.control.agenda.Agenda.AppointmentImpl;
import ssd.app.dao.DbHelper;

public class Appointment extends AppointmentImpl {
	private static final Logger LOGGER = LoggerFactory.getLogger(Appointment.class);
	
	private Patient patient;
	private Timestamp date;
	private double duration;
	private Service service;
	private String description;
	private Invoice invoice;
	private Boolean paid = true;
	private String calUID;

	private long id = -1;
	private Date created;
	private Date modified;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public Date getCreated() {
		return created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}
	public Date getModified() {
		return modified;
	}
	public void setModified(Date modified) {
		this.modified = modified;
	}

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
	public double getDuration() {
		return duration;
	}
	public void setDuration(double duration) {
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
	public Invoice getInvoice() {
		return invoice;
	}
	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
	}
	
	public Boolean getPaid() {
		return paid;
	}
	public Boolean isPaid() {
		return paid;
	}
	public void setPaid(Boolean paid) {
		this.paid = paid;
	}
	
	public String getCalUID() {
		return calUID;
	}
	public void setCalUID(String calUID) {
		this.calUID = calUID;
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
	
	/**
	 * Really delete the appointment
	 * 
	 * @throws SQLException
	 */
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
		// sb.append(patient.toString() + ":" + date.toString());
		String s = new SimpleDateFormat("EEEE dd.MMMM yyyy HH:mm", Locale.GERMAN).format(date);
		sb.append(s);
		sb.append(" ");
		sb.append(patient.toString());
		return sb.toString();
	}
}
