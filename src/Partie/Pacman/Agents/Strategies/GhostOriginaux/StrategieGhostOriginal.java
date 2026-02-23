package Partie.Pacman.Agents.Strategies.GhostOriginaux;

import Partie.Pacman.Agents.Agent;
import Partie.Pacman.Agents.AgentPacman;
import Partie.Pacman.Agents.Strategies.Strategie;
import Partie.Pacman.Agents.Strategies.StrategieGhostIntelligentFuite;
import Partie.Pacman.Game.PacmanGame;
import Partie.Pacman.Helper.CalculDistance;

/**
 * Classe abstraite pour les stratégie de fantômes classiques
 */
public abstract class StrategieGhostOriginal extends Strategie {
    
    public StrategieGhostOriginal(Agent a){
        super(a);
    }

    @Override
    public void onKeyPressed(int keyCode) {}

    @Override
    public void capsuleActivee() {}

    /**
     * Fuit quand une capsule est active
     * {@inheritDoc}
     */
    @Override
    public void capsuleDesactivee() {
        this.agent.setStrategie(new StrategieGhostIntelligentFuite(agent, this));
    }

    /**
     * Pacman le plus proche selon distance manhattan
     * @param game
     * @return Pacman trouvé
     */
    public AgentPacman closestManhattanPacman(PacmanGame game){
        AgentPacman closestP = null;
        int distMin = -1;

        for(AgentPacman p : game.getPacmans()){
            int d = CalculDistance.distanceManhattanEntre(this.agent.getPosition(), p.getPosition(), game);
            if(distMin==-1 || distMin > d){
                closestP = p;
                distMin = d;
            }
        }
        return closestP;
    }
}
