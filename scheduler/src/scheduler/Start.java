package scheduler;

import scheduler.AppointmentReminders.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;



/**
 *
 * @author Elliot Knuth
 */
public class Start extends Application {
    public static final DatabaseManager databaseManager = new DatabaseManager();
    private final ScheduledExecutorService service = Executors.newScheduledThreadPool(3);
    private static final List<ScheduledExecutorService> listOfExecutors = new ArrayList<>();
    
    public static void addExecutor(ScheduledExecutorService executor) {
        listOfExecutors.add(executor);
    }
    private static ResourceBundle rb;
    static Map<String, Object> controllers = new HashMap<>();
    
    static String getResourceBundleString(String field) {
        return rb.getString(field);
    }
    
    @Override
    public void start(Stage stage) throws Exception {
        addExecutor(service);
        try {
            databaseManager.connectToDatabase();
        }
        catch (ClassNotFoundException e) {
            System.out.println("Class not found.  Exiting program.");
            System.exit(0);
        }
        catch (SQLException e) {
            System.out.println("Cannot connect to the database.  Exiting program.");
            System.exit(0);
        }
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MainFXML.fxml"));
        Pane root = fxmlLoader.load();
        
        controllers.put("MainScreen", fxmlLoader.getController());
        
        Scene scene = new Scene(root);
        stage.setScene(scene);
        
        LoginController loginDialog = new LoginController();
        
        stage.show();
        AppointmentReminders appointmentReminder = new AppointmentReminders();
        createAlerts createAlarm = appointmentReminder.new createAlerts();
        service.scheduleAtFixedRate(appointmentReminder, 0, 30, TimeUnit.SECONDS);
        service.scheduleAtFixedRate(createAlarm, 1, 1, TimeUnit.MINUTES);
        
    }
    
    @Override
    public void stop() {
           listOfExecutors.forEach(executor -> executor.shutdown());
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //Set default locale for internationalization testing
        //NOTE only German and English are available with English being default
        //Locale.setDefault(Locale.GERMAN);
        
        rb = ResourceBundle.getBundle("scheduler.scheduler", Locale.getDefault());
        
        launch(args);
        
    }
    
}
