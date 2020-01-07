package game;

import game.Enums.cardClassifications;
import game.Enums.cardSubTypes;
import game.Enums.cardTypes;
import javafx.scene.image.Image;

public class TreacheryCard {
  String name;
  Enums.cardTypes type;
  Image cardImage;
  Enums.cardSubTypes subtype = null;
  Enums.cardClassifications classification =  null;

  private TreacheryCard() { }

  public TreacheryCard(String name, String type, String subtype, String classification, String imageFile) {
    this.name = name;
    this.type = type.toLowerCase().equals(cardTypes.TREACHERY.toString().toLowerCase()) ? cardTypes.TREACHERY : cardTypes.JUNK;
    if (subtype != null) {
      for (cardSubTypes currentSubtype : cardSubTypes.values()) {
        if (currentSubtype.toString().toLowerCase().equals(subtype)) {
          this.subtype = currentSubtype;
        }
      }
    }
    if (classification != null) {
      for (cardClassifications cardClassification : cardClassifications.values()) {
        if (cardClassification.toString().toLowerCase().equals(classification.toLowerCase())) {
          this.classification = cardClassification;
        }
      }
    }
    cardImage = new Image(imageFile);
  }

  public Image getImage() { return cardImage; }
}
