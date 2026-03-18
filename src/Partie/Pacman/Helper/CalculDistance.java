package Partie.Pacman.Helper;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Queue;

import Partie.Pacman.Agents.AgentAction;
import Partie.Pacman.Agents.AgentGhost;
import Partie.Pacman.Agents.AgentPacman;
import Partie.Pacman.Game.PacmanGame;
import pacman.online.commun.moteur.PositionAgent;

/**Ensemble de fonctions de calcul ou vérification de distance */
public class CalculDistance {
    /**
     * Calcul la distance réel entre 2 positions
     * @param a position
     * @param b position
     * @param game jeu où regarder
     * @return distance
     */
    public static int distanceReelEntre(PositionAgent a, PositionAgent b, PacmanGame game){

        Queue<ArrayList<PositionAgent>> queue = new ArrayDeque<>();
        ArrayList<PositionAgent> cheminInit = new ArrayList<>();
        cheminInit.add(a);
        queue.add(new ArrayList<>(cheminInit));

        boolean[][] visited = new boolean[game.getLabyrinthe().getSizeX()][game.getLabyrinthe().getSizeY()];
        visited[a.getX()][a.getY()] = true;

        while(!queue.isEmpty()){
            ArrayList<PositionAgent> chemin = queue.poll();
            PositionAgent posActuel = chemin.get(chemin.size() - 1);

            //Vérifier si on est arrivé à b
            if(posActuel.equals(b)){
                return chemin.size() - 1; //Retourne la distance (nombre de déplacements)
            }
            

            //Explorer les voisins
            for(int d : new int[]{0, 1, 2, 3}){
                AgentAction action = new AgentAction(d);
                int newX = posActuel.getX() + action.get_vx();
                int newY = posActuel.getY() + action.get_vy();
                if(game.isLegalMove(action, posActuel) && !visited[newX][newY]){
                    ArrayList<PositionAgent> newChemin = new ArrayList<>(chemin);//Récupérer le chemin actuel
                    newChemin.add(new PositionAgent(newX, newY, action.get_direction()));//Ajoute un déplacement possible depuis ce chemin
                    queue.add(newChemin); // Ajoute le nouveau chemin à la file d'attente
                    visited[newX][newY] = true; // Marque comme visité
                }
            }
        }
        
        return -1;
    }

    /**
     * Calcul la distance Manhattan entre 2 position
     * @param a position
     * @param b position
     * @param game jeu où regarder
     * @return distance
     */
    public static int distanceManhattanEntre(PositionAgent a, PositionAgent b, PacmanGame game){
        return Math.abs(a.getX()-b.getX()) + Math.abs(a.getY()-b.getY());
    }

    /**
     * Indique s'il y a un fantome dans un rayon autour d'une position
     * @param game jeu où regarder
     * @param startPos position de départ
     * @param maxDist rayon max de recherche
     * @return Vrai / Faux
     */
    public static boolean isThereGhostUnderDistance(PacmanGame game, PositionAgent startPos, int maxDist) {
        Queue<SimpleEntry<PositionAgent, Integer>> queue = new ArrayDeque<>();
        queue.add(new SimpleEntry<PositionAgent, Integer>(startPos, 0));

        boolean[][] visited = new boolean
            [game.getLabyrinthe().getSizeX()]
            [game.getLabyrinthe().getSizeY()];
        visited[startPos.getX()][startPos.getY()] = true;

        while (!queue.isEmpty()) {
            SimpleEntry<PositionAgent, Integer> tete = queue.poll();
            PositionAgent posActuel = tete.getKey();
            Integer dist = tete.getValue();

            if (dist > maxDist) continue; //comme on fait du BFS, si on arrive a cette distance, on ne repassera pas en dessous.

            // Vérifier s’il y a un fantôme
            for (AgentGhost g : game.getGhosts()) {
                if (posActuel.equals(g.getPosition())) {
                    return true;
                }
            }

            // Explorer les voisins
            for (int d : new int[]{0, 1, 2, 3}) {
                AgentAction action = new AgentAction(d);
                int newX = posActuel.getX() + action.get_vx();
                int newY = posActuel.getY() + action.get_vy();

                if (game.isLegalMove(action, posActuel) && !visited[newX][newY]) {
                    visited[newX][newY] = true;
                    SimpleEntry<PositionAgent, Integer> newPos = new SimpleEntry<>(new PositionAgent(newX, newY, action.get_direction()), dist+1);

                    queue.add(newPos);
                }
            }
        }

        return false; // Aucun pacman à distance <= maxDist
    }

    /**
     * Indique s'il y a un pacman dans un rayon autour d'une position
     * @param game jeu où regarder
     * @param startPos position de départ
     * @param maxDist rayon max de recherche
     * @return Vrai / Faux
     */
    public static boolean isTherePacmanUnderDistance(PacmanGame game, PositionAgent startPos, int maxDist) {
        Queue<SimpleEntry<PositionAgent, Integer>> queue = new ArrayDeque<>();
        queue.add(new SimpleEntry<PositionAgent, Integer>(startPos, 0));

        boolean[][] visited = new boolean
            [game.getLabyrinthe().getSizeX()]
            [game.getLabyrinthe().getSizeY()];
        visited[startPos.getX()][startPos.getY()] = true;

        while (!queue.isEmpty()) {
            SimpleEntry<PositionAgent, Integer> tete = queue.poll();
            PositionAgent posActuel = tete.getKey();
            Integer dist = tete.getValue();

            if (dist > maxDist) continue; //comme on fait du BFS, si on arrive a cette distance, on ne repassera pas en dessous.

            // Vérifier s’il y a un fantôme
            for (AgentPacman p : game.getPacmans()) {
                if (posActuel.equals(p.getPosition())) {
                    return true;
                }
            }

            // Explorer les voisins
            for (int d : new int[]{0, 1, 2, 3}) {
                AgentAction action = new AgentAction(d);
                int newX = posActuel.getX() + action.get_vx();
                int newY = posActuel.getY() + action.get_vy();

                if (game.isLegalMove(action, posActuel) && !visited[newX][newY]) {
                    visited[newX][newY] = true;
                    SimpleEntry<PositionAgent, Integer> newPos = new SimpleEntry<>(new PositionAgent(newX, newY, action.get_direction()), dist+1);

                    queue.add(newPos);
                }
            }
        }

        return false; // Aucun pacman à distance <= maxDist
    }
}
