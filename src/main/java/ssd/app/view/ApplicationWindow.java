package ssd.app.view;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ssd.app.dao.DbHelper;
import ssd.app.model.Appointment;
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
	private Button closeApp;
	
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
    	//bp.setPadding(new Insets(10, 20, 10, 20));
    	GridPane gp = createStartPane();
        
    	borderPane.setCenter(gp);
//    	MenuBar menuBar = createMenu();
//        bp.setTop(menuBar);
    	ToolBar toolBar = createToolBar();
    	borderPane.setTop(toolBar);
    	scene = new Scene(borderPane, 800, 600);
        stage.setTitle("PraxisBuch - Osteopathie CW");
        scene.getStylesheets().add("/css/global.css");
        stage.setScene(scene);
        stage.show();
//        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
//            public void handle(WindowEvent we) {
//                LOGGER.debug("Stage is closing");
//                DbHelper.getInstance().close();
//            }
//        });
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
    	serviceList = new Button();
    	serviceAdd = new Button();
    	appointmentList = new Button();
    	expensesList = new Button();
    	expensesAdd = new Button();

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
    	        GridPane gp = DisplayPatient.createPatientPane();
    	    	borderPane.setCenter(gp);
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
    	    @Override public void handle(ActionEvent e) {
    	        LOGGER.debug("Expenses list");
    	        Stage stage = (Stage) expensesList.getScene().getWindow();
    	        stage.setTitle("Ausgaben");
//    	        TableView<Appointment> tv = DisplayAppointment.createAppointmentTableView();
//    	    	borderPane.setCenter(tv);
    	    }
    	});
    	
    	expensesAdd.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/icons/FavouriteAdd.png"))));
    	expensesAdd.setTooltip(new Tooltip("Ausgabe hinzufügen"));
    	expensesAdd.setOnAction(new EventHandler<ActionEvent>() {
    	    @Override public void handle(ActionEvent e) {
    	        LOGGER.debug("Expenses add");
    	        Stage stage = (Stage) expensesAdd.getScene().getWindow();
    	        stage.setTitle("Ausgabe hinzufügen");
//    	        TableView<Appointment> tv = DisplayAppointment.createAppointmentTableView();
//    	    	borderPane.setCenter(tv);
    	    }
    	});

    	tb.getItems().addAll(closeApp, patientList, patientAdd, appointmentList, serviceList, serviceAdd, expensesList, expensesAdd);
    	
    	return tb;
    }
	
	public GridPane createStartPane(){
		GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(20, 0, 20, 20));
        gridPane.setHgap(7); 
        gridPane.setVgap(7);
        
        Label lbStart = new Label("Start screen - has to be styled");
        GridPane.setHalignment(lbStart, HPos.CENTER);
        gridPane.add(lbStart, 0, 0); 
        return gridPane;
	}
    

    
}

