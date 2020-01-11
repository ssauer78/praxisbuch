package ssd.app.view;

import java.io.File;
import java.util.Calendar;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import ssd.app.helper.ExportHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * https://examples.javacodegeeks.com/core-java/writeread-excel-files-in-java-example/
 * 
 */
@SuppressWarnings("restriction")
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
        
        CheckBox checkbox = new CheckBox("Write to Excel");
        checkbox.setTooltip(new Tooltip("Aktivieren um nach Excel zu expoertieren, ansonsten wird nach CSV exportieret"));
        
        // Create the export button and add functionality
        Button export = new Button("Exportieren");
        export.setOnAction((ActionEvent event) -> {	// if the button is triggered...
        	ExportHelper.exportForYear(checkbox.selectedProperty().get(), year.getValue(), lbShowWhere.getText()); // TODO we might want to store the directory 
        });
        
        gridPane.add(lbExport, 0, 0);	// column 1 ; row 1
        gridPane.add(year, 1, 0);	// column 2 ; row 1
        gridPane.add(checkbox, 1, 1);
        gridPane.add(lbSaveAs, 0, 2);
        gridPane.add(btnOpenDirectoryChooser, 1, 2);
        gridPane.add(export, 0, 3);
        gridPane.add(lbShowWhere, 1, 3);
        
		return gridPane;
	}
}
