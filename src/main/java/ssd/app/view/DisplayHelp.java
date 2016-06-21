package ssd.app.view;

import java.io.File;
import java.util.Calendar;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
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
	
	protected static GridPane createHelpPane(Stage stage, Map<String, ImageView> allImages) {
		log.debug("Creating help pane...");
		GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(20, 10, 20, 20));
        gridPane.setHgap(7); 
        gridPane.setVgap(7);
        
        Text t = new Text();
        t.setX(70.0f);
        t.setY(50.0f);
        t.setCache(true);
        t.setText("Praxisbuch - Hilfe");
        t.setFill(Color.GREEN);
        t.setFont(Font.font(null, FontWeight.BOLD, 22));
         
        Reflection r = new Reflection();
        r.setFraction(0.4f);
        t.setEffect(r);
        
        Label lbStart = new Label("Keine Termine gefunden");
        
        gridPane.add(t, 0, 0);
        
        gridPane.add(allImages.get("FavouriteAdd.png"), 0, 3);
        
        return gridPane;
	}
}
