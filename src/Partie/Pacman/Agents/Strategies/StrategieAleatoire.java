package Partie.Pacman.Agents.Strategies;

import java.util.Random;

import Partie.Pacman.Agents.Agent;
import Partie.Pacman.Agents.AgentAction;
import Partie.Pacman.Game.PacmanGame;

/** Stratégie de déplacement aléatoire */
public class StrategieAleatoire extends Strategie {
    AgentAction tamponAction;
    private static final Random R = new Random();
    
    public StrategieAleatoire(Agent agent){
        super(agent);
        this.tamponAction = new AgentAction(4);
    }
    
    /**
     * Renvoie une action aléatoire
     * {@inheritDoc}
     */
    @Override
    public AgentAction choixAction(PacmanGame game){
        AgentAction randomAction = new AgentAction(R.nextInt(4));
        if(game.isLegalMove(randomAction, this.agent.getPosition())){
            this.tamponAction = randomAction;
        } 
        return this.tamponAction;
    }

    @Override
    public void onKeyPressed(int keyCode) {}
    @Override
    public void capsuleActivee(){}
    @Override
    public void capsuleDesactivee(){}
}
