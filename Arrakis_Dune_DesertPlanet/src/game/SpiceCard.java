package game;

import game.Enums.cardTypes;
import javafx.scene.image.Image;

public class SpiceCard {
  String name;
  Enums.cardTypes type;
  Image cardImage;

  private SpiceCard() { }

  public SpiceCard(String name, String imageFile) {
    this.name = name;

    this.type = cardTypes.SPICE;

    cardImage = new Image(imageFile);
  }

  public Image getImage() { return cardImage; }
}
