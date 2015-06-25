package ssd.app.helper;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import ssd.app.dao.DbHelper;
import ssd.app.model.PatientFile;

public class PatientFilesHelper {

	private static final PatientFilesHelper INSTANCE = new PatientFilesHelper();
	
	public static PatientFilesHelper getInstance(){
		return INSTANCE;
	}
	
	private PatientFilesHelper(){
	}
	
	@SuppressWarnings("unchecked")
	public List<PatientFile> getPatientFiles(){
		List<PatientFile> files = new ArrayList<>();
		Session session = DbHelper.getInstance().openSession();
		Transaction tx = null;
		try{
			tx = session.beginTransaction();
			Query query = session.createQuery("FROM PatientFile");
			files = (List<PatientFile>)query.list(); 
			tx.commit();
		}catch (HibernateException e) {
			if(tx != null){
				tx.rollback();
			}
			e.printStackTrace(); 
		}finally {
			session.close(); 
		}
		return files;
		
		
//		List<PatientFile> files = new ArrayList<>();
//		String sql = "SELECT * FROM patientfiles ORDER BY patient_id";
//		try(Connection connection = DbHelper.getConnection(); 
//				PreparedStatement pstmt = connection.prepareStatement(sql);
//				ResultSet rs = pstmt.executeQuery()){
//			while(rs.next()){
//				PatientFile file = new PatientFile();
//				file.setId(rs.getLong("id"));
//				file.setFile(rs.getString("patientfile"));
//				Patient patient = new Patient();
//				file.setPatient(patient);
//				
//				files.add(file);
//			}
//		}
//		return files;
	}
}
