package Ressources.EtatLobby;

import org.json.JSONObject;

import Partie.Pacman.Agents.TypeAgent;
import Ressources.RequetesJSON;
import Ressources.TransformableJSON;

/**
 * Classe représentant les détails d'un joueur.
 */
public class DetailsJoueur implements TransformableJSON {
    private int idClient;
    private String username;
    private TypeAgent typeAgent;
    private boolean isBot;
    private String strategie; 

    public DetailsJoueur(int idClient, String username, TypeAgent typeAgent, boolean isBot, String strategie) {
        this.idClient = idClient;
        this.username = username;
        this.typeAgent = typeAgent;
        this.isBot = isBot;
        this.strategie = strategie;
    }

    public int getIdClient() {return idClient;}
    public String getUsername() {return username;}
    public TypeAgent getTypeAgent() {return typeAgent;}
    public boolean isBot() {return isBot;}
    public String getStrategie() {return strategie;}

    /**
     * Crée une instance de DetailsJoueur à partir d'un objet JSON.
     * @param json L'objet JSON contenant les données du joueur.
     * @return Une instance de DetailsJoueur ou null en cas d'erreur de parsing.
     */
    public static DetailsJoueur fromJSON(JSONObject json){
        try{
            int idClient = json.getInt(RequetesJSON.Attributs.Joueur.ID_CLIENT);
            String username = json.getString(RequetesJSON.Attributs.Joueur.USERNAME);
            TypeAgent typeAgent = TypeAgent.valueOf(json.getString(RequetesJSON.Attributs.Joueur.TYPE_AGENT));
            boolean isBot = json.getBoolean(RequetesJSON.Attributs.Joueur.IS_BOT);
            String strategie = json.getString(RequetesJSON.Attributs.Joueur.STRATEGIE);
            return new DetailsJoueur(idClient, username, typeAgent, isBot, strategie);
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
        json.put(RequetesJSON.Attributs.Joueur.IS_BOT, this.isBot);
        json.put(RequetesJSON.Attributs.Joueur.STRATEGIE, this.strategie);
        return json;
    }
}
