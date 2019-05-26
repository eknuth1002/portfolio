package inventoryManagementSystem;

import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

public class Dialogs {
	private static Alert alert = new Alert(null);
	static void exitDialog(String titleString, String contentString) {

		alert.setAlertType(AlertType.CONFIRMATION);
		alert.setHeaderText(null);
		alert.setTitle(titleString);
		alert.setContentText(contentString);
		
		
		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK) {
			System.exit(0);
		}
	}
	
	static void errorDialog(String titleString, String contentString) {

		alert.setAlertType(AlertType.ERROR);
		alert.setHeaderText(null);
		alert.setTitle(titleString);
		alert.setContentText(contentString);
		alert.showAndWait();
	}
	
	static Boolean confirmDialog(String titleString, String contentString) {
		alert.setAlertType(AlertType.CONFIRMATION);
		alert.setHeaderText(null);
		alert.setTitle(titleString);
		alert.setContentText(contentString);
		
		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK) {
			return true;
		}
		else {
			return false;
		}
	}
}
