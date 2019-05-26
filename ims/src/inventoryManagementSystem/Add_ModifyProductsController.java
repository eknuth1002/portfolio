package inventoryManagementSystem;

import inventoryManagementSystem.Model.AddModify;
import inventoryManagementSystem.Model.StockErrors;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class Add_ModifyProductsController {
	@FXML private TableView<Part> partsInProductTableView;
	@FXML private TableColumn<Part, Integer> partInProductIDColumn;
	@FXML private TableColumn<Part, String> partInProductNameColumn;
	@FXML private TableColumn<Part, Integer> partInProductInventoryLevelColumn;
	@FXML private TableColumn<Part, Double> partInProductPriceColumn;
	private ObservableList<Part> partsInProductList = FXCollections.observableArrayList();

	//To populate the parts views
	@FXML private TableView<Part> partsTableView;
	@FXML private TableColumn<Part, Integer> partIDColumn;
	@FXML private TableColumn<Part, String> partNameColumn;
	@FXML private TableColumn<Part, Integer> partInventoryLevelColumn;
	@FXML private TableColumn<Part, Double> partPriceColumn;
	private ObservableList<Part> partsNotInProductList = FXCollections.observableArrayList();
		
	@FXML private Label addLabel;
	@FXML private Label modifyLabel;
		
	Model model = Main.model;
	
	@FXML Button SaveButton;
	@FXML Button CancelButton;
	@FXML TextField idTextField;
	@FXML TextField nameTextField;
	@FXML TextField inventoryNumberField;
	@FXML TextField priceCostNumberField;
	@FXML TextField minNumberField;
	@FXML TextField maxNumberField;
	
	AddModify addModifyMode;
	@FXML Label ProductStatusLabel;
	@FXML TextField searchField;
	
	@FXML
	public void initialize() {
		setPartsNotInProductTable();			
		setPartsInProductTable();
	}
	
	private void setPartsNotInProductTable() {
		partIDColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getPartID()).asObject());
		partNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
		partInventoryLevelColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getInStock()).asObject());
		partPriceColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getPrice()).asObject());
		partsNotInProductList.setAll(model.getMainInventory().getAllParts());
		partsTableView.setItems(partsNotInProductList);
		partsTableView.getSortOrder().add(partNameColumn);
	}

	private void setPartsInProductTable() {
		try {
			partInProductIDColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getPartID()).asObject());
			partInProductNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
			partInProductInventoryLevelColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getInStock()).asObject());
			partInProductPriceColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getPrice()).asObject());
			partsInProductTableView.setItems(partsInProductList);
			partsInProductTableView.getSortOrder().add(partInProductNameColumn);
		}
		catch (Exception e) {
			
		}
	}
	
	public void sortAndRefreshView() {
		partsInProductTableView.sort();
		partsInProductTableView.refresh();
	}
	
	public void saveProduct() {
		Product product = new Product();
		
		try { //This try-catch block checks for any empty fields.  If there are any, a dialog is shown stating which one is empty
			if (nameTextField.getText().isEmpty()) {
				throw new Exception(StockErrors.INVALID_FIELD.toString(), new Throwable("Name Text Field"));
			}
			else if (inventoryNumberField.getText().isEmpty()) {
				throw new Exception(StockErrors.INVALID_FIELD.toString(), new Throwable("Inventory Text Field"));
			}
			else if (priceCostNumberField.getText().isEmpty()) {
				throw new Exception(StockErrors.INVALID_FIELD.toString(), new Throwable("Price Text Field"));
			}
			else if (minNumberField.getText().isEmpty()) {
				throw new Exception(StockErrors.INVALID_FIELD.toString(), new Throwable("Minimum Stock Text Field"));
			}
			else if (maxNumberField.getText().isEmpty()) {
				throw new Exception(StockErrors.INVALID_FIELD.toString(), new Throwable("Maximum Stock Text Field"));
			}
		}
		catch (Exception except) {
			Dialogs.errorDialog(new String("Blank Field"), new String("The " 
				+ except.getCause().toString().substring(except.getCause().toString().indexOf(":") + 2) 
				+ " is blank.  Please try again."));
			
			return;
		}
		
		try { //This try-catch block attempts to set values in the product
			if (idTextField.getText().matches("AutoGen - Disabled")) {
				product.setProductID(model.getMainInventory().getAllProducts().size() + 1);
			}
			else {
				product.setProductID(Integer.parseInt(idTextField.getText()));
			}
			
			for (Part part : partsInProductList) {
				product.addAssociatedPart(model.getMainInventory().lookupPart(part.getPartID()));	
			}
			
			product.setName(nameTextField.getText());
			
			try { product.setMax(Integer.parseInt(maxNumberField.getText())); }
				catch (NumberFormatException except) { throw new Exception(StockErrors.INVALID_FIELD.toString(), new Throwable("Maximum Stock Text Field")); }
			
			try { product.setMin(Integer.parseInt(minNumberField.getText())); }
				catch (NumberFormatException except) { throw new Exception(StockErrors.INVALID_FIELD.toString(), new Throwable("Minimum Stock Text Field")); }
			
 			try { product.setInStock(Integer.parseInt(inventoryNumberField.getText())); }
 				catch (NumberFormatException except) { throw new Exception(StockErrors.INVALID_FIELD.toString(), new Throwable("Inventory Text Field")); }
				catch (Exception except) { throw new Exception(except); }
			
			try { product.setPrice(Double.parseDouble(priceCostNumberField.getText())); }
				catch (NumberFormatException except) { throw new Exception(StockErrors.INVALID_FIELD.toString(), new Throwable("Price Text Field")); }
				catch (Exception except) { throw new Exception(except.toString(), new Throwable(except.getCause().toString().substring(except.getCause().toString().indexOf(':') + 2))); }
			
			//Checks if the user is adding or modifying a product.  If they are Modifying and remove all parts, the product is deleted automatically.
			if (product.getAllAssociatedParts().size() > 0) {
				if (addModifyMode == AddModify.ADD) {
				model.getMainInventory().addProduct(product);
				}
				else {
					model.getMainInventory().updateProduct(product);
				}
			}
			else {
				Dialogs.errorDialog("No associated parts", "I'm sorry, but you have no associated parts with the product.\nPlease add at least 1.");
			}
			
			model.revertPanes();
			ProductsListController plc = (ProductsListController) model.getControllers("ProductsListController");
			plc.sortAndRefreshView();
		}
		catch(Exception except) {
			if (except.toString().contains(StockErrors.MAX_TOO_LOW.toString())) {
				Dialogs.errorDialog(new String("Invalid Maximum Amount"), new String("You have entered"
						+ " an amount that is lower than the current minimum of " + product.getMin()
						+ ". Please enter a new amount."));
			}
			else if (except.toString().contains(StockErrors.MIN_TOO_HIGH.toString())) {
				Dialogs.errorDialog(new String("Invalid Minimum Amount"), new String("You have entered"
						+ " an amount that is higher than the current maximum of " + product.getMax()
						+ ". Please enter a new amount."));
			}
			else if (except.toString().contains(StockErrors.MIN_TOO_LOW.toString())) {
				Dialogs.errorDialog(new String("Invalid Minimum Amount"), new String("The minimum amount must"
						+ " be greater than or equal to 0."));
			}
			else if (except.toString().contains(StockErrors.INVALID_STOCK.toString())) {
				Dialogs.errorDialog(new String("Invalid Stock Amount"), new String("You have entered"
						+ " an amount that is outside of the range of " + product.getMin() + " and " + product.getMax()));
			}
			else if (except.toString().contains(StockErrors.INVALID_FIELD.toString())) {
				Dialogs.errorDialog(new String("Invalid Field"), new String("The " 
						+ except.getCause().toString().substring(except.getCause().toString().indexOf(":") + 2) 
						+ " is not a number.  Please correct this and try again."));
			}
			else if (except.toString().contains(StockErrors.PRICE_TOO_LOW.toString())) {
				Dialogs.errorDialog(new String("Invalid Price"), new String("The total price of the product must be more than the price of all the parts of "
						+ except.getCause().toString().substring(except.getCause().toString().indexOf(':') + 2)));
			}
			else {
				except.printStackTrace();
			}
		}
	}
	
	public void setAddModifyValue(AddModify addModifyChoice) {
		if (addModifyChoice == AddModify.ADD) {
			addModifyMode = AddModify.ADD;
			this.addLabel.setVisible(true);
			this.modifyLabel.setVisible(false);
		}
		else {
			addModifyMode = AddModify.MODIFY;
			this.addLabel.setVisible(false);
			this.modifyLabel.setVisible(true);
		}
	}
	
	//
	public void cancelButton() {
		if (Dialogs.confirmDialog("Cancel Product change?", "Do you wish to cancel all changes and return to the main screen?")) {
			model.revertPanes();
		}
	}
	
	public void setRecord(TableView<Product> productsTableView) {
		for (Product product : model.getMainInventory().getAllProducts()) {
			if (product.getProductID() == productsTableView.getSelectionModel().getSelectedItem().getProductID()) {
				idTextField.setText(String.valueOf(product.getProductID()));
				nameTextField.setText(product.getName());
				inventoryNumberField.setText(String.valueOf(product.getInStock()));
				priceCostNumberField.setText(String.valueOf(product.getPrice()));
				minNumberField.setText(String.valueOf(product.getMin()));
				maxNumberField.setText(String.valueOf(product.getMax()));
				try {
					partsInProductList.setAll(product.getAllAssociatedParts());
					//partsNotInProductList.setAll(model.getMainInventory().getAllParts());
					partsNotInProductList.removeAll(partsInProductList);
				}
				catch (Exception e) {
					
				}
				break;
			}
		}
		
	}

	public void addPart() {
		int partID;
		if (partsTableView.getChildrenUnmodifiable().isEmpty()) {
			Dialogs.errorDialog("Empty table", "I'm sorry, but there are no new parts available to add to this product.");
		}
		else if (partsTableView.getSelectionModel().isEmpty()) {
			Dialogs.errorDialog("No item selected", "Please select an item from the list");
		}
		
		else {
			partID = partsTableView.getSelectionModel().getSelectedItem().getPartID();
			partsInProductList.add(model.getMainInventory().lookupPart(partID));
			partsNotInProductList.remove(model.getMainInventory().lookupPart(partID));
			
		}
		sortAndRefreshView();
	}
	
	public void removePart() {
		int partID;
		if (Dialogs.confirmDialog("Remove Part for Product", "Do you wish to remove the selected part from the current product?")) {
			if (partsInProductTableView.getChildrenUnmodifiable().size() == 0) {
				Dialogs.errorDialog("Empty table", "I'm sorry, but there are no parts available to remove from this product.");
			}
			else if (partsInProductTableView.getSelectionModel().isEmpty()) {
				Dialogs.errorDialog("No item selected", "Please select an item from the list");
			}
			else {
				partID = partsInProductTableView.getSelectionModel().getSelectedItem().getPartID();
				partsInProductList.remove(model.getMainInventory().lookupPart(partID));
				partsNotInProductList.add(model.getMainInventory().lookupPart(partID));
			}
			partsTableView.sort();
		}
	}
	
	public void searchPartsList() {
		if (partsTableView.getItems().size() == 0) {
			Dialogs.errorDialog("Empty table", "I'm sorry, but there are no parts available to add to this product.");
		}
		else  {
			for (Part part : partsTableView.getItems()) {
				if (part.getName().matches(searchField.getText())) {
					partsTableView.getSelectionModel().select(part);
				}
			};
		}
	}
}
