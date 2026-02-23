package Partie.Pacman.Agents.Strategies.Factory;

import Partie.Pacman.Agents.Agent;
import Partie.Pacman.Agents.Strategies.Strategie;
import Partie.Pacman.Agents.Strategies.StrategieAleatoire;

/** Fabrique de stratégies aléatoire */
public class FactoryStrategieAleatoire implements FactoryStrategie{
    public FactoryStrategieAleatoire(){}

    /**
     * Créer une stratégie aléatoire
     */
    @Override
    public Strategie creerStrategie(Agent agent) {
        return new StrategieAleatoire(agent);
    }
    
}
