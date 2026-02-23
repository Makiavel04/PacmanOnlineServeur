package Partie.Pacman.Agents;

import Partie.Pacman.Game.PacmanGame;
import Partie.Pacman.Helper.CalculDistance;

/**
 * personnage Pacman
 */
public class AgentPacman extends Agent{
    public AgentPacman(PositionAgent pos){
        super(pos);
    }

    /**
     * Pacman vulnérable tant que pas de capsule
     * {@inheritDoc}
     */
    @Override
    public boolean isVulnerable(PacmanGame game){
        return !game.isCapsuleActive();
    }

    /**
     * Le pacman peut manger la nourriture
     * {@inheritDoc}
     */
    @Override
    public boolean canEatFood(){
        return true;
    }

    /**
     * Tant qu'il y a des vies
     * {@inheritDoc}
     */
    @Override
    public boolean peutReapparaitre(PacmanGame game) {
        return game.getNbViesPacman() > 0;
    }

    /**
     * Respawn et perd une vie
     * {@inheritDoc}
     */
    @Override
    public void respawn(PacmanGame game) {
        for(PositionAgent p : this.getPositionsPossiblesAutour(game, 5)){
            if(!CalculDistance.isThereGhostUnderDistance(game, p, 3)){
                game.perdreViePacman();
                this.setPosition(new PositionAgent(p.getX(), p.getY(), p.getDir()));
                this.setMort(false);
                break;
            }
        }
    }    
}
