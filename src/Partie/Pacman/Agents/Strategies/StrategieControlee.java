package Partie.Pacman.Agents.Strategies;

import java.awt.event.KeyEvent;

import Partie.Pacman.Agents.Agent;
import Partie.Pacman.Agents.AgentAction;
import Partie.Pacman.Game.PacmanGame;

/**Stratégie de contrôle au clavier */
public class StrategieControlee extends Strategie {
    private static final AgentAction ACT_NORTH = new AgentAction(AgentAction.NORTH);
    private static final AgentAction ACT_SOUTH = new AgentAction(AgentAction.SOUTH);
    private static final AgentAction ACT_WEST  = new AgentAction(AgentAction.WEST);
    private static final AgentAction ACT_EAST  = new AgentAction(AgentAction.EAST);
    private static final AgentAction ACT_STOP  = new AgentAction(AgentAction.STOP);

    private volatile AgentAction action;


    public StrategieControlee(Agent a){
        super(a);
        this.action = ACT_STOP; //Par défaut, ne rien faire
    }

    public void setAction(AgentAction action){
        this.action = action;
    }

    /** 
     * Renvoie l'action clavier
     * {@inheritDoc}
     */
    @Override
    public AgentAction choixAction(PacmanGame game){
        return this.action;
    }
    
    /**
     * Enregistre l'action associée à la touche
     * {@inheritDoc}
     */
    @Override
    public void onKeyPressed(int keyCode) {
        switch (keyCode) {
            case KeyEvent.VK_UP -> {
                action = ACT_NORTH;
                //System.out.println("haut");
            }
            case KeyEvent.VK_DOWN -> {
                action = ACT_SOUTH;
                //System.out.println("bas");
            }
            case KeyEvent.VK_LEFT -> {
                action = ACT_WEST;
                //System.out.println("gauche");
            }
            case KeyEvent.VK_RIGHT -> {
                action = ACT_EAST;
                //System.out.println("droite");
            }
            case KeyEvent.VK_ENTER -> {
                action = ACT_STOP;
                //System.out.println("stop");
            }
        }
        System.out.println("Action : Changement de direction");
    }
    @Override
    public void capsuleActivee(){}
    @Override
    public void capsuleDesactivee(){}

}
