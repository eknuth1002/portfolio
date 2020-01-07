package game;

import static game.Enums.rounds.BATTLE;
import static game.Enums.rounds.BIDDING;
import static game.Enums.rounds.COLLECTION;
import static game.Enums.rounds.REVIVAL_AND_MOVEMENT;
import static game.Enums.rounds.SPICE_BLOW;
import static game.Enums.rounds.STORM;

import game.Enums.cardClassifications;
import game.Enums.cardSubTypes;
import game.Enums.cardTypes;
import game.Enums.characterImages;
import game.Enums.difficulties;
import game.Enums.factions;
import game.Enums.landTypes;
import game.Enums.leaderImages;
import game.Enums.musicFiles;
import game.Enums.playerTypes;
import game.Enums.sfxFiles;
import game.Enums.troopImages;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Random;
import java.util.Scanner;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class Game {
  private Boolean musicPlaying = true;

  public final boolean debugOn = true;

  @FXML private AnchorPane opponentsStats;
  @FXML private AnchorPane statsArea;
  @FXML private AnchorPane map;
  @FXML private AnchorPane areaInfo;
  @FXML private ImageView mapImgView;
  @FXML private ImageView stormMarker;
  @FXML private Text areaNameText;
  @FXML private Text areaTypeText;
  @FXML private ChoiceBox<String> factionChoice1;
  @FXML private AnchorPane characterSelect;
  @FXML private ChoiceBox<String> playerType1;
  @FXML private ChoiceBox<String> factionChoice2;
  @FXML private ChoiceBox<String> playerType2;
  @FXML private Button startGameButton;
  @FXML private Slider stormMovementSelector;
  @FXML private AnchorPane stormRoundPane;

  private ArrayList<Integer> dots = new ArrayList<>(Arrays.asList(1,2,3,4,5,6));;
  private final HashMap<Integer, Integer> dotToSector = new HashMap<>();
  private ObservableList<Player> players = FXCollections.observableArrayList();
  private ObservableList<TreacheryCard> treacheryCardDeck = FXCollections.observableArrayList();
  private ObservableList<TreacheryCard> treacheryCardDiscard = FXCollections.observableArrayList();
  private ObservableList<SpiceCard> spiceCardDeck = FXCollections.observableArrayList();
  private ObservableList<SpiceCard> spiceCardDiscard = FXCollections.observableArrayList();
  private int round = 1;
  private boolean shieldWallDestroyed = false;
  private ArrayList<ArrayList<SVGPath>> sectorPaths = new ArrayList<ArrayList<SVGPath>>() {
    {
      add(0, new ArrayList<>());
      add(1, new ArrayList<>());
      add(2, new ArrayList<>());
      add(3, new ArrayList<>());
      add(4, new ArrayList<>());
      add(5, new ArrayList<>());
      add(6, new ArrayList<>());
      add(7, new ArrayList<>());
      add(8, new ArrayList<>());
      add(9, new ArrayList<>());
      add(10, new ArrayList<>());
      add(11, new ArrayList<>());
      add(12, new ArrayList<>());
      add(13, new ArrayList<>());
      add(14, new ArrayList<>());
      add(15, new ArrayList<>());
      add(16, new ArrayList<>());
      add(17, new ArrayList<>());
      add(18, new ArrayList<>());
    }
  };
  private HashMap<Region, ArrayList<SVGPath>> regionPaths = new HashMap<>();
  private HashMap<String, Region> regions = new HashMap();
  private ArrayList<Region> spiceRegions = new ArrayList<>();
  private Connection conn = null;
  private Statement stmt = null;
  private ResultSet results = null;
  public final int rotationDegrees = 20;
  public final int rotateCounterClockwise = -1;
  public final int rotateClockwise = 1;
  private int currentStormSector = 1;
  private Region currentlySelectedRegion;
  private short currentlySelectedSector;

  Comparator<Player> sortPlayers = (o1, o2) -> {
    if (dotToSector.get(o1.dot) > currentStormSector) {
      if (dotToSector.get(o2.dot) > currentStormSector) {
        return o1.dot < o2.dot ? -1 : 1;
      }
      else { return -1; }
    }
    else if (dotToSector.get(o1.dot) < currentStormSector) {
      if (dotToSector.get(o2.dot) < currentStormSector) {
        return o1.dot < o2.dot ? -1 : 1;
      }
    }

    return 1;
  };

  //Storm Round Components
  private SimpleIntegerProperty playerNumbersInputted = new SimpleIntegerProperty(0);
  private SimpleIntegerProperty numbersFromPlayers = new SimpleIntegerProperty(0);
  private HashMap<Region, HashMap<Integer, ArrayList<ImageView>>> regionTroopMarkers = new HashMap<>();
  @FXML private Button confirmStormNumberSelection;
  @FXML private Text stormRoundLabel;

  @FXML private ImageView treacheryDeckImageView;
  @FXML private ImageView treacheryDiscardPileImageView;
  @FXML private ImageView spiceDiscardPileImageView;
  @FXML private ImageView spiceDeckImageView;

  //Region's Spice Text Fields
  @FXML private TextField spiceBoxCielagoNorth;
  @FXML private TextField spiceBoxCielagoSouth;
  @FXML private TextField spiceBoxHabbanyaRidgeFlat;
  @FXML private TextField spiceBoxHabbanyaErg;
  @FXML private TextField spiceBoxTheGreatFlat;
  @FXML private TextField spiceBoxFuneralPlain;
  @FXML private TextField spiceBoxRockOutcroppings;
  @FXML private TextField spiceBoxHaggaBasin;
  @FXML private TextField spiceBoxBrokenLand;
  @FXML private TextField spiceBoxOhGap;
  @FXML private TextField spiceBoxSihayaRidge;
  @FXML private TextField spiceBoxRedChasm;
  @FXML private TextField spiceBoxWindPassNorth;
  @FXML private TextField spiceBoxSouthMesa;
  @FXML private TextField spiceBoxTheMinorErg;
  @FXML private AnchorPane regionTroopMarkerAnchorPane;
  private Player currentPlayer;
  private Enums.rounds currentRound;
  private SpiceCard wormCard;
  private TreacheryCard weatherControl;
  private TreacheryCard familyAtomics;
  private TreacheryCard tleilaxuGhola;
  private TreacheryCard hajr;


  //Combat Round Components
  @FXML private AnchorPane combatRoundPane;
  @FXML private ImageView combatWheelSpinner;
  @FXML private ImageView combatLeaderImageView;
  @FXML private Slider combatStrengthSlider;
  @FXML private ImageView combatWeaponCard;
  @FXML private ImageView combatDefenseCard;
  @FXML private AnchorPane confirmAttackPane;
  @FXML private Button confirmAttackButton;
  private ArrayList<Region> fights = new ArrayList<>();
  @FXML private AnchorPane combatPlanPane;
  @FXML private Button confirmPlanButton;
  private Player combatOpponent;


  //Bidding Round Components
  @FXML private AnchorPane biddingRoundPane;
  @FXML private ImageView biddingTreacheryCardBack1;
  @FXML private ImageView biddingTreacheryCardBack2;
  @FXML private ImageView biddingTreacheryCardBack3;
  @FXML private ImageView biddingTreacheryCardBack4;
  @FXML private ImageView biddingTreacheryCardBack5;
  @FXML private ImageView currentBiddingTreacheryCardBack;
  @FXML private Spinner treacherySpiceBidSpinner;
  @FXML private Button treacherySpiceBidButton;
  private int cardsToDeal = 0;
  private int currentBid = 0;
  private Player highestBidder;
  private HashSet<Player> passed = new HashSet<>();
  private TreacheryCard currentBiddingTreacheryCard = null;
  private ArrayList<Player> bidders = new ArrayList<>();
  private Player currentBidder;


  //Revive and Movement Round Components
  @FXML private AnchorPane reviveConfirmPane;
  @FXML private Spinner reviveTroopsSpinner;
  @FXML private Button confirmReviveButton;
  @FXML private Button cancelReviveButton;
  @FXML private AnchorPane movementConfirmPane;
  private Region moveFromRegion;
  private int moveFromSector;
  @FXML private Spinner moveTroopsSpinner;
  @FXML private Button confirmMoveButton;
  @FXML private Button cancelMoveButton;
  @FXML private Label movementPaneLabel;
  private EventHandler generalRegionClickHandler = (EventHandler<MouseEvent>) event -> {

    if (currentlySelectedRegion != null) {
      for (SVGPath regionPath : sectorPaths.get(currentlySelectedSector)) {
        if (regionPath.getId().equalsIgnoreCase(currentlySelectedRegion.getName().concat(" ").concat(
            currentlySelectedSector < 10 ? "0".concat(String.valueOf(currentlySelectedSector)) : String.valueOf(currentlySelectedSector))))  {
          regionPath.setStroke(Color.TRANSPARENT);
          break;
        }
      }
    }

    changeSelectedArea(((SVGPath)event.getSource()).getId());

    for (SVGPath regionPath : sectorPaths.get(currentlySelectedSector)) {
      if (regionPath.getId().equalsIgnoreCase(currentlySelectedRegion.getName().concat(" ").concat(
          currentlySelectedSector < 10 ? "0".concat(String.valueOf(currentlySelectedSector)) : String.valueOf(currentlySelectedSector))))  {
        regionPath.setStroke(Color.BLUE);
        break;
      }
    }

  };

  private EventHandler generalMoveClickHandler = (EventHandler<MouseEvent>) event -> {

    changeSelectedArea(((SVGPath)event.getSource()).getId());

  };


  private EventHandler fromRegionMovementClickHandler = new EventHandler<MouseEvent>() {
    @Override
    public void handle(MouseEvent event) {
      changeSelectedArea(((SVGPath) event.getSource()).getId());

      ((IntegerSpinnerValueFactory) moveTroopsSpinner.valueFactoryProperty().get())
          .maxProperty().setValue(currentPlayer.troopsOnPlanet.get(currentlySelectedRegion).size());


    }
  };

  private EventHandler toRegionMovementClickHandler = new EventHandler<ActionEvent>() {
    @Override
    public void handle(ActionEvent event) {
      moveFromRegion = currentlySelectedRegion;
      moveFromSector = currentlySelectedSector;

      for (Region region : currentPlayer.troopsOnPlanet.keySet()) {
        if (currentPlayer.troopsOnPlanet.get(region).size() > 0) {
          for (SVGPath path : regionPaths.get(region)) {
            path.setStroke(Color.TRANSPARENT);
            path.setOnMouseClicked(null);
          }
        }
      }

      for (SVGPath path: regionPaths.get(currentlySelectedRegion)) {
          path.setStroke(currentPlayer.color);
      }

      for (Region region : currentlySelectedRegion.getConnections()) {
        for (SVGPath path : regionPaths.get(region)) {
          path.setOnMouseClicked(generalMoveClickHandler);
          path.setStroke(Color.BLUE);
        }
      }

      movementPaneLabel.setText("Move Troops to which Region and Sector?");

      confirmMoveButton.setOnAction(event2 -> {
        currentPlayer.moveTroops(moveFromRegion, moveFromSector, currentlySelectedRegion, currentlySelectedSector,
            Integer.valueOf(moveTroopsSpinner.getValue().toString()));


        for (ImageView image : regionTroopMarkers.get(moveFromRegion).get(moveFromSector)) {
          if (image.getImage() == currentPlayer.troopsOnPlanet.get(moveFromRegion)
              .get(0).unitImage && currentPlayer.troopsOnPlanet.get(moveFromRegion).size() == 0){
            image.setImage(null);
          }
        }

        for (ImageView image : regionTroopMarkers.get(currentlySelectedRegion).get(Integer.valueOf(currentlySelectedSector))) {
          if (image.getImage() == null) {
            image.setImage(
                currentPlayer.troopsOnPlanet.get(currentlySelectedRegion).get(0).unitImage);
            break;
          }

        }

        for (Region region : regionPaths.keySet()) {
          for (SVGPath path : regionPaths.get(region)) {
            path.setStroke(Color.TRANSPARENT);
            path.setOnMouseClicked(generalRegionClickHandler);
          }
        }

        nextPlayer();
      });

      cancelMoveButton.setOnAction(event2 -> {
        for (Region region : currentlySelectedRegion.getConnections()) {
          for (SVGPath path : regionPaths.get(region)) {
            path.setOnMouseClicked(null);
            path.setStroke(Color.TRANSPARENT);
          }
        }

        for (Region region : currentPlayer.troopsOnPlanet.keySet()) {
          if (currentPlayer.troopsOnPlanet.get(region).size() > 0) {
            for (SVGPath path : regionPaths.get(region)) {
              path.setStroke(Color.BLUE);
              path.setOnMouseClicked(fromRegionMovementClickHandler);
            }
          }
        }

        cancelMoveButton.setOnAction(event3 -> {
          for (Region region : regions.values()) {
            for (SVGPath path : regionPaths.get(region)) {
              path.setOnMouseClicked(generalRegionClickHandler);
              path.setStroke(Color.TRANSPARENT);
            }
          }
          nextPlayer();
        });

        confirmMoveButton.setOnAction(toRegionMovementClickHandler);
      });
    }
  };

  private HashMap<Player, BattlePlan> battlePlans = new HashMap<>();
  private Player currentCombatant;
  private Player aggressor;
  @FXML private ChoiceBox combatOpponentChoiceBox;
  private TreacheryCard lasegun;
  private EventHandler combatRegionClickHandler = (EventHandler<MouseEvent>) event -> {
    if (fights.contains(regions.get(((SVGPath)event.getSource()).getId())) && currentCombatant != null) {
      changeSelectedArea(((SVGPath)event.getSource()).getId());

      combatStrengthSlider.setMax(currentCombatant.troopsOnPlanet.get(currentlySelectedRegion).size());

      for (SVGPath regionPath : sectorPaths.get(currentlySelectedSector)) {
        if (regionPath.getId().equalsIgnoreCase(currentlySelectedRegion.getName().concat(" ").concat(
            currentlySelectedSector < 10 ? "0".concat(String.valueOf(currentlySelectedSector)) : String.valueOf(currentlySelectedSector))))  {
          regionPath.setStroke(Color.TRANSPARENT);
          break;
        }
      }



      for (SVGPath regionPath : sectorPaths.get(currentlySelectedSector)) {
        if (regionPath.getId().equalsIgnoreCase(currentlySelectedRegion.getName().concat(" ").concat(
            currentlySelectedSector < 10 ? "0".concat(String.valueOf(currentlySelectedSector)) : String.valueOf(currentlySelectedSector))))  {
          regionPath.setStroke(currentPlayer.color);
          break;
        }
      }
    }

  };
  @FXML private AnchorPane fremenPlacementPane;
  @FXML private Label fremenPlacementCounter;
  @FXML private TableView opponentStatsTableView;
  @FXML private TableColumn factionColumn;
  @FXML private TableColumn spiceColumn;
  @FXML private TableColumn troopsInTankColumn;
  @FXML private TableColumn troopsInReserveColumn;
  @FXML private TableColumn treacheryCardsColumn;
  @FXML private Text areaTroopsText;
  @FXML private ImageView characterImage;
  @FXML private ImageView playerTreacheryCard1;
  @FXML private ImageView playerTreacheryCard2;
  @FXML private ImageView playerTreacheryCard3;
  @FXML private ImageView playerTreacheryCard4;
  @FXML private AnchorPane beneGesseritPrediction;
  @FXML private Button fremenPlacementConfirmButton;
  private Player beneGesseritPlayerWinPrediction = null;
  private int beneGesseritRoundWinPrediction = 0;
  @FXML private Spinner<Integer> sietchTabrSpinner;
  @FXML private Spinner<Integer> falseWallWestSector17Spinner;
  @FXML private Spinner<Integer> falseWallWestSector18Spinner;
  @FXML private Spinner<Integer> falseWallWestSector16Spinner;
  @FXML private Spinner<Integer> falseWallSouthSector4Spinner;
  @FXML private Spinner<Integer> falseWallSouthSector5Spinner;
  @FXML private AnchorPane tanksTroopPane;
  @FXML private ChoiceBox beneGesseritPlayerWinChoiceBox;
  @FXML private Spinner beneGesseritRoundWinSpinner;
  @FXML private Button beneGesseritPredictionConfirmButton;
  @FXML private ImageView playerDot1;
  @FXML private ImageView playerDot2;
  @FXML private ImageView playerDot3;
  @FXML private ImageView playerDot4;
  @FXML private ImageView playerDot5;
  @FXML private ImageView playerDot6;
  private boolean gameOver = false;
  @FXML private AnchorPane landTroopsConfirmPane;
  @FXML private Spinner landTroopsSpinner;
  @FXML private Button confirmLandButton;
  @FXML private Button cancelLandButton;
  private ArrayList<Region> fremenRegionList = new ArrayList<>();
  @FXML private Label currentBidLabel;
  private boolean extraMovement;
  @FXML private ImageView leader1;
  @FXML private ImageView leader2;
  @FXML private ImageView leader3;
  @FXML private ImageView leader4;
  @FXML private ImageView leader5;
  @FXML private Pane winnerPane;
  @FXML private ImageView winnerImage;
  @FXML private TextArea winnerText;
  @FXML private AnchorPane mainPane;
  @FXML private TabPane debugPane;
  @FXML private TextArea spiceDeckReportArea;
  @FXML private TextArea treacheryDeckReportArea;
  @FXML private ChoiceBox<String> beneGesseritPlayerPredictionDebugArea;
  @FXML private TextField beneGesseritRoundPredictionDebugArea;
  @FXML private TextArea player1DebugArea;
  @FXML private TextArea player2DebugArea;
  @FXML private Button player1WinDebugButton;
  @FXML private Button player2WinDebugButton;
  @FXML private Label beneGesseritPlayerLabelDebugArea;
  @FXML private Label fremenPlayingLabelDebugArea;
  @FXML private Button fremenWinButtonDebugArea;
  private boolean fremenPlaying = false;
  private boolean beneGesseritPlaying = false;
  @FXML private Button beneGesseritWinButtonDebugArea;
  private double maxMusicVolume = -1;
  private double maxSFXVolume = -1;
  SoundPlayer musicPlayer = new SoundPlayer(maxMusicVolume);
  SFXPlayer sfxPlayer = new SFXPlayer(maxSFXVolume);


  @FXML
  public void initialize() {
    try {


      initializeChoiceBoxes();
      connectToDatabase();
      loadUI();
      loadTreacheryDeck();
      loadSpiceDeck();

      sietchTabrSpinner.valueProperty().addListener(new ChangeListener<Number>() {
        @Override
        public void changed(ObservableValue<? extends Number> observable, Number oldValue,
            Number newValue) {
          if (oldValue.intValue() < newValue.intValue()) {
            fremenPlacementCounter
                .setText(String.valueOf(Integer.parseInt(fremenPlacementCounter.getText()) - 1));
          }
          else {
            fremenPlacementCounter
                .setText(String.valueOf(Integer.parseInt(fremenPlacementCounter.getText()) + 1));
          }

          ((IntegerSpinnerValueFactory) sietchTabrSpinner.getValueFactory()).setMax(10 -
              (falseWallSouthSector4Spinner.getValue() +
              falseWallSouthSector5Spinner.getValue() +
              falseWallWestSector16Spinner.getValue() +
              falseWallWestSector17Spinner.getValue() +
              falseWallWestSector18Spinner.getValue()));

          ((IntegerSpinnerValueFactory) falseWallSouthSector4Spinner.getValueFactory()).setMax(10 -
              (sietchTabrSpinner.getValue() +
                  falseWallSouthSector5Spinner.getValue() +
                  falseWallWestSector16Spinner.getValue() +
                  falseWallWestSector17Spinner.getValue() +
                  falseWallWestSector18Spinner.getValue()));

          ((IntegerSpinnerValueFactory) falseWallSouthSector5Spinner.getValueFactory()).setMax(10 -
              (sietchTabrSpinner.getValue() +
                  falseWallSouthSector4Spinner.getValue() +
                  falseWallWestSector16Spinner.getValue() +
                  falseWallWestSector17Spinner.getValue() +
                  falseWallWestSector18Spinner.getValue()));

          ((IntegerSpinnerValueFactory) falseWallWestSector16Spinner.getValueFactory()).setMax(10 -
              (sietchTabrSpinner.getValue() +
                  falseWallSouthSector4Spinner.getValue() +
                  falseWallSouthSector5Spinner.getValue() +
                  falseWallWestSector17Spinner.getValue() +
                  falseWallWestSector18Spinner.getValue()));

          ((IntegerSpinnerValueFactory) falseWallWestSector17Spinner.getValueFactory()).setMax(10 -
              (sietchTabrSpinner.getValue() +
                  falseWallSouthSector4Spinner.getValue() +
                  falseWallSouthSector5Spinner.getValue() +
                  falseWallWestSector16Spinner.getValue() +
                  falseWallWestSector18Spinner.getValue()));

          ((IntegerSpinnerValueFactory) falseWallWestSector18Spinner.getValueFactory()).setMax(10 -
              (sietchTabrSpinner.getValue() +
                  falseWallSouthSector4Spinner.getValue() +
                  falseWallSouthSector5Spinner.getValue() +
                  falseWallWestSector16Spinner.getValue() +
                  falseWallWestSector17Spinner.getValue()));
        }
      });

      falseWallSouthSector4Spinner.valueProperty().addListener(new ChangeListener<Number>() {
        @Override
        public void changed(ObservableValue<? extends Number> observable, Number oldValue,
            Number newValue) {
          if (oldValue.intValue() < newValue.intValue()) {
            fremenPlacementCounter
                .setText(String.valueOf(Integer.parseInt(fremenPlacementCounter.getText()) - 1));
          }
          else {
            fremenPlacementCounter
                .setText(String.valueOf(Integer.parseInt(fremenPlacementCounter.getText()) + 1));
          }

          ((IntegerSpinnerValueFactory) sietchTabrSpinner.getValueFactory()).setMax(10 -
              (falseWallSouthSector4Spinner.getValue() +
                  falseWallSouthSector5Spinner.getValue() +
                  falseWallWestSector16Spinner.getValue() +
                  falseWallWestSector17Spinner.getValue() +
                  falseWallWestSector18Spinner.getValue()));

          ((IntegerSpinnerValueFactory) falseWallSouthSector4Spinner.getValueFactory()).setMax(10 -
              (sietchTabrSpinner.getValue() +
                  falseWallSouthSector5Spinner.getValue() +
                  falseWallWestSector16Spinner.getValue() +
                  falseWallWestSector17Spinner.getValue() +
                  falseWallWestSector18Spinner.getValue()));

          ((IntegerSpinnerValueFactory) falseWallSouthSector5Spinner.getValueFactory()).setMax(10 -
              (sietchTabrSpinner.getValue() +
                  falseWallSouthSector4Spinner.getValue() +
                  falseWallWestSector16Spinner.getValue() +
                  falseWallWestSector17Spinner.getValue() +
                  falseWallWestSector18Spinner.getValue()));

          ((IntegerSpinnerValueFactory) falseWallWestSector16Spinner.getValueFactory()).setMax(10 -
              (sietchTabrSpinner.getValue() +
                  falseWallSouthSector4Spinner.getValue() +
                  falseWallSouthSector5Spinner.getValue() +
                  falseWallWestSector17Spinner.getValue() +
                  falseWallWestSector18Spinner.getValue()));

          ((IntegerSpinnerValueFactory) falseWallWestSector17Spinner.getValueFactory()).setMax(10 -
              (sietchTabrSpinner.getValue() +
                  falseWallSouthSector4Spinner.getValue() +
                  falseWallSouthSector5Spinner.getValue() +
                  falseWallWestSector16Spinner.getValue() +
                  falseWallWestSector18Spinner.getValue()));

          ((IntegerSpinnerValueFactory) falseWallWestSector18Spinner.getValueFactory()).setMax(10 -
              (sietchTabrSpinner.getValue() +
                  falseWallSouthSector4Spinner.getValue() +
                  falseWallSouthSector5Spinner.getValue() +
                  falseWallWestSector16Spinner.getValue() +
                  falseWallWestSector17Spinner.getValue()));
        }
      });

      falseWallSouthSector5Spinner.valueProperty().addListener(new ChangeListener<Number>() {
        @Override
        public void changed(ObservableValue<? extends Number> observable, Number oldValue,
            Number newValue) {
          if (oldValue.intValue() < newValue.intValue()) {
            fremenPlacementCounter
                .setText(String.valueOf(Integer.parseInt(fremenPlacementCounter.getText()) - 1));
          }
          else {
            fremenPlacementCounter
                .setText(String.valueOf(Integer.parseInt(fremenPlacementCounter.getText()) + 1));
          }

          ((IntegerSpinnerValueFactory) sietchTabrSpinner.getValueFactory()).setMax(10 -
              (falseWallSouthSector4Spinner.getValue() +
                  falseWallSouthSector5Spinner.getValue() +
                  falseWallWestSector16Spinner.getValue() +
                  falseWallWestSector17Spinner.getValue() +
                  falseWallWestSector18Spinner.getValue()));

          ((IntegerSpinnerValueFactory) falseWallSouthSector4Spinner.getValueFactory()).setMax(10 -
              (sietchTabrSpinner.getValue() +
                  falseWallSouthSector5Spinner.getValue() +
                  falseWallWestSector16Spinner.getValue() +
                  falseWallWestSector17Spinner.getValue() +
                  falseWallWestSector18Spinner.getValue()));

          ((IntegerSpinnerValueFactory) falseWallSouthSector5Spinner.getValueFactory()).setMax(10 -
              (sietchTabrSpinner.getValue() +
                  falseWallSouthSector4Spinner.getValue() +
                  falseWallWestSector16Spinner.getValue() +
                  falseWallWestSector17Spinner.getValue() +
                  falseWallWestSector18Spinner.getValue()));

          ((IntegerSpinnerValueFactory) falseWallWestSector16Spinner.getValueFactory()).setMax(10 -
              (sietchTabrSpinner.getValue() +
                  falseWallSouthSector4Spinner.getValue() +
                  falseWallSouthSector5Spinner.getValue() +
                  falseWallWestSector17Spinner.getValue() +
                  falseWallWestSector18Spinner.getValue()));

          ((IntegerSpinnerValueFactory) falseWallWestSector17Spinner.getValueFactory()).setMax(10 -
              (sietchTabrSpinner.getValue() +
                  falseWallSouthSector4Spinner.getValue() +
                  falseWallSouthSector5Spinner.getValue() +
                  falseWallWestSector16Spinner.getValue() +
                  falseWallWestSector18Spinner.getValue()));

          ((IntegerSpinnerValueFactory) falseWallWestSector18Spinner.getValueFactory()).setMax(10 -
              (sietchTabrSpinner.getValue() +
                  falseWallSouthSector4Spinner.getValue() +
                  falseWallSouthSector5Spinner.getValue() +
                  falseWallWestSector16Spinner.getValue() +
                  falseWallWestSector17Spinner.getValue()));
        }
      });

      falseWallWestSector16Spinner.valueProperty().addListener(new ChangeListener<Number>() {
        @Override
        public void changed(ObservableValue<? extends Number> observable, Number oldValue,
            Number newValue) {
          if (oldValue.intValue() < newValue.intValue()) {
            fremenPlacementCounter
                .setText(String.valueOf(Integer.parseInt(fremenPlacementCounter.getText()) - 1));
          }
          else {
            fremenPlacementCounter
                .setText(String.valueOf(Integer.parseInt(fremenPlacementCounter.getText()) + 1));
          }

          ((IntegerSpinnerValueFactory) sietchTabrSpinner.getValueFactory()).setMax(10 -
              (falseWallSouthSector4Spinner.getValue() +
                  falseWallSouthSector5Spinner.getValue() +
                  falseWallWestSector16Spinner.getValue() +
                  falseWallWestSector17Spinner.getValue() +
                  falseWallWestSector18Spinner.getValue()));

          ((IntegerSpinnerValueFactory) falseWallSouthSector4Spinner.getValueFactory()).setMax(10 -
              (sietchTabrSpinner.getValue() +
                  falseWallSouthSector5Spinner.getValue() +
                  falseWallWestSector16Spinner.getValue() +
                  falseWallWestSector17Spinner.getValue() +
                  falseWallWestSector18Spinner.getValue()));

          ((IntegerSpinnerValueFactory) falseWallSouthSector5Spinner.getValueFactory()).setMax(10 -
              (sietchTabrSpinner.getValue() +
                  falseWallSouthSector4Spinner.getValue() +
                  falseWallWestSector16Spinner.getValue() +
                  falseWallWestSector17Spinner.getValue() +
                  falseWallWestSector18Spinner.getValue()));

          ((IntegerSpinnerValueFactory) falseWallWestSector16Spinner.getValueFactory()).setMax(10 -
              (sietchTabrSpinner.getValue() +
                  falseWallSouthSector4Spinner.getValue() +
                  falseWallSouthSector5Spinner.getValue() +
                  falseWallWestSector17Spinner.getValue() +
                  falseWallWestSector18Spinner.getValue()));

          ((IntegerSpinnerValueFactory) falseWallWestSector17Spinner.getValueFactory()).setMax(10 -
              (sietchTabrSpinner.getValue() +
                  falseWallSouthSector4Spinner.getValue() +
                  falseWallSouthSector5Spinner.getValue() +
                  falseWallWestSector16Spinner.getValue() +
                  falseWallWestSector18Spinner.getValue()));

          ((IntegerSpinnerValueFactory) falseWallWestSector18Spinner.getValueFactory()).setMax(10 -
              (sietchTabrSpinner.getValue() +
                  falseWallSouthSector4Spinner.getValue() +
                  falseWallSouthSector5Spinner.getValue() +
                  falseWallWestSector16Spinner.getValue() +
                  falseWallWestSector17Spinner.getValue()));
        }
      });

      falseWallWestSector17Spinner.valueProperty().addListener(new ChangeListener<Number>() {
        @Override
        public void changed(ObservableValue<? extends Number> observable, Number oldValue,
            Number newValue) {
          if (oldValue.intValue() < newValue.intValue()) {
            fremenPlacementCounter
                .setText(String.valueOf(Integer.parseInt(fremenPlacementCounter.getText()) - 1));
          }
          else {
            fremenPlacementCounter
                .setText(String.valueOf(Integer.parseInt(fremenPlacementCounter.getText()) + 1));
          }

          ((IntegerSpinnerValueFactory) sietchTabrSpinner.getValueFactory()).setMax(10 -
              (falseWallSouthSector4Spinner.getValue() +
                  falseWallSouthSector5Spinner.getValue() +
                  falseWallWestSector16Spinner.getValue() +
                  falseWallWestSector17Spinner.getValue() +
                  falseWallWestSector18Spinner.getValue()));

          ((IntegerSpinnerValueFactory) falseWallSouthSector4Spinner.getValueFactory()).setMax(10 -
              (sietchTabrSpinner.getValue() +
                  falseWallSouthSector5Spinner.getValue() +
                  falseWallWestSector16Spinner.getValue() +
                  falseWallWestSector17Spinner.getValue() +
                  falseWallWestSector18Spinner.getValue()));

          ((IntegerSpinnerValueFactory) falseWallSouthSector5Spinner.getValueFactory()).setMax(10 -
              (sietchTabrSpinner.getValue() +
                  falseWallSouthSector4Spinner.getValue() +
                  falseWallWestSector16Spinner.getValue() +
                  falseWallWestSector17Spinner.getValue() +
                  falseWallWestSector18Spinner.getValue()));

          ((IntegerSpinnerValueFactory) falseWallWestSector16Spinner.getValueFactory()).setMax(10 -
              (sietchTabrSpinner.getValue() +
                  falseWallSouthSector4Spinner.getValue() +
                  falseWallSouthSector5Spinner.getValue() +
                  falseWallWestSector17Spinner.getValue() +
                  falseWallWestSector18Spinner.getValue()));

          ((IntegerSpinnerValueFactory) falseWallWestSector17Spinner.getValueFactory()).setMax(10 -
              (sietchTabrSpinner.getValue() +
                  falseWallSouthSector4Spinner.getValue() +
                  falseWallSouthSector5Spinner.getValue() +
                  falseWallWestSector16Spinner.getValue() +
                  falseWallWestSector18Spinner.getValue()));

          ((IntegerSpinnerValueFactory) falseWallWestSector18Spinner.getValueFactory()).setMax(10 -
              (sietchTabrSpinner.getValue() +
                  falseWallSouthSector4Spinner.getValue() +
                  falseWallSouthSector5Spinner.getValue() +
                  falseWallWestSector16Spinner.getValue() +
                  falseWallWestSector17Spinner.getValue()));
        }
      });

      falseWallWestSector18Spinner.valueProperty().addListener(new ChangeListener<Number>() {
        @Override
        public void changed(ObservableValue<? extends Number> observable, Number oldValue,
            Number newValue) {
          if (oldValue.intValue() < newValue.intValue()) {
            fremenPlacementCounter
                .setText(String.valueOf(Integer.parseInt(fremenPlacementCounter.getText()) - 1));
          }
          else {
            fremenPlacementCounter
                .setText(String.valueOf(Integer.parseInt(fremenPlacementCounter.getText()) + 1));
          }

          ((IntegerSpinnerValueFactory) sietchTabrSpinner.getValueFactory()).setMax(10 -
              (falseWallSouthSector4Spinner.getValue() +
                  falseWallSouthSector5Spinner.getValue() +
                  falseWallWestSector16Spinner.getValue() +
                  falseWallWestSector17Spinner.getValue() +
                  falseWallWestSector18Spinner.getValue()));

          ((IntegerSpinnerValueFactory) falseWallSouthSector4Spinner.getValueFactory()).setMax(10 -
              (sietchTabrSpinner.getValue() +
                  falseWallSouthSector5Spinner.getValue() +
                  falseWallWestSector16Spinner.getValue() +
                  falseWallWestSector17Spinner.getValue() +
                  falseWallWestSector18Spinner.getValue()));

          ((IntegerSpinnerValueFactory) falseWallSouthSector5Spinner.getValueFactory()).setMax(10 -
              (sietchTabrSpinner.getValue() +
                  falseWallSouthSector4Spinner.getValue() +
                  falseWallWestSector16Spinner.getValue() +
                  falseWallWestSector17Spinner.getValue() +
                  falseWallWestSector18Spinner.getValue()));

          ((IntegerSpinnerValueFactory) falseWallWestSector16Spinner.getValueFactory()).setMax(10 -
              (sietchTabrSpinner.getValue() +
                  falseWallSouthSector4Spinner.getValue() +
                  falseWallSouthSector5Spinner.getValue() +
                  falseWallWestSector17Spinner.getValue() +
                  falseWallWestSector18Spinner.getValue()));

          ((IntegerSpinnerValueFactory) falseWallWestSector17Spinner.getValueFactory()).setMax(10 -
              (sietchTabrSpinner.getValue() +
                  falseWallSouthSector4Spinner.getValue() +
                  falseWallSouthSector5Spinner.getValue() +
                  falseWallWestSector16Spinner.getValue() +
                  falseWallWestSector18Spinner.getValue()));

          ((IntegerSpinnerValueFactory) falseWallWestSector18Spinner.getValueFactory()).setMax(10 -
              (sietchTabrSpinner.getValue() +
                  falseWallSouthSector4Spinner.getValue() +
                  falseWallSouthSector5Spinner.getValue() +
                  falseWallWestSector16Spinner.getValue() +
                  falseWallWestSector17Spinner.getValue()));
        }
      });

      spiceCardDeck.addListener((ListChangeListener<? super SpiceCard>) c -> {
        if (spiceCardDeck.size() == 0) {
          spiceDeckImageView.setVisible(false);
        }
        else if (!spiceDeckImageView.isVisible()) {
          spiceDeckImageView.setVisible(true);
        }
      });
      treacheryCardDeck.addListener((ListChangeListener<? super TreacheryCard>) c -> {
        if (treacheryCardDeck.size() == 0) {
          treacheryDeckImageView.setVisible(false);
        }
        else if (!spiceDeckImageView.isVisible()) {
          treacheryDeckImageView.setVisible(true);
        }
      });

      spiceCardDiscard.addListener((ListChangeListener<SpiceCard>) c -> {
        if (spiceCardDiscard.size() == 0) {
          spiceDiscardPileImageView.setVisible(false);
        } else {
          spiceDiscardPileImageView.setImage(spiceCardDiscard.get(spiceCardDiscard.size() - 1).cardImage);

          if (!spiceDiscardPileImageView.isVisible()) {
            spiceDiscardPileImageView.setVisible(true);
          }
        }
      });

      treacheryCardDiscard.addListener((ListChangeListener<TreacheryCard>) c -> {
        if (treacheryCardDiscard.size() == 0) {
          treacheryDiscardPileImageView.setVisible(false);
        } else {
          treacheryDiscardPileImageView.setImage(treacheryCardDiscard.get(treacheryCardDiscard.size() - 1).cardImage);

          if (!treacheryDiscardPileImageView.isVisible()) {
            treacheryDiscardPileImageView.setVisible(true);
          }
        }
      });

      characterSelect.setVisible(true);

      startGameButton.setOnAction(event -> {
        if (factionChoice1.getSelectionModel().getSelectedIndex() != -1 &&
            playerType1.getSelectionModel().getSelectedIndex() != -1 &&
            factionChoice2.getSelectionModel().getSelectedIndex() != -1 &&
            playerType2.getSelectionModel().getSelectedIndex() != -1) {
          players.clear();
          String firstFactionChoice = factionChoice1.getSelectionModel().getSelectedItem()
              .toString();
          for (factions faction : factions.values()) {
            if (faction.toString().equals(firstFactionChoice)) {
              for (playerTypes playerType : playerTypes.values()) {
                if (playerType.toString().equalsIgnoreCase( playerType1.getSelectionModel()
                    .getSelectedItem().toString())) {
                  players.add(new Player(faction, playerType));
                }
              }
            }
          }

          String secondFactionChoice = factionChoice2.getSelectionModel().getSelectedItem()
              .toString();
          for (factions faction : factions.values()) {
            if (faction.toString().equals(secondFactionChoice)) {
              for (playerTypes playerType : playerTypes.values()) {
                if (playerType.toString().equalsIgnoreCase( playerType2.getSelectionModel()
                    .getSelectedItem().toString())) {
                  players.add(new Player(faction, playerType));
                }
              }
            }
          }

          characterSelect.setVisible(false);
          for (Player player : players) {
            switch (player.dot) {
              case 1:
                playerDot1.setImage(player.troopsInReserve.get(0).unitImage);
                break;
              case 2:
                playerDot2.setImage(player.troopsInReserve.get(0).unitImage);
                break;
              case 3:
                playerDot3.setImage(player.troopsInReserve.get(0).unitImage);
                break;
              case 4:
                playerDot4.setImage(player.troopsInReserve.get(0).unitImage);
                break;
              case 5:
                playerDot5.setImage(player.troopsInReserve.get(0).unitImage);
                break;
              case 6:
                playerDot6.setImage(player.troopsInReserve.get(0).unitImage);
                break;
            }
          }
          play();
        }
      });

      playerNumbersInputted.addListener((observable, oldValue, newValue) -> {
        if (newValue.intValue() == players.size()) {
          rotateStormMarker( -1, numbersFromPlayers.getValue());
          stormRoundPane.setVisible(false);
        }
      });
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void connectToDatabase() {
    try {
      Properties props = new Properties();
      props.setProperty("user","generic_user");
      props.put("password","testpass");
      conn = DriverManager.getConnection("jdbc:h2:./duneGameInfo.db", props);
    } catch (SQLException e) {
      System.out.println(e);
      e.printStackTrace();
    }
  }

  public void loadUI() throws IOException {

    statsArea.setStyle("-fx-background-color: #622022");

    factionColumn.setCellValueFactory(new PropertyValueFactory<Player, String>("factionDataModel"));
    spiceColumn.setCellValueFactory(new PropertyValueFactory<Player, Integer>("spiceDataModel"));
    troopsInTankColumn.setCellValueFactory(new PropertyValueFactory<Player, Integer>("troopsInTankDataModel"));
    troopsInReserveColumn.setCellValueFactory(new PropertyValueFactory<Player, Integer>("troopsInReserveDataModel"));
    treacheryCardsColumn.setCellValueFactory(new PropertyValueFactory<Player, Integer>("treacheryCardsDataModel"));

    opponentStatsTableView.setItems(players);

    playerTreacheryCard1.setOnMouseClicked(event -> {
      if (playerTreacheryCard1.getImage() != null) {
        if (currentRound == STORM && currentPlayer.treacheryCardHand.get(0).equals(weatherControl)) {
          playerNumbersInputted.set(1);
          stormMovementSelector.setMax(10);

          currentStormSector = (short) Math.abs((short) stormMarker.getRotate() / rotationDegrees) + 1;
          stormRoundPane.setVisible(true);
          numbersFromPlayers.set(0);
          chooseStormNumber();
          playerTreacheryCard1.setImage(null);
          treacheryCardDiscard.add(currentPlayer.treacheryCardHand.get(0));
          currentPlayer.treacheryCardHand.remove(currentPlayer.treacheryCardHand.get(0));
          return;
        }

        if (currentRound == REVIVAL_AND_MOVEMENT &&
            currentPlayer.treacheryCardHand.get(0).equals(tleilaxuGhola) && (
            currentPlayer.troopsInTanksDataModel.get() > 0 || currentPlayer.leadersInTank.size() == 0)) {
          for (int i = 0; i < currentPlayer.troopsInTanksDataModel.get() && i < 5; i++) {
            currentPlayer.reviveTroops(1);
          }
          playerTreacheryCard1.setImage(null);
          treacheryCardDiscard.add(currentPlayer.treacheryCardHand.get(0));
          currentPlayer.treacheryCardHand.remove(currentPlayer.treacheryCardHand.get(0));
          return;
        }

        if (currentRound == REVIVAL_AND_MOVEMENT &&
            currentPlayer.treacheryCardHand.get(0).equals(hajr)) {
          extraMovement = true;
          playerTreacheryCard1.setImage(null);
          treacheryCardDiscard.add(currentPlayer.treacheryCardHand.get(0));
          currentPlayer.treacheryCardHand.remove(currentPlayer.treacheryCardHand.get(0));
          return;
        }

        if (currentPlayer.treacheryCardHand.get(0).equals(familyAtomics)) {
          if (currentPlayer.troopsOnPlanet.containsKey(regions.get("Shield Wall"))) {
            shieldWallDestroyed = true;
            playerTreacheryCard1.setImage(null);
            treacheryCardDiscard.add(currentPlayer.treacheryCardHand.get(0));
            currentPlayer.treacheryCardHand.remove(currentPlayer.treacheryCardHand.get(0));
            return;
          }
          else {
            for (Region region : regions.get("Shield Wall").getConnections()) {
              if (currentPlayer.troopsOnPlanet.containsKey(region)) {
                shieldWallDestroyed = true;
                playerTreacheryCard1.setImage(null);
                treacheryCardDiscard.add(currentPlayer.treacheryCardHand.get(0));
                currentPlayer.treacheryCardHand.remove(currentPlayer.treacheryCardHand.get(0));
                return;
              }
            }
          }
        }

        if (currentRound.equals(BATTLE) && (
            currentPlayer.treacheryCardHand.get(0).name.equalsIgnoreCase("Cheap Hero") ||
                currentPlayer.treacheryCardHand.get(0).name.equalsIgnoreCase("Cheap Heroine"))) {
          combatLeaderImageView.setImage(currentPlayer.treacheryCardHand.get(0).cardImage);
          battlePlans.get(currentCombatant).setLeader(new Leader("Cheap Hero/ine", currentCombatant.faction, 0,  currentPlayer.treacheryCardHand.get(0).cardImage));
        }

        if (currentRound.equals(BATTLE) && battlePlans.get(currentPlayer).leader != null) {
          if (currentPlayer.treacheryCardHand.get(0).subtype == cardSubTypes.WEAPON) {
            battlePlans.get(currentPlayer).weapon = currentPlayer.treacheryCardHand.get(0);
            combatWeaponCard.setImage(playerTreacheryCard1.getImage());
          }
          else if(currentPlayer.treacheryCardHand.get(0).subtype == cardSubTypes.DEFENSE) {
            battlePlans.get(currentPlayer).defense = currentPlayer.treacheryCardHand.get(0);
            combatDefenseCard.setImage(playerTreacheryCard1.getImage());
          }

        }
      }
    });
    playerTreacheryCard2.setOnMouseClicked(event -> {
      if (playerTreacheryCard2.getImage() != null) {
        if (currentRound == STORM && currentPlayer.treacheryCardHand.get(1).equals(weatherControl)) {
          playerNumbersInputted.set(1);
          stormMovementSelector.setMax(10);

          currentStormSector = (short) Math.abs((short) stormMarker.getRotate() / rotationDegrees) + 1;
          stormRoundPane.setVisible(true);
          numbersFromPlayers.set(0);
          chooseStormNumber();
          playerTreacheryCard2.setImage(null);
          treacheryCardDiscard.add(currentPlayer.treacheryCardHand.get(1));
          currentPlayer.treacheryCardHand.remove(currentPlayer.treacheryCardHand.get(1));
          return;
        }

        if (currentRound == REVIVAL_AND_MOVEMENT &&
            currentPlayer.treacheryCardHand.get(1).equals(tleilaxuGhola) &&
            currentPlayer.troopsInTanksDataModel.get() > 0) {
          for (int i = 0; i < currentPlayer.troopsInTanksDataModel.get() && i < 5; i++) {
            currentPlayer.reviveTroops(1);
          }
          playerTreacheryCard2.setImage(null);
          treacheryCardDiscard.add(currentPlayer.treacheryCardHand.get(1));
          currentPlayer.treacheryCardHand.remove(currentPlayer.treacheryCardHand.get(1));
          return;
        }

        if (currentRound == REVIVAL_AND_MOVEMENT &&
            currentPlayer.treacheryCardHand.get(1).equals(hajr)) {
          extraMovement = true;
          playerTreacheryCard2.setImage(null);
          treacheryCardDiscard.add(currentPlayer.treacheryCardHand.get(1));
          currentPlayer.treacheryCardHand.remove(currentPlayer.treacheryCardHand.get(1));
          return;
        }

        if (currentPlayer.treacheryCardHand.get(1).equals(familyAtomics)) {
          if (currentPlayer.troopsOnPlanet.containsKey(regions.get("Shield Wall"))) {
            shieldWallDestroyed = true;
            playerTreacheryCard2.setImage(null);
            treacheryCardDiscard.add(currentPlayer.treacheryCardHand.get(1));
            currentPlayer.treacheryCardHand.remove(currentPlayer.treacheryCardHand.get(1));
            return;
          }
          else {
            for (Region region : regions.get("Shield Wall").getConnections()) {
              if (currentPlayer.troopsOnPlanet.containsKey(region)) {
                shieldWallDestroyed = true;
                playerTreacheryCard2.setImage(null);
                treacheryCardDiscard.add(currentPlayer.treacheryCardHand.get(1));
                currentPlayer.treacheryCardHand.remove(currentPlayer.treacheryCardHand.get(1));
                return;
              }
            }
          }
        }

        if (currentRound.equals(BATTLE) && (
            currentPlayer.treacheryCardHand.get(1).name.equalsIgnoreCase("Cheap Hero") ||
                currentPlayer.treacheryCardHand.get(1).name.equalsIgnoreCase("Cheap Heroine"))) {
          combatLeaderImageView.setImage(currentPlayer.treacheryCardHand.get(1).cardImage);
          battlePlans.get(currentCombatant).setLeader(new Leader("Cheap Hero/ine", currentCombatant.faction, 0,  combatLeaderImageView.getImage()));
        }

        if (currentRound.equals(BATTLE) && battlePlans.get(currentPlayer).leader != null) {
          if (currentPlayer.treacheryCardHand.get(1).subtype == cardSubTypes.WEAPON) {
            battlePlans.get(currentPlayer).weapon = currentPlayer.treacheryCardHand.get(1);
            combatWeaponCard.setImage(playerTreacheryCard2.getImage());
          }
          else if(currentPlayer.treacheryCardHand.get(1).subtype == cardSubTypes.DEFENSE) {
            battlePlans.get(currentPlayer).defense = currentPlayer.treacheryCardHand.get(1);
            combatDefenseCard.setImage(playerTreacheryCard1.getImage());
          }
        }
      }
    });
    playerTreacheryCard3.setOnMouseClicked(event -> {
      if (playerTreacheryCard3.getImage() != null) {
        if (currentRound == STORM && currentPlayer.treacheryCardHand.get(2).equals(weatherControl)) {
          playerNumbersInputted.set(1);
          stormMovementSelector.setMax(10);

          currentStormSector = (short) Math.abs((short) stormMarker.getRotate() / rotationDegrees) + 1;
          stormRoundPane.setVisible(true);
          numbersFromPlayers.set(0);
          chooseStormNumber();
          playerTreacheryCard3.setImage(null);
          treacheryCardDiscard.add(currentPlayer.treacheryCardHand.get(2));
          currentPlayer.treacheryCardHand.remove(currentPlayer.treacheryCardHand.get(2));
          return;
        }

        if (currentRound == REVIVAL_AND_MOVEMENT &&
            currentPlayer.treacheryCardHand.get(2).equals(tleilaxuGhola) &&
            currentPlayer.troopsInTanksDataModel.get() > 0) {
          for (int i = 0; i < currentPlayer.troopsInTanksDataModel.get() && i < 5; i++) {
            currentPlayer.reviveTroops(1);
          }
          playerTreacheryCard3.setImage(null);
          treacheryCardDiscard.add(currentPlayer.treacheryCardHand.get(2));
          currentPlayer.treacheryCardHand.remove(currentPlayer.treacheryCardHand.get(2));
          return;
        }

        if (currentRound == REVIVAL_AND_MOVEMENT &&
            currentPlayer.treacheryCardHand.get(2).equals(hajr)) {
          extraMovement = true;
          playerTreacheryCard3.setImage(null);
          treacheryCardDiscard.add(currentPlayer.treacheryCardHand.get(2));
          currentPlayer.treacheryCardHand.remove(currentPlayer.treacheryCardHand.get(2));
          return;
        }

        if (currentPlayer.treacheryCardHand.get(2).equals(familyAtomics)) {
          if (currentPlayer.troopsOnPlanet.containsKey(regions.get("Shield Wall"))) {
            shieldWallDestroyed = true;
            playerTreacheryCard3.setImage(null);
            treacheryCardDiscard.add(currentPlayer.treacheryCardHand.get(2));
            currentPlayer.treacheryCardHand.remove(currentPlayer.treacheryCardHand.get(2));
            return;
          }
          else {
            for (Region region : regions.get("Shield Wall").getConnections()) {
              if (currentPlayer.troopsOnPlanet.containsKey(region)) {
                shieldWallDestroyed = true;
                playerTreacheryCard3.setImage(null);
                treacheryCardDiscard.add(currentPlayer.treacheryCardHand.get(2));
                currentPlayer.treacheryCardHand.remove(currentPlayer.treacheryCardHand.get(2));
                return;
              }
            }
          }
        }

        if (currentRound.equals(BATTLE) && (
            currentPlayer.treacheryCardHand.get(2).name.equalsIgnoreCase("Cheap Hero") ||
                currentPlayer.treacheryCardHand.get(2).name.equalsIgnoreCase("Cheap Heroine"))) {
          combatLeaderImageView.setImage(currentPlayer.treacheryCardHand.get(2).cardImage);
          battlePlans.get(currentCombatant).setLeader(new Leader("Cheap Hero/ine", currentCombatant.faction, 0,  combatLeaderImageView.getImage()));
        }

        if (currentRound.equals(BATTLE) && battlePlans.get(currentPlayer).leader != null) {
          if (currentPlayer.treacheryCardHand.get(2).subtype == cardSubTypes.WEAPON) {
            battlePlans.get(currentPlayer).weapon = currentPlayer.treacheryCardHand.get(2);
            combatWeaponCard.setImage(playerTreacheryCard3.getImage());
          }
          else if(currentPlayer.treacheryCardHand.get(2).subtype == cardSubTypes.DEFENSE) {
            battlePlans.get(currentPlayer).defense = currentPlayer.treacheryCardHand.get(2);
            combatWeaponCard.setImage(playerTreacheryCard3.getImage());
          }
        }
      }
    });
    playerTreacheryCard4.setOnMouseClicked(event -> {
      if (playerTreacheryCard4.getImage() != null) {
        if (currentRound == STORM && currentPlayer.treacheryCardHand.get(3).equals(weatherControl)) {
          playerNumbersInputted.set(1);
          stormMovementSelector.setMax(10);

          currentStormSector = (short) Math.abs((short) stormMarker.getRotate() / rotationDegrees) + 1;
          stormRoundPane.setVisible(true);
          numbersFromPlayers.set(0);
          chooseStormNumber();
          playerTreacheryCard4.setImage(null);
          treacheryCardDiscard.add(currentPlayer.treacheryCardHand.get(3));
          currentPlayer.treacheryCardHand.remove(currentPlayer.treacheryCardHand.get(3));
          return;
        }

        if (currentRound == REVIVAL_AND_MOVEMENT &&
            currentPlayer.treacheryCardHand.get(3).equals(tleilaxuGhola) &&
            currentPlayer.troopsInTanksDataModel.get() > 0) {
          for (int i = 0; i < currentPlayer.troopsInTanksDataModel.get() && i < 5; i++) {
            currentPlayer.reviveTroops(1);
          }
          playerTreacheryCard4.setImage(null);
          treacheryCardDiscard.add(currentPlayer.treacheryCardHand.get(3));
          currentPlayer.treacheryCardHand.remove(currentPlayer.treacheryCardHand.get(3));
          return;
        }

        if (currentRound == REVIVAL_AND_MOVEMENT &&
            currentPlayer.treacheryCardHand.get(3).equals(hajr)) {
          extraMovement = true;
          playerTreacheryCard4.setImage(null);
          treacheryCardDiscard.add(currentPlayer.treacheryCardHand.get(3));
          currentPlayer.treacheryCardHand.remove(currentPlayer.treacheryCardHand.get(3));
          return;
        }

        if (currentPlayer.treacheryCardHand.get(3).equals(familyAtomics)) {
          if (currentPlayer.troopsOnPlanet.containsKey(regions.get("Shield Wall"))) {
            shieldWallDestroyed = true;
            playerTreacheryCard4.setImage(null);
            treacheryCardDiscard.add(currentPlayer.treacheryCardHand.get(3));
            currentPlayer.treacheryCardHand.remove(currentPlayer.treacheryCardHand.get(3));
            return;
          }
          else {
            for (Region region : regions.get("Shield Wall").getConnections()) {
              if (currentPlayer.troopsOnPlanet.containsKey(region)) {
                shieldWallDestroyed = true;
                playerTreacheryCard4.setImage(null);
                treacheryCardDiscard.add(currentPlayer.treacheryCardHand.get(3));
                currentPlayer.treacheryCardHand.remove(currentPlayer.treacheryCardHand.get(3));
                return;
              }
            }
          }
        }

        if (currentRound.equals(BATTLE) &&
            (currentPlayer.treacheryCardHand.get(3).name.equalsIgnoreCase("Cheap Hero") ||
                currentPlayer.treacheryCardHand.get(3).name.equalsIgnoreCase("Cheap Heroine"))) {
          combatLeaderImageView.setImage(currentPlayer.treacheryCardHand.get(3).cardImage);
          battlePlans.get(currentCombatant).setLeader(new Leader("Cheap Hero/ine", currentCombatant.faction, 0,  combatLeaderImageView.getImage()));
        }

        if (currentRound.equals(BATTLE) && battlePlans.get(currentPlayer).leader != null) {
          if (currentPlayer.treacheryCardHand.get(3).subtype == cardSubTypes.WEAPON) {
            battlePlans.get(currentPlayer).weapon = currentPlayer.treacheryCardHand.get(3);
            combatWeaponCard.setImage(playerTreacheryCard4.getImage());
          }
          else if(currentPlayer.treacheryCardHand.get(3).subtype == cardSubTypes.DEFENSE) {
            battlePlans.get(currentPlayer).defense = currentPlayer.treacheryCardHand.get(3);
            combatWeaponCard.setImage(playerTreacheryCard4.getImage());
          }
        }
      }
    });

    leader1.setOnMouseClicked(event -> {
      if (leader1.getImage() != null && currentRound == BATTLE && leader1.getOpacity() == 1) {
        for (Leader leader : currentCombatant.leaders) {
          if (leader.image.equals(leader1.getImage())) {
            battlePlans.get(currentCombatant).leader = leader;
            combatLeaderImageView.setImage(leader.getImage());
          }
        }
      }
    });
    leader2.setOnMouseClicked(event -> {
      if (leader2.getImage() != null && currentRound == BATTLE && leader2.getOpacity() == 1) {
        for (Leader leader : currentCombatant.leaders) {
          if (leader.image.equals(leader2.getImage())) {
            battlePlans.get(currentCombatant).leader = leader;
            combatLeaderImageView.setImage(leader.getImage());
          }
        }
      }
    });
    leader3.setOnMouseClicked(event -> {
      if (leader3.getImage() != null && currentRound == BATTLE && leader3.getOpacity() == 1) {
        for (Leader leader : currentCombatant.leaders) {
          if (leader.image.equals(leader3.getImage())) {
            battlePlans.get(currentCombatant).leader = leader;
            combatLeaderImageView.setImage(leader.getImage());
          }
        }
      }
    });
    leader4.setOnMouseClicked(event -> {
      if (leader4.getImage() != null && currentRound == BATTLE && leader4.getOpacity() == 1) {
        for (Leader leader : currentCombatant.leaders) {
          if (leader.image.equals(leader4.getImage())) {
            battlePlans.get(currentCombatant).leader = leader;
            combatLeaderImageView.setImage(leader.getImage());
          }
        }
      }
    });
    leader5.setOnMouseClicked(event -> {
      if (leader5.getImage() != null && currentRound == BATTLE && leader5.getOpacity() == 1) {
        for (Leader leader : currentCombatant.leaders) {
          if (leader.image.equals(leader5.getImage())) {
            battlePlans.get(currentCombatant).leader = leader;
            combatLeaderImageView.setImage(leader.getImage());
          }
        }
      }
    });

    players.sort(sortPlayers);
    opponentStatsTableView.setOnMouseClicked(event -> { });

    falseWallSouthSector4Spinner.setValueFactory(new IntegerSpinnerValueFactory(0, 10, 0, 1));
    falseWallSouthSector5Spinner.setValueFactory(new IntegerSpinnerValueFactory(0, 10, 0, 1));
    falseWallWestSector16Spinner.setValueFactory(new IntegerSpinnerValueFactory(0, 10, 0, 1));
    falseWallWestSector17Spinner.setValueFactory(new IntegerSpinnerValueFactory(0, 10, 0, 1));
    falseWallWestSector18Spinner.setValueFactory(new IntegerSpinnerValueFactory(0, 10, 0, 1));
    sietchTabrSpinner.setValueFactory(new IntegerSpinnerValueFactory(0, 10, 0, 1));

    Scanner scan = new Scanner(new File("DuneMapPaths.svg"));
    scan.useDelimiter(">");

    while (scan.hasNext()) {
      String path = scan.next();

      path = path.replaceAll("\n", "");

      if (path.contains("<path") == false) {
        continue;
      }

      String key = path.substring(path.indexOf(" id=\"") + 5, path.indexOf("\"", path.indexOf("id=\"") + 4));
      int sector = Integer.parseInt(key.substring(key.lastIndexOf(' ')  + 1));
      String value = path.substring(path.indexOf(" d=") + 4, path.lastIndexOf(' ') - 1);

      regions.put(key.substring(0, key.lastIndexOf(' ')), null);


      SVGPath svg = new SVGPath();
      svg.setId(key);
      svg.setContent(value);
      svg.setFill(Color.TRANSPARENT);

      svg.setOnMouseClicked(generalRegionClickHandler);

      sectorPaths.get(sector).add(svg);
      map.getChildren().add(svg);
    }

    setRegions();

    for (Region region : regions.values()) {
      regionPaths.put(region, new ArrayList<>());
    }
    for (ArrayList<SVGPath> pathList : sectorPaths) {
      for (SVGPath path : pathList) {
        regionPaths.get(regions.get(path.getId().substring(0, path.getId().lastIndexOf(" "))))
        .add(path);
      }
    }

    for (Region oneStepFromGreatFlat : regions.get("The Great Flat").getConnections()) {
      for (Region twoStepsFromGreatFlat: oneStepFromGreatFlat.getConnections()) {
        fremenRegionList.add(twoStepsFromGreatFlat);
      }
      fremenRegionList.add(oneStepFromGreatFlat);
    }

    regionTroopMarkerAnchorPane.getChildren().forEach(node -> {
      String region = node.getId().substring(0, node.getId().lastIndexOf(" "));
      int sector = Integer.parseInt(node.getId().substring(node.getId().lastIndexOf(" ") + 1));
      regionTroopMarkers.putIfAbsent(regions.get(region), new HashMap<>());
      regionTroopMarkers.get(regions.get(region)).putIfAbsent(sector, new ArrayList<>());
      regionTroopMarkers.get(regions.get(region)).get(sector).add((ImageView) node);
    });

  }

  private void changeSelectedArea(String key) {

    if (key == null) {
      areaTroopsText.setText(" - ");
      areaNameText.setText("");
      areaTypeText.setText("");

      currentlySelectedRegion = null;
      currentlySelectedSector = -1;

      return;
    }

    currentlySelectedRegion = regions.get(key.substring(0, key.lastIndexOf(' ')));
    currentlySelectedSector = Short.parseShort(key.substring(key.lastIndexOf(' ') + 1));

    areaTroopsText.setText(" - ");
    areaNameText.setText(regions.get(key.substring(0, key.lastIndexOf(' '))).getName());
    areaTypeText.setText(regions.get(key.substring(0, key.lastIndexOf(' '))).getLandType().toString());
    ObservableList<Player> tempPlayers = FXCollections.observableArrayList(players);
    tempPlayers.sort(new Comparator<Player>() {
      @Override
      public int compare(Player o1, Player o2) {
        return o1.factionDataModel.get().compareTo(o2.factionDataModel.get());
      }
    });
    for (Player player : tempPlayers) {
      if (player.troopsOnPlanet.get(currentlySelectedRegion).size() > 0) {
        areaTroopsText.setText(
            areaTroopsText.getText()
                .concat(player.factionDataModel.get()).concat(": ")
                .concat(String.valueOf(player.troopsOnPlanet.get(currentlySelectedRegion).size()))
                .concat(" - "));
      }
    }
  }

  private void initializeChoiceBoxes() {
    Comparator factionSort = Comparator.comparing(Object::toString);

    for (factions faction : factions.values()) {
      factionChoice1.getItems().add(faction.toString());
      factionChoice2.getItems().add(faction.toString());
    }
    for (playerTypes playerType : playerTypes.values()) {
      playerType1.getItems().add(playerType.toString());
      playerType2.getItems().add(playerType.toString());
    }


    factionChoice1.getItems().sort(factionSort);
    factionChoice1.getSelectionModel().selectedItemProperty().addListener ((observable, oldValue, newValue) -> {
      if (oldValue != null) {
        factionChoice2.getItems().add(oldValue);
      }
      if (newValue != null) {
        factionChoice2.getItems().remove(newValue);
      }

      factionChoice2.getItems().sort(factionSort);
      factionChoice2.getItems().sort(factionSort);

    });
    factionChoice2.getSelectionModel().selectedItemProperty().addListener ((observable, oldValue, newValue) -> {
      if (newValue != null) {
        factionChoice1.getItems().remove(newValue);
      }
      if (oldValue != null) {
        factionChoice1.getItems().add(oldValue);
      }
      factionChoice1.getItems().sort(factionSort);
    });
  }


  public void loadTreacheryDeck() {
    try {
      if (conn != null && !conn.isClosed()) {
        stmt = conn.createStatement();

        results = stmt.executeQuery("SELECT cards.name, number, cardtype, type AS subtype, subtype AS classification, imagefile "
            + "FROM cards "
            + "JOIN treachery_cards ON (cards.name = treachery_cards.name) "
            + "WHERE lower(cardtype) in ('treachery', 'junk');");



        while (results.next()) {
          for (int i = 1; i <= results.getInt("number"); i++) {
            treacheryCardDiscard
                .add(new TreacheryCard(results.getString("name"),
                    results.getString("cardtype"),
                    results.getString("subtype"),
                    results.getString("classification"),
                    "images/cards/".concat(results.getString("imagefile"))));
          }
        }

        weatherControl = treacheryCardDiscard.filtered(treacheryCard -> treacheryCard.name.equalsIgnoreCase("Weather Control")).get(0);
        familyAtomics = treacheryCardDiscard.filtered(treacheryCard -> treacheryCard.name.equalsIgnoreCase("Family Atomics")).get(0);
        tleilaxuGhola = treacheryCardDiscard.filtered(treacheryCard -> treacheryCard.name.equalsIgnoreCase("Tleilaxu Ghola")).get(0);
        hajr = treacheryCardDiscard.filtered(treacheryCard -> treacheryCard.name.equalsIgnoreCase("Hajr")).get(0);
        lasegun = treacheryCardDiscard.filtered(treacheryCard -> treacheryCard.name.equalsIgnoreCase("Lasegun")).get(0);

        shuffleDeck(cardTypes.TREACHERY);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public void loadSpiceDeck() {
    try {
      if (conn != null && !conn.isClosed()) {
        stmt = conn.createStatement();

        results = stmt.executeQuery("SELECT * FROM cards WHERE lower(cardtype) in ('spice');");



        while (results.next()) {
          spiceCardDiscard.add(new SpiceCard(results.getString("name"), "images/cards/".concat(results.getString("imagefile"))));
        }

        shuffleDeck(cardTypes.SPICE);


        wormCard = spiceCardDeck.filtered(spiceCard -> spiceCard.name.equalsIgnoreCase("Shai-Hulud")).get(0);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  private void shuffleDeck(cardTypes deck) {
    if (deck == cardTypes.SPICE) {
      int spiceDeckSize = spiceCardDiscard.size();
      for (int i = 0; i < spiceDeckSize; i++) {
        SpiceCard card = spiceCardDiscard.get(new Random().nextInt(spiceCardDiscard.size()));
        spiceCardDeck.add(card);
        spiceCardDiscard.remove(card);
      }
    }
    else if (deck == cardTypes.TREACHERY) {
      int treacheryDeckSize = treacheryCardDiscard.size();
      for (int i = 0; i < treacheryDeckSize; i++) {
        TreacheryCard card = treacheryCardDiscard.get(new Random().nextInt(treacheryCardDiscard.size()));
        treacheryCardDeck.add(card);
        treacheryCardDiscard.remove(card);
      }
    }
  }

  public void disconnectFromDatabase() {
    try {
      conn.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public void placeSetupMarkers() {
    for (Player player : players) {
      player.troopsOnPlanet.forEach((region, troops) -> {
        if (troops.size() > 0) {
          for (Troop troop : troops) {
            if (regionTroopMarkers.get(region).get(troop.sector).get(0).getImage() == null) {
              regionTroopMarkers.get(region).get(troop.sector).get(0)
                  .setImage(troop.getUnitImage());
              break;
            }
          }
        }
      });
    }
  }

  //Sets up the regions
  private void setRegions() {
    try {
      if (conn != null && !conn.isClosed()) {
        stmt = conn.createStatement();

        results = stmt
            .executeQuery("SELECT name, landType, spiceSector, spiceAmount, sectors, connections FROM regions;");
        HashMap<String, String[]> connections = new HashMap<>();
        while (results.next()) {

          if (results.getString("connections") != null) {
            connections.put(results.getString("name"), results.getString("connections").split(","));
          }



          regions.put(results.getString("name"),
              new Region(results.getString("name"), results.getString("landType"),
                  results.getShort("spiceSector"), results.getShort("spiceAmount"), results.getString("sectors")));

          if (results.getShort("spiceAmount") != 0) {
            spiceRegions.add(regions.get(results.getString("name")));
          }
        }


        for (Region region : regions.values()) {
          if (connections.containsKey(region.getName())) {
            HashSet<Region> regionConnections = new HashSet<>();

            for (String name : connections.get(region.getName())) {
              regionConnections.add(regions.get(name));
            }

            region.setConnections(regionConnections);
          }
        }

        regions.get(spiceBoxCielagoNorth.getId()).setSpiceBox(spiceBoxCielagoNorth);
        regions.get(spiceBoxCielagoSouth.getId()).setSpiceBox(spiceBoxCielagoSouth);
        regions.get(spiceBoxHabbanyaRidgeFlat.getId()).setSpiceBox(spiceBoxHabbanyaRidgeFlat);
        regions.get(spiceBoxHabbanyaErg.getId()).setSpiceBox(spiceBoxHabbanyaErg);
        regions.get(spiceBoxTheGreatFlat.getId()).setSpiceBox(spiceBoxTheGreatFlat);
        regions.get(spiceBoxFuneralPlain.getId()).setSpiceBox(spiceBoxFuneralPlain);
        regions.get(spiceBoxRockOutcroppings.getId()).setSpiceBox(spiceBoxRockOutcroppings);
        regions.get(spiceBoxHaggaBasin.getId()).setSpiceBox(spiceBoxHaggaBasin);
        regions.get(spiceBoxBrokenLand.getId()).setSpiceBox(spiceBoxBrokenLand);
        regions.get(spiceBoxOhGap.getId()).setSpiceBox(spiceBoxOhGap);
        regions.get(spiceBoxSihayaRidge.getId()).setSpiceBox(spiceBoxSihayaRidge);
        regions.get(spiceBoxRedChasm.getId()).setSpiceBox(spiceBoxRedChasm);
        regions.get(spiceBoxWindPassNorth.getId()).setSpiceBox(spiceBoxWindPassNorth);
        regions.get(spiceBoxSouthMesa.getId()).setSpiceBox(spiceBoxSouthMesa);
        regions.get(spiceBoxTheMinorErg.getId()).setSpiceBox(spiceBoxTheMinorErg);

        //Adds dots to sector translation
        dotToSector.put(1, 2);
        dotToSector.put(2, 5);
        dotToSector.put(3, 8);
        dotToSector.put(4, 11);
        dotToSector.put(5, 14);
        dotToSector.put(6, 17);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public void play() {
    if (debugOn == true) {
      (mainPane.getScene()).setOnKeyPressed(event -> {
        if (event.getCode() == KeyCode.F1) {
          if (debugPane.visibleProperty().get() == false) {
            debugPane.visibleProperty().set(true);
          }
          else {
            debugPane.visibleProperty().set(false);
          }
        }
      });

      debugPane.visibleProperty().addListener((observable, oldValue, newValue) -> {
        if (newValue == true) {
          StringBuilder spiceDeckText = new StringBuilder();
          spiceDeckText.append("Spice Cards in Deck:\n");
          if (spiceCardDeck.size() > 0) {
            for (int i = 0; i < spiceCardDeck.size(); i++) {
              spiceDeckText
                  .append("Name: " + spiceCardDeck.get(i).name + "Position: " + (i + 1) + "\n");
            }
          }
          else {
            spiceDeckText.append("No cards in the Spice Deck\n");
          }

          spiceDeckText.append("\n\nSpice Cards in Discard:\n");
          if (spiceCardDiscard.size() > 0) {
            for (int i = 0; i < spiceCardDeck.size(); i++) {
              spiceDeckText
                  .append("Name: " + spiceCardDeck.get(i).name + "Position: " + (i + 1) + "\n");
            }
          }
          else {
            spiceDeckText.append("No cards in the Spice Discard Pile");
          }

          spiceDeckReportArea.setText(spiceDeckText.toString());

          StringBuilder treacheryCardsText = new StringBuilder();
          treacheryCardsText.append("Treachery Cards in Deck:\n");
          if (treacheryCardDeck.size() > 0) {
            for (int i = 0; i < treacheryCardDeck.size(); i++) {
              treacheryCardsText
                  .append("Name: " + treacheryCardDeck.get(i).name + "Position: " + (i + 1) + "\n");
            }
          }
          else {
            treacheryCardsText.append("No cards in the Treachery Deck\n");
          }

          treacheryCardsText.append("\n\nTreachery Cards in Discard:\n");
          if (treacheryCardDiscard.size() > 0) {
            for (int i = 0; i < treacheryCardDiscard.size(); i++) {
              treacheryCardsText
                  .append("Name: " + treacheryCardDiscard.get(i).name + "Position: " + (i + 1) + "\n");
            }
          }
          else {
            treacheryCardsText.append("No cards in the Treachery Discard Pile\n");
          }
          for (Player player : players) {
            if (player.treacheryCardHand.size() > 0) {
              treacheryCardsText.append(player.faction.toString() + "'s Hand:\n");
              for (TreacheryCard card : player.treacheryCardHand) {
                treacheryCardsText.append(card.name + "\n");
              }
              treacheryCardsText.append("\n");
            }
          }

          treacheryDeckReportArea.setText(treacheryCardsText.toString());

          if (beneGesseritPlaying == false) {
            beneGesseritPlayerPredictionDebugArea.setDisable(true);
            beneGesseritRoundPredictionDebugArea.setDisable(true);
            beneGesseritWinButtonDebugArea.setDisable(true);
            beneGesseritPlayerLabelDebugArea.setVisible(true);
          }
          else {
            beneGesseritPlayerPredictionDebugArea.setDisable(false);
            beneGesseritRoundPredictionDebugArea.setDisable(false);
            beneGesseritPlayerLabelDebugArea.setVisible(false);
            beneGesseritWinButtonDebugArea.setDisable(false);
            beneGesseritRoundPredictionDebugArea.setText(String.valueOf(beneGesseritRoundWinPrediction));
            ArrayList<String> factions = new ArrayList<>();
            players.forEach(player -> factions.add(player.faction.toString()));
            beneGesseritPlayerPredictionDebugArea.setItems(FXCollections.observableArrayList(factions));
            for (Player player : players) {
              if (player == beneGesseritPlayerWinPrediction) {
                beneGesseritPlayerPredictionDebugArea.getSelectionModel().select(player.faction.toString());
              }
            }

            beneGesseritWinButtonDebugArea.setOnAction(event -> {
              round = Integer.parseInt(beneGesseritRoundPredictionDebugArea.getText());
              beneGesseritRoundWinPrediction = round;

              for (Player player : players) {
                if (player.faction.toString().equals(
                    beneGesseritPlayerPredictionDebugArea.getSelectionModel().getSelectedItem())) {
                  beneGesseritPlayerWinPrediction = player;
                }

                  if (player.troopsInReserve.size() < 4) {
                    for (Region region : regions.values()) {
                      if (region.getLandType() != landTypes.STRONGHOLD &&
                          player.troopsOnPlanet.get(region).size() > 0) {
                        player.killTroops(region, 1);
                      }
                      if (player.troopsInReserve.size() >= 4) {
                        break;
                      }
                    }
                  }

                  players.get(0).landTroops(regions.get("Arrakeen"), 10, 1);
                  players.get(0).landTroops(regions.get("Carthag"), 11, 1);
                  players.get(0).landTroops(regions.get("Habbanya Ridge Sietch"), 17, 1);
                  players.get(0).landTroops(regions.get("Sietch Tabr"), 14, 1);
                }


              checkForWinner();
            });
          }

          if (fremenPlaying == false) {
            fremenPlayingLabelDebugArea.setVisible(true);
            fremenWinButtonDebugArea.setDisable(true);
          }
          else {
            fremenPlayingLabelDebugArea.setVisible(false);
            fremenWinButtonDebugArea.setDisable(false);
            fremenWinButtonDebugArea.setOnAction(event -> {
              for (Player player : players) {
                if (player.faction == factions.HARKONNEN ||
                    player.faction == factions.ATREIDES ||
                    player.faction == factions.SPACE_GUILD &&
                  player.troopsOnPlanet.get(regions.get("Tuek's Sietch")).size() > 0) {
                  player.killTroops(regions.get("Tuek's Sietch"), player.troopsOnPlanet.get(regions.get("Tuek's Sietch")).size());
                }
                if (player.faction == factions.FREMEN) {
                  if (player.troopsInReserveDataModel.get() < 2) {
                    for (Entry<Region, ArrayList<Troop>> regionList : player.troopsOnPlanet
                        .entrySet()) {
                      if (regionList.getValue().size() > 0 &&
                          (!regionList.getKey().getName().equalsIgnoreCase("Sietch Tabr") ||
                              !regionList.getKey().getName()
                                  .equalsIgnoreCase("Habbanya Ridge Sietch"))) {
                        player.killTroops(regionList.getKey(), 1);
                      }
                      if (player.troopsInReserve.size() >= 2) {
                        break;
                      }
                    }
                    player.reviveTroops(2);
                  }

                  player.landTroops(regions.get("Sietch Tabr"), 14, 1);
                  player.landTroops(regions.get("Habbanya Ridge Sietch"), 17, 1);
                }
              }

              round = 15;

              beneGesseritRoundWinPrediction = 14;

              checkForWinner();
            });
          }

          StringBuilder playerText = new StringBuilder();
          Player player = players.get(0);
          playerText.append(player.faction.toString() + ":\n" +
              "Troop in reserve: " + player.troopsInReserve.size() + "\n" +
              "Troops in Tanks: " + player.troopsInTank.size() + "\n" +
              "Troops on Planet: \n");
          for (Entry<Region, ArrayList<Troop>> playerRegion : player.troopsOnPlanet.entrySet()) {
            playerText.append("\t" + playerRegion.getKey().getName() + "- " + playerRegion.getValue().size() + "\n");
          }
          playerText.append("Treachery Cards: \n");
          for (TreacheryCard card : player.treacheryCardHand) {
            playerText.append("\t" + card.name);
          }
          playerText.append("\nLeader Status:");
          for (Leader leader : player.leaders) {
            playerText.append("\t" + leader.getName() + " - " + (player.leadersInTank.get(leader.image) == true ? "In Tank" : "Alive1"));
          }

          player1DebugArea.setText(playerText.toString());

          playerText = new StringBuilder();
          player = players.get(1);
          playerText.append(player.faction.toString() + ":\n" +
              "Troop in reserve: " + player.troopsInReserve.size() + "\n" +
              "Troops in Tanks: " + player.troopsInTank.size() + "\n" +
              "Troops on Planet: \n");
          for (Entry<Region, ArrayList<Troop>> playerRegion : player.troopsOnPlanet.entrySet()) {
            playerText.append("\t" + playerRegion.getKey().getName() + "- " + playerRegion.getValue().size() + "\n");
          }
          playerText.append("Treachery Cards: \n");
          for (TreacheryCard card : player.treacheryCardHand) {
            playerText.append("\t" + card.name);
          }
          playerText.append("\nLeader Status:");
          for (Leader leader : player.leaders) {
            playerText.append("\t" + leader.getName() + " - " + (player.leadersInTank.get(leader.image) == true ? "In Tank" : "Alive1"));
          }
          player2DebugArea.setText(playerText.toString());

          player1WinDebugButton.setOnAction(event -> {
            if (players.get(0).troopsInReserve.size() < 4) {
              for (Region region : regions.values()) {
                if (region.getLandType() != landTypes.STRONGHOLD &&
                  players.get(0).troopsOnPlanet.get(region).size() > 0) {
                  players.get(0).killTroops(region, 1);
                }
                if (players.get(0).troopsInReserve.size() >= 4) {
                  break;
                }
              }
            }
            players.get(0).landTroops(regions.get("Arrakeen"), 10, 1);
            players.get(0).landTroops(regions.get("Carthag"), 11, 1);
            players.get(0).landTroops(regions.get("Habbanya Ridge Sietch"), 17, 1);
            players.get(0).landTroops(regions.get("Sietch Tabr"), 14, 1);

            checkForWinner();
          });
          player2WinDebugButton.setOnAction(event -> {
            if (players.get(1).troopsInReserve.size() < 4) {
              for (Region region : regions.values()) {
                if (region.getLandType() != landTypes.STRONGHOLD &&
                    players.get(1).troopsOnPlanet.get(region).size() > 0) {
                  players.get(1).killTroops(region, 1);
                }
                if (players.get(1).troopsInReserve.size() >= 4) {
                  break;
                }
              }
            }
            players.get(1).landTroops(regions.get("Arrakeen"), 10, 1);
            players.get(1).landTroops(regions.get("Carthag"), 11, 1);
            players.get(1).landTroops(regions.get("Habbanya Ridge Sietch"), 17, 1);
            players.get(1).landTroops(regions.get("Sietch Tabr"), 14, 1);

            checkForWinner();
          });
        }
      });




    }

    if (dots.size() < 6) {
      dots = new ArrayList<>(Arrays.asList(1,2,3,4,5,6));
    }

    musicPlayer.play(musicFiles.WIN);
    currentRound = STORM;
    players.sort(sortPlayers);

    Player fremenPlayer = null;
    for (Player player : players) {
      if (player.faction == factions.FREMEN) {
        fremenPlaying = true;
        fremenPlayer = player;
        break;
      }

      if (player.faction == factions.BENE_GESSERIT) {
        beneGesseritPlaying = true;
        break;
      }
    }
    currentPlayer = players.get(0);
    opponentStatsTableView.getSelectionModel().select(currentPlayer);

    characterImage.setImage(currentPlayer.characterImage);
    leader1.setImage(currentPlayer.leaders.get(0).getImage());
    leader2.setImage(currentPlayer.leaders.get(1).getImage());
    leader3.setImage(currentPlayer.leaders.get(2).getImage());
    leader4.setImage(currentPlayer.leaders.get(3).getImage());
    leader5.setImage(currentPlayer.leaders.get(4).getImage());

    if (fremenPlaying && fremenPlayer.troopsInReserve.size() == 20) {
      runFremenPlacement();
    }
    else if (beneGesseritPlaying && beneGesseritPlayerWinPrediction == null) {
      runBeneGesseritPrediciton();
    }
    else {
      placeSetupMarkers();
      runStormRound();
    }
  }

  private void runBeneGesseritPrediciton() {
    Player beneGesseritPlayer = null;
    for (Player player : players) {
      if (player.faction == factions.BENE_GESSERIT) {
        beneGesseritPlayer = player;
        break;
      }
    }
    if (beneGesseritPlayer.getPlayerType() == playerTypes.COMPUTER) {
     beneGesseritRoundWinPrediction = (new Random().nextInt(15)) + 1;
     beneGesseritPlayerWinPrediction = players.get(new Random().nextInt(2));
      play();
    }
    else {
      for (Player player : players) {
        if (player.faction == factions.BENE_GESSERIT) {
          currentPlayer = player;
          break;
        }
      }
      beneGesseritPrediction.setVisible(true);
      ArrayList<String> playerList = new ArrayList<>();
      for (Player player : players) {
        playerList.add(player.getFactionDataModel());
      }
      playerList.sort(new Comparator<String>() {
        @Override
        public int compare(String o1, String o2) {
          return o1.compareTo(o2);
        }
      });

      beneGesseritPlayerWinChoiceBox.getItems().addAll(playerList);
      beneGesseritPlayerWinChoiceBox.getSelectionModel().select(0);
      beneGesseritRoundWinSpinner.setValueFactory(new IntegerSpinnerValueFactory(5, 15, 5, 1));
      beneGesseritPredictionConfirmButton.setOnAction(event -> {
        for (Player player : players) {
          if (beneGesseritPlayerWinChoiceBox.getSelectionModel().getSelectedItem()
              .equals(player.getFactionDataModel())) {
            beneGesseritPlayerWinPrediction = player;
            break;
          }
        }
        beneGesseritRoundWinPrediction = Integer
            .parseInt(beneGesseritRoundWinSpinner.getValue().toString());
        beneGesseritPrediction.setVisible(false);
        play();
      });
    }
  }

  private void runFremenPlacement() {
    for (Player player : players) {
      if (player.faction == factions.FREMEN) {
        currentPlayer = player;
      }
    }
    if (currentPlayer.getPlayerType() == playerTypes.COMPUTER) {
      Troop troop;
      Region region;
      for (int i = 1; i <= 2; i++) {
        troop = currentPlayer.troopsInReserve.get(0);
        region = regions.get("Sietch Tabr");
        troop.setRegion(region);
        troop.setSector(14);
        currentPlayer.troopsOnPlanet.get(region).add(troop);
        currentPlayer.troopsInReserve.remove(troop);
        currentPlayer.troopsInReserveDataModel.subtract(1);
      }

        troop = currentPlayer.troopsInReserve.get(0);
        region = regions.get("False Wall South");
        troop.setRegion(region);
        troop.setSector(4);
        currentPlayer.troopsOnPlanet.get(region).add(troop);
        currentPlayer.troopsInReserve.remove(troop);
        currentPlayer.troopsInReserveDataModel.subtract(1);

        troop = currentPlayer.troopsInReserve.get(0);
        region = regions.get("False Wall South");
        troop.setRegion(region);
        troop.setSector(5);
        currentPlayer.troopsOnPlanet.get(region).add(troop);
        currentPlayer.troopsInReserve.remove(troop);
        currentPlayer.troopsInReserveDataModel.subtract(1);

      for (int i = 1; i <= 2; i++) {
        troop = currentPlayer.troopsInReserve.get(0);
        region = regions.get("False Wall West");
        troop.setRegion(region);
        troop.setSector(16);
        currentPlayer.troopsOnPlanet.get(region).add(troop);
        currentPlayer.troopsInReserve.remove(troop);
        currentPlayer.troopsInReserveDataModel.subtract(1);
      }

      for (int i = 1; i <= 2; i++) {
        troop = currentPlayer.troopsInReserve.get(0);
        region = regions.get("False Wall West");
        troop.setRegion(region);
        troop.setSector(17);
        currentPlayer.troopsOnPlanet.get(region).add(troop);
        currentPlayer.troopsInReserve.remove(troop);
        currentPlayer.troopsInReserveDataModel.subtract(1);
      }

      for (int i = 1; i <= 2; i++) {
        troop = currentPlayer.troopsInReserve.get(0);
        region = regions.get("False Wall West");
        troop.setRegion(region);
        troop.setSector(18);
        currentPlayer.troopsOnPlanet.get(region).add(troop);
        currentPlayer.troopsInReserve.remove(troop);
        currentPlayer.troopsInReserveDataModel.subtract(1);
      }

      play();
    }
    else {
      for (Player player : players) {
        if (player.faction == factions.FREMEN) {
          currentPlayer = player;
        }
      }

      fremenPlacementPane.setVisible(true);

      fremenPlacementConfirmButton.setOnAction(event -> {
        if (sietchTabrSpinner.getValue() +
            falseWallSouthSector4Spinner.getValue() +
            falseWallSouthSector5Spinner.getValue() +
            falseWallWestSector16Spinner.getValue() +
            falseWallWestSector17Spinner.getValue() +
            falseWallWestSector18Spinner.getValue()
            == 10) {
          Troop troop;
          for (int i = 1; i <= sietchTabrSpinner.getValue(); i++) {
            troop = currentPlayer.troopsInReserve.get(0);
            Region region = regions.get("Sietch Tabr");
            troop.setRegion(region);
            troop.setSector(14);
            currentPlayer.troopsOnPlanet.get(region).add(troop);
            currentPlayer.troopsInReserve.remove(troop);
            currentPlayer.troopsInReserveDataModel.subtract(1);
          }

          for (int i = 1; i <= falseWallSouthSector4Spinner.getValue(); i++) {
            troop = currentPlayer.troopsInReserve.get(0);
            Region region = regions.get("False Wall South");
            troop.setRegion(region);
            troop.setSector(4);
            currentPlayer.troopsOnPlanet.get(region).add(troop);
            currentPlayer.troopsInReserve.remove(troop);
            currentPlayer.troopsInReserveDataModel.subtract(1);
          }

          for (int i = 1; i <= falseWallSouthSector5Spinner.getValue(); i++) {
            troop = currentPlayer.troopsInReserve.get(0);
            Region region = regions.get("False Wall South");
            troop.setRegion(region);
            troop.setSector(5);
            currentPlayer.troopsOnPlanet.get(region).add(troop);
            currentPlayer.troopsInReserve.remove(troop);
            currentPlayer.troopsInReserveDataModel.subtract(1);
          }

          for (int i = 1; i <= falseWallWestSector16Spinner.getValue(); i++) {
            troop = currentPlayer.troopsInReserve.get(0);
            Region region = regions.get("False Wall West");
            troop.setRegion(region);
            troop.setSector(16);
            currentPlayer.troopsOnPlanet.get(region).add(troop);
            currentPlayer.troopsInReserve.remove(troop);
            currentPlayer.troopsInReserveDataModel.subtract(1);
          }

          for (int i = 1; i <= falseWallWestSector17Spinner.getValue(); i++) {
            troop = currentPlayer.troopsInReserve.get(0);
            Region region = regions.get("False Wall West");
            troop.setRegion(region);
            troop.setSector(17);
            currentPlayer.troopsOnPlanet.get(region).add(troop);
            currentPlayer.troopsInReserve.remove(troop);
            currentPlayer.troopsInReserveDataModel.subtract(1);
          }

          for (int i = 1; i <= falseWallWestSector18Spinner.getValue(); i++) {
            troop = currentPlayer.troopsInReserve.get(0);
            Region region = regions.get("False Wall West");
            troop.setRegion(region);
            troop.setSector(18);
            currentPlayer.troopsOnPlanet.get(region).add(troop);
            currentPlayer.troopsInReserve.remove(troop);
            currentPlayer.troopsInReserveDataModel.subtract(1);
          }

          fremenPlacementPane.setVisible(false);
          play();
        }
      });
    }
  }

  private void nextRound() {
    currentPlayer = players.get(0);
    if ((currentRound == SPICE_BLOW) && (spiceCardDiscard.get(spiceCardDiscard.size() - 1) == wormCard)) {
      stormRoundPane.setVisible(false);
      runNexusRound();
    }
    else {
      switch (currentRound) {
        case STORM:
          currentRound = SPICE_BLOW;
          break;
        case SPICE_BLOW:
          currentRound = BIDDING;
          stormRoundPane.setVisible(false);
          biddingRoundPane.setVisible(true);
          runBiddingRound();
          break;
        case BIDDING:
          currentRound = REVIVAL_AND_MOVEMENT;
          biddingRoundPane.setVisible(false);
          reviveConfirmPane.setVisible(true);
          runRevivalAndMovementRound();
          break;
        case REVIVAL_AND_MOVEMENT:
          currentRound = BATTLE;
          movementConfirmPane.setVisible(false);
          combatRoundPane.setVisible(true);
          runBattleRound();
          break;
        case BATTLE:
          currentRound = COLLECTION;
          combatRoundPane.setVisible(false);
          runCollectionRound();
          break;
        case COLLECTION:
          checkForWinner();
          if (gameOver == false) {
            stormRoundPane.setVisible(false);
            biddingRoundPane.setVisible(false);
            reviveConfirmPane.setVisible(false);
            landTroopsConfirmPane.setVisible(false);
            movementConfirmPane.setVisible(false);
            combatPlanPane.setVisible(false);
            confirmAttackPane.setVisible(false);
            combatRoundPane.setVisible(false);
            round++;
            currentRound = STORM;
            players.sort(sortPlayers);
            currentPlayer = players.get(0);
            characterImage.setImage(currentPlayer.characterImage);
            leader1.setImage(currentPlayer.leaders.get(0).getImage());
            leader2.setImage(currentPlayer.leaders.get(1).getImage());
            leader3.setImage(currentPlayer.leaders.get(2).getImage());
            leader4.setImage(currentPlayer.leaders.get(3).getImage());
            leader5.setImage(currentPlayer.leaders.get(4).getImage());
            runStormRound();
          }
          break;
      }
    }
  }

  private void checkForWinner() {
    if (debugOn) {
      debugPane.visibleProperty().set(false);
    }

    int strongholdsHeld = 0;
    Player winningPlayer = null;
    for (Player player : players) {
      for (Region region : player.troopsOnPlanet.keySet()) {
        if (region.getLandType() == landTypes.STRONGHOLD &&
            player.troopsOnPlanet.get(region).size() > 0) {
          strongholdsHeld++;
        }
      }

      if (strongholdsHeld >= 4) {
        winningPlayer = player;
        break;
      }
    }

    if (strongholdsHeld >= 4) {

      musicPlayer.transition(2, musicFiles.WIN);
      if (beneGesseritPlayerWinPrediction == winningPlayer && beneGesseritRoundWinPrediction == round) {
        //Bene Gesserit Wins
        for (Player player2 : players) {
          if (player2.faction == factions.BENE_GESSERIT) {
            winningPlayer = player2;
          }
        }
        winnerPane.setVisible(true);
        characterSelect.setVisible(true);
        winnerImage.setImage(winningPlayer.characterImage);
        winnerText.setText("Congratulations Bene Gesserit. \n"
            + "You have defeated your enemies and ensured your plan \n"
            + "to create the Kwaisatz Haderach on the Desert Planet, Dune.");
        gameOver = true;
      }
      else {
      //Display Winner
        winnerPane.setVisible(true);
        characterSelect.setVisible(true);
        winnerImage.setImage(winningPlayer.characterImage);
        winnerText.setText("Congratulations ".concat(winningPlayer.faction.toString().concat("\n"
            + "You have defeated your enemies and conquered the Desert Planet, Dune.")));
        gameOver = true;
      }
      return;
    }


    if (round == 15) {
      Player fremenPlayer = null;
      Player guildPlayer = null;
      boolean fremanControlSietchTabrAndHabbanyaRidge = false;
      boolean atreidesEmperorOrHGarkonnenControlsTueksSietch = false;
      for (Player player : players) {
        if (player.faction == factions.FREMEN) {
          fremenPlayer = player;
          if (player.troopsOnPlanet.get(regions.get("Sietch Tabr")).size() > 0 &&
              player.troopsOnPlanet.get(regions.get("Habbanya Ridge Sietch")).size() > 0) {
            fremanControlSietchTabrAndHabbanyaRidge = true;
          }
          else {
            break;
          }
        }

        if (player.faction == factions.SPACE_GUILD) {
          guildPlayer = player;
        }

        if ((player.faction == factions.ATREIDES ||
            player.faction == factions.EMPEROR ||
            player.faction == factions.HARKONNEN) &&
            player.troopsOnPlanet.get(regions.get("Tuek's Sietch")).size() > 0)
        {
          atreidesEmperorOrHGarkonnenControlsTueksSietch = true;
        }
      }

      if (fremenPlayer != null && fremanControlSietchTabrAndHabbanyaRidge && !atreidesEmperorOrHGarkonnenControlsTueksSietch) {
        winningPlayer = fremenPlayer;
        musicPlayer.transition(2, musicFiles.WIN);
        if (beneGesseritPlayerWinPrediction == fremenPlayer && beneGesseritRoundWinPrediction == round) {
          //Bene Gesserit Wins
          winnerPane.setVisible(true);
          characterSelect.setVisible(true);
          winnerImage.setImage(winningPlayer.characterImage);
          winnerText.setText("Congratulations Bene Gesserit. \n"
              + "You have defeated your enemies and ensured your plan \n"
              + "to create the Kwaisatz Haderach on the Desert Planet, Dune.");
          gameOver = true;
        }
        winnerPane.setVisible(true);
        characterSelect.setVisible(true);
        winnerImage.setImage(winningPlayer.characterImage);
        winnerText.setText("Congratulations Fremen \n"
            + "You have defeated your enemies and changed Dune to destroy the Spice.");
        gameOver = true;
      }
      else {
        if (guildPlayer != null &&  beneGesseritPlayerWinPrediction == guildPlayer && beneGesseritRoundWinPrediction == round) {
          //Bene Gesserit Wins
        }
        else {
          winnerPane.setVisible(true);
          characterSelect.setVisible(true);
          winnerImage.setImage(winningPlayer.characterImage);
          winnerText.setText("Congratulations Space Guild.\n"
              + "You have defeated your enemies and prevented them from \n"
              + "controlling the Desert Planet, Dune.");
          gameOver = true;
        }
      }

    }
    else {
      runStormRound();
    }
  }

  private void continueRound() {
    switch (currentRound) {
      case STORM:
        chooseStormNumber();
        break;
      case SPICE_BLOW:
        //runSpiceBlow();
        break;
      case BIDDING:
        runBiddingRound();
        break;
      case REVIVAL_AND_MOVEMENT:
        runRevivalAndMovementRound();
        break;
      case BATTLE:
        runBattleRound();
        break;
      case COLLECTION:
        runCollectionRound();
        break;
    }
  }


  private void nextPlayer() {
    if (currentRound == REVIVAL_AND_MOVEMENT && extraMovement == true) {
      extraMovement = false;
      moveTroops();
    }

    if (currentPlayer == players.get(players.size() - 1) ) {
      currentPlayer = players.get(0);

      playerTreacheryCard1.setVisible(false);
      playerTreacheryCard2.setVisible(false);
      playerTreacheryCard3.setVisible(false);
      playerTreacheryCard4.setVisible(false);

      ArrayList<ImageView> treacheryCardList = new ArrayList<>(Arrays.asList(playerTreacheryCard1, playerTreacheryCard2, playerTreacheryCard3, playerTreacheryCard4));
      for (int i = 0; i < currentPlayer.treacheryCardsDataModel.get(); i++)
      {
        treacheryCardList.get(i).setImage(currentPlayer.treacheryCardHand.get(i).cardImage);
        treacheryCardList.get(i).setVisible(true);
      };

      characterImage.setImage(currentPlayer.characterImage);
      leader1.setImage(currentPlayer.leaders.get(0).getImage());
      leader2.setImage(currentPlayer.leaders.get(1).getImage());
      leader3.setImage(currentPlayer.leaders.get(2).getImage());
      leader4.setImage(currentPlayer.leaders.get(3).getImage());
      leader5.setImage(currentPlayer.leaders.get(4).getImage());
      for (Entry<Image, Boolean> entry : currentPlayer.leadersInTank.entrySet()) {
        if (leader1.getImage().equals(entry.getKey()) && entry.getValue() == true) {
          leader1.setOpacity(.5);
        }
        else {
          leader1.setOpacity(1);
        }

        if (leader2.getImage().equals(entry.getKey()) && entry.getValue() == true) {
          leader2.setOpacity(.5);
        }
        else {
          leader2.setOpacity(1);
        }

        if (leader3.getImage().equals(entry.getKey()) && entry.getValue() == true) {
          leader3.setOpacity(.5);
        }
        else {
          leader3.setOpacity(1);
        }

        if (leader4.getImage().equals(entry.getKey()) && entry.getValue() == true) {
          leader4.setOpacity(.5);
        }
        else {
          leader4.setOpacity(1);
        }

        if (leader5.getImage().equals(entry.getKey()) && entry.getValue() == true) {
          leader5.setOpacity(.5);
        }
        else {
          leader5.setOpacity(1);
        }
      }

      nextRound();
    }

    else {
      currentPlayer = players.get(players.indexOf(currentPlayer) + 1);

      characterImage.setImage(currentPlayer.characterImage);
      leader1.setImage(currentPlayer.leaders.get(0).getImage());
      leader2.setImage(currentPlayer.leaders.get(1).getImage());
      leader3.setImage(currentPlayer.leaders.get(2).getImage());
      leader4.setImage(currentPlayer.leaders.get(3).getImage());
      leader5.setImage(currentPlayer.leaders.get(4).getImage());

      for (Entry<Image, Boolean> entry : currentPlayer.leadersInTank.entrySet()) {
        if (leader1.getImage().equals(entry.getKey()) && entry.getValue() == true) {
          leader1.setOpacity(.5);
        }
        else {
          leader1.setOpacity(1);
        }

        if (leader2.getImage().equals(entry.getKey()) && entry.getValue() == true) {
          leader2.setOpacity(.5);
        }
        else {
          leader2.setOpacity(1);
        }

        if (leader3.getImage().equals(entry.getKey()) && entry.getValue() == true) {
          leader3.setOpacity(.5);
        }
        else {
          leader3.setOpacity(1);
        }

        if (leader4.getImage().equals(entry.getKey()) && entry.getValue() == true) {
          leader4.setOpacity(.5);
        }
        else {
          leader4.setOpacity(1);
        }

        if (leader5.getImage().equals(entry.getKey()) && entry.getValue() == true) {
          leader5.setOpacity(.5);
        }
        else {
          leader5.setOpacity(1);
        }
      }


      playerTreacheryCard1.setVisible(false);
      playerTreacheryCard2.setVisible(false);
      playerTreacheryCard3.setVisible(false);
      playerTreacheryCard4.setVisible(false);

      ArrayList<ImageView> treacheryCardList = new ArrayList<>(Arrays.asList(playerTreacheryCard1, playerTreacheryCard2, playerTreacheryCard3, playerTreacheryCard4));
      for (int i = 0; i < currentPlayer.treacheryCardsDataModel.get(); i++)
      {
        treacheryCardList.get(i).setImage(currentPlayer.treacheryCardHand.get(i).cardImage);
        treacheryCardList.get(i).setVisible(true);
      };
      continueRound();
    }
  }



  /*
   * Runs the Storm round
   */
  private void runStormRound() {
    playerNumbersInputted.set(0);
    numbersFromPlayers.set(0);
    stormMovementSelector.setMax(round == 1 ? 20 : 3);

    currentStormSector = (short) Math.abs((short) stormMarker.getRotate() / rotationDegrees) + 1;
    stormRoundPane.setVisible(true);

    chooseStormNumber();


  }

  /**
   * Chooses the Storm Rounds choice number
   * @implNote To be run from storm round
   */
  public void chooseStormNumber() {
    musicPlayer.transition(2, currentPlayer.music);
    if (currentPlayer.playerType == playerTypes.COMPUTER) {
      numbersFromPlayers.set(numbersFromPlayers.add(new Random().nextInt((int) stormMovementSelector.getMax()) + 1).getValue());
      playerNumbersInputted.set(playerNumbersInputted.add(1).getValue());
      nextPlayer();
    }

    else {
      stormRoundLabel.setText("Storm Round for ".concat(currentPlayer.faction.toString()));

      confirmStormNumberSelection.setOnAction(new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
          numbersFromPlayers
              .set(numbersFromPlayers.add((int) stormMovementSelector.getValue()).getValue());
          playerNumbersInputted.set(playerNumbersInputted.add(1).getValue());
          nextPlayer();
        }
      });
    }


  }

  /**
   * Rotates the Storm Marker, which starts one sector to the left of bottom center
   *
   * @param direction Use -1 for counter clockwise and 1 for clockwise.  No other value functions.
   * @param numberOfSectors Use any number greater than 0
   *
   *
   */
  private void rotateStormMarker(int direction, int numberOfSectors) {
    if (numberOfSectors > 0 && (direction == rotateClockwise || direction == rotateCounterClockwise)) {

        RotateTransition transition = new RotateTransition();

        transition.setNode(stormMarker);
        transition.setAxis(new Point3D(0,0, direction));
        transition.setByAngle(rotationDegrees * numberOfSectors);
        transition.setDuration(Duration.millis(1000).multiply(numberOfSectors));
        transition.setInterpolator(Interpolator.LINEAR);
        sfxPlayer.play(sfxFiles.STORM);
        transition.play();

        transition.setOnFinished(event -> {
          sfxPlayer.stop();
          int smRotation = (int) stormMarker.getRotate();
          if (smRotation > 340 || smRotation < -340) {
          /*
          Set the sign needed for realigning to the negative rotation scheme.
          Sign must be positive if rotation is negative and negative if rotation is positive
          (Trust me, this is why math sucks sometimes)
           */

          int sign = smRotation < 0 ? 1 : -1;

          int numberOfRotations = Math.abs(smRotation / 360); //Total number of rotations to move
          stormMarker.setRotate(smRotation + (360 * numberOfRotations * sign));

          }

          int newSector = (short) Math.abs((short) stormMarker.getRotate() / rotationDegrees) + 1;
          if (round != 1) {
            do {
              if (currentStormSector < 17) {
                currentStormSector += 1;
              } else {
                currentStormSector = 1;
              }

              for (Region region : regions.values()) {
                if (region.getSpiceSector() == currentStormSector && region.getCurrentSpice() > 0) {
                  region.removeSpice(region.getSpiceAmount()); //Destroy all spice if region in current sector has any
                }
              }


              for (Region region : regions.values()) {
                if (region.getSectors().contains(currentStormSector)) { //check if region is in current sector
                  if (((shieldWallDestroyed == true &&
                      (region.getName().equalsIgnoreCase("Imperial Basin") ||
                          region.getName().equalsIgnoreCase("Carthag") ||
                          region.getName().equalsIgnoreCase("Arrakeen")))
                      //If Shield Wall is destroyed, check if region is one of 3 now vulnerable

                      ||

                      (region.getLandType() == landTypes.SAND &&
                          !region.getName().equalsIgnoreCase("Imperial Basin")))) {
                      //Otherwise, check if it is sand, but not Imperial Basin

                        for (Player player : players) {
                          player.killTroops(region, 20);
                          for (ImageView image : regionTroopMarkers.get(region).get(currentStormSector)) {
                            image.setImage(null);
                          }
                        }
                  }
                }
              }
            } while (currentStormSector != newSector);

          }

          runSpiceBlow();
        });
    }
  }



  /*
   * Runs Spice Blow Round
   */
  public void runSpiceBlow() {
    stormRoundPane.setVisible(false);

    SpiceCard card = spiceCardDeck.get(0);
    spiceCardDeck.remove(card);

    while (card.equals(wormCard) && round == 1) {
      spiceCardDeck.add(new Random().nextInt(spiceCardDeck.size() - 1) + 1, card);
      card = spiceCardDeck.get(0);
    }

    if (regions.containsKey(card.name)) {
      Region spiceRegion = regions.get(card.name);
      spiceRegion.addSpice(spiceRegion.getSpiceAmount());
      spiceCardDiscard.add(card);
    }

    nextRound();
  }



  /*
   * Runs Nexus Round if Spice Blow is Worm Card
   */
  private void runNexusRound() {
    //No Nexus Round due to only 2 players for Beta
  }



  /*
   * Runs Bidding Round
   */
  private void runBiddingRound() {
    cardsToDeal = 0;

    for (Player player : players) {
      if (player.totalSpice == 0) {
        player.addSpice(2);
      }

      bidders.add(player);

      if (player.treacheryCardHand.size() < 4) {
        cardsToDeal++;
      }
      else {
        passed.add(player);
      }
    }

    currentBid = 0;
    currentBidder = bidders.get(0);


    biddingRoundPane.setVisible(true);

    continueBiddingRound();
  }

  private void continueBiddingRound() {
    musicPlayer.transition(2, musicFiles.TREACHERY);
    playerTreacheryCard1.setVisible(false);
    playerTreacheryCard2.setVisible(false);
    playerTreacheryCard3.setVisible(false);
    playerTreacheryCard4.setVisible(false);
    ArrayList<ImageView> treacheryCardList = new ArrayList<>(Arrays.asList(playerTreacheryCard1, playerTreacheryCard2, playerTreacheryCard3, playerTreacheryCard4));
    for (int i = 0; i < currentBidder.treacheryCardsDataModel.get(); i++)
    {
      treacheryCardList.get(i).setImage(currentBidder.treacheryCardHand.get(i).cardImage);
      treacheryCardList.get(i).setVisible(true);
    }

    if (cardsToDeal > 0) {
      if (currentBiddingTreacheryCard == null) {
        currentBiddingTreacheryCard = treacheryCardDeck.get(0);
        treacheryCardDeck.remove(0);
      }

      switch (cardsToDeal) {
        case 6:
          biddingTreacheryCardBack5.setVisible(true);
        case 5:
          biddingTreacheryCardBack4.setVisible(true);
        case 4:
          biddingTreacheryCardBack3.setVisible(true);
        case 3:
          biddingTreacheryCardBack2.setVisible(true);
        case 2:
          biddingTreacheryCardBack1.setVisible(true);
        case 1:
          currentBiddingTreacheryCardBack.setVisible(true);
      }


      if (!passed.contains(currentBidder) && currentBidder.totalSpice > currentBid) {
        if (currentPlayer.getPlayerType() == playerTypes.COMPUTER) {
          if (new Random().nextDouble() > .5) {
            currentBid = currentBid + 1;
            highestBidder = currentBidder;

          }
          else {
            passed.add(currentBidder);
          }

          if (passed.size() == players.size()) {
            currentPlayer = players.get(0);

            playerTreacheryCard1.setVisible(false);
            playerTreacheryCard2.setVisible(false);
            playerTreacheryCard3.setVisible(false);
            playerTreacheryCard4.setVisible(false);

            for (int i = 0; i < currentPlayer.treacheryCardsDataModel.get(); i++) {
              treacheryCardList.get(i).setImage(currentPlayer.treacheryCardHand.get(i).cardImage);
              treacheryCardList.get(i).setVisible(true);
            }
            ;

            characterImage.setImage(currentPlayer.characterImage);
            leader1.setImage(currentPlayer.leaders.get(0).getImage());
            leader2.setImage(currentPlayer.leaders.get(1).getImage());
            leader3.setImage(currentPlayer.leaders.get(2).getImage());
            leader4.setImage(currentPlayer.leaders.get(3).getImage());
            leader5.setImage(currentPlayer.leaders.get(4).getImage());
            for (Entry<Image, Boolean> entry : currentPlayer.leadersInTank.entrySet()) {
              if (leader1.getImage().equals(entry.getKey()) && entry.getValue() == true) {
                leader1.setOpacity(.5);
              } else {
                leader1.setOpacity(1);
              }

              if (leader2.getImage().equals(entry.getKey()) && entry.getValue() == true) {
                leader2.setOpacity(.5);
              } else {
                leader2.setOpacity(1);
              }

              if (leader3.getImage().equals(entry.getKey()) && entry.getValue() == true) {
                leader3.setOpacity(.5);
              } else {
                leader3.setOpacity(1);
              }

              if (leader4.getImage().equals(entry.getKey()) && entry.getValue() == true) {
                leader4.setOpacity(.5);
              } else {
                leader4.setOpacity(1);
              }

              if (leader5.getImage().equals(entry.getKey()) && entry.getValue() == true) {
                leader5.setOpacity(.5);
              } else {
                leader5.setOpacity(1);
              }
            }

            nextRound();
          } else if (passed.size() == bidders.size() - 1 && currentBid > 0) {
            awardCard(highestBidder, currentBid);
          } else {
            nextBidder();
          }
        }

        else {
          treacherySpiceBidSpinner
              .setValueFactory(
                  new IntegerSpinnerValueFactory(0, currentBidder.totalSpice - currentBid, 0, 1));

          currentBidLabel.setText(String.valueOf(currentBid));

          treacherySpiceBidButton.setOnAction(event -> {
            if (Integer.parseInt(treacherySpiceBidSpinner.getValue().toString()) == 0) {
              passed.add(currentBidder);
            } else {
              currentBid =
                  currentBid + Integer.parseInt(treacherySpiceBidSpinner.getValue().toString());
              highestBidder = currentBidder;
            }

            if (passed.size() == players.size()) {
              currentPlayer = players.get(0);

              playerTreacheryCard1.setVisible(false);
              playerTreacheryCard2.setVisible(false);
              playerTreacheryCard3.setVisible(false);
              playerTreacheryCard4.setVisible(false);

              for (int i = 0; i < currentPlayer.treacheryCardsDataModel.get(); i++) {
                treacheryCardList.get(i).setImage(currentPlayer.treacheryCardHand.get(i).cardImage);
                treacheryCardList.get(i).setVisible(true);
              }
              ;

              characterImage.setImage(currentPlayer.characterImage);
              leader1.setImage(currentPlayer.leaders.get(0).getImage());
              leader2.setImage(currentPlayer.leaders.get(1).getImage());
              leader3.setImage(currentPlayer.leaders.get(2).getImage());
              leader4.setImage(currentPlayer.leaders.get(3).getImage());
              leader5.setImage(currentPlayer.leaders.get(4).getImage());
              for (Entry<Image, Boolean> entry : currentPlayer.leadersInTank.entrySet()) {
                if (leader1.getImage().equals(entry.getKey()) && entry.getValue() == true) {
                  leader1.setOpacity(.5);
                } else {
                  leader1.setOpacity(1);
                }

                if (leader2.getImage().equals(entry.getKey()) && entry.getValue() == true) {
                  leader2.setOpacity(.5);
                } else {
                  leader2.setOpacity(1);
                }

                if (leader3.getImage().equals(entry.getKey()) && entry.getValue() == true) {
                  leader3.setOpacity(.5);
                } else {
                  leader3.setOpacity(1);
                }

                if (leader4.getImage().equals(entry.getKey()) && entry.getValue() == true) {
                  leader4.setOpacity(.5);
                } else {
                  leader4.setOpacity(1);
                }

                if (leader5.getImage().equals(entry.getKey()) && entry.getValue() == true) {
                  leader5.setOpacity(.5);
                } else {
                  leader5.setOpacity(1);
                }
              }

              nextRound();
            } else if (passed.size() == bidders.size() - 1 && currentBid > 0) {
              awardCard(highestBidder, currentBid);
            } else {
              nextBidder();
            }
          });
        }
      }
      else {
        passed.add(currentBidder);
        nextBidder();
      }
    }
    else {

      nextRound();
    }
  }

  void nextBidder() {
    if (currentBidder == bidders.get(bidders.size() - 1)) {
      currentBidder = bidders.get(0);
    }
    else {
      currentBidder = bidders.get(bidders.indexOf(currentBidder) + 1);
    }
    characterImage.setImage(currentBidder.characterImage);
    leader1.setImage(currentBidder.leaders.get(0).getImage());
    leader2.setImage(currentBidder.leaders.get(1).getImage());
    leader3.setImage(currentBidder.leaders.get(2).getImage());
    leader4.setImage(currentBidder.leaders.get(3).getImage());
    leader5.setImage(currentBidder.leaders.get(4).getImage());
    continueBiddingRound();
  }

  //Awards card
  private void awardCard(Player winner, int bid) {
    winner.treacheryCardHand.add(currentBiddingTreacheryCard);
    winner.setTreacheryCardsDataModel(winner.treacheryCardsDataModel.add(1).get());
    winner.removeSpice(bid);
    currentBiddingTreacheryCard = null;

    Player lastFirstBidder = bidders.get(0);
    bidders.remove(lastFirstBidder);
    bidders.add(lastFirstBidder);

    passed.clear();
    for (Player player : players) {
      if (player.treacheryCardHand.size() == 4) {
        passed.add(player);
      }
    }

    currentBid = 0;
    highestBidder = null;
    currentBidder = bidders.get(0);
    cardsToDeal--;
    biddingTreacheryCardBack5.setVisible(false);
    biddingTreacheryCardBack4.setVisible(false);
    biddingTreacheryCardBack3.setVisible(false);
    biddingTreacheryCardBack2.setVisible(false);
    biddingTreacheryCardBack1.setVisible(false);
    currentBiddingTreacheryCardBack.setVisible(false);

    continueBiddingRound();
  }



  /*
   * Runs Revival and Movement Round
   */
  private void runRevivalAndMovementRound() {
    musicPlayer.transition(2, currentPlayer.music);
    biddingRoundPane.setVisible(false);
    reviveConfirmPane.setVisible(true);

    for (Entry<Region, ArrayList<SVGPath>> entry : regionPaths.entrySet()) {
      for (SVGPath path : entry.getValue()) {
        path.removeEventHandler(MouseEvent.MOUSE_CLICKED, generalRegionClickHandler);
      }
    }


    // Process free revivals
    if (currentPlayer.troopsInTank.size() != 0) {
      for (int i = 1; i <= currentPlayer.troopsInTank.size() && i <= currentPlayer.freeRevivals;
          i++) {
        for (Troop troop : currentPlayer.troopsInTank) {
          troop.setRegion(null);
          troop.setSector(-1);
          currentPlayer.troopsInReserve.add(troop);
          currentPlayer.troopsInTank.remove(troop);
        }
      }
      currentPlayer.remainingRevivals = 3 - currentPlayer.freeRevivals;

      int maxRevivals = 0;
      if (currentPlayer.freeRevivals < 3) {
        maxRevivals = currentPlayer.totalSpice / 2 > 1 ? currentPlayer.remainingRevivals : 1;
      }

      if (currentPlayer.getPlayerType() == playerTypes.COMPUTER) {
        if (3 - currentPlayer.freeRevivals > 0 && currentPlayer.totalSpice / 2 > maxRevivals ) {
          currentPlayer.reviveTroops(maxRevivals);
          currentPlayer.spiceDataModel.subtract(maxRevivals * 2);
        }
      }
      else {
        reviveTroopsSpinner.setValueFactory(new IntegerSpinnerValueFactory(0, maxRevivals, 0, 1));

        if (3 - currentPlayer.freeRevivals > 0 && currentPlayer.totalSpice / 2 > Integer.parseInt(reviveTroopsSpinner.getValue().toString())) {
          confirmReviveButton.setOnAction(event -> {
            currentPlayer.reviveTroops(Integer.parseInt(reviveTroopsSpinner.getValue().toString()));
            currentPlayer.spiceDataModel.subtract(Integer.parseInt(reviveTroopsSpinner.getValue().toString()));
            landTroops();
          });
          cancelReviveButton.setOnAction(event -> landTroops());
        } else {
          landTroops();
        }
      }
    }
    else {
      landTroops();
    }

  }

  private void landTroops() {
    reviveConfirmPane.setVisible(false);
    movementConfirmPane.setVisible(false);
    landTroopsConfirmPane.setVisible(true);

    if (currentPlayer.troopsInReserve.size() > 0) {
      int maxTroopLanding = currentPlayer.totalSpice / 2;

      if (currentPlayer.getPlayerType() == playerTypes.COMPUTER) {
        if (currentPlayer.faction == factions.FREMEN) {
            Region region = fremenRegionList.get(new Random().nextInt(fremenRegionList.size()));


        } else if (maxTroopLanding > 0 && currentPlayer.troopsInReserve.size() >= maxTroopLanding) {
          for (Region region : regions.values()) {
            if (region.getCurrentSpice() > 0) {
              int numberofTroops = new Random().nextInt(maxTroopLanding) + 1;
              currentPlayer.landTroops(region, region.getSpiceSector(), numberofTroops);
              currentPlayer.removeSpice(numberofTroops * 2);
            }
            if (region.getLandType() == landTypes.STRONGHOLD) {
              int numberofTroops = new Random().nextInt(maxTroopLanding) + 1;
              currentPlayer.landTroops(region, region.getSpiceSector(), numberofTroops);
              currentPlayer.removeSpice(numberofTroops * 2);
            }
          }
        }
      }
      else {
        if (currentPlayer.faction == factions.FREMEN) {
          landTroopsSpinner.setValueFactory(new IntegerSpinnerValueFactory(0,
              currentPlayer.troopsInReserve.size(), 0, 1));
          confirmLandButton.setOnAction(event -> {
            if (fremenRegionList.contains(currentlySelectedRegion)
                & currentlySelectedSector != currentStormSector) {
              currentPlayer.landTroops(currentlySelectedRegion, currentlySelectedSector,
                  Integer.parseInt(landTroopsSpinner.getValue().toString()));
            }
          });
          cancelLandButton.setOnAction(event -> moveTroops());
        } else if (maxTroopLanding > 0 && currentPlayer.troopsInReserve.size() >= maxTroopLanding) {
          landTroopsSpinner.setValueFactory(new IntegerSpinnerValueFactory(0,
              currentPlayer.troopsInReserve.size() < maxTroopLanding ? currentPlayer.troopsInReserve
                  .size() : maxTroopLanding, 0, 1));

          confirmLandButton.setOnAction(event -> {
            if (currentlySelectedRegion != null & currentlySelectedSector != currentStormSector) {
              currentPlayer.landTroops(currentlySelectedRegion, currentlySelectedSector,
                  Integer.parseInt(landTroopsSpinner.getValue().toString()));
              moveTroops();

              if (currentlySelectedRegion.getLandType() == landTypes.STRONGHOLD) {
                currentPlayer
                    .removeSpice(Integer.parseInt(landTroopsSpinner.getValue().toString()));
              } else {
                currentPlayer
                    .removeSpice(Integer.parseInt(landTroopsSpinner.getValue().toString()) * 2);
              }
              for (Player player : players) {
                if (player.faction.equals(factions.SPACE_GUILD)) {
                  if (currentlySelectedRegion.getLandType() == landTypes.STRONGHOLD) {
                    player
                        .addSpice(Integer.parseInt(landTroopsSpinner.getValue().toString()));
                  } else {
                    player
                        .addSpice(Integer.parseInt(landTroopsSpinner.getValue().toString()) * 2);
                  }
                  break;
                }
              }
            }
          });

          cancelLandButton.setOnAction(event -> moveTroops());
        } else {
          moveTroops();
        }
      }
    }
    else {
      moveTroops();
    }

  }

  public void moveTroops() {
    landTroopsConfirmPane.setVisible(false);
    movementConfirmPane.setVisible(true);
    movementPaneLabel.setText("Move Troops from which Region and Sector?");
    moveTroopsSpinner.setValueFactory(new IntegerSpinnerValueFactory(1, 1, 1, 1));

    for (Region region : regions.values()) {
      for (SVGPath path : regionPaths.get(region)) {
        path.setOnMouseClicked(null);
      }
    }

    for (Region region : currentPlayer.troopsOnPlanet.keySet()) {
      if (currentPlayer.troopsOnPlanet.get(region).size() > 0) {
        for (SVGPath path : regionPaths.get(region)) {
          path.setStroke(Color.BLUE);
          path.setOnMouseClicked(fromRegionMovementClickHandler);
        }
      }
    }

    confirmMoveButton.setOnAction(toRegionMovementClickHandler);

    cancelMoveButton.setOnAction(event -> {
      for (Region region : regions.values()) {
        for (SVGPath path : regionPaths.get(region)) {
          path.setOnMouseClicked(generalRegionClickHandler);
          path.setStroke(Color.TRANSPARENT);
        }
      }
      nextPlayer();
    });

  }

  /*
   * Runs Combat Round
   */
  private void runBattleRound() {
    musicPlayer.transition(2, currentPlayer.music);
    combatRoundPane.setVisible(true);
    fights.clear();
    battlePlans.clear();
    currentCombatant = currentPlayer;
    aggressor = currentCombatant;


    //Find which regions to fight in
    for (Player player : players) {
      if (player == currentCombatant) {
        continue;
      }

      for (Region region : player.troopsOnPlanet.keySet()) {
        if (player.troopsOnPlanet.get(region).size() > 0 &&
            currentCombatant.troopsOnPlanet.get(region).size() > 0
            && !region.equals(regions.get("Polar Sink"))) {
          fights.add(region);
        }
      }

    }


    if (fights.size() > 0) {
      runFights(fights);
    }
    else {
      nextPlayer();
    }
  }

  private void runFights (ArrayList<Region> fights) {
    confirmAttackPane.setVisible(true);
    combatPlanPane.setVisible(false);

    combatLeaderImageView.setImage(null);
    combatDefenseCard.setImage(null);
    combatWeaponCard.setImage(null);

    battlePlans.put(currentCombatant, new BattlePlan());

    if (currentPlayer.getPlayerType() == playerTypes.COMPUTER) {
      Region fightRegion = fights.get(new Random().nextInt(fights.size()));

      battlePlans.get(currentCombatant).setLeader(currentCombatant.leaders.get(new Random().nextInt(currentCombatant.leaders.size())));
      battlePlans.get(currentCombatant).setTroopsDedicated(new Random().nextInt(currentCombatant.troopsOnPlanet.get(fightRegion).size()));
      for (TreacheryCard card : currentCombatant.treacheryCardHand) {
        if (card.subtype == cardSubTypes.DEFENSE) {
          battlePlans.get(currentCombatant).setDefense(card);
          break;
        }
      }

      for (TreacheryCard card : currentCombatant.treacheryCardHand) {
        if (card.subtype == cardSubTypes.WEAPON) {
          battlePlans.get(currentCombatant).setDefense(card);
          break;
        }
      }
    }
    else {
      for (Region region : regions.values()) {
        for (SVGPath path : regionPaths.get(region)) {
          path.setOnMouseClicked(null);
        }
      }

      for (Region region : fights) {
        for (SVGPath path : regionPaths.get(region)) {
          path.setStroke(currentPlayer.color);
          path.setOnMouseClicked(combatRegionClickHandler);
        }
      }

      confirmAttackButton.setOnAction(event -> {
        confirmAttackPane.setVisible(false);
        combatPlanPane.setVisible(true);
        combatOpponentChoiceBox.getItems().clear();

        for (Player player : players.filtered(item -> item.faction != currentCombatant.faction)) {
          combatOpponentChoiceBox.getItems().add(player.faction.toString());
        }
        combatOpponentChoiceBox.getSelectionModel().select(0);

        confirmPlanButton.setOnAction(event1 -> {
          if (fights.contains(currentlySelectedRegion) && currentCombatant != combatOpponent) {
            battlePlans.get(currentCombatant).region = currentlySelectedRegion;
            battlePlans.get(currentCombatant).troopsDedicated = (int) combatStrengthSlider
                .getValue();
            for (Player player : players) {
              if (player.faction.toString().equals(combatOpponentChoiceBox.getValue())) {
                combatOpponent = player;
                break;
              }
            }

          }
          nextCombatant();
        });
      });
    }
  }

  private void nextCombatant() {
    if (currentCombatant == combatOpponent) {
      resolveBattle();
    }
    else {
      currentCombatant = combatOpponent;

      ArrayList<ImageView> treacheryCardList = new ArrayList<>(Arrays
          .asList(playerTreacheryCard1, playerTreacheryCard2, playerTreacheryCard3,
              playerTreacheryCard4));
      for (int i = 0; i < currentPlayer.treacheryCardsDataModel.get(); i++) {
        treacheryCardList.get(i).setImage(currentCombatant.treacheryCardHand.get(i).cardImage);
      }

      characterImage.setImage(currentCombatant.characterImage);
      leader1.setImage(currentCombatant.leaders.get(0).getImage());
      leader2.setImage(currentCombatant.leaders.get(1).getImage());
      leader3.setImage(currentCombatant.leaders.get(2).getImage());
      leader4.setImage(currentCombatant.leaders.get(3).getImage());
      leader5.setImage(currentCombatant.leaders.get(4).getImage());
      for (Entry<Image, Boolean> entry : currentCombatant.leadersInTank.entrySet()) {
        if (leader1.getImage().equals(entry.getKey()) && entry.getValue() == true) {
          leader1.setOpacity(.5);
        } else {
          leader1.setOpacity(1);
        }

        if (leader2.getImage().equals(entry.getKey()) && entry.getValue() == true) {
          leader2.setOpacity(.5);
        } else {
          leader2.setOpacity(1);
        }

        if (leader3.getImage().equals(entry.getKey()) && entry.getValue() == true) {
          leader3.setOpacity(.5);
        } else {
          leader3.setOpacity(1);
        }

        if (leader4.getImage().equals(entry.getKey()) && entry.getValue() == true) {
          leader4.setOpacity(.5);
        } else {
          leader4.setOpacity(1);
        }

        if (leader5.getImage().equals(entry.getKey()) && entry.getValue() == true) {
          leader5.setOpacity(.5);
        } else {
          leader5.setOpacity(1);
        }
      }
    }
    runFights(fights);
  }

  private void resolveBattle() {
    sfxPlayer.play(sfxFiles.CANNON);
    Region region = battlePlans.get(aggressor).region;

    //If one fires a Lasegun and the other person has a Shield (Projectile Defense) then...
    //KILL EVERYONE AND BURN THE SPICE TO THE GROUND!!!
    if (battlePlans.get(aggressor).getWeapon() == lasegun &&
        battlePlans.get(combatOpponent).getDefense().classification == cardClassifications.PROJECTILE &&
        battlePlans.get(combatOpponent).getDefense().subtype == cardSubTypes.DEFENSE ||
        battlePlans.get(combatOpponent).getWeapon() == lasegun &&
        battlePlans.get(combatOpponent).getDefense().classification == cardClassifications.PROJECTILE &&
        battlePlans.get(combatOpponent).getDefense().subtype == cardSubTypes.DEFENSE ) {

        aggressor.killTroops(region, aggressor.troopsOnPlanet.get(region).size());
        combatOpponent.killTroops(region, combatOpponent.troopsOnPlanet.get(region).size());
        for (ArrayList<ImageView> list : regionTroopMarkers.get(region).values()) {
          for (ImageView image : list) {
            image.setImage(null);
          }
        }
        region.removeSpice(region.getCurrentSpice());
    }

    if (battlePlans.get(aggressor).getLeader() != null && aggressor.leadersInTank.get(battlePlans.get(aggressor).getLeader().image).booleanValue() == false) {
      if (battlePlans.get(combatOpponent).getWeapon() != null) {
        if (battlePlans.get(aggressor).getDefense() != null) {
          if (battlePlans.get(combatOpponent).getWeapon().classification !=
              battlePlans.get(aggressor).defense.classification) {
            aggressor.killLeader(battlePlans.get(aggressor).getLeader());
          }
        }
        else {
          aggressor.killLeader(battlePlans.get(aggressor).getLeader());
        }
      }
    }


    if (battlePlans.get(combatOpponent).getLeader() != null && !combatOpponent.leadersInTank.containsKey(battlePlans.get(combatOpponent).getLeader().image)) {
      if (battlePlans.get(aggressor).getWeapon() != null) {
        if (battlePlans.get(combatOpponent).getDefense() != null) {
          if (battlePlans.get(aggressor).getWeapon().classification !=
              battlePlans.get(combatOpponent).defense.classification) {
            combatOpponent.killLeader(battlePlans.get(combatOpponent).getLeader());
          }
        }
        else {
          combatOpponent.killLeader(battlePlans.get(combatOpponent).getLeader());
        }
      }
    }

    int aggressorStrength = battlePlans.get(aggressor).getTroopsDedicated();
    if (battlePlans.get(aggressor).getLeader() != null && aggressor.leadersInTank.get(battlePlans.get(aggressor).getLeader().image).booleanValue() == false) {
      aggressorStrength += battlePlans.get(aggressor).getLeader().power;
    }

    int combatOpponentStrength = battlePlans.get(combatOpponent).getTroopsDedicated();
    if (battlePlans.get(combatOpponent).getLeader() != null && combatOpponent.leadersInTank.get(battlePlans.get(combatOpponent).getLeader().image).booleanValue() == false) {
      combatOpponentStrength += battlePlans.get(combatOpponent).getLeader().power;
    }

    if (aggressorStrength < combatOpponentStrength) {
      aggressor.killTroops(region, aggressor.troopsOnPlanet.get(region).size());
      combatOpponent.killTroops(region, battlePlans.get(combatOpponent).troopsDedicated);
    }
    else {
      combatOpponent.killTroops(region, combatOpponent.troopsOnPlanet.get(region).size());
      aggressor.killTroops(region, battlePlans.get(aggressor).troopsDedicated);
    }
    fights.remove(region);

    for (ArrayList<ImageView> list : regionTroopMarkers.get(region).values()) {
      for (ImageView image : list) {
        if (aggressor.troopsOnPlanet.get(region).size() == 0 && image.getImage().equals(aggressor.troopsInTank.get(0).unitImage)) {
          image.setImage(null);
        }
        if (combatOpponent.troopsOnPlanet.get(region).size() == 0 && image.getImage().equals(combatOpponent.troopsInTank.get(0).unitImage)) {
          image.setImage(null);
        }
      }
    }

    if (fights.size() > 0) {
      runFights(fights);
    }
    else {
      nextRound();
    }
  }

  /*
   * Runs Collection Round
   */
  private void runCollectionRound() {
    for (Region region : regionPaths.keySet()) {
      for (SVGPath path : regionPaths.get(region)) {
        path.setOnMouseClicked(generalRegionClickHandler);
      }
    }

    for (Player player : players) {
      for (Region region : spiceRegions) {
        if (region.getSpiceSector() != -1 &&
            region.getCurrentSpice() > 0 &&
            player.troopsOnPlanet.get(region).size() > 0) {
            for (int i = 1; i <= player.troopsOnPlanet.get(region).size(); i++) {
              if (player.troopsOnPlanet.get(regions.get("Carthag")).size() > 0 ||
                  player.troopsOnPlanet.get(regions.get("Arrakeen")).size() > 0) {
                if (region.getCurrentSpice() >= 3) {
                  region.removeSpice(3);
                  player.addSpice(3);
                } else if (region.getCurrentSpice() == 2) {
                  region.removeSpice(2);
                  player.addSpice(2);
                } else {
                  region.removeSpice(1);
                  player.addSpice(1);
                }
              } else {
                if (region.getCurrentSpice() >= 2) {
                  region.removeSpice(2);
                  player.addSpice(2);
                } else {
                  region.removeSpice(1);
                  player.addSpice(1);
                }
              }
            }
        }
      }
    }
    nextRound();
  }

  public void setMaxMusicVolume(double maxVolume) {
    if (maxMusicVolume == -1) {
      maxMusicVolume = maxVolume;
    }
  }

  public void setMaxSFXVolume(double maxVolume) {
    if (maxSFXVolume == -1) {
      maxSFXVolume = maxVolume;
    }
  }

  public class Player {

    private Paint color;
    private playerTypes playerType;
    private factions faction;
    private Image characterImage;
    private difficulties difficulty;
    private HashMap<Region, ArrayList<Troop>> troopsOnPlanet = new HashMap<>();
    private ArrayList<Troop> troopsInReserve = new ArrayList<>();
    private ArrayList<Troop> troopsInTank = new ArrayList<>();
    private ArrayList<TreacheryCard> treacheryCardHand = new ArrayList();
    private int totalSpice;
    private int freeRevivals;
    private int remainingRevivals;
    private ArrayList<game.Player> alliance = new ArrayList<>();
    private int dot; //Dot number sector 1 clockwise
    private ArrayList<Leader> leaders = new ArrayList<>();
    private HashMap<Image, Boolean> leadersInTank = new HashMap<>();
    private boolean ableToReviveLeaders = false;
    private musicFiles music;

    private SimpleStringProperty factionDataModel = new SimpleStringProperty();
    private SimpleIntegerProperty spiceDataModel = new SimpleIntegerProperty();
    private SimpleIntegerProperty troopsInReserveDataModel = new SimpleIntegerProperty();
    private SimpleIntegerProperty troopsInTanksDataModel = new SimpleIntegerProperty();
    private SimpleIntegerProperty treacheryCardsDataModel = new SimpleIntegerProperty();

    private Player() { }

    public Player(factions faction, playerTypes type) {
      this.faction = faction;
      setFactionDataModel(faction.toString());
      this.playerType = type;
      Random dot = new Random();
      int nextDot = dot.nextInt(dots.size());
      this.dot = dots.get(nextDot);
      dots.remove(nextDot);

      Image troopImage;
      Image advancedTroopImage;
      switch (faction) {
        case ATREIDES:
          troopImage = new Image(troopImages.ATREIDES.toString());
          break;
        case BENE_GESSERIT:
          troopImage = new Image(troopImages.BENE_GESSERIT.toString());
          break;
        case EMPEROR:
          troopImage = new Image(troopImages.EMPEROR.toString());
          advancedTroopImage = new Image(troopImages.EMPEROR_ADVANCED.toString());
          break;
        case FREMEN:
          troopImage = new Image(troopImages.FREMEN.toString());
          advancedTroopImage = new Image(troopImages.FREMEN_ADVANCED.toString());
          break;
        case HARKONNEN:
          troopImage = new Image(troopImages.HARKONNEN.toString());
          break;
        default:
          troopImage = new Image(troopImages.SPACE_GUILD.toString());
          break;
      }
      for (int i = 1; i <= 20; i++) {
        troopsInReserve.add(new Troop(troopImage));
      }

      for (Region region : regions.values()) {
        troopsOnPlanet.put(region, new ArrayList<>());
      }

      loadFaction();
      placeInitialTroops();
    }

    public void placeInitialTroops() {
      Region region;
      Troop troop;
      switch(faction) {
        case ATREIDES:
          region = regions.get("Arrakeen");

          for (int i = 1; i <= 10; i++) {
            troop = troopsInReserve.get(0);
            troop.setRegion(region);
            troop.setSector(10);
            troopsOnPlanet.get(region).add(troop);
            troopsInReserve.remove(troop);
          }
          break;
        case BENE_GESSERIT:
          region = regions.get("Polar Sink");
          troop = troopsInReserve.get(0);
          troop.setRegion(region);
          troop.setSector(0);
          troopsOnPlanet.get(region).add(troop);
          troopsInReserve.remove(troop);
          break;

        case HARKONNEN:
          region = regions.get("Carthag");
          for (int i = 1; i <= 11; i++) {
            troop = troopsInReserve.get(0);
            troop.setRegion(region);
            troop.setSector(11);
            troopsOnPlanet.get(region).add(troop);
            troopsInReserve.remove(troop);
          }
          break;

        case SPACE_GUILD:
          region = regions.get("Tuek's Sietch");
          for (int i = 1; i <= 5; i++) {
            troop = troopsInReserve.get(0);
            troop.setRegion(region);
            troop.setSector(5);
            troopsOnPlanet.get(region).add(troop);
            troopsInReserve.remove(troop);
          }
          break;
      }
      setTroopsInReserveDataModel(troopsInReserve.size());
    }

    public playerTypes getPlayerType() {
      return playerType;
    }

    private void loadFaction() {
      switch (faction) {
        //All players have total of 3 revivals per turn, and 1 leader if all 5 in are tanks
        //Spice per extra revival is 2 spice, Spice per leader is leader's fighting cost
        case ATREIDES:
        /*
        To Start: 10 troops in Arrakeen & 10 in reservce, start with 10 spice
        Free Revivals: 2
        */
        characterImage = new Image(characterImages.ATREIDES.toString());
        music = musicFiles.ATREIDES;
          totalSpice = 10;
          freeRevivals = 2;
          color = Color.GREEN;
          leaders.add(new Leader("Duncan Idaho", factions.ATREIDES, 2, new Image(leaderImages.DUNCAN_IDAHO.toString())));
          leaders.add(new Leader("Gurney Halleck", factions.ATREIDES, 4, new Image(leaderImages.GURNEY_HALLEC.toString())));
          leaders.add(new Leader("Lady Jessica", factions.ATREIDES, 5, new Image(leaderImages.LADY_JESSICA.toString())));
          leaders.add(new Leader("Thufir Hawat", factions.ATREIDES, 5, new Image(leaderImages.THUFIR_HAWAT.toString())));
          leaders.add(new Leader("Wellington Yueh", factions.ATREIDES, 1, new Image(leaderImages.WELLINGTON_YUEH.toString())));
          break;
        case BENE_GESSERIT:
        /*
        To Start: 1 troop in polar sink and 19 tokens off planet, start with 5 spice
        Free Revivals: 1
         */
          characterImage = new Image(characterImages.BENE_GESSERIT.toString());
          music = musicFiles.BENE_GESSERIT;
          totalSpice = 5;
          freeRevivals = 1;
          color = Color.CORNFLOWERBLUE;
          leaders.add(new Leader("Alia", factions.BENE_GESSERIT, 5, new  Image(leaderImages.ALIA.toString())));
          leaders.add(new Leader("Margot Lady Fenring", factions.BENE_GESSERIT, 5, new Image(leaderImages.MARGOT_LADY_FENRING.toString())));
          leaders.add(new Leader("Princess Irulan", factions.BENE_GESSERIT, 5, new Image(leaderImages.PRINCESS_IRULAN.toString())));
          leaders.add(new Leader("Reverend Mother Ramallo", factions.BENE_GESSERIT, 5, new Image(leaderImages.REVEREND_MOTHER_RAMALLO.toString())));
          leaders.add(new Leader("Wanna Marcus", factions.BENE_GESSERIT, 5, new Image(leaderImages.WANNA_MARCUS.toString())));
          break;
        case EMPEROR:
        /*
        To start: 20 Tokens off planet, start with 10 spice
        Free revivals: 1
        */
          characterImage = new Image(characterImages.EMPEROR.toString());
          music = musicFiles.EMPEROR;
          totalSpice = 10;
          freeRevivals = 1;
          color = Color.RED;
          leaders.add(new Leader("Bashar", factions.EMPEROR, 2, new Image(leaderImages.BASHAR.toString())));
          leaders.add(new Leader("Burseg", factions.EMPEROR, 3, new Image(leaderImages.BURSEG.toString())));
          leaders.add(new Leader("Caid", factions.EMPEROR, 3, new Image(leaderImages.CAID.toString())));
          leaders.add(new Leader("Captain Aramsham", factions.EMPEROR, 5, new Image(leaderImages.CAPTAIN_ARAMSHAM.toString())));
          leaders.add(new Leader("Count Hasimir Fenring", factions.EMPEROR, 6, new Image(leaderImages.COUNT_HASIMIR_FENRING.toString())));
          break;
        case FREMEN:
        /*
        At start:  10tokens distributed at will to Sietch Tabr, False Wall South, and False Wall West, and 10 on far side of planet, start with 3 spice
        Free revivals: 3
         */
          characterImage = new Image(characterImages.FREMEN.toString());
          music = musicFiles.FREMEN;
          totalSpice = 3;
          freeRevivals = 3;
          color = Color.LIGHTGOLDENRODYELLOW;
          leaders.add(new Leader("Chani", factions.FREMEN, 6, new Image(leaderImages.CHANI.toString())));
          leaders.add(new Leader("Jamis", factions.FREMEN, 2, new Image(leaderImages.JAMIS.toString())));
          leaders.add(new Leader("Otheym", factions.FREMEN, 5, new Image(leaderImages.OTHEYM.toString())));
          leaders.add(new Leader("Shadout Mapes", factions.FREMEN, 3, new Image(leaderImages.SHADOUT_MAPES.toString())));
          leaders.add(new Leader("Stilgar", factions.FREMEN, 7, new Image(leaderImages.STILGAR.toString())));
          break;
        case HARKONNEN:
        /*
        At start: 10 tokens in Carthag, 10 of planet, start with 10 spice
        Free Revivals: 2
         */
          characterImage = new Image(characterImages.HARKONNEN.toString());
          music = musicFiles.HARKONNEN;
          totalSpice = 10;
          freeRevivals = 2;
          color = Color.DARKGRAY;
          leaders.add(new Leader("Beast Rabban", factions.HARKONNEN, 5, new Image(leaderImages.BEAST_RABBAN.toString())));
          leaders.add(new Leader("Captain Iakin Nefud", factions.HARKONNEN, 2, new Image(leaderImages.CAPTAIN_IAKIN_NEFUD.toString())));
          leaders.add(new Leader("Feyd-Rautha", factions.HARKONNEN, 6, new Image(leaderImages.FEYD_RAUTHA.toString())));
          leaders.add(new Leader("Peter De Vries", factions.HARKONNEN, 3, new Image(leaderImages.PETER_DE_VRIES.toString())));
          leaders.add(new Leader("Umman Kudu", factions.HARKONNEN, 1, new Image(leaderImages.UMMAN_KUDU.toString())));
          break;
        case SPACE_GUILD:
        /*
        At start:5 Tokens in Tuek's Sietch and 15 off planet, start with 5 spice
        Free revivals" 1
         */
          characterImage = new Image(characterImages.SPACE_GUILD.toString());
          music = musicFiles.SPACE_GUILD;
          color = Color.ORANGE;
          totalSpice = 5;
          freeRevivals = 1;
          leaders.add(new Leader("Esmar Tuek", factions.SPACE_GUILD, 3, new Image(leaderImages.ESMAR_TUEK.toString())));
          leaders.add(new Leader("Master Bewt", factions.SPACE_GUILD, 3, new Image(leaderImages.MASTER_BEWT.toString())));
          leaders.add(new Leader("Guild Representative", factions.SPACE_GUILD, 1, new Image(leaderImages.GUILD_REPRESENTATIVE.toString())));
          leaders.add(new Leader("Soo-Soo Sook", factions.SPACE_GUILD, 2, new Image(leaderImages.SOOSOO_SOOK.toString())));
          leaders.add(new Leader("Staban Tuek", factions.SPACE_GUILD, 5, new Image(leaderImages.STABAN_TUEK.toString())));
          break;
      }

      for (int i = 0; i < 5; i++) {
        leadersInTank.put(leaders.get(i).getImage(), false);
      }

      setSpiceDataModel(totalSpice);
    }

    public void killLeader(Leader leader) {
      leadersInTank.put(leader.image, true);
      int numberOfLeadersInTank = 0;
      for (Boolean bool : leadersInTank.values()) {
        if (bool == true) {
          numberOfLeadersInTank++;
        }
        else {
          break;
        }
      }
      if (numberOfLeadersInTank == 5) {
        ableToReviveLeaders = true;
      }
    }

    public void reviveLeader(Leader leader) {
      leadersInTank.put(leader.image, false);

      int numberOfLeadersInTank = 5;
      for (Boolean bool : leadersInTank.values()) {
        if (bool == false) {
          numberOfLeadersInTank--;
        }
        else {
          break;
        }
      }
      if (numberOfLeadersInTank == 0) {
        ableToReviveLeaders = false;
      }
    }

    void landTroops(Region region, int sector, int numberOfTroops) {
      if (numberOfTroops <= troopsInReserve.size()) {
        Troop troop;
        for (int i = 1; i <= numberOfTroops; i++) {
          troop = troopsInReserve.get(0);
          troop.setRegion(region);
          troop.setSector(sector);
          troopsInReserve.remove(troop);
          troopsOnPlanet.get(region).add(troop);
          setTroopsInReserveDataModel(troopsInReserve.size());
          for (int j = 0; j < regionTroopMarkers.get(region).get(sector).size(); j++) {
            ImageView imageView = regionTroopMarkers.get(region).get(sector).get(j);

            if (imageView.getImage() == troop.getUnitImage()) {
              break;
            }
            else if (imageView.getImage() == null) {
              imageView.setImage(troop.unitImage);
              break;
            }
          }
        }
      }
    }

    void reviveTroops(int numberToRevive) {
      Troop troop;

      for (int i = 0; i > numberToRevive && i <= troopsInTank.size(); i++) {
        troop = troopsInTank.get(0);
        troopsInReserve.add(troop);
        troopsInTank.remove(troop);
        setTroopsInReserveDataModel(troopsInReserve.size());
        setTroopsInTanksDataModel(troopsInTank.size());
        for (Node tank : tanksTroopPane.getChildren()) {
          if (((ImageView) tank).getImage() == troop.getUnitImage()) {
            ((ImageView) tank).setImage(null);
            break;
          }
        }
      }
    }

    void moveTroops(Region fromRegion, int fromSector, Region toRegion, int toSector, int number) {
       if (troopsOnPlanet.get(fromRegion).size() - number >= 0) {
         Troop troop;
         ArrayList<Troop> troopsFromSector = new ArrayList<>();
         for (int i = 0; i < number; i++) {
           troop = troopsOnPlanet.get(fromRegion).get(i);
           if (troop.getSector() == fromSector) {
             troopsFromSector.add(troop);
           }
         }

         /* If the troops from the selected aren't enough to fulfill move order,
         *  then check if a troop is not already in the move list.  If not, find out if it is in the
         * lowest available sector, and add it to the move list if it is until the move list
         * (troopsFromSector) equals the order (number)
          */
         if (troopsFromSector.size() < number) {
           for (int i = 1; i <= number - troopsFromSector.size(); i++) {
             troop = troopsOnPlanet.get(fromRegion).get(i);
             if (!troopsFromSector.contains(troop)) {
               for (int j = 0; j < troop.getRegion().getSectors().size(); j++) {
                 int sector = troop.getRegion().getSectors().get(j);
                 if (sector != fromSector && troop.getSector() == sector) {
                   troopsFromSector.add(troop);
                   break;
                 }
               }
             }
           }
         }

         for (Troop trooper : troopsFromSector) {
           if (trooper.getSector() == fromSector) {
             troopsOnPlanet.get(fromRegion).remove(trooper);
             troopsOnPlanet.get(toRegion).add(trooper);
             trooper.setSector(toSector);
           }
         }
       }
    }

    void killTroops(Region region, int number) {
      Troop troop;

      if (number > troopsOnPlanet.get(region).size()) {
        number = troopsOnPlanet.get(region).size();
      }

      for (int i = 1; i <= number; i++) {
        troop = troopsOnPlanet.get(region).get(0);
        troopsOnPlanet.get(region).remove(troop);
        troopsInTank.add(troop);
        troop.setSector(-1);
        troop.setRegion(null);

        for (Node tank : tanksTroopPane.getChildren()) {
          if (((ImageView) tank).getImage() == null) {
            ((ImageView) tank).setImage(troop.getUnitImage());
            break;
          }
        }
      }


      setTroopsInTanksDataModel(troopsInTank.size());
    }


    public void addSpice(int spiceToAdd) {
      totalSpice += spiceToAdd;
      setSpiceDataModel(totalSpice);
    }

    public void removeSpice(int spiceToRemove) {
      totalSpice -= spiceToRemove;
      setSpiceDataModel(totalSpice);
    }

    public String getFactionDataModel() {
      return factionDataModel.get();
    }

    public SimpleStringProperty factionDataModelProperty() {
      return factionDataModel;
    }

    public void setFactionDataModel(String factionDataModel) {
      this.factionDataModel.set(factionDataModel);
    }

    public int getSpiceDataModel() {
      return spiceDataModel.get();
    }

    public SimpleIntegerProperty spiceDataModelProperty() {
      return spiceDataModel;
    }

    public void setSpiceDataModel(int spiceDataModel) {
      this.spiceDataModel.set(spiceDataModel);
    }

    public int getTroopsInReserveDataModel() {
      return troopsInReserveDataModel.get();
    }

    public SimpleIntegerProperty troopsInReserveDataModelProperty() {
      return troopsInReserveDataModel;
    }

    public void setTroopsInReserveDataModel(int troopsInReserveDataModel) {
      this.troopsInReserveDataModel.set(troopsInReserveDataModel);
    }

    public int getTroopsInTanksDataModel() {
      return troopsInTanksDataModel.get();
    }

    public SimpleIntegerProperty troopsInTanksDataModelProperty() {
      return troopsInTanksDataModel;
    }

    public void setTroopsInTanksDataModel(int troopsInTanksDataModel) {
      this.troopsInTanksDataModel.set(troopsInTanksDataModel);
    }

    public int getTreacheryCardsDataModel() {
      return treacheryCardsDataModel.get();
    }

    public SimpleIntegerProperty treacheryCardsDataModelProperty() {
      return treacheryCardsDataModel;
    }

    public void setTreacheryCardsDataModel(int treacheryCardsDataModel) {
      this.treacheryCardsDataModel.set(treacheryCardsDataModel);
    }
  }

  private class Troop {

    private Image unitImage;
    private Image advancedUnitImage = null;
    private int sector = -1;
    private Region region = null;

    private boolean inTank = false;
    public Troop(Image unitImage) {
      this.unitImage = unitImage;
    }

    private Image getUnitImage() { return unitImage; }

    private Image getAdvancedUnitImage() { return advancedUnitImage; }

    private int getSector() { return sector; }

    private void setSector(int sector) { this.sector = sector; }

    private Region getRegion() { return region; }

    private void setRegion(Region region) { this.region = region; }

    private boolean isInTank() { return inTank; }

    private void setInTank(boolean inTank) { this.inTank = inTank; }
  }


  private class BattlePlan {
    Leader leader = null;
    TreacheryCard weapon = null;
    TreacheryCard defense = null;
    int troopsDedicated = 0;
    Region region = null;

    private BattlePlan() {}

    private Leader getLeader() {
      return leader;
    }

    private void setLeader(Leader leader) {
      this.leader = leader;
    }

    private TreacheryCard getWeapon() {
      return weapon;
    }

    private void setWeapon(TreacheryCard weapon) {
      this.weapon = weapon;
    }

    private TreacheryCard getDefense() {
      return defense;
    }

    private void setDefense(TreacheryCard defense) {
      this.defense = defense;
    }

    private int getTroopsDedicated() {
      return troopsDedicated;
    }

    private void setTroopsDedicated(int troopsDedicated) {
      this.troopsDedicated = troopsDedicated;
    }
  }
}
