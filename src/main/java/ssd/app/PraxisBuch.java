package ssd.app;

import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ssd.app.dao.DbHelper;
import ssd.app.view.ApplicationWindow;

public class PraxisBuch {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PraxisBuch.class);
	
	public static void main(String[] args){
		DbHelper.getDbHelper().registerShutDownHook();
		
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				PraxisBuch.LOGGER.debug("Run application");
				
				ApplicationWindow.launch(ApplicationWindow.class);
			}
		});
	}
	
	
}
