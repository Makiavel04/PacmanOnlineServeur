package Partie.Pacman.Agents;

import Partie.Pacman.Game.PacmanGame;
import Partie.Pacman.Helper.CalculDistance;

/** Personnage fantôme */
public class AgentGhost extends Agent{
    private int cooldown;
    private boolean movedLastTime;

    public AgentGhost(PositionAgent p){
        super(p);
        this.movedLastTime = false;
    }

    /**
     * Si passe à Vrai, on ajoute aussi un cooldown
     * {@inheritDoc}
     */
    @Override
    public void setMort(boolean b){
        super.setMort(b);
        if(b) cooldown = 20; 
    }


    /**
     * Quand le fantôme est vulnérable il ne
     * {@inheritDoc}
     */
    @Override
    public AgentAction choisirAction(PacmanGame game){
        if(this.isVulnerable(game) && this.movedLastTime){
            this.movedLastTime = false;
            return new AgentAction(AgentAction.STOP);
        }else{
            if(this.isVulnerable(game)) this.movedLastTime = true;
            return super.choisirAction(game);
        }
    }

    /**
     * Fantôme vulnérable quand capsule active
     * {@inheritDoc}
     */
    @Override
    public boolean isVulnerable(PacmanGame game){
        return game.isCapsuleActive();
    }

    /**
     * Le fantôme ne peut pas manger la nourriture
     * {@inheritDoc}
     */
    @Override
    public boolean canEatFood(){
        return false;
    }

    /**
     * Toujours
     * {@inheritDoc}
     */
    @Override
    public boolean peutReapparaitre(PacmanGame game) {
        return true;
    }


    /**
     * Respawn après le cooldown
     * {@inheritDoc}
     */
    @Override
    public void respawn(PacmanGame game) {
        if(cooldown>0){
            --cooldown;
        }else{
            for(PositionAgent p : this.getPositionsPossiblesAutour(game, 5)){
                if(!CalculDistance.isTherePacmanUnderDistance(game, p, 3)){
                    this.setPosition(new PositionAgent(p.getX(), p.getY(), p.getDir()));
                    this.setMort(false);
                    break;
                }
            }
        }
    }

    


    
}