package ssd.app.view;

import jfxtras.scene.control.LocalDateTimeTextField;
import jfxtras.scene.control.agenda.Agenda;
import jfxtras.scene.control.agenda.Agenda.Appointment;
import jfxtras.scene.layout.GridPane;
import ssd.app.helper.AppointmentHelper;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.collections.ListChangeListener;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Hyperlink;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.Priority;
import javafx.util.Callback;

public class DisplayCalendar{
	private static final Logger LOGGER = LoggerFactory.getLogger(DisplayCalendar.class);
	
	/**
	 * The full calendar page. Adds controls on the top and the calendar in the body 
	 * 
	 * @return
	 */
	public static GridPane getCalendarView(){
		Agenda agenda = DisplayCalendar.createAppointmentCalendarView();
        Node node = DisplayCalendar.getControlPanel(agenda);
        
        GridPane grid = new GridPane();
        grid.setVgap(2.0);
        grid.setHgap(2.0);
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.add(node, 0, 0);
        grid.add(agenda, 0, 1);
        
        return grid;
	}
	
	/**
	 * Create the calendar. It's an Agenda object with all appointments added to it. 
	 * 
	 * @return
	 */
	protected static Agenda createAppointmentCalendarView(){
		Agenda agenda = new Agenda();
		agenda.setLocale(Locale.GERMAN);
		agenda.setAllowResize(false);
		
		final Map<String, Agenda.AppointmentGroup> lAppointmentGroupMap = new TreeMap<String, Agenda.AppointmentGroup>();
        for (Agenda.AppointmentGroup lAppointmentGroup : agenda.appointmentGroups()) {
            lAppointmentGroupMap.put(lAppointmentGroup.getDescription(), lAppointmentGroup);
        }
        
		// accept new appointments
        /*agenda.newAppointmentCallbackProperty().set(new Callback<Agenda.LocalDateTimeRange, Agenda.Appointment>()
        {
            @Override
            public Agenda.Appointment call(LocalDateTimeRange dateTimeRange)
            {
                return new Agenda.AppointmentImplLocal()
	                .withStartLocalDateTime( dateTimeRange.getStartLocalDateTime() )
	                .withEndLocalDateTime( dateTimeRange.getEndLocalDateTime() )
	                .withSummary("new")
	                .withDescription("new")
	                .withAppointmentGroup(lAppointmentGroupMap.get("group01"));
            }
        });*/
        
        List<Appointment> appointments = new ArrayList<Appointment>();
        List<ssd.app.model.Appointment> storedAppointments = new ArrayList<ssd.app.model.Appointment>();
        try {
        	storedAppointments = AppointmentHelper.getInstance().getAppointments(0); // get all appointments 
		} catch (SQLException e1) {
			// ignore => just show nothing
			LOGGER.error(e1.getMessage());
		} catch (ParseException e) {
			e.printStackTrace();
		}
        for (ssd.app.model.Appointment appointment : storedAppointments) {
        	LocalDateTime start = LocalDateTime.now();
        	try{
        		start = appointment.getDate().toLocalDateTime();
        	}catch(NullPointerException e){
        		// TODO remove appointment from list
        	}
        	LocalDateTime end = start.plusHours((long)appointment.getDuration());
        	appointments.add(new Agenda.AppointmentImplLocal()
	            .withStartLocalDateTime(start)
	            .withEndLocalDateTime(end)
	            .withSummary(appointment.getPatient().toString())
	            .withDescription(appointment.getDescription())
	            .withAppointmentGroup(lAppointmentGroupMap.get("group07"))
	            
	        );
        	appointment.setStartLocalDateTime(start);
        	appointment.setEndLocalDateTime(end);
        	appointment.setSummary(appointment.getPatient().toString());
        	appointment.setAppointmentGroup(lAppointmentGroupMap.get("group07"));
        }
        
        // Add a callback on the change appointment event
        // for now we only change the starttime... might be a problem TODO
        agenda.appointmentChangedCallbackProperty().set(new Callback<Appointment, Void>() {
        	@Override
            public Void call(Appointment appointment)
            {
        		ssd.app.model.Appointment _appointment = (ssd.app.model.Appointment)appointment;
        		_appointment.setDate(Timestamp.valueOf(_appointment.getStartLocalDateTime()));
        		try {
					_appointment.save();
				} catch (SQLException e) {
					LOGGER.error("Cannot save appointment");
				}
                
                return null;
            }
		});
        
        // add a listener to the appointments observable list
        agenda.appointments().addListener(new ListChangeListener<Appointment>() {
			@Override
			public void onChanged(Change<? extends Appointment> c) {
				while (c.next()) {
                    if (c.wasPermutated()) {
                        for (int i = c.getFrom(); i < c.getTo(); ++i) {
                             //permutate
                        }
                    } else if (c.wasUpdated()) {
                             //update item
                    } else {
                        for (Appointment remitem : c.getRemoved()) {
                        	// if an item was removed from the list, we delete the appointment from the database
            				ssd.app.model.Appointment _appointment = (ssd.app.model.Appointment)remitem;
                        	try {
								_appointment.delete();
							} catch (SQLException e) {
								LOGGER.error("Cannot delete appointment");
							}
                        }
                        /*for (Appointment additem : c.getAddedSubList()) {
                            //additem.add(Outer.this);
                        }*/
                    }
                }
			}
        });
        
        agenda.appointments().addAll(storedAppointments);
		
		return agenda;
	}
	

	/**
	 * Display a node for the calendar controls. 
	 * 
	 * @param agenda	The Agenda object
	 * @return
	 */
    protected static Node getControlPanel(Agenda agenda) {
        // the result
        GridPane lGridPane = new GridPane();
        lGridPane.setVgap(2.0);
        lGridPane.setHgap(2.0);
 
        // setup the grid so all the labels will not grow, but the rest will
        ColumnConstraints lColumnConstraintsAlwaysGrow = new ColumnConstraints();
        lColumnConstraintsAlwaysGrow.setHgrow(Priority.ALWAYS);
        ColumnConstraints lColumnConstraintsNeverGrow = new ColumnConstraints();
        lColumnConstraintsNeverGrow.setHgrow(Priority.NEVER);
        lGridPane.getColumnConstraints().addAll(lColumnConstraintsNeverGrow, lColumnConstraintsAlwaysGrow);
        
        // displayed calendar
        
        Hyperlink last = new Hyperlink();
        last.setGraphic(new ImageView(new Image("/icons/ArrowLeft.png")));
        last.setOnAction(event -> {
        	LocalDateTime now = agenda.displayedLocalDateTime().get();
        	now = now.minusWeeks(1);
            agenda.setDisplayedLocalDateTime(now);
        });
    	lGridPane.add(last, new GridPane.C().row(0).col(0).halignment(HPos.RIGHT));
        
        LocalDateTimeTextField lLocalDateTimeTextField = new LocalDateTimeTextField();
        lLocalDateTimeTextField.setLocale(Locale.GERMAN);
        lGridPane.add(lLocalDateTimeTextField, new GridPane.C().row(0).col(1).margin(new Insets(5, 15, 5, 15)));
        lLocalDateTimeTextField.localDateTimeProperty().bindBidirectional(agenda.displayedLocalDateTime());
        
        Hyperlink next = new Hyperlink();
        next.setGraphic(new ImageView(new Image("/icons/ArrowRight.png")));
        next.setOnAction(event -> {
        	LocalDateTime now = agenda.displayedLocalDateTime().get();
        	now = now.plusWeeks(1);
            agenda.setDisplayedLocalDateTime(now);
        });
        lGridPane.add(next, new GridPane.C().row(0).col(2));
        // done
        return lGridPane;
    }
    
}
