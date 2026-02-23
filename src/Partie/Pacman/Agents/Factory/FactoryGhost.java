package Partie.Pacman.Agents.Factory;

import Partie.Pacman.Agents.AgentGhost;
import Partie.Pacman.Agents.PositionAgent;

/** fabrique de fantômes */
public class FactoryGhost implements FactoryAgent{
    public FactoryGhost() {
        super();
    }

    /** Créer un fantôme */
    @Override
    public AgentGhost creerAgent(PositionAgent pos) {
        return new AgentGhost(pos);
    };
}
