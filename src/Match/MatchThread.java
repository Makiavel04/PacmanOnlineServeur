package Match;
import java.util.Vector;

import org.json.JSONObject;

import Client.ClientHandler;
import Controller.ServerController;

public class MatchThread extends Thread {
    public static int idCpt = 0;

    private int idMatch;
    private ClientHandler host;
    private int playerNumberWaited;
    private int currentPlayerNumber;
    private ServerController serverController;
    private Vector<ClientHandler> clients;

    int tour ;

    public MatchThread(ClientHandler client) {
        this.idMatch = MatchThread.idCpt++;
        this.host = client;
        this.playerNumberWaited = 2; // à changer plus tard selon la map
        this.currentPlayerNumber = 0;
        this.clients = new Vector<ClientHandler>();
    }   

    @Override
    public void run() {
        tour = 0;
        System.out.println("Match thread started");
        try{
            for(int i = 0; i < 5; i++) {
                tour++;
                sleep(1000);
                this.notfierTour();
            }
            sleep(2000);
            this.notifierFinPartie();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public int getID() {
        return this.idMatch;
    }

    public synchronized void connectClient(ClientHandler client) throws Exception {
        if(!this.isFilled()) {
            clients.add(client);
            this.currentPlayerNumber++;
        }else {
            throw new Exception("Match is already full");
        }
    }

    public boolean isFilled() {
        return this.currentPlayerNumber >= this.playerNumberWaited;
    }

    public void lancerPartie(){
        this.notifierDebutPartie();
        this.start();
    }

    public void notifierDebutPartie() {
        for(int i = 0; i < clients.size(); i++) {
            clients.get(i).debutPartie();
        }
    }

    public void notfierTour() {
        JSONObject etat = new JSONObject();
        etat.put("action", "miseAJourPartie");
        etat.put("tour", tour);
        for(int i = 0; i < clients.size(); i++) {
            clients.get(i).majTour(etat.toString());
        }
    }
    public void notifierFinPartie() {
        for(int i = 0; i < clients.size(); i++) {
            clients.get(i).finPartie();
        }
    }
}
