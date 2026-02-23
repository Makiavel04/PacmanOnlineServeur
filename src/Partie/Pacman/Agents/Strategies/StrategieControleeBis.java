package Partie.Pacman.Agents.Strategies;

import java.awt.event.KeyEvent;

import Partie.Pacman.Agents.Agent;
import Partie.Pacman.Agents.AgentAction;
import Partie.Pacman.Game.PacmanGame;

/**Stratégie de contrôle au clavier */
public class StrategieControleeBis extends Strategie {
    private AgentAction action;

    public StrategieControleeBis(Agent a){
        super(a);
        this.action = new AgentAction(4); //Par défaut, ne rien faire
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
            case KeyEvent.VK_Z -> {
                action = new AgentAction(AgentAction.NORTH);
                //System.out.println("haut");
            }
            case KeyEvent.VK_S -> {
                action = new AgentAction(AgentAction.SOUTH);
                //System.out.println("bas");
            }
            case KeyEvent.VK_Q -> {
                action = new AgentAction(AgentAction.WEST);
                //System.out.println("gauche");
            }
            case KeyEvent.VK_D -> {
                action = new AgentAction(AgentAction.EAST);
                //System.out.println("droite");
            }
            case KeyEvent.VK_SPACE -> {
                action = new AgentAction(AgentAction.STOP);
                //System.out.println("stop");
            }
        }
    }
    @Override
    public void capsuleActivee(){}
    @Override
    public void capsuleDesactivee(){}

}
