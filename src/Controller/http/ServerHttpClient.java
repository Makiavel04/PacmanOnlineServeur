package Controller.http;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import pacman.online.commun.dto.RequetesJSON;

public class ServerHttpClient {
    private static final String ATTR_ID_PARTIE = "idPartie";
    private static final String ATTR_ERREUR = "erreur";

    private static final String ERR_PSEUDO = "pseudo";
    private static final String ERR_MDP = "motdepasse";

    public static final String ERR_VAINQUEUR= "vainqueur";
    public static final String ERR_SCORE_PACMAN = "scorePacman";
    public static final String ERR_SCORE_FANTOMES = "scoreFantomes";
    public static final String ERR_PACMANS = "pacmans";
    public static final String ERR_FANTOMES = "fantomes";

    private static final String ERR_DAO = "dao";


    private final HttpClient httpClient;
    private String uri_api;

    public ServerHttpClient(String uri){
        this.httpClient = HttpClient.newBuilder().build();
        this.uri_api = uri;
    }


    public AuthResult demandeConnexion(String username, String password){
        JSONObject json = new JSONObject();
        json.put(RequetesJSON.Attributs.AuthentificationAttr.PSEUDO, username);
        json.put(RequetesJSON.Attributs.AuthentificationAttr.MOT_DE_PASSE, password);

        HttpRequest requete = HttpRequest.newBuilder().uri(URI.create(this.uri_api + "/connexion"))
                .header("Content-Type", "application/json")
                .POST(BodyPublishers.ofString(json.toString()))
                .build();

        try{
            HttpResponse<String> response = this.httpClient.send(requete, BodyHandlers.ofString());
            if(response.statusCode() == 200){
                JSONObject jsonResponse = new JSONObject(response.body());
                boolean result = jsonResponse.getBoolean(RequetesJSON.Attributs.AuthentificationAttr.RESULTAT);
                if(result){
                    int id = jsonResponse.getInt(RequetesJSON.Attributs.AuthentificationAttr.ID_JOUEUR);
                    String pseudo = jsonResponse.getString(RequetesJSON.Attributs.AuthentificationAttr.PSEUDO);
                    String couleur = "#FFFFFF";
                    if(jsonResponse.has(RequetesJSON.Attributs.AuthentificationAttr.COULEUR)) couleur = jsonResponse.getString(RequetesJSON.Attributs.AuthentificationAttr.COULEUR);
                    AuthResult authResult = new AuthResult(id, result, pseudo, couleur, "");
                    return authResult;
                }else {
                    JSONObject jsonErreurs = jsonResponse.getJSONObject(ATTR_ERREUR);
                    String erreur = "";
                    if(jsonErreurs.has(ERR_PSEUDO)) erreur += jsonErreurs.getString(ERR_PSEUDO);
                    if(jsonErreurs.has(ERR_MDP)) erreur += jsonErreurs.getString(ERR_MDP);
                    if(jsonErreurs.has(ERR_DAO)) erreur += jsonErreurs.getString(ERR_DAO);
                    return new AuthResult(-1, false, username,"#FFFFFF", erreur);
                }
                
            }else{
                throw new Exception("Erreur lors de l'authentification, code de réponse : " + response.statusCode());
            }
        }catch(Exception e){
            System.out.println("Erreur lors de la requête : " + e.getMessage());
        }
        return new AuthResult(-1, false, username,"#FFFFFF", "Erreur lors du contact avec le serveur."); 
    }

    public void envoiScore(PartieResult resultat){
        JSONObject json = new JSONObject();
        json.put(RequetesJSON.Attributs.BilanPartieAttr.SCORE_PACMAN, resultat.scorePacmans);
        json.put(RequetesJSON.Attributs.BilanPartieAttr.SCORE_FANTOME, resultat.scoreFantomes);
        json.put(RequetesJSON.Attributs.BilanPartieAttr.VAINQUEUR, resultat.vainqueur);
        JSONArray jsonPacmans = new JSONArray(resultat.idPacmans);
        JSONArray jsonFantomes = new JSONArray(resultat.idFantomes);
        json.put(RequetesJSON.Attributs.BilanPartieAttr.PACMANS, jsonPacmans);
        json.put(RequetesJSON.Attributs.BilanPartieAttr.FANTOMES, jsonFantomes);

        HttpRequest requete = HttpRequest.newBuilder().uri(URI.create(this.uri_api + "/resultat_partie"))
                .header("Content-Type", "application/json")
                .POST(BodyPublishers.ofString(json.toString()))
                .build();

        try{
            this.httpClient.sendAsync(requete, BodyHandlers.ofString()).thenAccept(response -> {
                if(response.statusCode() == 200){
                    JSONObject jsonResponse = new JSONObject(response.body());
                    Integer idPartie = jsonResponse.getInt(ATTR_ID_PARTIE);
                    System.out.println("Envoie des résultats réussis pour la partie : " + idPartie);
                }else if(response.statusCode() == 400){
                    JSONObject jsonResponse = new JSONObject(response.body());
                    JSONObject jsonErreurs = jsonResponse.getJSONObject(ATTR_ERREUR);
                    System.out.println("Erreur lors de l'envoie des résultats :");
                    if(jsonErreurs.has(ERR_DAO)) System.err.println("\t" + ERR_DAO + ":" + jsonErreurs.getString(ERR_DAO));
                    if(jsonErreurs.has(ERR_DAO)) System.err.println("\t" + ERR_VAINQUEUR + ":" + jsonErreurs.getString(ERR_VAINQUEUR));
                    if(jsonErreurs.has(ERR_DAO)) System.err.println("\t" + ERR_SCORE_PACMAN + ":" + jsonErreurs.getString(ERR_SCORE_PACMAN));
                    if(jsonErreurs.has(ERR_DAO)) System.err.println("\t" + ERR_SCORE_FANTOMES + ":" + jsonErreurs.getString(ERR_SCORE_FANTOMES));
                    if(jsonErreurs.has(ERR_DAO)) System.err.println("\t" + ERR_PACMANS + ":" + jsonErreurs.getString(ERR_PACMANS));
                    if(jsonErreurs.has(ERR_DAO)) System.err.println("\t" + ERR_FANTOMES + ":" + jsonErreurs.getString(ERR_FANTOMES));
                }else {
                    System.out.println("Erreur lors de inconnue lors de l'envoie des résultats : " + response.statusCode());
                }
            });
        }catch(Exception e){
             System.out.println("Erreur lors de l'envoi du score : " + e.getMessage());
        }
    }
}
