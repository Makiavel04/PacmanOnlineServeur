package Partie.Pacman.Agents.Strategies;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Queue;

import Partie.Pacman.Agents.Agent;
import Partie.Pacman.Agents.AgentAction;
import Partie.Pacman.Agents.AgentPacman;
import Partie.Pacman.Game.PacmanGame;
import Partie.Pacman.Helper.CalculDistance;
import pacman.online.commun.moteur.PositionAgent;

/**Stratégie de fuite pour fantôme */
public class StrategieGhostIntelligentFuite extends Strategie{
    Strategie lastStrat;

    public StrategieGhostIntelligentFuite(Agent agent, Strategie s){
        super(agent);
        this.lastStrat = s;
    }

    /**
     * Choisie une action pour fuire un pacman
     * {@inheritDoc}
     */
    @Override
    public AgentAction choixAction(PacmanGame game){
        ArrayList<AgentPacman> pacmans = game.getPacmans();
        if (pacmans.isEmpty()) {
            return new StrategieAleatoire(this.agent).choixAction(game);
        }

        return moveAway(game, pacmans);
    }

    /**
     * Donne une action pour fuire un pacman
     * @param game
     * @param pacmans pacmans du jeu
     * @return action à faire
     */
    public AgentAction moveAway(PacmanGame game, ArrayList<AgentPacman> pacmans) {
        ArrayList<PositionAgent> pathToClosest = pathToClosestPacman(game, pacmans);
        if(pathToClosest.size() < 2){
            return new AgentAction(AgentAction.STOP); //Déjà dessus ou pas de chemin
        }

        AgentAction actionAway = new AgentAction(AgentAction.STOP);
        int maxDistance = pathToClosest.size()-1; //Distance actuelle au Pacman le plus proche

        for(int d : new int[]{0, 1, 2, 3}){
            AgentAction action = new AgentAction(d);
            int newX = this.agent.getPosition().getX() + action.get_vx();
            int newY = this.agent.getPosition().getY() + action.get_vy();
            PositionAgent newPos = new PositionAgent(newX, newY, d);

            if(game.isLegalMove(action, this.agent.getPosition()) && !newPos.equals(pathToClosest.get(1))){
                //Calculer la distance au Pacman le plus proche depuis cette nouvelle position
                int distance = CalculDistance.distanceReelEntre(newPos, pathToClosest.get(pathToClosest.size()-1), game); //Distance depuis la nouvelle position au Pacman le plus proche
                if(CalculDistance.distanceReelEntre(newPos, pathToClosest.get(pathToClosest.size()-1), game) > maxDistance){
                    maxDistance = distance;
                    actionAway = action;
                }
                
            }
        }

        return actionAway;
    }

    /**
     * Renvoie le chemin vers le pacman le plus proche
     * @param game 
     * @param pacmans pacmans du jeu
     * @return le chemin
     */
    public ArrayList<PositionAgent> pathToClosestPacman(PacmanGame game, ArrayList<AgentPacman> pacmans){
        PositionAgent startPos = new PositionAgent(this.agent.getPosition());

        Queue<ArrayList<PositionAgent>> queue = new ArrayDeque<>();
        ArrayList<PositionAgent> cheminInit = new ArrayList<>();
        cheminInit.add(startPos);
        queue.add(new ArrayList<>(cheminInit));

        boolean[][] visited = new boolean[game.getLabyrinthe().getSizeX()][game.getLabyrinthe().getSizeY()];
        visited[startPos.getX()][startPos.getY()] = true;

        while(!queue.isEmpty()){
            ArrayList<PositionAgent> chemin = queue.poll();
            PositionAgent posActuel = chemin.get(chemin.size() - 1);

            //Vérifier si on est sur un Pacman
            for(AgentPacman p : pacmans){
                if(posActuel.equals(p.getPosition())){
                    //Pacman trouvé, retourner la première action du chemin
                    if(chemin.size() < 2) return new ArrayList<>();//Déjà dessus

                    return chemin;
                }
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
        
        return new ArrayList<>();
    }    

    @Override
    public void onKeyPressed(int keyCode){}

    @Override
    public void capsuleActivee(){} //Déjà dans la strat de fuite quand capsule active

    /**
     * Repasse à la stratégie précédente quand la capsule se désactive
     * {@inheritDoc}
     */
    @Override
    public void capsuleDesactivee(){
        this.agent.setStrategie(lastStrat);
    }
}
