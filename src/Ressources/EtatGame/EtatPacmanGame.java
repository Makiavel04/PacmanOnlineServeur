package Ressources.EtatGame;

import org.json.JSONObject;

import Ressources.RequetesJSON;

public class EtatPacmanGame extends EtatGame {
    private int scorePacmans;
    private int scoreFantomes;
    private String plateau;
    private int viesPacmans;
    private boolean capsuleActive;


    public EtatPacmanGame(int tour, int scorePacmans, int scoreFantomes, String plateau, int viesPacmans, boolean capsuleActive) {
        super(tour);
        this.scorePacmans = scorePacmans;
        this.scoreFantomes = scoreFantomes;
        this.plateau = plateau;
        this.viesPacmans = viesPacmans;
        this.capsuleActive = capsuleActive;
    }

    public int getScorePacmans() {return scorePacmans;}
    public int getScoreFantomes() {return scoreFantomes;}
    public String getPlateau() {return plateau;}
    public int getViesPacmans() {return viesPacmans;}
    public boolean isCapsuleActive() {return capsuleActive;}

    public static EtatPacmanGame fromJSON(JSONObject json) {
        try {
            int tour = json.getInt(RequetesJSON.Attributs.Partie.TOUR);
            int scorePacmans = json.getInt(RequetesJSON.Attributs.Partie.SCORE_PACMANS);
            int scoreFantomes = json.getInt(RequetesJSON.Attributs.Partie.SCORE_FANTOMES);
            String plateau = json.getString(RequetesJSON.Attributs.Partie.PLATEAU);
            int viesPacmans = json.getInt(RequetesJSON.Attributs.Partie.VIES_PACMANS);
            boolean capsuleActive = json.getBoolean(RequetesJSON.Attributs.Partie.CAPSULE_ACTIVE);
            return new EtatPacmanGame(tour, scorePacmans, scoreFantomes, plateau, viesPacmans, capsuleActive);
        } catch (Exception e) {
            System.out.println("Error parsing JSON to EtatPacmanGame: " + e.getMessage());
            return null;
        }
    }
    
    @Override
    public JSONObject toJSON() {
        JSONObject json = super.toJSON();
        json.put(RequetesJSON.Attributs.Partie.SCORE_PACMANS, this.scorePacmans);
        json.put(RequetesJSON.Attributs.Partie.SCORE_FANTOMES, this.scoreFantomes);
        json.put(RequetesJSON.Attributs.Partie.PLATEAU, this.plateau);
        json.put(RequetesJSON.Attributs.Partie.VIES_PACMANS, this.viesPacmans);
        json.put(RequetesJSON.Attributs.Partie.CAPSULE_ACTIVE, this.capsuleActive);
        return json;
    }

}
