package Ressources.EtatLobby;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import Ressources.RequetesJSON;
import Ressources.TransformableJSON;

public class DetailsLobby implements TransformableJSON {

    private int idLobby;
    private int idHost;

    private int nbJoueur;
    private int nbMaxJoueur;

    private int nbMaxPacman;
    private int nbPacman;
    private int nbMaxFantome;
    private int nbFantome;

    private ArrayList<DetailsJoueur> joueurs;

    private String map;

    public DetailsLobby(int idLobby, int nbJoueur, int nbMaxJoueur, int nbMaxPacman, int nbPacman, int nbMaxFantome, int nbFantome, int idHost, ArrayList<DetailsJoueur> joueurs, String map) {
        this.idLobby = idLobby;
        this.nbJoueur = nbJoueur;
        this.nbMaxJoueur = nbMaxJoueur;
        this.nbMaxPacman = nbMaxPacman;
        this.nbPacman = nbPacman;
        this.nbMaxFantome = nbMaxFantome;
        this.nbFantome = nbFantome;
        this.idHost = idHost;
        this.joueurs = joueurs;
        this.map = map;
    }

    public int getIdLobby() {return idLobby;}
    public int getIdHost() {return idHost;}
    public int getNbJoueur() {return nbJoueur;}
    public int getNbMaxJoueur() {return nbMaxJoueur;}
    public int getNbPacman() {return nbPacman;}
    public int getNbMaxPacman() {return nbMaxPacman;}
    public int getNbFantome() {return nbFantome;}
    public int getNbMaxFantome() {return nbMaxFantome;}
    public ArrayList<DetailsJoueur> getJoueurs() {return joueurs;}
    public String getMap() {return map;}

    public static DetailsLobby fromJSON(JSONObject json){
        if(json == null) {
            System.out.println("Error: JSON object is null");
            return null;
        }

        int idLobby = json.optInt(RequetesJSON.Attributs.Lobby.ID_LOBBY, -1);

        if(idLobby == -1) {
            return new DetailsLobby(-1,0,0,0,0,0,0,-1,new ArrayList<DetailsJoueur>(),"");
        }

        try{
            int nbJoueur = json.getInt(RequetesJSON.Attributs.Lobby.NB_JOUEUR);
            int nbMaxJoueur = json.getInt(RequetesJSON.Attributs.Lobby.NB_MAX_JOUEUR);
            int idHost = json.getInt(RequetesJSON.Attributs.Lobby.ID_HOST);
            JSONArray joueursJson = json.getJSONArray(RequetesJSON.Attributs.Lobby.JOUEURS);
            ArrayList<DetailsJoueur> joueurs = new ArrayList<DetailsJoueur>();
            for(int i=0; i<joueursJson.length(); i++){
                joueurs.add(DetailsJoueur.fromJSON(joueursJson.getJSONObject(i)));
            }
            int nbMaxPacman = json.getInt(RequetesJSON.Attributs.Lobby.NB_MAX_PACMAN);
            int nbPacman = json.getInt(RequetesJSON.Attributs.Lobby.NB_PACMAN);
            int nbMaxFantome = json.getInt(RequetesJSON.Attributs.Lobby.NB_MAX_FANTOME);
            int nbFantome = json.getInt(RequetesJSON.Attributs.Lobby.NB_FANTOME);  
            String map = json.getString(RequetesJSON.Attributs.Lobby.MAP);
            return new DetailsLobby(idLobby, nbJoueur, nbMaxJoueur, nbMaxPacman, nbPacman, nbMaxFantome, nbFantome, idHost, joueurs, map);
        }catch(Exception e){
            System.out.println("Error parsing JSON to DetailsLobby: " + e.getMessage());
            return new DetailsLobby(-1,0,0,0,0,0,0,-1,new ArrayList<DetailsJoueur>(),"");
        }
    }
    
    @Override
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put(RequetesJSON.Attributs.Lobby.ID_LOBBY, this.idLobby);
        json.put(RequetesJSON.Attributs.Lobby.NB_JOUEUR, this.nbJoueur);
        json.put(RequetesJSON.Attributs.Lobby.NB_MAX_JOUEUR, this.nbMaxJoueur);
        json.put(RequetesJSON.Attributs.Lobby.ID_HOST, this.idHost);
        JSONArray joueursJson = new JSONArray();
        for(DetailsJoueur joueur : this.joueurs){
            joueursJson.put(joueur.toJSON());
        }
        json.put(RequetesJSON.Attributs.Lobby.JOUEURS, joueursJson);
        json.put(RequetesJSON.Attributs.Lobby.NB_MAX_PACMAN, this.nbMaxPacman);
        json.put(RequetesJSON.Attributs.Lobby.NB_PACMAN, this.nbPacman);
        json.put(RequetesJSON.Attributs.Lobby.NB_MAX_FANTOME, this.nbMaxFantome);
        json.put(RequetesJSON.Attributs.Lobby.NB_FANTOME, this.nbFantome);  
        json.put(RequetesJSON.Attributs.Lobby.MAP, this.map);
        return json;
    }
}
