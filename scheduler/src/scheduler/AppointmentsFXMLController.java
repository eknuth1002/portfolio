/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler;

import scheduler.CustomExceptions.Exceptions;
import java.net.URL;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TimeZone;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.util.StringConverter;

/**
 * FXML Controller class
 *
 * @author Elliot Knuth
 */
public class AppointmentsFXMLController implements Initializable {
    
    @FXML private AnchorPane addAppointmentPane;
    @FXML private AnchorPane modifyAppointmentPane;
    @FXML private TextField titleField;
    @FXML private TextField modifyTitleField;
    @FXML private TextField locationField;
    @FXML private TextField modifyLocationField;
    @FXML private ComboBox<String> descriptionField;
    @FXML private ComboBox<String> modifyDescriptionField;
    @FXML private TextField startTimeHour;
    @FXML private TextField modifyStartTimeHour;
    @FXML private TextField startTimeMinute;
    @FXML private TextField modifyStartTimeMinute;
    @FXML private TextField endTimeHour;
    @FXML private TextField modifyEndTimeHour;
    @FXML private TextField endTimeMinute;
    @FXML private TextField modifyEndTimeMinute;
    @FXML private TextField URLField;
    @FXML private TextField modifyURLField;
    @FXML private Label customerLabel;
    @FXML private Label modifyCustomerLabel;
    @FXML private Label dateLabel;
    @FXML private Label modifyDateLabel;
    @FXML private DatePicker dateField;
    @FXML private DatePicker modifyDateField;
    @FXML private GridPane modifyAppointmentGridPane;
    @FXML private TextArea appointmentListingArea;
    @FXML private Button viewByMonthButton;
    @FXML private Button viewByWeekButton;
    @FXML private Button viewAllButton;
    @FXML private Button addAppointmentButton;
    @FXML private Button modifyAppointmentButton;
    @FXML private Text addAppointmentTitle;
    @FXML private Text modifyAppointmentTitle;
    @FXML private Label startTimeLabel;
    @FXML private Label modifyStartTimeLabel;
    @FXML private Label endTimeLabel;
    @FXML private Label modifyEndTimeLabel;
    @FXML private Label titleLabel;
    @FXML private Label modifyTitleLabel;
    @FXML private Label descriptionLabel;
    @FXML private Label modifyDescriptionLabel;
    @FXML private Label locationLabel;
    @FXML private Label modifyLocationLabel;
    @FXML private Label urlLabel;
    @FXML private Label modifyUrlLabel;
    @FXML private Button okButton;
    @FXML private Button modifyOkButton;
    @FXML private Button cancelButton;
    @FXML private Button modifyCancelButton;
    @FXML private Button currentMonthButton;
    @FXML private Button currentWeekButton;
    @FXML private Button previousMonthButton;
    @FXML private Button previousWeekButton;
    @FXML private Button nextMonthButton;
    @FXML private Button nextWeekButton;
    
    @FXML private Spinner<String> startMeridiem;
    @FXML private Spinner<String> endMeridiem;
    @FXML private Spinner<String> modifyStartMeridiem;
    @FXML private Spinner<String> modifyEndMeridiem;
    final private ObservableList<String> meridiemValues = FXCollections.observableArrayList("A.M.","P.M.");
   
    
    private int calendarCounter = 0;

    final ObservableList<AppointmentDataModel> appointmentsData = FXCollections.observableArrayList();
    final FilteredList<AppointmentDataModel> filteredAppointmentsData = new FilteredList(appointmentsData, data -> true);

    @FXML private TableView<AppointmentDataModel> appointmentsTable;
    @FXML private TableColumn<AppointmentDataModel, String> appointmentsDateColumn;
    @FXML private TableColumn<AppointmentDataModel, String> appointmentsTimeColumn;
    @FXML private TableColumn<AppointmentDataModel, String> appointmentsLocationColumn;
    @FXML private TableColumn<AppointmentDataModel, String> appointmentsContactColumn;
    
    
    UnaryOperator<String> convertToLocalTimeFromUTC = data -> {
        if (data.length() > 19)
            data = data.substring(0, data.length() - 2);
        return LocalDateTime.parse(data, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                .atZone(ZoneId.of(TimeZone.getDefault().getID()))
                .withZoneSameInstant(ZoneId.of("Etc/UTC"))
                .toString()
                .substring(0, 16)
                .concat(":00")
                .replace('T', ' ');
    };
    
    UnaryOperator<String> convertToUTCFromLocalTime = data -> {
        if (data.length() > 19)
            data = data.substring(0, data.length() - 2);
        String localDateString = LocalDateTime.parse(data, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                .atZone(ZoneId.of("Etc/UTC"))
                .withZoneSameInstant(ZoneId.of(TimeZone.getDefault().getID()))
                .toString();
        return localDateString.substring(0, 16).concat(":00").replace('T', ' ');
    };
    
  
    Predicate<AppointmentDataModel> weeklyPredicate = data -> {
      //<editor-fold>
      LocalDate currentDatePlaceholder = LocalDate.now().plusWeeks(calendarCounter).minusDays(LocalDate.now().getDayOfWeek().getValue());
        
        if (((AppointmentDataModel) data).getDate().get()
            .startsWith(currentDatePlaceholder.toString())) return true;

        if (((AppointmentDataModel) data).getDate().get()
            .startsWith(currentDatePlaceholder.plusDays(1).toString())) return true;

        if (((AppointmentDataModel) data).getDate().get()
            .startsWith(currentDatePlaceholder.plusDays(2).toString())) return true;

        if (((AppointmentDataModel) data).getDate().get()
            .startsWith(currentDatePlaceholder.plusDays(3).toString())) return true;

        if (((AppointmentDataModel) data).getDate().get()
            .startsWith(currentDatePlaceholder.plusDays(4).toString())) return true;

        if (((AppointmentDataModel) data).getDate().get()
            .startsWith(currentDatePlaceholder.plusDays(5).toString())) return true;

        return (((AppointmentDataModel) data).getDate().get()
            .startsWith(currentDatePlaceholder.plusDays(6).toString()));
    };
    //</editor-fold>
    Predicate<AppointmentDataModel> monthlyPredicate = data ->  {
        
        
        return ((AppointmentDataModel) data).getDate().get()
                .startsWith(LocalDate.now().plusMonths(calendarCounter).toString().substring(0, 7));
    };
    
    @FXML private GridPane appointmentGridPane;
    @FXML private GridPane addAppointmentGridPane;
    
    private final ObservableList<String> customerList = FXCollections.observableArrayList();
    @FXML private ComboBox<String> customerField;
    @FXML private ComboBox<String> modifyCustomerField;
    ScheduledExecutorService appointmentDataRefresh = Executors.newScheduledThreadPool(1);
    
    
    

    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Start.addExecutor(appointmentDataRefresh);
        ((CustomerRecordsFXMLController) Start.controllers.get("Customers")).customerData.forEach(data -> {
            String name = data.getCustomerNameAsString();
            customerList.add(name);
                });
        addAppointmentTitle.setText(Start.getResourceBundleString("addAppointment"));
        currentMonthButton.setText(Start.getResourceBundleString("currentMonth"));
        currentWeekButton.setText(Start.getResourceBundleString("currentWeek"));
        previousMonthButton.setText(Start.getResourceBundleString("previousMonth"));
        previousWeekButton.setText(Start.getResourceBundleString("previousWeek"));
        nextMonthButton.setText(Start.getResourceBundleString("nextMonth"));
        nextWeekButton.setText(Start.getResourceBundleString("nextWeek"));
        viewByMonthButton.setText(Start.getResourceBundleString("viewByMonth"));
        viewByWeekButton.setText(Start.getResourceBundleString("viewByWeek"));
        viewAllButton.setText(Start.getResourceBundleString("viewAll"));
        addAppointmentButton.setText(Start.getResourceBundleString("addAppointment"));
        modifyAppointmentButton.setText(Start.getResourceBundleString("modifyAppointment"));
        startTimeLabel.setText(Start.getResourceBundleString("startTime"));
        endTimeLabel.setText(Start.getResourceBundleString("endTime"));
        titleLabel.setText(Start.getResourceBundleString("title"));
        descriptionLabel.setText(Start.getResourceBundleString("description"));
        locationLabel.setText(Start.getResourceBundleString("location"));
        customerLabel.setText(Start.getResourceBundleString("customer"));
        dateLabel.setText(Start.getResourceBundleString("date"));
        urlLabel.setText(Start.getResourceBundleString("url"));
        okButton.setText(Start.getResourceBundleString("ok"));
        cancelButton.setText(Start.getResourceBundleString("cancel"));
        modifyStartTimeLabel.setText(Start.getResourceBundleString("startTime"));
        modifyEndTimeLabel.setText(Start.getResourceBundleString("endTime"));
        modifyTitleLabel.setText(Start.getResourceBundleString("title"));
        modifyDescriptionLabel.setText(Start.getResourceBundleString("description"));
        modifyLocationLabel.setText(Start.getResourceBundleString("location"));
        modifyCustomerLabel.setText(Start.getResourceBundleString("customer"));
        modifyDateLabel.setText(Start.getResourceBundleString("date"));
        modifyUrlLabel.setText(Start.getResourceBundleString("url"));
        modifyOkButton.setText(Start.getResourceBundleString("ok"));
        modifyCancelButton.setText(Start.getResourceBundleString("cancel"));
        appointmentsDateColumn.setText(Start.getResourceBundleString("date"));
        appointmentsTimeColumn.setText(Start.getResourceBundleString("time"));
        appointmentsContactColumn.setText(Start.getResourceBundleString("consultant"));
        appointmentsLocationColumn.setText(Start.getResourceBundleString("location"));
        startMeridiem.setValueFactory(new SpinnerValueFactory.ListSpinnerValueFactory<>(meridiemValues));
        endMeridiem.setValueFactory(new SpinnerValueFactory.ListSpinnerValueFactory<>(meridiemValues));
        startMeridiem.getValueFactory().setValue("A.M.");
        startMeridiem.getValueFactory().setWrapAround(true);
        endMeridiem.getValueFactory().setValue("A.M.");
        endMeridiem.getValueFactory().setWrapAround(true);
        modifyStartMeridiem.setValueFactory(new SpinnerValueFactory.ListSpinnerValueFactory<>(meridiemValues));
        modifyEndMeridiem.setValueFactory(new SpinnerValueFactory.ListSpinnerValueFactory<>(meridiemValues));
        modifyStartMeridiem.getValueFactory().setValue("A.M.");
        modifyStartMeridiem.getValueFactory().setWrapAround(true);
        modifyEndMeridiem.getValueFactory().setValue("A.M.");
        modifyEndMeridiem.getValueFactory().setWrapAround(true);
        descriptionField.setItems(FXCollections.observableArrayList("Initial Consultation","Consultation","Follow-up Consultation"));
        modifyDescriptionField.setItems(FXCollections.observableArrayList("Initial Consultation","Consultation","Follow-up Consultation"));
        
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
        dateField.setConverter(converter);
        modifyDateField.setConverter(converter);
        
        appointmentDataRefresh.scheduleAtFixedRate(() -> {
        System.out.println("Fetching appointment data at ".concat(LocalTime.now().toString()));
            try {
            ResultSet results = DatabaseManager.populateAppointmentMaps();

            resultsloop:
            while (results.next()) {
                
                for (AppointmentDataModel value : appointmentsData) {
                    try {
                        if (Integer.parseInt(results.getString("appointmentId")) == value.getAppointmentId().get() &&
                                Integer.parseInt(results.getString("customerId")) == value.getCustomerId().get() &&
                                results.getString("title").equals(value.getTitle().get()) &&
                                results.getString("description").equals(value.getDescription().get()) &&
                                results.getString("location").equals(value.getLocation().get()) &&
                                results.getString("contact").equals(value.getContact().get()) &&
                                results.getString("url").equals(value.getURL().get()) &&
                                convertToUTCFromLocalTime.apply(results.getString("start")).equals(value.getStart().get()) &&
                                convertToUTCFromLocalTime.apply(results.getString("end")).equals(value.getEnd().get())) {
                            continue resultsloop;   
                        }
                    } catch (SQLException ex) {
                        Logger.getLogger(AppointmentsFXMLController.class.getName()).log(Level.SEVERE, null, ex);
                        continue resultsloop;
                    }
                }
                
                appointmentsData.add(new AppointmentDataModel(
                        Integer.parseInt(results.getString("appointmentId")),
                        Integer.parseInt(results.getString("customerId")),
                        results.getString("title"),
                        results.getString("description"),
                        results.getString("location"),
                        results.getString("contact"),
                        results.getString("url"),
                        convertToUTCFromLocalTime.apply(results.getString("start")),
                        convertToUTCFromLocalTime.apply(results.getString("end"))));
            }
        } catch (SQLException ex) {
            Logger.getLogger(CustomerRecordsFXMLController.class.getName()).log(Level.SEVERE, null, ex);
        }}, 0, 1, TimeUnit.MINUTES);
        
        customerField.setItems(customerList);
        modifyCustomerField.setItems(customerList);
        
        appointmentsTable.setPlaceholder(new Label(Start.getResourceBundleString("noContent")));
        appointmentsTable.setItems(filteredAppointmentsData);
        appointmentsDateColumn.setCellValueFactory(cellData -> cellData.getValue().getDate());
        appointmentsTimeColumn.setCellValueFactory(cellData -> cellData.getValue().getTime());
        appointmentsLocationColumn.setCellValueFactory(cellData -> cellData.getValue().getLocation());
        appointmentsContactColumn.setCellValueFactory(cellData -> cellData.getValue().getContact());
        
        appointmentsTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null)
                appointmentListingArea.clear();
            else {
                StringBuilder appointmentData = new StringBuilder();
                ObservableList<CustomerDataModel> customerData = ((CustomerRecordsFXMLController) Start.controllers.get("Customers")).customerData;
                appointmentData.append("Name: ").append(customerData.get(newValue.getCustomerId().get()-1).getCustomerNameAsString()).append("\n");
                appointmentData.append("Appointment Type: ").append(newValue.getDescription().get()).append("\n");
                appointmentData.append("Customer's URL: ").append(newValue.getURL().get());

                appointmentListingArea.setText(appointmentData.toString());
            }
            
        });
    }

    @FXML
    private void addAppointment(ActionEvent event) {
        try {     
            dataVerifier verify = new dataVerifier();
            verify.checkField(titleField);
            verify.checkField(descriptionField);
            verify.checkField(locationField);
            verify.checkField(customerField);
            verify.checkField(dateField);
            verify.checkField(startTimeHour);
            verify.checkField(startTimeMinute);
            verify.checkField(endTimeHour);
            verify.checkField(endTimeMinute);
            verify.checkField(URLField);

            String startTime = startMeridiem.getValue().equals("A.M.") && startTimeHour.getText().equals("12")? 
                    dateField.getValue().toString()
                            .concat(" ")
                            .concat("00")
                            .concat(":")
                            .concat(startTimeMinute.getText())
                            .concat(":00") :
                    startMeridiem.getValue().equals("P.M.") && !startTimeHour.getText().equals("12") ? 
                    dateField.getValue().toString()
                            .concat(" ")
                            .concat(String.valueOf(Integer.parseInt(startTimeHour.getText())+ 12))
                            .concat(":")
                            .concat(startTimeMinute.getText())
                            .concat(":00") :
                    dateField.getValue().toString()
                            .concat(" ")
                            .concat(startTimeHour.getText())
                            .concat(":")
                            .concat(startTimeMinute.getText())
                            .concat(":00"); 



            String endTime = endMeridiem.getValue().equals("A.M.") && endTimeHour.getText().equals("12") ? 
                    dateField.getValue().toString()
                            .concat(" ")
                            .concat("00")
                            .concat(":")
                            .concat(endTimeMinute.getText())
                            .concat(":00"):
                    endMeridiem.getValue().equals("P.M.") && !endTimeHour.getText().equals("12")? 
                    dateField.getValue().toString()
                            .concat(" ")
                            .concat(String.valueOf(Integer.parseInt(endTimeHour.getText())+ 12))
                            .concat(":")
                            .concat(endTimeMinute.getText())
                            .concat(":00"):
                    dateField.getValue().toString()
                            .concat(" ")
                            .concat(endTimeHour.getText())
                            .concat(":")
                            .concat(endTimeMinute.getText())
                            .concat(":00");

            try {

                verify.checkAppointmentHours(Time.valueOf(startTime.substring(11)), Time.valueOf(endTime.substring(11)), Date.valueOf(dateField.getValue()), customerField.getSelectionModel().getSelectedIndex() + 1);

                final Map<String, String> valuesForPreparedStatement = new LinkedHashMap<>();
                String preparedStatement = "INSERT INTO appointment (customerId, title, description, location, contact, url, start, end, createDate, createdBy, lastUpdate, lastUpdateBy) "
                                         + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW(), ?, NOW(), ?)";
                valuesForPreparedStatement.put("customerId", String.valueOf(customerField.getSelectionModel().getSelectedIndex() + 1));
                valuesForPreparedStatement.put("title", titleField.getText());
                valuesForPreparedStatement.put("description", descriptionField.getValue());
                valuesForPreparedStatement.put("location", locationField.getText());
                valuesForPreparedStatement.put("contact", DatabaseManager.getUser());
                valuesForPreparedStatement.put("url", URLField.getText());

                valuesForPreparedStatement.put("start", convertToLocalTimeFromUTC.apply(startTime));

                valuesForPreparedStatement.put("end", convertToLocalTimeFromUTC.apply(endTime));

                try {
                        DatabaseManager.add(valuesForPreparedStatement, preparedStatement);
                        appointmentsData.add(new AppointmentDataModel(appointmentsData.size(), customerField.getSelectionModel().getSelectedIndex() + 1,
                                             titleField.getText(), descriptionField.getValue(), locationField.getText(), DatabaseManager.getUser(),
                                             URLField.getText(), startTime, endTime));
                    } 
                    catch (SQLException ex) {
                        Logger.getLogger(CustomerRecordsFXMLController.class.getName()).log(Level.SEVERE, null, ex);
                    } finally {
                        valuesForPreparedStatement.clear();
                        preparedStatement = "";
                        
                        
                }
                addAppointmentPane.setVisible(false);
                titleField.clear();
                descriptionField.getSelectionModel().select(-1);
                locationField.clear();
                customerField.getSelectionModel().select(-1);
                dateField.setValue(null);
                startTimeHour.clear();
                startTimeMinute.clear();
                endTimeHour.clear();
                endTimeMinute.clear();
                URLField.clear();
            } catch (Exceptions.InvalidTimeException ex) {
                if (ex.getMessage().equals("overlappingAppointments"))
                    Alerts.errorAlert(Start.getResourceBundleString("timeError"), Start.getResourceBundleString("userName").concat(" ").concat(DatabaseManager.getUser()).concat(Start.getResourceBundleString("overlappingAppointments")));
                else
                    Alerts.errorAlert(Start.getResourceBundleString("timeError"), Start.getResourceBundleString(ex.getMessage()));
            }
        }
        catch (Exceptions.InvalidTimeException e) {
            Alerts.errorAlert(Start.getResourceBundleString("timeError"), Start.getResourceBundleString(e.getMessage()));
        }
    }

    @FXML
    private void cancelAddAppointment(ActionEvent event) {
        titleField.clear();
        descriptionField.getSelectionModel().select(-1);
        locationField.clear();
        customerField.getSelectionModel().select(-1);
        dateField.setValue(null);
        startTimeHour.clear();
        startTimeMinute.clear();
        endTimeHour.clear();
        endTimeMinute.clear();
        URLField.clear();

        addAppointmentPane.setVisible(false);
    }
    
    @FXML
    private void populateModifyTab() {
        if (appointmentsTable.getSelectionModel().getSelectedIndex() != -1) {
            modifyAppointmentPane.setVisible(true);
            AppointmentDataModel appointment = appointmentsTable.getSelectionModel().getSelectedItem();
            String startTime = appointment.getStart().get().substring(appointment.getStart().get().indexOf(" ") + 1, appointment.getStart().get().length() - 2);
            String endTime = appointment.getEnd().get().substring(appointment.getEnd().get().indexOf(" ") + 1, appointment.getEnd().get().length() - 2);
            String startHour = Integer.valueOf(startTime.substring(0,2)) > 12 ? String.valueOf(Integer.valueOf(startTime.substring(0,2)) - 12) : startTime.substring(0,2);
            String startMinute = startTime.substring(3,5);
            String endHour = Integer.valueOf(endTime.substring(0,2)) > 12 ? String.valueOf(Integer.valueOf(endTime.substring(0,2)) - 12) : endTime.substring(0,2);
            String endMinute = endTime.substring(3,5);
            modifyStartMeridiem.getValueFactory().setValue(Integer.valueOf(startTime.substring(0,2)) >= 12 ? "P.M." : "A.M.");
            modifyEndMeridiem.getValueFactory().setValue(Integer.valueOf(endTime.substring(0,2)) >= 12 ? "P.M." : "A.M.");
            modifyTitleField.setText(appointment.getTitle().get());
            modifyDescriptionField.setValue(appointment.getDescription().get());
            modifyLocationField.setText(appointment.getLocation().get());
            modifyCustomerField.getSelectionModel().select(appointment.getCustomerId().get()-1);
            modifyDateField.setValue(LocalDate.parse(appointment.getDate().get()));
            modifyStartTimeHour.setText(startHour);
            modifyStartTimeMinute.setText(startMinute);
            modifyEndTimeHour.setText(endHour);
            modifyEndTimeMinute.setText(endMinute);
            modifyURLField.setText(appointment.getURL().get());
        }
    }
    
    @FXML
    private void modifyAppointment(ActionEvent event) {
        try {
            dataVerifier verify = new dataVerifier();
        
            verify.checkField(modifyTitleField);
            verify.checkField(modifyDescriptionField);
            verify.checkField(modifyLocationField);
            verify.checkField(modifyCustomerField);
            verify.checkField(modifyDateField);
            verify.checkField(modifyStartTimeHour);
            verify.checkField(modifyStartTimeMinute);
            verify.checkField(modifyEndTimeHour);
            verify.checkField(modifyEndTimeMinute);
            verify.checkField(modifyURLField);

            String startTime = modifyStartMeridiem.getValue().equals("A.M.") && modifyEndTimeHour.getText().equals("12")? 
                    modifyDateField.getValue().toString()
                            .concat(" ")
                            .concat("00")
                            .concat(":")
                            .concat(modifyStartTimeMinute.getText())
                            .concat(":00") :
                    modifyStartMeridiem.getValue().equals("P.M.") && !modifyStartTimeHour.getText().equals("12") ? 
                    modifyDateField.getValue().toString()
                            .concat(" ")
                            .concat(String.valueOf(Integer.parseInt(modifyStartTimeHour.getText())+ 12))
                            .concat(":")
                            .concat(modifyStartTimeMinute.getText())
                            .concat(":00") :
                    modifyDateField.getValue().toString()
                            .concat(" ")
                            .concat(modifyStartTimeHour.getText())
                            .concat(":")
                            .concat(modifyStartTimeMinute.getText())
                            .concat(":00"); 




             String endTime = modifyEndMeridiem.getValue().equals("A.M.") && modifyEndTimeHour.getText().equals("12") ? 
                    modifyDateField.getValue().toString()
                            .concat(" ")
                            .concat("00")
                            .concat(":")
                            .concat(modifyEndTimeMinute.getText())
                            .concat(":00"):
                    modifyEndMeridiem.getValue().equals("P.M.") && !modifyEndTimeHour.getText().equals("12")? 
                    modifyDateField.getValue().toString()
                            .concat(" ")
                            .concat(String.valueOf(Integer.parseInt(modifyEndTimeHour.getText())+ 12))
                            .concat(":")
                            .concat(modifyEndTimeMinute.getText())
                            .concat(":00"):
                    modifyDateField.getValue().toString()
                            .concat(" ")
                            .concat(modifyEndTimeHour.getText())
                            .concat(":")
                            .concat(modifyEndTimeMinute.getText())
                            .concat(":00");

            try {

                verify.checkAppointmentHours(Time.valueOf(startTime.substring(11)), Time.valueOf(endTime.substring(11)), Date.valueOf(modifyDateField.getValue()), modifyCustomerField.getSelectionModel().getSelectedIndex() + 1);
            
                LinkedList<CustomerDataModel> customers = new LinkedList<>(((CustomerRecordsFXMLController) Start.controllers.get("Customers")).customerData);
                AppointmentDataModel appointment = new AppointmentDataModel(appointmentsTable.getSelectionModel().getSelectedItem().getAppointmentId().get(),
                                                    (customers.get(modifyCustomerField.getSelectionModel().getSelectedIndex())).getCustomerIdAsInteger(),
                                                    modifyTitleField.getText(), modifyDescriptionField.getValue(), modifyLocationField.getText(),
                                                    appointmentsTable.getSelectionModel().getSelectedItem().getContact().get(), modifyURLField.getText(), startTime, endTime);
                final Map<String, String> valuesForPreparedStatement = new LinkedHashMap<>();
                String preparedStatement = "UPDATE appointment SET customerID = ?, title = ?, description = ?, location = ?,"
                                         + "url = ?, start = ?, end = ?, lastUpdate = NOW(), lastUpdateBy = ? WHERE appointmentId = ?";

                valuesForPreparedStatement.put("customerId", String.valueOf(appointment.getCustomerId().get()));
                valuesForPreparedStatement.put(modifyTitleField.getId(), appointment.getTitle().get());
                valuesForPreparedStatement.put(modifyDescriptionField.getId(), appointment.getDescription().get());
                valuesForPreparedStatement.put(modifyLocationField.getId(),appointment.getLocation().get());
                valuesForPreparedStatement.put(modifyURLField.getId(), appointment.getURL().get());
                valuesForPreparedStatement.put("start", convertToLocalTimeFromUTC.apply(startTime));
                valuesForPreparedStatement.put("end", convertToLocalTimeFromUTC.apply(endTime));
                valuesForPreparedStatement.put("appointmentId", String.valueOf(appointment.getAppointmentId().get()));

                try {
                    DatabaseManager.update(valuesForPreparedStatement, preparedStatement);
                    appointmentsData.set(appointment.getAppointmentId().get() -1, appointment);
                } 
                catch (SQLException ex) {
                    Logger.getLogger(CustomerRecordsFXMLController.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (Exceptions.InvalidTimeException ex) {
                if (ex.getMessage().equals("overlappingAppointments"))
                    Alerts.errorAlert(Start.getResourceBundleString("timeError"), Start.getResourceBundleString("userName").concat(" ").concat(DatabaseManager.getUser()).concat(Start.getResourceBundleString("overlappingAppointments")));
                else
                    Alerts.errorAlert(Start.getResourceBundleString("timeError"), Start.getResourceBundleString(ex.getMessage()));
            } 
            modifyAppointmentPane.setVisible(false);
            modifyTitleField.clear();
            modifyDescriptionField.getSelectionModel().select(-1);
            modifyLocationField.clear();
            modifyCustomerField.getSelectionModel().select(-1);
            modifyDateField.setValue(null);
            modifyStartTimeHour.clear();
            modifyStartTimeMinute.clear();
            modifyEndTimeHour.clear();
            modifyEndTimeMinute.clear();
            modifyURLField.clear();
            
        }
        catch (Exception e){
        
        }
    }
    
    @FXML
    private void cancelModifyAppointment(ActionEvent event) {
        modifyTitleField.clear();
        modifyDescriptionField.getSelectionModel().select(-1);
        modifyLocationField.clear();
        modifyCustomerField.getSelectionModel().select(-1);
        modifyDateField.setValue(null);
        modifyStartTimeHour.clear();
        modifyStartTimeMinute.clear();
        modifyEndTimeHour.clear();
        modifyEndTimeMinute.clear();
        modifyURLField.clear();
        modifyAppointmentPane.setVisible(false);
    }

    @FXML
    private void showAddApointment(ActionEvent event) {
        addAppointmentPane.setVisible(true);
        titleField.requestFocus();
    }

    @FXML
    private void viewByMonthFilter(ActionEvent event) {
        calendarCounter = 0;
        appointmentsTable.setPlaceholder(new Label((Start.getResourceBundleString("noContent")).concat(" for the month of ").concat(Start.getResourceBundleString(LocalDate.now().plusWeeks(calendarCounter).getMonth().toString()))));
        previousMonthButton.setVisible(true);
        previousWeekButton.setVisible(false);
        currentMonthButton.setVisible(true);
        currentWeekButton.setVisible(false);
        nextMonthButton.setVisible(true);
        nextWeekButton.setVisible(false);
        
        filteredAppointmentsData.setPredicate(monthlyPredicate);
    }

    @FXML
    private void viewByWeekFilter(ActionEvent event) {
        appointmentsTable.setPlaceholder(new Label((Start.getResourceBundleString("noContent")).concat(Start.getResourceBundleString("weekOf")).concat(LocalDate.now().plusWeeks(calendarCounter).minusDays(LocalDate.now().getDayOfWeek().getValue()).toString())));
        calendarCounter = 0;
        previousWeekButton.setVisible(true);
        previousMonthButton.setVisible(false);
        currentWeekButton.setVisible(true);
        currentMonthButton.setVisible(false);
        nextWeekButton.setVisible(true);
        nextMonthButton.setVisible(false);
        
        filteredAppointmentsData.setPredicate(weeklyPredicate);
    }

    @FXML
    private void viewAllFilter(ActionEvent event) {
        previousMonthButton.setVisible(false);
        previousWeekButton.setVisible(false);
        currentMonthButton.setVisible(false);
        currentWeekButton.setVisible(false);
        nextMonthButton.setVisible(false);
        nextWeekButton.setVisible(false);
        
        filteredAppointmentsData.setPredicate(data -> true);
    }
    
    @FXML
    private void previousMonth(ActionEvent event) {
        calendarCounter--;
        appointmentsTable.setPlaceholder(new Label((Start.getResourceBundleString("noContent")).concat(Start.getResourceBundleString("monthOf")).concat(Start.getResourceBundleString(LocalDate.now().plusMonths(calendarCounter).getMonth().toString()))));
        filteredAppointmentsData.setPredicate(null);
        filteredAppointmentsData.setPredicate(monthlyPredicate);
    }
    
    @FXML
    private void nextMonth(ActionEvent event) {
        calendarCounter++;
        appointmentsTable.setPlaceholder(new Label((Start.getResourceBundleString("noContent")).concat(Start.getResourceBundleString("monthOf")).concat(Start.getResourceBundleString(LocalDate.now().plusMonths(calendarCounter).getMonth().toString()))));
        filteredAppointmentsData.setPredicate(null);
        filteredAppointmentsData.setPredicate(monthlyPredicate);
    }
    
    @FXML
    private void currentMonth(ActionEvent event) {
        calendarCounter = 0;
        appointmentsTable.setPlaceholder(new Label((Start.getResourceBundleString("noContent")).concat(Start.getResourceBundleString("monthOf")).concat(Start.getResourceBundleString(LocalDate.now().plusMonths(calendarCounter).getMonth().toString()))));
        filteredAppointmentsData.setPredicate(null);
        filteredAppointmentsData.setPredicate(monthlyPredicate);
    }
    
    @FXML
    private void currentWeek(ActionEvent event) {
        calendarCounter = 0;
        appointmentsTable.setPlaceholder(new Label((Start.getResourceBundleString("noContent")).concat(Start.getResourceBundleString("weekOf")).concat(LocalDate.now().plusWeeks(calendarCounter).minusDays(LocalDate.now().getDayOfWeek().getValue()).toString())));
        filteredAppointmentsData.setPredicate(null);
        filteredAppointmentsData.setPredicate(weeklyPredicate);
    }
    
    @FXML
    private void previousWeek(ActionEvent event) {
        calendarCounter--;
        appointmentsTable.setPlaceholder(new Label((Start.getResourceBundleString("noContent")).concat(Start.getResourceBundleString("weekOf")).concat(LocalDate.now().plusWeeks(calendarCounter).minusDays(LocalDate.now().getDayOfWeek().getValue()).toString())));
        filteredAppointmentsData.setPredicate(null);
        filteredAppointmentsData.setPredicate(weeklyPredicate);
    }
    
    @FXML
    private void nextWeek(ActionEvent event) {
        calendarCounter++;
        appointmentsTable.setPlaceholder(new Label((Start.getResourceBundleString("noContent")).concat(Start.getResourceBundleString("weekOf")).concat(LocalDate.now().plusWeeks(calendarCounter).minusDays(LocalDate.now().getDayOfWeek().getValue()).toString())));
        filteredAppointmentsData.setPredicate(null);
        filteredAppointmentsData.setPredicate(weeklyPredicate);
        
    }
}
