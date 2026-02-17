package Client;
import java.io.IOException;
import java.net.Socket;

import org.json.JSONObject;

import Controller.ServerController;
import Match.MatchThread;

public class ClientHandler {

    public static int idCpt = 0;
    private int idClient;
    private ServerController serverController;
    private ClientListener listener;
    private ClientIssuer issuer;
    private Socket so;

    public ClientHandler(Socket so, ServerController sc) {
        this.idClient = ClientHandler.idCpt++;
        this.serverController = sc;
        this.so = so;
        this.listener = new ClientListener(so, this);
        this.issuer = new ClientIssuer(so, this);
        System.out.println("New client: " + idClient + " created");
    }

    public int getID() {
        return this.idClient;
    }

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
        try {
            so.close(); 
            System.out.println("Connection with client: " + idClient + " closed");

        } 
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void gestionReception(String action, org.json.JSONObject objReq) {
        switch (action) {
            case ("demanderAuthentification"):
                System.out.println("Client " + this.getID() + " demande d'authentification");
                this.demanderAuthentification(objReq);
                break;
            case("demanderPartie"):
                System.out.println("Client " + this.getID() + " demande une partie");
                this.demanderMatch();
                break;
            case ("envoyerAction"):
                break;
            default:
                break;
        }
    }

    public void demanderAuthentification(JSONObject objReq) {
        String username = objReq.getString("username");
        String password = objReq.getString("password");
        boolean authResult = this.serverController.demandeAuthentification(username, password);
        System.out.println("Authentication result for client " + idClient + ": " + authResult);
        JSONObject objResp = new JSONObject();
        objResp.put("action", "reponseAuthentification");
        objResp.put("reponse", authResult);
        this.issuer.envoyerRequete(objResp.toString());
    }

    public void demanderMatch() {
        JSONObject objResp = new JSONObject();
        objResp.put("action", "reponseDemandePartie");
        try {
            MatchThread match = this.serverController.demandeDeMatch(this);
            objResp.put("idMatch", match.getID());
            System.out.println("Client: " + idClient + " connected to match: " + match.getID());

        }catch (Exception e) {
            System.out.println("No match found for client: " + idClient);
            e.printStackTrace();
            objResp.put("idMatch", -1);
        }
        this.issuer.envoyerRequete(objResp.toString());

        this.serverController.verifierLancementMatch(objResp.getInt("idMatch"));
    }

    public void debutPartie() {
        JSONObject objResp = new JSONObject();
        objResp.put("action", "debutPartie");
        //Etat init du jeu à envoyer au client ?
        this.issuer.envoyerRequete(objResp.toString());
    }

    public void majTour(String etatTour){//pour ne pas perdre de temps, le messages et déjà préparé, il suffit de l'envoyer
        this.issuer.envoyerRequete(etatTour);
    }

    public void finPartie() {
        JSONObject objResp = new JSONObject();
        objResp.put("action", "finDePartie");
        //Etat final du jeu à envoyer au client ?
        this.issuer.envoyerRequete(objResp.toString());
    }
}
