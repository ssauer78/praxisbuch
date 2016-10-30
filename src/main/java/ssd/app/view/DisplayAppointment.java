package ssd.app.view;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;

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
import javafx.scene.control.TableColumn.SortType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ssd.app.helper.ApplicationHelper;
import ssd.app.helper.AppointmentHelper;
import ssd.app.helper.PatientsHelper;
import ssd.app.helper.ServiceHelper;
import ssd.app.model.Appointment;
import ssd.app.model.Patient;
import ssd.app.model.Service;

public class DisplayAppointment {
	private static final Logger LOGGER = LoggerFactory.getLogger(DisplayAppointment.class);
	
	protected static Stage createAddAppointmentDialog(Long patientId) {
		Patient patient = PatientsHelper.getInstance().getPatient(patientId);
		return createAddAppointmentDialog(patient);
	}
	
	protected static Stage createAddAppointmentDialog(Patient patient) {
		Stage dialog = new Stage();
		dialog.initModality(Modality.APPLICATION_MODAL);
		Stage stage = (Stage)ApplicationWindow.getScene().getWindow();// (Stage) closeApp.getScene().getWindow();
		dialog.initOwner(stage);
		dialog.setTitle("Neue Termin");
		
		GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(20, 10, 20, 20));
        gridPane.setHgap(7); 
        gridPane.setVgap(7);
        Label lbPatient = new Label("Neuer Termin für " + patient.toString());
        lbPatient.setStyle(ApplicationWindow.getLabelStyle());
        GridPane.setHalignment(lbPatient, HPos.LEFT);
        
        //********************** DATE ***************************
        Label lbDate = new Label("Datum: ");
        lbDate.setStyle(ApplicationWindow.getLabelStyle());
        GridPane.setHalignment(lbDate, HPos.LEFT);
        DatePicker datePicker = new DatePicker();
        StringConverter<LocalDate> converter = ApplicationHelper.getStringConverterLocalDate();           
        datePicker.setConverter(converter);
        datePicker.setPromptText(ApplicationHelper.datePattern.toLowerCase());

        //********************* TIME HOURS ************************
        final Slider timeSlider = new Slider(7, 21, 9);
        timeSlider.setMajorTickUnit(1);
        timeSlider.setMinorTickCount(0);
        timeSlider.setShowTickMarks(true);
        timeSlider.setShowTickLabels(true);
        timeSlider.setSnapToTicks(true);
        final Label timeValue = new Label("9 h");
        final Label timeCaption = new Label("Uhrzeit:");

        timeCaption.setStyle(ApplicationWindow.getLabelStyle());
        GridPane.setHalignment(timeCaption, HPos.LEFT);
        
        timeSlider.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                Number old_val, Number new_val) {
                    timeValue.setText(String.format("%.0f", new_val) + " h");
            }
        });
        timeSlider.valueChangingProperty().addListener(observable -> {
        	timeSlider.setValue(Math.round(timeSlider.getValue()));
        });
      //********************* TIME MINUTES ************************
        final Slider timeMSlider = new Slider(0, 3, 0);
        timeMSlider.setMajorTickUnit(1);
        timeMSlider.setMinorTickCount(0);
        timeMSlider.setShowTickLabels(true);
        timeMSlider.setShowTickMarks(false);
        timeMSlider.setSnapToTicks(true);
        final Label timeMValue = new Label("0 min");
        final Label timeMCaption = new Label("Minuten:");
        
        timeMCaption.setStyle(ApplicationWindow.getLabelStyle());
        GridPane.setHalignment(timeMCaption, HPos.LEFT);
        timeMSlider.setLabelFormatter(new StringConverter<Double>() {
            @Override
            public String toString(Double n) {
                if (n == 1) return "15";
                if (n == 2) return "30";
                if (n == 3) return "45";
                return "0";
            }
            @Override
            public Double fromString(String s) {
                switch (s) {
                    case "0":
                        return 0d;
                    case "15":
                        return 1d;
                    case "30":
                        return 2d;
                    case "45":
                        return 3d;
                    default:
                        return 0d;
                }
            }
        });
        timeMSlider.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                Number old_val, Number new_val) {
                    timeMValue.setText(String.format("%d", (new_val.intValue() *  15)) + " min");
            	}
        });
        timeMSlider.valueChangingProperty().addListener(observable -> {
        	timeMSlider.setValue(Math.round(timeMSlider.getValue()));
        });
        
        //********************* SERVICE *****************************
        Label lbService = new Label("Leistung: ");
        lbService.setStyle(ApplicationWindow.getLabelStyle());
        GridPane.setHalignment(lbService, HPos.LEFT);
        ArrayList<Service> services = new ArrayList<Service>();
        try {
			services = (ArrayList<Service>)ServiceHelper.getInstance().getServices();
		} catch (SQLException e) {
			// ignore, just don't show any
			LOGGER.error(e.getMessage());
		}
        ObservableList<Service> serviceList = FXCollections.observableArrayList(services);
        final ComboBox<Service> comboBox = new ComboBox<Service>(serviceList);
        comboBox.getSelectionModel().selectFirst(); //select the first element
        comboBox.setCellFactory(new Callback<ListView<Service>,ListCell<Service>>(){
        	@Override
        	public ListCell<Service> call(ListView<Service> p) {
        		final ListCell<Service> cell = new ListCell<Service>(){
        			@Override
        			protected void updateItem(Service t, boolean bln) {
        				super.updateItem(t, bln);
        				if(t != null){
        					setText(t.toString());
        				}else{
        					setText(null);
        				}
        			}
        		};
        		return cell;
        	}
        });
        
        //****************** DESCRIPTION ******************************
        final TextArea description = new TextArea();
        description.setPromptText("Beschreibung");
        
        //****************** DURATION ******************************
        Label lbDuration = new Label("Dauer: ");
        lbDuration.setStyle(ApplicationWindow.getLabelStyle());
        GridPane.setHalignment(lbService, HPos.LEFT);
        TextField appointmentDuration = new TextField ("1");
        appointmentDuration.setPrefColumnCount(3);
        appointmentDuration.textProperty().addListener(new ChangeListener<String>(){
        	@Override
        	public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
    			try{
    				if(!newValue.isEmpty()){
	    				// test if what the user gives is actually a double
	    				Double.parseDouble(appointmentDuration.getText());
    				}
    			}catch(NumberFormatException e){
    				appointmentDuration.setText(oldValue);
    			}
            }
        });
        
        //****************** SUBMIT BUTTON ******************************
        Button submit = new Button("Termin erstellen");
        submit.setOnAction((ActionEvent event) -> {
        	LOGGER.debug("Create appointment");
        	Appointment appointment = new Appointment();
        	appointment.setPatient(patient);
        	LocalDate localDate = datePicker.getValue();
        	
        	int hours = ((Double)timeSlider.getValue()).intValue();
        	int minutes = ((Double)timeMSlider.getValue()).intValue() * 15;
        	appointment.setDate(Timestamp.valueOf(localDate.atTime(hours, minutes)));
        	appointment.setDescription(description.getText());
        	appointment.setService(comboBox.getValue());
        	appointment.setDuration(Double.parseDouble(appointmentDuration.getText()));
        	appointment.setCreated(new Date());
        	appointment.setModified(new Date());
        	try {
				appointment.save();
				dialog.close();
			} catch (Exception e) {
				ApplicationHelper.showError(e, "Speichen eines Termins fehlgeschlagen", "Der Termin konnte nicht gespeichert werden. ");
			}
        });
        
        gridPane.add(lbPatient, 0, 0, 3, 1);	// column 1 ; row 1
        gridPane.add(lbDate, 0, 1);	// column 1 ; row 2
        gridPane.add(datePicker, 1, 1, 2, 1);	// column 2 ; row 2
        gridPane.add(timeCaption, 0, 2);
        gridPane.add(timeSlider, 1, 2);
        gridPane.add(timeValue, 2, 2);
        gridPane.add(timeMCaption, 0, 3);
        gridPane.add(timeMSlider, 1, 3);
        gridPane.add(timeMValue, 2, 3);
        gridPane.add(lbService, 0, 4);
        gridPane.add(comboBox, 1, 4, 2, 1);
        gridPane.add(description, 0, 5, 3, 1);
        gridPane.add(lbDuration, 0, 6);
        gridPane.add(appointmentDuration, 1, 6, 2, 1);
        gridPane.add(submit, 1, 7);
        
        Scene scene = new Scene(gridPane, 300, 500);
        dialog.setScene(scene);
		return dialog;
	}
	
	@SuppressWarnings("unchecked")
	protected static TableView<Appointment> createAppointmentTableView(){
		TableView<Appointment> appointmentTable = new TableView<Appointment>();
        appointmentTable.setEditable(true);
        
        TableColumn<Appointment, Appointment> appointmentAC = new TableColumn<Appointment, Appointment>("");
        appointmentAC.setMaxWidth(30);
        appointmentAC.setSortable(false);
        appointmentAC.setCellValueFactory(new Callback<CellDataFeatures<Appointment, Appointment>, ObservableValue<Appointment>>() {
			@Override 
			public ObservableValue<Appointment> call(CellDataFeatures<Appointment, Appointment> features) {
				return new ReadOnlyObjectWrapper<Appointment>(features.getValue());
			}
		});
        appointmentAC.setCellFactory(new Callback<TableColumn<Appointment, Appointment>, TableCell<Appointment, Appointment>>() {
        	@Override 
        	public TableCell<Appointment, Appointment> call(TableColumn<Appointment, Appointment> btnCol) {
        		return new TableCell<Appointment, Appointment>() {
        			final ImageView buttonGraphic = new ImageView(new Image(getClass().getResourceAsStream("/icons/Pencil.png"), (double)16, (double)16, true, true));
        			final Button button = new Button(); 
        			{
        				button.setGraphic(buttonGraphic);
        				button.setTooltip(new Tooltip("Neuen Termin hinzufügen"));
        				button.setMinWidth(20);
        				button.setMaxWidth(20);
        				button.setMaxHeight(20);
        				button.setMinHeight(20);
        			}
        			@Override 
        			public void updateItem(final Appointment appointment, boolean empty) {
        				super.updateItem(appointment, empty);

        				if(empty){	// don't show the button if there is no patient
        					return;
        				}
        				setGraphic(button);
        				button.setOnAction(new EventHandler<ActionEvent>() {
        					@Override 
        					public void handle(ActionEvent event) {
        						LOGGER.debug("Handle button click 'edit appointment'");
								Stage dialog = DisplayAppointment.createEditAppointmentDialog(appointment);
								dialog.show();
        					}
        				});
        			}
        		};
        	}
        });
        
        TableColumn<Appointment, Appointment> appointmentDelete = new TableColumn<Appointment, Appointment>("");
        appointmentDelete.setMaxWidth(30);
        appointmentDelete.setSortable(false);
        appointmentDelete.setCellValueFactory(new Callback<CellDataFeatures<Appointment, Appointment>, ObservableValue<Appointment>>() {
			@Override 
			public ObservableValue<Appointment> call(CellDataFeatures<Appointment, Appointment> features) {
				return new ReadOnlyObjectWrapper<Appointment>(features.getValue());
			}
		});
        appointmentDelete.setCellFactory(new Callback<TableColumn<Appointment, Appointment>, TableCell<Appointment, Appointment>>() {
        	@Override 
        	public TableCell<Appointment, Appointment> call(TableColumn<Appointment, Appointment> btnCol) {
        		return new TableCell<Appointment, Appointment>() {
        			final ImageView buttonGraphic = new ImageView(
        					new Image(getClass().getResourceAsStream("/icons/Delete2.png"), (double)16, (double)16, true, true));
        			final Button button = new Button(); 
        			{
        				button.setGraphic(buttonGraphic);
        				button.setTooltip(new Tooltip("Neuen Termin hinzufügen"));
        				button.setMinWidth(20);
        				button.setMaxWidth(20);
        				button.setMaxHeight(20);
        				button.setMinHeight(20);
        			}
        			@Override 
        			public void updateItem(final Appointment appointment, boolean empty) {
        				super.updateItem(appointment, empty);

        				if(empty){	// don't show the button if there is no patient
        					return;
        				}
        				setGraphic(button);
        				button.setOnAction(new EventHandler<ActionEvent>() {
        					@Override 
        					public void handle(ActionEvent event) {
        						LOGGER.debug("Handle button click 'delete appointment'");
        						try {
        							Alert confirm = new Alert(AlertType.CONFIRMATION);
        							confirm.setTitle("Wirklisch löschen?");
        							confirm.setHeaderText("");
        							confirm.setContentText("Soll der Termin wirklich gelöscht werden?");
        							Optional<ButtonType> result = confirm.showAndWait();
        							if (result.get() == ButtonType.OK){
        								appointment.delete();
    									Stage stage = (Stage) ApplicationWindow.getScene().getWindow();
    					    	        stage.setTitle("Termine");
    									TableView<Appointment> tv = createAppointmentTableView();
    					    	        ApplicationWindow.getBorderPane().setCenter(tv);
        							}
								} catch (SQLException e) {
									ApplicationHelper.showError(e, "Löschen des Termins fehlgeschlagen", 
											"Der Termin konnte nicht gelöscht werden. ");
								}
        					}
        				});
        			}
        		};
        	}
        });
        
        final TableColumn<Appointment, Patient> appointmentPatientColumn = new TableColumn<Appointment, Patient>("Patient");
        
        appointmentPatientColumn.setCellValueFactory(new PropertyValueFactory<Appointment, Patient>("Patient"));
        appointmentPatientColumn.setEditable(false);
        
        DateTimeFormatter myDateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        final TableColumn<Appointment, String> appointmentDateColumn = new TableColumn<Appointment, String>("Datum");
        appointmentDateColumn.setComparator(new Comparator<String>(){

            @Override 
            public int compare(String t, String t1) {
            	try{
            		SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm");
            		Date d1 = format.parse(t);                
            		Date d2 = format.parse(t1);
            		return Long.compare(d1.getTime(), d2.getTime());
            	}catch(ParseException p){
            		p.printStackTrace();
            	}
            	return -1;
                 
            }
            
       });
        appointmentDateColumn.setCellValueFactory(new PropertyValueFactory<Appointment, String>("Date"));
        appointmentDateColumn.setCellValueFactory(
        	cellData -> new ReadOnlyObjectWrapper<String>(myDateFormatter.format(cellData.getValue().getDate().toLocalDateTime()))
        );
        appointmentDateColumn.setEditable(false);
        
        final TableColumn<Appointment, Double> appointmentDurationColumn = new TableColumn<Appointment, Double>("Dauer");
        appointmentDurationColumn.setCellValueFactory(new PropertyValueFactory<Appointment, Double>("Duration"));
        appointmentDurationColumn.setEditable(false);
        
        final TableColumn<Appointment, String> appointmentDescriptionColumn = new TableColumn<Appointment, String>("Info");
        appointmentDescriptionColumn.setCellValueFactory(new PropertyValueFactory<Appointment, String>("Description"));
        appointmentDescriptionColumn.setEditable(false);
        
        final TableColumn<Appointment, Service> appointmentServiceColumn = new TableColumn<Appointment, Service>("Leistung");
        appointmentServiceColumn.setCellValueFactory(new PropertyValueFactory<Appointment, Service>("Service"));
        appointmentServiceColumn.setCellValueFactory(
        	cellData -> new ReadOnlyObjectWrapper<Service>(cellData.getValue().getService())
        );
        appointmentServiceColumn.setEditable(false);
        
        final TableColumn<Appointment, Appointment> appointmentPaidColumn = new TableColumn<Appointment, Appointment>("Bezahlt");
        appointmentPaidColumn.setCellValueFactory(new Callback<CellDataFeatures<Appointment, Appointment>, ObservableValue<Appointment>>() {
			@Override 
			public ObservableValue<Appointment> call(CellDataFeatures<Appointment, Appointment> features) {
				return new ReadOnlyObjectWrapper<Appointment>(features.getValue());
			}
		});
        appointmentPaidColumn.setCellFactory(new Callback<TableColumn<Appointment, Appointment>, TableCell<Appointment, Appointment>>() {
        	@Override 
        	public TableCell<Appointment, Appointment> call(TableColumn<Appointment, Appointment> btnCol) {
        		return new TableCell<Appointment, Appointment>() {
        			@Override 
        			public void updateItem(final Appointment appointment, boolean empty) {
        				super.updateItem(appointment, empty);
        				if(empty || appointment == null){	// don't show the button if there is no appointment
        					return;
        				}
        				if(appointment.isPaid()){
        					setStyle("-fx-background-color: green");
        				}else{
        					setStyle("-fx-background-color: red");
        				}
        			}
        		};
        	}
        });
        appointmentPaidColumn.setEditable(false);
        
        appointmentTable.getColumns().addAll(appointmentAC, appointmentDelete, appointmentPatientColumn, appointmentDateColumn, 
        		appointmentDurationColumn, appointmentServiceColumn, appointmentPaidColumn, appointmentDescriptionColumn);
        
        List<Appointment> appointments = new ArrayList<Appointment>();
		try {
			appointments = AppointmentHelper.getInstance().getAppointments(0); // get all appointments 
		} catch (SQLException e1) {
			// ignore => just show nothing
			LOGGER.error(e1.getMessage());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(appointments.size() > 0){
	        ObservableList<Appointment> data = FXCollections.observableList(appointments);
	        appointmentTable.setItems(data);
		}
		
		appointmentTable.getSortOrder().add(appointmentDateColumn);
		appointmentDateColumn.setSortType(SortType.DESCENDING);
        return appointmentTable;
	}



	protected static Stage createEditAppointmentDialog(Appointment appointment) {
		Stage dialog = new Stage();
		dialog.initModality(Modality.APPLICATION_MODAL);
		Stage stage = (Stage)ApplicationWindow.getScene().getWindow();
		dialog.initOwner(stage);
		dialog.setTitle("Termin bearbeiten");
		
		Patient patient = appointment.getPatient();
		
		GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(20, 10, 20, 20));
        gridPane.setHgap(7); 
        gridPane.setVgap(7);
        Label lbPatient = new Label("Termin für " + patient.toString() + " bearbeiten");
        lbPatient.setStyle(ApplicationWindow.getLabelStyle());
        GridPane.setHalignment(lbPatient, HPos.LEFT);
        
        //********************** DATE ***************************
        Label lbDate = new Label("Datum: ");
        lbDate.setStyle(ApplicationWindow.getLabelStyle());
        GridPane.setHalignment(lbDate, HPos.LEFT);
        DatePicker datePicker = new DatePicker();
        StringConverter<LocalDate> converter = ApplicationHelper.getStringConverterLocalDate();           
        datePicker.setConverter(converter);
        datePicker.setPromptText(ApplicationHelper.datePattern.toLowerCase());
        
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(appointment.getDate().getTime());
        datePicker.setValue(LocalDate.of(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH)));
        
        //********************* TIME HOURS ************************
        final Slider timeSlider = new Slider(7, 21, 9);
        timeSlider.setMajorTickUnit(1);
        timeSlider.setMinorTickCount(0);
        timeSlider.setShowTickMarks(true);
        timeSlider.setShowTickLabels(true);
        timeSlider.setSnapToTicks(true);
        final Label timeValue = new Label("9 h");
        final Label timeCaption = new Label("Uhrzeit:");

        timeCaption.setStyle(ApplicationWindow.getLabelStyle());
        GridPane.setHalignment(timeCaption, HPos.LEFT);
        
        timeSlider.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                Number old_val, Number new_val) {
                    timeValue.setText(String.format("%.0f", new_val) + " h");
            }
        });
        timeSlider.valueChangingProperty().addListener(
    		observable -> {
    			timeSlider.setValue(Math.round(timeSlider.getValue()));
    		}
    	);
        timeSlider.adjustValue(cal.get(Calendar.HOUR_OF_DAY));
        
        //********************* TIME MINUTES ************************
        final Slider timeMSlider = new Slider(0, 3, 0);
        timeMSlider.setMajorTickUnit(1);
        timeMSlider.setMinorTickCount(0);
        timeMSlider.setShowTickLabels(true);
        timeMSlider.setShowTickMarks(false);
        timeMSlider.setSnapToTicks(true);
        final Label timeMValue = new Label("0 min");
        final Label timeMCaption = new Label("Minuten:");
        
        timeMCaption.setStyle(ApplicationWindow.getLabelStyle());
        GridPane.setHalignment(timeMCaption, HPos.LEFT);
        timeMSlider.setLabelFormatter(new StringConverter<Double>() {
            @Override
            public String toString(Double n) {
                if (n == 1) return "15";
                if (n == 2) return "30";
                if (n == 3) return "45";
                return "0";
            }
            @Override
            public Double fromString(String s) {
                switch (s) {
                    case "0":
                        return 0d;
                    case "15":
                        return 1d;
                    case "30":
                        return 2d;
                    case "45":
                        return 3d;
                    default:
                        return 0d;
                }
            }
        });
        timeMSlider.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                Number old_val, Number new_val) {
                    timeMValue.setText(String.format("%d", (new_val.intValue() *  15)) + " min");
            }
        });
        timeMSlider.valueChangingProperty().addListener(observable -> {
        	timeMSlider.setValue(Math.round(timeMSlider.getValue()));
        });
        timeMSlider.adjustValue(Math.round(cal.get(Calendar.MINUTE) / 15));
        
        //********************* SERVICE *****************************
        Label lbService = new Label("Leistung: ");
        lbService.setStyle(ApplicationWindow.getLabelStyle());
        GridPane.setHalignment(lbService, HPos.LEFT);
        ArrayList<Service> services = new ArrayList<Service>();
        try {
			services = (ArrayList<Service>)ServiceHelper.getInstance().getServices();
		} catch (SQLException e) {
			// ignore, just don't show any
			LOGGER.error(e.getMessage());
		}
        ObservableList<Service> serviceList = FXCollections.observableArrayList(services);
        final ComboBox<Service> comboBox = new ComboBox<Service>(serviceList);
        comboBox.getSelectionModel().select(appointment.getService());
        comboBox.setCellFactory(new Callback<ListView<Service>,ListCell<Service>>(){
        	@Override
        	public ListCell<Service> call(ListView<Service> p) {
        		final ListCell<Service> cell = new ListCell<Service>(){
        			@Override
        			protected void updateItem(Service t, boolean bln) {
        				super.updateItem(t, bln);
        				if(t != null){
        					setText(t.toString());
        				}else{
        					setText(null);
        				}
        			}
        		};
        		return cell;
        	}
        });
        
        //****************** DESCRIPTION ******************************
        final TextArea description = new TextArea();
        description.setPromptText("Beschreibung");
        description.appendText(appointment.getDescription());
        
        //****************** DURATION ******************************
        Label lbDuration = new Label("Dauer: ");
        lbDuration.setStyle(ApplicationWindow.getLabelStyle());
        GridPane.setHalignment(lbService, HPos.LEFT);
        TextField appointmentDuration = new TextField();
        appointmentDuration.setPrefColumnCount(3);
        appointmentDuration.appendText(Double.toString(appointment.getDuration()));
        appointmentDuration.textProperty().addListener(new ChangeListener<String>(){
        	@Override
        	public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
    			try{
    				if(!newValue.isEmpty()){
	    				// test if what the user gives is actually a double
	    				Double.parseDouble(appointmentDuration.getText());
    				}
    			}catch(NumberFormatException e){
    				appointmentDuration.setText(oldValue);
    			}
            }
        });
        
        //****************** PAID ***************************************
        CheckBox cbPaid = new CheckBox("Bezahlt");
        if(appointment.isPaid()){
        	cbPaid.setSelected(true);
        }
        
        //****************** SUBMIT BUTTON ******************************
        Button submit = new Button("Termin editieren");
        submit.setOnAction((ActionEvent event) -> {
        	LOGGER.debug("Edit appointment");
        	LocalDate localDate = datePicker.getValue();
        	int hours = ((Double)timeSlider.getValue()).intValue();
        	int minutes = ((Double)timeMSlider.getValue()).intValue() * 15;
        	appointment.setDate(Timestamp.valueOf(localDate.atTime(hours, minutes)));
        	appointment.setDescription(description.getText());
        	appointment.setService(comboBox.getValue());
        	appointment.setDuration(Double.parseDouble(appointmentDuration.getText()));
        	appointment.setPaid(cbPaid.isSelected());
        	appointment.setCreated(new Date());
        	appointment.setModified(new Date());
        	try {
				appointment.save();
				dialog.close();
				stage.setTitle("Termine");
				TableView<Appointment> tv = DisplayAppointment.createAppointmentTableView();
    	        ApplicationWindow.getBorderPane().setCenter(tv);
			} catch (Exception e) {
				ApplicationHelper.showError(e, "Speichen eines Termins fehlgeschlagen", "Der Termin konnte nicht gespeichert werden. ");
			}
        });
        
        gridPane.add(lbPatient, 0, 0, 3, 1);	// column 1 ; row 1
        gridPane.add(lbDate, 0, 1);	// column 1 ; row 2
        gridPane.add(datePicker, 1, 1, 2, 1);	// column 2 ; row 2
        gridPane.add(timeCaption, 0, 2);
        gridPane.add(timeSlider, 1, 2);
        gridPane.add(timeValue, 2, 2);
        gridPane.add(timeMCaption, 0, 3);
        gridPane.add(timeMSlider, 1, 3);
        gridPane.add(timeMValue, 2, 3);
        gridPane.add(lbService, 0, 4);
        gridPane.add(comboBox, 1, 4, 2, 1);
        gridPane.add(description, 0, 5, 3, 1);
        gridPane.add(lbDuration, 0, 6);
        gridPane.add(appointmentDuration, 1, 6, 2, 1);
        gridPane.add(cbPaid, 0, 7, 2, 1);
        gridPane.add(submit, 1, 8);
        
        Scene scene = new Scene(gridPane, 300, 500);
        dialog.setScene(scene);
		return dialog;
	}
}
