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
public class AppointmentDataModel {
    private SimpleIntegerProperty appointmentId;
    private SimpleIntegerProperty customerId;
    private SimpleStringProperty title;
    private SimpleStringProperty description;
    private SimpleStringProperty location;
    private SimpleStringProperty contact;
    private SimpleStringProperty url;
    private SimpleStringProperty start;
    private SimpleStringProperty end;
    
    public AppointmentDataModel(int appointmentId, int customerId, String title, String description,  
                             String location, String contact, String url, String start, String end) {
        this.appointmentId = new SimpleIntegerProperty(appointmentId);
        this.customerId = new SimpleIntegerProperty(customerId);
        this.title = new SimpleStringProperty(title);
        this.description = new SimpleStringProperty(description);
        this.location = new SimpleStringProperty(location);
        this.contact = new SimpleStringProperty(contact);
        this.url = new SimpleStringProperty(url);
        this.start = new SimpleStringProperty(start);
        this.end = new SimpleStringProperty(end);
    }

    void setAppointmentId(int appointmentId) {
        this.appointmentId.set(appointmentId);
    }

    void setCustomerId(int customerId) {
        this.customerId.set(customerId);
    }

    void setTitle(String title) {
        this.title.set(title);
    }

    void setDescription(String description) {
        this.description.set(description);
    }

    void setLocation(String location) {
        this.location.set(location);
    }

    void setContact(String contact) {
        this.contact.set(contact);
    }

    void setUrl(String url) {
        this.url.set(url);
    }

    void setStart(String start) {
        this.start.set(start);
    }

    void setEnd(String end) {
        this.end.set(end);
    }

    public IntegerProperty getAppointmentId() {
        return appointmentId;
    }
    
    public StringProperty getDate() {
        return new SimpleStringProperty(start.getValue().substring(0, start.getValue().indexOf(" ")));
    }
    
    public StringProperty getTime() {
        return new SimpleStringProperty(start.getValue().substring(start.getValue().indexOf(" ") + 1).concat(" - ").concat(end.getValue().substring(end.getValue().indexOf(" ") + 1)));
    }

    public IntegerProperty getCustomerId() {
        return customerId;
    }

    public StringProperty getTitle() {
        return title;
    }

    public StringProperty getDescription() {
        return description;
    }

    public StringProperty getLocation() {
        return location;
    }

    public StringProperty getContact() {
        return contact;
    }
    
    public StringProperty getURL() {
        return url;
    }

    public StringProperty getStart() {
        return start;
    }

    public StringProperty getEnd() {
        return end;
    }
}
