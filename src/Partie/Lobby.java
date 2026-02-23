package Partie;

import java.util.ArrayList;
import java.util.Vector;

import org.json.JSONObject;

import Client.ClientHandler;
import Controller.ServerController;
import Partie.Pacman.Agents.TypeAgent;
import Partie.Pacman.Game.PacmanGame;
import Ressources.RequetesJSON;
import Ressources.EtatGame.EtatGame;
import Ressources.EtatLobby.DetailsJoueur;
import Ressources.EtatLobby.DetailsLobby;
import Ressources.EtatLobby.ResumeLobby;

public class Lobby {
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
        this.idLobby = Lobby.idCpt++;
        this.idHost = client.getID();
        this.serverController = serverController;

        this.clients = new Vector<ClientHandler>();
        clients.add(client);    
        
        this.bots = new Vector<Bot>();
        this.botCpt = 0;
        
        this.nbPacmanMax = 2; // à changer plus tard selon la map
        this.nbFantomeMax = 2; // à changer plus tard selon la map

        this.nbJoueurMax = 4; // à changer plus tard selon la map
        this.game = null;

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

    //--- Connection client ---
    public synchronized void connectClient(ClientHandler client) throws Exception {
        synchronized (this.clients) {
            if(!this.isFilled() && this.game == null) {
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
        return new DetailsLobby(this.idLobby, this.getNbJoueur(), this.nbJoueurMax, this.nbPacmanMax, this.getNbPacman(), this.nbFantomeMax, this.getNbFantome(), this.idHost, this.getAllLobbyClients());
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
        this.notifierDebutPartie();
        this.game = new PacmanGame(5, this);
        this.game.setNomLabyrinthe("./layout/1test.lay");
        this.game.init();
        this.game.launch();
    }

    public void notifierDebutPartie() {
        synchronized (this.clients) {
             for(ClientHandler client : clients) {
                client.debutPartie();
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
        this.majLobby(-1);//tout le monde doit etre MAj meme le client qui l'a fait
    }

    //--- Gestion des bots ---
    public void ajouterBot(TypeAgent type, int idClient) {
        if(idClient == this.idHost) {
            synchronized (this.bots) {
                if((type == TypeAgent.PACMAN && this.getNbPacman() < this.nbPacmanMax) || (type == TypeAgent.FANTOME && this.getNbFantome() < this.nbFantomeMax)) {
                    ++this.botCpt;
                    Bot bot = new Bot(this.botCpt, type);
                    this.bots.add(bot); 
                    this.majLobby(-2); //-2 pour indiquer que c'est un bot
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
                    this.majLobby(-2); //-2 pour indiquer que c'est un bot
                }
            }
        } 
    } 

}
