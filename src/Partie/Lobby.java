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
        return this.nbJoueurConnecte >= this.nbJoueurMax;
    }

    public MatchThread getMatchThread() {
        return this.matchThread;
    }

    //--- Connection client ---
    public synchronized void connectClient(ClientHandler client) throws Exception {
        if(!this.isFilled() && this.matchThread == null) {
            clients.add(client);
            this.nbJoueurConnecte++;
            this.majLobby(client.getID());
        }else {
            throw new Exception("Match is already full");
        }
    }
    public synchronized void disconnectClient(ClientHandler client) {
        clients.remove(client);
        this.nbJoueurConnecte--;
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
        for(ClientHandler client : clients) {
            clientsDetails.add(client.getDetailsClient());
        }
        return clientsDetails;
    }

    public ClientHandler getHost() {
        for(ClientHandler client : clients) {
            if(client.getID() == this.idHost) {
                return client;
            }
        }
        return null;
    }

    public void migrerHost(){
        if(this.clients.size() > 0) {
            if(this.idHost == this.clients.get(0).getID()) {
                this.idHost = this.clients.get(1).getID();
            } else {
                this.idHost = this.clients.get(0).getID();
            }
        }
    }

    public void majLobby(int newClientId) {
        for(ClientHandler client : clients) {
            if(client.getID() != newClientId) {//On ne veut pas maj le client qui vient de se connecter
                client.majLobby(this.getDetailsLobby().toJSON());
            }
        }
    }



    //--- Gestion du match ---
    public void lancerPartie(){
        this.notifierDebutPartie();
        this.matchThread = new MatchThread(this);
        this.matchThread.start();
    }

    public void notifierDebutPartie() {
        for(int i = 0; i < clients.size(); i++) {
            clients.get(i).debutPartie();
        }
    }

    public void notfierTour(JSONObject etat) {
        for(int i = 0; i < clients.size(); i++) {
            clients.get(i).majTour(etat.toString());
        }
    }
    public void notifierFinPartie() {
        for(int i = 0; i < clients.size(); i++) {
            clients.get(i).finPartie();
        }
        this.matchThread = null;
    }}
