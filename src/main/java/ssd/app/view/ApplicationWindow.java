package ssd.app.view;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
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
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ssd.app.dao.DbHelper;
import ssd.app.helper.AppointmentHelper;
import ssd.app.model.Appointment;
import ssd.app.model.Expense;
import ssd.app.model.Service;

@SuppressWarnings("restriction")
public class ApplicationWindow extends Application{

	private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationWindow.class);
	
	private static BorderPane borderPane;
	private static Scene scene;
	
	private Button serviceList = new Button();
	private Button serviceAdd = new Button();
	private Button appointmentList = new Button();
	private Button appointmentCalendar = new Button();
	private Button expensesList = new Button();
	private Button expensesAdd = new Button();
	private Button patientList = new Button();
	private Button patientAdd = new Button();
	// private Button patientSearch = new Button();
	private Button closeApp = new Button();
	private Button showCharts = new Button();
	private Button export = new Button();
	private Button homePage = new Button();
	private Button cleanUp = new Button();
	private Button helpPage = new Button();
	
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
    	allImages.put("RecycleBin.png", new Image(getClass().getResourceAsStream("/icons/RecycleBin.png")));
    	allImages.put("Help.png", new Image(getClass().getResourceAsStream("/icons/Help.png")));
    	return allImages;
    }


	private ToolBar createToolBar(){
    	ToolBar tb = new ToolBar();
    	
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
    	        // TableView<Appointment> tv = DisplayAppointment.createAppointmentTableView();

    	        GridPane gp = DisplayAppointment.createAppointmentTablePane();
    	        ApplicationWindow.getBorderPane().setCenter(gp);
    	    	borderPane.setCenter(gp);
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
    	
    	/*showCharts.setGraphic(new ImageView(allImages.get("BarChart.png")));
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
    	});*/
    	
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
    	
    	cleanUp.setGraphic(new ImageView(allImages.get("RecycleBin.png")));
    	cleanUp.setTooltip(new Tooltip("Alte Termine löschen"));
    	cleanUp.setOnAction(new EventHandler<ActionEvent>() {
    	    @Override 
    	    public void handle(ActionEvent e) {
    	        LOGGER.debug("export");
    	        Stage stage = (Stage) helpPage.getScene().getWindow();
    	        stage.setTitle("Hilfe");
    	    	TabPane tp = DisplayHelp.createHelpPane(stage, allImages);
    	    	borderPane.setCenter(tp);
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
    			serviceList, serviceAdd, expensesList, expensesAdd, export, cleanUp, helpPage);
    	
    	return tb;
    }
	
	public GridPane createStartPane(){
		GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(20, 20, 20, 30));
        gridPane.setHgap(30); 
        gridPane.setVgap(10);
        
        Text header = new Text("Praxisbuch - Termine");
        header.setCache(true);
        header.setFill(Color.GREEN);
        header.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
        
        List<Appointment> today_appointments = new ArrayList<Appointment>();
        List<Appointment> tomorrow_appointments = new ArrayList<Appointment>();
        List<Appointment> dayAfterTomorrow_appointments = new ArrayList<Appointment>();
		try {
			today_appointments = AppointmentHelper.getInstance().getAppointments(new Date());
			Date tomorrow = new Date();
			Date dayAfterTomorrow = new Date();
			Calendar c = Calendar.getInstance(); 
			c.setTime(tomorrow); 
			c.add(Calendar.DATE, 1);
			tomorrow = c.getTime();
			tomorrow_appointments = AppointmentHelper.getInstance().getAppointments(tomorrow);
			c.add(Calendar.DATE, 1);
			dayAfterTomorrow = c.getTime();
			dayAfterTomorrow_appointments = AppointmentHelper.getInstance().getAppointments(dayAfterTomorrow);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
        
        gridPane.add(header, 0, 0, 3, 1);
        
        Text toadyText = new Text("Heute");
        toadyText.setFill(Color.PERU);
        toadyText.setFont(Font.font("Verdana", FontWeight.EXTRA_BOLD, 14));
        gridPane.add(toadyText, 0, 2);
        
        Text tomorrowText = new Text("Morgen");
        tomorrowText.setFill(Color.PERU);
        tomorrowText.setFont(Font.font("Verdana", FontWeight.EXTRA_BOLD, 14));
        gridPane.add(tomorrowText, 1, 2);
        
        Text dayAfterTomorrowText = new Text("Übermorgen");
        dayAfterTomorrowText.setFill(Color.PERU);
        dayAfterTomorrowText.setFont(Font.font("Verdana", FontWeight.EXTRA_BOLD, 14));
        gridPane.add(dayAfterTomorrowText, 2, 2);
        
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		StringBuilder sb = new StringBuilder();
		
        if (today_appointments == null || today_appointments.size() <= 0){
        	gridPane.add(new Label("Keine Termine"), 0, 3);
        }else{
        	int row = 4;
        	for (Appointment appointment : today_appointments) {
        		sb = new StringBuilder();
        		sb.append(sdf.format(appointment.getDate()));
        		sb.append(" ");
        		sb.append(appointment.getPatient().toString());
				gridPane.add(new Label(sb.toString()), 0, row++);
			}
        }
        if (tomorrow_appointments == null || tomorrow_appointments.size() <= 0){
        	gridPane.add(new Label("Keine Termine"), 1, 3);
        }else{
        	int row = 4;
        	for (Appointment appointment : tomorrow_appointments) {
        		sb = new StringBuilder();
        		sb.append(sdf.format(appointment.getDate()));
        		sb.append(" ");
        		sb.append(appointment.getPatient().toString());
				gridPane.add(new Label(sb.toString()), 1, row++);
			}
        }
        if (dayAfterTomorrow_appointments == null || dayAfterTomorrow_appointments.size() <= 0){
        	gridPane.add(new Label("Keine Termine"), 2, 3);
        }else{
        	int row = 4;
        	for (Appointment appointment : dayAfterTomorrow_appointments) {
        		sb = new StringBuilder();
        		sb.append(sdf.format(appointment.getDate()));
        		sb.append(" ");
        		sb.append(appointment.getPatient().toString());
				gridPane.add(new Label(sb.toString()), 2, row++);
			}
        }
        
        return gridPane;
	}
	
}

