package game;

public class Enums {

  public enum musicFiles {
    HARKONNEN {
      @Override
      public String toString() { return "music/mechanolith-by-kevin-macleod.mp3"; }
    },

    ATREIDES {
      @Override
      public String toString() {
        return "music/all-this-by-kevin-macleod.mp3";
      }
    },

    BENE_GESSERIT {
      @Override
      public String toString() {
        return "music/clash-defiant-by-kevin-macleod.mp3";
      }
    },

    FREMEN {
      @Override
      public String toString() {
        return "music/crossing-the-chasm-by-kevin-macleod.mp3";
      }
    },

    SPACE_GUILD {
      @Override
      public String toString() {
        return "music/dangerous-by-kevin-macleod.mp3";
      }
    },

    EMPEROR {
      @Override
      public String toString() {
        return "music/darkling-by-kevin-macleod.mp3";
      }
    },

    NEXUS {
      @Override
      public String toString() {
        return "music/district-four-by-kevin-macleod.mp3";
      }
    },

    TREACHERY {
      @Override
      public String toString() {
        return "music/five-armies-by-kevin-macleod.mp3";
      }
    },

    WIN {
      @Override
      public String toString() {
        return "music/hitman-by-kevin-macleod.mp3";
      }
    },

    LOSS {
      @Override
      public String toString() {
        return "music/movement-proposition-by-kevin-macleod.mp3";
      }
    },

    TITLE {
      @Override
      public String toString() {
        return "music/oppressive-gloom-by-kevin-macleod.mp3";
      }
    };
  }


  public enum sfxFiles {

    STORM {
      @Override
      public String toString() {
        return "sfx/heavy-rain-daniel_simon.mp3";
      }
    },

    CANNON {
      @Override
      public String toString() {
        return "sfx/Laser_Cannon-Mike_Koenig-797224747.mp3";
      }
    };
  }

  public enum landTypes {
    SAND {
      @Override
      public String toString() {
        return "Sand";
      }
    },
    ROCK {
      @Override
      public String toString() {
        return "Rock";
      }
    },
    STRONGHOLD{
      @Override
      public String toString() {
        return "Stronghold";
      }
    },
    POLARSINK{
      @Override
      public String toString() {
        return "Polar Sink";
      }
    }
  }

  public enum playerTypes {
    HUMAN {
      @Override
      public String toString() { return "Human"; }
    },
    COMPUTER {
      @Override
      public String toString() { return "Computer"; } ;
    }/*,
    NETWORK {
      @Override
      public String toString() { return "Network Player"; }
    }
    */
  }

  public enum leaderImages {
    DUNCAN_IDAHO {
      @Override
      public String toString() { return "images/factionImages/leaderTokenAtreidesDuncanIdaho.png"; }
    },
    GURNEY_HALLEC {
      @Override
      public String toString() { return "images/factionImages/leaderTokenAtreidesGurneyHalleck.png"; }
    },
    LADY_JESSICA {
      @Override
      public String toString() { return "images/factionImages/leaderTokenAtreidesLadyJessica.png"; }
    },
    THUFIR_HAWAT {
      @Override
      public String toString() { return "images/factionImages/leaderTokenAtreidesThufirHawat.png"; }
    },
    WELLINGTON_YUEH {
      @Override
      public String toString() { return "images/factionImages/leaderTokenAtreidesWellingtonYueh.png"; }
    },
    ALIA {
      @Override
      public String toString() { return "images/factionImages/leaderTokenBeneGesseritAlia.png"; }
    },
    MARGOT_LADY_FENRING {
      @Override
      public String toString() { return "images/factionImages/leaderTokenBeneGesseritMargotLadyFenring.png"; }
    },
    PRINCESS_IRULAN {
      @Override
      public String toString() { return "images/factionImages/leaderTokenBeneGesseritPrincessIrulan.png"; }
    },
    REVEREND_MOTHER_RAMALLO {
      @Override
      public String toString() { return "images/factionImages/leaderTokenBeneGesseritReverendMotherRamallo.png"; }
    },
    WANNA_MARCUS {
      @Override
      public String toString() { return "images/factionImages/leaderTokenBeneGesseritWannaMarcus.png"; }
    },
    BASHAR {
      @Override
      public String toString() { return "images/factionImages/leaderTokenEmperorBashar.png"; }
    },
    BURSEG {
      @Override
      public String toString() { return "images/factionImages/leaderTokenEmperorBurseg.png"; }
    },
    CAID {
      @Override
      public String toString() { return "images/factionImages/leaderTokenEmperorCaid.png"; }
    },
    CAPTAIN_ARAMSHAM {
      @Override
      public String toString() { return "images/factionImages/leaderTokenEmperorCaptainAramsham.png"; }
    },
    COUNT_HASIMIR_FENRING {
      @Override
      public String toString() { return "images/factionImages/leaderTokenEmperorCountHasimirFenring.png"; }
    },
    CHANI {
      @Override
      public String toString() { return "images/factionImages/leaderTokenFremenChani.png"; }
    },
    JAMIS {
      @Override
      public String toString() { return "images/factionImages/leaderTokenFremenJamis.png"; }
    },
    OTHEYM {
      @Override
      public String toString() { return "images/factionImages/leaderTokenFremenOtheym.png"; }
    },
    SHADOUT_MAPES {
      @Override
      public String toString() { return "images/factionImages/leaderTokenFremenShadoutMapes.png"; }
    },
    STILGAR {
      @Override
      public String toString() { return "images/factionImages/leaderTokenFremenStilgar.png"; }
    },
    ESMAR_TUEK {
      @Override
      public String toString() { return "images/factionImages/leaderTokenGuildEsmarTuek.png"; }
    },
    MASTER_BEWT {
      @Override
      public String toString() { return "images/factionImages/leaderTokenGuildMasterBewt.png"; }
    },
    GUILD_REPRESENTATIVE {
      @Override
      public String toString() { return "images/factionImages/leaderTokenGuildRepresentative.png"; }
    },
    SOOSOO_SOOK{
      @Override
      public String toString() { return "images/factionImages/leaderTokenGuildSooSooSook.png"; }
    },
    STABAN_TUEK {
      @Override
      public String toString() { return "images/factionImages/leaderTokenGuildStabanTuek.png"; }
    },
    BEAST_RABBAN {
      @Override
      public String toString() { return "images/factionImages/leaderTokenHarkonnenBeastRabban.png"; }
    },
    CAPTAIN_IAKIN_NEFUD {
      @Override
      public String toString() { return "images/factionImages/leaderTokenHarkonnenCaptainIakinNefud.png"; }
    },
    FEYD_RAUTHA {
      @Override
      public String toString() { return "images/factionImages/leaderTokenHarkonnenFeydRautha.png"; }
    },
    PETER_DE_VRIES {
      @Override
      public String toString() { return "images/factionImages/leaderTokenHarkonnenPiterDeVries.png"; }
    },
    UMMAN_KUDU {
      @Override
      public String toString() { return "images/factionImages/leaderTokenHarkonnenUmmanKudu.png"; }
    }
  }

  public enum troopImages {
      ATREIDES{
        @Override
        public String toString() { return "images/factionImages/troopTokenAtreides.png"; }
      },
      BENE_GESSERIT{
        @Override
        public String toString() { return "images/factionImages/troopTokenBeneGesserit.png"; }
      },
      EMPEROR {
        @Override
        public String toString() { return "images/factionImages/troopTokenEmperor.png"; }
      },
      EMPEROR_ADVANCED {
        @Override
        public String toString() { return "images/factionImages/troopTokenEmperorWithStar.png"; }
      },
      FREMEN {
        @Override
        public String toString() { return "images/factionImages/troopTokenFremen.png"; }
      },
      FREMEN_ADVANCED {
        @Override
        public String toString() { return "images/factionImages/troopTokenFremenWithStar.png"; }
      },
      HARKONNEN {
        @Override
        public String toString() { return "images/factionImages/troopTokenHarkonnen.png"; }
      },
      SPACE_GUILD{
        @Override
        public String toString() { return "images/factionImages/troopTokenGuild.png"; }
      }
    }

  public enum characterImages {
    ATREIDES{
      @Override
      public String toString() { return "images/factionImages/characterAtreides.png"; }
    },
    BENE_GESSERIT{
      @Override
      public String toString() { return "images/factionImages/characterBeneGesserit.png"; }
    },
    EMPEROR {
      @Override
      public String toString() { return "images/factionImages/characterEmperor.png"; }
    },
    FREMEN {
      @Override
      public String toString() { return "images/factionImages/characterFremen.png"; }
    },
    HARKONNEN {
      @Override
      public String toString() { return "images/factionImages/characterHarkonnen.png"; }
    },
    SPACE_GUILD{
      @Override
      public String toString() { return "images/factionImages/characterGuild.png"; }
    }
  }


  public enum factions {
    HARKONNEN {
      @Override
      public String toString() { return "Harkonnen"; }
    },
    ATREIDES{
      @Override
      public String toString() { return "Atreides"; }
    },
    BENE_GESSERIT{
      @Override
      public String toString() { return "Bene Gesserit"; }
    },
    FREMEN{
      @Override
      public String toString() { return "Fremen"; }
    },
    SPACE_GUILD{
      @Override
      public String toString() { return "The Guild"; }

    },
    EMPEROR{
      @Override
      public String toString() { return "Emperor"; }
    };

  }

  public enum cardTypes {
    SPICE {
      @Override
      public String toString() {
        return "Spice";
      }
    },
    TREACHERY{
      @Override
      public String toString() {
        return "Treachery";
      }
    },
    JUNK {
      @Override
      public String toString() {
        return "Junk";
      }
    };
  }

  public enum difficulties {
    EASY {
      @Override
      public String toString() { return "Easy"; }
    },

    MEDIUM {
      @Override
      public String toString() { return "Medium"; }
    },

    HARD {
      @Override
      public String toString() { return "Hard"; }
    }
  }

  public enum cardSubTypes {
    WEAPON {
      @Override
      public String toString() {
        return "Weapon";
      }
    }, DEFENSE {
      @Override
      public String toString() {
        return "Defense";
      }
    }, SPECIAL {
      @Override
      public String toString() {
        return "Special";
      }
    }
  }

  public enum cardClassifications {
    PROJECTILE, POISON
  }

  public enum rounds {
    SETUP, STORM, SPICE_BLOW, NEXUS, BIDDING, REVIVAL_AND_MOVEMENT, BATTLE, COLLECTION;
  }
}
