package game;

import game.Enums.musicFiles;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

public class mainScreenController {

  @FXML
  private Button optionsButton;
  @FXML
  private Pane overlayPane;
  @FXML
  private GridPane grid;
  @FXML
  private Button newGameButton;

  public void setMaxMusicVolume(double maxMusicVolume) {
    this.maxMusicVolume = maxMusicVolume;
  }

  public void setMaxSFXVolume(double maxSFXVolume) {
    this.maxSFXVolume = maxSFXVolume;
  }

  private double maxMusicVolume;
  private double maxSFXVolume;
  SoundPlayer musicPlayer = new SoundPlayer(1);
  private mainScreenController controller;

  public mainScreenController() {
    musicPlayer.play(musicFiles.TITLE);

  }

  public void optionsButtonClick() {

    try {
      musicPlayer.stop();
      overlayPane.getChildren().clear();
      FXMLLoader pane = new FXMLLoader(getClass().getResource("optionsScreen.fxml"));
      overlayPane.getChildren().add(pane.load());
      ((OptionsScreen) pane.getController()).setMainScreenControllerPane(controller);
    } catch (IOException e) {
      e.printStackTrace();
    }

    overlayPane.visibleProperty().set(true);

  }

  public void newGameButtonClick() {
    try {
      overlayPane.getChildren().clear();
      FXMLLoader pane = new FXMLLoader(getClass().getResource("playScreen.fxml"));
      overlayPane.getChildren().add(pane.load());
      ((Game)pane.getController()).setMaxMusicVolume(maxMusicVolume);
      ((Game)pane.getController()).setMaxSFXVolume(maxSFXVolume);
    } catch (IOException e) {
      e.printStackTrace();
    }
    overlayPane.visibleProperty().set(true);
    musicPlayer.stop();
  }

  public void setController(mainScreenController controller) {
    if (this.controller == null) {
      this.controller = controller;
    }
  }

  public void unloadOverlayFrame() {
    overlayPane.setVisible(false);
  }
}
