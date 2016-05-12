package ssd.app.model;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ssd.app.dao.DbHelper;



/** 
 * Patient Dynamic stores dynamic patient data. 
 * @author sauer
 *
 */
public class PatientDynamic {
	private static final Logger LOGGER = LoggerFactory.getLogger(PatientDynamic.class);
	
	private long id = -1;

	private Patient patient;
	private String fieldname;
	private String value;

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public Patient getPatient() {
		return patient;
	}
	public void setPatient(Patient patient) {
		this.patient = patient;
	}
	public String getFieldname() {
		return fieldname;
	}
	public void setFieldname(String fieldname) {
		this.fieldname = fieldname;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	public boolean save(){
		if(this.getId() == -1){	// INSERT new patient dynamic 
			return addDynamic();
		}else{	// UPDATE existing patient dynamic
			Session session = DbHelper.getInstance().openSession();
			Transaction tx = null;
			try{
				tx = session.beginTransaction();
				session.update(this); 
				tx.commit();
			}catch (HibernateException e) {
				if (tx != null){
					tx.rollback();
				}
				e.printStackTrace(); 
				return false;
			}finally {
				session.close(); 
			}
		}
		return true;
	}
	
	public boolean addDynamic(){
		LOGGER.debug("Add dynamic value " + value);
		Session session = DbHelper.getInstance().openSession();
		Transaction tx = null;
		try{
			tx = session.beginTransaction();
			session.save(this); 
			tx.commit();
		}catch (HibernateException e) {
			if (tx != null){
				tx.rollback();
			}
			// e.printStackTrace(); 
			return false;
		}finally {
			session.close(); 
		}
		return true;
	}
	
	@SuppressWarnings("unchecked")
	public static PatientDynamic getDynamic(Patient p, String fieldname){
		LOGGER.debug("Get dynamic value for " + fieldname);
		Session session = DbHelper.getInstance().openSession();
		try{
			Criteria crit = session.createCriteria(PatientDynamic.class)
					.add(Restrictions.eq("patient", p))
					.add(Restrictions.like("fieldname", fieldname));
			LOGGER.debug(crit.toString());
			List<PatientDynamic> dynamics = (List<PatientDynamic>)crit.list();
			if(dynamics.size() > 0){
				return dynamics.get(0);
			}
		}catch (HibernateException e) {
			e.printStackTrace(); 
		}finally {
			session.close(); 
		}
		
		return null;
	}
	

	@SuppressWarnings("unchecked")
	public static List<PatientDynamic> getDynamics(Patient patient){
		List<PatientDynamic> dynamics = new ArrayList<PatientDynamic>();
		
		Session session = DbHelper.getInstance().openSession();
		Transaction tx = null;
		try{
			tx = session.beginTransaction();
			Query query = session.createQuery("FROM PatientDynamic WHERE patient = ?");
			dynamics = (List<PatientDynamic>)query.setParameter(0, patient).list(); 
			tx.commit();
		}catch (HibernateException e) {
			if(tx != null){
				tx.rollback();
			}
			e.printStackTrace(); 
		}finally {
			session.close(); 
		}
		
		return dynamics;
	}
	
}
