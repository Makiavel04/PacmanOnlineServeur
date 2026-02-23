package Partie.Pacman.Agents.Strategies.Factory;

import Partie.Pacman.Agents.Agent;
import Partie.Pacman.Agents.Strategies.Strategie;
import Partie.Pacman.Agents.Strategies.GhostOriginaux.StrategieGhostPinky;

/** Fabrique de stratégies Pinky */
public class FactoryStrategieGhostPinky implements FactoryStrategie{
    public FactoryStrategieGhostPinky(){}


    /**
     * Créer une stratégie Pinky
     */
    @Override
    public Strategie creerStrategie(Agent agent) {
        return new StrategieGhostPinky(agent);
    }
    
}
