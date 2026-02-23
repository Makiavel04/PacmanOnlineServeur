package Partie;

import Partie.Pacman.Agents.Agent;
import Partie.Pacman.Agents.TypeAgent;
import Ressources.EtatLobby.DetailsJoueur;

public class Bot implements Joueur {
    Agent agent;
    TypeAgent typeAgent;
    String nom;

    public Bot(int num, TypeAgent typeAgent){
        this.typeAgent = typeAgent;
        this.nom = "Bot" + num;
        this.agent = null;
    }

    @Override
    public void initJoueur(Agent agent) {
        this.agent = agent;
    }

    @Override
    public TypeAgent getTypeAgent() {
        return typeAgent;
    }

    @Override
    public Agent getAgent() {
        return agent;
    }

    @Override
    public void setAgent(Agent agent) {
        this.agent = agent;
    }

    @Override
    public DetailsJoueur getDetailsJoueur() {
        return new DetailsJoueur(-1, this.nom, this.typeAgent);
    }
        
}
