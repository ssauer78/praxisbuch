package ssd.app.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DbHelper {

	private static final DbHelper INSTANCE = new DbHelper();
	private static final Logger LOGGER = LoggerFactory.getLogger(DbHelper.class);
	
//	private BasicDataSource ds;
	private static SessionFactory sessionFactory;
	
	private DbHelper(){
		
	}
	
	public SessionFactory getSessionFactory(){
		return sessionFactory;
	}
	
	public static DbHelper getDbHelper(){
		return DbHelper.INSTANCE;
	}
	
//	public DataSource getDataSource(){
//		return ds;
//	}
//	
//	public void init(){
//		LOGGER.debug("Loading properties");
//		Properties properties = new Properties();
//		properties.put("db.path", "./target/db");
//		properties.put("db.username", "sa");
//		properties.put("db.password", "");
//		try{
//			properties.load(getClass().getResourceAsStream("/app.properties"));
//		}catch(IOException e){
//			LOGGER.error("Failed to load properties", e);
//		}
//		
//		LOGGER.debug("Creating the datasource");
//		ds = new BasicDataSource();
//		ds.setDriverClassName("org.h2.Driver");
//		ds.setUrl("jdbc:h2:" + properties.getProperty("db.path"));
//		LOGGER.debug(properties.getProperty("db.username"));
//		ds.setUsername(properties.getProperty("db.username"));
//		LOGGER.debug(properties.getProperty("db.password"));
//		ds.setPassword(properties.getProperty("db.password"));
//		
//		LOGGER.debug("Executing flyway (database migration)");
//		Flyway flyway = new Flyway();
//		flyway.setDataSource(ds);
//		flyway.migrate();
//	}
//	
//	public void close(){
//		LOGGER.debug("Closing the datasource");
//		if(ds != null){
//			try {
//				ds.close();
//			} catch (SQLException e) {
//				LOGGER.error("Failed to close the datasource", e);
//			}
//		}
//	}

//	public static Connection getConnection() throws SQLException {
//		return getInstance().getDataSource().getConnection();
//	}
//	
//	public static void getAll() throws SQLException{
//
//		LOGGER.debug("Contacts");
//		Statement statement = getConnection().createStatement();
//		try(ResultSet rs = statement.executeQuery("SELECT * FROM contacts")){
//			while(rs.next()){
//				LOGGER.debug(" >> [{}] {} ({})", new Object[]{rs.getInt("id"), rs.getString("name"), rs.getString("contacts")});
//				//System.out.printf(" >> [%d] %s (%s)%n", rs.getInt("id"), rs.getString("name"), rs.getString("contacts"));
//			}
//		}
//	}
//	
	public void registerShutDownHook(){
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			
			@Override
			public void run() {
				close();	// Close database connection
				LOGGER.debug("Done closing.");
			}
		}));
	}
	
	
	
	
    /**
     * initialises the configuration if not yet done and returns the current
     * instance
     * 
     * @return
     */
    public static SessionFactory getInstance() {
         if (sessionFactory == null){
              init();
         }
         return sessionFactory;
    }

    /**
     * Returns the ThreadLocal Session instance. Lazy initialize the
     * <code>SessionFactory</code> if needed.
     * 
     * @return Session
     * @throws HibernateException
     */
    public Session openSession() {
         return sessionFactory.getCurrentSession();
    }

    /**
     * The behaviour of this method depends on the session context you have
     * configured. This factory is intended to be used with a hibernate.cfg.xml
     * including the following property <property
     * name="current_session_context_class">thread</property> This would return
     * the current open session or if this does not exist, will create a new
     * session
     * 
     * @return
     */
    public Session getCurrentSession() {
         return sessionFactory.getCurrentSession();
    }

    /**
     * initializes the sessionfactory in a safe way even if more than one thread
     * tries to build a sessionFactory
     */
    private static synchronized void init() {
		 /*
		  * [laliluna] check again for null because sessionFactory may have been
		  * initialized between the last check and now
		  * 
		  */
    	if (sessionFactory == null) {
    		LOGGER.debug("Create new session factory");
    		try{
     			sessionFactory = new Configuration().configure().buildSessionFactory();
     		}catch (Throwable ex) { 
     			System.err.println("Failed to create sessionFactory object." + ex);
     			throw new ExceptionInInitializerError(ex); 
     		}
    	}
    }

    /**
     * Close the factory. 
     */
	public static void close(){
		LOGGER.debug("Closing the factory");
		if (sessionFactory != null){
			sessionFactory.close();
		}
		sessionFactory = null;
	}
}
