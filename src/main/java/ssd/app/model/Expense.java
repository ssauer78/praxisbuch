package ssd.app.model;

import java.sql.SQLException;
import java.sql.Timestamp;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleFloatProperty;
import ssd.app.dao.DbHelper;

public class Expense extends Item {

	private static final Logger LOGGER = LoggerFactory.getLogger(Expense.class);
	
	private String name;
	private String description;
	private Float cost;
	private Timestamp date;
	
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
	/**
	 * For the interface only 
	 * @return
	 */
	public final FloatProperty costProperty() {
		FloatProperty value = new SimpleFloatProperty(cost);
        return value;
    }
	public Float getCost() {
		return cost;
	}
	public void setCost(Float cost) {
		this.cost = cost;
	}
	public Timestamp getDate() {
		return date;
	}
	public void setDate(Timestamp date) {
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
				LOGGER.warn("Removed the expense with the ID " + id + ". ");
			}catch (HibernateException e) {
				if (tx!=null) tx.rollback();
				e.printStackTrace(); 
			}finally {
				session.close(); 
			}
		}else{
			LOGGER.warn("It was tried to delete a non existing expense. ");
		}
	}
	
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append(name + ":" + cost + " Euro");
		return sb.toString();
	}
}
