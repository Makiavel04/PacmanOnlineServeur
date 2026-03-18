package Partie.Pacman.Agents.Factory;

import Partie.Pacman.Agents.AgentPacman;
import pacman.online.commun.moteur.PositionAgent;

/** fabrique de pacman */
public class FactoryPacman implements FactoryAgent{
    public FactoryPacman() {
        super();
    }

    /** Créer un pacman */
    @Override
    public AgentPacman creerAgent(PositionAgent pos) {
        return new AgentPacman(pos);
    };
}
