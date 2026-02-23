package Partie.Pacman.Agents.Strategies.Factory;

import Partie.Pacman.Agents.Agent;
import Partie.Pacman.Agents.Strategies.Strategie;
import Partie.Pacman.Agents.Strategies.GhostOriginaux.StrategieGhostClyde;

/** Fabrique de stratégies Clyde */
public class FactoryStrategieGhostClyde implements FactoryStrategie{
    public FactoryStrategieGhostClyde(){}

    /**
     * Créer une stratégie Clyde
     */
    @Override
    public Strategie creerStrategie(Agent agent) {
        return new StrategieGhostClyde(agent);
    }
    
}
