package Client;
import java.io.IOException;
import java.net.Socket;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONObject;

import Controller.ServerController;
import Partie.Joueur;
import Partie.Lobby;
import Partie.Pacman.Agents.Agent;
import Partie.Pacman.Agents.Strategies.Strategie;
import Partie.Pacman.Agents.Strategies.TypeStrategie;
import pacman.online.commun.dto.RequetesJSON;
import pacman.online.commun.dto.game.EtatPacmanGame;
import pacman.online.commun.dto.lobby.BilanPartie;
import pacman.online.commun.dto.lobby.DetailsJoueur;
import pacman.online.commun.moteur.TypeAgent;

/**
 * Point de connection et d'échange entre serveur et client.
 */
public class ClientHandler implements Joueur{
    /** Compteur d'identifiants de clients */
    public static int idCpt = 0;
    private int idClient;
    private ServerController serverController;
    private Lobby lobby;
    /** Récepteur pour les messages reçus du client */
    private ClientListener listener;
    /** Émetteur pour les messages envoyés au client */
    private ClientIssuer issuer;
    private Socket so;
    private String username;
    private String couleur;

    /** Type de l'agent joué par le client */
    private TypeAgent typeAgent;
    /** Agent joué par le client */
    private Agent agent;

    public ClientHandler(Socket so, ServerController sc) {
        this.idClient = ++ClientHandler.idCpt;
        this.serverController = sc;
        this.lobby = null;
        this.so = so;
        this.listener = new ClientListener(so, this);
        this.issuer = new ClientIssuer(so, this);
        this.couleur = null;
        System.out.println("New client#" + idClient + " created");
    }

    //--- Getters et setters ---
    public int getId() {
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

    /** Retourne les détails du joueur pour l'affichage chez le client */
    public DetailsJoueur getDetailsJoueur() {
        return new DetailsJoueur(this.idClient, this.username, this.typeAgent, this.isBot(), this.getTypeStrategie().name());
    }

    public void setCouleur(String couleur) {
        this.couleur = couleur;
    }
    public String getCouleur() {
        return this.couleur;
    }

    // --- Gestion de la connection ---
    
    /**
     * Ouvre la connection avec le client en démarrant les threads de réception et d'émission.
     * @throws IOException si une erreur survient lors de l'ouverture de la connection
     */
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

    /**
     * Ferme la connection avec le client en interrompant les threads de réception et d'émission, en retirant le client du serveur et du lobby, et en fermant le socket.
     */
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
    /**
     * Gère la réception d'une requête du client en fonction de son action, en appelant la méthode correspondante pour chaque type de requête.
     * @param action action de la requête reçue du client
     * @param objReq corps de la requête reçue du client sous forme de JSONObject
     */
    public void gestionReception(String action, JSONObject objReq) {
        switch (action) {
            case (RequetesJSON.ASK_AUTHENTIFICATION):
                System.out.println("Client#" + this.getId() + " demande d'authentification");
                this.demanderAuthentification(objReq);
                break;
            case (RequetesJSON.ASK_LISTE_LOBBIES):
                System.out.println("Client#" + this.getId() + " demande la liste des lobbies");
                this.listerLobbies();
                break;
            case(RequetesJSON.ASK_DEMANDE_PARTIE):
                System.out.println("Client#" + this.getId() + " demande une partie");
                this.demanderMatch(objReq);
                break;
            case(RequetesJSON.QUITTER_LOBBY):
                System.out.println("Client#" + this.getId() + " demande à quitter le lobby");
                this.quitterLobby(objReq);
                break;
            case(RequetesJSON.ASK_LANCEMENT_PARTIE):
                System.out.println("Client#" + this.getId() + " demande le lancement de la partie");
                this.demanderLancementPartie(objReq);
                break;
            case ("envoyerAction"):
                break;
            case (RequetesJSON.ASK_AJOUT_BOT):
                System.out.println("Client#" + this.getId() + " demande l'ajout d'un bot");
                this.demanderAjoutBot(objReq);
                break;
            case (RequetesJSON.ASK_RETRAIT_BOT):
                System.out.println("Client#" + this.getId() + " demande le retrait d'un bot");
                this.demanderRetraitBot(objReq);
                break;
            case (RequetesJSON.ASK_CHANGEMENT_CAMP):
                System.out.println("Client#" + this.getId() + " demande le changement de camp");
                this.demanderChangementCamp(objReq);
                break;
            case (RequetesJSON.SEND_DEPLACEMENT) :
                System.out.println("Client#" + this.getId() + " envoie une action de déplacement");
                this.traiterDeplacement(objReq);
                break;
            case (RequetesJSON.ASK_CHANGER_STRATEGIE_BOT):
                System.out.println("Client#" + this.getId() + " demande le changement de stratégie d'un bot");
                this.demanderChangementStrategieBot(objReq);
                break;
            case (RequetesJSON.ASK_CHANGEMENT_MAP):
                System.out.println("Client#" + this.getId() + " demande le changement de map");
                this.demanderChangementMap(objReq);
                break;
            default:
                break;
        }
    }

    /**
     * Relaye la demande d'authentification du client au contrôleur du serveur puis renvoi la réponse.
     * @param objReq corps de la requête
     */
    public void demanderAuthentification(JSONObject objReq) {
        String username = objReq.getString(RequetesJSON.Attributs.AuthentificationAttr.USERNAME);
        String password = objReq.getString(RequetesJSON.Attributs.AuthentificationAttr.PASSWORD);
        boolean authResult = this.serverController.demandeAuthentification(username, password);
        System.out.println("Resultat d'authentification pour le client#" + idClient + " : " + authResult);
        JSONObject objResp = new JSONObject();
        objResp.put(RequetesJSON.Attributs.ACTION, RequetesJSON.RES_AUTHENTIFICATION);
        objResp.put(RequetesJSON.Attributs.AuthentificationAttr.USERNAME, username);
        objResp.put(RequetesJSON.Attributs.AuthentificationAttr.ID_CLIENT, idClient);
        objResp.put(RequetesJSON.Attributs.AuthentificationAttr.RESULTAT, authResult);
        if(authResult) {
            this.setUsername(username);
            this.setCouleur("#09ff00"); //Changer la couleur en foncitn de celle active
        }
        this.issuer.envoyerRequete(objResp.toString());
    }

    /**
     * Envoie la liste des lobbies disponibles au client (en réponse à sa demande)
     */
    public void listerLobbies() {
        Vector<Lobby> lobbies = this.serverController.listerLobbies();
        JSONObject objResp = new JSONObject();
        objResp.put(RequetesJSON.Attributs.ACTION, RequetesJSON.RES_LISTE_LOBBIES);
        JSONArray arrLobbies = new JSONArray();
        for(Lobby lobby : lobbies) {
            arrLobbies.put(lobby.getDetailsLobby().toJSON());
        }
        objResp.put(RequetesJSON.Attributs.LobbyAttr.LISTE_LOBBIES, arrLobbies);
        this.issuer.envoyerRequete(objResp.toString());
    }

    /**
     * Relaye la demande de match du client au contrôleur du serveur puis renvoi la réponse (détails du lobby rejoint ou échec de la demande)
     * @param objReq corps de la requête
     */
    public void demanderMatch(JSONObject objReq) {
        JSONObject objResp = null;
        try {
            int idLobby = objReq.getInt(RequetesJSON.Attributs.LobbyAttr.ID_LOBBY);
            Lobby lobby = this.serverController.demandeDeMatch(this, idLobby);
            System.out.println("Client#" + idClient + " connecté au lobby#" + lobby.getId());
            this.setLobby(lobby);
            
            objResp = lobby.getDetailsLobby().toJSON();
            objResp.put(RequetesJSON.Attributs.ACTION, RequetesJSON.RES_DEMANDE_PARTIE);
            objResp.put(RequetesJSON.Attributs.LobbyAttr.STRATS_PACMAN, TypeStrategie.getNomStrategieCompatibles(TypeAgent.PACMAN));
            objResp.put(RequetesJSON.Attributs.LobbyAttr.STRATS_FANTOME, TypeStrategie.getNomStrategieCompatibles(TypeAgent.FANTOME));
            objResp.put(RequetesJSON.Attributs.LobbyAttr.LISTE_MAPS_DISPONIBLES, lobby.getListeMaps());

        }catch (Exception e) {
            System.out.println("Pas de match trouvé pour le client#" + idClient);
            e.printStackTrace();
            objResp = new JSONObject();
            objResp.put(RequetesJSON.Attributs.ACTION, RequetesJSON.RES_DEMANDE_PARTIE);
            objResp.put(RequetesJSON.Attributs.LobbyAttr.ID_LOBBY, -1);
            objResp.put(RequetesJSON.Attributs.LobbyAttr.STRATS_PACMAN, new JSONArray());
            objResp.put(RequetesJSON.Attributs.LobbyAttr.STRATS_FANTOME, new JSONArray());
            objResp.put(RequetesJSON.Attributs.LobbyAttr.LISTE_MAPS_DISPONIBLES, new JSONArray());
        }
        this.issuer.envoyerRequete(objResp.toString());
    }

    /**
     * Relaye la demande de quitter le lobby au lobby concerné et nettoie le ClientHandler
     * @param objReq corps de la requête
     */
    public void quitterLobby(JSONObject objReq) {
        int idLobby = objReq.getInt(RequetesJSON.Attributs.LobbyAttr.ID_LOBBY);
        if(this.lobby != null && idLobby == this.lobby.getId()) {
            this.lobby.disconnectClient(this.idClient);
            this.setLobby(null);
            this.agent = null; //Efface l'agent pour éviter les soucis si on relance une partie
            this.typeAgent = null; //Efface le type d'agent pour éviter les soucis si on relance une partie
            System.out.println("Client#" + idClient + " a quitté le lobby#" + idLobby);
        }else{
            System.out.println("Client#" + idClient + " a tenté de quitter un lobby dont il ne fait pas partie (Lobby#" + idLobby + ")");
        }
    }

    /**
     * Envoie les nouveaux détails du lobby au client
     * @param detailsLobby détails du lobby à envoyer au client sous forme de JSONObject
     */
    public void majLobby(JSONObject detailsLobby){
        detailsLobby.put(RequetesJSON.Attributs.ACTION, RequetesJSON.MAJ_LOBBY);
        this.issuer.envoyerRequete(detailsLobby.toString());
    }

    /**
     * Relaye la demande de lancement de partie au lobby concerné
     * @param objReq corps de la requête
     */
    public void demanderLancementPartie(JSONObject objReq) {
        int idLobby = objReq.getInt(RequetesJSON.Attributs.LobbyAttr.ID_LOBBY);
        this.serverController.verifierLancementMatch(idLobby, this.idClient);
    }

    /**
     * Envoie l'état initial de la partie au client pour le lancement de la partie
     * @param etatinit état initial de la partie à envoyer au client sous forme d'Objet JSON
     */
    public void debutPartie(EtatPacmanGame etatinit) {
        JSONObject objResp = etatinit.toJSON();
        objResp.put(RequetesJSON.Attributs.ACTION, RequetesJSON.DEBUT_PARTIE);
        //Etat init du jeu à envoyer au client ?
        this.issuer.envoyerRequete(objResp.toString());
    }

    /**
     * Envoie l'état de la partie au client pour la mise à jour de l'affichage chez le client
     * @param etatTour état de la partie 
     */
    public void majTour(String etatTour){//pour ne pas perdre de temps, le messages et déjà préparé, il suffit de l'envoyer
        this.issuer.envoyerRequete(etatTour);
    }

    /**
     * Signal la fin de partie au client et envoie le score
     * @param score résultat de la partie
     */
    public void finPartie(BilanPartie score) {
        JSONObject objResp = score.toJSON();
        objResp.put(RequetesJSON.Attributs.ACTION, RequetesJSON.FIN_PARTIE);
        //Etat final du jeu à envoyer au client ?
        this.issuer.envoyerRequete(objResp.toString());
        this.agent = null; //Efface l'agent pour éviter les soucis si on relance
    }

    /**
     * Relaye la demande d'ajout de bot au lobby concerné
     * @param objReq corps de la requête
     */
    public void demanderAjoutBot(JSONObject objReq){
        String type = objReq.getString(RequetesJSON.Attributs.JoueurAttr.TYPE_AGENT);
        TypeAgent typeAgent = TypeAgent.valueOf(type);
        if(this.lobby != null) {
            this.lobby.ajouterBot(typeAgent, this.idClient);
        }
    }

    /**
     * Relaye la demande de retrait de bot au lobby concerné
     * @param objReq corps de la requête
     */
    public void demanderRetraitBot(JSONObject objReq){
        String type = objReq.getString(RequetesJSON.Attributs.JoueurAttr.TYPE_AGENT);
        TypeAgent typeAgent = TypeAgent.valueOf(type);
        if(this.lobby != null) {
            this.lobby.retirerBot(typeAgent, this.idClient);
        }
    }

    /**
     * Relaye la demande de changement de stratégie d'un bot au lobby concerné
     * @param objReq corps de la requête
     */
    public void demanderChangementStrategieBot(JSONObject objReq) {
        TypeStrategie typeStrategie = TypeStrategie.valueOf(objReq.getString(RequetesJSON.Attributs.LobbyAttr.TYPE_STRATEGIE));
        int numBot = objReq.getInt(RequetesJSON.Attributs.LobbyAttr.NUM_BOT);
        this.lobby.setStrategieBot(this.idClient, numBot, typeStrategie);
    }

    /**
     * Relaye la demande de changement de camp au lobby concerné
     * @param objReq corps de la requête
     */
    public void demanderChangementCamp(JSONObject objReq) {
        this.lobby.changerCamp(this);
    }

    /**
     * Change la direction de son agent en fonction de l'action de déplacement reçue du client
     * @param objReq corps de la requête contenant la direction du déplacement demandée par le client
     */
    public void traiterDeplacement(JSONObject objReq) {
        int direction = objReq.getInt(RequetesJSON.Attributs.PartieAttr.SENS_MOUVEMENT);
        if(this.agent != null){
            this.agent.getStrategie().onKeyPressed(direction);
        }
    }

    /**
     * Relaye la demande de changement de map au lobby concerné
     * @param objReq corps de la requête
     */
    public void demanderChangementMap(JSONObject objReq) {
        String nomMap = objReq.getString(RequetesJSON.Attributs.LobbyAttr.MAP);
        this.lobby.changerMap(this, nomMap);
    }

    /**
     * Envoie au client la réponse du lobby pour le changement de map
     * @param autorise booleen autorisé ou non
     * @param nbPacmanMax nombre maximum de pacman autorisés sur la map choisie
     * @param nbFantomeMax nombre maximum de fantômes autorisés sur la map choisie
     */
    public void autorisationChangementMap(boolean autorise, int nbPacmanMax, int nbFantomeMax) {
        JSONObject objResp = new JSONObject();
        objResp.put(RequetesJSON.Attributs.ACTION, RequetesJSON.RES_CHANGEMENT_MAP);
        objResp.put(RequetesJSON.Attributs.LobbyAttr.AUTORISE_CHANGEMENT, autorise);
        objResp.put(RequetesJSON.Attributs.LobbyAttr.NB_MAX_PACMAN, nbPacmanMax);
        objResp.put(RequetesJSON.Attributs.LobbyAttr.NB_MAX_FANTOME, nbFantomeMax);
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
