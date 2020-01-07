package game;

import game.Enums.difficulties;
import game.Enums.factions;
import game.Enums.playerTypes;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

public class Player {
  private static ArrayList<Integer> dots = new ArrayList<>(Arrays.asList(1,2,3,4,5,6));
  private playerTypes playerType;
  private factions faction;
  private difficulties difficulty;
  private HashMap<Integer, HashMap<Region, Integer>> troopsOnPlanet = new HashMap() {
    {
      put(0, new HashMap<Region, Integer>());
      put(1, new HashMap<Region, Integer>());
      put(2, new HashMap<Region, Integer>());
      put(3, new HashMap<Region, Integer>());
      put(4, new HashMap<Region, Integer>());
      put(5, new HashMap<Region, Integer>());
      put(6, new HashMap<Region, Integer>());
      put(7, new HashMap<Region, Integer>());
      put(8, new HashMap<Region, Integer>());
      put(9, new HashMap<Region, Integer>());
      put(10, new HashMap<Region, Integer>());
      put(11, new HashMap<Region, Integer>());
      put(12, new HashMap<Region, Integer>());
      put(13, new HashMap<Region, Integer>());
      put(14, new HashMap<Region, Integer>());
      put(15, new HashMap<Region, Integer>());
      put(16, new HashMap<Region, Integer>());
      put(17, new HashMap<Region, Integer>());
      put(18, new HashMap<Region, Integer>());
    }

  }; //Troops are sorted by sector 0-18 then placed in Region
  private HashMap<Region, Integer> troopsInRegion = new HashMap<>();
  private int troopsInReserve;
  private ArrayList<TreacheryCard> treacheryCardHand = new ArrayList();
  private int totalSpice;
  private int freeRevivals;
  private ArrayList<Player> alliance = new ArrayList<>();
  private int dot; //Dot number sector 1 clockwise

  private Player() { }

  public Player(factions faction, HashMap<String,Region> regions) {
    Scanner scan = null;
    try {
      scan = new Scanner(new File("DuneMapPaths.svg"));

    scan.useDelimiter("\" />");

    while (scan.hasNext()) {
      String path = scan.next();
      path = path.replaceAll("\n", "");
      String key = path
          .substring(path.indexOf(" id=\"") + 5, path.indexOf("\"", path.indexOf("id=\"") + 4));
      int sector = Integer.parseInt(key.substring(key.length() - 2));

      troopsOnPlanet.putIfAbsent(sector, new HashMap<>());
      troopsOnPlanet.get(sector).put(regions.get(key.substring(0, key.length() - 3)), 0);
    }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    this.faction = faction;
    playerType = playerTypes.HUMAN;
    Random dot = new Random();
    int nextDot = dot.nextInt(dots.size());
    this.dot = dots.get(nextDot);
    dots.remove(nextDot);
    loadFaction();
  }

  public Player(factions faction, difficulties difficulty) {
    this.faction = faction;
    playerType = playerTypes.COMPUTER;
    this.difficulty = difficulty;
    Random dot = new Random();
    int nextDot = dot.nextInt(dots.size());
    this.dot = dots.get(nextDot);
    dots.remove(nextDot);
    loadFaction();
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
        troopsInReserve = 10;
        totalSpice = 10;
        freeRevivals = 2;
        break;
      case BENE_GESSERIT:
        /*
        To Start: 1 troop in polar sink and 19 tokens off planet, start with 5 spice
        Free Revivals: 1
         */
        troopsInReserve = 19;
        totalSpice = 5;
        freeRevivals = 1;
        break;
      case EMPEROR:
        /*
        To start: 20 Tokens off planet, start with 10 spice
        Free revivals: 1
        */
        troopsInReserve = 20;
        totalSpice = 10;
        freeRevivals = 1;
        break;

      case FREMEN:
        /*
        At start:  10tokens distributed at will to Sietch Tabr, False Wall South, and False Wall West, and 10 on far side of planet, start with 3 spice
        Free revivals: 3
         */
        troopsInReserve = 10;
        totalSpice = 3;
        freeRevivals = 3;
        break;
      case HARKONNEN:
        /*
        At start: 10 tokens in Carthag, 10 of planet, start with 10 spice
        Free Revivals: 2
         */
        troopsInReserve = 10;
        totalSpice = 10;
        freeRevivals = 2;
        break;
      case SPACE_GUILD:
        /*
        At start:5 Tokens in Tuek's Sietch and 15 off planet, start with 5 spice
        Free revivals" 1
         */
        troopsInReserve = 15;
        totalSpice = 5;
        freeRevivals = 1;
        break;
    }
  }

  public void placeTroops(String key) {
    String region = key.substring(0, key.length() - 3);
    String sector = key.substring(key.length() - 2);
  }
}
