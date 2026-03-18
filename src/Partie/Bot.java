package Partie;

import Partie.Pacman.Agents.Agent;
import Partie.Pacman.Agents.Strategies.Strategie;
import Partie.Pacman.Agents.Strategies.TypeStrategie;
import pacman.online.commun.dto.lobby.DetailsJoueur;
import pacman.online.commun.moteur.TypeAgent;

/**
 * Classe représentant un bot dans une partie de Pacman.
 */
public class Bot implements Joueur {
    /** Agent du bot */
    Agent agent;
    /** Type de la stratégie du bot */
    TypeStrategie typeStrategie;
    /** Type de l'agent du bot */
    TypeAgent typeAgent;
    /** Numéro du bot */
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
        if (this.agent != null) {
            return this.agent.getStrategie();
        } else {
            return null;
        }
    }

    @Override
    public DetailsJoueur getDetailsJoueur() {
        return new DetailsJoueur(this.getId(), this.getNom(), this.typeAgent, this.isBot(),
                this.getTypeStrategie().name());
    }

    public String getCouleur()  {
        if (typeAgent == TypeAgent.PACMAN) {
            return "#40E0D0";
        } else {
            return "#7d669e";
        }
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

    /**
     * Gestion de la fin de partie pour le bot.
     */
    public void finPartie() {
        this.agent = null;
    }
}
