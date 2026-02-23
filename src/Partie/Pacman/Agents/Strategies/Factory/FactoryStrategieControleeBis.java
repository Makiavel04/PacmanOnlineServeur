package Partie.Pacman.Agents.Strategies.Factory;

import Partie.Pacman.Agents.Agent;
import Partie.Pacman.Agents.Strategies.Strategie;
import Partie.Pacman.Agents.Strategies.StrategieControleeBis;

/** Fabrique de stratégies controlée bis */
public class FactoryStrategieControleeBis implements FactoryStrategie{
    public FactoryStrategieControleeBis(){}

    /**
     * Créer une stratégie controlée bis
     */
    @Override
    public Strategie creerStrategie(Agent agent) {
        return new StrategieControleeBis(agent);
    }
    
}
