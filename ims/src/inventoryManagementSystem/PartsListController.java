package inventoryManagementSystem;

import java.io.IOException;
import inventoryManagementSystem.Model.AddModify;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class PartsListController {

	Model model = Main.model;
	
	@FXML private TableView<Part> partsTableView;
	@FXML private TableColumn<Part, Integer> partIDColumn;
	@FXML private TableColumn<Part, String> partNameColumn;
	@FXML private TableColumn<Part, Integer> inventoryLevelColumn;
	@FXML private TableColumn<Part, Double> priceColumn;
	
	@FXML Button addButton;
	@FXML Button modifyButton;
	@FXML Button deleteButton;

	@FXML TextField searchField;
	
	@FXML
	public void initialize() {
		partIDColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getPartID()).asObject());
		partNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
		inventoryLevelColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getInStock()).asObject());
		priceColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getPrice()).asObject());
			
		partsTableView.setItems(model.getMainInventory().getAllParts());		
	}
	
	public void addButton() throws IOException {
		FXMLLoader pane = new FXMLLoader(getClass().getResource("FXML/Add_ModifyParts.fxml"));
		model.setPane("main", pane.load());
		model.addControllers("Add_ModifyPartsController", pane.getController());
		Add_ModifyPartsController amp = (Add_ModifyPartsController) model.getControllers("Add_ModifyPartsController");
		amp.setAddModifyValue(AddModify.ADD);
		model.setPane("sub", null);
		
	}
	
	public void modifyButton() throws IOException {
		if (partsTableView.getItems().isEmpty() == false) {
			if (partsTableView.getSelectionModel().getSelectedItem() == null) {
				Dialogs.errorDialog("No item selected", "Please select an item from the list");
			}
			else {
				FXMLLoader pane = new FXMLLoader(getClass().getResource("FXML/Add_ModifyParts.fxml"));
				model.setPane("main", pane.load());
				model.addControllers("Add_ModifyPartsController", pane.getController());
				Add_ModifyPartsController amp = (Add_ModifyPartsController) model.getControllers("Add_ModifyPartsController");
				amp.setAddModifyValue(AddModify.MODIFY);
			
				model.setPane("sub", null);
				amp.setRecord(partsTableView);
			}
		}
		else {
			Dialogs.errorDialog("No item selected", "There are no items in the list.  Please add an item.");
		}
	}
	
	public void deleteButton() {
		if (!partsTableView.getItems().isEmpty()) {
			if (partsTableView.getSelectionModel().isEmpty()) {
				Dialogs.errorDialog("No item selected", "Please select an item from the list");
			}
			else {
				if (Dialogs.confirmDialog("Delete part?", "Do you wish to delete the currently selected part?")) {
					
					for (Product product : model.getMainInventory().getAllProducts()) {
						for (Part partInProduct : product.getAllAssociatedParts()) {
							if (partsTableView.getSelectionModel().getSelectedItem().getPartID() == partInProduct.getPartID()) {
								product.removeAssociatedPart(partsTableView.getSelectionModel().getSelectedItem().getPartID());
								break;
							}
						}
					}

					model.getMainInventory().removePart(partsTableView.getSelectionModel().getSelectedItem().getPartID());
				}
			}
		}
	}
	
	public void searchPartsList() {
		if (partsTableView.getItems().size() == 0) {
			Dialogs.errorDialog("Empty table", "I'm sorry, but there are parts available in the list.");
		}
		else  {
			for (Part part : partsTableView.getItems()) {
				if (part.getName().matches(searchField.getText())) {
					partsTableView.getSelectionModel().select(part);
				}
			};
		}
	}
	
	public void sortAndRefreshView() {
		partsTableView.sort();
		partsTableView.refresh();
	}
}
