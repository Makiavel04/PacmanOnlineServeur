package Partie.Pacman.Agents.Factory;

import Partie.Pacman.Agents.Agent;
import pacman.online.commun.moteur.PositionAgent;

/** Interface de fabrique d'agents */
public interface FactoryAgent {
    /** Créer un agent */
    public Agent creerAgent(PositionAgent pos);
}
