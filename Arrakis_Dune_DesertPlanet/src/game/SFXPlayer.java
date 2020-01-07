package game;

import game.Enums.sfxFiles;
import java.io.File;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;

public class SFXPlayer {

  // to store current position
  private MediaPlayer player;
  private Status status;
  private double maxVolume;

  private SFXPlayer() {
  }

  //Plays a specified sfxFile at specified volume
  public SFXPlayer (double maxVolume) {
    this.maxVolume = maxVolume;

  }

  public void play(sfxFiles file) {
    if (status == Status.STOPPED) {
      player = new MediaPlayer(new Media(new File(file.toString()).toURI().toString()));
    }
    player = new MediaPlayer(new Media(new File(file.toString()).toURI().toString()));
    player.setVolume(maxVolume);
    player.setOnEndOfMedia(() -> status = Status.STOPPED);
    player.play();
    status = Status.PLAYING;
  }

  public void stop() {
    player.stop();
  }
}