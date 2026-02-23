package Partie.Pacman.Agents.Strategies.Factory;

import Partie.Pacman.Agents.Agent;
import Partie.Pacman.Agents.Strategies.Strategie;
import Partie.Pacman.Agents.Strategies.StrategieImmobile;

/** Fabrique de stratégies immobiles */
public class FactoryStrategieImmobile implements FactoryStrategie{
    public FactoryStrategieImmobile(){}

    /**
     * Créer une stratégie immobile
     */
    @Override
    public Strategie creerStrategie(Agent agent) {
        return new StrategieImmobile(agent);
    }
}
