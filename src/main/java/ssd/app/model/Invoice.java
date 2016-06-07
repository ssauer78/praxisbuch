package ssd.app.model;

import java.sql.SQLException;
import java.util.Date;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ssd.app.dao.DbHelper;

public class Invoice extends Item{
	private static final Logger LOGGER = LoggerFactory.getLogger(Invoice.class);
	
	private Date exported;
	private String description = "";
	private Boolean removed = false;
	
	public Date getExported() {
		return exported;
	}

	public void setExported(Date exported) {
		this.exported = exported;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean getRemoved() {
		return removed;
	}

	public void setRemoved(Boolean removed) {
		this.removed = removed;
	}

	public void save() throws SQLException{
		Session session = DbHelper.getInstance().openSession();
		Transaction tx = null;
		tx = session.beginTransaction();
		if(this.getId() == -1){	// INSERT new invoice
			this.setId((Long) session.save(this)); 
			tx.commit();
		}else{	// UPDATE existing patient
			session.update(this); 
			tx.commit();
		}
	}
	
	/**
	 * We don't really delete the invoice. We only set the deleted flag. 
	 * 
	 * @throws SQLException
	 */
	public void delete() throws SQLException{
		if(this.getId() != -1){
			this.removed = true;
			this.save();
		}else{
			LOGGER.warn("It was tried to delete a non existing invoice.");
		}
	}
	
	@Override
	public String toString() {
		return String.valueOf(this.getId());
	}
}
