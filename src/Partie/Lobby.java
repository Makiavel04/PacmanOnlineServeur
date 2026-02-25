package Partie;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.json.JSONObject;

import Client.ClientHandler;
import Controller.ServerController;
import Partie.Pacman.Agents.TypeAgent;
import Partie.Pacman.Agents.Strategies.TypeStrategie;
import Partie.Pacman.Game.Maze;
import Partie.Pacman.Game.PacmanGame;
import Ressources.RequetesJSON;
import Ressources.EtatGame.EtatGame;
import Ressources.EtatGame.EtatPacmanGame;
import Ressources.EtatLobby.DetailsJoueur;
import Ressources.EtatLobby.DetailsLobby;
import Ressources.EtatLobby.ResumeLobby;

public class Lobby {
    public static final String DOSSIER_MAP = "./layout/";
    public static int idCpt = 0;

    private int idLobby;
    private int idHost;
    private int nbJoueurMax;
    private int nbPacmanMax;
    private int nbFantomeMax;
    private ServerController serverController;
    private Vector<ClientHandler> clients;
    private int botCpt;
    private Vector<Bot> bots;
    private PacmanGame game;

    public Lobby(ClientHandler client, ServerController serverController) {
        this.idLobby = ++Lobby.idCpt;
        this.idHost = client.getID();
        this.serverController = serverController;

        this.clients = new Vector<ClientHandler>();
        clients.add(client);    
        
        this.bots = new Vector<Bot>();
        this.botCpt = 0;

        this.game = new PacmanGame(Integer.MAX_VALUE, this);
        this.game.setNomLabyrinthe("./layout/1test.lay");
        this.game.resetLabyrinthe();
        
        this.majNbJoueurMax();

        this.roleParDefaut(client);
    }

    public int getID() {
        return this.idLobby;
    }

    public boolean isFilled() {
        
        synchronized (this.clients) {
            return this.getNbJoueur() >= this.nbJoueurMax;
        }
}

    public PacmanGame getGame() {
        return this.game;
    }

    public int getNbJoueur(){
        synchronized (this.clients) {
            synchronized (this.bots) {
                return this.clients.size() + this.bots.size();
            }
        }
    }

    public int getNbPacman(){
        return this.getPacmans().size();
    }

    public Vector<Joueur> getPacmans(){
        synchronized(this.clients) {
            synchronized(this.bots) {
                Vector<Joueur> pacmans = new Vector<Joueur>();
                for(ClientHandler client : clients) {
                    if(client.getTypeAgent() == TypeAgent.PACMAN) { 
                        pacmans.add(client);
                    }
                }
                for(Bot bot : bots) {
                    if(bot.getTypeAgent() == TypeAgent.PACMAN) {
                        pacmans.add(bot);
                    }
                }
                return pacmans;
            }
        }
    }

    public int getNbFantome(){
        return this.getFantomes().size(); 
    }

    public Vector<Joueur> getFantomes(){
        synchronized(this.clients) {
            synchronized(this.bots) {
                Vector<Joueur> fantomes = new Vector<Joueur>();
                for(ClientHandler client : clients) {
                    if(client.getTypeAgent() == TypeAgent.FANTOME) { 
                        fantomes.add(client);
                    }
                }
                for(Bot bot : bots) {
                    if(bot.getTypeAgent() == TypeAgent.FANTOME) {
                        fantomes.add(bot);
                    }
                }
                return fantomes;
            }
        }
    }

    public void majNbJoueurMax() {
        this.nbPacmanMax = this.game.getLabyrinthe().getInitNumberOfPacmans();
        this.nbFantomeMax = this.game.getLabyrinthe().getInitNumberOfGhosts();
        this.nbJoueurMax = this.nbFantomeMax + this.nbPacmanMax;
    }

    //--- Connection client ---
    public synchronized void connectClient(ClientHandler client) throws Exception {
        synchronized (this.clients) {
            if(!this.isFilled() && !this.game.isRunning()) {
                this.clients.add(client);
                this.roleParDefaut(client);
                this.majLobby(client.getID());
            }else {
                throw new Exception("Match is already full");
            }
        }
    }

    public void disconnectClient(int clientId) {
        synchronized (this.clients) {
            this.clients.removeIf(client -> client.getID() == clientId);
            if(this.clients.size() == 0) {
                this.serverController.removeLobby(this.idLobby);
            } else {
                if(clientId == this.idHost) {
                    this.migrerHost();
                }
                this.majLobby(clientId);
            }
        }
    }

    //--- Infos du lobby ---
    public ResumeLobby getResumeLobby(){
        return new ResumeLobby(this.idLobby, this.getNbJoueur(), this.nbJoueurMax);
    }

    public DetailsLobby getDetailsLobby(){
        String nomMap = this.game.getNomLabyrinthe().substring(this.game.getNomLabyrinthe().lastIndexOf("/") + 1); //Extraint nom de la map du chemin complet
        //lastIndexOf renvoie -1 si rien trouvé donc -1+1 = 0 donc chaine complète
        return new DetailsLobby(this.idLobby, this.getNbJoueur(), this.nbJoueurMax, this.nbPacmanMax, this.getNbPacman(), this.nbFantomeMax, this.getNbFantome(), this.idHost, this.getAllLobbyClients(), nomMap);
    }

    public ArrayList<DetailsJoueur> getAllLobbyClients() {
        ArrayList<DetailsJoueur> clientsDetails = new ArrayList<>();
        synchronized (this.clients) {
            synchronized (this.bots) {
                for(ClientHandler client : clients) {
                    clientsDetails.add(client.getDetailsJoueur());
                }
                for(Bot bot : bots) {
                    clientsDetails.add(bot.getDetailsJoueur());
                }
            }
        }
        return clientsDetails;
    }

    public ClientHandler getHost() {
        synchronized (this.clients) {
            for(ClientHandler client : clients) {
                if(client.getID() == this.idHost) {
                    return client;
                }
            }
        }
        return null;
    }

    public void migrerHost(){
        synchronized (this.clients) {
            if(!this.clients.isEmpty()) { //Sinon il devrait être supprimé avant car plus personne dans le lobby
                this.idHost = this.clients.get(0).getID();
                System.out.println("Host transféré au client#" + this.idHost + " dans le lobby#" + this.idLobby);
            }
        }
    }

    public void majLobby(int newClientId) {
        synchronized (this.clients) {
            for(ClientHandler client : clients) {
                if(client.getID() != newClientId) {//On ne veut pas maj le client qui vient de se connecter
                    client.majLobby(this.getDetailsLobby().toJSON());
                }
            }
        }
    }

    public List<String> getListeMaps() {
        List<String> nomsLayouts = new ArrayList<>();
        
        // On cible le dossier "layout" (chemin relatif)
        File dossier = new File(Lobby.DOSSIER_MAP);
        
        // On vérifie si le dossier existe et est bien un répertoire
        if (dossier.exists() && dossier.isDirectory()) {
            
            // Filtre pour ne prendre que les fichiers finissant par .lay
            File[] fichiers = dossier.listFiles((dir, name) -> name.toLowerCase().endsWith(".lay"));
            
            if (fichiers != null) {
                for (File f : fichiers) {
                    // On ajoute le nom (avec ou sans l'extension selon ton besoin)
                    nomsLayouts.add(f.getName());
                }
            }
        } else {
            System.err.println("Erreur : Le dossier " + Lobby.DOSSIER_MAP + " est introuvable.");
        }
        
        return nomsLayouts;
    }

    public void changerMap(ClientHandler clientHandler, String nomMap) {
        synchronized(this.clients){
            synchronized(this.bots){
                String old = this.game.getNomLabyrinthe();
                String cheminMap = Lobby.DOSSIER_MAP + nomMap;
                if(clientHandler.getID() == this.idHost && !(old.equals(cheminMap))) {
                    Maze m = null;
                    try{
                        m = new Maze(cheminMap);
                    }catch(Exception e){
                        System.err.println("Erreur lors de la création du labyrinthe : " + e.getMessage());
                        m = null;
                    }

                    if(m == null){
                        clientHandler.autorisationChangementMap(false, 0, 0);
                    }else if(m!=null && (this.getNbPacman() > m.getInitNumberOfPacmans() || this.getNbFantome() > m.getInitNumberOfGhosts())){
                        clientHandler.autorisationChangementMap(false, m.getInitNumberOfPacmans(), m.getInitNumberOfGhosts());
                        System.out.println("Changement de map refusé pour le client#" + clientHandler.getID() + " dans le lobby#" + this.idLobby + " : La nouvelle map ne supporte pas le nombre actuel de joueurs.");
                    }else{
                        System.out.println("Client#" + clientHandler.getID() + " a changé la map du lobby#" + this.idLobby + " de " + old + " à " + cheminMap);
                        this.game.setNomLabyrinthe(cheminMap);
                        this.game.resetLabyrinthe();
                        this.majNbJoueurMax();
                        //clientHandler.autorisationChangementMap(true); // Pas nécessaire car recoit maj d'état
                        this.majLobby(0); //0 pour indiquer  que tout le monde doit être MAJ
                    }
                }
            }
        }
    }

    //--- Gestion du match ---

    public void tenterLancementPartie(int idClient){
        synchronized (this.clients) {
            if (this.isFilled() && this.getHost().getID() == idClient) { // Synchronized car getHost et isFilled (bien qu'ayant leur propre synchronisation) sont utilisés ensemble, et on veut le même état pour les deux.
                    this.lancerPartie();
                    System.out.println("Lobby#" + this.idLobby + " démarrer avec succès par le client#" + idClient);
            } else {
                if(!this.isFilled()) System.out.println("Lobby#" + this.idLobby + " ne peut pas démarrer : Pas plein.");
                else if(this.getHost().getID() != idClient) System.out.println("Lobby#" + this.idLobby + " ne peut pas démarrer. Client#" + idClient + " n'est pas l'hôte.");
            }
        }
    }

    public void lancerPartie(){
        this.game.init();
        this.notifierDebutPartie();
        this.game.launch();
    }

    public void notifierDebutPartie() {
        synchronized (this.clients) {
            EtatPacmanGame etatInit = this.game.getEtat();
            for(ClientHandler client : clients) {
                client.debutPartie(etatInit);
            }
        }
    }

    public void notifierTour(EtatGame etat) {
        JSONObject jsonEtat = etat.toJSON();
        jsonEtat.put(RequetesJSON.Attributs.ACTION, RequetesJSON.MAJ_PARTIE);
        synchronized (this.clients) {
             for(ClientHandler client : clients) {
                client.majTour(jsonEtat.toString());
            }
        }
    }
    public void notifierFinPartie() {
        synchronized (this.clients) {
            for(ClientHandler client : clients) {
                client.finPartie();
            }
            //Faire quitter tous les clients du lobby et supprimer le lobby
            for(ClientHandler client : clients) {
                client.setLobby(null);
            }
            this.clients.clear();
            this.serverController.removeLobby(this.idLobby);
        }
    }

    public void roleParDefaut(ClientHandler client) {
        if(this.getNbPacman() < this.nbPacmanMax) {
            client.setTypeAgent(TypeAgent.PACMAN);
        } else if(this.getNbFantome() < this.nbFantomeMax) {
            client.setTypeAgent(TypeAgent.FANTOME);
        }
    }

    public void changerCamp(ClientHandler client) {
        if(client.getTypeAgent() == TypeAgent.PACMAN && this.getNbFantome() < this.nbFantomeMax) {
            client.setTypeAgent(TypeAgent.FANTOME);
            System.out.println("Client#" + client.getID() + " est passé FANTOME dans le lobby#" + this.idLobby);
        } else if(client.getTypeAgent() == TypeAgent.FANTOME && this.getNbPacman() < this.nbPacmanMax) {
            client.setTypeAgent(TypeAgent.PACMAN);
            System.out.println("Client#" + client.getID() + " est passé PACMAN dans le lobby#" + this.idLobby);
        }
        this.majLobby(0);//tout le monde doit etre MAj meme le client qui l'a fait
    }

    //--- Gestion des bots ---
    public void ajouterBot(TypeAgent type, int idClient) {
        if(idClient == this.idHost) {
            synchronized (this.bots) {
                if((type == TypeAgent.PACMAN && this.getNbPacman() < this.nbPacmanMax) || (type == TypeAgent.FANTOME && this.getNbFantome() < this.nbFantomeMax)) {
                    ++this.botCpt;
                    Bot bot = new Bot(this.botCpt, type, TypeStrategie.IMMOBILE);
                    this.bots.add(bot); 
                    this.majLobby(0); //0 pour indiquer  que tout le monde doit être MAJ
                }
            }
        }
    }

    public void retirerBot(TypeAgent type, int idClient) {
        if(idClient == this.idHost) {
            synchronized (this.bots) {
                if(!this.bots.isEmpty()) {
                    for(Bot bot : this.bots) {
                        if(type == bot.getTypeAgent() ) {
                            bots.remove(bot);
                            break;
                        }
                    }
                    this.majLobby(0); //0 pour indiquer  que tout le monde doit être MAJ
                }
            }
        } 
    } 

    public Bot getBot(int idBot) {
        synchronized (this.bots) {
            for(Bot bot : this.bots) {
                if(bot.getId() == idBot) {
                    return bot;
                }
            }
        }
        return null;
    }

    public void setStrategieBot(int idClient, int idBot, TypeStrategie typeStrategie) {
        if(idClient == this.idHost) {
            synchronized (this.bots) {
                Bot bot = this.getBot(idBot);
                if(bot != null && typeStrategie != null && typeStrategie.estCompatibleAvec(bot.getTypeAgent()) && typeStrategie.isUtilisableBot()) {
                    bot.setTypeStrategie(typeStrategie);
                }
                majLobby(0);
            }
        }
    }

}
