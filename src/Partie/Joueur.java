package Partie;

import Partie.Pacman.Agents.Agent;
import Partie.Pacman.Agents.TypeAgent;
import Partie.Pacman.Agents.Strategies.Strategie;
import Partie.Pacman.Agents.Strategies.TypeStrategie;
import Ressources.EtatLobby.DetailsJoueur;

/**
 * Interface représentant un joueur dans une partie de Pacman.
 */
public interface Joueur {
    /**
     * Initialise le joueur avec un agent.
     * @param agent L'agent associé au joueur.
     */
    public void initJoueur(Agent agent);

    public TypeAgent getTypeAgent();
    public TypeStrategie getTypeStrategie();
    public Strategie getStrategie();

    /**
     * Récupère les détails du joueur pour l'affichage dans le lobby.
     * @return Les détails du joueur.
     */
    public DetailsJoueur getDetailsJoueur();
}
