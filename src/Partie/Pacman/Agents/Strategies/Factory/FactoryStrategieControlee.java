package Partie.Pacman.Agents.Strategies.Factory;

import Partie.Pacman.Agents.Agent;
import Partie.Pacman.Agents.Strategies.Strategie;
import Partie.Pacman.Agents.Strategies.StrategieControlee;

/** Fabrique de stratégies controlée */
public class FactoryStrategieControlee implements FactoryStrategie{
    public FactoryStrategieControlee(){}

    /**
     * Créer une stratégie controlée
     */
    @Override
    public Strategie creerStrategie(Agent agent) {
        return new StrategieControlee(agent);
    }
    
}
