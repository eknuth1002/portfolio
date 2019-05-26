package scheduler;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import scheduler.CustomExceptions.Exceptions.*;
import java.util.Optional;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.StageStyle;
import javafx.util.Pair;


public class LoginController {
   
    public LoginController () { 
        Dialog<Pair<String, String>> loginDialog = new Dialog<>();
        loginDialog.getDialogPane().getStylesheets().add(getClass().getResource("scheduler/dialog.css").toExternalForm());
        
        loginDialog.setHeaderText(Start.getResourceBundleString("loginTitle"));
        ButtonType loginButton = new ButtonType(Start.getResourceBundleString("submit"), ButtonData.OK_DONE);
        ButtonType clearButton = new ButtonType(Start.getResourceBundleString("clear"), ButtonData.APPLY);
        ButtonType exitButton = new ButtonType(Start.getResourceBundleString("exit"), ButtonData.OTHER);
        loginDialog.getDialogPane().getButtonTypes().addAll(loginButton, clearButton, exitButton);
        loginDialog.initStyle(StageStyle.UNDECORATED);
        
        Node loginButtonNode = loginDialog.getDialogPane().lookupButton(loginButton);
        loginButtonNode.setDisable(true);
        GridPane loginGrid = new GridPane();
        TextField userName = new TextField();
        userName.setPromptText(Start.getResourceBundleString("userName"));
        PasswordField password = new PasswordField();
        password.setPromptText(Start.getResourceBundleString("password"));
        
        loginGrid.add(new Label(Start.getResourceBundleString("userName").concat(": ")), 0, 0);
        loginGrid.add(userName, 0, 1);
        loginGrid.add(new Label(Start.getResourceBundleString("password").concat(": ")), 1, 0);
        loginGrid.add(password, 1, 1);
                
        loginDialog.getDialogPane().setContent(loginGrid);
        
        
        userName.textProperty().addListener((observable, oldValue, newValue) -> loginButtonNode.setDisable(newValue.trim().isEmpty()));
       
        loginDialog.setResultConverter(button -> { 
            if (button.equals(loginButton)) return new Pair<>(userName.getText(), password.getText());
            if (button.equals(clearButton)) userName.clear(); password.clear();
            if (button.equals(exitButton)) System.exit(0);
            return null; 
        });
        
        while (DatabaseManager.isUserLoggedIn() == false) {
            Optional<Pair<String, String>> result = loginDialog.showAndWait();

            result.ifPresent(userNameAndPassword -> {
                Path userLogins = Paths.get("userLogins.txt");

                try {
                    if (DatabaseManager.verifyUsernameAndPassword(new ArrayList<String> (Arrays.asList(userNameAndPassword.getKey(), userNameAndPassword.getValue())) ) ) {

                        if (!Files.exists(userLogins)) {
                            try {
                                Files.createFile(userLogins);
                            } catch (IOException ex) {
                                Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }

                        try (BufferedWriter writer = Files.newBufferedWriter(userLogins, Charset.defaultCharset(), StandardOpenOption.APPEND)) {
                            writer.append("User \"" + userNameAndPassword.getKey() + "\" logged in on " + Calendar.getInstance().getTime().toString());
                            writer.newLine();
                        } catch (IOException ex) {
                            Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
                        }

                        DatabaseManager.setUser(userNameAndPassword.getKey());

                        ((MainFXMLController) Start.controllers.get("MainScreen")).loadMainScreen();
                    }
                    else {
                       throw new InvalidUsernameorPassword(Start.getResourceBundleString("dialogErrorTitle"), Start.getResourceBundleString("dialogWrongPassword"));

                    }
                } catch (SQLException ex) {
                    Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
                }
                catch (InvalidUsernameorPassword ex) {
                    if (!Files.exists(userLogins)) {
                            try {
                                Files.createFile(userLogins);
                            } catch (IOException ioEx) {
                                Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ioEx);
                            }
                        }

                        try (BufferedWriter writer = Files.newBufferedWriter(userLogins, Charset.defaultCharset(), StandardOpenOption.APPEND)) {
                            writer.write("Failed Login attempt using user name \"" + userNameAndPassword.getKey() + "\" on " + Calendar.getInstance().getTime().toString());
                            writer.newLine();
                        } catch (IOException ioEx) {
                            Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ioEx);
                        }
                }
            });
        }
    }   
}
