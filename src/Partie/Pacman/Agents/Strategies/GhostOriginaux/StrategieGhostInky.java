package Partie.Pacman.Agents.Strategies.GhostOriginaux;

import Partie.Pacman.Agents.Agent;
import Partie.Pacman.Agents.AgentAction;
import Partie.Pacman.Agents.AgentPacman;
import Partie.Pacman.Agents.PositionAgent;
import Partie.Pacman.Agents.Strategies.StrategieAleatoire;
import Partie.Pacman.Game.PacmanGame;
import Partie.Pacman.Helper.CalculDistance;

/** Stratégie classique Inky */
public class StrategieGhostInky extends StrategieGhostOriginal{

    public StrategieGhostInky(Agent a){
        super(a);
    }

    /**
     * Utilise un vecteur entre 2 cases devant le pacman et lui  qu'il double pour choisir son action (Normalement se base sur un autre fantôme que lui mais ici pas sûr car pas toujours tous les fantômes)
     * {@inheritDoc}
     */
    @Override
    public AgentAction choixAction(PacmanGame game) {
        //Pacman 
        AgentPacman pacman = this.closestManhattanPacman(game);
        if(pacman == null){
            return new StrategieAleatoire(this.agent).choixAction(game);
        }
        PositionAgent posPacman = pacman.getPosition();

        //Pas de demi tour donc si orientation 
        // 0 <=X=> 1
        // 2 <=X=> 3 
        int[] opposites = {1, 0, 3, 2};
        int currentDir = this.agent.getPosition().getDir();
        int dirDemitour = (currentDir >= 0 && currentDir <= 3) ? opposites[currentDir] : -1;

        //Vecteur => Normalement mix position Blinky et Pacman. Ici blinky pas assuré donc entre lui et pacman
        AgentAction sens = new AgentAction(currentDir);
        PositionAgent pos2devantPacman = new PositionAgent(posPacman.getX()+(2*sens.get_vx()), posPacman.getY()+(2*sens.get_vy()), posPacman.getDir());
        PositionAgent posInky = agent.getPosition();
        int vx = (pos2devantPacman.getX() - posInky.getX()) * 2;
        int vy = (pos2devantPacman.getY() - posInky.getY()) * 2;
        PositionAgent cible = new PositionAgent(posInky.getX() + vx, posInky.getY() + vy, posInky.getDir());

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
