package game;

import game.Enums.landTypes;
import java.util.ArrayList;
import java.util.HashSet;
import javafx.scene.control.TextField;

public class Region {

  private String name;
  private Enums.landTypes landType;
  private short spiceSector;
  private TextField spiceBox;
  private short spiceAmount;
  private int currentSpice = 0;
  private HashSet<Region> connections;


  private ArrayList<Integer> sectors = new ArrayList<>();

  private Region() { }

  public Region(String name, String landType, short spiceSector, short spiceAmount, String sectors) {

    this.name = name;
    for (landTypes land : Enums.landTypes.values()) {
      if (land.toString().toLowerCase().equals(landType.toLowerCase())) {
        this.landType = land;
        break;
      }
    }
    this.spiceSector = spiceSector;
    this.spiceAmount = spiceAmount;
    for (String sector : sectors.split(",")) {
      this.sectors.add(Integer.parseInt(sector));
    }
  }

  public void setConnections(HashSet<Region> connections) {
    if (this.connections == null) {
      this.connections = connections;
    }
  }

  public HashSet<Region> getConnections() {
    return connections;
  }

  public void setSpiceBox(TextField spiceBox) {
    if (this.spiceBox == null) {
      this.spiceBox = spiceBox;
    }
  }

  public String getName() {
    return name;
  }

  public landTypes getLandType() {
    return landType;
  }

  public int getSpiceSector() {
    return spiceSector;
  }

  public int getSpiceAmount() {
    return spiceAmount;
  }

  public int getCurrentSpice() {
    return currentSpice;
  }

  public void addSpice(int spiceAmountToAdd) {
    currentSpice += spiceAmountToAdd;
    spiceBox.setText(String.valueOf(currentSpice));
     if (Integer.parseInt(spiceBox.getText()) > 0) {
       spiceBox.setVisible(true);
     }
     else {
       spiceBox.setVisible(false);
     }
  }

  public void removeSpice(int spiceAmountToRemove) {
    if (currentSpice - spiceAmountToRemove >= 0) {
      currentSpice -= spiceAmountToRemove;
      spiceBox.setText(String.valueOf(currentSpice));
      if (Integer.parseInt(spiceBox.getText()) == 0) {
        spiceBox.setVisible(false);
      }
    };
  }
  public ArrayList<Integer> getSectors() { return sectors; }
}
