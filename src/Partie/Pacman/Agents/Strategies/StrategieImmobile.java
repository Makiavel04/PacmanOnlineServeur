package Partie.Pacman.Agents.Strategies;

import Partie.Pacman.Agents.Agent;
import Partie.Pacman.Agents.AgentAction;
import Partie.Pacman.Game.PacmanGame;

/** Stratégie de jeu immobile */
public class StrategieImmobile extends Strategie {

    public StrategieImmobile(Agent a) {
        super(a);
    }

    /**
     * Choisie toujours STOP.
     * {@inheritDoc}
     */
    @Override
    public AgentAction choixAction(PacmanGame game) {
        return new AgentAction(AgentAction.STOP);
    }

    @Override
    public void onKeyPressed(int keyCode) {} // Ne fait rien, l'agent reste immobile
    @Override
    public void capsuleActivee(){}
    @Override
    public void capsuleDesactivee(){}
}
