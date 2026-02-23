package Partie.Pacman.Agents.Strategies.Factory;

import Partie.Pacman.Agents.Agent;
import Partie.Pacman.Agents.Strategies.Strategie;
import Partie.Pacman.Agents.Strategies.StrategieGhostIntelligentChasse;

/** Fabrique de stratégies fantômes intelligents */
public class FactoryStrategieGhostIntelligent implements FactoryStrategie {

    public FactoryStrategieGhostIntelligent(){}

    /**
     * Créer une stratégie fantômes intelligents
     */
    @Override
    public Strategie creerStrategie(Agent agent) {
        return new StrategieGhostIntelligentChasse(agent); //La strat ghost en fuite n'est pas une strat de départ
    }
    
}
