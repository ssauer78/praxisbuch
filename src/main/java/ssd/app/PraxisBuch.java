package ssd.app;

import java.sql.SQLException;

import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ssd.app.dao.DbHelper;
import ssd.app.helper.PatientsHelper;
import ssd.app.helper.ServiceHelper;
import ssd.app.view.ApplicationWindow;

public class PraxisBuch {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PraxisBuch.class);
	
	public static void main(String[] args){
		DbHelper.getDbHelper().registerShutDownHook();
		
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				PraxisBuch.LOGGER.debug("Run application");
				
				try {
					PatientsHelper.getInstance().initDb();
					ServiceHelper.getInstance().initDb();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				ApplicationWindow.launch(ApplicationWindow.class);
			}
		});
	}
	
	
}
