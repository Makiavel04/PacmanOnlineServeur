package Partie.Pacman.Agents.Strategies;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Queue;

import Partie.Pacman.Agents.Agent;
import Partie.Pacman.Agents.AgentAction;
import Partie.Pacman.Agents.AgentGhost;
import Partie.Pacman.Agents.PositionAgent;
import Partie.Pacman.Game.PacmanGame;

/** Stratégie de chasse aux fantômes quand on est sous capsule */
public class StrategiePacmanIntelligentCapsule extends Strategie{

    public StrategiePacmanIntelligentCapsule(Agent agent){
        super(agent);
    }

    /**
     * Choisie une action pour chasser les fantômes
     * {@inheritDoc}
     */
    @Override
    public AgentAction choixAction(PacmanGame game) {
        //BFS pour trouver le chemin le plus court vers Pacman
        ArrayList<AgentGhost> ghosts = game.getGhosts();
        if (ghosts.isEmpty()) {
            //si pas de ghost on repasse en strat classique
            this.agent.setStrategie(new StrategiePacmanIntelligentVulnerable(agent));
            return this.agent.choisirAction(game); // Pas de Pacman à fuire
        }

        return moveCloser(game, ghosts);
    }
    
    /**
     * Action pour se diriger vers un fantôme
     * @param game
     * @param ghosts fantômes du jeu
     * @return action à faire
     */
    public AgentAction moveCloser(PacmanGame game, ArrayList<AgentGhost> ghosts) {        
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

            //Vérifier si on est sur un fantôme
            for(AgentGhost g : ghosts){
                if(posActuel.equals(g.getPosition())){
                    //fantôme trouvé, retourner la première action du chemin
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

    @Override
    public void onKeyPressed(int keyCode) {}
    
    @Override
    public void capsuleActivee(){} //Capsule déjà active en mode mangeur

    /**
     * Passe en fuite sans capsule
     * {@inheritDoc}
     */
    @Override
    public void capsuleDesactivee(){
        this.agent.setStrategie(new StrategiePacmanIntelligentVulnerable(this.agent));
    }
}
