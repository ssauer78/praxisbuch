package ssd.app.model;

import java.sql.SQLException;
import java.sql.Timestamp;

import javafx.scene.image.Image;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ssd.app.dao.DbHelper;

public class Patient extends Item{
	private static final Logger LOGGER = LoggerFactory.getLogger(Patient.class);
	
	private String name;
	private String givenName = "";
	private String insurance = "";
	private Boolean privatelyInsured = false;
	private Boolean assistanceInsurance = false;
	private Timestamp birthday;
	//private Address address;
	private String phone = "";
	private String email = "";
	private String address = "";
	private String zipcode = "";
	private String city = "";
	private String country = "Germany";
	private Image photo;
	
	public Image getPhoto() {
		return photo;
	}
	public void setPhoto(Image photo) {
		this.photo = photo;
	}
	public Boolean getPrivatelyInsured() {
		return privatelyInsured;
	}
	public void setPrivatelyInsured(Boolean privatelyInsured) {
		this.privatelyInsured = privatelyInsured;
	}
	public Boolean getAssistanceInsurance() {
		return assistanceInsurance;
	}
	public void setAssistanceInsurance(Boolean assistanceInsurance) {
		this.assistanceInsurance = assistanceInsurance;
	}
	public String getZipcode() {
		return zipcode;
	}
	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getGivenName() {
		return givenName;
	}
	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}
	public String getInsurance() {
		return insurance;
	}
	public void setInsurance(String insurance) {
		this.insurance = insurance;
	}
	public Timestamp getBirthday() {
		return birthday;
	}
	public void setBirthday(Timestamp birthday) {
		this.birthday = birthday;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	
	public void save() throws SQLException{
		Session session = DbHelper.getInstance().openSession();
		Transaction tx = null;
		tx = session.beginTransaction();
		if(this.getId() == -1){	// INSERT new patient
			this.setId((Long) session.save(this)); 
			tx.commit();
		}else{	// UPDATE existing patient
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
				LOGGER.warn("Removed the patient with the ID " + id + ".");
			}catch (HibernateException e) {
				if (tx!=null) tx.rollback();
				e.printStackTrace(); 
			}finally {
				session.close(); 
			}
		}else{
			LOGGER.warn("It was tried to delete a non existing patient.");
		}
	}
	
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		if(this.getId() == -1){
			sb.append("[No ID] ");
		}else{
			sb.append("[").append(this.getId()).append("] ");
		}
		
		if(name == null){
			sb.append("no name");
		}else{
			sb.append(givenName + " " + name);
		}
		
		return sb.toString();
	}
}
