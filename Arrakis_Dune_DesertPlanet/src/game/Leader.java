package game;

import game.Enums.factions;
import javafx.scene.image.Image;

public class Leader {

  String name;
  factions faction;
  int power;
  Image image;

  private Leader() {}

  public Leader(String name, factions faction, int power, Image image) {
    this.name = name;
    this.faction = faction;
    this.power = power;
    this.image = image;
  }

  public String getName() {
    return name;
  }

  public factions getFaction() {
    return faction;
  }

  public int getPower() {
    return power;
  }

  public Image getImage() {
    return image;
  }


}
