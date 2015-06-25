/**
 * 
 */
package ssd.app.model;

import java.sql.SQLException;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ssd.app.dao.DbHelper;

/**
 * @author sauer
 *
 */
public class PatientFile extends Item {
	private static final Logger LOGGER = LoggerFactory.getLogger(PatientFile.class);
	
	private Patient patient;
	private String file;
	
	public Patient getPatient() {
		return patient;
	}
	public void setPatient(Patient patient) {
		this.patient = patient;
	}
	public String getFile() {
		return file;
	}
	public void setFile(String file) {
		this.file = file;
	}
	
	public void save() throws SQLException{
		Session session = DbHelper.getInstance().openSession();
		Transaction tx = null;
		tx = session.beginTransaction();
		if(this.getId() == -1){	// INSERT new patient file
			this.setId((Long) session.save(this)); 
			tx.commit();
		}else{	// UPDATE existing patient file
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
				LOGGER.warn("Removed the patient file with the ID " + id + ".");
			}catch (HibernateException e) {
				if (tx!=null) tx.rollback();
				e.printStackTrace(); 
			}finally {
				session.close(); 
			}
		}else{
			LOGGER.warn("It was tried to delete a non existing patient file.");
		}
	}
	
//	public void save() throws SQLException{
//		try(Connection connection = DbHelper.getConnection()){
//			if(this.getId() == -1){	// INSERT new patient
//				String sql = "INSERT INTO patientfiles (patientfile, patient_id, created, modified) VALUES (?, ?, ?, ?)";
//				try(PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
//					pstmt.setString(1, file);
//					pstmt.setLong(2, patient.getId());
//					pstmt.setDate(3,java.sql.Date.valueOf(this.getCreated().toString()));
//					pstmt.setDate(4, java.sql.Date.valueOf(this.getModified().toString()));
//					
//					pstmt.execute();
//					
//					try(ResultSet rs = pstmt.getGeneratedKeys()){
//						rs.next();
//						this.setId(rs.getLong(1));
//					}
//				}
//			}else{	// UPDATE existing patient
//				String sql = "UPDATE patientfiles SET "
//						+ "patientfile = ?, patient_id = ? "
//						+ "WHERE id = ?";
//				try(PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
//					pstmt.setString(1, file);
//					pstmt.setLong(2, patient.getId());
//					pstmt.setDate(3,java.sql.Date.valueOf(this.getCreated().toString()));
//					pstmt.setDate(4, java.sql.Date.valueOf(this.getModified().toString()));
//					pstmt.setLong(5, this.getId());
//					pstmt.execute();
//				}
//			}
//		}
//	}
//	
//	public void delete() throws SQLException{
//		
//		if(this.getId() != -1){
//		
//			String sql = "DELETE FROM patientfiles WHERE id = ?";
//			try(Connection connection = DbHelper.getConnection(); PreparedStatement pstmt = connection.prepareStatement(sql)){
//				pstmt.setLong(1, this.getId());
//				pstmt.execute();
//				this.setId(-1);
//			}
//			
//		}else{
//			LOGGER.warn("It was tried to delete a non existing patient file.");
//		}
//	}
	
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append(file);
		
		return sb.toString();
	}
	
}
