package Partie.Pacman.Agents.Strategies.Factory;

import Partie.Pacman.Agents.Agent;
import Partie.Pacman.Agents.Strategies.Strategie;
import Partie.Pacman.Agents.Strategies.GhostOriginaux.StrategieGhostBlinky;

/** Fabrique de stratégies Blinky */
public class FactoryStrategieGhostBlinky implements FactoryStrategie{
    public FactoryStrategieGhostBlinky(){}

    /**
     * Créer une stratégie Blinky
     */
    @Override
    public Strategie creerStrategie(Agent agent) {
        return new StrategieGhostBlinky(agent);
    }
}
