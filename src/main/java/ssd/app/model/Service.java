package ssd.app.model;

import java.sql.SQLException;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ssd.app.dao.DbHelper;


public class Service extends Item {
	private static final Logger LOGGER = LoggerFactory.getLogger(Service.class);
	
	private String name;
	private int costPerUnit;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * For the interface only 
	 * @return
	 */
	public final IntegerProperty costProperty() {
		IntegerProperty value = new SimpleIntegerProperty(costPerUnit);
        return value;
    }
	public int getCostPerUnit() {
		return costPerUnit;
	}

	public void setCostPerUnit(int costPerUnit) {
		this.costPerUnit = costPerUnit;
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
		sb.append(name + " (" + costPerUnit + " Euro)");
		return sb.toString();
	}
}
