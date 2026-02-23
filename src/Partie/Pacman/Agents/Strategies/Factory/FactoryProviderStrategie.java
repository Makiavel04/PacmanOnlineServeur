package Partie.Pacman.Agents.Strategies.Factory;

import java.util.Map;

import Partie.Pacman.Agents.Strategies.TypeStrategie;

/** Fournisseur de fabrique de stratégies */
public class FactoryProviderStrategie {
    /** Liste des fabriques */
    private Map<TypeStrategie, FactoryStrategie> factories;

    public FactoryProviderStrategie(){
        this.factories = Map.of(
            TypeStrategie.ALEATOIRE, new FactoryStrategieAleatoire(),
            TypeStrategie.IMMOBILE,new FactoryStrategieImmobile(),
            TypeStrategie.CONTROLEE1, new FactoryStrategieControlee(),
            TypeStrategie.CONTROLEE2, new FactoryStrategieControleeBis(),
            TypeStrategie.PACMAN_INTELLIGENT, new FactoryStrategiePacmanIntelligent(),
            TypeStrategie.GHOST_BLINKY, new FactoryStrategieGhostBlinky(),
            TypeStrategie.GHOST_CLYDE, new FactoryStrategieGhostClyde(),
            TypeStrategie.GHOST_INKY, new FactoryStrategieGhostInky(),
            TypeStrategie.GHOST_PINKY, new FactoryStrategieGhostPinky(),
            TypeStrategie.GHOST_INTELLIGENT, new FactoryStrategieGhostIntelligent()
        );
    }


    /**
     * Récupérer un type de fabrique
     * @param typeStrat type de la fabrique
     * @return fabrique
     */
    public FactoryStrategie getFactory(TypeStrategie typeStrat){
        return factories.get(typeStrat);
    }
}
