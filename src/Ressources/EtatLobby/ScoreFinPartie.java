package Ressources.EtatLobby;

import org.json.JSONObject;

import Partie.Pacman.Agents.TypeAgent;
import Ressources.RequetesJSON;
import Ressources.TransformableJSON;
/** Classe représentant le score à la fin d'une partie */
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

    /**
     * Crée une instance de ScoreFinPartie à partir d'un objet JSON.œ
     * @param json L'objet JSON contenant les données du score de fin de partie.
     * @return Une instance de ScoreFinPartie ou null en cas d'erreur de parsing.
     */
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