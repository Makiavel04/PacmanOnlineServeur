package Ressources.EtatLobby;

import org.json.JSONObject;

import Ressources.RequetesJSON;
import Ressources.TransformableJSON;

public class ResumeLobby implements TransformableJSON {
    //Attributs
    private int idLobby;
    private int nbJoueur;
    private int nbMaxJoueur;

    public ResumeLobby(int idLobby, int nbJoueur, int nbMaxJoueur) {
        this.idLobby = idLobby;
        this.nbJoueur = nbJoueur;
        this.nbMaxJoueur = nbMaxJoueur;
    }

    public int getIdLobby() {return idLobby;}
    public int getNbJoueur() {return nbJoueur;}
    public int getNbMaxJoueur() {return nbMaxJoueur;}

    public static ResumeLobby fromJSON(JSONObject json){
        try{
            int idLobby = json.getInt(RequetesJSON.Attributs.Lobby.ID_LOBBY);
            int nbJoueur = json.getInt(RequetesJSON.Attributs.Lobby.NB_JOUEUR);
            int nbMaxJoueur = json.getInt(RequetesJSON.Attributs.Lobby.NB_MAX_JOUEUR);
            return new ResumeLobby(idLobby, nbJoueur, nbMaxJoueur);
        }catch(Exception e){
            System.out.println("Error parsing JSON to InfosLobby: " + e.getMessage());
            return null;
        }
    }

    @Override
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put(RequetesJSON.Attributs.Lobby.ID_LOBBY, this.idLobby);
        json.put(RequetesJSON.Attributs.Lobby.NB_JOUEUR, this.nbJoueur);
        json.put(RequetesJSON.Attributs.Lobby.NB_MAX_JOUEUR, this.nbMaxJoueur);
        return json;
    }
    
}
