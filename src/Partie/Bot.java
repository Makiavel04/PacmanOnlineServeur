package Partie;

import Partie.Pacman.Agents.Agent;
import Partie.Pacman.Agents.TypeAgent;
import Partie.Pacman.Agents.Strategies.Strategie;
import Partie.Pacman.Agents.Strategies.TypeStrategie;
import Ressources.EtatLobby.DetailsJoueur;

public class Bot implements Joueur {
    Agent agent;
    TypeStrategie typeStrategie;
    TypeAgent typeAgent;
    int numBot;

    public Bot(int num, TypeAgent typeAgent, TypeStrategie typeStrategie) {
        this.typeStrategie = typeStrategie;
        this.typeAgent = typeAgent;
        this.numBot = num;
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
    public TypeStrategie getTypeStrategie() {
        return this.typeStrategie;
    }

    public void setTypeStrategie(TypeStrategie typeStrategie) {
        this.typeStrategie = typeStrategie;
    }

    @Override
    public Strategie getStrategie() {
        if(this.agent != null) {
            return this.agent.getStrategie();
        }else {
            return null;
        }
    }

    @Override
    public DetailsJoueur getDetailsJoueur() {
        return new DetailsJoueur(this.getId(), this.getNom(), this.typeAgent, this.isBot(), this.getTypeStrategie().name());
    }

    public int getId() {
        return -this.numBot;
    }   

    public int getNumBot() {
        return numBot;
    }

    public String getNom() {
        return "Bot #" + this.numBot;
    }

    public boolean isBot() {
        return true;
    }
        
    public void finPartie() {
        this.agent = null;
    }
}
