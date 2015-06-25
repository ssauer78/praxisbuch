package ssd.app.view;

import java.sql.SQLException;
import java.util.ArrayList;
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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
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
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.converter.DefaultStringConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ssd.app.helper.ApplicationHelper;
import ssd.app.helper.IntegerEditingCell;
import ssd.app.helper.ServiceHelper;
import ssd.app.model.Service;

import com.sun.org.apache.xerces.internal.impl.xpath.regex.ParseException;

public class DisplayService {
	private static final Logger LOGGER = LoggerFactory.getLogger(DisplayService.class);

	@SuppressWarnings("unchecked")
	protected static TableView<Service> createServiceTableView() throws ParseException {
		TableView<Service> serviceTable = new TableView<Service>();
        serviceTable.setEditable(true);

        TableColumn<Service, Service> serviceActionColumn = new TableColumn<>("Actions");
        serviceActionColumn.setMinWidth(150);
        serviceActionColumn.setCellValueFactory(new Callback<CellDataFeatures<Service, Service>, ObservableValue<Service>>() {
          @Override 
          public ObservableValue<Service> call(CellDataFeatures<Service, Service> features) {
              return new ReadOnlyObjectWrapper<Service>(features.getValue());
          }
        });
        serviceActionColumn.setCellFactory(new Callback<TableColumn<Service, Service>, TableCell<Service, Service>>() {
        	@Override 
        	public TableCell<Service, Service> call(TableColumn<Service, Service> btnCol) {
        		return new TableCell<Service, Service>() {
        			final ImageView buttonGraphic = new ImageView(new Image(getClass().getResourceAsStream("/icons/Delete2.png")));
        			final Button button = new Button(); {
        				button.setGraphic(buttonGraphic);
        				button.setTooltip(new Tooltip("Leistung löschen"));
        				button.setMinWidth(32);
        			}
        			@Override 
        			public void updateItem(final Service service, boolean empty) {
        				super.updateItem(service, empty);

        				if(empty){	// don't show the button if there is no Service
        					return;
        				}
        				setGraphic(button);
        				button.setOnAction(new EventHandler<ActionEvent>() {
        					@Override 
        					public void handle(ActionEvent event) {
        						LOGGER.debug("Handle button click 'delete service'");
        						try {
        							Alert confirm = new Alert(AlertType.CONFIRMATION);
        							confirm.setTitle("Wirklisch löschen?");
        							confirm.setHeaderText("");
        							confirm.setContentText("Soll die Leistung '" + service.getName() + "' wirklich gelöscht werden?");
        							Optional<ButtonType> result = confirm.showAndWait();
        							if (result.get() == ButtonType.OK){
    									service.delete();
    									Stage stage = (Stage) ApplicationWindow.getScene().getWindow();
    					    	        stage.setTitle("Leistungen");
    									TableView<Service> tv = createServiceTableView();
    					    	        ApplicationWindow.getBorderPane().setCenter(tv);
        							}
								} catch (SQLException e) {
									ApplicationHelper.showError(e, "Löschen der Leistung fehlgeschlagen", 
											"Die Leistung " + service.toString() + " konnte nicht gelöscht werden. ");
								}
        					}
        				});
        			}
        		};
        	}
        });
        

    	TableColumn<Service, String> serviceNameColumn =new TableColumn<Service, String>("Leistung");
        serviceNameColumn.setCellValueFactory(new PropertyValueFactory<Service, String>("name"));
        serviceNameColumn.setCellFactory(TextFieldTableCell.<Service, String>forTableColumn(new DefaultStringConverter()));
        serviceNameColumn.setOnEditCommit(new EventHandler<CellEditEvent<Service, String>>() {
        	@Override
        	public void handle(CellEditEvent<Service, String> t) {
        		Service s = ((Service) t.getTableView().getItems().get(t.getTablePosition().getRow()));
        		s.setName(t.getNewValue());
        		try {
					s.save();
				} catch (SQLException e) {
					LOGGER.error(e.getMessage());
					ApplicationHelper.showError(e, "Speichern einer Leistung fehlgeschlagen", "Die Leistung " + s.toString() + " konnte nicht gespeichert werden. ");
				} 
        	}
        });
        
        TableColumn<Service, Number> serviceCostColumn = new TableColumn<Service, Number>("Kosten pro Einheit");
        serviceCostColumn.setCellValueFactory(cellData -> cellData.getValue().costProperty());
        serviceCostColumn.setCellFactory(col -> new IntegerEditingCell());

        serviceTable.getColumns().addAll(serviceActionColumn, serviceNameColumn, serviceCostColumn);
        
        List<Service> services = new ArrayList<Service>();
		try {
			services = ServiceHelper.getInstance().getServices();
		} catch (SQLException e1) {
			// ignore => just show nothing
		}
		if(services.size() > 0){
	        ObservableList<Service> data = FXCollections.observableList(services);
	        serviceTable.setItems(data);
		}
		
		return serviceTable;
	}
	
	protected static GridPane createServicePane() {
		GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(20, 0, 20, 20));
        gridPane.setHgap(7); 
        gridPane.setVgap(7);
        Label lbService = new Label("Leistung: ");
        lbService.setStyle(ApplicationWindow.getLabelStyle());
        GridPane.setHalignment(lbService, HPos.RIGHT);
        TextField serviceName = new TextField ();
        
        Label lbCost = new Label("Kosten pro Einheit: ");
        lbCost.setStyle(ApplicationWindow.getLabelStyle());
        GridPane.setHalignment(lbCost, HPos.RIGHT);
        TextField serviceCost = new TextField ();
        serviceCost.setPrefColumnCount(3);
        serviceCost.lengthProperty().addListener(new ChangeListener<Number>(){
        	@Override
        	public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {              
        		if(newValue.intValue() > oldValue.intValue()){
        			char ch = serviceCost.getText().charAt(oldValue.intValue());              
        			// Check if the new character is the number or other's
        			if(!(ch >= '0' && ch <= '9' )){
        				// if it's not number then just setText to previous one
        				serviceCost.setText(serviceCost.getText().substring(0, serviceCost.getText().length()-1)); 
        			}
        		}
            }
        });
        Label lbEuro = new Label(" €");
        lbEuro.setStyle(ApplicationWindow.getLabelStyle());
        GridPane.setHalignment(lbEuro, HPos.RIGHT);
        
        Button submit = new Button("Submit");
        submit.setOnAction((ActionEvent event) -> {
        	LOGGER.debug("Create service");
        	Service service = new Service();
        	service.setName(serviceName.getText());
        	service.setCostPerUnit(Integer.parseInt(serviceCost.getText()));
        	service.setCreated(new Date());
        	service.setModified(new Date());
        	try {
				service.save();
				serviceName.setText("");
				serviceCost.setText("");
			} catch (Exception e) {
				ApplicationHelper.showError(e, "Speichen einer Leistung fehlgeschlagen", "Die Leistung konnte nicht gespeichert werden. ");
			}
        });
        
        gridPane.add(lbService, 0, 0);	// column 1 ; row 1
        gridPane.add(serviceName, 1, 0);	// column 1 ; row 2
        gridPane.add(lbCost, 0, 1);	// column 1 ; row 2
        gridPane.add(serviceCost, 1, 1);	// column 2 ; row 2
        gridPane.add(lbEuro, 2, 1);	// column 3 ; row 2
        
        gridPane.add(submit, 1, 2);
        
		return gridPane;
	}
}
