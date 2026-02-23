package Partie.Pacman.Agents.Strategies.GhostOriginaux;

import Partie.Pacman.Agents.Agent;
import Partie.Pacman.Agents.AgentAction;
import Partie.Pacman.Agents.AgentPacman;
import Partie.Pacman.Agents.PositionAgent;
import Partie.Pacman.Agents.Strategies.StrategieAleatoire;
import Partie.Pacman.Game.PacmanGame;
import Partie.Pacman.Helper.CalculDistance;

/** Stratégie classique Clyde */
public class StrategieGhostClyde extends StrategieGhostOriginal{

    public StrategieGhostClyde(Agent a){
        super(a);
    }

    /**
     * Fuit si à moins de 8 case du pacman et le chasse sinon
     * {@inheritDoc}
     */
    @Override
    public AgentAction choixAction(PacmanGame game) {
        AgentPacman pacman = this.closestManhattanPacman(game);
        if(pacman == null){
            return new StrategieAleatoire(this.agent).choixAction(game);
        }

        if(CalculDistance.distanceManhattanEntre(this.agent.getPosition(), pacman.getPosition(), game) > 8){ //Chasse a plus de 8 blocs
            return chercherAction(pacman, game, true);
        }else{ // Fuit à moins de 8
            return chercherAction(pacman, game, false);
        }
    }

    /**
     * Renvoie une action en fonction de si on fuit ou si on chasse
     * @param pacman pacman cible
     * @param game jeu
     * @param chasse Vrai : chasse , Faux : fuit
     * @return action à faire
     */
    public AgentAction chercherAction(AgentPacman pacman, PacmanGame game, boolean chasse){
        PositionAgent cible = new PositionAgent(pacman.getPosition());

        //Pas de demi tour donc si orientation 
        // 0 <=X=> 1
        // 2 <=X=> 3 
        int[] opposites = {1, 0, 3, 2};
        int currentDir = this.agent.getPosition().getDir();
        int dirDemitour = (currentDir >= 0 && currentDir <= 3) ? opposites[currentDir] : -1;
                
        AgentAction actionRef = null;
        int distRef = -1;
        for(int dir : new int[]{0, 1, 2, 3}){
            if(dir == dirDemitour) continue; //Skip si demi-tour
            AgentAction a = new AgentAction(dir);
            if(game.isLegalMove(a, this.agent.getPosition())){
                PositionAgent newPos = new PositionAgent(this.agent.getPosition().getX()+a.get_vx(), this.agent.getPosition().getY()+a.get_vy(), dir);
                int dist =  CalculDistance.distanceManhattanEntre(newPos, cible, game);
                if(distRef==-1 || //Pas encore d'action
                    (chasse && dist < distRef) || //Ou on chasse
                    (!chasse && dist > distRef) || // Ou on fuit
                    (dist == distRef && Math.random() < 0.5) // Si distance égale, choisir aléatoirement
                ){
                    actionRef = a;
                    distRef = dist;
                }
            }
        }

        if(actionRef==null){//Si aucun coup légal => cul de sac alors on STOP pour le tour
            actionRef = new AgentAction(AgentAction.STOP);
        }

        return actionRef;
    }

}
