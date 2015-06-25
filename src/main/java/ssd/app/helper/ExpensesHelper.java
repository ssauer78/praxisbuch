package ssd.app.helper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import ssd.app.dao.DbHelper;
import ssd.app.model.Expense;

public class ExpensesHelper {
	private static final ExpensesHelper INSTANCE = new ExpensesHelper();
	
	public static ExpensesHelper getInstance(){
		return INSTANCE;
	}
	
	private ExpensesHelper(){
	}
	
	@SuppressWarnings("unchecked")
	public List<Expense> getExpenses() throws SQLException{
		List<Expense> expenses = new ArrayList<Expense>();
		Session session = DbHelper.getInstance().openSession();
		Transaction tx = null;
		try{
			tx = session.beginTransaction();
			Query query = session.createQuery("FROM Expense");
			expenses = (List<Expense>)query.list(); 
			tx.commit();
		}catch (HibernateException e) {
			if(tx != null){
				tx.rollback();
			}
			e.printStackTrace(); 
		}finally {
			session.close(); 
		}
		return expenses;
	}
	
	public Expense getExpense(Long expenseId){
		Expense expense = new Expense();
		Session session = DbHelper.getInstance().openSession();
		Transaction tx = null;
		try{
			tx = session.beginTransaction();
			expense = (Expense) session.get(Expense.class, expenseId); 
			tx.commit();
		}catch (HibernateException e) {
			if(tx != null){
				tx.rollback();
			}
			e.printStackTrace(); 
		}finally {
			session.close(); 
		}
		return expense;
	}
}
