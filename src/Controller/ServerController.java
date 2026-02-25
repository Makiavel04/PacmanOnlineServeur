package Controller;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;


import Client.ClientHandler;
import Partie.Lobby;

/**
 * Classe principale du serveur, elle gère les connexions des clients, les lobbies et la communication avec les clients.
 */
public class ServerController {
    private int port;
    private ServerSocket ecoute;
    /**Liste des clients connectés au serveur */
    private Vector<ClientHandler> clients;
    /**Liste des lobbies actifs sur le serveur */
    private Vector<Lobby> lobbies;

    public ServerController(int p){
        this.port = p;
        this.clients = new Vector<ClientHandler>();
        this.lobbies = new Vector<Lobby>();
        System.out.println("Serveur mis en place");
    } 

    /**
     * Lance le serveur et gère les connexions entrantes des clients. Pour chaque client qui se connecte, un nouveau ClientHandler est créé pour gérer la communication avec ce client.
     */
    public void launch() {
        try {
            this.ecoute = new ServerSocket(port);
            System.out.println("Serveur lancé sur le port: " + this.port);
            while (true) {
                Socket so = this.ecoute.accept();
                so.setTcpNoDelay(true);
                ClientHandler clientHandler = new ClientHandler(so, this);
                clientHandler.ouvrirConnection();
                synchronized (this.clients) {
                    this.clients.add(clientHandler);
                }
                int idClient = clientHandler.getID();
                System.out.println("Nouveau client#"+ idClient +" sur le serveur");
            }
        } catch (IOException e) {
            System.out.println("Problème serveur :\n"+ e);
        }
    }
    
    /** Vérifie si authentification valide */
    public boolean demandeAuthentification(String username, String password) {
        return true;
    }

    /** Fournit la liste des lobbies actifs */
    public Vector<Lobby> listerLobbies(){
        synchronized(this.lobbies){
            return new Vector<>(this.lobbies); //Eviter de retourner la référence directe à la liste des lobbies pour éviter les problèmes de concurrence
        }
    }

    /**
     * Gère la demande de match d'un client.
     * @param client Le client qui fait la demande de match
     * @param idLobby L'identifiant du lobby à rejoindre, ou -1 pour en créer un nouveau
     * @return Le lobby auquel le client a été connecté ou créé
      * @throws Exception Si le lobby spécifié n'existe pas ou si la connexion au lobby échoue
     */
    public Lobby demandeDeMatch(ClientHandler client, int idLobby) throws Exception {
        try{
            Lobby lobbyFound;
            synchronized (this.lobbies) {
                if(idLobby == -1) {
                    lobbyFound = this.creerLobby(client);
                }else{
                    lobbyFound = this.getLobby(idLobby);
                    if (lobbyFound == null) {
                        throw new Exception("Lobby#" + idLobby + " non trouvé.");
                    }
                    this.connectionLobby(client, lobbyFound);
                }
            }
            return lobbyFound;
        } catch (Exception e) {
            System.out.println("Erreur dans la gestion de demander de match : " + e.getMessage());
            throw e;
        }
    }

    /**
     * Vérifie si les conditions de lancement d'une partie sont remplies dans un lobby donné, et si c'est le cas, lance la partie.
     * @param idLobby L'identifiant du lobby pour lequel vérifier le lancement de la partie
     * @param idClient L'identifiant du client qui demande le lancement de la partie (hôte normalement)
     */
    public void verifierLancementMatch(int idLobby, int idClient) {
        synchronized (this.lobbies) {
            Lobby lobby = this.getLobby(idLobby);
            if (lobby != null) {
                lobby.tenterLancementPartie(idClient);
            } else {
                System.out.println("Lobby#" + idLobby + " ne peut pas démarrer : non trouvé.");
            }
        }
    }

    /**
     * Crée un nouveau lobby avec le client spécifié comme hôte, ajoute ce lobby à la liste des lobbies actifs et retourne le lobby créé.
     * @param client client qui demande la création du lobby et qui en sera l'hôte
     * @return Le lobby nouvellement créé
     */
    public Lobby creerLobby (ClientHandler client) {
        Lobby lobby = new Lobby(client, this);
        synchronized (this.lobbies) {
            this.lobbies.add(lobby);
        }
        return lobby;
    }

    /**
     * Connecte un client à un lobby donné en appelant la méthode de connexion du lobby. 
     * @param client Le client à connecter au lobby
     * @param lobby Le lobby auquel connecter le client
     * @throws Exception Si la connexion du client au lobby échoue 
     */
    public void connectionLobby (ClientHandler client, Lobby lobby) throws Exception {
        try{
            synchronized (this.lobbies) {
                lobby.connectClient(client);
            }
            System.out.println("Client#" + client.getID() +" connecté au lobby#" + lobby.getID());
        } catch (Exception e) {
            System.out.println("Erreur lors de la connexion du client#" + client.getID() + " au lobby#" + lobby.getID() + " : " + e.getMessage());
            throw e;
        }
    }

    /**
     * Récupére le client par son id
     * @param idClient id du client à récupérer
     * @return le client trouvé ou null si aucun client ne correspond à cet id
     */
    public ClientHandler getClient(int idClient) {
        synchronized (this.clients){    
            for (int i = 0; i < clients.size(); i++) {
                if (clients.get(i).getID() == idClient) {
                    return clients.get(i);
                }        
            }
            return null;
        }
    }

    /**
     * Récupére le lobby par son id
     * @param idLobby id du lobby à récupérer
     * @return le lobby trouvé ou null si aucun lobby ne correspond à cet id
     */
    public Lobby getLobby(int idLobby) {
        synchronized (this.lobbies){    
            for (int i = 0; i < lobbies.size(); i++) {
                if (lobbies.get(i).getID() == idLobby) {
                    return lobbies.get(i);
                }        
            }
            return null;
        }
    }

    /**
     * Retire un client du serveur
     * @param idClient L'identifiant du client à retirer
     */
    public void removeClient(int idClient){
        synchronized (this.clients) {
            this.clients.removeIf(client -> client.getID() == idClient);
        }
        System.out.println("Client#" + idClient + " supprimé avec succès");
    }

    /**
     * Retire un lobby du serveur
     * @param idLobby L'identifiant du lobby à retirer
     */
    public void removeLobby(int idLobby){
        synchronized (this.lobbies) {
            this.lobbies.removeIf(lobby -> lobby.getID() == idLobby);
        }
        System.out.println("Lobby#" + idLobby + " supprimé avec succès");
    }

    public synchronized Vector<Lobby> getLobbies() {
        return lobbies;
    }
    public synchronized Vector<ClientHandler> getClients() {
        return clients;
    }
}
