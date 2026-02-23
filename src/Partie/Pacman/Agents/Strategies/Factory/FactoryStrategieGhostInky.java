package Partie.Pacman.Agents.Strategies.Factory;

import Partie.Pacman.Agents.Agent;
import Partie.Pacman.Agents.Strategies.Strategie;
import Partie.Pacman.Agents.Strategies.GhostOriginaux.StrategieGhostInky;

/** Fabrique de stratégies Inky */
public class FactoryStrategieGhostInky implements FactoryStrategie{
    public FactoryStrategieGhostInky(){}

    /**
     * Créer une stratégie Inky
     */
    @Override
    public Strategie creerStrategie(Agent agent) {
        return new StrategieGhostInky(agent);
    }
    
}
