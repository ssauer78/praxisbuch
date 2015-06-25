package ssd.app.helper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import ssd.app.dao.DbHelper;
import ssd.app.model.Service;

public class ServiceHelper {

	private static final ServiceHelper INSTANCE = new ServiceHelper();
	
	public static ServiceHelper getInstance(){
		return INSTANCE;
	}
	
	private ServiceHelper(){
	}
	
	@SuppressWarnings("unchecked")
	public List<Service> getServices() throws SQLException{
		List<Service> services = new ArrayList<Service>();
		Session session = DbHelper.getInstance().openSession();
		Transaction tx = null;
		try{
			tx = session.beginTransaction();
			Query query = session.createQuery("FROM Service");
			services = (List<Service>)query.list(); 
			tx.commit();
		}catch (HibernateException e) {
			if(tx != null){
				tx.rollback();
			}
			e.printStackTrace(); 
		}finally {
			session.close(); 
		}
		return services;
	}
	
	public Service getPatient(Long serviceId){
		Service service = new Service();
		Session session = DbHelper.getInstance().openSession();
		Transaction tx = null;
		try{
			tx = session.beginTransaction();
			service = (Service) session.get(Service.class, serviceId); 
			tx.commit();
		}catch (HibernateException e) {
			if(tx != null){
				tx.rollback();
			}
			e.printStackTrace(); 
		}finally {
			session.close(); 
		}
		return service;
	}

	public void initDb() throws SQLException {
//		Service s1 = new Service();
//		s1.setName("Osteopathie");
//		s1.setCostPerUnit(80);
//		Date date = new Date();
//		s1.setCreated(date);
//		s1.setModified(date);
//		s1.save();
//		
//		s1 = new Service();
//		s1.setName("Ohrakupunktur");
//		s1.setCostPerUnit(45);
//		date = new Date();
//		s1.setCreated(date);
//		s1.setModified(date);
//		s1.save();
	}
	
}
