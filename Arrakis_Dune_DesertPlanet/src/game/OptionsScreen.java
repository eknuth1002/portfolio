package game;

import game.Enums.musicFiles;
import game.Enums.sfxFiles;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;

public class OptionsScreen {

  @FXML private Button doneButton;
  private mainScreenController mainScreenController;
  @FXML private Slider maxSFXVolumeSlider;
  @FXML private Slider maxMusicVolumeSlider;
  @FXML private Button musicTestButton;
  @FXML private Button sfxTestButton;
  private SFXPlayer sfxPlayer;
  private SoundPlayer musicPlayer;
  private boolean musicPlaying = false;
  private boolean sfxPlaying = false;
  @FXML
  public void initialize() {
    musicTestButton.setOnAction(event -> {
      if (musicPlaying) {
        musicPlayer.stop();
        musicTestButton.setText("Test Music Volume");
        maxMusicVolumeSlider.setDisable(false);
        musicPlaying = false;
      }
      else {
        musicPlayer = new SoundPlayer(maxMusicVolumeSlider.getValue());
        musicPlayer.play(musicFiles.ATREIDES);
        musicTestButton.setText("Stop Music");
        maxMusicVolumeSlider.setDisable(true);
        musicPlaying = true;
      }
    });
    sfxTestButton.setOnAction(event -> {
      if (sfxPlaying) {
        sfxPlayer.stop();
        sfxTestButton.setText("Test SFX Volume");
        maxSFXVolumeSlider.setDisable(false);
        sfxPlaying = false;
      }
      else {
        sfxPlayer = new SFXPlayer(maxSFXVolumeSlider.getValue());
        sfxPlayer.play(sfxFiles.STORM);
        sfxTestButton.setText("Stop SFX");
        maxSFXVolumeSlider.setDisable(true);
        sfxPlaying = true;
      }
    });

    doneButton.setOnAction(event -> {
      mainScreenController.setMaxMusicVolume(maxMusicVolumeSlider.getValue());
      mainScreenController.setMaxSFXVolume(maxSFXVolumeSlider.getValue());
      unloadFrame();
    });
  }

  private void unloadFrame() {
    mainScreenController.unloadOverlayFrame();
  }

  public void setMainScreenControllerPane(mainScreenController controller) {
    if (this.mainScreenController == null) {
      this.mainScreenController = controller;
    }
  }


}
