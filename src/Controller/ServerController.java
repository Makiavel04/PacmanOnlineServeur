package Controller;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONObject;

import Client.ClientHandler;
import Partie.Lobby;

public class ServerController {
    private int port;
    private ServerSocket ecoute;
    private Vector<ClientHandler> clients;
    private Vector<Lobby> lobbies;

    public ServerController(int p){
        this.port = p;
        this.clients = new Vector<ClientHandler>();
        this.lobbies = new Vector<Lobby>();
        System.out.println("Server set up");
    } 

    public void launch() {
        try {
            this.ecoute = new ServerSocket(port);
            System.out.println("Server launched on port: " + this.port);
            while (true) {
                Socket so = this.ecoute.accept();
                ClientHandler clientHandler = new ClientHandler(so, this);
                clientHandler.ouvrirConnection();
                this.clients.add(clientHandler);
                int idClient = clientHandler.getID();
                System.out.println("New client: "+ idClient +" on the server");
            }
        } catch (IOException e) {
            System.out.println("Problem\n"+ e);
        }
    }
    
    public boolean demandeAuthentification(String username, String password) {
        return true;
    }

    public JSONObject listerLobbies(){
        JSONObject infosLobbies = new JSONObject();
        infosLobbies.put("action", "reponseListeLobbies");

        JSONArray lobbiesArray = new JSONArray();
        for(Lobby lobby : lobbies) {
            lobbiesArray.put(lobby.getInfosLobby().toJSON());
        }
        infosLobbies.put("lobbies", lobbiesArray);
        return infosLobbies;
    }

    public synchronized Lobby demandeDeMatch(ClientHandler client, int idLobby) throws Exception {
        try{
            Lobby lobbyFound;
            if(idLobby == -1) {
                lobbyFound = this.creerLobby(client);
            }else{
                lobbyFound = this.getLobby(idLobby);
                if (lobbyFound == null) {
                    throw new Exception("Lobby with ID " + idLobby + " not found.");
                }
            }
            this.connectionLobby(client, lobbyFound);
            return lobbyFound;
        } catch (Exception e) {
            System.out.println("Error handling lobby request: " + e.getMessage());
            throw e;
        }

    }

    public void verifierLancementMatch(int idLobby) {
        Lobby lobby = this.getLobby(idLobby);
        if (lobby != null && lobby.isFilled()) {
            lobby.lancerPartie();
            System.out.println("Lobby: " + idLobby + " started successfully");
        } else {
            if(lobby==null) System.out.println("Lobby: " + idLobby + " cannot be started. Not found.");
            else if(!lobby.isFilled()) System.out.println("Lobby: " + idLobby + " cannot be started. Not filled.");
        }
    }

    public Lobby trouverLobbyAvecPlace () {
        for (int i = 0; i < lobbies.size(); i++) {
            Lobby lobby = lobbies.get(i);
            if (!lobby.isFilled()) {
                return lobby;
            }
        }
        return null;
    }

    public Lobby creerLobby (ClientHandler client) {
        Lobby lobby = new Lobby(client, this);
        lobbies.add(lobby);
        return lobby;
    }

    public synchronized void connectionLobby (ClientHandler client, Lobby lobby) throws Exception {
        try{
            lobby.connectClient(client);
            System.out.println("Client: " + client.getID() +"successfully connected to lobby: "+ lobby.getID());
        } catch (Exception e) {
            System.out.println("Error connecting client to lobby: " + e.getMessage());
            throw e;
        }
    }

    public ClientHandler getClient(int idClient) {
        for (int i = 0; i < clients.size(); i++) {
            if (clients.get(i).getID() == idClient) {
                return clients.get(i);
            }        
        }
        return null;
    }

    public Lobby getLobby(int idLobby) {
        for (int i = 0; i < lobbies.size(); i++) {
            if (lobbies.get(i).getID() == idLobby) {
                return lobbies.get(i);
            }        
        }
        return null;
    }

    public void removeClient(int idClient){
        for (int i = 0; i < clients.size(); i++) {
            if (clients.get(i).getID() == idClient) {
                clients.remove(i);
                System.out.println("Client " + idClient + " removed successfully");
                break;//Et s'arrête
            }
        }
    }

    }
