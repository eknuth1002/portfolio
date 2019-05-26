package inventoryManagementSystem;

import javafx.fxml.FXML;
import java.io.IOException;
import inventoryManagementSystem.Model.AddModify;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class ProductsListController {
	
	Model model = Main.model;

	@FXML private TableView<Product> productsTableView;
	@FXML private TableColumn<Product, Integer> productIDColumn;
	@FXML private TableColumn<Product, String> productNameColumn;
	@FXML private TableColumn<Product, Integer> inventoryLevelColumn;
	@FXML private TableColumn<Product, Double> priceColumn;

	@FXML Button addButton;
	@FXML Button modifyButton;
	@FXML Button deleteButton;

	@FXML TextField searchTextField;


	@FXML
	public void initialize() {
		try
		{
			productIDColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getProductID()).asObject());
			productNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
			inventoryLevelColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getInStock()).asObject());
			priceColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getPrice()).asObject());
			productsTableView.setItems(model.getMainInventory().getAllProducts());
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void addButton() throws IOException {
		FXMLLoader pane = new FXMLLoader(getClass().getResource("FXML/Add_ModifyProducts.fxml"));
		model.setPane("main", pane.load());
		model.addControllers("Add_ModifyProductsController", pane.getController());
		Add_ModifyProductsController amp = (Add_ModifyProductsController) model.getControllers("Add_ModifyProductsController");
		amp.setAddModifyValue(AddModify.ADD);
		model.setPane("sub", null);
		
	}

	public void modifyButton() throws IOException {
		if (productsTableView.getItems().isEmpty() == false) {
			if (productsTableView.getSelectionModel().getSelectedItem() == null) {
				Dialogs.errorDialog("No item selected", "Please select an item from the list");
			}
			else {
				FXMLLoader pane = new FXMLLoader(getClass().getResource("FXML/Add_ModifyProducts.fxml"));
				model.setPane("main", pane.load());
				model.addControllers("Add_ModifyProductsController", pane.getController());
				Add_ModifyProductsController amp = (Add_ModifyProductsController) model.getControllers("Add_ModifyProductsController");
				amp.setAddModifyValue(AddModify.MODIFY);
				model.setPane("sub", null);
				amp.setRecord(productsTableView);
			}
		}
		else {
			Dialogs.errorDialog("No item selected", "There are no items in the list.  Please add an item.");
		}
	}
	
	public void deleteButton() {
		if (!productsTableView.getItems().isEmpty()) {
			if (productsTableView.getSelectionModel().isEmpty()) {
				Dialogs.errorDialog("No item selected", "Please select an item from the list");
			}
			else {
				int productID = productsTableView.getSelectionModel().getSelectedItem().getProductID();
				if (Dialogs.confirmDialog("Delete product?", "Do you wish to delete the currently selected product?")) {
					if (model.getMainInventory().lookupProduct(productID).getAllAssociatedParts().size() == 0) {
						model.getMainInventory().removeProduct(productID);
					}
					else {
						Dialogs.errorDialog("Part assigned", "There is still a part assigned.  Please modify the product\nand remove the part before deleting.");
					}
				}
			}
		}			
	}
	
	public void searchProductsList() {
		if (productsTableView.getItems().size() == 0) {
			Dialogs.errorDialog("Empty table", "I'm sorry, but there are parts available in the list.");
		}
		else  {
			for (Product product : productsTableView.getItems()) {
				if (product.getName().matches(searchTextField.getText())) {
					productsTableView.getSelectionModel().select(product);
				}
			};
		}
	}
	
	public void sortAndRefreshView() {
		productsTableView.sort();
		productsTableView.refresh();
	}
}
