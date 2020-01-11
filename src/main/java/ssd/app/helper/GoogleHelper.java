package ssd.app.helper;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ssd.app.helper.GoogleConnector;
import ssd.app.model.Appointment;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;

public class GoogleHelper {
	private static final Logger LOGGER = LoggerFactory.getLogger(GoogleHelper.class);
	private static final String CALENDAR = "primary";
	
	private Calendar googleService;
	
	public GoogleHelper(){
		try{
			googleService = GoogleConnector.getCalendarService();
		}catch(IOException e){
			LOGGER.error("Cannot create connection to Google API. ");
		}
	}
	
	public void checkAppointments() throws IOException{
		
        // List the next 10 events from the primary calendar.
//        DateTime now = new DateTime(System.currentTimeMillis());
//        Events events = googleService.events().list("primary")
//            .setMaxResults(10)
//            .setTimeMin(now)
//            .setOrderBy("startTime")
//            .setSingleEvents(true)
//            .execute();
//        List<Event> items = events.getItems();
//        if (items.size() == 0) {
//            LOGGER.debug("No upcoming events found.");
//        } else {
//        	LOGGER.debug("Upcoming events");
//            for (Event event : items) {
//                DateTime start = event.getStart().getDateTime();
//                if (start == null) {
//                    start = event.getStart().getDate();
//                }
//                LOGGER.debug(event.getSummary() + " (" + start + ")");
//            }
//        }
//        
	    
		List<Appointment> appointments = AppointmentHelper.getAllFutureAppointments();
		for (Appointment appointment : appointments) {
			System.out.println(appointment.toString());
			//googleService.events().list(CALENDAR).
		}
	     
	}
	
	/**
	 * Add an appointment to the google calendar. 
	 * 
	 * Refer to the Java quickstart on how to setup the environment:
	 * https://developers.google.com/google-apps/calendar/quickstart/java
	 * Change the scope to CalendarScopes.CALENDAR and delete any stored
	 * credentials.
	 * 
	 * @param appointment
	 * @throws IOException
	 */
	public void addCalendarEntry(Appointment appointment) throws IOException{
		
		Event event = new Event()
		         .setSummary("Termin: " + appointment.getPatient())
		         .setLocation("")
		         .setDescription(appointment.getDescription());
		
		DateTime startDateTime = new DateTime(appointment.getDate());
	    EventDateTime start = new EventDateTime()
	         .setDateTime(startDateTime)
	         .setTimeZone("Europe/Berlin");
	    event.setStart(start);
	    Timestamp appointmentEndDate = appointment.getDate();
	    appointmentEndDate.setTime(appointment.getDate().getTime() + (60*60*1000));
	    DateTime endDateTime = new DateTime(appointmentEndDate);
	    EventDateTime end = new EventDateTime()
	         .setDateTime(endDateTime)
	         .setTimeZone("Europe/Berlin");
	    event.setEnd(end);
	    
	    event = googleService.events().insert(CALENDAR, event).execute();
	    
	    appointment.setCalUID(event.getICalUID());
	    try {
			appointment.save();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	    
	    System.out.printf("Event created: %s\n", event.getHtmlLink());
	}
	
	public void removeCalendarEntry(Appointment appointment){
		
	}
}
