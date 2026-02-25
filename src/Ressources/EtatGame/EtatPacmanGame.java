package Ressources.EtatGame;

import org.json.JSONObject;

import Partie.Pacman.Game.Maze;
import Ressources.RequetesJSON;

/**
 * Classe représentant l'état d'une partie de Pacman à un moment donné.
 */
public class EtatPacmanGame extends EtatGame {
    private int scorePacmans;
    private int scoreFantomes;
    private Maze plateau;
    private int viesPacmans;
    private boolean capsuleActive;


    public EtatPacmanGame(int tour, int scorePacmans, int scoreFantomes, Maze plateau, int viesPacmans, boolean capsuleActive) {
        super(tour);
        this.scorePacmans = scorePacmans;
        this.scoreFantomes = scoreFantomes;
        this.plateau = plateau;
        this.viesPacmans = viesPacmans;
        this.capsuleActive = capsuleActive;
    }

    public int getScorePacmans() {return scorePacmans;}
    public int getScoreFantomes() {return scoreFantomes;}
    public Maze getPlateau() {return plateau;}
    public int getViesPacmans() {return viesPacmans;}
    public boolean isCapsuleActive() {return capsuleActive;}

    /**
     * Crée une instance d'EtatPacmanGame à partir d'un objet JSON.
     * @param json L'objet JSON contenant les données de l'état du jeu.
     * @return Une instance d'EtatPacmanGame ou null en cas d'erreur de parsing.
     */
    public static EtatPacmanGame fromJSON(JSONObject json) {
        try {
            int tour = json.getInt(RequetesJSON.Attributs.Partie.TOUR);
            int scorePacmans = json.getInt(RequetesJSON.Attributs.Partie.SCORE_PACMANS);
            int scoreFantomes = json.getInt(RequetesJSON.Attributs.Partie.SCORE_FANTOMES);
            Maze plateau = Maze.fromJSON(json.getJSONObject(RequetesJSON.Attributs.Partie.PLATEAU));
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
        json.put(RequetesJSON.Attributs.Partie.PLATEAU, this.plateau.toJSON());
        
        

        json.put(RequetesJSON.Attributs.Partie.VIES_PACMANS, this.viesPacmans);
        json.put(RequetesJSON.Attributs.Partie.CAPSULE_ACTIVE, this.capsuleActive);
        return json;
    }

}
