package Partie.Pacman.Game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.stream.Collectors;

import Partie.Joueur;
import Partie.Lobby;
import Partie.Pacman.Agents.Agent;
import Partie.Pacman.Agents.AgentAction;
import Partie.Pacman.Agents.AgentGhost;
import Partie.Pacman.Agents.AgentPacman;
import Partie.Pacman.Agents.Factory.FactoryProviderAgent;
import Partie.Pacman.Agents.Strategies.Strategie;
import Partie.Pacman.Agents.Strategies.TypeStrategie;
import Partie.Pacman.Agents.Strategies.Factory.FactoryProviderStrategie;
import pacman.online.commun.dto.game.EtatPacmanGame;
import pacman.online.commun.moteur.EtatAgent;
import pacman.online.commun.moteur.Maze;
import pacman.online.commun.moteur.PositionAgent;
import pacman.online.commun.moteur.TypeAgent;

/**
 * Jeu de pacman
 */
public class PacmanGame extends Game{
    /** Bonnus fantome si fin sous cette barre */
    public static final int TOUR_BONUS = 200;
    
    /** Bonus par vies restantes aux Pacmans */
    public static final int BONUS_VIE = 75;

    /** Labyrinthe du jeu */
    private Maze labyrinthe;
    private String nomLabyrinthe;

    /** Liste des agents sur le plateau */
    private ArrayList<Agent> listeAgents; 
    /** Actions de agents qui peuvent jouer */
    private HashMap<Agent, AgentAction> actionsAgents;

    private int dureeCapsule; //Coopération => durée capsule partagée entre tous les pacmans
    private int scorePacman; //Coopération => score partagé entre tous les pacmans
    private int nbViesPacman; //Nombre de vies total des pacmans (coopération)

    private int scoreGhost; //Coopération => score des ghosts (nombre de pacmans mangés)

    private TypeAgent vainqueur; //Indique le vainqueur de la partie (pacman, fantome ou nul)

    /** Fournisseur de constructeurs d'agents */
    private FactoryProviderAgent factoriesAgents;
    /** Fournisseur de constructeurs de stratégies */
    private FactoryProviderStrategie factoriesStrategies;

    /**
     * Création d'un jeu de pacman
     * @param maxTurn
     */
    public PacmanGame(int maxTurn, Lobby lobby) {
        super(maxTurn, lobby);
        this.labyrinthe = null;
        this.nomLabyrinthe = null;
        this.listeAgents = new ArrayList<>();
        this.actionsAgents = new HashMap<>();
        this.dureeCapsule = 0;
        this.factoriesAgents = new FactoryProviderAgent();
        this.factoriesStrategies = new FactoryProviderStrategie();
    }
    
    public Maze getLabyrinthe(){return this.labyrinthe;}
    public void setLabyrinthe(Maze l){
        this.labyrinthe = l;
    }
    public void setNomLabyrinthe(String nom){
        this.nomLabyrinthe = nom;
    }
    public String getNomLabyrinthe(){
        return this.nomLabyrinthe;
    }

    public TypeAgent getVainqueur() {
        return this.vainqueur;
    }
    public void setVainqueur(TypeAgent vainqueur) {
        this.vainqueur = vainqueur;
    }

    /**
     * Remise à zero du labyrinthe
     */
    public void resetLabyrinthe(){
        if(this.nomLabyrinthe!=null){
            try{
                this.setLabyrinthe(new Maze(this.nomLabyrinthe));
            }catch(Exception e){
                System.out.println("Erreur lors du reset du labyrinthe : " + e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }

    public int getNbViesPacman() {
        return this.nbViesPacman;
    }

    /**
     * Remise à zero du nombre de vies
     */
    public void resetViesPacman(){
        this.nbViesPacman = 3;
    }

    public int getScorePacman(){
        return this.scorePacman;
    }

    /**
     * Remise à zero du score
     */
    public void resetScorePacman(){
        this.scorePacman = 0;
    }
    
    /**
     * Ajout de points au score
     * @param s points à ajouter
     */
    public void addScorePacman(int s){
        this.scorePacman += s;
        System.out.println("Score Pacman : " + this.scorePacman);
    }

    public int getScoreGhost() {
        return this.scoreGhost;
    }

    /**
     * Remise à zero du score des ghosts
     */
    public void resetScoreGhost(){
        this.scoreGhost = 0;
    }   

    /**
     * Ajout de points au score des ghosts
     * @param s points à ajouter
     */
    public void addScoreGhost(int s){
        this.scoreGhost += s;
        System.out.println("Score Ghost : " + this.scoreGhost);
    }
    
    /**
     * Remise à zero de la durée de la capsule
     */
    public void resetCapsule(){
        this.dureeCapsule = 0;
    }

    /**
     * Reset des différents compteurs et des agents. 
     * {@inheritDoc}
     */
    @Override
    public void initializeGame() {
        this.listeAgents = new ArrayList<>();
        this.actionsAgents = new HashMap<>();
        this.setVainqueur(null);
        this.resetScorePacman();
        this.resetScoreGhost();
        this.resetViesPacman();
        this.resetCapsule();
        this.resetLabyrinthe();

        Vector<Joueur> jPacmans = this.lobby.getPacmans();
        Vector<Joueur> jFantomes = this.lobby.getFantomes();

        for(int i=0; i<this.labyrinthe.getGhosts_start().size();  ++i){
            PositionAgent p = this.labyrinthe.getGhosts_start().get(i); //Position de départ dans la map
            Agent g = factoriesAgents.getFactory(TypeAgent.FANTOME).creerAgent(p); //Création de l'agent
            Strategie s = factoriesStrategies.getFactory(jFantomes.get(i).getTypeStrategie()).creerStrategie(g); //Strat selon type de strat du joueur
            g.setStrategie(s); //Affectation de la strat à l'agent

            jFantomes.get(i).initJoueur(g); //Initialisation du joueur avec l'agent
            g.setJoueur(jFantomes.get(i));
            addAgent(g); //Ajout de l'agent à la liste d'agents
        }
        for(int i=0; i<this.labyrinthe.getPacman_start().size(); ++i){
            PositionAgent p = this.labyrinthe.getPacman_start().get(i); //Position de départ dans la map
            Agent m = factoriesAgents.getFactory(TypeAgent.PACMAN).creerAgent(p); //Création de l'agent
            Strategie s = factoriesStrategies.getFactory(jPacmans.get(i).getTypeStrategie()).creerStrategie(m); //Strat selon type de strat du joueur
            m.setStrategie(s); //Affectation de la strat à l'agent
    
            jPacmans.get(i).initJoueur(m); //Initialisation du joueur avec l'agent
            m.setJoueur(jPacmans.get(i)); //Affectation du joueur à l'agent
            addAgent(m); //Ajout de l'agent à la liste d'agents
        }

        //this.actualiserLabyrinthe(); //Actualisation du labyrinthe pour prendre en compte les agents

        ArrayList<EtatAgent> etatsP = getPacmansEvenDead().stream()
            .map(Agent::getEtatAgent)
            .collect(Collectors.toCollection(ArrayList::new));
        this.labyrinthe.setEtat_pacmans(etatsP);

        ArrayList<EtatAgent> etatsG = getGhostsEvenDead().stream()
            .map(Agent::getEtatAgent)
            .collect(Collectors.toCollection(ArrayList::new));
        this.labyrinthe.setEtat_ghosts(etatsG);
        System.out.println("Jeu initialisé.");
    }

    /**
     * Ajouter un agent à la liste d'agents
     * @param a agent à ajouter
     */
    public void addAgent(Agent a){
        this.listeAgents.add(a);
    }

    /**
     * Récuperer l'ensemble des pacmans en vie
     * @return liste des pacmans en vie
     */
    public ArrayList<AgentPacman> getPacmans(){
        ArrayList<AgentPacman> listePacmans = new ArrayList<>();
        for(Agent a : this.listeAgents){
            if((!a.isMort()) && (a instanceof AgentPacman)){
                listePacmans.add((AgentPacman) a);
            }
        }
        return listePacmans;    
    }

    /**
     * Récuperer tous les pacmans 
     * @return liste des pacmans 
     */
    public ArrayList<AgentPacman> getPacmansEvenDead(){
        ArrayList<AgentPacman> listePacmans = new ArrayList<>();
        for(Agent a : this.listeAgents){
            if((a instanceof AgentPacman)){
                listePacmans.add((AgentPacman) a);
            }
        }
        return listePacmans;    
    }
    
    /**
     * Récuperer l'ensemble des fantômes en vie
     * @return liste des fantômes en vie
     */
    public ArrayList<AgentGhost> getGhosts(){
        ArrayList<AgentGhost> listeGhosts = new ArrayList<>();
        for(Agent a : this.listeAgents){
            if((!a.isMort()) && (a instanceof AgentGhost)){
                listeGhosts.add((AgentGhost) a);
            }
        }
        return listeGhosts;    
    }

    /**
     * Récuperer tous les fantômes 
     * @return liste des fantômes 
     */
    public ArrayList<AgentGhost> getGhostsEvenDead(){
        ArrayList<AgentGhost> listeGhosts = new ArrayList<>();
        for(Agent a : this.listeAgents){
            if((a instanceof AgentGhost)){
                listeGhosts.add((AgentGhost) a);
            }
        }
        return listeGhosts;    
    }

    /**
     * Récupération des mouvements de chaque agent, contrôle de ces mouvements, application, gestion des morts et actualisation de l'état du labyrinthe. 
     * {@inheritDoc}
     */
    @Override
    public void takeTurn() {
        System.out.println("\nTour " + Integer.toString(this.getTurn()) + " du jeu en cours.");
        if(this.isCapsuleActive()) System.out.println("Capsule active, durée restante : " + this.dureeCapsule);
        this.labyrinthe.plateauToString();
        this.actionsAgents.clear();

        //Récupérer les actions de chaque agent
        for(Agent a : listeAgents.stream().filter(agent -> !agent.isMort()).collect(Collectors.toCollection(ArrayList::new))){//Ne demande qu'aux agents vivants
            AgentAction action = a.choisirAction(this);
            actionsAgents.put(a, action);
        }

        //Vérifier les actions 
        controlerActionsTurn();
        gererMorts();

        //Jouer les actions après contrôle
        jouerActionsTurn();

        if(this.dureeCapsule>0){
            this.dureeCapsule = this.dureeCapsule - 1;
            if(this.dureeCapsule==0){
                this.capsuleChanged(false);//Désactiver de la capsule
            }
        }

        // Mettre le labyrinthe à jour
        actualiserLabyrinthe();
    }
    
    /**
     * Contrôler les actions pour voir les collisions 
     */
    public void controlerActionsTurn(){
        for(Map.Entry<Agent,AgentAction> entry : actionsAgents.entrySet()){
            Agent agent = entry.getKey();
            AgentAction action = entry.getValue();
            if(action.get_direction()!=AgentAction.STOP && !agent.isMort()){ // Si on est pas à l'arrêt, on vérifie les collisions
                if(isLegalMove(action, agent.getPosition())){// Si le coup est légal
                    //Vérifier les collisions avec les autres agents
                    for(Agent autre_agent : listeAgents){ //Ne ragarde que par rapport aux agents vivants
                        if(!autre_agent.isMort()){//Si l'autre agent n'est pas en attente de réapparition ou mort dans une autre résolution de collision du tour.
                            AgentAction actionAutre = this.actionsAgents.get(autre_agent);
                            
                            if(agent != autre_agent && agent.simulerAction(action).equals(autre_agent.getPosition())){//Si on se déplace sur un autre agent et ...
                                if(agent.getPosition().equals(autre_agent.simulerAction(actionAutre))){ //... que l'on se croise,
                                    if(this.gererCollisions(autre_agent, agent)){//Si collision on se stoppe tous les deux
                                        this.actionsAgents.put(agent, new AgentAction(AgentAction.STOP));
                                        this.actionsAgents.put(autre_agent, new AgentAction(AgentAction.STOP));
                                    }
                                } else if(autre_agent.getPosition().getDir() == AgentAction.STOP){//... qu'il est stoppé 
                                    if(this.gererCollisions(autre_agent, agent)) //Si collision je me stoppe
                                        this.actionsAgents.put(agent, new AgentAction(AgentAction.STOP));
                                    
                                }
                            }else if(agent != autre_agent && agent.simulerAction(action).equals(autre_agent.simulerAction(actionAutre))){//else if on va sur la meme case
                                if(this.gererCollisions(autre_agent,agent)){ //Si collision l'autre se stoppe
                                    this.actionsAgents.put(autre_agent, new AgentAction(AgentAction.STOP));
                                }//colision létale : Le gagnant ne s'arrete pas
                            }
                        }
                    }
                }else{ //Sinon, on l'annule 
                    this.actionsAgents.put(agent, new AgentAction(AgentAction.STOP));
                }
            }
        }
    }

    /**
     * Gestion de la collision entre 2 agents
     * @param a1 agent 1
     * @param a2 agent 2
     * @return Vrai : Les deux agents se collisione. Faux : l'un des agents mange l'autre et passe donc
     */
    public boolean gererCollisions(Agent a1, Agent a2){
        if(a1.isVulnerable(this)==a2.isVulnerable(this)){
            //Rien ne se passe
            return true;//Collisions : les deux s'arrêtent
        } else if(a1.isVulnerable(this) && !a1.isMort()){
            if(this.isCapsuleActive()){//Ce cas ne se produit que si on a la capsule active ce qui implique que ceux qui mangent sont des pacmans
                eatGhost();
            }else{
                eatPacman();
            }
            a1.setMort(true);//a1 meurt
            return false; //Pas de collision : a2 mange a1
        } else if(a2.isVulnerable(this) && !a2.isMort()){
            if(this.isCapsuleActive()){//Ce cas ne se produit que si on a la capsule active ce qui implique que ceux qui mangent sont des pacmans
                eatGhost();
            }else{
                eatPacman();
            }
            a2.setMort(true);//a2 meurt
            return false; //Pas de collision : a1 mange a2
        }else {//Normalement inaccessible
            return true; //Dans le doute, collision
        }
    }

    /**
     * Gestion de la mort avec réapparition ou non
     */
    public void gererMorts(){
        ArrayList<Agent> morts = listeAgents.stream().filter(agent -> agent.isMort()).collect(Collectors.toCollection(ArrayList::new)); //Récupère la liste des agents morts
        for(Agent a : morts){
            this.actionsAgents.remove(a); //Les agents morts ne jouent pas
            if(a.peutReapparaitre(this)){
                a.respawn(this);
                this.faireManger(a); //Fait manger sur la case au cas où nourriture à la réapparition
            }else{
                this.tuerAgent(a);
            }
        }
    }

    /**
     * Application des actions contrôlées
     */
    public void jouerActionsTurn(){
        actionsAgents.forEach((agent, action) -> {
            moveAgent(agent, action);
            if(agent.canEatFood()) faireManger(agent);
        });
        
    }

    /**
     * Faire manger un agent s'il y a nourriture ou capsule sur la case
     * @param agent agent
     */
    public void faireManger(Agent agent){
        PositionAgent pos = agent.getPosition();
        if(this.labyrinthe.isFood(pos.getX(), pos.getY())){
            this.eatFood(pos.getX(), pos.getY());
        } else if(this.labyrinthe.isCapsule(pos.getX(), pos.getY())){
            this.eatCapsule(pos.getX(), pos.getY());
        }
    }

    /**
     * Manger la nourriture en (x,y) et marquer les points associés
     * @param x
     * @param y
     */
    public void eatFood(int x, int y){
        this.labyrinthe.setFood(x, y, false);
        this.addScorePacman(10);
        System.out.println("Pacman a mangé de la nourriture : +10pts");
    }

    /**
     * Manger la capsule en (x,y) et marquer les points associés
     * @param x
     * @param y
     */
    public void eatCapsule(int x, int y){
        this.labyrinthe.setCapsule(x, y, false);
        capsuleChanged(true); //Activer la capsule
        this.dureeCapsule = 20; //Durée fixe (+= si veut cumuler)
        this.addScorePacman(20);
        System.out.println("Pacman a mangé une capsule : +20pts");
    }

    /**
     * Notifie les agents en cas de changements de l'état de la capsule (Active ou non)
     * @param active
     */
    public void capsuleChanged(boolean active){
        for(Agent a : this.listeAgents){
            a.capsuleChanged(active);
        }
    }

    /**
     * Faire manger un fantôme à un pacman et associer le score
     */
    public void eatGhost(){
        this.addScorePacman(50); 
        System.out.println("Pacman a mangé un fantôme : +50pts");
    }

    /**
     * Faire manger un pacman à un fantôme et associer le score
     */
    public void eatPacman(){
        this.addScoreGhost(75);
        System.out.println("Fantôme a mangé un pacman : +75pts");
    }

    /**
     * Actualiser les positions des agents pour le labyrinthe
     */
    public void actualiserLabyrinthe(){
        ArrayList<EtatAgent> etatsPacman = getPacmans().stream().map(Agent::getEtatAgent).collect(Collectors.toCollection(ArrayList::new)); //Liste de position des Pacmans
        this.labyrinthe.setEtat_pacmans(etatsPacman);

        ArrayList<EtatAgent> etatsGhost = getGhosts().stream().map(Agent::getEtatAgent).collect(Collectors.toCollection(ArrayList::new)); //Liste de position des Ghosts
        this.labyrinthe.setEtat_ghosts(etatsGhost);
    }

    /**
     * Continuer si pas gagné ou pas perdu. 
     * {@inheritDoc}
     */
    @Override
    public boolean gameContinue() {
        if(!this.gagner() && !this.perdre()){
            return true;
        }
        return false;
    }

    /**
     * Test de victoire
     * @return Vrai ou Faux
     */
    public boolean gagner(){
        if(!this.labyrinthe.hasFoodLeft()){
            return true;
        }
        return false;
    }

    /**
     * Test de défaite
     * @return Vrai ou Faux
     */
    public boolean perdre(){
        if(this.getPacmansEvenDead().isEmpty()){
            return true;
        }
        return false;
    }

    @Override
    public void gameOver() {
        if(this.gagner()){
            System.out.println("Tous les Pacmans ont mangé toute la nourriture.");
            System.out.println("Partie terminée : Victoire !");
            //Pacman gagnent des points supplémentaire s'ils finissent avec des vies restantes
            this.addScorePacman(PacmanGame.BONUS_VIE*this.getNbViesPacman());
            this.setVainqueur(TypeAgent.PACMAN);
        } else {
            System.out.println("Tous les Pacmans sont morts.");
            System.out.println("Partie terminée : Défaite !");
            //Ghost gagnent des points supplémentaire si finissent en moins de 200 tours
            this.addScoreGhost(PacmanGame.TOUR_BONUS-this.getTurn());
            this.setVainqueur(TypeAgent.FANTOME);
        }
        this.lobby.notifierFinPartie();
    }

    /**
     * Test si une action est légale
     * @param action action à tester
     * @param pos position depuis laquelle on part
     * @return Vrai / Faux
     */
    public boolean isLegalMove(AgentAction action, PositionAgent pos) {
        int newX = pos.getX() + action.get_vx();
        int newY = pos.getY() + action.get_vy();
        return !this.isMur(newX, newY);
    }

    /**
     * Test si une capsule est active
     * @return Vrai / Faux
     */
    public boolean isCapsuleActive(){
        return this.dureeCapsule>0;
    }

    /**
     * Faire effectuer une action à un agent
     * @param agent
     * @param action
     */
    public void moveAgent(Agent agent, AgentAction action){
        agent.effectuerAction(action);
    }

    /**
     * Détruire un agent qui ne peut pas réapparaitre
     * @param agent
     */
    public void tuerAgent(Agent agent){
        this.listeAgents.remove(agent);
    }

    /**
     * Test si la case (x,y) est de la nourriture
     * @param x
     * @param y
     * @return Vrai / Faux
     */
    public boolean isFood(int x, int y){
        return this.labyrinthe.isFood(x,y);
    }

    /**
     * Test si la case (x,y) est une capsule
     * @param x
     * @param y
     * @return Vrai / Faux
     */
    public boolean isCapsule(int x, int y){
        return this.labyrinthe.isCapsule(x, y);
    }

    /**
     * Indique s'il reste des capsules sur le terrain
     * @return Vrai / Faux
     */
    public boolean areCapsuleRemaining(){
        for(int i=0; i<this.labyrinthe.getSizeX();++i){
            for(int j=0; j<this.labyrinthe.getSizeY();++j){
                if(isCapsule(i,j)) return true;
            }
        }
        return false;
    }

    /**
     * Test s'il y a un agent sur la case (x,y)
     * @param x
     * @param y
     * @return Vrai / Faux
     */
    public boolean agentSurCase(int x, int y){
        for(Agent a : this.listeAgents){
            if(a.getPosition().getX()==x && a.getPosition().getY()==y) return true;
        }
        return false;
    }

    /**
     * Test si la case (x,y) est un mur
     * @param x
     * @param y
     * @return Vrai / Faux
     */
    public boolean isMur(int x, int y){
        return this.labyrinthe.isWall(x, y);
    }

    /**
     * Faire perdre une vie aux pacmans
     */
    public void perdreViePacman(){
        --this.nbViesPacman;
    }

    /**
     * Changer la stratégie d'un agent
     * @param a agent 
     * @param t stratégie
     */
    public void changerStrategie(Agent a, TypeStrategie t){
        a.setStrategie(factoriesStrategies.getFactory(t).creerStrategie(a));
    }

    /**
     * Récupérer l'agent qui se trouve à une position
     * @param p position où chercher
     * @return l'agent ou null s'il n'y en a pas
     */
    public Agent getAgentAt(PositionAgent p){
        for(Agent a : this.listeAgents){
            if(a.getPosition().equals(p)){
                return a;
            }
        }
        return null;
    }

    /**
     * Notifie les agents d'une action au clavier
     * @param keyCode code de la touche
     */
    public void actionClavier(int keyCode){
        this.getPacmans().forEach(
            agent -> {
                agent.getStrategie().onKeyPressed(keyCode); 
        });
    }

    //--- Jeu en réseau ---
    @Override
    public EtatPacmanGame getEtat() {//Ne passe pas par une classe Etat pour diminuer le temps de traitement pendant
        return new EtatPacmanGame(
            this.getTurn(),
            this.getScorePacman(),
            this.getScoreGhost(),
            this.labyrinthe,
            this.getNbViesPacman(),
            this.isCapsuleActive()
        );
    }
}