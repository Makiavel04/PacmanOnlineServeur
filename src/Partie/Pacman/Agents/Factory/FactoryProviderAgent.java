package Partie.Pacman.Agents.Factory;

import java.util.Map;

import Partie.Pacman.Agents.TypeAgent;

/** Fournisseur de fabrique d'agents */
public class FactoryProviderAgent {
    /** Liste des fabriques */
    private Map<TypeAgent, FactoryAgent> factories;

    public FactoryProviderAgent(){
        this.factories = Map.of(
            TypeAgent.PACMAN, new FactoryPacman(),
            TypeAgent.FANTOME, new FactoryGhost()
        );
    }

    /**
     * Récupérer un type de fabrique
     * @param typeAgent type de la fabrique
     * @return fabrique
     */
    public FactoryAgent getFactory(TypeAgent typeAgent){
        return factories.get(typeAgent);
    }
}
