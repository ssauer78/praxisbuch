package ssd.app.view;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.Reflection;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ssd.app.dao.DbHelper;
import ssd.app.helper.AppointmentHelper;
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
	private Button appointmentCalendar;
	private Button expensesList;
	private Button expensesAdd;
	private Button patientList;
	private Button patientAdd;
	private Button patientSearch;
	private Button closeApp;
	private Button showCharts;
	private Button export;
	private Button homePage;
	private Button helpPage;
	
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
    	DbHelper.getInstance();	// init the db right at the start
    	
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
    
    private Map<String, Image> getImages(){
    	Map<String, Image> allImages = new HashMap<String, Image>();
    	allImages.put("Power.png", new Image(getClass().getResourceAsStream("/icons/Power.png")));
    	allImages.put("FavouriteAdd.png", new Image(getClass().getResourceAsStream("/icons/FavouriteAdd.png")));
    	allImages.put("BarChart.png", new Image(getClass().getResourceAsStream("/icons/BarChart.png")));
    	allImages.put("Home.png", new Image(getClass().getResourceAsStream("/icons/Home.png")));
    	allImages.put("Users.png", new Image(getClass().getResourceAsStream("/icons/Users.png")));
    	allImages.put("UsersAdd.png", new Image(getClass().getResourceAsStream("/icons/UsersAdd.png")));
    	allImages.put("Search.png", new Image(getClass().getResourceAsStream("/icons/Search.png")));
    	allImages.put("Books.png", new Image(getClass().getResourceAsStream("/icons/Books.png")));
    	allImages.put("Paste.png", new Image(getClass().getResourceAsStream("/icons/Paste.png")));
    	allImages.put("ServiceAdd.png", new Image(getClass().getResourceAsStream("/icons/ServiceAdd.png")));
    	allImages.put("Calendar.png", new Image(getClass().getResourceAsStream("/icons/Calendar.png")));
    	allImages.put("Favourite.png", new Image(getClass().getResourceAsStream("/icons/Favourite.png")));
    	allImages.put("ExportToFile.png", new Image(getClass().getResourceAsStream("/icons/ExportToFile.png")));
    	allImages.put("Help.png", new Image(getClass().getResourceAsStream("/icons/Help.png")));
    	return allImages;
    }


	private ToolBar createToolBar(){
    	ToolBar tb = new ToolBar();
    	closeApp = new Button();
    	patientList = new Button();
    	patientAdd = new Button();
    	patientSearch  = new Button();
    	serviceList = new Button();
    	serviceAdd = new Button();
    	appointmentCalendar = new Button();
    	appointmentList = new Button();
    	expensesList = new Button();
    	expensesAdd = new Button();
    	showCharts = new Button();
    	export = new Button();
    	homePage = new Button();
    	helpPage = new Button();
    	
    	Map<String, Image> allImages = getImages();

    	closeApp.setGraphic(new ImageView(allImages.get("Power.png")));
    	closeApp.setTooltip(new Tooltip("PraxisBuch beenden"));
    	closeApp.setOnAction(new EventHandler<ActionEvent>() {
    	    @Override public void handle(ActionEvent e) {
    	        LOGGER.debug("Closing application");
    	        Stage stage = (Stage) closeApp.getScene().getWindow();
    	        stage.close();
    	    }
    	});
    	
    	homePage.setGraphic(new ImageView(allImages.get("Home.png")));
    	homePage.setTooltip(new Tooltip("Startseite"));
    	homePage.setOnAction(new EventHandler<ActionEvent>() {
			@Override 
			public void handle(ActionEvent e) {
    	        LOGGER.debug("Home page");
    	        Stage stage = (Stage) homePage.getScene().getWindow();
    	        stage.setTitle("Startseite");
    	        
    	    	GridPane gp = createStartPane();
    	        
    	    	borderPane.setCenter(gp);
    	    }
    	});
    	 
    	//Set the icon/graphic for the ToolBar Buttons.
    	patientList.setGraphic(new ImageView(allImages.get("Users.png")));
    	patientList.setTooltip(new Tooltip("Liste aller Patienten"));
    	patientList.setOnAction(new EventHandler<ActionEvent>() {
			@Override 
			public void handle(ActionEvent e) {
    	        LOGGER.debug("Patient list");
    	        Stage stage = (Stage) patientList.getScene().getWindow();
    	        stage.setTitle("Patienten Liste");
    	        
    	        GridPane gp = DisplayPatient.createPatientTableViewWithFilter();
    	        borderPane.setCenter(gp);
    	    }
    	});
    	
    	patientAdd.setGraphic(new ImageView(allImages.get("UsersAdd.png")));
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
    	/*
    	patientSearch.setGraphic(new ImageView(allImages.get("Search.png")));
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
    	*/
    	serviceList.setGraphic(new ImageView(allImages.get("Books.png")));
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
    	
    	serviceAdd.setGraphic(new ImageView(allImages.get("ServiceAdd.png")));
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
    	
    	appointmentCalendar.setGraphic(new ImageView(allImages.get("Calendar.png")));
    	appointmentCalendar.setTooltip(new Tooltip("Kalender anzeigen"));
    	appointmentCalendar.setOnAction(new EventHandler<ActionEvent>() {
    	    @Override public void handle(ActionEvent e) {
    	        LOGGER.debug("Appointment list");
    	        Stage stage = (Stage) appointmentCalendar.getScene().getWindow();
    	        stage.setTitle("Termine");
    	        // TableView<Appointment> tv = DisplayAppointment.createAppointmentTableView();
    	        GridPane tv = DisplayCalendar.getCalendarView();
    	    	borderPane.setCenter(tv);
    	    }
    	});
    	
    	appointmentList.setGraphic(new ImageView(allImages.get("Paste.png")));
    	appointmentList.setTooltip(new Tooltip("Termine anzeigen"));
    	appointmentList.setOnAction(new EventHandler<ActionEvent>() {
    	    @Override public void handle(ActionEvent e) {
    	        LOGGER.debug("Appointment list");
    	        Stage stage = (Stage) appointmentList.getScene().getWindow();
    	        stage.setTitle("Termine");
    	        TableView<Appointment> tv = DisplayAppointment.createAppointmentTableView();
    	        // GridPane tv = DisplayCalendar.getCalendarView();
    	    	borderPane.setCenter(tv);
    	    }
    	});
    	
    	expensesList.setGraphic(new ImageView(allImages.get("Favourite.png")));
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
    	
    	expensesAdd.setGraphic(new ImageView(allImages.get("FavouriteAdd.png")));
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
    	
    	showCharts.setGraphic(new ImageView(allImages.get("BarChart.png")));
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
    	
    	export.setGraphic(new ImageView(allImages.get("ExportToFile.png")));
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
    	
    	helpPage.setGraphic(new ImageView(allImages.get("Help.png")));
    	helpPage.setTooltip(new Tooltip("Hilfe"));
    	helpPage.setOnAction(new EventHandler<ActionEvent>() {
    	    @Override 
    	    public void handle(ActionEvent e) {
    	        LOGGER.debug("export");
    	        Stage stage = (Stage) helpPage.getScene().getWindow();
    	        stage.setTitle("Hilfe");
    	    	TabPane tp = DisplayHelp.createHelpPane(stage, allImages);
    	    	borderPane.setCenter(tp);
    	    }
    	});
    	
    	

    	tb.getItems().addAll(closeApp, homePage, patientList, patientAdd, appointmentList, appointmentCalendar,
    			serviceList, serviceAdd, expensesList, expensesAdd, export, showCharts, helpPage);
    	
    	return tb;
    }
	
	public GridPane createStartPane(){
		GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(20, 10, 20, 20));
        gridPane.setHgap(7); 
        gridPane.setVgap(7);
        
        Text header = new Text();
        header.setX(70.0f);
        header.setY(50.0f);
        header.setCache(true);
        header.setText("Praxisbuch - Heutige Termine");
        header.setFill(Color.GREEN);
        header.setFont(Font.font(null, FontWeight.BOLD, 20));
         
        /*Reflection r = new Reflection();
        r.setFraction(0.4f);
        header.setEffect(r);*/
        header.setTextAlignment(TextAlignment.CENTER);
        // TODO get appointments from 2 days before and one week after. Show them per day
        List<Appointment> appointments = new ArrayList<Appointment>();
		try {
			appointments = AppointmentHelper.getInstance().getAppointments(new Date());
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
        
        gridPane.add(header, 0, 0);
        
        if (appointments == null || appointments.size() <= 0){
        	gridPane.add(new Label("Keine Termine gefunden"), 0, 2);
        }else{
        	int row = 3;
        	for (Appointment appointment : appointments) {
        		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        		StringBuilder sb = new StringBuilder();
        		sb.append(sdf.format(appointment.getDate()));
        		sb.append(" ");
        		sb.append(appointment.getPatient().toString());
        		
				gridPane.add(new Label(sb.toString()), 0, row++);
			}
        }
        
        return gridPane;
	}
}

