package scheduler;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import scheduler.CustomExceptions.Exceptions.*;

/**
 * FXML Controller class
 *
 * @author Elliot Knuth
 */
public class LoginFXMLController implements Initializable {

    @FXML
    private TextField UserName;
    @FXML
    private TextField Password;
    @FXML
    private Label userNameLabel;
    @FXML
    private Label passwordLabel;
    @FXML
    private Button submitButton;
    @FXML
    private Button clearButton;
    
    @FXML
    private GridPane LoginPane;
    @FXML
    private Label title;
    

    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        userNameLabel.setText(Start.getResourceBundleString("userName"));
        passwordLabel.setText(Start.getResourceBundleString("password"));
        submitButton.setText(Start.getResourceBundleString("submit"));
        clearButton.setText(Start.getResourceBundleString("clear"));
        title.setText(Start.getResourceBundleString("loginTitle"));
        
    }
    
    void setSize(int width, int height) {
        LoginPane.setPrefSize(width, height);
    }
   
    @FXML
    public void clearFields(){
        UserName.clear();
        Password.clear();
    }
    
    @FXML
    public void verifyUsernameAndPassword(){
        Path userLogins = Paths.get("userLogins.txt");
        
        try {
            if (DatabaseManager.verifyUsernameAndPassword(new ArrayList<String>(Arrays.asList(UserName.getText(), Password.getText()) ))) {
               
                if (!Files.exists(userLogins)) {
                    try {
                        Files.createFile(userLogins);
                    } catch (IOException ex) {
                        Logger.getLogger(LoginFXMLController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                
                try (BufferedWriter writer = Files.newBufferedWriter(userLogins, Charset.defaultCharset(), StandardOpenOption.APPEND)) {
                    writer.append("User \"" + UserName.getText() + "\" logged in on " + Calendar.getInstance().getTime().toString());
                    writer.newLine();
                } catch (IOException ex) {
                    Logger.getLogger(LoginFXMLController.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                DatabaseManager.setUser(UserName.getText());
                
                ((MainFXMLController) Start.controllers.get("MainScreen")).loadMainScreen();
            }
            else {
               throw new InvalidUsernameorPassword(Start.getResourceBundleString("dialogErrorTitle"), Start.getResourceBundleString("dialogWrongPassword"));
                
            }
        } catch (SQLException ex) {
            Logger.getLogger(LoginFXMLController.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (InvalidUsernameorPassword ex) {
            if (!Files.exists(userLogins)) {
                    try {
                        Files.createFile(userLogins);
                    } catch (IOException ioEx) {
                        Logger.getLogger(LoginFXMLController.class.getName()).log(Level.SEVERE, null, ioEx);
                    }
                }
                
                try (BufferedWriter writer = Files.newBufferedWriter(userLogins, Charset.defaultCharset(), StandardOpenOption.APPEND)) {
                    writer.write("Failed Login attempt using user name \"" + UserName.getText() + "\" on " + Calendar.getInstance().getTime().toString());
                    writer.newLine();
                } catch (IOException ioEx) {
                    Logger.getLogger(LoginFXMLController.class.getName()).log(Level.SEVERE, null, ioEx);
                }
                
                

        }
    }   
    
}
