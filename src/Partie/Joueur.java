package Partie;

import Partie.Pacman.Agents.Agent;
import Partie.Pacman.Agents.TypeAgent;
import Ressources.EtatLobby.DetailsJoueur;

public interface Joueur {
    public void initJoueur(Agent agent);
    public TypeAgent getTypeAgent();

    public Agent getAgent();
    public void setAgent(Agent agent);

    public DetailsJoueur getDetailsJoueur();
}
