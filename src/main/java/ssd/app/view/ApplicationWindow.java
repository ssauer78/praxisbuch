package ssd.app.view;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableView;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ssd.app.dao.DbHelper;
import ssd.app.helper.PatientsHelper;
import ssd.app.model.Appointment;
import ssd.app.model.Expense;
import ssd.app.model.Patient;
import ssd.app.model.Service;

public class ApplicationWindow extends Application{

	private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationWindow.class);
	
	private static BorderPane borderPane;
	private static Scene scene;
	
	private Button serviceList;
	private Button serviceAdd;
	private Button appointmentList;
	private Button expensesList;
	private Button expensesAdd;
	private Button patientList;
	private Button patientAdd;
	private Button patientSearch;
	private Button closeApp;
	private Button showCharts;
	private Button export;
	
	private static final String LABEL_STYLE = "-fx-text-fill: black; -fx-font-size: 14;-fx-effect: dropshadow(one-pass-box, white, 5, 0, 1, 1);";
	
	public static String getLabelStyle(){
		return LABEL_STYLE;
	}
	
	protected static Scene getScene(){
		return scene;
	}
	protected static BorderPane getBorderPane(){
		return borderPane;
	}
	
    @Override
    public void start(Stage stage) throws Exception{
    	borderPane = new BorderPane();
    	GridPane gp = createStartPane();
        
    	borderPane.setCenter(gp);
    	ToolBar toolBar = createToolBar();
    	borderPane.setTop(toolBar);
    	scene = new Scene(borderPane, 900, 600);

        stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/cw_logo.png")));
        stage.setTitle("PraxisBuch - Osteopathie CW");
        scene.getStylesheets().add("/css/global.css");
        stage.setScene(scene);
        stage.show();
    }
    
    
    @Override
	public void stop() throws Exception {
    	LOGGER.debug("Stage is closing - close dbhelper");
    	DbHelper.getInstance().close();
		super.stop();
	}


	private ToolBar createToolBar(){
    	ToolBar tb = new ToolBar();
    	closeApp = new Button();
    	patientList = new Button();
    	patientAdd = new Button();
    	patientSearch  = new Button();
    	serviceList = new Button();
    	serviceAdd = new Button();
    	appointmentList = new Button();
    	expensesList = new Button();
    	expensesAdd = new Button();
    	showCharts = new Button();
    	export = new Button();

    	closeApp.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/icons/Power.png"))));
    	closeApp.setTooltip(new Tooltip("PraxisBuch beenden"));
    	closeApp.setOnAction(new EventHandler<ActionEvent>() {
    	    @Override public void handle(ActionEvent e) {
    	        LOGGER.debug("Closing application");
    	        Stage stage = (Stage) closeApp.getScene().getWindow();
    	        stage.close();
    	    }
    	});
    	 
    	//Set the icon/graphic for the ToolBar Buttons.
    	patientList.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/icons/Users.png"))));
    	patientList.setTooltip(new Tooltip("Liste aller Patienten"));
    	patientList.setOnAction(new EventHandler<ActionEvent>() {
			@Override 
			public void handle(ActionEvent e) {
    	        LOGGER.debug("Patient list");
    	        Stage stage = (Stage) patientList.getScene().getWindow();
    	        stage.setTitle("Patienten Liste");
				TableView<Patient> tv = DisplayPatient.createPatientTableView();
    	        borderPane.setCenter(tv);
    	    }
    	});
    	
    	patientAdd.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/icons/UsersAdd.png"))));
    	patientAdd.setTooltip(new Tooltip("Neuen Patienten erstellen"));
    	patientAdd.setOnAction(new EventHandler<ActionEvent>() {
    	    @Override public void handle(ActionEvent e) {
    	        LOGGER.debug("Patient add");
    	        Stage stage = (Stage) patientAdd.getScene().getWindow();
    	        stage.setTitle("Patienten anlegen");
    	        ScrollPane sp = DisplayPatient.createPatientPane();
    	        //ScrollPane sp = DisplayPatient.createEditPatientDialog(new Patient(), stage, null);
    	    	borderPane.setCenter(sp);
    	    }
    	});
    	
    	patientSearch.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/icons/Search.png"))));
    	patientSearch.setTooltip(new Tooltip("Patienten suchen"));
    	patientSearch.setOnAction(new EventHandler<ActionEvent>() {
    	    @Override public void handle(ActionEvent e) {
    	        LOGGER.debug("Patient search");
            	List<Patient> patients = new ArrayList<Patient>();
				try {
					patients = PatientsHelper.getInstance().getPatients();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
            	ChoiceDialog<Patient> choosePatient = new ChoiceDialog<Patient>(null, patients);
            	choosePatient.setTitle("Patient suchen");
            	
            	Optional<Patient> result = choosePatient.showAndWait();
            	// The Java 8 way to get the response value (with lambda expression).
            	result.ifPresent(patient -> {
            		LOGGER.debug(patient.toString());
            		// open a patient edit window (TODO: should we have it within the application or as a popup?)
            		// Stage dialog = DisplayPatient.createEditPatientDialog(patient);
					// dialog.show();
            		Stage stage = (Stage) patientAdd.getScene().getWindow();
        	        stage.setTitle("Patienten anlegen");
        	        ScrollPane sp = DisplayPatient.createEditPatientPane(patient, stage, null, true); // open the patient edit window in the main app
        	    	borderPane.setCenter(sp);
            	});
    	    }
    	});
    	
    	serviceList.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/icons/Books.png"))));
    	serviceList.setTooltip(new Tooltip("Liste aller Leistungen"));
    	serviceList.setOnAction(new EventHandler<ActionEvent>() {
			@Override 
			public void handle(ActionEvent e) {
    	        LOGGER.debug("Service list");
    	        Stage stage = (Stage) serviceList.getScene().getWindow();
    	        stage.setTitle("Leistungen");
    	        TableView<Service> tv = DisplayService.createServiceTableView();
    	        borderPane.setCenter(tv);
    	    }
    	});
    	
    	serviceAdd.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/icons/ServiceAdd.png"))));
    	serviceAdd.setTooltip(new Tooltip("Neue Leistung erstellen"));
    	serviceAdd.setOnAction(new EventHandler<ActionEvent>() {
    	    @Override public void handle(ActionEvent e) {
    	        LOGGER.debug("Service add");
    	        Stage stage = (Stage) serviceAdd.getScene().getWindow();
    	        stage.setTitle("Leistung anlegen");
    	        GridPane gp = DisplayService.createServicePane();
    	    	borderPane.setCenter(gp);
    	    }
    	});
    	
    	appointmentList.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/icons/Calendar.png"))));
    	appointmentList.setTooltip(new Tooltip("Termine anzeigen"));
    	appointmentList.setOnAction(new EventHandler<ActionEvent>() {
    	    @Override public void handle(ActionEvent e) {
    	        LOGGER.debug("Appointment list");
    	        Stage stage = (Stage) appointmentList.getScene().getWindow();
    	        stage.setTitle("Termine");
    	        TableView<Appointment> tv = DisplayAppointment.createAppointmentTableView();
    	    	borderPane.setCenter(tv);
    	    }
    	});
    	
    	expensesList.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/icons/Favourite.png"))));
    	expensesList.setTooltip(new Tooltip("Ausgaben anzeigen"));
    	expensesList.setOnAction(new EventHandler<ActionEvent>() {
    	    @Override 
    	    public void handle(ActionEvent e) {
    	        LOGGER.debug("Expenses list");
    	        Stage stage = (Stage) expensesList.getScene().getWindow();
    	        stage.setTitle("Ausgaben");
    	        TableView<Expense> tv = DisplayExpenses.createExpensesTableView();
    	    	borderPane.setCenter(tv);
    	    }
    	});
    	
    	expensesAdd.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/icons/FavouriteAdd.png"))));
    	expensesAdd.setTooltip(new Tooltip("Ausgabe hinzufügen"));
    	expensesAdd.setOnAction(new EventHandler<ActionEvent>() {
    	    @Override 
    	    public void handle(ActionEvent e) {
    	        LOGGER.debug("Expenses add");
    	        Stage stage = (Stage) expensesAdd.getScene().getWindow();
    	        stage.setTitle("Ausgabe hinzufügen");
    	    	GridPane gp = DisplayExpenses.createExpensesPane();
    	    	borderPane.setCenter(gp);
    	    }
    	});
    	
    	showCharts.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/icons/BarChart.png"))));
    	showCharts.setTooltip(new Tooltip("Verdienst anzeigen"));
    	showCharts.setOnAction(new EventHandler<ActionEvent>() {
    	    @Override 
    	    public void handle(ActionEvent e) {
    	        LOGGER.debug("show chart");
    	        Stage stage = (Stage) showCharts.getScene().getWindow();
    	        stage.setTitle("Verdienst anzeigen");
    	        LineChart<Number,Number> chart = DisplayExpenses.createIncomeChart();
    	    	borderPane.setCenter(chart);
    	    }
    	});
    	
    	export.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/icons/ExportToFile.png"))));
    	export.setTooltip(new Tooltip("Exportieren"));
    	export.setOnAction(new EventHandler<ActionEvent>() {
    	    @Override 
    	    public void handle(ActionEvent e) {
    	        LOGGER.debug("export");
    	        Stage stage = (Stage) export.getScene().getWindow();
    	        stage.setTitle("Exportieren");
    	    	GridPane gp = DisplayExport.createExportPane(stage);
    	    	borderPane.setCenter(gp);
    	    }
    	});

    	tb.getItems().addAll(closeApp, patientList, patientAdd, patientSearch, appointmentList, 
    			serviceList, serviceAdd, expensesList, expensesAdd, export, showCharts);
    	
    	return tb;
    }
	
	public GridPane createStartPane(){
		GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(20, 10, 20, 20));
        gridPane.setHgap(7); 
        gridPane.setVgap(7);
        
        Label lbStart = new Label("Start screen - has to be styled");
        GridPane.setHalignment(lbStart, HPos.CENTER);
        gridPane.add(lbStart, 0, 0); 
        return gridPane;
	}
}

