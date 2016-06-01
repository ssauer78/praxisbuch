package ssd.app.view;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
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
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.converter.DefaultStringConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ssd.app.helper.ApplicationHelper;
import ssd.app.helper.ExpensesHelper;
import ssd.app.helper.FloatEditingCell;
import ssd.app.model.Expense;

import com.sun.org.apache.xerces.internal.impl.xpath.regex.ParseException;

public class DisplayExpenses {
	private static final Logger LOGGER = LoggerFactory.getLogger(DisplayExpenses.class);

	@SuppressWarnings("unchecked")
	protected static TableView<Expense> createExpensesTableView() throws ParseException {
		TableView<Expense> expensesTable = new TableView<Expense>();
        expensesTable.setEditable(true);

        TableColumn<Expense, Expense> expenseActionColumn = new TableColumn<>("");
        expenseActionColumn.setMinWidth(50);
        expenseActionColumn.setMaxWidth(50);
        expenseActionColumn.setCellValueFactory(new Callback<CellDataFeatures<Expense, Expense>, ObservableValue<Expense>>() {
          @Override 
          public ObservableValue<Expense> call(CellDataFeatures<Expense, Expense> features) {
              return new ReadOnlyObjectWrapper<Expense>(features.getValue());
          }
        });
        expenseActionColumn.setCellFactory(new Callback<TableColumn<Expense, Expense>, TableCell<Expense, Expense>>() {
        	@Override 
        	public TableCell<Expense, Expense> call(TableColumn<Expense, Expense> btnCol) {
        		return new TableCell<Expense, Expense>() {
        			final ImageView buttonGraphic = new ImageView(new Image(getClass().getResourceAsStream("/icons/Delete2.png")));
        			final Button button = new Button(); {
        				button.setGraphic(buttonGraphic);
        				button.setTooltip(new Tooltip("Leistung löschen"));
        				button.setMinWidth(32);
        			}
        			@Override 
        			public void updateItem(final Expense expense, boolean empty) {
        				super.updateItem(expense, empty);

        				if(empty){	// don't show the button if there is no Expense
        					return;
        				}
        				setGraphic(button);
        				button.setOnAction(new EventHandler<ActionEvent>() {
        					@Override 
        					public void handle(ActionEvent event) {
        						LOGGER.debug("Handle button click 'delete expense'");
        						try {
        							Alert confirm = new Alert(AlertType.CONFIRMATION);
        							confirm.setTitle("Wirklisch löschen?");
        							confirm.setHeaderText("");
        							confirm.setContentText("Soll die Ausgabe '" + expense.getName() + "' wirklich gelöscht werden?");
        							Optional<ButtonType> result = confirm.showAndWait();
        							if (result.get() == ButtonType.OK){
    									expense.delete();
    									Stage stage = (Stage) ApplicationWindow.getScene().getWindow();
    					    	        stage.setTitle("Leistungen");
    									TableView<Expense> tv = createExpensesTableView();
    					    	        ApplicationWindow.getBorderPane().setCenter(tv);
        							}
								} catch (SQLException e) {
									ApplicationHelper.showError(e, "Löschen der Ausgabe fehlgeschlagen", 
											"Die Ausgabe " + expense.toString() + " konnte nicht gelöscht werden. ");
								}
        					}
        				});
        			}
        		};
        	}
        });
        

    	TableColumn<Expense, String> expenseNameColumn = new TableColumn<Expense, String>("Ausgabe");
        expenseNameColumn.setCellValueFactory(new PropertyValueFactory<Expense, String>("name"));
        expenseNameColumn.setCellFactory(TextFieldTableCell.<Expense, String>forTableColumn(new DefaultStringConverter()));
        expenseNameColumn.setOnEditCommit(new EventHandler<CellEditEvent<Expense, String>>() {
        	@Override
        	public void handle(CellEditEvent<Expense, String> t) {
        		Expense s = ((Expense) t.getTableView().getItems().get(t.getTablePosition().getRow()));
        		s.setName(t.getNewValue());
        		try {
					s.save();
				} catch (SQLException e) {
					LOGGER.error(e.getMessage());
					ApplicationHelper.showError(e, "Speichern einer Ausgabe fehlgeschlagen", "Die Ausgabe " + s.toString() + " konnte nicht gespeichert werden. ");
				} 
        	}
        });
        
        TableColumn<Expense, Number> expenseCostColumn = new TableColumn<Expense, Number>("Kosten");
        expenseCostColumn.setCellValueFactory(cellData -> cellData.getValue().costProperty());
        expenseCostColumn.setCellFactory(col -> new FloatEditingCell());
        
        DateTimeFormatter myDateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        final TableColumn<Expense, String> expenseDateColumn = new TableColumn<Expense, String>("Datum");
        expenseDateColumn.setCellValueFactory(new PropertyValueFactory<Expense, String>("Date"));
        expenseDateColumn.setCellValueFactory(
        	cellData -> new ReadOnlyObjectWrapper<String>(myDateFormatter.format(cellData.getValue().getDate().toLocalDateTime()))
        );
        expenseDateColumn.setEditable(false);
        expenseDateColumn.setComparator(new Comparator<String>(){
        	@Override 
            public int compare(String t, String t1) {
        		try{
            	   SimpleDateFormat format =new SimpleDateFormat("dd.MM.yyyy");
            	   Date d1, d2;
            	   try {
            		   d1 = format.parse(t);
            		   d2 = format.parse(t1);
                	   return Long.compare(d1.getTime(), d2.getTime());
            	   } catch (java.text.ParseException e) {
            		   e.printStackTrace();
            	   }
        		}catch(ParseException p){
        			p.printStackTrace();
        		}
        		return -1;
        	}
        });

        expensesTable.getColumns().addAll(expenseActionColumn, expenseNameColumn, expenseCostColumn, expenseDateColumn);
        
        List<Expense> expenses = new ArrayList<Expense>();
		try {
			expenses = ExpensesHelper.getInstance().getExpenses();
		} catch (SQLException e1) {
			// ignore => just show nothing
		}
		if(expenses.size() > 0){
	        ObservableList<Expense> data = FXCollections.observableList(expenses);
	        expensesTable.setItems(data);
		}
		
		return expensesTable;
	}
	
	protected static GridPane createExpensesPane() {
		GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(20, 0, 20, 20));
        gridPane.setHgap(7); 
        gridPane.setVgap(7);
        Label lbExpense = new Label("Ausgabe: ");
        lbExpense.setStyle(ApplicationWindow.getLabelStyle());
        GridPane.setHalignment(lbExpense, HPos.RIGHT);
        TextField expenseName = new TextField ();
        
        Label lbCost = new Label("Kosten: ");
        lbCost.setStyle(ApplicationWindow.getLabelStyle());
        GridPane.setHalignment(lbCost, HPos.RIGHT);
        TextField expenseCost = new TextField ();
        expenseCost.setPrefColumnCount(3);
        expenseCost.lengthProperty().addListener(new ChangeListener<Number>(){
        	@Override
        	public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {              
        		if(newValue.intValue() > oldValue.intValue()){
        			char ch = expenseCost.getText().charAt(oldValue.intValue());              
        			// Check if the new character is the number or other's
        			if(!(ch >= '0' && ch <= '9' ) && ch != '.'){
        				// if it's not number then just setText to previous one
        				expenseCost.setText(expenseCost.getText().substring(0, expenseCost.getText().length()-1)); 
        			}
        		}
            }
        });
        Label lbEuro = new Label(" €");
        lbEuro.setStyle(ApplicationWindow.getLabelStyle());
        GridPane.setHalignment(lbEuro, HPos.RIGHT);
        
        Label lbInfo = new Label("Beschreibung");
        lbInfo.setStyle(ApplicationWindow.getLabelStyle());
        TextArea description = new TextArea();
        description.setPromptText("Beschreibung");
        
        Button submit = new Button("Speichern");
        submit.setOnAction((ActionEvent event) -> {
        	LOGGER.debug("Create expense");
        	Expense expense = new Expense();
        	expense.setName(expenseName.getText());
        	expense.setDescription(description.getText());
        	expense.setCost(Float.parseFloat(expenseCost.getText()));
        	expense.setDate(new Timestamp(System.currentTimeMillis()));
        	expense.setCreated(new Date());
        	expense.setModified(new Date());
        	try {
				expense.save();
				expenseName.setText("");
				expenseCost.setText("");
				description.setText("");
			} catch (Exception e) {
				ApplicationHelper.showError(e, "Speichen der Ausgabe fehlgeschlagen", "Die Ausgabe konnte nicht gespeichert werden. ");
			}
        });
        
        gridPane.add(lbExpense, 0, 0);	// column 1 ; row 1
        gridPane.add(expenseName, 1, 0);	// column 1 ; row 2
        gridPane.add(lbCost, 0, 1);	// column 1 ; row 2
        gridPane.add(expenseCost, 1, 1);	// column 2 ; row 2
        gridPane.add(lbEuro, 2, 1);	// column 3 ; row 2
        gridPane.add(lbInfo, 0, 2);
        gridPane.add(description, 1, 2);
        gridPane.add(submit, 1, 3);
        
		return gridPane;
	}
}
