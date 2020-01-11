package ssd.app;

import java.io.IOException;

import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ssd.app.dao.DbHelper;
import ssd.app.helper.GoogleHelper;
import ssd.app.helper.WriteInvoice;
import ssd.app.view.ApplicationWindow;

public class DocBook {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DocBook.class);
	
	public static void main(String[] args){
		DbHelper.getDbHelper().registerShutDownHook();
		
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				
				/*
				// Testen
				WriteInvoice wi = new WriteInvoice(null);
				try {
					wi.writeInvoiceToPDF();
				} catch (Exception e) {
					e.printStackTrace();
				}*/
				
				
				DocBook.LOGGER.debug("Run application");
				
//				GoogleHelper gh = new GoogleHelper();
//				try {
//					gh.checkAppointments();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
				
				ApplicationWindow.launch(ApplicationWindow.class);
			}
		});
	}
	
	
}
