package Controller;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONObject;

import Client.ClientHandler;
import Partie.Lobby;
import Ressources.RequetesJSON;

public class ServerController {
    private int port;
    private ServerSocket ecoute;
    private Vector<ClientHandler> clients;
    private Vector<Lobby> lobbies;

    public ServerController(int p){
        this.port = p;
        this.clients = new Vector<ClientHandler>();
        this.lobbies = new Vector<Lobby>();
        System.out.println("Serveur mis en place");
    } 

    public void launch() {
        try {
            this.ecoute = new ServerSocket(port);
            System.out.println("Serveur lancé sur le port: " + this.port);
            while (true) {
                Socket so = this.ecoute.accept();
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
    
    public boolean demandeAuthentification(String username, String password) {
        return true;
    }

    public JSONObject listerLobbies(){//Faire la transformation ailleurs ?
        JSONObject infosLobbies = new JSONObject();
        infosLobbies.put(RequetesJSON.Attributs.ACTION, RequetesJSON.RES_LISTE_LOBBIES);

        JSONArray lobbiesArray = new JSONArray();
        synchronized (this.lobbies) {
            for(Lobby lobby : lobbies) {
                lobbiesArray.put(lobby.getResumeLobby().toJSON());
            }
        }
        infosLobbies.put(RequetesJSON.Attributs.Lobby.LISTE_LOBBIES, lobbiesArray);
        return infosLobbies;
    }

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

    public Lobby creerLobby (ClientHandler client) {
        Lobby lobby = new Lobby(client, this);
        synchronized (this.lobbies) {
            this.lobbies.add(lobby);
        }
        return lobby;
    }

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

    public void removeClient(int idClient){
        synchronized (this.clients) {
            this.clients.removeIf(client -> client.getID() == idClient);
        }
        System.out.println("Client#" + idClient + " supprimé avec succès");
    }

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
