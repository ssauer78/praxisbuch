package ssd.app.view;

import java.io.File;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import ssd.app.helper.AppointmentHelper;
import ssd.app.model.Appointment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * https://examples.javacodegeeks.com/core-java/writeread-excel-files-in-java-example/
 * 
 */
public class DisplayExport {
	private static final Logger LOGGER = LoggerFactory.getLogger(DisplayExport.class);
	
	protected static GridPane createExportPane(Stage stage) {
		GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(20, 10, 20, 20));
        gridPane.setHgap(7); 
        gridPane.setVgap(7);
        
        Label lbExport = new Label("Jahr: ");
        lbExport.setStyle(ApplicationWindow.getLabelStyle());
        GridPane.setHalignment(lbExport, HPos.RIGHT);
        
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        ObservableList<Integer> allYears = FXCollections.observableArrayList();
        for (int i = currentYear; i > 2011; i--) {
			allYears.add(i);
		}
        ChoiceBox<Integer> year = new ChoiceBox<Integer>(allYears);
        year.setValue(currentYear - 1); // select last year as default
        
        Label lbSaveAs = new Label("Speichern unter: ");
        lbExport.setStyle(ApplicationWindow.getLabelStyle());
        GridPane.setHalignment(lbExport, HPos.RIGHT);
        
        Label lbShowWhere = new Label("");
        lbExport.setStyle(ApplicationWindow.getLabelStyle());
        GridPane.setHalignment(lbExport, HPos.RIGHT);
        
        
        // Create the directory chooser button
        Button btnOpenDirectoryChooser = new Button();
        btnOpenDirectoryChooser.setText("Verzeichnis auswählen");
        btnOpenDirectoryChooser.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                DirectoryChooser directoryChooser = new DirectoryChooser();
                File selectedDirectory = directoryChooser.showDialog(stage);
                if(selectedDirectory != null){
                	// show the selected directory to the user
                	lbShowWhere.textProperty().bind(new SimpleStringProperty(selectedDirectory.getAbsolutePath()));
                }
            }
        });
        
        // Create the export button and add functionality
        Button export = new Button("Exportieren");
        export.setOnAction((ActionEvent event) -> {	// if the button is triggered...
        	LOGGER.debug(year.valueProperty().getValue().toString());
        	List<Appointment> appointments = new ArrayList<Appointment>();
    		try {
    			appointments = AppointmentHelper.getInstance().getAppointments(year.getValue()); // get all appointments 
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
        });
        
        gridPane.add(lbExport, 0, 0);	// column 1 ; row 1
        gridPane.add(year, 1, 0);	// column 2 ; row 1
        gridPane.add(lbSaveAs, 0, 1);
        gridPane.add(btnOpenDirectoryChooser, 1, 1);
        gridPane.add(export, 0, 2);
        gridPane.add(lbShowWhere, 1, 2);
        
		return gridPane;
	}
}
