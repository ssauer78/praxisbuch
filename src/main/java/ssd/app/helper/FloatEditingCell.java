package ssd.app.helper;

import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ssd.app.model.Expense;
import javafx.scene.control.TableCell;
import javafx.scene.control.TextField;

/**
 * From here: http://stackoverflow.com/questions/27900344/how-to-make-a-table-column-with-integer-datatype-editable-without-changing-it-to
 * 
 * @author sauer
 *
 */
public class FloatEditingCell extends TableCell<Expense, Number> {

	private static final Logger LOGGER = LoggerFactory.getLogger(FloatEditingCell.class);
	private final TextField textField = new TextField();
	private final Pattern floatPattern = Pattern.compile("[-+]?(\\d*[.])?\\d+");

	public FloatEditingCell() {
		textField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
			if (! isNowFocused) {
				processEdit();
			}
		});
		textField.setOnAction(event -> processEdit());
	}

	private void processEdit() {
		String text = textField.getText();
		if (floatPattern.matcher(text).matches()) {
			commitEdit(Float.parseFloat(text));
		} else {
			cancelEdit();
		}
	}

	@Override
	public void updateItem(Number value, boolean empty) {
		super.updateItem(value, empty);
		if (empty) {
			setText(null);
			setGraphic(null);
		} else if (isEditing()) {
			setText(null);
			textField.setText(value.toString());
			setGraphic(textField);
		} else {
			setText(value.toString());
			setGraphic(null);
		}
	}

	@Override
	public void startEdit() {
		super.startEdit();
		Number value = getItem();
		if (value != null) {
			textField.setText(value.toString());
			setGraphic(textField);
			setText(null);
		}
	}

	@Override
	public void cancelEdit() {
		super.cancelEdit();
		setText(getItem().toString());
		setGraphic(null);
	}

//	// This seems necessary to persist the edit on loss of focus; not sure why:
//	@Override
//	public void commitEdit(Number value) {
//		super.commitEdit(value);
//		Service s = ((Service)this.getTableRow().getItem());
//		s.setCostPerUnit(value.intValue());
//		try {
//			s.save();
//		} catch (SQLException e) {
//			LOGGER.error(e.getMessage());
//			ApplicationHelper.showError(e, "Speichen fehlgeschlagen", 
//					s.toString() + " konnte nicht gespeichert werden. ");
//		} 
//	}
}
