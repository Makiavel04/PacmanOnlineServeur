package Partie.Pacman.Game;


import Partie.Lobby;
import Ressources.EtatGame.EtatGame;

/**
 * Classe de jeu abstraite
 */
public abstract class Game implements Runnable{
    int turn;
    int maxTurn;
    boolean isRunning;
    int intervalle;
    /** intervalle par defaut entre 2 tours */
    public static int DEFAULT_INTERVALLE = 500; //en ms

    Thread thread;
    Lobby lobby;

    public Game(int maxTurn, Lobby lobby){
        this.maxTurn = maxTurn;
        this.lobby = lobby;
        this.intervalle = DEFAULT_INTERVALLE;
    }

    public boolean isRunning(){return this.isRunning;}
    //public void setRunning(boolean isRunning) { this.isRunning = isRunning;}
    public int getTurn() {return this.turn;}
    public void setIntervalle(int intervalle) {this.intervalle = intervalle;}
    public int getIntervalle() {return this.intervalle;}

    /**
     * Initialisation des attributs généraux du jeu
     */
    public void init(){
        this.turn = 0;
        this.isRunning = false;
        this.initializeGame();
    }

    /**
     * Lancement du jeu
     */
    public void launch(){//Lance fonction run sur un thread
        this.isRunning = true;
        this.thread = new Thread(this);
        this.thread.start();
    }

    /**
     * Faire un tour de jeu
     */
    public void step(){
        if((this.turn<=this.maxTurn) && gameContinue()){
            this.turn += 1;
            this.takeTurn();
        }else{
            this.isRunning = false;
            this.gameOver();
        }
    }

    /**
     * Mettre le jeu en pause
     */
    public void pause(){this.isRunning = false;}

    /**
     * Rédéfinit run() pour lancer les tours tant qu'il n'y a pas de pause
     */
    @Override
    public void run(){
        while(this.isRunning()){
            this.step();
            this.lobby.notifierTour(this.getEtat());
            try{
                Thread.sleep(this.intervalle);
            }catch(InterruptedException e){
                System.out.println(e.getMessage());
            }

        }
    }

    /** Initialise les attributs propre au jeu */
    public abstract void initializeGame(); 
    /** Effectue les actions du tour */
    public abstract void takeTurn(); 
    /** Indique si la partie est fini */
    public abstract boolean gameContinue(); //indique si la partie est finie
    /** Traitement de fin de partie */
    public abstract void gameOver();

    //--- Jeu en réseau ---

    public abstract EtatGame getEtat();
}
