package ssd.app;

import java.sql.SQLException;

import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ssd.app.dao.DbHelper;
import ssd.app.helper.PatientsHelper;
import ssd.app.helper.ServiceHelper;
import ssd.app.view.ApplicationWindow;

public class Main {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
	
	public static void main(String[] args){
//		DbHelper.getDbHelper().getInstance();
		DbHelper.getDbHelper().registerShutDownHook();
		
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				Main.LOGGER.debug("Run application");
				
				try {
					PatientsHelper.getInstance().initDb();
					ServiceHelper.getInstance().initDb();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
//				Application app = new Application();
//				app.setTitle("New app");
//				app.setSize(800, 600);
//				app.setLocationRelativeTo(null);
//				app.setDefaultCloseOperation(Application.EXIT_ON_CLOSE);
//				app.setVisible(true);
				ApplicationWindow.launch(ApplicationWindow.class);
//				app.addWindowListener(new WindowAdapter() {
//					
//					@Override
//					public void windowClosing(WindowEvent e){
//						DbHelper.getInstance().close();
//						Main.LOGGER.debug("Done.");
//					}
//					
//				});
			}
		});
	}
	
	
}
