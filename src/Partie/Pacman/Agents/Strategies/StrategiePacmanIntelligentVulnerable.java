package Partie.Pacman.Agents.Strategies;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Queue;

import Partie.Pacman.Agents.Agent;
import Partie.Pacman.Agents.AgentAction;
import Partie.Pacman.Agents.AgentGhost;
import Partie.Pacman.Game.PacmanGame;
import Partie.Pacman.Helper.CalculDistance;
import pacman.online.commun.moteur.PositionAgent;

/** Stratégie de pacman intelligent quand il est vunérable */
public class StrategiePacmanIntelligentVulnerable extends Strategie {
    double fuitDepuis; //Compteur pour savoir depuis combien de temps on est fuit un fantome

    public StrategiePacmanIntelligentVulnerable(Agent agent){
        super(agent);
        this.fuitDepuis = 0.0;
    }

    /**
     * Choisi une action pour en fonction du contexte 
     *      - Aller vers nourriture
     *      - Fantôme proche => fuire
     *      - Fuit depuis trop longtemps => cherche une capsule
     * {@inheritDoc}
     */
    @Override
    public AgentAction choixAction(PacmanGame game) {
        ArrayList<AgentGhost> ghosts = game.getGhosts();
        ArrayList<PositionAgent> ghostProche;
        if (ghosts.isEmpty()) {
            ghostProche = null;
        }else{
            ghostProche = pathToGhostUnderDistance(game, 5);
        }

        if(fuitDepuis<20.0 && ghostProche != null){//pour eviter les boucles de fuites, si on fuit depuis trop longtemps, on arrete de fuir
            fuitDepuis += 1.0;
            return fuireGhost(game, ghostProche);//Fantome à 5 de distance, fuir
        }else if(fuitDepuis>=20.0 && ghostProche != null){ //Si on fuit depuis trop longtemps, on arrete de fuir et on tente d'aller vers une capsule
            return moveToCapsuleCarefully(game, ghostProche);
        }else{
            fuitDepuis -= 0.5; // plus lent si un fantôme reste proche
            fuitDepuis = Math.max(0, fuitDepuis);
            return moveToFood(game, false);//Sinon, avancer vers la nourriture
        }
    }

    /**
     * Donne l'action pour fuire
     * @param game
     * @param pathToGhost chemin vers le fantôme le plus proche
     * @return action à faire
     */
    public AgentAction fuireGhost(PacmanGame game, ArrayList<PositionAgent> pathToGhost) {
        PositionAgent ghostPos = pathToGhost.get(pathToGhost.size() - 1);

        int distMax = pathToGhost.size()-1; //Distance actuelle au fantome le plus proche
        AgentAction actionFuite = new AgentAction(AgentAction.STOP);

        for(int d : new int[]{0, 1, 2, 3}){//Test un move dans les 4 directions
            AgentAction action = new AgentAction(d);
            int newX = this.agent.getPosition().getX() + action.get_vx();
            int newY = this.agent.getPosition().getY() + action.get_vy();
            PositionAgent newPos = new PositionAgent(newX, newY, d);

            if(game.isLegalMove(action, this.agent.getPosition()) && !newPos.equals(pathToGhost.get(1))){//Si coup légal et pas vers le fantome
                //Calculer la distance au fantome le plus proche depuis cette nouvelle position
                int distance = CalculDistance.distanceReelEntre(newPos, ghostPos, game); //Distance depuis la nouvelle position au fantome le plus proche
                if(CalculDistance.distanceReelEntre(newPos, ghostPos, game) > distMax){
                    distMax = distance;
                    actionFuite = action;
                }
                
            }
        }
        return actionFuite;
    }

    /**
     * Donne l'action pour aller vers la nourriture
     * @param game
     * @param prioCapsule doit on aller en priorité vers une capsule
     * @return action à faire
     */
    public AgentAction moveToFood(PacmanGame game, boolean prioCapsule) {
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

            if(game.isFood(posActuel.getX(), posActuel.getY()) && (!prioCapsule || !game.areCapsuleRemaining())){
                //Food trouvée, retourner la première action du chemin
                System.out.println("Move"+ chemin.get(1).getX()+" "+chemin.get(1).getY());
                return actionVersCaseSuivante(startPos, chemin.get(1));
            }else if(game.isCapsule(posActuel.getX(), posActuel.getY())){
                //Food ou capsule trouvée, retourner la première action du chemin
                return actionVersCaseSuivante(startPos, chemin.get(1));
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
        System.err.println("STOP");
        return new AgentAction(AgentAction.STOP);
    }

    /**
     * Action pour aller vers une capsule en faisant quand même attention car on est chassé
     * @param game
     * @param pathToClosestGhost chemin vers le fantôme le plus proche
     * @return action à faire
     */
    public AgentAction moveToCapsuleCarefully(PacmanGame game, ArrayList<PositionAgent> pathToClosestGhost){
        AgentAction actionVersCapsule = moveToFood(game, true); //Cherche la capsule la plus proche, si aucune, va vers la nourriture
        PositionAgent nextPos = new PositionAgent(
            this.agent.getPosition().getX() + actionVersCapsule.get_vx(),
            this.agent.getPosition().getY() + actionVersCapsule.get_vy(),
            actionVersCapsule.get_direction()
        );

        if(nextPos.equals(pathToClosestGhost.get(1))){//Si la prochaine position est sur le chemin du fantome le plus proche
            AgentAction fuite = fuireGhost(game, pathToClosestGhost); //Fuir le fantome
            if(!((fuite.get_direction()==0 && this.agent.getPosition().getDir()==1) 
                || (fuite.get_direction()==1 && this.agent.getPosition().getDir()==0) 
                || (fuite.get_direction()==2 && this.agent.getPosition().getDir()==3) 
                || (fuite.get_direction()==3 && this.agent.getPosition().getDir()==2))){//Si ca revient a faire demitour n'avance a rien donc skip
                return fuite;
            }
        }
            
        return actionVersCapsule; //Aller vers la capsule
    }

    /**
     * Chemin vers un fantôme s'il y en a un dans le rayon
     * @param game 
     * @param maxDist rayon de recherche
     * @return chemin vers le fantôme s'il existe, null sinon
     */
    public ArrayList<PositionAgent> pathToGhostUnderDistance(PacmanGame game, int maxDist) {
        PositionAgent startPos = new PositionAgent(this.agent.getPosition());

        Queue<ArrayList<PositionAgent>> queue = new ArrayDeque<>();
        ArrayList<PositionAgent> cheminInit = new ArrayList<>();
        cheminInit.add(startPos);
        queue.add(cheminInit);

        boolean[][] visited = new boolean[game.getLabyrinthe().getSizeX()][game.getLabyrinthe().getSizeY()];
        visited[startPos.getX()][startPos.getY()] = true;

        while (!queue.isEmpty()) {
            ArrayList<PositionAgent> chemin = queue.poll();
            PositionAgent posActuel = chemin.get(chemin.size() - 1);

            if (chemin.size() - 1 > maxDist) continue;

            // Vérifier s’il y a un fantôme
            for (AgentGhost g : game.getGhosts()) {
                if (posActuel.equals(g.getPosition())) {
                    return chemin;
                }
            }

            // Explorer les voisins
            for (int d : new int[]{0, 1, 2, 3}) {
                AgentAction action = new AgentAction(d);
                int newX = posActuel.getX() + action.get_vx();
                int newY = posActuel.getY() + action.get_vy();

                if (game.isLegalMove(action, posActuel)
                    && !visited[newX][newY]) {

                    visited[newX][newY] = true;

                    ArrayList<PositionAgent> newChemin =
                        new ArrayList<>(chemin);
                    newChemin.add(
                        new PositionAgent(newX, newY, action.get_direction())
                    );

                    queue.add(newChemin);
                }
            }
        }

        return null; // Aucun fantôme à distance <= maxDist
    }

    /**
     * Définir l'action pour aller d'une case à une autre
     * @param startPos position actuelle
     * @param nextPos position où on veut aller
     * @return action à faire
     */
    public AgentAction actionVersCaseSuivante(PositionAgent startPos, PositionAgent nextPos) {    
        int dx = nextPos.getX() - startPos.getX();
        int dy = nextPos.getY() - startPos.getY();
        if (dx == 1) return new AgentAction(AgentAction.EAST);
        else if (dx == -1) return new AgentAction(AgentAction.WEST);
        else if (dy == 1) return new AgentAction(AgentAction.SOUTH);
        else if (dy == -1) return new AgentAction(AgentAction.NORTH);
        else return new AgentAction(AgentAction.STOP); // Pas de mouvement
    }

    @Override
    public void onKeyPressed(int keyCode) {} // Ne fait rien, l'agent reste immobile
    
    /**
     * Passe en chasse aux fantômes avec une capsule
     * {@inheritDoc}
     */
    @Override
    public void capsuleActivee(){
        this.agent.setStrategie(new StrategiePacmanIntelligentCapsule(this.agent));
    }
    @Override
    public void capsuleDesactivee(){} //Capsule déjà inactive en mode prudent
    
}
