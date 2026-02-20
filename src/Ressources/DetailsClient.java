package Ressources;

import org.json.JSONObject;

public class DetailsClient {
    private int idClient;
    private String username;

    public DetailsClient(int idClient, String username) {
        this.idClient = idClient;
        this.username = username;
    }

    public int getIdClient() {return idClient;}
    public String getUsername() {return username;}

    public static DetailsClient fromJSON(JSONObject json){
        try{
            int idClient = json.getInt(RequetesJSON.Attributs.Client.ID_CLIENT);
            String username = json.getString(RequetesJSON.Attributs.Client.USERNAME);
            return new DetailsClient(idClient, username);
        }catch(Exception e){
            System.out.println("Error parsing JSON to detaisClient: " + e.getMessage());
            return null;
        }
    }

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put(RequetesJSON.Attributs.Client.ID_CLIENT, this.idClient);
        json.put(RequetesJSON.Attributs.Client.USERNAME, this.username);
        return json;
    }
}
