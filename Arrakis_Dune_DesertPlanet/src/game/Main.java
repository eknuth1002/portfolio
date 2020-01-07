package game;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {


    private Stage mainStage;

    private int height = 600;
    private int width = 800;

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        mainStage = primaryStage;
        mainStage.setTitle("Arrakis. Dune. Desert Planet.");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("mainScreen.fxml"));
        Parent root = loader.load();
        ((mainScreenController) loader.getController()).setController(loader.getController());
        mainStage.setScene(new Scene(root));
        mainStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }

    public void setStage(Scene scene) {
        mainStage.setScene(scene);
    }
}
