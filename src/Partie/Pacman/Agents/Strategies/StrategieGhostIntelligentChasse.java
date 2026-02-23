package Partie.Pacman.Agents.Strategies;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Queue;

import Partie.Pacman.Agents.Agent;
import Partie.Pacman.Agents.AgentAction;
import Partie.Pacman.Agents.AgentPacman;
import Partie.Pacman.Agents.PositionAgent;
import Partie.Pacman.Game.PacmanGame;

/**Stratégie de chasse pour fantôme */
public class StrategieGhostIntelligentChasse extends Strategie {
    
    public StrategieGhostIntelligentChasse(Agent agent){
        super(agent);
    }

    /**
     * Choisie une action pour aller vers un pacman
     * {@inheritDoc}
     */
    public AgentAction choixAction(PacmanGame game){
        //BFS pour trouver le chemin le plus court vers Pacman
        ArrayList<AgentPacman> pacmans = game.getPacmans();
        if (pacmans.isEmpty()) {
            return new StrategieAleatoire(this.agent).choixAction(game); // Pas de Pacman à chasser
        }

        return moveCloser(game, pacmans);
    }
    
    /**
     * Donne une action vers le pacman le plus proche
     * @param game 
     * @param pacmans liste des pacmans du jeu
     * @return action à faire
     */
    public AgentAction moveCloser(PacmanGame game, ArrayList<AgentPacman> pacmans) {        
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
                    if(chemin.size() < 2) return new AgentAction(AgentAction.STOP);//Déjà dessus

                    PositionAgent firstStep = chemin.get(1);
                    int dx = firstStep.getX() - startPos.getX();
                    int dy = firstStep.getY() - startPos.getY();
                    if(dx == 1) return new AgentAction(AgentAction.EAST);
                    else if(dx == -1) return new AgentAction(AgentAction.WEST);
                    else if(dy == 1) return new AgentAction(AgentAction.SOUTH);
                    else if(dy == -1) return new AgentAction(AgentAction.NORTH);
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
        return new AgentAction(AgentAction.STOP);
    }

    public void onKeyPressed(int keyCode){}

    /**
     * Fuit quand la capsule s'active
     * {@inheritDoc}
     */
    public void capsuleActivee(){
        this.agent.setStrategie(new StrategieGhostIntelligentFuite(this.agent, this));
    }
    public void capsuleDesactivee(){} //Déjà dans la strat de chasse quand capsule désactivée
}
