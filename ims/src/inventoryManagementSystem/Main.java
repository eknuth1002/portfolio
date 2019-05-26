package inventoryManagementSystem;
	
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main extends Application {
	static final Model model = new Model();
	private Scene scene;
	@Override
	public void start(Stage primaryStage) {
		try {
			model.setScene(scene);
			model.setStage(primaryStage);
			Parent root = FXMLLoader.load(getClass().getResource("FXML/MainScreen.FXML"));
			scene = new Scene(root,1050,650);
			primaryStage.setMinHeight(650);
			primaryStage.setMinWidth(1050);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
