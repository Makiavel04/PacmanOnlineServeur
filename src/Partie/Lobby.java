package Partie;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.json.JSONObject;

import Client.ClientHandler;
import Controller.ServerController;
import Controller.http.PartieResult;
import Partie.Pacman.Agents.Strategies.TypeStrategie;
import Partie.Pacman.Game.PacmanGame;
import pacman.online.commun.dto.RequetesJSON;
import pacman.online.commun.dto.game.EtatGame;
import pacman.online.commun.dto.game.EtatPacmanGame;
import pacman.online.commun.dto.lobby.BilanPartie;
import pacman.online.commun.dto.lobby.DetailsJoueur;
import pacman.online.commun.dto.lobby.DetailsLobby;
import pacman.online.commun.dto.lobby.ResumeLobby;
import pacman.online.commun.moteur.Maze;
import pacman.online.commun.moteur.TypeAgent;

public class Lobby {
    /**
     * Chemin vers la map demander
     * @return chemin vers la map
     */
    public static String pathToLayout(String nomMap) {
        URL resource = Lobby.class.getResource("/layout/" + nomMap);
        if (resource == null){
            System.err.println("Map " + nomMap +" introuvable.");
            return null;
        } 
        try{
            return new File(resource.toURI()).getAbsolutePath();
        } catch (Exception e) {
            return new File(resource.getPath()).getAbsolutePath();
        }
    }

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
        this.idHost = client.getId();
        this.serverController = serverController;

        this.clients = new Vector<ClientHandler>();
        clients.add(client);

        this.bots = new Vector<Bot>();
        this.botCpt = 0;

        this.game = new PacmanGame(Integer.MAX_VALUE, this);
        System.out.println(Lobby.pathToLayout("1test.lay"));
        this.game.setNomLabyrinthe(Lobby.pathToLayout("1test.lay"));
        this.game.resetLabyrinthe();

        this.majNbJoueurMax();

        this.roleParDefaut(client);
    }

    public int getId() {
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

    public int getNbJoueur() {
        synchronized (this.clients) {
            synchronized (this.bots) {
                return this.clients.size() + this.bots.size();
            }
        }
    }

    public int getNbPacman() {
        return this.getPacmans().size();
    }

    public Vector<Joueur> getPacmans() {
        synchronized (this.clients) {
            synchronized (this.bots) {
                Vector<Joueur> pacmans = new Vector<Joueur>();
                for (ClientHandler client : clients) {
                    if (client.getTypeAgent() == TypeAgent.PACMAN) {
                        pacmans.add(client);
                    }
                }
                for (Bot bot : bots) {
                    if (bot.getTypeAgent() == TypeAgent.PACMAN) {
                        pacmans.add(bot);
                    }
                }
                return pacmans;
            }
        }
    }

    public int getNbFantome() {
        return this.getFantomes().size();
    }

    public Vector<Joueur> getFantomes() {
        synchronized (this.clients) {
            synchronized (this.bots) {
                Vector<Joueur> fantomes = new Vector<Joueur>();
                for (ClientHandler client : clients) {
                    if (client.getTypeAgent() == TypeAgent.FANTOME) {
                        fantomes.add(client);
                    }
                }
                for (Bot bot : bots) {
                    if (bot.getTypeAgent() == TypeAgent.FANTOME) {
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

    // --- Connection client ---
    /**
     * Connecte un client au lobby
     * 
     * @param client client à connecter
     * @throws Exception si le lobby est déjà plein ou si la partie a déjà commencé
     */
    public synchronized void connectClient(ClientHandler client) throws Exception {
        synchronized (this.clients) {
            if (!this.isFilled() && !this.game.isRunning()) {
                this.clients.add(client);
                this.roleParDefaut(client);
                this.majLobby(client.getId());
            } else {
                throw new Exception("Match is already full");
            }
        }
    }

    /**
     * Déconnecte un client du lobby. Si le client est l'hôte, un nouveau hôte est
     * désigné. Si le lobby devient vide, il est supprimé.
     * 
     * @param clientId client à déconnecter
     */
    public void disconnectClient(int clientId) {
        synchronized (this.clients) {
            this.clients.removeIf(client -> client.getId() == clientId);
            if (this.clients.size() == 0) {
                this.game.stop();
                this.serverController.removeLobby(this.idLobby);
            } else {
                if (clientId == this.idHost) {
                    this.migrerHost();
                }
                this.majLobby(clientId);
            }
        }
    }

    // --- Infos du lobby ---
    /**
     * Retourne un résumé du lobby (sans les détails des joueurs)
     * 
     * @return résumé du lobby
     */
    public ResumeLobby getResumeLobby() {
        return new ResumeLobby(this.idLobby, this.getNbJoueur(), this.nbJoueurMax);
    }

    /**
     * Retourne les détails du lobby (avec les détails des joueurs)
     * 
     * @return détails du lobby
     */
    public DetailsLobby getDetailsLobby() {
        String nomMap = this.game.getNomLabyrinthe().substring(this.game.getNomLabyrinthe().lastIndexOf("/") + 1); // Extrait nom de la map du chemin complet
        // lastIndexOf renvoie -1 si rien trouvé donc -1+1 = 0 donc chaine complète
        return new DetailsLobby(this.idLobby, this.getNbJoueur(), this.nbJoueurMax, this.nbPacmanMax,
                this.getNbPacman(), this.nbFantomeMax, this.getNbFantome(), this.idHost, this.getAllLobbyClients(),
                nomMap);
    }

    /**
     * Retourne les détails de tous les clients du lobby (humains et bots)
     * 
     * @return liste des détails de tous les clients du lobby
     */
    public ArrayList<DetailsJoueur> getAllLobbyClients() {
        ArrayList<DetailsJoueur> clientsDetails = new ArrayList<>();
        synchronized (this.clients) {
            synchronized (this.bots) {
                for (ClientHandler client : clients) {
                    clientsDetails.add(client.getDetailsJoueur());
                }
                for (Bot bot : bots) {
                    clientsDetails.add(bot.getDetailsJoueur());
                }
            }
        }
        return clientsDetails;
    }

    public ClientHandler getHost() {
        synchronized (this.clients) {
            for (ClientHandler client : clients) {
                if (client.getId() == this.idHost) {
                    return client;
                }
            }
        }
        return null;
    }

    /**
     * Change l'hôte du lobby
     */
    public void migrerHost() {
        synchronized (this.clients) {
            if (!this.clients.isEmpty()) { // Sinon il devrait être supprimé avant car plus personne dans le lobby
                this.idHost = this.clients.get(0).getId();
                System.out.println("Host transféré au client#" + this.idHost + " dans le lobby#" + this.idLobby);
            }
        }
    }

    /**
     * Met à jour le lobby pour tous les clients (sauf celui qui vient de faire une
     * action, identifié par newClientId)
     * 
     * @param newClientId Id du client à omettre
     */
    public void majLobby(int newClientId) {
        synchronized (this.clients) {
            for (ClientHandler client : clients) {
                if (client.getId() != newClientId) {// On ne veut pas maj le client qui vient de se connecter
                    client.majLobby(this.getDetailsLobby().toJSON());
                }
            }
        }
    }

    /**
     * Liste le nom de toutes les maps disponibles dans le dossier de maps
     * 
     * @return liste des noms de maps disponibles
     */
    public List<String> getListeMaps() {
        List<String> nomsLayouts = new ArrayList<>();

        // On cible le dossier "layout" (chemin relatif)
        File dossier = new File(Lobby.pathToLayout(""));

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
            System.err.println("Erreur : Le dossier de layouts est introuvable. ( " + Lobby.pathToLayout("") + " )");
        }

        return nomsLayouts;
    }

    /**
     * Change la map du lobby et notifie les clients
     * 
     * @param clientHandler client qui demande le changement de map (doit être
     *                      l'hôte)
     * @param nomMap        nom de la map à charger (doit être dans le dossier de
     *                      maps et compatible avec le nombre de joueurs actuel)
     */
    public void changerMap(ClientHandler clientHandler, String nomMap) {
        synchronized (this.clients) {
            synchronized (this.bots) {
                String old = this.game.getNomLabyrinthe();
                String cheminMap = Lobby.pathToLayout(nomMap);
                if (clientHandler.getId() == this.idHost && !(old.equals(cheminMap))) {
                    Maze m = null;
                    try {
                        m = new Maze(cheminMap);
                    } catch (Exception e) {
                        System.err.println("Erreur lors de la création du labyrinthe : " + e.getMessage());
                        m = null;
                    }

                    if (m == null) {
                        clientHandler.autorisationChangementMap(false, 0, 0);
                    } else if (m != null && (this.getNbPacman() > m.getInitNumberOfPacmans()
                            || this.getNbFantome() > m.getInitNumberOfGhosts())) {
                        clientHandler.autorisationChangementMap(false, m.getInitNumberOfPacmans(),
                                m.getInitNumberOfGhosts());
                        System.out.println("Changement de map refusé pour le client#" + clientHandler.getId()
                                + " dans le lobby#" + this.idLobby
                                + " : La nouvelle map ne supporte pas le nombre actuel de joueurs.");
                    } else {
                        System.out.println("Client#" + clientHandler.getId() + " a changé la map du lobby#"
                                + this.idLobby + " de " + old + " à " + cheminMap);
                        this.game.setNomLabyrinthe(cheminMap);
                        this.game.resetLabyrinthe();
                        this.majNbJoueurMax();
                        // clientHandler.autorisationChangementMap(true); // Pas nécessaire car recoit
                        // maj d'état
                        this.majLobby(0); // 0 pour indiquer que tout le monde doit être MAJ
                    }
                }
            }
        }
    }

    // --- Gestion du match ---
    /**
     * Tenter de lancer la partie. Refuse si lobby pas plein ou pas demandé par
     * l'hôte.
     * 
     * @param idClient ID du client qui demande le lancement de la partie
     */
    public void tenterLancementPartie(int idClient) {
        synchronized (this.clients) {
            if (this.isFilled() && this.getHost().getId() == idClient) { // Synchronized car getHost et isFilled (bien qu'ayant leur propre synchronisation) sont utilisés ensemble, et on veut le même état pour les deux.
                this.lancerPartie();
                System.out.println("Lobby#" + this.idLobby + " démarrer avec succès par le client#" + idClient);
            } else {
                if (!this.isFilled())
                    System.out.println("Lobby#" + this.idLobby + " ne peut pas démarrer : Pas plein.");
                else if (this.getHost().getId() != idClient)
                    System.out.println("Lobby#" + this.idLobby + " ne peut pas démarrer. Client#" + idClient
                            + " n'est pas l'hôte.");
            }
        }
    }

    /**
     * Lance la partie en initialisant le jeu, en notifiant les clients du début de
     * partie, puis en lançant le jeu.
     */
    public void lancerPartie() {
        this.game.init();
        this.notifierDebutPartie();
        this.game.launch();
    }

    /**
     * Notifie les clients du début de la partie et envoie l'état initial du jeu
     */
    public void notifierDebutPartie() {
        synchronized (this.clients) {
            EtatPacmanGame etatInit = this.game.getEtat();
            for (ClientHandler client : clients) {
                client.debutPartie(etatInit);
            }
        }
    }

    /**
     * Notifie les clients de la mise à jour de l'état du jeu à chaque tour
     * 
     * @param etat état actuel du jeu
     */
    public void notifierTour(EtatGame etat) {
        JSONObject jsonEtat = etat.toPartialJSON();
        jsonEtat.put(RequetesJSON.Attributs.ACTION, RequetesJSON.MAJ_PARTIE);
        synchronized (this.clients) {
            for (ClientHandler client : clients) {
                client.majTour(jsonEtat.toString());
            }
        }
    }

    /**
     * Notifie les clients de la fin de la partie et envoie le score final, puis
     * réinitialise les bots
     */
    public void notifierFinPartie() {
        synchronized (this.clients) {
            BilanPartie score = new BilanPartie(this.game.getVainqueur(), this.game.getScoreGhost(), this.game.getScorePacman());
            for (ClientHandler client : clients) {
                client.finPartie(score);
            }
        }
        synchronized (this.bots) {
            for (Bot bot : bots) {
                bot.finPartie();// Réinit le bot
            }
        }

        this.serverController.envoyerResultat(this.resultatPartie());
    }
    
    public PartieResult resultatPartie(){
        ArrayList<Integer> idPacmans = new ArrayList<>();
        ArrayList<Integer> idFantomes = new ArrayList<>();
        synchronized(this.clients){
            for(ClientHandler cli : this.clients){
                if(cli.getTypeAgent()==TypeAgent.PACMAN) idPacmans.add(cli.getId()); 
                else idFantomes.add(cli.getId());
            }
        }

        String vainqueur;
        if(this.game.getVainqueur() == TypeAgent.PACMAN) vainqueur = "P";
        else vainqueur = "F";
        return new PartieResult(this.game.getScorePacman(), this.game.getScoreGhost(), idPacmans, idFantomes, vainqueur);
    }

    /**
     * Attribue un rôle par défaut au client qui vient de se connecter
     * 
     * @param client client à qui attribuer un rôle
     */
    public void roleParDefaut(ClientHandler client) {
        if (this.getNbPacman() < this.nbPacmanMax) {
            client.setTypeAgent(TypeAgent.PACMAN);
        } else if (this.getNbFantome() < this.nbFantomeMax) {
            client.setTypeAgent(TypeAgent.FANTOME);
        }
    }

    /**
     * Change le camp du client (de Pacman à Fantome ou inversement) si possible,
     * puis notifie les clients du changement
     * 
     * @param client client qui demande le changement de camp
     */
    public void changerCamp(ClientHandler client) {
        if (client.getTypeAgent() == TypeAgent.PACMAN && this.getNbFantome() < this.nbFantomeMax) {
            client.setTypeAgent(TypeAgent.FANTOME);
            System.out.println("Client#" + client.getId() + " est passé FANTOME dans le lobby#" + this.idLobby);
        } else if (client.getTypeAgent() == TypeAgent.FANTOME && this.getNbPacman() < this.nbPacmanMax) {
            client.setTypeAgent(TypeAgent.PACMAN);
            System.out.println("Client#" + client.getId() + " est passé PACMAN dans le lobby#" + this.idLobby);
        }
        this.majLobby(0);// tout le monde doit etre MAj meme le client qui l'a fait
    }

    // --- Gestion des bots ---
    /**
     * Ajoute un bot du type spécifié si possible, puis notifie les clients du
     * changement
     * 
     * @param type     type de bot à ajouter (Pacman ou Fantome)
     * @param idClient id du client qui demande l'ajout du bot (doit être l'hôte)
     */
    public void ajouterBot(TypeAgent type, int idClient) {
        if (idClient == this.idHost) {
            synchronized (this.bots) {
                if ((type == TypeAgent.PACMAN && this.getNbPacman() < this.nbPacmanMax)
                        || (type == TypeAgent.FANTOME && this.getNbFantome() < this.nbFantomeMax)) {
                    ++this.botCpt;
                    Bot bot = new Bot(this.botCpt, type, TypeStrategie.IMMOBILE);
                    this.bots.add(bot);
                    this.majLobby(0); // 0 pour indiquer que tout le monde doit être MAJ
                }
            }
        }
    }

    /**
     * Retire un bot du type spécifié si possible, puis notifie les clients du
     * changement
     * 
     * @param type     type de bot à retirer (Pacman ou Fantome)
     * @param idClient id du client qui demande le retrait du bot (doit être l'hôte)
     */
    public void retirerBot(TypeAgent type, int idClient) {
        if (idClient == this.idHost) {
            synchronized (this.bots) {
                if (!this.bots.isEmpty()) {
                    for (Bot bot : this.bots) {
                        if (type == bot.getTypeAgent()) {
                            bots.remove(bot);
                            break;
                        }
                    }
                    this.majLobby(0); // 0 pour indiquer que tout le monde doit être MAJ
                }
            }
        }
    }

    /**
     * Retourne le bot correspondant à l'id spécifié, ou null si aucun bot ne
     * correspond
     * 
     * @param idBot id du bot à trouver
     * @return le bot correspondant à l'id spécifié, ou null si aucun bot ne
     *         correspond
     */
    public Bot getBot(int idBot) {
        synchronized (this.bots) {
            for (Bot bot : this.bots) {
                if (bot.getId() == idBot) {
                    return bot;
                }
            }
        }
        return null;
    }

    /**
     * Change la stratégie du bot spécifié si possible, puis notifie les clients du
     * changement
     * 
     * @param idClient      id du client qui demande le changement de stratégie
     *                      (doit être l'hôte)
     * @param idBot         id du bot dont la stratégie doit être changée
     * @param typeStrategie nouvelle stratégie à attribuer au bot
     */
    public void setStrategieBot(int idClient, int idBot, TypeStrategie typeStrategie) {
        if (idClient == this.idHost) {
            synchronized (this.bots) {
                Bot bot = this.getBot(idBot);
                if (bot != null && typeStrategie != null && typeStrategie.estCompatibleAvec(bot.getTypeAgent())
                        && typeStrategie.isUtilisableBot()) {
                    bot.setTypeStrategie(typeStrategie);
                }
                majLobby(0);
            }
        }
    }

}
