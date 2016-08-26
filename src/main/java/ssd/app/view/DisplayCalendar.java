package ssd.app.view;

import jfxtras.scene.control.LocalDateTimeTextField;
import jfxtras.scene.control.agenda.Agenda;
import jfxtras.scene.control.agenda.Agenda.Appointment;
import jfxtras.scene.control.agenda.Agenda.LocalDateTimeRange;
import jfxtras.scene.control.agenda.AgendaSkinSwitcher;
import jfxtras.scene.layout.GridPane;
import ssd.app.helper.AppointmentHelper;

import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.Priority;
import javafx.util.Callback;

public class DisplayCalendar{
	private static final Logger LOGGER = LoggerFactory.getLogger(DisplayCalendar.class);
	
	protected static Agenda createAppointmentCalendarView(){
		Agenda agenda = new Agenda();
		agenda.setLocale(Locale.GERMAN);
		agenda.setAllowResize(false);
		
		final Map<String, Agenda.AppointmentGroup> lAppointmentGroupMap = new TreeMap<String, Agenda.AppointmentGroup>();
        for (Agenda.AppointmentGroup lAppointmentGroup : agenda.appointmentGroups()) {
            lAppointmentGroupMap.put(lAppointmentGroup.getDescription(), lAppointmentGroup);
        }
        
		// accept new appointments
        agenda.newAppointmentCallbackProperty().set(new Callback<Agenda.LocalDateTimeRange, Agenda.Appointment>()
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
        });
        
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
        	LocalDateTime start = appointment.getDate().toLocalDateTime();
        	LocalDateTime end = start.plusHours((long)appointment.getDuration());
        	appointments.add(new Agenda.AppointmentImplLocal()
	            .withStartLocalDateTime(start)
	            .withEndLocalDateTime(end)
	            .withSummary(appointment.getPatient().toString())
	            .withDescription(appointment.getDescription())
	            .withAppointmentGroup(lAppointmentGroupMap.get("group07"))
	        );
		}
        
        LOGGER.debug("appointments: " + appointments.size());
        
        agenda.appointments().addAll(appointments);
		
		return agenda;
	}
	

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
        int lRowIdx = 0;
 
        // skin
        {
            lGridPane.add(new Label("Skin"), new GridPane.C().row(lRowIdx).col(0).halignment(HPos.RIGHT));
            AgendaSkinSwitcher lAgendaSkinSwitcher = new AgendaSkinSwitcher(agenda);
            lGridPane.add(lAgendaSkinSwitcher, new GridPane.C().row(lRowIdx).col(1));
        }
        lRowIdx++;
 
        // displayed calendar
        {
            lGridPane.add(new Label("Display"), new GridPane.C().row(lRowIdx).col(0).halignment(HPos.RIGHT));
            LocalDateTimeTextField lLocalDateTimeTextField = new LocalDateTimeTextField();
            lGridPane.add(lLocalDateTimeTextField, new GridPane.C().row(lRowIdx).col(1));
            lLocalDateTimeTextField.localDateTimeProperty().bindBidirectional(agenda.displayedLocalDateTime());
        }
        lRowIdx++;
        // done
        return lGridPane;
    }
    
}
