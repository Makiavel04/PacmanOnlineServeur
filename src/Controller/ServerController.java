package Controller;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

import Client.ClientHandler;
import Match.MatchThread;

public class ServerController {
    private int port;
    private ServerSocket ecoute;
    private Vector<ClientHandler> clients;
    private Vector<MatchThread> matches;

    public ServerController(int p){
        this.port = p;
        this.clients = new Vector<ClientHandler>();
        this.matches = new Vector<MatchThread>();
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

    public synchronized MatchThread demandeDeMatch(ClientHandler client) throws Exception {
        try{
            MatchThread matchFound = this.trouverMatchAvecPlace();
            if (matchFound == null) {
                matchFound = this.creerMatch(client);
            } 
            this.connectionMatch(client, matchFound);
            return matchFound;
        } catch (Exception e) {
            System.out.println("Error handling match request: " + e.getMessage());
            throw e;
        }

    }

    public void verifierLancementMatch(int idMatch) {
        MatchThread match = this.getMatch(idMatch);
        if (match != null && match.isFilled() && (match.getState() == Thread.State.NEW)) {
            match.lancerPartie();
            System.out.println("Match: " + idMatch + " started successfully");
        } else {
            System.out.println("Match: " + idMatch + " cannot be started. Not found or not filled.");
        }
    }

    public MatchThread trouverMatchAvecPlace () {
        for (int i = 0; i < matches.size(); i++) {
            MatchThread match = matches.get(i);
            if (!match.isFilled()) {
                return match;
            }
        }
        return null;
    }

    public MatchThread creerMatch (ClientHandler client) {
        MatchThread match = new MatchThread(client);
        matches.add(match);
        return match;
    }

    public synchronized void connectionMatch (ClientHandler client, MatchThread match) throws Exception {
        try{
            match.connectClient(client);
            System.out.println("Client: " + client.getID() +"successfully connected to match: "+ match.getID());
        } catch (Exception e) {
            System.out.println("Error connecting client to match: " + e.getMessage());
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

    public MatchThread getMatch(int idMatch) {
        for (int i = 0; i < matches.size(); i++) {
            if (matches.get(i).getID() == idMatch) {
                return matches.get(i);
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

    public boolean demandeAuthentification(String username, String password) {
        return true;
    }
}
