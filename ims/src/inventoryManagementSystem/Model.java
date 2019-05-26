package inventoryManagementSystem;

import java.util.HashMap;
import java.util.Map;

import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Model
{
	private Stage stage;
	private Scene scene;
	private Map<String, Object> controllers = new HashMap<String, Object>();
	private Map<String, Pane> paneList = new HashMap<String, Pane>();
	private ObservableMap<String, Pane> panes = FXCollections.observableMap(paneList);
	private Inventory mainInventory = new Inventory();
	
	public enum StockErrors {
		INVALID_STOCK, MAX_TOO_LOW, MIN_TOO_LOW, MIN_TOO_HIGH, PRICE_TOO_LOW, INVALID_FIELD
	}
	
	public enum AddModify {
		ADD, MODIFY
	}
	
	public Model() {		
		panes.put("main", null);
		panes.put("sub", null);
		panes.put("prev_main", null);
		panes.put("prev_sub", null);
	}
	
	public Inventory getMainInventory() {
		return mainInventory;
	}
	
	public void setScene(Scene scene) {
		this.scene = scene;
	}
	
	public void setStage (Stage stage) {
		this.stage = stage;
	}
	public void addControllers(String controllerName, Object controller)
	{
		if (!controllers.containsKey(controllerName)) {
			controllers.put(controllerName, controller);
		}
		else
		{
			controllers.replace(controllerName, controller);
		}
		
	}
	
	public Object getControllers(String controllerName)
	{
		return controllers.get(controllerName);
	}
	
	public Scene getScene() {
		return scene;
	}
	
	public ObservableMap<String, Pane> getPaneMap() {
		return panes;
	}
	public Stage getStage() {
		return stage;
	}
	
	public Pane getPane(String paneName) {
		return panes.get(paneName);
	}
	
	public void revertPanes() {
		panes.replace("main", panes.get("prev_main"));
		if (panes.get("prev_sub") != null) {
			panes.replace("sub", panes.get("prev_sub"));
		}
		
	}
	
	public void setPane(String paneName, Pane pane) {
		paneList.replace("prev_" + paneName, panes.get(paneName));
		panes.replace(paneName.toLowerCase(), pane);
	}
}
