package Client;
import java.io.IOException;
import java.net.Socket;

import org.json.JSONArray;
import org.json.JSONObject;

import Controller.ServerController;
import Partie.Joueur;
import Partie.Lobby;
import Partie.Pacman.Agents.Agent;
import Partie.Pacman.Agents.TypeAgent;
import Partie.Pacman.Agents.Strategies.Strategie;
import Partie.Pacman.Agents.Strategies.TypeStrategie;
import Ressources.RequetesJSON;
import Ressources.EtatGame.EtatPacmanGame;
import Ressources.EtatLobby.DetailsJoueur;

public class ClientHandler implements Joueur{

    public static int idCpt = 0;
    private int idClient;
    private ServerController serverController;
    private Lobby lobby;
    private ClientListener listener;
    private ClientIssuer issuer;
    private Socket so;
    private String username;

    private TypeAgent typeAgent;
    private Agent agent;

    public ClientHandler(Socket so, ServerController sc) {
        this.idClient = ++ClientHandler.idCpt;
        this.serverController = sc;
        this.lobby = null;
        this.so = so;
        this.listener = new ClientListener(so, this);
        this.issuer = new ClientIssuer(so, this);
        System.out.println("New client#" + idClient + " created");
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

    public boolean isBot() {
        return false;
    }

    public DetailsJoueur getDetailsJoueur() {
        return new DetailsJoueur(this.idClient, this.username, this.typeAgent, this.isBot(), this.getTypeStrategie().name());
    }

    // --- Gestion de la connection ---
    public void ouvrirConnection() throws IOException {
        try {
            this.listener.ouvrirConnection();
            this.issuer.ouvrirConnection();
            this.listener.start();
            this.issuer.start();
            System.out.println("Connection with client#" + idClient + " opened successfully");
        }
        catch (IOException e) {
            System.out.println("Error opening connection for client#" + idClient);
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
        System.out.println("Connection with client#" + idClient + " closed");

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
                System.out.println("Client#" + this.getID() + " demande d'authentification");
                this.demanderAuthentification(objReq);
                break;
            case (RequetesJSON.ASK_LISTE_LOBBIES):
                System.out.println("Client#" + this.getID() + " demande la liste des lobbies");
                this.listerLobbies();
                break;
            case(RequetesJSON.ASK_DEMANDE_PARTIE):
                System.out.println("Client#" + this.getID() + " demande une partie");
                this.demanderMatch(objReq);
                break;
            case(RequetesJSON.ASK_LANCEMENT_PARTIE):
                System.out.println("Client#" + this.getID() + " demande le lancement de la partie");
                this.demanderLancementPartie(objReq);
                break;
            case ("envoyerAction"):
                break;
            case (RequetesJSON.ASK_AJOUT_BOT):
                System.out.println("Client#" + this.getID() + " demande l'ajout d'un bot");
                this.demanderAjoutBot(objReq);
                break;
            case (RequetesJSON.ASK_RETRAIT_BOT):
                System.out.println("Client#" + this.getID() + " demande le retrait d'un bot");
                this.demanderRetraitBot(objReq);
                break;
            case (RequetesJSON.ASK_CHANGEMENT_CAMP):
                System.out.println("Client#" + this.getID() + " demande le changement de camp");
                this.demanderChangementCamp(objReq);
                break;
            case (RequetesJSON.SEND_DEPLACEMENT) :
                System.out.println("Client#" + this.getID() + " envoie une action de déplacement");
                this.traiterDeplacement(objReq);
                break;
            case (RequetesJSON.ASK_CHANGER_STRATEGIE_BOT):
                System.out.println("Client#" + this.getID() + " demande le changement de stratégie d'un bot");
                this.demanderChangementStrategieBot(objReq);
                break;
            case (RequetesJSON.ASK_CHANGEMENT_MAP):
                System.out.println("Client#" + this.getID() + " demande le changement de map");
                this.demanderChangementMap(objReq);
                break;
            default:
                break;
        }
    }

    public void demanderAuthentification(JSONObject objReq) {
        String username = objReq.getString(RequetesJSON.Attributs.Authentification.USERNAME);
        String password = objReq.getString(RequetesJSON.Attributs.Authentification.PASSWORD);
        boolean authResult = this.serverController.demandeAuthentification(username, password);
        System.out.println("Resultat d'authentification pour le client#" + idClient + " : " + authResult);
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
            System.out.println("Client#" + idClient + " connecté au lobby#" + lobby.getID());
            this.setLobby(lobby);
            
            objResp = lobby.getDetailsLobby().toJSON();
            objResp.put(RequetesJSON.Attributs.ACTION, RequetesJSON.RES_DEMANDE_PARTIE);
            objResp.put(RequetesJSON.Attributs.Lobby.STRATS_PACMAN, TypeStrategie.getNomStrategieCompatibles(TypeAgent.PACMAN));
            objResp.put(RequetesJSON.Attributs.Lobby.STRATS_FANTOME, TypeStrategie.getNomStrategieCompatibles(TypeAgent.FANTOME));
            objResp.put(RequetesJSON.Attributs.Lobby.LISTE_MAPS_DISPONIBLES, lobby.getListeMaps());

        }catch (Exception e) {
            System.out.println("Pas de match trouvé pour le client#" + idClient);
            e.printStackTrace();
            objResp = new JSONObject();
            objResp.put(RequetesJSON.Attributs.ACTION, RequetesJSON.RES_DEMANDE_PARTIE);
            objResp.put(RequetesJSON.Attributs.Lobby.ID_LOBBY, -1);
            objResp.put(RequetesJSON.Attributs.Lobby.STRATS_PACMAN, new JSONArray());
            objResp.put(RequetesJSON.Attributs.Lobby.STRATS_FANTOME, new JSONArray());
            objResp.put(RequetesJSON.Attributs.Lobby.LISTE_MAPS_DISPONIBLES, new JSONArray());
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

    public void debutPartie(EtatPacmanGame etatinit) {
        JSONObject objResp = etatinit.toJSON();
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

    public void demanderAjoutBot(JSONObject objReq){
        String type = objReq.getString(RequetesJSON.Attributs.Joueur.TYPE_AGENT);
        TypeAgent typeAgent = TypeAgent.valueOf(type);
        if(this.lobby != null) {
            this.lobby.ajouterBot(typeAgent, this.idClient);
        }
    }
    public void demanderRetraitBot(JSONObject objReq){
        String type = objReq.getString(RequetesJSON.Attributs.Joueur.TYPE_AGENT);
        TypeAgent typeAgent = TypeAgent.valueOf(type);
        if(this.lobby != null) {
            this.lobby.retirerBot(typeAgent, this.idClient);
        }
    }

    public void demanderChangementStrategieBot(JSONObject objReq) {
        TypeStrategie typeStrategie = TypeStrategie.valueOf(objReq.getString(RequetesJSON.Attributs.Lobby.TYPE_STRATEGIE));
        int numBot = objReq.getInt(RequetesJSON.Attributs.Lobby.NUM_BOT);
        this.lobby.setStrategieBot(this.idClient, numBot, typeStrategie);
    }

    public void demanderChangementCamp(JSONObject objReq) {
        this.lobby.changerCamp(this);
    }

    public void traiterDeplacement(JSONObject objReq) {
        int direction = objReq.getInt(RequetesJSON.Attributs.Partie.SENS_MOUVEMENT);
        if(this.agent != null){
            this.agent.getStrategie().onKeyPressed(direction);
        }
    }

    public void demanderChangementMap(JSONObject objReq) {
        String nomMap = objReq.getString(RequetesJSON.Attributs.Lobby.MAP);
        this.lobby.changerMap(this, nomMap);
    }

    public void autorisationChangementMap(boolean autorise, int nbPacmanMax, int nbFantomeMax) {
        JSONObject objResp = new JSONObject();
        objResp.put(RequetesJSON.Attributs.ACTION, RequetesJSON.RES_CHANGEMENT_MAP);
        objResp.put(RequetesJSON.Attributs.Lobby.AUTORISE_CHANGEMENT, autorise);
        objResp.put(RequetesJSON.Attributs.Lobby.NB_MAX_PACMAN, nbPacmanMax);
        objResp.put(RequetesJSON.Attributs.Lobby.NB_MAX_FANTOME, nbFantomeMax);
        this.issuer.envoyerRequete(objResp.toString());
    }

    // --- Joueur ---
    @Override
    public void initJoueur(Agent agent) {
        this.agent = agent;
    }

    public void setTypeAgent(TypeAgent type) {
        this.typeAgent = type;
    }
    @Override
    public TypeAgent getTypeAgent() {
        return this.typeAgent;
    }

    @Override
    public TypeStrategie getTypeStrategie() {
        return TypeStrategie.CONTROLEE1; //Pour le client la stratégie est toujours contrôlée, c'est le client qui décide de l'action à faire
    }

    @Override
    public Strategie getStrategie() {
        if(this.agent != null) {
            return this.agent.getStrategie();
        }else {
            return null;
        }
    }
     
}
