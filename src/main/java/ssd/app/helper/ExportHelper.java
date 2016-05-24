package ssd.app.helper;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ssd.app.model.Appointment;

public class ExportHelper {
	private static final Logger LOGGER = LoggerFactory.getLogger(ExportHelper.class);
	/*
	 * Export the data for year year and store the export file to to. 
	 */
	public static void exportForYear(int year, String to){
		List<Appointment> appointments = new ArrayList<Appointment>();
		try {
			appointments = AppointmentHelper.getInstance().getAppointments(year); // get all appointments 
		} catch (SQLException e1) {
			// ignore => just show nothing
			LOGGER.error(e1.getMessage());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		LOGGER.debug(Integer.toString(appointments.size()));
		for (Appointment app : appointments) {
			LOGGER.debug(app.toString());
		}
	}
}
