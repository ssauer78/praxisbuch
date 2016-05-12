package ssd.app.helper;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.util.StringConverter;

public class ApplicationHelper {
	private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationHelper.class);
	public static final String datePattern = "dd.MM.yyyy";
	public static final String dateTimePattern = "dd.MM.yyyy HH:mm:ss";
	
	public static void showError(Exception e, String headerText, String contentText){
    	Alert alert = new Alert(AlertType.ERROR);
    	alert.setTitle("Fehler");
    	if(headerText != ""){
    		alert.setHeaderText(headerText);
    	}
    	alert.setContentText(contentText);

    	// Create expandable Exception.
    	StringWriter sw = new StringWriter();
    	PrintWriter pw = new PrintWriter(sw);
    	e.printStackTrace(pw);
    	String exceptionText = sw.toString();

    	Label label = new Label("Details:");

    	TextArea textArea = new TextArea(exceptionText);
    	textArea.setEditable(false);
    	textArea.setWrapText(true);

    	textArea.setMaxWidth(Double.MAX_VALUE);
    	textArea.setMaxHeight(Double.MAX_VALUE);
    	GridPane.setVgrow(textArea, Priority.ALWAYS);
    	GridPane.setHgrow(textArea, Priority.ALWAYS);

    	GridPane expContent = new GridPane();
    	expContent.setMaxWidth(Double.MAX_VALUE);
    	expContent.add(label, 0, 0);
    	expContent.add(textArea, 0, 1);

    	// Set expandable Exception into the dialog pane.
    	alert.getDialogPane().setExpandableContent(expContent);

    	alert.showAndWait();
    }
    
	/**
	 * Create a converter for LocalDate <> String
	 * @return StringConverter
	 */
    public static StringConverter<LocalDate> getStringConverterLocalDate(){
    	LOGGER.debug("String converter called");
    	return new StringConverter<LocalDate>() {
            DateTimeFormatter dateFormatter = 
                DateTimeFormatter.ofPattern(datePattern);
            @Override
            public String toString(LocalDate date) {
                if (date != null) {
                    return dateFormatter.format(date);
                } else {
                    return "";
                }
            }
            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    return LocalDate.parse(string, dateFormatter);
                } else {
                    return null;
                }
            }
        }; 
    }
    
    /**
	 * Create a converter for LocalDateTime <> String
	 * @return StringConverter
	 */
    public static StringConverter<LocalDateTime> getStringConverterDateTime(){
    	LOGGER.debug("String converter called");
    	return new StringConverter<LocalDateTime>() {
            DateTimeFormatter dateFormatter = 
                DateTimeFormatter.ofPattern(dateTimePattern);
            @Override
            public String toString(LocalDateTime date) {
                if (date != null) {
                    return dateFormatter.format(date);
                } else {
                    return "";
                }
            }
            @Override
            public LocalDateTime fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    return LocalDateTime.parse(string, dateFormatter);
                } else {
                    return null;
                }
            }
        }; 
    }
}
