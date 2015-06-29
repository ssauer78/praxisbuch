package ssd.app.view;

import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;
import javafx.util.converter.DefaultStringConverter;
import ssd.app.helper.ApplicationHelper;
import ssd.app.helper.PatientsHelper;
import ssd.app.helper.ServiceHelper;
import ssd.app.model.Appointment;
import ssd.app.model.Patient;
import ssd.app.model.PatientDynamic;
import ssd.app.model.Service;

public class DisplayPatient {
	private static final Logger LOGGER = LoggerFactory.getLogger(DisplayPatient.class);

	@SuppressWarnings("unchecked")
	protected static TableView<Patient> createPatientTableView() {
		TableView<Patient> patientTable = new TableView<Patient>();
		patientTable.setEditable(true);

		TableColumn<Patient, Patient> patientAddAppointmentColumn = new TableColumn<>("");
		patientAddAppointmentColumn.setMaxWidth(50);
		patientAddAppointmentColumn.setSortable(false);
		patientAddAppointmentColumn.setCellValueFactory(new Callback<CellDataFeatures<Patient, Patient>, ObservableValue<Patient>>() {
			@Override 
			public ObservableValue<Patient> call(CellDataFeatures<Patient, Patient> features) {
				return new ReadOnlyObjectWrapper<Patient>(features.getValue());
			}
		});
        patientAddAppointmentColumn.setCellFactory(new Callback<TableColumn<Patient, Patient>, TableCell<Patient, Patient>>() {
        	@Override 
        	public TableCell<Patient, Patient> call(TableColumn<Patient, Patient> btnCol) {
        		return new TableCell<Patient, Patient>() {
        			final ImageView buttonGraphic = new ImageView(new Image(getClass().getResourceAsStream("/icons/Calendar.png")));
        			final Button bAddAppointment = new Button(); 
        			
        			{	// initialize buttons: added to default constructor
        				bAddAppointment.setGraphic(buttonGraphic);
        				bAddAppointment.setTooltip(new Tooltip("Neuen Termin hinzufügen"));
        				bAddAppointment.setMinWidth(32);
        			}
        			
        			@Override 
        			public void updateItem(final Patient patient, boolean empty) {
        				super.updateItem(patient, empty);

        				if(empty){	// don't show the button if there is no patient
        					return;
        				}
        				setGraphic(bAddAppointment);
        				bAddAppointment.setOnAction(new EventHandler<ActionEvent>() {
        					@Override 
        					public void handle(ActionEvent event) {
        						LOGGER.debug("Handle button click 'add new appointment'");
        						Stage dialog = DisplayAppointment.createAddAppointmentDialog(patient);
        						dialog.show();
        					}
        				});
        			}
        		};
        	}
        });
        
        TableColumn<Patient, Patient> patientEditColumn = new TableColumn<>("");
        patientEditColumn.setMaxWidth(50);
		patientEditColumn.setSortable(false);
		patientEditColumn.setCellValueFactory(new Callback<CellDataFeatures<Patient, Patient>, ObservableValue<Patient>>() {
			@Override 
			public ObservableValue<Patient> call(CellDataFeatures<Patient, Patient> features) {
				return new ReadOnlyObjectWrapper<Patient>(features.getValue());
			}
		});
        patientEditColumn.setCellFactory(new Callback<TableColumn<Patient, Patient>, TableCell<Patient, Patient>>() {
        	@Override 
        	public TableCell<Patient, Patient> call(TableColumn<Patient, Patient> btnCol) {
        		return new TableCell<Patient, Patient>() {
        			final ImageView buttonGraphicEdit = new ImageView(new Image(getClass().getResourceAsStream("/icons/Pencil.png")));
        			final Button bEdit = new Button();
        			
        			{	// initialize buttons: added to default constructor
        				bEdit.setGraphic(buttonGraphicEdit);
        				bEdit.setTooltip(new Tooltip("Patient bearbeiten"));
        				bEdit.setMinWidth(32);
        			}
        			
        			@Override 
        			public void updateItem(final Patient patient, boolean empty) {
        				super.updateItem(patient, empty);

        				if(empty){	// don't show the button if there is no patient
        					return;
        				}
        				
        				setGraphic(bEdit);
        				bEdit.setOnAction(new EventHandler<ActionEvent>() {

							@Override
							public void handle(ActionEvent event) {
								LOGGER.debug("Handle edit button click");
								Stage dialog = DisplayPatient.createEditPatientDialog(patient);
								dialog.show();
							}
        					
						});
        			}
        		};
        	}
        });
        
        TableColumn<Patient, Patient> patientDeleteColumn = new TableColumn<>("");
        patientDeleteColumn.setMaxWidth(50);
        patientDeleteColumn.setSortable(false);
        patientDeleteColumn.setCellValueFactory(new Callback<CellDataFeatures<Patient, Patient>, ObservableValue<Patient>>() {
			@Override 
			public ObservableValue<Patient> call(CellDataFeatures<Patient, Patient> features) {
				return new ReadOnlyObjectWrapper<Patient>(features.getValue());
			}
		});
        patientDeleteColumn.setCellFactory(new Callback<TableColumn<Patient, Patient>, TableCell<Patient, Patient>>() {
        	@Override 
        	public TableCell<Patient, Patient> call(TableColumn<Patient, Patient> btnCol) {
        		return new TableCell<Patient, Patient>() {
        			final ImageView buttonGraphicDelete = new ImageView(new Image(getClass().getResourceAsStream("/icons/Delete2.png")));
        			final Button bEdit = new Button();
        			
        			{	// initialize buttons: added to default constructor
        				bEdit.setGraphic(buttonGraphicDelete);
        				bEdit.setTooltip(new Tooltip("Patient bearbeiten"));
        				bEdit.setMinWidth(32);
        			}
        			
        			@Override 
        			public void updateItem(final Patient patient, boolean empty) {
        				super.updateItem(patient, empty);

        				if(empty){	// don't show the button if there is no patient
        					return;
        				}
        				
        				setGraphic(bEdit);
        				bEdit.setOnAction(new EventHandler<ActionEvent>() {
        					@Override 
        					public void handle(ActionEvent event) {
        						LOGGER.debug("Handle button click 'delete service'");
        						try {
        							Alert confirm = new Alert(AlertType.CONFIRMATION);
        							confirm.setTitle("Wirklisch löschen?");
        							confirm.setHeaderText("");
        							confirm.setContentText("Soll der Patient '" + patient.getName() + "' wirklich gelöscht werden?");
        							Optional<ButtonType> result = confirm.showAndWait();
        							if (result.get() == ButtonType.OK){
    									patient.delete();
    									Stage stage = (Stage) ApplicationWindow.getScene().getWindow();
    					    	        stage.setTitle("Leistungen");
    									TableView<Patient> tv = createPatientTableView();
    					    	        ApplicationWindow.getBorderPane().setCenter(tv);
        							}
								} catch (SQLException e) {
									ApplicationHelper.showError(e, "Löschen des Patienten fehlgeschlagen", 
											"Der Patient " + patient.toString() + " konnte nicht gelöscht werden. ");
								}
        					}
        				});
        			}
        		};
        	}
        });
        
        TableColumn<Patient, String> patientNameColumn = new TableColumn<Patient, String>("Nachname");
        patientNameColumn.setCellValueFactory(new PropertyValueFactory<Patient, String>("name"));
        patientNameColumn.setCellFactory(TextFieldTableCell.<Patient, String>forTableColumn(new DefaultStringConverter()));
        patientNameColumn.setOnEditCommit(new EventHandler<CellEditEvent<Patient, String>>() {
        	@Override
        	public void handle(CellEditEvent<Patient, String> t) {
        		Patient p = ((Patient) t.getTableView().getItems().get(t.getTablePosition().getRow()));
        		p.setName(t.getNewValue());
        		try {
					p.save();
				} catch (SQLException e) {
					LOGGER.error(e.getMessage());
					ApplicationHelper.showError(e, "Speichen eines Patienten fehlgeschlagen", "Der Patient " 
									+ p.toString() + " konnte nicht gespeichert werden. ");
				} 
        	}
        });

    	TableColumn<Patient, String> patientGivebNameColumn = new TableColumn<Patient, String>("Vorname");
        patientGivebNameColumn.setCellValueFactory(new PropertyValueFactory<Patient, String>("givenName"));
        
        TableColumn<Patient, String> patientInsuranceColumn = new TableColumn<Patient, String>("Versicherung");
        patientInsuranceColumn.setCellValueFactory(new PropertyValueFactory<Patient, String>("insurance"));
        
        TableColumn<Patient, String> patientPhoneColumn = new TableColumn<Patient, String>("Telefon");
        patientPhoneColumn.setCellValueFactory(new PropertyValueFactory<Patient, String>("phone"));
        
        TableColumn<Patient, String> patientEMailColumn = new TableColumn<Patient, String>("EMail");
        patientEMailColumn.setCellValueFactory(new PropertyValueFactory<Patient, String>("Email"));
        
        final TableColumn<Patient, String> dynamics = new TableColumn<Patient, String>("Extra");
        dynamics.setCellValueFactory(new Callback<CellDataFeatures<Patient, String>, ObservableValue<String>>() {
			@Override 
			public ObservableValue<String> call(CellDataFeatures<Patient, String> features) {
				Patient p = features.getValue();
				List<PatientDynamic> pds = PatientDynamic.getDynamics(p);
				String pdss = "";
				for (PatientDynamic pd : pds) {
					pdss += pd.getFieldname() + ": " + pd.getValue() + "\n";
				}
				return new ReadOnlyObjectWrapper<String>(pdss);
			}
		});
        
        patientTable.getColumns().addAll(patientAddAppointmentColumn, patientEditColumn, patientDeleteColumn, patientNameColumn, patientGivebNameColumn, 
        		patientInsuranceColumn, patientPhoneColumn, patientEMailColumn, dynamics);
        
        List<Patient> patients = new ArrayList<Patient>();
		try {
			patients = PatientsHelper.getInstance().getPatients();
		} catch (SQLException e1) {
			// ignore => just show nothing
		}
		if(patients.size() > 0){
	        ObservableList<Patient> data = FXCollections.observableList(patients);
	        patientTable.setItems(data);
		}
		
		return patientTable;
	}
	
    protected static GridPane createPatientPane(){
		GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(20, 0, 20, 20));
        gridPane.setHgap(7); 
        gridPane.setVgap(7);
         
        TextField tfFirstName = new TextField();
        tfFirstName.setPromptText("Vorname");
        TextField tfLastName = new TextField();
        tfLastName.setPromptText("Nachname *");
        TextField tfPhone = new TextField();
        tfPhone.setPromptText("Telefon *");
        TextField tfEmail = new TextField();
        tfEmail.setPromptText("E-Mail *");
        DatePicker birthdayPicker = new DatePicker();
        StringConverter<LocalDate> converter = ApplicationHelper.getStringConverter();           
        birthdayPicker.setConverter(converter);
        birthdayPicker.setPromptText("Geburtstag " + ApplicationHelper.datePattern.toLowerCase());
        TextField tfInurance = new TextField();
        tfInurance.setPromptText("Versicherung *");
        CheckBox cbPrivate = new CheckBox("Privat");
        CheckBox cbAssistance = new CheckBox("Beihilfe");
        Label address = new Label("Adresse");
        TextField tfZip = new TextField();
        tfZip.setPrefColumnCount(6);
        tfZip.setPromptText("Plz");
        TextField tfCity = new TextField();
        tfCity.setPromptText("Stadt");
        TextField tfStreet = new TextField();
        tfStreet.setPromptText("Straße");
        TextField tfNumber = new TextField();
        tfNumber.setPromptText("No.");
        Label errorLabel = new Label("");
         
        ImageView imageView = new ImageView(new Image(DisplayPatient.class.getResourceAsStream("/icons/VCardbig.png"), 0, 180, true, true));
        GridPane.setHalignment(imageView, HPos.LEFT);
        
        Button submit = new Button("Speichern");
        submit.setOnAction((ActionEvent event) -> {
        	Patient p = new Patient();
        	p.setGivenName(tfFirstName.getText());
        	
        	// Check for last name of patient. If not given, show error message and style the text box
        	if(tfLastName.getText().trim().isEmpty() || tfLastName.getText() == null){
        		errorLabel.setText("Bitte einen Namen angeben. ");
        		errorLabel.getStyleClass().add("validation_error");
        		tfLastName.getStyleClass().add("validation_error");
        		return;
        	}else{
        		p.setName(tfLastName.getText());
        	}
        	
        	// Check for last name of patient. If not given, show error message and style the text box
        	p.setPhone(tfPhone.getText());
        	p.setEmail(tfEmail.getText());
        	if(birthdayPicker.getValue() != null){
        		p.setBirthday(Timestamp.valueOf(birthdayPicker.getValue().atTime(0, 0)));
        	}
        	p.setInsurance(tfInurance.getText());
        	p.setPrivatelyInsured(cbPrivate.isSelected());
        	p.setAssistanceInsurance(cbAssistance.isSelected());
        	p.setAddress(tfStreet.getText() + " " + tfNumber.getText());
        	p.setZipcode(tfZip.getText());
        	p.setCity(tfCity.getText());
        	
    		try {
				p.save();
			} catch (Exception e) {
				LOGGER.debug(e.getMessage());
				ApplicationHelper.showError(e, "Speichen des Patienten fehlgeschlagen", "Der Patient konnte nicht gespeichert werden. ");
			}
        });
        
        gridPane.add(imageView, 2, 0, 1, 6);
        gridPane.add(tfFirstName, 0, 0);	gridPane.add(tfLastName, 1, 0);
        gridPane.add(birthdayPicker, 0, 1, 2, 1);
        gridPane.add(tfInurance, 0, 2, 2, 1);
        gridPane.add(cbPrivate, 0, 3);		gridPane.add(cbAssistance, 1, 3, 2, 1);
        gridPane.add(tfPhone, 0, 4, 2, 1);
        gridPane.add(tfEmail, 0, 5, 2, 1);
        gridPane.add(address, 0, 6, 2, 1);
        gridPane.add(tfStreet, 0, 7, 2, 1);	gridPane.add(tfNumber, 2, 7);
        gridPane.add(tfZip, 0, 8);			gridPane.add(tfCity, 1, 8, 2, 1);
        gridPane.add(submit, 0, 9);
        gridPane.add(errorLabel, 1, 9, 2, 1);
        return gridPane;
	}
    
    /**
     * Create the edit dialog for a patient. 
     * 
     * @param patient
     * @return
     */
    protected static Stage createEditPatientDialog(Patient patient) {
		Stage dialog = new Stage();
		dialog.initModality(Modality.APPLICATION_MODAL);	// "Lock" the parent window
		Stage stage = (Stage)ApplicationWindow.getScene().getWindow();
		dialog.initOwner(stage);
		dialog.setTitle("Patient bearbeiten");
		
		GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(20, 10, 20, 20));
        gridPane.setHgap(7); 
        gridPane.setVgap(7);
        Label lbPatient = new Label(patient.toString() + " bearbeiten");
        lbPatient.setStyle(ApplicationWindow.getLabelStyle());
        GridPane.setHalignment(lbPatient, HPos.LEFT);
        
        TextField tfFirstName = new TextField();
        tfFirstName.setPromptText("Vorname");
        tfFirstName.setText(patient.getGivenName());
        TextField tfLastName = new TextField();
        tfLastName.setPromptText("Nachname *");
        tfLastName.setText(patient.getName());
        TextField tfPhone = new TextField();
        tfPhone.setPromptText("Telefon *");
        tfPhone.setText(patient.getPhone());
        TextField tfEmail = new TextField();
        tfEmail.setPromptText("E-Mail *");
        tfEmail.setText(patient.getEmail());
        DatePicker birthdayPicker = new DatePicker();
        StringConverter<LocalDate> converter = ApplicationHelper.getStringConverter();
        birthdayPicker.setConverter(converter);
        birthdayPicker.setPromptText("Geburtstag " + ApplicationHelper.datePattern.toLowerCase());
        if(patient.getBirthday() != null){
	        Calendar cal = Calendar.getInstance();
	        cal.setTimeInMillis(patient.getBirthday().getTime());
	        birthdayPicker.setValue(LocalDate.of(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH)));
        }
        TextField tfInurance = new TextField();
        tfInurance.setPromptText("Versicherung *");
        tfInurance.setText(patient.getInsurance());
        CheckBox cbPrivate = new CheckBox("Privat");
        if(patient.getPrivatelyInsured()){
        	cbPrivate.setSelected(true);
        }
        CheckBox cbAssistance = new CheckBox("Beihilfe");
        if(patient.getAssistanceInsurance()){
        	cbAssistance.setSelected(true);
        }
        Label address = new Label("Adresse");
        TextField tfZip = new TextField();
        tfZip.setText(patient.getZipcode());
        tfZip.setPrefColumnCount(6);
        tfZip.setPromptText("Plz");
        TextField tfCity = new TextField();
        tfCity.setPromptText("Stadt");
        tfCity.setText(patient.getCity());
        TextField tfStreet = new TextField();
        tfStreet.setPromptText("Straße");
        tfStreet.setText(patient.getAddress());
        Label errorLabel = new Label("");
        
        ImageView imageView = new ImageView(new Image(DisplayPatient.class.getResourceAsStream("/icons/VCardbig.png"), 0, 180, true, true));
        GridPane.setHalignment(imageView, HPos.LEFT);
        
        //****************** SUBMIT BUTTON ******************************
        Button submit = new Button("Speichern");
        submit.setOnAction((ActionEvent event) -> {
        	patient.setGivenName(tfFirstName.getText());
        	
        	// Check for last name of patient. If not given, show error message and style the text box
        	if(tfLastName.getText().trim().isEmpty() || tfLastName.getText() == null){
        		errorLabel.setText("Bitte einen Namen angeben. ");
        		errorLabel.getStyleClass().add("validation_error");
        		tfLastName.getStyleClass().add("validation_error");
        		return;
        	}else{
        		patient.setName(tfLastName.getText());
        	}
        	
        	// Check for last name of patient. If not given, show error message and style the text box
        	patient.setPhone(tfPhone.getText());
        	patient.setEmail(tfEmail.getText());
        	if(birthdayPicker.getValue() != null){
        		patient.setBirthday(Timestamp.valueOf(birthdayPicker.getValue().atTime(0, 0)));
        	}
        	patient.setInsurance(tfInurance.getText());
        	patient.setPrivatelyInsured(cbPrivate.isSelected());
        	patient.setAssistanceInsurance(cbAssistance.isSelected());
        	patient.setAddress(tfStreet.getText());
        	patient.setZipcode(tfZip.getText());
        	patient.setCity(tfCity.getText());
        	
    		try {
				patient.save();
				dialog.close();
				stage.setTitle("Patienten Liste");
				TableView<Patient> tv = DisplayPatient.createPatientTableView();
    	        ApplicationWindow.getBorderPane().setCenter(tv);
			} catch (Exception e) {
				LOGGER.debug(e.getMessage());
				ApplicationHelper.showError(e, "Speichen des Patienten fehlgeschlagen", "Der Patient konnte nicht gespeichert werden. ");
			}
        });
        
        gridPane.add(imageView, 2, 0, 1, 6);
        gridPane.add(tfFirstName, 0, 0);	gridPane.add(tfLastName, 1, 0);
        gridPane.add(birthdayPicker, 0, 1, 2, 1);
        gridPane.add(tfInurance, 0, 2, 2, 1);
        gridPane.add(cbPrivate, 0, 3);		gridPane.add(cbAssistance, 1, 3, 2, 1);
        gridPane.add(tfPhone, 0, 4, 2, 1);
        gridPane.add(tfEmail, 0, 5, 2, 1);
        gridPane.add(address, 0, 6, 2, 1);
        gridPane.add(tfStreet, 0, 7, 3, 1);	
        gridPane.add(tfZip, 0, 8);			gridPane.add(tfCity, 1, 8, 2, 1);
        gridPane.add(submit, 0, 9);
        gridPane.add(errorLabel, 1, 9, 2, 1);
        Scene scene = new Scene(gridPane, 600, 500);
        dialog.setScene(scene);
		return dialog;
	}
}
