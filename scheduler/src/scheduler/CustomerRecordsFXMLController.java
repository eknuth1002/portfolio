/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

/**
 * FXML Controller class
 *
 * @author Elliot Knuth
 */
public class CustomerRecordsFXMLController implements Initializable {

    @FXML
    private TabPane customerTabPane;
    @FXML
    private AnchorPane customerAnchorPane;
    @FXML
    private GridPane addCustomerGridPane;
    @FXML
    private GridPane modifyCustomerGridPane;
    @FXML
    private AnchorPane addCustomerTab;
    @FXML
    private AnchorPane modifyCustomerTab;

    @FXML
    private TextField addNameField;
    private final ObservableMap<Integer, String> nameMap = FXCollections.observableMap(new LinkedHashMap<>());
   
    @FXML
    private TextField addAddressField;
    private final ObservableMap<Integer, String> addressMap = FXCollections.observableMap(new LinkedHashMap<>());
    
    @FXML
    private TextField addAddress2Field;
    private final ObservableMap<Integer, String> address2Map = FXCollections.observableMap(new LinkedHashMap<>());
    
    @FXML
    private TextField addCityField;
    private final ObservableMap<Integer, String> cityMap = FXCollections.observableMap(new LinkedHashMap<>());
    
    @FXML
    private TextField addPostalCodeField;
    private final ObservableMap<Integer, String> postalCodeMap = FXCollections.observableMap(new LinkedHashMap<>());
    
    @FXML
    private TextField addCountryField;
    private final ObservableMap<Integer, String> countryMap = FXCollections.observableMap(new LinkedHashMap<>());
    
    @FXML
    private TextField addPhoneField;
    private final ObservableMap<Integer, String> phoneMap = FXCollections.observableMap(new LinkedHashMap<>());
    
    @FXML
    private TextField modifyNameField;
    @FXML
    private TextField modifyAddressField;
    @FXML
    private TextField modifyAddress2Field;
    @FXML
    private TextField modifyCityField;
    @FXML
    private TextField modifyCountryField;
    @FXML
    private TextField modifyPostalCodeField;

    @FXML
    private Button addCustomerButton;
    @FXML
    private Button updateButton;

    final ObservableList<CustomerDataModel> customerData = FXCollections.observableArrayList();

    @FXML
    private TableView<CustomerDataModel> customerRecordsTable;
    @FXML
    private TableColumn<CustomerDataModel, Integer> customerIDColumn;
    @FXML
    private TableColumn<CustomerDataModel, String> customerNameColumn;

    @FXML
    private Label customerNameLabel;
    @FXML
    private Label addressLabel;
    @FXML
    private Label address2Label;
    @FXML
    private Label cityLabel;
    @FXML
    private Label postalCodeLabel;
    @FXML
    private Label countryLabel;
    @FXML
    private Label modifyNameLabel;
    @FXML
    private Label modifyAddressLabel;
    @FXML
    private Label modifyAddress2Label;
    @FXML
    private Label modifyCityLabel;
    @FXML
    private Label modifyCountryLabel;
    @FXML
    private Label modifyPostalCodeLabel;
    @FXML
    private Tab addCustomerTabLabel;
    @FXML
    private Tab modifyCustomerTabLabel;
    @FXML
    private Label phoneNumberLabel;
    @FXML
    private Label modifyPhoneNumberLabel;
    @FXML
    private TextField modifyPhoneNumberField;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        customerNameLabel.setText(Start.getResourceBundleString("name"));
        addressLabel.setText(Start.getResourceBundleString("address"));
        address2Label.setText(Start.getResourceBundleString("address2"));
        cityLabel.setText(Start.getResourceBundleString("city"));
        postalCodeLabel.setText(Start.getResourceBundleString("postalCode"));
        countryLabel.setText(Start.getResourceBundleString("country"));
        phoneNumberLabel.setText(Start.getResourceBundleString("phone"));
        addCustomerButton.setText(Start.getResourceBundleString("addCustomer"));
        updateButton.setText(Start.getResourceBundleString("updateCustomer"));
        modifyNameLabel.setText(Start.getResourceBundleString("name"));
        modifyAddressLabel.setText(Start.getResourceBundleString("address"));
        modifyAddress2Label.setText(Start.getResourceBundleString("address2"));
        modifyCityLabel.setText(Start.getResourceBundleString("city"));
        modifyPostalCodeLabel.setText(Start.getResourceBundleString("postalCode"));
        modifyCountryLabel.setText(Start.getResourceBundleString("country"));
        modifyPhoneNumberLabel.setText(Start.getResourceBundleString("phone"));
        addCustomerButton.setText(Start.getResourceBundleString("addCustomer"));
        updateButton.setText(Start.getResourceBundleString("updateCustomer"));
        modifyCustomerTabLabel.setText(Start.getResourceBundleString("modifyCustomer"));
        addCustomerTabLabel.setText(Start.getResourceBundleString("addCustomer"));

        customerTabPane.prefWidthProperty().bind(customerAnchorPane.widthProperty().subtract(30));
        customerTabPane.maxWidthProperty().bind(customerAnchorPane.widthProperty().subtract(30));

        addCustomerTab.prefWidthProperty().bind(customerAnchorPane.widthProperty().subtract(30));
        addCustomerTab.maxWidthProperty().bind(customerAnchorPane.widthProperty().subtract(30));

        addCustomerGridPane.prefWidthProperty().bind(addCustomerTab.widthProperty().subtract(30));
        addCustomerGridPane.maxWidthProperty().bind(addCustomerTab.widthProperty().subtract(30));

        /*

    Start of ComboBox filler block for Add tab and customerData model

         */
        try {
            ResultSet results = DatabaseManager.populateCustomerMaps();

            while (results.next()) {
                if (results.getString("customerId") != null) {
                    nameMap.put(Integer.parseInt(results.getString("customerId")), results.getString("customerName"));
                }
                if (results.getString("addressId") != null) {
                    addressMap.put(Integer.parseInt(results.getString("addressId")), results.getString("address"));
                    address2Map.put(Integer.parseInt(results.getString("addressId")), results.getString("address2"));
                    postalCodeMap.put(Integer.parseInt(results.getString("addressId")), results.getString("postalCode"));
                    phoneMap.put(Integer.parseInt(results.getString("addressId")), results.getString("phone"));
                }
                if (results.getString("cityId") != null) {
                    cityMap.put(Integer.parseInt(results.getString("cityId")), results.getString("city"));
                }
                if (results.getString("countryId") != null) {
                    countryMap.put(Integer.parseInt(results.getString("countryId")), results.getString("country"));
                }

                
                if (results.getString("customerId") != null && results.getString("addressID") != null
                        && results.getString("cityId") != null && results.getString("countryId") != null) {

                    customerData.add(new CustomerDataModel(results.getString("customerName"),
                            Integer.parseInt(results.getString("customerId")),
                            results.getString("address"),
                            results.getString("address2"),
                            Integer.parseInt(results.getString("addressID")),
                            results.getString("city"),
                            Integer.parseInt(results.getString("cityId")),
                            results.getString("country"),
                            Integer.parseInt(results.getString("countryId")),
                            results.getString("postalCode"),
                            results.getString("phone")));
                }

            }
           
            
        } catch (SQLException ex) {
            Logger.getLogger(CustomerRecordsFXMLController.class.getName()).log(Level.SEVERE, null, ex);
        }
        /*

    End of ComboBox filler block for Add Tab and customerData model

         */
        customerData.sort((CustomerDataModel value1, CustomerDataModel value2) -> value1.getCustomerIdAsInteger() < value2.getCustomerIdAsInteger() ? -1 : 1); 
        
        customerRecordsTable.setPlaceholder(new Label("There are no appointments matching the criteria."));
        customerRecordsTable.setItems(customerData);
        customerIDColumn.setText("ID");
        
        customerIDColumn.setSortType(TableColumn.SortType.DESCENDING);
        customerIDColumn.setCellValueFactory(cellData -> cellData.getValue().getCustomerId().asObject());
        customerNameColumn.setText("Name");
        customerNameColumn.setCellValueFactory(cellData -> cellData.getValue().getCustomerName());
        

    } //End of initalize

    private void clearCustomer() {
        addCustomerGridPane.getChildren().forEach(node -> { if (node instanceof TextField) ((TextField) node).setText(""); });
    }

    @FXML
    public void addCustomer() {
        final Map<String, String> valuesForPreparedStatement = new LinkedHashMap<>();
        String preparedStatement = "";
        dataVerifier verifier = new dataVerifier();

        //Check for Country and add if not in list
        if (verifier.checkField(addCountryField) && !countryMap.containsValue(addCountryField.getText())) {

            valuesForPreparedStatement.put(addCountryField.getId(), addCountryField.getText());
            preparedStatement = "INSERT INTO country (country, createDate, createdBy, lastUpdate, lastUpdateBy) VALUES"
                    + "(?, NOW(), ?, NOW(), ?)";

            try {
                DatabaseManager.add(valuesForPreparedStatement, preparedStatement);
                countryMap.put(countryMap.size() + 1, addCountryField.getText());
            } catch (SQLException ex) {
                Logger.getLogger(CustomerRecordsFXMLController.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                valuesForPreparedStatement.clear();
                preparedStatement = "";
            }
        }

        //Check for City and add if not in list
        if (verifier.checkField(addCityField) && !cityMap.containsValue(addCityField.getText())) {

            valuesForPreparedStatement.put(addCountryField.getId(), addCountryField.getText());
            valuesForPreparedStatement.put(addCityField.getId(), addCityField.getText());
            preparedStatement = "INSERT INTO city (countryId, city, createDate, createdBy, lastUpdate, lastUpdateBy) VALUES"
                    + "((SELECT countryId FROM country WHERE country = ? LIMIT 1), ?,  NOW(), ?, NOW(), ?)";

            try {
                DatabaseManager.add(valuesForPreparedStatement, preparedStatement);
                cityMap.put(cityMap.size() + 1, addCityField.getText());
            } catch (SQLException ex) {
                Logger.getLogger(CustomerRecordsFXMLController.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                valuesForPreparedStatement.clear();
                preparedStatement = "";
            }

        }

        Supplier<Boolean> checkIfAddressFieldsSameKey = () -> {
            for (CustomerDataModel addressModel : customerData) {

                if (verifier.checkField(addAddressField)
                        && verifier.checkField(addPostalCodeField)
                        && verifier.checkField(addPhoneField)
                        && addAddressField.getText().equals(addressModel.getAddressAsString())
                        && addAddress2Field.getText().equals(addressModel.getAddress2AsString())
                        && addPostalCodeField.getText().equals(addressModel.getPostalCodeAsString())
                        && addPhoneField.getText().equals(addressModel.getPhoneAsString())) {
                    return true;

                }
            }
            return false;
        };

        //Check for Address and Postal Code and add if not in list
        if (addAddress2Field.getText() == null) {
            addAddress2Field.setText("");
        }

        if (!checkIfAddressFieldsSameKey.get()) {

            valuesForPreparedStatement.put(addCityField.getId(), addCityField.getText());
            valuesForPreparedStatement.put(addAddressField.getId(), addAddressField.getText());
            valuesForPreparedStatement.put(addAddress2Field.getId(), addAddress2Field.getText());
            valuesForPreparedStatement.put(addPostalCodeField.getId(), addPostalCodeField.getText());
            valuesForPreparedStatement.put(addPhoneField.getId(), addPhoneField.getText());
            preparedStatement = "INSERT INTO address (cityId, address, address2, postalCode, phone, createDate, createdBy, lastUpdate, lastUpdateBy) VALUES"
                    + "((SELECT cityId FROM city WHERE city = ? LIMIT 1), ?, ? , ?, ?, NOW(), ?, NOW(), ?)";

            try {
                DatabaseManager.add(valuesForPreparedStatement, preparedStatement);
                addressMap.put(addressMap.size() + 1, addAddressField.getText());
                address2Map.put(address2Map.size() + 1, addAddress2Field.getText());
                postalCodeMap.put(postalCodeMap.size() + 1, addPostalCodeField.getText());
            } catch (SQLException ex) {
                Logger.getLogger(CustomerRecordsFXMLController.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                valuesForPreparedStatement.clear();
                preparedStatement = "";
            }
        }

        //Check for Name and add if not in list
        if (verifier.checkField(addNameField) && !nameMap.containsValue(addNameField.getText())) {

            valuesForPreparedStatement.put(addAddressField.getId(), addAddressField.getText());
            valuesForPreparedStatement.put(addAddress2Field.getId(), addAddress2Field.getText());
            valuesForPreparedStatement.put(addPostalCodeField.getId(), addPostalCodeField.getText());
            valuesForPreparedStatement.put(addPhoneField.getId(), addPhoneField.getText());
            valuesForPreparedStatement.put(addNameField.getId(), addNameField.getText());

            preparedStatement = "INSERT INTO customer (addressId, customerName, active, createDate, createdBy, lastUpdate, lastUpdateBy) VALUES"
                    + "((SELECT addressId FROM address WHERE address = ? AND address2 = ? AND postalCode = ? AND phone = ? LIMIT 1), ?, 1, NOW(), ?, NOW(), ?)";

            try {
                DatabaseManager.add(valuesForPreparedStatement, preparedStatement);
                nameMap.put(nameMap.size() + 1, addNameField.getText());
            } catch (SQLException ex) {
                Logger.getLogger(CustomerRecordsFXMLController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        try {
        ResultSet result = DatabaseManager.searchForCustomer(String.valueOf(customerData.size()+1));

        if (result.next()){
            if (result.getString("customerId") != null && result.getString("addressID") != null
                    && result.getString("cityId") != null && result.getString("countryId") != null) {

                customerData.add(new CustomerDataModel(result.getString("customerName"),
                        Integer.parseInt(result.getString("customerId")),
                        result.getString("address"),
                        result.getString("address2"),
                        Integer.parseInt(result.getString("addressID")),
                        result.getString("city"),
                        Integer.parseInt(result.getString("cityId")),
                        result.getString("country"),
                        Integer.parseInt(result.getString("countryId")),
                        result.getString("postalCode"),
                        result.getString("phone")));
            }
        }
        } catch(SQLException e) {
            
        }
        try {
            ResultSet results = DatabaseManager.populateCustomerMaps();
            
            while (results.next()) {
                if (results.getString("customerId") != null && Integer.valueOf(results.getString("customerId")) == customerData.size()) {
                    nameMap.put(Integer.parseInt(results.getString("customerId")), results.getString("customerName"));
                    addressMap.put(Integer.parseInt(results.getString("addressId")), results.getString("address"));
                    address2Map.put(Integer.parseInt(results.getString("addressId")), results.getString("address2"));
                    postalCodeMap.put(Integer.parseInt(results.getString("addressId")), results.getString("postalCode"));
                    phoneMap.put(Integer.parseInt(results.getString("addressId")), results.getString("phone"));
                    cityMap.put(Integer.parseInt(results.getString("cityId")), results.getString("city"));
                    countryMap.put(Integer.parseInt(results.getString("countryId")), results.getString("country"));

                    customerData.add(new CustomerDataModel(results.getString("customerName"),
                            Integer.parseInt(results.getString("customerId")),
                            results.getString("address"),
                            results.getString("address2"),
                            Integer.parseInt(results.getString("addressID")),
                            results.getString("city"),
                            Integer.parseInt(results.getString("cityId")),
                            results.getString("country"),
                            Integer.parseInt(results.getString("countryId")),
                            results.getString("postalCode"),
                            results.getString("phone")));
                }

            }
           
            
        } catch (SQLException ex) {
            Logger.getLogger(CustomerRecordsFXMLController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        clearCustomer();
        customerTabPane.getSelectionModel().select(0);
        
    }

    @FXML
    public void modifyCustomer() {
        
        CustomerDataModel customer = customerRecordsTable.getSelectionModel().getSelectedItem();
        final Map<String, String> valuesForPreparedStatement = new LinkedHashMap<>();
        String preparedStatement;
        dataVerifier verifier = new dataVerifier();

        if (verifier.checkField(modifyCountryField)) {
            if (!countryMap.containsValue(modifyCountryField.getText())) {
                preparedStatement = "INSERT INTO country (country, createDate, createdBy, lastUpdate, lastUpdateBy) "
                        + "?, NOW(), ?, NOW(), ?";
                valuesForPreparedStatement.put(modifyCountryField.getId(), modifyCountryField.getText());
                try {
                    DatabaseManager.add(valuesForPreparedStatement, preparedStatement);
                    countryMap.put(countryMap.size() + 1, modifyCountryField.getText());
                    customer.setCountryId(countryMap.size());
                    customer.setCountry(modifyCountryField.getText());
                } catch (SQLException ex) {

                } finally {
                    valuesForPreparedStatement.clear();
                }
            } else {
                customer.setCountry(modifyCountryField.getText());
            }
        }

        if (verifier.checkField(modifyCityField)) {
            if (!cityMap.containsValue(modifyCityField.getText())) {
                valuesForPreparedStatement.put("countryId", modifyCountryField.getText());
                valuesForPreparedStatement.put(modifyCityField.getId(), modifyCityField.getText());

                preparedStatement = "INSERT INTO city (countryId, city, createDate, createdBy, lastUpdate, lastUpdateBy) VALUES"
                        + "((SELECT countryId FROM country WHERE country = ?), ?,  NOW(), ?, NOW(), ?)";
                try {
                    DatabaseManager.add(valuesForPreparedStatement, preparedStatement);
                    cityMap.put(cityMap.size() + 1, modifyCityField.getText());
                    customer.setCityId(cityMap.size());
                    customer.setCity(modifyCityField.getText());
                } catch (SQLException ex) {

                } finally {
                    valuesForPreparedStatement.clear();
                }
            } else {
                customer.setCity(modifyCityField.getText());
            }
        }
        
        Supplier<Boolean> checkIfAddressFieldsSameKey = () -> {
            for (CustomerDataModel addressModel : customerData) {

                if (verifier.checkField(modifyAddressField)
                        && verifier.checkField(modifyPostalCodeField)
                        && verifier.checkField(modifyPhoneNumberField)
                        && modifyAddressField.getText().equals(addressModel.getAddressAsString())
                        && modifyAddress2Field.getText().equals(addressModel.getAddress2AsString())
                        && modifyPostalCodeField.getText().equals(addressModel.getPostalCodeAsString())
                        && modifyPhoneNumberField.getText().equals(addressModel.getPhoneAsString())) {
                    return true;

                }
            }
            return false;
        };

        if (verifier.checkField(modifyAddressField) && verifier.checkField(modifyPostalCodeField)) {
            if (modifyAddress2Field.getText() == null) {
                modifyAddress2Field.setText("");
            }
            if (!checkIfAddressFieldsSameKey.get()) {

                valuesForPreparedStatement.put("cityId", customer.getCityIdAsInteger().toString());
                valuesForPreparedStatement.put(modifyAddressField.getId(), modifyAddressField.getText());
                valuesForPreparedStatement.put(modifyAddress2Field.getId(), modifyAddress2Field.getText());
                valuesForPreparedStatement.put(modifyPostalCodeField.getId(), modifyPostalCodeField.getText());
                valuesForPreparedStatement.put(modifyPhoneNumberField.getId(), modifyPhoneNumberField.getText());
                preparedStatement = "INSERT INTO address (cityId, address, address2, postalCode, phone, createDate, createdBy, lastUpdate, lastUpdateBy) VALUES"
                        + "(?, ?, ?, ?, ?, NOW(), ?, NOW(), ?)";

                try {
                    DatabaseManager.add(valuesForPreparedStatement, preparedStatement);
                    addressMap.put(addressMap.size() + 1, modifyAddressField.getText());
                    address2Map.put(address2Map.size() + 1, modifyAddress2Field.getText());
                    postalCodeMap.put(postalCodeMap.size() + 1, modifyPostalCodeField.getText());
                    customer.setCustomerName(modifyNameField.getText());
                    customer.setAddressId(addressMap.size());
                    customer.setAddress(modifyAddressField.getText());
                    customer.setAddress2(modifyAddress2Field.getText());
                    customer.setPostalCode(modifyPostalCodeField.getText());
                    customer.setPhone(modifyPhoneNumberField.getText());
                } catch (SQLException ex) {
                    Logger.getLogger(CustomerRecordsFXMLController.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    valuesForPreparedStatement.clear();
                }
            } else {
                customer.setCustomerName(modifyNameField.getText());
                
                
                addressloop:
                for (int addressKey : addressMap.keySet()) {
                    for (int address2Key : address2Map.keySet()) {
                        for (int postalCodeKey : postalCodeMap.keySet()) {
                            for (int phoneKey : phoneMap.keySet()) {
                                if (addressKey == address2Key && address2Key == postalCodeKey && postalCodeKey == phoneKey
                                        && modifyAddressField.getText().equals(addressMap.get(addressKey))
                                        && modifyAddress2Field.getText().equals(address2Map.get(address2Key))
                                        && modifyPostalCodeField.getText().equals(postalCodeMap.get(postalCodeKey))
                                        && modifyPhoneNumberField.getText().equals(phoneMap.get(phoneKey))) {
                                    customer.setAddressId(addressKey);
                                    customer.setAddress(addressMap.get(addressKey));
                                    customer.setAddress2(address2Map.get(address2Key));
                                    customer.setPostalCode(postalCodeMap.get(postalCodeKey));
                                    customer.setPhone(phoneMap.get(phoneKey));
                                    break addressloop;
                                }
                            }
                        }
                    }
                }
            }
        }



        //Update all fields from Customer
        valuesForPreparedStatement.put("customerName", customer.getCustomerNameAsString());
        valuesForPreparedStatement.put("addressId", customer.getAddressIdAsInteger().toString());
        valuesForPreparedStatement.put("customerId", customer.getCustomerIdAsInteger().toString());
        preparedStatement = "UPDATE customer SET customerName = ?, addressId = ?, lastUpdate = NOW(), lastUpdateBy = ? WHERE customerId = ?";
        try {
            DatabaseManager.update(valuesForPreparedStatement, preparedStatement);
        } catch (SQLException ex) {

        }
            customerData.set(customer.getCustomerIdAsInteger()-1, customer);
            valuesForPreparedStatement.clear();
        
            
        

    }

    @FXML
    public void populateModifyTab() {
        CustomerDataModel currentSelected = customerRecordsTable.getSelectionModel().getSelectedItem();

        modifyNameField.setText(currentSelected.getCustomerNameAsString());
        modifyAddressField.setText(currentSelected.getAddressAsString());
        modifyAddress2Field.setText(currentSelected.getAddress2AsString());
        modifyCityField.setText(currentSelected.getCityAsString());
        modifyCountryField.setText(currentSelected.getCountryAsString());
        modifyPostalCodeField.setText(currentSelected.getPostalCodeAsString());
        modifyPhoneNumberField.setText(currentSelected.getPhoneAsString());
    }
}
