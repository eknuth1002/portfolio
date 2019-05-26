/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler;

import java.sql.Time;
import scheduler.CustomExceptions.Exceptions.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.Optional;
import java.util.function.Predicate;
import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import javafx.util.StringConverter;
/**
 *
 * @author Elliot Knuth
 */
public class dataVerifier {
    private Dialog<String> fieldDialog = new Dialog<>();
    private Optional<String> result = null;
    private Node fieldButtonNode ;
    private GridPane dialogGrid = new GridPane();
    private TextField fieldValue = new TextField();
    private ComboBox<String> fieldCombo = new ComboBox();
    private DatePicker fieldPicker = new DatePicker();
    private Label dialogLabel = new Label();
    private ChangeListener disableSubmitButtonOnEmptyText = ((observable, oldValue, newValue) -> fieldButtonNode.setDisable(((String)newValue).trim().isEmpty()));
    private ButtonType okButton = new ButtonType(Start.getResourceBundleString("submit"), ButtonBar.ButtonData.OK_DONE);
    private ButtonType cancelButton = new ButtonType(Start.getResourceBundleString("cancel"), ButtonBar.ButtonData.CANCEL_CLOSE);
    private ButtonType clearButton = new ButtonType(Start.getResourceBundleString("clear"), ButtonBar.ButtonData.APPLY);

    public dataVerifier() {
        fieldDialog.initModality(Modality.NONE);
        fieldDialog.getDialogPane().getStylesheets().add(getClass().getResource("scheduler/dialog.css").toExternalForm());


        fieldDialog.getDialogPane().getButtonTypes().addAll(okButton, clearButton, cancelButton);
        fieldDialog.initStyle(StageStyle.UTILITY);

        fieldButtonNode = fieldDialog.getDialogPane().lookupButton(okButton);
        fieldButtonNode.setDisable(true);
        dialogGrid.add(dialogLabel, 0, 0);

        fieldPicker.setEditable(false);





        StringConverter converter = new StringConverter<LocalDate>() {
            DateTimeFormatter dateFormatter =
                DateTimeFormatter.ofPattern("yyyy-MM-dd");
            @Override
            public String toString(LocalDate date) {
                if (date != null) {
                    return dateFormatter.format(date);
                } else {
                    return "";
                }
            }
            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    return LocalDate.parse(string, dateFormatter);
                } else {
                    return null;
                }
            }
        };
        fieldPicker.setConverter(converter);
    }
    public synchronized void checkAppointmentHours (Time startTime, Time endTime, Date appointmentDate, int customer) throws InvalidTimeException {
        if (startTime.after(endTime) || startTime.equals(endTime))
            throw new InvalidTimeException("startTimeBeforeEnd");

        if (startTime.before(Time.valueOf("09:00:00"))) {
            throw new InvalidTimeException("beforeHours");
        }
        if (endTime.after(Time.valueOf("17:00:00"))) {
            throw new InvalidTimeException("afterHours");
        }
        LinkedList<AppointmentDataModel> appointmentData = new LinkedList<>(((AppointmentsFXMLController)Start.controllers.get("Appointments")).appointmentsData);

        appointmentData.forEach((data) -> {
            Time dataStart = Time.valueOf(data.getTime().getValue().substring(0, data.getTime().getValue().indexOf(" ")));
            Time dataEnd = Time.valueOf(data.getTime().getValue().substring(data.getTime().getValue().lastIndexOf(" ") + 1, data.getTime().getValue().length()));
            Date date = Date.valueOf(data.getDate().getValue());
            if (date.equals(appointmentDate)) {
                if (data.getContact().getValue().equals(DatabaseManager.getUser()) || data.getCustomerId().get() ==  customer) {
                    if (((startTime.after(dataStart) || startTime.equals(dataStart)) && startTime.before(dataEnd)) || (endTime.after(dataStart) && (endTime.before(dataEnd) || endTime.equals(dataEnd)))) {
                        throw new InvalidTimeException("overlappingAppointments");
                    }
                }
            }
        });
    }

    public synchronized boolean checkField(Node node) {
        if (dialogGrid.getChildren().size() > 1)
            dialogGrid.getChildren().remove(dialogGrid.getChildren().size()-1);

        String nodeValue = "";
        if (node.getId().toLowerCase().equals("start"))
            dialogLabel.setText(Start.getResourceBundleString("correct").concat(Start.getResourceBundleString("date")).concat(" ").concat(Start.getResourceBundleString("field")));
        else if (node.getId().toLowerCase().contains("time"))
            dialogLabel.setText(Start.getResourceBundleString("correct").concat(Start.getResourceBundleString(node.getId().substring(0, node.getId().indexOf("Time") + 4))).concat(" ").concat(Start.getResourceBundleString("field")));
        else
            dialogLabel.setText(Start.getResourceBundleString("correct").concat(Start.getResourceBundleString(node.getId())).concat(" ").concat(Start.getResourceBundleString("field")));

        fieldValue.textProperty().removeListener(disableSubmitButtonOnEmptyText);
        boolean invalidField = true;

        Predicate<String> hasDigit = (value -> {
            for (int i = 0; i< value.length(); i++) {
                if (Character.isDigit(value.charAt(i)))
                        return true;
            }
            return false;
        });

        Predicate<String> hasAlpha = (value -> {
            for (int i = 0; i< value.length(); i++) {
                if (Character.isAlphabetic(value.charAt(i)))
                        return true;
            }
            return false;
        });

        Predicate<String> isValidDate = (value -> {
            for (int i = 0; i< value.length(); i++) {
                if (i == 4 || i == 7)
                    continue;
                if (Character.isAlphabetic(value.charAt(i)))
                    return true;
            }
            return false;
        });

        if (node instanceof ComboBox) {
            fieldCombo.setItems(((ComboBox) node).getItems());
            dialogGrid.add(fieldCombo, 0, 1);
            if (((ComboBox) node).getValue() != null)
                nodeValue = ((ComboBox)node).getValue().toString();
        }

        if (node instanceof TextField) {
            fieldValue.textProperty().addListener(disableSubmitButtonOnEmptyText);
            dialogGrid.add(fieldValue, 0, 1);

            if(((TextField) node).getText() != null)
            nodeValue = ((TextField)node).getText();
        }
        if (node instanceof DatePicker) {
            fieldValue.textProperty().addListener(disableSubmitButtonOnEmptyText);
            dialogGrid.add(fieldPicker, 0, 1);

            if (((DatePicker) node).getValue() != null)
            nodeValue = ((DatePicker) node).getValue().toString();
        }


        fieldValue.setText(nodeValue);
        fieldDialog.getDialogPane().setContent(dialogGrid);

        fieldDialog.setResultConverter(button -> {
            if (button.equals(okButton)) {
                return node instanceof ComboBox ? fieldCombo.getValue() :
                        node instanceof TextField ? fieldValue.getText() :
                        fieldPicker.getValue().toString();
            }
            if (button.equals(clearButton)) fieldValue.clear();

            return null;
        });

        while (invalidField) {
            try {
                if (nodeValue.trim().isEmpty()) {
                    if (node.getId().toLowerCase().equals("start"))
                        fieldDialog.setHeaderText(Start.getResourceBundleString("date").concat(Start.getResourceBundleString("cannotBeEmpty")));
                    else if (node.getId().toLowerCase().contains("time"))
                        if (node.getId().toLowerCase().contains("hour")) {
                            throw new InvalidTimeException("invalidHour");

                        }
                        if (node.getId().toLowerCase().contains("minute")) {
                            throw new InvalidTimeException("invalidMinute");

                        }
                    else
                        fieldDialog.setHeaderText(Start.getResourceBundleString(node.getId()).concat(Start.getResourceBundleString("cannotBeEmpty")));
                    throw new InvalidField();
                }
                else if (node.getId().toLowerCase().contains("name") ||
                        node.getId().toLowerCase().contains("city") ||
                        node.getId().toLowerCase().contains("country") ||
                        node.getId().toLowerCase().contains("title") ||
                        node.getId().toLowerCase().contains("description") ||
                        node.getId().toLowerCase().contains("location") ||
                        node.getId().toLowerCase().contains("contact")) {
                            if (hasDigit.test(nodeValue)) {
                            fieldDialog.setHeaderText(Start.getResourceBundleString(node.getId()).concat(Start.getResourceBundleString("cannotHaveNumbers")));
                            throw new InvalidField();
                        }
                }
                else if (node.getId().toLowerCase().contains("phone") ||
                        node.getId().toLowerCase().contains("time")) {


                        if(hasAlpha.test(nodeValue)) {
                            fieldDialog.setHeaderText(Start.getResourceBundleString(node.getId()).concat(Start.getResourceBundleString("cannotHaveLetters")));
                            throw new InvalidField();
                        }
                        if (node.getId().toLowerCase().contains("hour")) {
                            if (Integer.valueOf(nodeValue) < 0 || Integer.valueOf(nodeValue) > 12) {
                                throw new InvalidTimeException("invalidHour");
                            }
                        }
                        if (node.getId().toLowerCase().contains("minute")) {
                            if (Integer.valueOf(nodeValue) < 0 || Integer.valueOf(nodeValue) > 59) {
                                throw new InvalidTimeException("invalidMinute");
                            }
                        }
                }
                else if (node.getId().toLowerCase().equals("start"))
                    if(isValidDate.test(nodeValue)) {
                            fieldDialog.setHeaderText(Start.getResourceBundleString("date").concat(Start.getResourceBundleString("cannotHaveLetters")));
                            throw new InvalidField();
                    }
                
                invalidField = false;
            }
            catch (InvalidField e) {
                result = fieldDialog.showAndWait();
                
                result.ifPresent(newFieldValue -> {
                    if (node instanceof ComboBox) 
                        ((ComboBox) node).setValue(newFieldValue);
                    
                    if (node instanceof TextField) 
                        ((TextField) node).setText(newFieldValue);
                    
                    if (node instanceof DatePicker)
                        ((DatePicker) node).setValue(LocalDate.parse(result.get()));
                    
                });
                if (result.isPresent())
                    nodeValue = result.get();
                else
                    throw new InvalidField();
            }
            catch (InvalidTimeException e) {
                throw e;
            }
        }
        
        return true;
    }
}
