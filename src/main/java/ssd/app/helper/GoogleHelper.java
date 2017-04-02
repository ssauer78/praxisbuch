package ssd.app.helper;

import java.io.IOException;
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
        DateTime now = new DateTime(System.currentTimeMillis());
        Events events = googleService.events().list("primary")
            .setMaxResults(10)
            .setTimeMin(now)
            .setOrderBy("startTime")
            .setSingleEvents(true)
            .execute();
        List<Event> items = events.getItems();
        if (items.size() == 0) {
            LOGGER.debug("No upcoming events found.");
        } else {
        	LOGGER.debug("Upcoming events");
            for (Event event : items) {
                DateTime start = event.getStart().getDateTime();
                if (start == null) {
                    start = event.getStart().getDate();
                }
                LOGGER.debug(event.getSummary() + " (" + start + ")");
            }
        }
        
	     // Refer to the Java quickstart on how to setup the environment:
	     // https://developers.google.com/google-apps/calendar/quickstart/java
	     // Change the scope to CalendarScopes.CALENDAR and delete any stored
	     // credentials.

	     Event event = new Event()
	         .setSummary("Google I/O 2015")
	         .setLocation("800 Howard St., San Francisco, CA 94103")
	         .setDescription("A chance to hear more about Google's developer products.");

	     DateTime startDateTime = new DateTime("2016-11-21T09:00:00-07:00");
	     EventDateTime start = new EventDateTime()
	         .setDateTime(startDateTime)
	         .setTimeZone("Europe/Berlin");
	     event.setStart(start);

	     DateTime endDateTime = new DateTime("2016-11-21T17:00:00-07:00");
	     EventDateTime end = new EventDateTime()
	         .setDateTime(endDateTime)
	         .setTimeZone("Europe/Berlin");
	     event.setEnd(end);

	     /*String[] recurrence = new String[] {"RRULE:FREQ=DAILY;COUNT=2"};
	     event.setRecurrence(Arrays.asList(recurrence));

	     EventAttendee[] attendees = new EventAttendee[] {
	         new EventAttendee().setEmail("lpage@example.com"),
	         new EventAttendee().setEmail("sbrin@example.com"),
	     };
	     event.setAttendees(Arrays.asList(attendees));
		
	     EventReminder[] reminderOverrides = new EventReminder[] {
	         new EventReminder().setMethod("email").setMinutes(24 * 60),
	         new EventReminder().setMethod("popup").setMinutes(10),
	     };
	     Event.Reminders reminders = new Event.Reminders()
	         .setUseDefault(false)
	         .setOverrides(Arrays.asList(reminderOverrides));
	     event.setReminders(reminders);
	     */
	     event = googleService.events().insert(CALENDAR, event).execute();
	     System.out.printf("Event created: %s\n", event.getHtmlLink());
	}
	
	/**
	 * Add an appointment to the google calendar. 
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
	    System.out.printf("Event created: %s\n", event.getHtmlLink());
	}
	
	public void removeCalendarEntry(Appointment appointment){
		
	}
}
