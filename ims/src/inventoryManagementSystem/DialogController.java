package inventoryManagementSystem;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
	
public class DialogController {
	@FXML
	private BorderPane MyDialog;
	@FXML
	static Label Message;
	@FXML
	private Button Confirm;
	@FXML
	private Button Cancel;
	
	public void makeDialog(String message)
	{
		Message.setText(message);
		MyDialog.setVisible(true);
	}
	
	@FXML
	public void exit()
	{
		System.exit(0);
	}
	
	@FXML
	private void cancel(ActionEvent event)
	{
		MyDialog.setVisible(false);
	}

}