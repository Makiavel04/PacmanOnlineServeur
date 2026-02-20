package Ressources;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

public class DetailsLobby {

    private int idLobby;
    private int nbJoueur;
    private int nbMaxJoueur;
    private int idHost;
    private ArrayList<DetailsClient> joueurs;

    public DetailsLobby(int idLobby, int nbJoueur, int nbMaxJoueur, int idHost, ArrayList<DetailsClient> joueurs) {
        this.idLobby = idLobby;
        this.nbJoueur = nbJoueur;
        this.nbMaxJoueur = nbMaxJoueur;
        this.idHost = idHost;
        this.joueurs = joueurs;
    }

    public int getIdLobby() {return idLobby;}
    public int getNbJoueur() {return nbJoueur;}
    public int getNbMaxJoueur() {return nbMaxJoueur;}
    public int getIdHost() {return idHost;}
    public ArrayList<DetailsClient> getJoueurs() {return joueurs;}

    public static DetailsLobby fromJSON(JSONObject json){
        if(json == null) {
            System.out.println("Error: JSON object is null");
            return null;
        }

        int idLobby = json.optInt(RequetesJSON.Attributs.Lobby.ID_LOBBY, -1);

        if(idLobby == -1) {
            return new DetailsLobby(-1,0,0,-1,new ArrayList<DetailsClient>());
        }

        try{
            int nbJoueur = json.getInt(RequetesJSON.Attributs.Lobby.NB_JOUEUR);
            int nbMaxJoueur = json.getInt(RequetesJSON.Attributs.Lobby.NB_MAX_JOUEUR);
            int idHost = json.getInt(RequetesJSON.Attributs.Lobby.ID_HOST);
            JSONArray joueursJson = json.getJSONArray(RequetesJSON.Attributs.Lobby.JOUEURS);
            ArrayList<DetailsClient> joueurs = new ArrayList<DetailsClient>();
            for(int i=0; i<joueursJson.length(); i++){
                joueurs.add(DetailsClient.fromJSON(joueursJson.getJSONObject(i)));
            }
            return new DetailsLobby(idLobby, nbJoueur, nbMaxJoueur, idHost, joueurs);
        }catch(Exception e){
            System.out.println("Error parsing JSON to DetailsLobby: " + e.getMessage());
            return new DetailsLobby(-1,0,0,-1,new ArrayList<DetailsClient>());
        }
    }

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put(RequetesJSON.Attributs.Lobby.ID_LOBBY, this.idLobby);
        json.put(RequetesJSON.Attributs.Lobby.NB_JOUEUR, this.nbJoueur);
        json.put(RequetesJSON.Attributs.Lobby.NB_MAX_JOUEUR, this.nbMaxJoueur);
        json.put(RequetesJSON.Attributs.Lobby.ID_HOST, this.idHost);
        JSONArray joueursJson = new JSONArray();
        for(DetailsClient joueur : this.joueurs){
            joueursJson.put(joueur.toJSON());
        }
        json.put(RequetesJSON.Attributs.Lobby.JOUEURS, joueursJson);
        return json;
    }
}
