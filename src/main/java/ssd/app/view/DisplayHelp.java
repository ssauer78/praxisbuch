package ssd.app.view;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.effect.Reflection;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class DisplayHelp {
private static final Logger log = LoggerFactory.getLogger(DisplayHelp.class);
	
	protected static TabPane createHelpPane(Stage stage, Map<String, Image> allImages) {
		log.debug("Creating help pane...");
		GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(20, 10, 20, 20));
        gridPane.setHgap(7); 
        gridPane.setVgap(7);
        
        Text t = new Text();
        t.setX(70.0f);
        t.setY(50.0f);
        t.setCache(true);
        t.setText("Menü1 - Hilfe");
        t.setFill(Color.GREEN);
        t.setFont(Font.font(null, FontWeight.BOLD, 22));
         
        Reflection r = new Reflection();
        r.setFraction(0.4f);
        t.setEffect(r);
        
        Label lbHome = new Label("Hier sehen Sie die letzen und nächsten Termine. ");
        Label lbPatients = new Label("Zeigt eine List mit Patienten in einer Tabelle. In der ersten Spalte ist es möglich einen neuen Termin anzulegen, den Patienten zu editieren oder zu löschen. ");
        lbPatients.setWrapText(true);
        gridPane.add(t, 0, 0, 2, 1);
        
        gridPane.add(new ImageView(allImages.get("Home.png")), 0, 3);
        gridPane.add(lbHome, 1, 3);
        
        gridPane.add(new ImageView(allImages.get("Users.png")), 0, 4);
        gridPane.add(lbPatients, 1, 4);
        
        
        TabPane tabPane = new TabPane();
        Tab tab = new Tab();
        tab.setText("Menü");
        tab.setContent(gridPane);
        tab.setClosable(false);
        tabPane.getTabs().add(tab);
        
        return tabPane;
	}
}
