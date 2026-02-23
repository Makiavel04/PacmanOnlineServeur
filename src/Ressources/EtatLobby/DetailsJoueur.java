package Ressources.EtatLobby;

import org.json.JSONObject;

import Partie.Pacman.Agents.TypeAgent;
import Ressources.RequetesJSON;
import Ressources.TransformableJSON;

public class DetailsJoueur implements TransformableJSON {
    private int idClient;
    private String username;
    private TypeAgent typeAgent;

    public DetailsJoueur(int idClient, String username, TypeAgent typeAgent) {
        this.idClient = idClient;
        this.username = username;
        this.typeAgent = typeAgent;
    }

    public int getIdClient() {return idClient;}
    public String getUsername() {return username;}
    public TypeAgent getTypeAgent() {return typeAgent;}

    public static DetailsJoueur fromJSON(JSONObject json){
        try{
            int idClient = json.getInt(RequetesJSON.Attributs.Joueur.ID_CLIENT);
            String username = json.getString(RequetesJSON.Attributs.Joueur.USERNAME);
            TypeAgent typeAgent = TypeAgent.valueOf(json.getString(RequetesJSON.Attributs.Joueur.TYPE_AGENT));
            return new DetailsJoueur(idClient, username, typeAgent);
        }catch(Exception e){
            System.out.println("Error parsing JSON to detailsJoueur: " + e.getMessage());
            return null;
        }
    }

    @Override
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put(RequetesJSON.Attributs.Joueur.ID_CLIENT, this.idClient);
        json.put(RequetesJSON.Attributs.Joueur.USERNAME, this.username);
        json.put(RequetesJSON.Attributs.Joueur.TYPE_AGENT, this.typeAgent.toString());
        return json;
    }
}
