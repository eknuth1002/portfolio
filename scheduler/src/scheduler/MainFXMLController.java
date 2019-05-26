/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;


/**
 * FXML Controller class
 *
 * @author Elliot Knuth
 */
public class MainFXMLController implements Initializable {
    @FXML
    public AnchorPane MainPane;
    @FXML
    private TabPane mainScreen;
    @FXML
    private Tab customersTab;
    @FXML
    private Tab appointmentsTab;
    @FXML
    private Tab reportsTab;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        customersTab.setText(Start.getResourceBundleString("customerDatabase"));
        appointmentsTab.setText(Start.getResourceBundleString("appointments"));
        mainScreen.prefWidthProperty().bind(MainPane.widthProperty());
        mainScreen.maxWidthProperty().bind(MainPane.widthProperty());
    }

    public void loadMainScreen() {
        
        StackTraceElement[] tracer = new Throwable().getStackTrace();
        if (tracer[1].getClassName().equals("src.LoginController")) {
            try {
                mainScreen.setVisible(true);
 
                FXMLLoader fxmlLoader;
                fxmlLoader = new FXMLLoader(getClass().getResource("CustomerRecordsFXML.fxml"));
                customersTab.setContent(fxmlLoader.load());
                Start.controllers.put("Customers", fxmlLoader.getController());
                
                fxmlLoader = new FXMLLoader(getClass().getResource("AppointmentsFXML.fxml"));
                appointmentsTab.setContent(fxmlLoader.load());
                Start.controllers.put("Appointments", fxmlLoader.getController());
                
                fxmlLoader = new FXMLLoader(getClass().getResource("ReportsFXML.fxml"));
                reportsTab.setContent(fxmlLoader.load());
                Start.controllers.put("Reports", fxmlLoader.getController());
            } catch (IOException ex) {
               Logger.getLogger(MainFXMLController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }


    
    
}
