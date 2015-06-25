package ssd.app.model;

import java.sql.SQLException;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ssd.app.dao.DbHelper;

public class Expense extends Item {

	private static final Logger LOGGER = LoggerFactory.getLogger(Expense.class);
	
	private String name;
	private String description;
	private String cost;
	private String date;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getCost() {
		return cost;
	}
	public void setCost(String cost) {
		this.cost = cost;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	
	public void save() throws SQLException{
		Session session = DbHelper.getInstance().openSession();
		Transaction tx = null;
		tx = session.beginTransaction();
		if(this.getId() == -1){	// INSERT new 
			this.setId((Long) session.save(this)); 
			tx.commit();
		}else{	// UPDATE existing 
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
		sb.append(name + ":" + cost + " Euro");
		return sb.toString();
	}
}
