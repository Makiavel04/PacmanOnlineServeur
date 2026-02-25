package Ressources.EtatGame;

import org.json.JSONObject;

import Ressources.RequetesJSON;
import Ressources.TransformableJSON;

/**
 * Classe abstraite représentant l'état d'un jeu à un moment donné.
 */
public abstract class EtatGame implements TransformableJSON{
    private int tour;

    public EtatGame(int tour) {
        this.tour = tour;
    }

    public int getTour(){return this.tour;}

    //FromJSON inconnu car pas de classe concrète, à implémenter dans les classes filles

    //La méthode toJSON des classes filles doit être implémentée pour inclure les informations spécifiques à chaque jeu, en plus du tour
    @Override
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put(RequetesJSON.Attributs.Partie.TOUR, this.tour);
        return json;
    }


}
