package Partie.Pacman.Agents.Strategies.GhostOriginaux;

import Partie.Pacman.Agents.Agent;
import Partie.Pacman.Agents.AgentAction;
import Partie.Pacman.Agents.AgentPacman;
import Partie.Pacman.Agents.PositionAgent;
import Partie.Pacman.Agents.Strategies.StrategieAleatoire;
import Partie.Pacman.Game.PacmanGame;
import Partie.Pacman.Helper.CalculDistance;
/**Strategie classique Pinky */
public class StrategieGhostPinky extends StrategieGhostOriginal {

    public StrategieGhostPinky(Agent a) {
        super(a);
    }

    /**
     * Cherche toujours à aller 4 cases devant le pacman
     * {@inheritDoc}
     */
    @Override
    public AgentAction choixAction(PacmanGame game) {
        //Cible 
        AgentPacman pacman = this.closestManhattanPacman(game);
        if(pacman == null){
            return new StrategieAleatoire(this.agent).choixAction(game);
        }

        //Pas de demi tour donc si orientation 
        // 0 <=X=> 1
        // 2 <=X=> 3 
        int[] opposites = {1, 0, 3, 2};
        int currentDir = this.agent.getPosition().getDir();
        int dirDemitour = (currentDir >= 0 && currentDir <= 3) ? opposites[currentDir] : -1;

        PositionAgent posPacman = pacman.getPosition();
        AgentAction sens = new AgentAction(posPacman.getDir());
        PositionAgent cible = new PositionAgent(posPacman.getX()+(4*sens.get_vx()), posPacman.getY()+(4*sens.get_vy()), currentDir);
        
        
        AgentAction actionMin = null;
        int distMin = -1;
        for(int dir : new int[]{0, 1, 2, 3}){
            if(dir == dirDemitour) continue; //Skip si demi-tour
            AgentAction a = new AgentAction(dir);
            if(game.isLegalMove(a, this.agent.getPosition())){
                PositionAgent newPos = new PositionAgent(this.agent.getPosition().getX()+a.get_vx(), this.agent.getPosition().getY()+a.get_vy(), dir);
                int dist =  CalculDistance.distanceManhattanEntre(newPos, cible, game);
                if((distMin==-1 || dist < distMin) || (dist == distMin && Math.random() < 0.5)){ // Si distance égale, choisir aléatoirement)
                    actionMin = a;
                    distMin = dist;
                }
            }
        }

        if(actionMin==null){//Si aucun coup légal => cul de sac alors on STOP pour le tour
            actionMin = new AgentAction(AgentAction.STOP);
        }

        return actionMin;
    }    
}
