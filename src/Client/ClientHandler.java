package Client;
import java.io.IOException;
import java.net.Socket;

import org.json.JSONObject;

import Controller.ServerController;
import Partie.Lobby;
import Ressources.DetailsClient;
import Ressources.RequetesJSON;

public class ClientHandler {

    public static int idCpt = 0;
    private int idClient;
    private ServerController serverController;
    private Lobby lobby;
    private ClientListener listener;
    private ClientIssuer issuer;
    private Socket so;
    String username;

    public ClientHandler(Socket so, ServerController sc) {
        this.idClient = ClientHandler.idCpt++;
        this.serverController = sc;
        this.lobby = null;
        this.so = so;
        this.listener = new ClientListener(so, this);
        this.issuer = new ClientIssuer(so, this);
        System.out.println("New client: " + idClient + " created");
    }

    //--- Getters et setters ---
    public int getID() {
        return this.idClient;
    }
    
    public String getUsername() {
        return this.username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public void setLobby(Lobby lobby) {
        this.lobby = lobby;
    }

    public DetailsClient getDetailsClient() {
        return new DetailsClient(this.idClient, this.username);
    }

    // --- Gestion de la connection ---
    public void ouvrirConnection() throws IOException {
        try {
            this.listener.ouvrirConnection();
            this.issuer.ouvrirConnection();
            this.listener.start();
            this.issuer.start();
            System.out.println("Connection with client: " + idClient + " opened successfully");
        }
        catch (IOException e) {
            System.out.println("Error opening connection for client: " + idClient);
            e.printStackTrace();
            throw e;
        }
    }

    public void fermerConnection() {
        this.listener.interrupt();
        this.issuer.interrupt();
        this.serverController.removeClient(this.idClient);
        if(this.lobby != null) {//Évite le crash si le client se déconnecte avant d'avoir rejoint un lobby
            this.lobby.disconnectClient(this.idClient);
        }
        System.out.println("Connection with client: " + idClient + " closed");

        try {
            if(this.listener != null) this.listener.fermerReader();
            if(this.issuer != null) this.issuer.fermerWriter();
            if(so != null && !so.isClosed()) {
                so.close(); //En dernier car lèvent sistématiquement une IOException
            }
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    // --- Gestion des requêtes ---
    public void gestionReception(String action, JSONObject objReq) {
        switch (action) {
            case (RequetesJSON.ASK_AUTHENTIFICATION):
                System.out.println("Client " + this.getID() + " demande d'authentification");
                this.demanderAuthentification(objReq);
                break;
            case (RequetesJSON.ASK_LISTE_LOBBIES):
                System.out.println("Client " + this.getID() + " demande la liste des lobbies");
                this.listerLobbies();
                break;
            case(RequetesJSON.ASK_DEMANDE_PARTIE):
                System.out.println("Client " + this.getID() + " demande une partie");
                this.demanderMatch(objReq);
                break;
            case(RequetesJSON.ASK_LANCEMENT_PARTIE):
                System.out.println("Client " + this.getID() + " demande le lancement de la partie");
                this.demanderLancementPartie(objReq);
                break;
            case ("envoyerAction"):
                break;
            default:
                break;
        }
    }

    public void demanderAuthentification(JSONObject objReq) {
        String username = objReq.getString(RequetesJSON.Attributs.Authentification.USERNAME);
        String password = objReq.getString(RequetesJSON.Attributs.Authentification.PASSWORD);
        boolean authResult = this.serverController.demandeAuthentification(username, password);
        System.out.println("Authentication result for client " + idClient + ": " + authResult);
        JSONObject objResp = new JSONObject();
        objResp.put(RequetesJSON.Attributs.ACTION, RequetesJSON.RES_AUTHENTIFICATION);
        objResp.put(RequetesJSON.Attributs.Authentification.USERNAME, username);
        objResp.put(RequetesJSON.Attributs.Authentification.ID_CLIENT, idClient);
        objResp.put(RequetesJSON.Attributs.Authentification.RESULTAT, authResult);
        if(authResult) {
            this.setUsername(username);
        }
        this.issuer.envoyerRequete(objResp.toString());
    }

    public void listerLobbies() {
        JSONObject objResp = this.serverController.listerLobbies();
        this.issuer.envoyerRequete(objResp.toString());
    }

    public void demanderMatch(JSONObject objReq) {
        JSONObject objResp = null;
        try {
            int idLobby = objReq.getInt(RequetesJSON.Attributs.Lobby.ID_LOBBY);
            Lobby lobby = this.serverController.demandeDeMatch(this, idLobby);
            System.out.println("Client: " + idClient + " connected to match: " + lobby.getID());
            this.setLobby(lobby);
            
            objResp = lobby.getDetailsLobby().toJSON();
            objResp.put(RequetesJSON.Attributs.ACTION, RequetesJSON.RES_DEMANDE_PARTIE);

        }catch (Exception e) {
            System.out.println("No match found for client: " + idClient);
            e.printStackTrace();
            objResp = new JSONObject();
            objResp.put(RequetesJSON.Attributs.ACTION, RequetesJSON.RES_DEMANDE_PARTIE);
            objResp.put(RequetesJSON.Attributs.Lobby.ID_LOBBY, -1);
        }
        this.issuer.envoyerRequete(objResp.toString());
    }

    public void majLobby(JSONObject detailsLobby){
        detailsLobby.put(RequetesJSON.Attributs.ACTION, RequetesJSON.MAJ_LOBBY);
        this.issuer.envoyerRequete(detailsLobby.toString());
    }

    public void demanderLancementPartie(JSONObject objReq) {
        int idLobby = objReq.getInt(RequetesJSON.Attributs.Lobby.ID_LOBBY);
        this.serverController.verifierLancementMatch(idLobby, this.idClient);
    }

    public void debutPartie() {
        JSONObject objResp = new JSONObject();
        objResp.put(RequetesJSON.Attributs.ACTION, RequetesJSON.DEBUT_PARTIE);
        //Etat init du jeu à envoyer au client ?
        this.issuer.envoyerRequete(objResp.toString());
    }

    public void majTour(String etatTour){//pour ne pas perdre de temps, le messages et déjà préparé, il suffit de l'envoyer
        this.issuer.envoyerRequete(etatTour);
    }

    public void finPartie() {
        JSONObject objResp = new JSONObject();
        objResp.put(RequetesJSON.Attributs.ACTION, RequetesJSON.FIN_PARTIE);
        //Etat final du jeu à envoyer au client ?
        this.issuer.envoyerRequete(objResp.toString());
    }
}
