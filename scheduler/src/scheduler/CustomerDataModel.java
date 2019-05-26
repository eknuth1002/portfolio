/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author Elliot Knuth
 */
public class CustomerDataModel {
    private SimpleStringProperty customerName;
    private SimpleStringProperty address;
    private SimpleStringProperty address2;
    private SimpleStringProperty city;
    private SimpleStringProperty country;
    private SimpleStringProperty postalCode;
    private SimpleStringProperty phone;
    
    private SimpleIntegerProperty customerId;
    private SimpleIntegerProperty addressId;
    private SimpleIntegerProperty cityId;
    private SimpleIntegerProperty countryId;
    
    public CustomerDataModel(String customerName, int customerId, String address, String address2, int addressID, 
                             String city, int cityId, String country, int countryId, String postalCode, String phone) {
        this.customerName = new SimpleStringProperty(customerName);
        this.customerId = new SimpleIntegerProperty(customerId);
        this.address = new SimpleStringProperty(address);
        this.address2 = new SimpleStringProperty(address2);
        this.addressId = new SimpleIntegerProperty(addressID);
        this.city = new SimpleStringProperty(city);
        this.cityId = new SimpleIntegerProperty(cityId);
        this.country = new SimpleStringProperty(country);
        this.countryId = new SimpleIntegerProperty(countryId);
        this.postalCode = new SimpleStringProperty(postalCode);
        this.phone = new SimpleStringProperty(phone);
    }

    void setCustomerName(String customerName) {
        this.customerName.set(customerName);
    }

    void setAddress(String address) {
        this.address.set(address);
    }

    void setAddress2(String address2) {
        this.address2.set(address2);
    }

    void setCity(String city) {
        this.city.set(city);
    }

    void setCountry(String country) {
        this.country.set(country);
    }

    void setPostalCode(String postalCode) {
        this.postalCode.set(postalCode);
    }

    void setPhone(String phone) {
        this.phone.set(phone);
    }

    void setCustomerId(int customerId) {
        this.customerId.set(customerId);
    }

    void setAddressId(int addressId) {
        this.addressId.set(addressId);
    }

    void setCityId(int cityId) {
        this.cityId.set(cityId);
    }

    void setCountryId(Integer countryId) {
        this.countryId.set(countryId);
    }

    public StringProperty getCustomerName() {
        return customerName;
    }

    public StringProperty getAddress() {
        return address;
    }

    public StringProperty getAddress2() {
        return address2;
    }

    public StringProperty getCity() {
        return city;
    }

    public StringProperty getCountry() {
        return country;
    }

    public StringProperty getPostalCode() {
        return postalCode;
    }

    public StringProperty getPhone() {
        return phone;
    }

    public IntegerProperty getCustomerId() {
        return customerId;
    }

    public IntegerProperty getAddressId() {
        return addressId;
    }

    public IntegerProperty getCityId() {
        return cityId;
    }

    public IntegerProperty getCountryId() {
        return countryId;
    }

    public String getCustomerNameAsString() {
        return customerName.get();
    }

    public String getAddressAsString() {
        return address.get();
    }

    public String getAddress2AsString() {
        return address2.get();
    }

    public String getCityAsString() {
        return city.get();
    }

    public String getCountryAsString() {
        return country.get();
    }

    public String getPostalCodeAsString() {
        return postalCode.get();
    }

    public String getPhoneAsString() {
        return phone.get();
    }

    public Integer getCustomerIdAsInteger() {
        return customerId.get();
    }

    public Integer getAddressIdAsInteger() {
        return addressId.get();
    }

    public Integer getCityIdAsInteger() {
        return cityId.get();
    }

    public Integer getCountryIdAsInteger() {
        return countryId.get();
    }
    
}
