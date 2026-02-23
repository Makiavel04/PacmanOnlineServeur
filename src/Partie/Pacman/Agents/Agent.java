package Partie.Pacman.Agents;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Queue;

import Partie.Pacman.Agents.Strategies.Strategie;
import Partie.Pacman.Game.PacmanGame;

/** Personnage du jeu */
public abstract class Agent {
    /** Position actuel */
    protected PositionAgent posActuel;
    /** Position de départ / spawn */
    protected PositionAgent posInit;
    /** Stratégie de l'agent */
    protected Strategie strat;
    /** Agent vivant ou non */
    protected boolean mort;

    public Agent(PositionAgent pos){
        this.posActuel = pos;
        this.posInit = new PositionAgent(pos.getX(), pos.getY(), pos.getDir());
        this.mort = false;
    }

    public PositionAgent getPosition(){
        return this.posActuel;
    }
    public void setPosition(PositionAgent p){
        this.posActuel = p;
    }
    
    public Strategie getStrategie(){
        return this.strat;
    }
    public void setStrategie(Strategie s){
        this.strat = s;
    }

    public boolean isMort(){
        return this.mort;
    }
    public void setMort(boolean m){
        this.mort = m;
    }

    /**
     * Choisir l'action à faire
     * @param game 
     * @return action choisie
     */
    public AgentAction choisirAction(PacmanGame game){
        return this.strat.choixAction(game);
    }

    /**
     * Simule le déplacement
     * @param action action à simuler
     * @return position d'arrivée simulée
     */
    public PositionAgent simulerAction(AgentAction action){
        int newX = this.posActuel.getX() + action.get_vx();
        int newY = this.posActuel.getY() + action.get_vy();
        return new PositionAgent(newX, newY, action.get_direction());
    }

    /**
     * Déplace l'agent
     * @param action action pour se déplacer
     */
    public void effectuerAction(AgentAction action){
        this.posActuel.setDir(action.get_direction());
        this.posActuel.setX(this.posActuel.getX() + action.get_vx());
        this.posActuel.setY(this.posActuel.getY() + action.get_vy());
    }

    /**
     * Change de stratégie avec les changements de capsule si besoin
     * @param active
     */
    public void capsuleChanged(boolean active){
        if (active) {
            this.strat.capsuleActivee();
        } else {
            this.strat.capsuleDesactivee();
        }
    }

    /**
     * Indique si l'agent est vulnérable
     * @param game 
     * @return Vrai / Faux
     */
    public abstract boolean isVulnerable(PacmanGame game);
    /**
     * Indique si l'agent peut manger de la nourriture au sol
     * @return Vrai / Faux
     */
    public abstract boolean canEatFood();
    /**
     * Indique si l'agent peut réapparaitre
     * @param game
     * @return Vrai / Faux
     */
    public abstract boolean peutReapparaitre(PacmanGame game);
    /**
     * Procedure de réapparition
     * @param game
     */
    public abstract void respawn(PacmanGame game);

    /**
     * Renvoie les cases libres pour une réapparition autour du spawn de l'agent
     * @param game Jeu
     * @param range distance de recherche au spawn original
     * @return liste de positions possibles
     */
    public ArrayList<PositionAgent> getPositionsPossiblesAutour(PacmanGame game, int range){
        ArrayList<PositionAgent> positions = new ArrayList<>();
        positions.add(posInit);
        Queue<SimpleEntry<PositionAgent, Integer>> queue = new ArrayDeque<>();
        queue.add(new SimpleEntry<PositionAgent, Integer>(posInit, 0));

        boolean[][] visited = new boolean
            [game.getLabyrinthe().getSizeX()]
            [game.getLabyrinthe().getSizeY()];
        visited[posInit.getX()][posInit.getY()] = true;

        while (!queue.isEmpty()) {
            SimpleEntry<PositionAgent, Integer> tete = queue.poll();
            PositionAgent posActuel = tete.getKey();
            Integer dist = tete.getValue();

            if (dist > range) continue; //comme on fait du BFS, si on arrive a cette distance, on ne repassera pas en dessous.

            // Explorer les voisins
            for (int d : new int[]{0, 1, 2, 3}) {
                AgentAction action = new AgentAction(d);
                int newX = posActuel.getX() + action.get_vx();
                int newY = posActuel.getY() + action.get_vy();

                if (!visited[newX][newY] && game.isLegalMove(action, posActuel) && !game.agentSurCase(newX, newY) && !game.isMur(newX, newY)) {
                    visited[newX][newY] = true;
                    SimpleEntry<PositionAgent, Integer> newPos = new SimpleEntry<>(new PositionAgent(newX, newY, action.get_direction()), dist+1);

                    positions.add(newPos.getKey());
                    queue.add(newPos);
                }
            }
        }

        return positions;
    }
}
