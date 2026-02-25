package Partie;

import Partie.Pacman.Agents.Agent;
import Partie.Pacman.Agents.TypeAgent;
import Partie.Pacman.Agents.Strategies.Strategie;
import Partie.Pacman.Agents.Strategies.TypeStrategie;
import Ressources.EtatLobby.DetailsJoueur;

public interface Joueur {
    public void initJoueur(Agent agent);
    public TypeAgent getTypeAgent();
    public TypeStrategie getTypeStrategie();
    public Strategie getStrategie();

    public DetailsJoueur getDetailsJoueur();
}
