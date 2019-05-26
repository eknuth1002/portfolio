/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler;

import java.net.URL;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;

/**
 * FXML Controller class
 *
 * @author Elliot Knuth
 */
public class ReportsFXMLController implements Initializable {

    @FXML
    private RadioButton appointmentsPerMonthButton;
    @FXML
    private RadioButton consultantsScheduleButton;
    @FXML
    private RadioButton customerAppointmentsButton;
    @FXML
    private ToggleGroup reports;

    ObservableList<AppointmentDataModel> appointmentData = ((AppointmentsFXMLController) Start.controllers.get("Appointments")).appointmentsData;
    @FXML
    private TextArea reportArea;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    @FXML
    private void calculateNumberOfAppointments(ActionEvent event) {
        LinkedList<AppointmentDataModel> appointments = new LinkedList(appointmentData);
        LinkedHashMap<String, HashMap<String, Integer>> appointmentsByType = new LinkedHashMap<>();

        Month[] monthnames = new Month[12];
        for (int i = 0; i < 12; i++) {
            monthnames[i] = LocalDate.now().getMonth().plus(i);
        }

        for (Month monthName : monthnames) {
            appointmentsByType.put(monthName.toString(), new HashMap<>());
        }

        appointmentsByType.forEach((String key, HashMap<String, Integer> value) -> {
            value.put("Initial Consultation", 0);
            value.put("Consultation", 0);
            value.put("Follow-up Consultation", 0);
        });

        for (AppointmentDataModel data : appointments) {
            LocalDate dataDate = LocalDate.parse(data.getDate().getValue(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            for (Month monthName : Month.values()) {
                if ((dataDate.getYear() == LocalDate.now().getYear() && dataDate.getMonthValue() - LocalDate.now().getMonthValue() >= 0
                        || dataDate.getYear() == LocalDate.now().plusYears(1).getYear() && dataDate.getMonthValue() < LocalDate.now().getMonthValue())
                        && dataDate.getMonth().equals(monthName)) {

                    appointmentsByType.get(monthName.toString()).put(data.getDescription().getValue(),
                            appointmentsByType.get(monthName.toString()).get(data.getDescription().getValue()) + 1);

                }
            }
        }

        StringBuilder report = new StringBuilder("The number of appointments by type per month for the next year are:\n\n");

        appointmentsByType.forEach((key, value) -> {
            report.append(key).append(" has : \n");
            value.forEach((appointmentType, appointNumber) -> {

                report.append("\t").append(appointNumber.toString()).append(" ").append(appointmentType).append(" appointments.\n");
            });
        });
        reportArea.clear();
        reportArea.setText(report.toString());
    }

    @FXML
    private void consultantSchedules(ActionEvent event) {
        LinkedHashMap<String, LinkedList<String>> appointmentsByConsultant = new LinkedHashMap<>();
        LinkedList<AppointmentDataModel> appointments = new LinkedList(appointmentData);

        for (AppointmentDataModel data : appointments) {
            if (!data.getContact().getValueSafe().isEmpty()) {
                LocalDate appointmentDate = LocalDate.parse(data.getDate().get());
                if ((appointmentDate.isAfter(LocalDate.now()) || appointmentDate.isEqual(LocalDate.now())) && appointmentDate.isBefore(LocalDate.now().plusYears(1))) {
                    appointmentsByConsultant.computeIfAbsent(data.getContact().getValue(), key -> appointmentsByConsultant.put(key, new LinkedList<>()));
                    appointmentsByConsultant.computeIfPresent(data.getContact().getValue(), (key, value) -> {
                        value.add(data.getDate().get().concat(" at ").concat(data.getTime().get()));
                        return value;
                    });
                }

            }
        }

        StringBuilder report = new StringBuilder("The schedules for the following consultants for the next year are:\n\n");

        appointmentsByConsultant.forEach((key, value) -> {
            report.append(key).append(" has : \n");
            value.forEach(appointment -> {

                report.append("\t").append(appointment.toString()).append("\n");
            });
        });
        reportArea.clear();
        reportArea.setText(report.toString());
    }

    @FXML
    private void appointmentsByCustomer(ActionEvent event) {
        LinkedHashMap<String, LinkedList<String>> appointmentsByCustomer = new LinkedHashMap<>();
        LinkedList<AppointmentDataModel> appointments = new LinkedList(appointmentData);

        for (AppointmentDataModel data : appointments) {
            if (!data.getContact().getValueSafe().isEmpty()) {
                LocalDate appointmentDate = LocalDate.parse(data.getDate().get());
                if ((appointmentDate.isAfter(LocalDate.now()) || appointmentDate.isEqual(LocalDate.now())) && appointmentDate.isBefore(LocalDate.now().plusYears(1))) {
                    appointmentsByCustomer.computeIfAbsent(data.getContact().getValue(), key -> appointmentsByCustomer.put(key, new LinkedList<>()));
                    appointmentsByCustomer.computeIfPresent(data.getContact().getValue(), (key, value) -> {
                        value.add("an appointment for a "
                                .concat(data.getDescription().getValue())
                                .concat(" on ")
                                .concat(data.getDate().get())
                                .concat(" at ")
                                .concat(data.getTime().get())
                                .concat(" at ")
                                .concat(data.getLocation().get()));
                        return value;
                    });
                }

            }
        }

        StringBuilder report = new StringBuilder("The schedules for the following customers for the next year are:\n\n");

        appointmentsByCustomer.forEach((key, value) -> {
            report.append(key).append(" has : \n");
            value.forEach(appointment -> {

                report.append("\t").append(appointment.toString()).append("\n");
            });
        });
        reportArea.clear();
        reportArea.setText(report.toString());
    }
}
