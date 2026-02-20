package Partie;

import java.util.ArrayList;
import java.util.Vector;

import org.json.JSONObject;

import Client.ClientHandler;
import Controller.ServerController;
import Ressources.DetailsClient;
import Ressources.DetailsLobby;
import Ressources.ResumeLobby;

public class Lobby {
    public static int idCpt = 0;

    private int idLobby;
    private int idHost;
    private int nbJoueurMax;
    private int nbJoueurConnecte;
    private ServerController serverController;
    private Vector<ClientHandler> clients;
    private MatchThread matchThread;

    public Lobby(ClientHandler client, ServerController serverController) {
        this.idLobby = Lobby.idCpt++;
        this.idHost = client.getID();
        this.serverController = serverController;
        this.clients = new Vector<ClientHandler>();
        clients.add(client);    
        this.nbJoueurMax = 2; // à changer plus tard selon la map
        this.nbJoueurConnecte = this.clients.size();
        this.matchThread = null;
    }

    public int getID() {
        return this.idLobby;
    }

    public boolean isFilled() {
        
        synchronized (this.clients) {
            return this.nbJoueurConnecte >= this.nbJoueurMax;
        }
}

    public MatchThread getMatchThread() {
        return this.matchThread;
    }

    //--- Connection client ---
    public synchronized void connectClient(ClientHandler client) throws Exception {
        synchronized (this.clients) {
            if(!this.isFilled() && this.matchThread == null) {
                this.clients.add(client);
                this.nbJoueurConnecte = this.clients.size();
                this.majLobby(client.getID());
            }else {
                throw new Exception("Match is already full");
            }
        }
    }

    public void disconnectClient(int clientId) {
        synchronized (this.clients) {
            this.clients.removeIf(client -> client.getID() == clientId);
            this.nbJoueurConnecte = this.clients.size();
            if(this.clients.size() == 0) {
                this.serverController.removeLobby(this.idLobby);
            } else {
                if(clientId == this.idHost) {
                    this.migrerHost();
                }
                this.majLobby(clientId);
            }
        }
    }

    //--- Infos du lobby ---
    public ResumeLobby getResumeLobby(){
        return new ResumeLobby(this.idLobby, this.nbJoueurConnecte, this.nbJoueurMax);
    }

    public DetailsLobby getDetailsLobby(){
        return new DetailsLobby(this.idLobby, this.nbJoueurConnecte, this.nbJoueurMax, this.idHost, this.getAllLobbyClients());
    }

    public ArrayList<DetailsClient> getAllLobbyClients() {
        ArrayList<DetailsClient> clientsDetails = new ArrayList<>();
        synchronized (this.clients) {
            for(ClientHandler client : clients) {
                clientsDetails.add(client.getDetailsClient());
            }
        }
        return clientsDetails;
    }

    public ClientHandler getHost() {
        synchronized (this.clients) {
            for(ClientHandler client : clients) {
                if(client.getID() == this.idHost) {
                    return client;
                }
            }
        }
        return null;
    }

    public void migrerHost(){
        synchronized (this.clients) {
            if(!this.clients.isEmpty()) { //Sinon il devrait être supprimé avant car plus personne dans le lobby
                this.idHost = this.clients.get(0).getID();
                System.out.println("Host migrated to client: " + this.idHost + " in lobby: " + this.idLobby);
            }
        }
    }

    public void majLobby(int newClientId) {
        synchronized (this.clients) {
            for(ClientHandler client : clients) {
                if(client.getID() != newClientId) {//On ne veut pas maj le client qui vient de se connecter
                    client.majLobby(this.getDetailsLobby().toJSON());
                }
            }
        }
    }



    //--- Gestion du match ---
    public void tenterLancementPartie(int idClient){
        synchronized (this.clients) {
            if (this.isFilled() && this.getHost().getID() == idClient) { // Synchronized car getHost et isFilled (bien qu'ayant leur propre synchronisation) sont utilisés ensemble, et on veut le même état pour les deux.
                    this.lancerPartie();
                    System.out.println("Lobby: " + this.idLobby + " started successfully");
            } else {
                if(!this.isFilled()) System.out.println("Lobby: " + this.idLobby + " cannot be started. Not filled.");
                else if(this.getHost().getID() != idClient) System.out.println("Lobby: " + this.idLobby + " cannot be started. Client " + idClient + " is not the host.");
            }
        }
    }

    public void lancerPartie(){
        this.notifierDebutPartie();
        this.matchThread = new MatchThread(this);
        this.matchThread.start();
    }

    public void notifierDebutPartie() {
        synchronized (this.clients) {
             for(ClientHandler client : clients) {
                client.debutPartie();
            }
        }
    }

    public void notfierTour(JSONObject etat) {
        synchronized (this.clients) {
             for(ClientHandler client : clients) {
                client.majTour(etat.toString());
            }
        }
    }
    public void notifierFinPartie() {
        synchronized (this.clients) {
            for(ClientHandler client : clients) {
                client.finPartie();
            }
            //Faire quitter tous les clients du lobby et supprimer le lobby
            for(ClientHandler client : clients) {
                client.setLobby(null);
            }
            this.clients.clear();
            this.serverController.removeLobby(this.idLobby);
        }
    }
}
