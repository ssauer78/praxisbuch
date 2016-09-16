package ssd.app.view;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Tooltip;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;
import javafx.util.converter.DefaultStringConverter;
import ssd.app.helper.ApplicationHelper;
import ssd.app.helper.AppointmentHelper;
import ssd.app.helper.ExportHelper;
import ssd.app.helper.PatientsHelper;
import ssd.app.model.Appointment;
import ssd.app.model.Patient;
import ssd.app.model.PatientDynamic;

public class DisplayPatient {
	private static final Logger LOGGER = LoggerFactory.getLogger(DisplayPatient.class);

	@SuppressWarnings("unchecked")
	protected static TableView<Patient> createPatientTableView(FilteredList<Patient> patients) {
		TableView<Patient> patientTable = new TableView<Patient>();
		patientTable.setEditable(true);
		
		TableColumn<Patient, Patient> patientAddAppointmentColumn = new TableColumn<>("");
		patientAddAppointmentColumn.setMaxWidth(30);
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
        			final ImageView buttonGraphic = new ImageView(new Image(getClass().getResourceAsStream("/icons/Calendar.png"), (double)16, (double)16, true, true));
        			final Button bAddAppointment = new Button(); 
        			
        			{	// initialize buttons: added to default constructor
        				bAddAppointment.setGraphic(buttonGraphic);
        				bAddAppointment.setTooltip(new Tooltip("Neuen Termin hinzufügen"));
        				bAddAppointment.setMinWidth(20);
        				bAddAppointment.setMaxWidth(20);
        				bAddAppointment.setMaxHeight(20);
        				bAddAppointment.setMinHeight(20);
        			}
        			
        			@Override 
        			public void updateItem(final Patient patient, boolean empty) {

        				if(empty){	// don't show the button if there is no patient
        					setGraphic( null );
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

        				super.updateItem(patient, empty);
        			}
        		};
        	}
        });
        
        TableColumn<Patient, Patient> patientEditColumn = new TableColumn<>("");
        patientEditColumn.setMaxWidth(30);
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
        			final ImageView buttonGraphicEdit = new ImageView(new Image(getClass().getResourceAsStream("/icons/Pencil.png"), (double)16, (double)16, true, true));
        			final Button bEdit = new Button();
        			
        			{	// initialize buttons: added to default constructor
        				bEdit.setGraphic(buttonGraphicEdit);
        				bEdit.setTooltip(new Tooltip("Patient bearbeiten"));
        				bEdit.setMinWidth(20);
        				bEdit.setMaxWidth(20);
        				bEdit.setMaxHeight(20);
        				bEdit.setMinHeight(20);
        			}
        			
        			@Override 
        			public void updateItem(final Patient patient, boolean empty) {
        				super.updateItem(patient, empty);

        				if(empty){	// don't show the button if there is no patient
        					setGraphic( null );
        					return;
        				}
        				
        				setGraphic(bEdit);
        				bEdit.setOnAction(new EventHandler<ActionEvent>() {

							@Override
							public void handle(ActionEvent event) {
								LOGGER.debug("Handle edit button click");
								Stage stage = (Stage)ApplicationWindow.getScene().getWindow();
								// dialog.show();
								ScrollPane sp = DisplayPatient.createEditPatientPane(patient, stage, null, true); // open the patient edit window in the main app
				    	        ApplicationWindow.getBorderPane().setCenter(sp);
							}
        					
						});
        			}
        		};
        	}
        });
        
        TableColumn<Patient, Patient> patientDeleteColumn = new TableColumn<>("");
        patientDeleteColumn.setMaxWidth(30);
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
        			final ImageView buttonGraphicDelete = new ImageView(
        					new Image(getClass().getResourceAsStream("/icons/Delete2.png"), (double)16, (double)16, true, true));
        			final Button bDelete = new Button();
        			
        			{	// initialize buttons: added to default constructor
        				bDelete.setGraphic(buttonGraphicDelete);
        				bDelete.setTooltip(new Tooltip("Patient bearbeiten"));
        				bDelete.setMinWidth(20);
        				bDelete.setMaxWidth(20);
        				bDelete.setMaxHeight(20);
        				bDelete.setMinHeight(20);
        			}
        			
        			@Override 
        			public void updateItem(final Patient patient, boolean empty) {
        				super.updateItem(patient, empty);

        				if(empty){	// don't show the button if there is no patient
        					setGraphic( null );
        					return;
        				}
        				
        				setGraphic(bDelete);
        				bDelete.setOnAction(new EventHandler<ActionEvent>() {
        					@Override 
        					public void handle(ActionEvent event) {
        						LOGGER.debug("Handle button click 'delete patient'");
        						try {
        							Alert confirm = new Alert(AlertType.CONFIRMATION);
        							confirm.setTitle("Wirklisch löschen?");
        							confirm.setHeaderText("");
        							confirm.setContentText("Soll der Patient '" + patient.getName() + "' wirklich gelöscht werden?");
        							Optional<ButtonType> result = confirm.showAndWait();
        							if (result.get() == ButtonType.OK){
    									patient.delete();
    									Stage stage = (Stage) ApplicationWindow.getScene().getWindow();
    					    	        stage.setTitle("Patienten");
    					    	        GridPane gp = DisplayPatient.createPatientTableViewWithFilter();
    					    	        ApplicationWindow.getBorderPane().setCenter(gp);
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
        
        patientTable.getColumns().addAll(patientAddAppointmentColumn, patientEditColumn, 
        		patientDeleteColumn, patientNameColumn, patientGivebNameColumn, 
        		patientInsuranceColumn, patientPhoneColumn, patientEMailColumn, dynamics);
        
        SortedList<Patient> sortedPatients = new SortedList<>(patients);
		if(sortedPatients.size() > 0){
	        sortedPatients.comparatorProperty().bind(patientTable.comparatorProperty());
	        patientTable.setItems(sortedPatients);
		}
		
		return patientTable;
	}
	
	protected static GridPane createPatientTableViewWithFilter(){
		GridPane full = new GridPane();

		FilteredList<Patient> patients = PatientsHelper.initializePatientList("");
        TextField filterField = new TextField();
        filterField.setTooltip(new Tooltip("Patientenliste filtern"));
        filterField.setPromptText("Filter");
        filterField.textProperty().addListener((observable, oldValue, newValue) -> {
        	patients.setPredicate(patient -> {
                // If filter text is empty, display all persons.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                // Compare first name and last name of every person with filter text.
                String lowerCaseFilter = newValue.toLowerCase();
                if (patient.getName().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches first name.
                } else if (patient.getGivenName().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches last name.
                } else if (patient.getInsurance().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches last name.
                } else if (patient.getEmail().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches last name.
                } else if (patient.getAddress().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches last name.
                } else if (patient.getZipcode().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches last name.
                } else if (patient.getCity() != null && patient.getCity().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches last name.
                } else if (patient.getCountry() != null && patient.getCountry().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches last name.
                }
                return false; // Does not match.
            });
        });
		TableView<Patient> tv = DisplayPatient.createPatientTableView(patients);
		
		GridPane.setHgrow(tv, Priority.ALWAYS);
		GridPane.setVgrow(tv, Priority.ALWAYS);
		full.add(filterField, 0, 0);
		full.add(tv, 0, 1);
		return full;
	}
	
	/**
	 * Show a GridPane to add a new patient! 
	 * 
	 * @return
	 */
    protected static ScrollPane createPatientPane(){
		GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(20, 10, 20, 20));
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
        StringConverter<LocalDate> converter = ApplicationHelper.getStringConverterLocalDate();           
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
        
        List<PatientDynamic> pds = new ArrayList<PatientDynamic>();
        List<TextField> dynamics = new ArrayList<TextField>();
        for(PatientDynamic patientDynamic : pds){
        	TextField dyntf = new TextField();
			dyntf.setText(patientDynamic.getValue());
			dyntf.setPromptText(patientDynamic.getFieldname());
			
			dynamics.add(dyntf);
        }
        
        Button addDynamic = new Button("Extra hinzufügen");
        addDynamic.setOnAction((ActionEvent event) -> {
        	TextInputDialog dialog2 = new TextInputDialog("Extra hinzufügen");
        	dialog2.setTitle("Extra hinzufügen");
        	dialog2.setHeaderText("Geben Sie einen Namen für das Extrafeld");
        	dialog2.setContentText("Extra Name:");
        	// Traditional way to get the response value.
        	Optional<String> result = dialog2.showAndWait();
        	if (result.isPresent()){
        	    LOGGER.debug("name: " + result.get());
        	}
        	
        	// The Java 8 way to get the response value (with lambda expression).
        	result.ifPresent(name -> {
        		TextField dyntf2 = new TextField();
    			dyntf2.setText("");
    			dyntf2.setPromptText(name);
    			dynamics.add(dyntf2);	// so that the save button is aware of it
    			int row = 11 + dynamics.size();
        		gridPane.add(new Label(name), 0, row);
        		gridPane.add(dyntf2, 1, row, 2, 1);
        	});
        });
        
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
				
				// now we can save the dynamics. Not possible before, because no patient exists
				for(TextField dyn : dynamics){
	        		PatientDynamic pdyn = PatientDynamic.getDynamic(p, dyn.getPromptText());
	        		if(pdyn == null){
	        			pdyn = new PatientDynamic();
	        			pdyn.setPatient(p);
	        			pdyn.setFieldname(dyn.getPromptText());
	        		}
	        		pdyn.setValue(dyn.getText());
	        		pdyn.save();
	        	}
				
				// patient was saved -> go back to patient list
				Stage stage = (Stage) submit.getScene().getWindow();
    	        stage.setTitle("Patienten Liste");
    	        GridPane gp = DisplayPatient.createPatientTableViewWithFilter();
				// TableView<Patient> tv = DisplayPatient.createPatientTableView(null);
				BorderPane borderPane = ApplicationWindow.getBorderPane();
    	        borderPane.setCenter(gp);
			} catch (Exception e) {
				LOGGER.debug(e.getMessage());
				ApplicationHelper.showError(e, "Speichen des Patienten fehlgeschlagen", "Der Patient konnte nicht gespeichert werden. ");
			}
        });
        
        Button addPicture = new Button("Bild hinzufügen");
        addPicture.setOnAction((ActionEvent event) -> {
        	Stage stage = (Stage) submit.getScene().getWindow();
        	FileChooser fileChooser = new FileChooser();
        	fileChooser.setTitle("Open Resource File");
        	fileChooser.showOpenDialog(stage);
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
        
        Label extras = new Label("Extra Felder");
        extras.setUnderline(true);
        gridPane.add(extras, 0, 10); gridPane.add(addDynamic, 1, 10);
        

        ScrollPane sp = new ScrollPane(gridPane);
        return sp;
	}
    
    /**
     * Open the patient edit pane in a popup window! 
     * 
     * @param patient
     * @return
     */
    protected static Stage getEditPatientDialog(Patient patient) {
    	Stage dialog = new Stage();
		dialog.initModality(Modality.APPLICATION_MODAL);	// "Lock" the parent window
		Stage stage = (Stage)ApplicationWindow.getScene().getWindow();
		dialog.initOwner(stage);
		dialog.setTitle("Patient bearbeiten");
		ScrollPane sp = createEditPatientPane(patient, stage, dialog, false);
		Scene scene = new Scene(sp, 650, 550);
        dialog.setScene(scene);
		return dialog;
    }
    
    /**
     * Create the edit dialog for a patient. 
     * 
     * @param patient	The patient object
     * @param stage		The application window
     * @param dialog	The dialog window
     * @return
     */
    protected static ScrollPane createEditPatientPane(Patient patient, Stage stage, Stage dialog, Boolean showAppointments) {
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
        StringConverter<LocalDate> converter = ApplicationHelper.getStringConverterLocalDate();
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
        
        List<PatientDynamic> pds = PatientDynamic.getDynamics(patient);
        List<TextField> dynamics = new ArrayList<TextField>();
        for(PatientDynamic patientDynamic : pds){
        	TextField dyntf = new TextField();
			dyntf.setText(patientDynamic.getValue());
			dyntf.setPromptText(patientDynamic.getFieldname());
			
			dynamics.add(dyntf);
        }
        
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
        	
        	for(TextField dyn : dynamics){
        		PatientDynamic pdyn = PatientDynamic.getDynamic(patient, dyn.getPromptText());
        		if(pdyn == null){
        			pdyn = new PatientDynamic();
        			pdyn.setPatient(patient);
        			pdyn.setFieldname(dyn.getPromptText());
        		}
        		pdyn.setValue(dyn.getText());
        		pdyn.save();
        	}
        	
    		try {
				patient.save();
				if(dialog != null){	// do we have a dialog to close?
					dialog.close();
				}
				stage.setTitle("Patienten Liste");
				// we have to load the table view again in case something was changed... otherwise the display might be wrong
				GridPane gp = DisplayPatient.createPatientTableViewWithFilter();
    	        ApplicationWindow.getBorderPane().setCenter(gp);
			} catch (Exception e) {
				LOGGER.debug(e.getMessage());
				ApplicationHelper.showError(e, "Speichen des Patienten fehlgeschlagen", "Der Patient konnte nicht gespeichert werden. ");
			}
        });
        
        Button close = new Button("Abbrechen");
        close.setOnAction((ActionEvent event) -> {
        	if(dialog != null){	// do we have a dialog to close?
        		dialog.close();
        	}
        });
        
        Button addPicture = new Button("Bild hinzufügen");
        addPicture.setOnAction((ActionEvent event) -> {
        	FileChooser fileChooser = new FileChooser();
        	fileChooser.setTitle("Open Resource File");
        	fileChooser.showOpenDialog(stage);
        });
        
        Button addDynamic = new Button("Extra hinzufügen");
        addDynamic.setOnAction((ActionEvent event) -> {
        	TextInputDialog dialog2 = new TextInputDialog("Extra hinzufügen");
        	dialog2.setTitle("Extra hinzufügen");
        	dialog2.setHeaderText("Geben Sie einen Namen für das Extrafeld");
        	dialog2.setContentText("Extra Name:");
        	// Traditional way to get the response value.
        	Optional<String> result = dialog2.showAndWait();
        	if (result.isPresent()){
        	    LOGGER.debug("name: " + result.get());
        	}
        	
        	// The Java 8 way to get the response value (with lambda expression).
        	result.ifPresent(name -> {
        		PatientDynamic _dyn = new PatientDynamic();
            	_dyn.setPatient(patient);
        		_dyn.setFieldname(name);
        		_dyn.setValue("");
        		if(_dyn.save()){
	        		pds.add(_dyn);
	        		TextField dyntf2 = new TextField();
	    			dyntf2.setText(_dyn.getValue());
	    			dyntf2.setPromptText(_dyn.getFieldname());
	    			dynamics.add(dyntf2);	// so that the save button is aware of it
	    			int row = 12 + pds.size();
	        		gridPane.add(new Label(name), 0, row);
	        		gridPane.add(dyntf2, 1, row, 2, 1);
        		}else{
        			errorLabel.setText("Der Extra-Wert konnte nicht hinzugefügt werden. ");
            		errorLabel.getStyleClass().add("validation_error");
        		}
        	});
        });
        
        List<Appointment> appointments = null;
        ListView<Appointment> appointmentsView = new ListView<Appointment>();
        if(showAppointments){ 
	        appointments = AppointmentHelper.getAppointments(patient);
	        ObservableList<Appointment> items = FXCollections.observableArrayList ();
	        for (Appointment appointment : appointments) {
	        	items.add(appointment);
			}
	        appointmentsView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
	        appointmentsView.setItems(items);
	        appointmentsView.setCellFactory(new Callback<ListView<Appointment>, ListCell<Appointment>>(){
	            @Override
	            public ListCell<Appointment> call(ListView<Appointment> p) {
	                ListCell<Appointment> cell = new ListCell<Appointment>(){
	                    @Override
	                    protected void updateItem(Appointment a, boolean bln) {
	                        super.updateItem(a, bln);
	                        if (a != null) {
	                        	DateFormat outputFormatter = new SimpleDateFormat("dd.MM.yyyy");
	                        	String output = outputFormatter.format(a.getDate());	
	                            setText(output + " - " + a.getService().getName() + " - " + a.getDuration());
	                        }
	                    }
	                };
	                return cell;
	            }
	        });
	        appointmentsView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Appointment>() {
	            @Override
	            public void changed(ObservableValue<? extends Appointment> observable, Appointment oldValue, Appointment newValue) {
	                System.out.println("ListView selection changed from oldValue = " 
	                        + oldValue + " to newValue = " + newValue);
	            }
	        });
        }
        Button printInvoice = new Button("Rechnung erstellen");
        printInvoice.setOnAction((ActionEvent event) -> {	// if the button is triggered...
        	boolean exported = ExportHelper.createInvoice(appointmentsView.getSelectionModel().getSelectedItems());
        	// TODO: add success/failure popup
        });
        
        if(showAppointments){ 
	        Label appointmentHead = new Label("Termine");
	        appointmentHead.setUnderline(true);
	        gridPane.add(appointmentHead, 3, 0);
        }
        gridPane.add(imageView, 2, 0, 1, 6);
        gridPane.add(tfFirstName, 0, 0);	gridPane.add(tfLastName, 1, 0);
        if(showAppointments){ 
        	gridPane.add(appointmentsView, 3, 1, 1, (11 + dynamics.size()));
        	gridPane.add(printInvoice, 3, 12 + dynamics.size());
        }
        gridPane.add(birthdayPicker, 0, 1, 2, 1);
        gridPane.add(tfInurance, 0, 2, 2, 1);
        gridPane.add(cbPrivate, 0, 3);		gridPane.add(cbAssistance, 1, 3, 2, 1);
        gridPane.add(tfPhone, 0, 4, 2, 1);
        gridPane.add(tfEmail, 0, 5, 2, 1);
        gridPane.add(address, 0, 6, 2, 1);
        gridPane.add(tfStreet, 0, 7, 3, 1);	
        gridPane.add(tfZip, 0, 8);			gridPane.add(tfCity, 1, 8, 2, 1);
        
        gridPane.add(submit, 0, 9); /* gridPane.add(addPicture, 0, 9);*/ gridPane.add(close, 1, 9);
        gridPane.add(errorLabel, 1, 10, 2, 1);
        
        Label extras = new Label("Extra Felder");
        extras.setUnderline(true);
        gridPane.add(extras, 0, 11); gridPane.add(addDynamic, 1, 11);
        int row = 11;
        for(TextField dyn : dynamics){
			Label dynl = new Label(dyn.getPromptText());
			gridPane.add(dynl, 0, ++row);	gridPane.add(dyn, 1, row, 2, 1);
        }
        
        ScrollPane sp = new ScrollPane();
        sp.setFitToHeight(true);
        
        sp.setContent(gridPane);
        return sp;
	}
}
