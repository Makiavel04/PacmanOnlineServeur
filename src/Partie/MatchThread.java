package Partie;

import org.json.JSONObject;

public class MatchThread extends Thread {
    private Lobby lobby;

    int tour ;

    public MatchThread(Lobby lobby) {
        this.lobby = lobby;
    }   

    public int getTour() {
        return this.tour;
    }

    @Override
    public void run() {
        tour = 0;
        System.out.println("Match thread started");
        try{
            for(int i = 0; i < 5; i++) {
                tour++;
                sleep(1000);
                this.lobby.notfierTour(this.getEtat());
            }
            sleep(2000);
            this.lobby.notifierFinPartie();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public JSONObject getEtat() {
        JSONObject etat = new JSONObject();
        etat.put("action", "miseAJourPartie");
        etat.put("tour", this.tour);
        return etat;
    }
}
