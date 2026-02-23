package Partie.Pacman.Agents.Strategies.Factory;

import Partie.Pacman.Agents.Agent;
import Partie.Pacman.Agents.Strategies.Strategie;
import Partie.Pacman.Agents.Strategies.StrategiePacmanIntelligentVulnerable;

/** Fabrique de stratégies pacman intelligents */
public class FactoryStrategiePacmanIntelligent implements FactoryStrategie {

    public FactoryStrategiePacmanIntelligent(){}

    /**
     * Créer une stratégie pacman intelligent
     */
    @Override
    public Strategie creerStrategie(Agent agent) {
        return new StrategiePacmanIntelligentVulnerable(agent); //La strat pacman sous capsule n'est pas une strat de départ
    }

    
}
