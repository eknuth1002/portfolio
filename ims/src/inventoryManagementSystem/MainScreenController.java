package inventoryManagementSystem;

import java.io.IOException;

import javafx.collections.MapChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

public class MainScreenController {
	@FXML private Pane MainPane;
	@FXML private Pane SubPane;
	@FXML private StackPane MainScreen;
	@FXML private BorderPane MyDialog;
	@FXML static Label Message;
	@FXML private Button Confirm;
	@FXML private Button Cancel;
	
	Model model;
	
	
	@FXML
	private void initialize() {
		model = Main.model;
	 	model.getPaneMap().addListener(new MapChangeListener<String, Pane>() {
	 	;
			@Override
			public void onChanged(javafx.collections.MapChangeListener.Change<? extends String, ? extends Pane> change)
			{
				if (!MainPane.getChildren().isEmpty()) {
						MainPane.getChildren().clear();
						
				}
				MainPane.getChildren().add(model.getPane("main"));
				if (!SubPane.getChildren().isEmpty()) {
					SubPane.getChildren().clear();
					
				}
				if (model.getPane("sub") != null) {
					SubPane.setMouseTransparent(false);
					SubPane.getChildren().add(model.getPane("sub"));
				}
				else {
					SubPane.setMouseTransparent(true);
				}
			}
		});
		
		try
		{
			FXMLLoader pane = new FXMLLoader(getClass().getResource("FXML/PartsList.fxml"));
			model.setPane("main", pane.load());
			model.addControllers("PartsListController", pane.getController());
			pane = new FXMLLoader(getClass().getResource("FXML/ProductsList.fxml"));
			model.setPane("sub", pane.load());
			model.addControllers("ProductsListController", pane.getController());
			
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	
	public void exitButton()
	{
		Dialogs.exitDialog("Exit Confirmation", "Are you sure you wish to exit?");
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
	
	public void changePane(String paneName) {
		if (paneName.toLowerCase() != "main") {
			this.SubPane.getChildren().clear();
			this.SubPane.getChildren().add(model.getPane("sub"));
		}
	}
	
	public Pane getMainPane() {
		return this.MainPane;
	}
	public Pane getSubPane() {
		return this.SubPane;
	}
}
