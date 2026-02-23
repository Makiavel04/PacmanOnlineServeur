package Partie.Pacman.Agents.Strategies.Factory;

import Partie.Pacman.Agents.Agent;
import Partie.Pacman.Agents.Strategies.Strategie;

/** interface de fabrique de stratégies */
public interface FactoryStrategie {
    /** Créer une stratégie */
    public Strategie creerStrategie(Agent agent);
}
