package Ressources.EtatLobby;

import org.json.JSONObject;

import Partie.Pacman.Agents.TypeAgent;
import Ressources.RequetesJSON;
import Ressources.TransformableJSON;

public class ScoreFinPartie implements TransformableJSON{
    private TypeAgent vainqueur;
    private int scoreFantome;
    private int scorePacman;

    public ScoreFinPartie(TypeAgent vainqueur, int scoreFantome, int scorePacman) {
        this.vainqueur = vainqueur;
        this.scoreFantome = scoreFantome;
        this.scorePacman = scorePacman;
    }

    public TypeAgent getVainqueur() {return vainqueur;}
    public int getScoreFantome() {return scoreFantome;}
    public int getScorePacman() {return scorePacman;}

    public static ScoreFinPartie fromJSON(JSONObject json) {
        TypeAgent vainqueur = TypeAgent.valueOf(json.getString(RequetesJSON.Attributs.ScoreFinPartie.VAINQUEUR));
        int scoreFantome = json.getInt(RequetesJSON.Attributs.ScoreFinPartie.SCORE_FANTOME);
        int scorePacman = json.getInt(RequetesJSON.Attributs.ScoreFinPartie.SCORE_PACMAN);
        return new ScoreFinPartie(vainqueur, scoreFantome, scorePacman);
    }

    @Override
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put(RequetesJSON.Attributs.ScoreFinPartie.VAINQUEUR, vainqueur.name());
        json.put(RequetesJSON.Attributs.ScoreFinPartie.SCORE_FANTOME, scoreFantome);
        json.put(RequetesJSON.Attributs.ScoreFinPartie.SCORE_PACMAN, scorePacman);
        return json;
    }
}