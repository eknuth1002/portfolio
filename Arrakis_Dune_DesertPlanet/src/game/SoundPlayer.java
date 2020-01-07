package game;

import game.Enums.musicFiles;
import java.io.File;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

public class SoundPlayer {

  // to store current position
  private MediaPlayer player;
  private double maximumVolume = 1.0;

  private SoundPlayer() { }

  public SoundPlayer(double maxVolume) {
    maximumVolume = maxVolume;
  }

  public void play(musicFiles file) {
    player = new MediaPlayer(new Media(new File(file.toString()).toURI().toString()));
    player.setVolume(maximumVolume);
    player.setCycleCount(MediaPlayer.INDEFINITE);
    player.play();
  }

  //Stop the media file
  public void stop() {
    if (player.getMedia() != null) {
      player.stop();
    }
  }

  //Fade music in
  public void fadeIn(long seconds) {
    if (player.getMedia() != null) {
      Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(seconds),
          new KeyValue(player.volumeProperty(), maximumVolume, Interpolator.LINEAR)));
      timeline.play();
    }
  }

  //Fade music out
  public void fadeOut(long seconds) {
    if (player.getMedia() != null) {
      Timeline timeline = new Timeline(new KeyFrame(
          Duration.seconds(seconds),
          new KeyValue(player.volumeProperty(), 0, Interpolator.LINEAR)));
      timeline.play();
    }
  }

  //Transition media file to new music
  public void transition(long seconds, musicFiles file) {
    if (player.getMedia() != null) {
      MediaPlayer tempPlayer = new MediaPlayer(
          new Media(new File(file.toString()).toURI().toString()));
      tempPlayer.setVolume(0.0);
      tempPlayer.play();

      //Fade in temp player
      Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(seconds),
          new KeyValue(tempPlayer.volumeProperty(), maximumVolume, Interpolator.LINEAR)));
      timeline.play();

      //Fade out old player
      fadeOut(seconds);

      player = tempPlayer;
    }
  }
}