package Partie;

import java.util.Vector;

import org.json.JSONObject;

import Client.ClientHandler;
import Controller.ServerController;

public class Lobby {
    public static int idCpt = 0;

    private int idMatch;
    private ClientHandler host;
    private int nbJoueurMax;
    private int nbJoueurConnecte;
    private ServerController serverController;
    private Vector<ClientHandler> clients;
    private MatchThread matchThread;

    public Lobby(ClientHandler client, ServerController serverController) {
        this.idMatch = Lobby.idCpt++;
        this.host = client;
        this.nbJoueurMax = 2; // à changer plus tard selon la map
        this.nbJoueurConnecte = 0;
        this.clients = new Vector<ClientHandler>();
        this.serverController = serverController;
        this.matchThread = null;
    }

    public int getID() {
        return this.idMatch;
    }

    public synchronized void connectClient(ClientHandler client) throws Exception {
        if(!this.isFilled()) {
            clients.add(client);
            this.nbJoueurConnecte++;
        }else {
            throw new Exception("Match is already full");
        }
    }

    public synchronized void disconnectClient(ClientHandler client) {
        clients.remove(client);
        this.nbJoueurConnecte--;
    }

    public boolean isFilled() {
        return this.nbJoueurConnecte >= this.nbJoueurMax;
    }

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
    }

    public MatchThread getMatchThread() {
        return this.matchThread;
    }

    public InfosLobby getInfosLobby(){
        return new InfosLobby(this.idMatch, this.nbJoueurConnecte, this.nbJoueurMax);
    }
}
