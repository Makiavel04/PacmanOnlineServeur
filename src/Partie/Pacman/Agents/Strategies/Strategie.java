package Partie.Pacman.Agents.Strategies;

import Partie.Pacman.Agents.Agent;
import Partie.Pacman.Agents.AgentAction;
import Partie.Pacman.Game.PacmanGame;

/**
 * Classe abstraite de stratégie
 */
public abstract class Strategie {
    /** Agent attaché à la stratégie */
    protected Agent agent;

    public Strategie(Agent a){
        this.agent = a;
    }

    /**
     * Fournir une action
     * @param game
     * @return action choisie
     */
    public abstract AgentAction choixAction(PacmanGame game);

    /**
     * Réagir à une entrée clavier
     * @param keyCode touche préssée
     */
    public abstract void onKeyPressed(int keyCode);

    /**
     * Réagir à l'activation d'une capsule
     */
    public abstract void capsuleActivee();

    /**
     * Réagir à la désactivation d'une capsule
     */
    public abstract void capsuleDesactivee();
}
