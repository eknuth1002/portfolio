package inventoryManagementSystem;

import inventoryManagementSystem.Model.AddModify;
import inventoryManagementSystem.Model.StockErrors;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Button;

public class Add_ModifyPartsController {
	@FXML
	private Label addLabel;
	@FXML
	private Label modifyLabel;
	@FXML
	private ToggleGroup InhouseOutSource;
	@FXML
	private TextField IDTextField;
	@FXML
	private Label MachineID_CompanyName_Label;
	
	Model model;
	@FXML Button SaveButton;
	@FXML TextField NameTextField;
	@FXML TextField InventoryNumberField;
	@FXML TextField PriceCostNumberField;
	@FXML TextField MinNumberField;
	@FXML TextField MaxNumberField;
	@FXML TextField MachineID_CompanyNameTextField;
	@FXML RadioButton Inhouse;
	@FXML RadioButton Outsourced;
	
	AddModify addModifyMode;
	@FXML Button CancelButton;
	
	public Add_ModifyPartsController() {
		model = Main.model;
		
	}
	
	public void savePart() {
		RadioButton selectedButton = (RadioButton) InhouseOutSource.getSelectedToggle();
		Part part;
		
		if (selectedButton.getText().matches("Inhouse")) { part = new Inhouse(); }
		else { part = new Outsourced(); } //Set to Company Name
		
		try { //This try-catch block checks for any empty fields.  If there are any, a dialog is shown stating which one is empty
			if (NameTextField.getText().isEmpty()) {
				throw new Exception(StockErrors.INVALID_FIELD.toString(), new Throwable("Name Text Field"));
			}
			else if (InventoryNumberField.getText().isEmpty()) {
				throw new Exception(StockErrors.INVALID_FIELD.toString(), new Throwable("Inventory Text Field"));
			}
			else if (PriceCostNumberField.getText().isEmpty()) {
				throw new Exception(StockErrors.INVALID_FIELD.toString(), new Throwable("Price Text Field"));
			}
			else if (MinNumberField.getText().isEmpty()) {
				throw new Exception(StockErrors.INVALID_FIELD.toString(), new Throwable("Minimum Stock Text Field"));
			}
			else if (MaxNumberField.getText().isEmpty()) {
				throw new Exception(StockErrors.INVALID_FIELD.toString(), new Throwable("Maximum Stock Text Field"));
			}
			else if (MachineID_CompanyNameTextField.getText().isEmpty()) {
				if (selectedButton.getText().matches("Inhouse")) {
					throw new Exception(StockErrors.INVALID_FIELD.toString(), new Throwable("Machine ID Text Field"));
				}
				else {
					throw new Exception(StockErrors.INVALID_FIELD.toString(), new Throwable("Company Name Text Field"));
				}
			}
		}
		catch (Exception except) {
			Dialogs.errorDialog(new String("Blank Field"), new String("The " 
				+ except.getCause().toString().substring(except.getCause().toString().indexOf(":") + 2) 
				+ " is blank.  Please try again."));
			
			return;
		}
		
		try { //This try-catch block attempts to set each field.  A dialog is shown if any field is invalid to tell the user the highest most block to correct.
			if (IDTextField.getText().matches("AutoGen - Disabled")) {
				part.setPartID(model.getMainInventory().getAllParts().size() + 1);
			}
			else {
				part.setPartID(Integer.parseInt(IDTextField.getText()));
			}

			part.setName(NameTextField.getText());
			
			try { part.setMax(Integer.parseInt(MaxNumberField.getText())); }
				catch (NumberFormatException except) { throw new Exception(StockErrors.INVALID_FIELD.toString(), new Throwable("Maximum Stock Text Field")); }
			
			try { part.setMin(Integer.parseInt(MinNumberField.getText())); }
				catch (NumberFormatException except) { throw new Exception(StockErrors.INVALID_FIELD.toString(), new Throwable("Minimum Stock Text Field")); }
			
 			try { part.setInStock(Integer.parseInt(InventoryNumberField.getText())); }
 				catch (NumberFormatException except) { throw new Exception(StockErrors.INVALID_FIELD.toString(), new Throwable("Inventory Text Field")); }
				catch (Exception except) { throw new Exception(except); }
			
			try { part.setPrice(Double.parseDouble(PriceCostNumberField.getText())); }
				catch (NumberFormatException except) { throw new Exception(StockErrors.INVALID_FIELD.toString(), new Throwable("Price Text Field")); }
			
			
			
			if (selectedButton.getText().matches("Inhouse")) {
				try { ((Inhouse) part).setMachineID(Integer.parseInt(MachineID_CompanyNameTextField.getText())); }
					catch (NumberFormatException except) { throw new Exception(StockErrors.INVALID_FIELD.toString(), new Throwable("Machine ID Text Field")); }
			}
			else {
				((Outsourced) part).setcompanyName(MachineID_CompanyNameTextField.getText());
			}
			
			
			if (addModifyMode == AddModify.ADD) {
				model.getMainInventory().addPart(part);
			}
			else {
				model.getMainInventory().updatePart(part);
			}
			
			model.revertPanes();
			PartsListController plc = (PartsListController) model.getControllers("PartsListController");
			plc.sortAndRefreshView();
			
		}
		catch(Exception except) {
			if (except.toString().contains(StockErrors.MAX_TOO_LOW.toString())) {
				Dialogs.errorDialog(new String("Invalid Maximum Amount"), new String("You have entered"
						+ " an amount that is lower than the current minimum of " + part.getMin()
						+ ". Please enter a new amount."));
			}
			else if (except.toString().contains(StockErrors.MIN_TOO_HIGH.toString())) {
				Dialogs.errorDialog(new String("Invalid Minimum Amount"), new String("You have entered"
						+ " an amount that is higher than the current maximum of " + part.getMax()
						+ ". Please enter a new amount."));
			}
			else if (except.toString().contains(StockErrors.MIN_TOO_LOW.toString())) {
				Dialogs.errorDialog(new String("Invalid Minimum Amount"), new String("The minimum amount must"
						+ " be greater than or equal to 0."));
			}
			else if (except.toString().contains(StockErrors.INVALID_STOCK.toString())) {
				Dialogs.errorDialog(new String("Invalid Stock Amount"), new String("You have entered"
						+ " an amount that is outside of the range of " + part.getMin() + " and " + part.getMax()));
			}
			else if (except.toString().contains(StockErrors.INVALID_FIELD.toString())) {
				Dialogs.errorDialog(new String("Invlaid Field"), new String("The " 
						+ except.getCause().toString().substring(except.getCause().toString().indexOf(":") + 2) 
						+ " is not a number.  Please correct this and try again."));
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
	
	public void cancelButton() {
		if (Dialogs.confirmDialog("Cancel Part change?", "Do you wish to cancel all changes and return to the main screen?")) {
			model.revertPanes();
		}
	}

	public void setLabels() {
		RadioButton selectedButton = (RadioButton) InhouseOutSource.getSelectedToggle();
		if (selectedButton.getText().matches("Inhouse")) {
			MachineID_CompanyName_Label.setText("Machine ID");
		}
		else {
			MachineID_CompanyName_Label.setText("Company Name");
		}
			
	}

	public void setRecord(TableView<Part> partsTableView) {
		for (Part part : model.getMainInventory().getAllParts()) {
			if (part.getPartID() == partsTableView.getSelectionModel().getSelectedItem().getPartID()) {
				IDTextField.setText(String.valueOf(part.getPartID()));
				NameTextField.setText(part.getName());
				InventoryNumberField.setText(String.valueOf(part.getInStock()));
				PriceCostNumberField.setText(String.valueOf(part.getPrice()));
				MinNumberField.setText(String.valueOf(part.getMin()));
				MaxNumberField.setText(String.valueOf(part.getMax()));
				try {
					MachineID_CompanyNameTextField.setText(String.valueOf( ((Inhouse) part).getMachineID()) );
					Inhouse.setSelected(true);
				}
				catch (Exception e) {
					Outsourced.setSelected(true);
					MachineID_CompanyName_Label.setText("Company Name");
					MachineID_CompanyNameTextField.setText( ((Outsourced) part).getCompanyName());
				}
				break;
			}
		}
		
	}
}
