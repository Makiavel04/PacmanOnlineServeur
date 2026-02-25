package Ressources;
/**
 * Classe contenant les différentes requêtes utilisées pour la communication entre le client et le serveur
 */
public final class RequetesJSON {
    public static final String ASK_AUTHENTIFICATION = "demanderAuthentification";
    public static final String RES_AUTHENTIFICATION = "reponseAuthentification";

    public static final String ASK_LISTE_LOBBIES = "demanderListeLobbies";
    public static final String RES_LISTE_LOBBIES = "reponseListeLobbies";

    public static final String ASK_DEMANDE_PARTIE = "demanderPartie";
    public static final String RES_DEMANDE_PARTIE = "reponseDemandePartie";
    public static final String ASK_LANCEMENT_PARTIE = "demanderLancementPartie";

    public static final String MAJ_LOBBY = "majLobby";
    public static final String QUITTER_LOBBY = "quitterLobby";
        
    public static final String DEBUT_PARTIE = "debutPartie";
    public static final String MAJ_PARTIE = "miseAJourPartie";
    public static final String FIN_PARTIE = "finDePartie";

    public static final String ASK_AJOUT_BOT = "demanderAjoutBot";
    public static final String ASK_RETRAIT_BOT = "demanderRetraitBot";
    public static final String ASK_CHANGER_STRATEGIE_BOT = "demanderChangementStrategieBot";

    public static final String ASK_CHANGEMENT_CAMP = "demanderChangementCamp";

    public static final String ASK_CHANGEMENT_MAP = "demanderChangementMap";
    public static final String RES_CHANGEMENT_MAP = "reponseChangementMap";

    public static final String SEND_DEPLACEMENT = "envoyerDeplacement";

    /** Classe contenant les attributs utilisés dans les requêtes JSON */
    public final class Attributs {
        public static final String ACTION = "action";

        /** Attributs pour requêtes d'authentification */
        public static final class Authentification {
            public static final String USERNAME = "username";
            public static final String PASSWORD = "password";
            public static final String ID_CLIENT = "idClient";
            public static final String RESULTAT = "resultat";
        }

        /** Attributs pour requêtes liées au lobby */
        public final class Lobby {
            public final static String LISTE_LOBBIES = "liste_lobbies";

            public static final String ID_LOBBY = "idLobby";
            public static final String NB_JOUEUR = "nbJoueur";
            public static final String NB_MAX_JOUEUR = "nbMaxJoueur";
            public static final String ID_HOST = "host";
            public static final String JOUEURS = "joueurs";
            public static final String NB_MAX_PACMAN = "nbMaxPacman";
            public static final String NB_PACMAN = "nbPacman";
            public static final String NB_MAX_FANTOME = "nbMaxFantome"; 
            public static final String NB_FANTOME = "nbFantome";
            public static final String STRATS_PACMAN = "stratsPacman";
            public static final String STRATS_FANTOME = "stratsFantome";
            public static final String TYPE_STRATEGIE = "typeStrategie";
            public static final String NUM_BOT = "numBot";

            public static final String LISTE_MAPS_DISPONIBLES = "listeMaps";
            public static final String MAP = "map";
            public static final String AUTORISE_CHANGEMENT = "autoriseChangement";
        }

        /** Attributs pour requêtes liées aux joueurs */
        public final class Joueur {
            public static final String ID_CLIENT = "idClient";
            public static final String USERNAME = "username";
            public static final String TYPE_AGENT = "typeAgent";
            public static final String IS_BOT = "isBot";
            public static final String STRATEGIE = "strategie";
        }

        /** Attributs pour requêtes liées à la partie */
        public final class Partie {
            public static final String TOUR = "tour";

            public static final String PLATEAU = "plateau";
            public static final String PACMANS_POS = "pacmansPos";
            public static final String FANTOMES_POS = "fantomesPos";
            public static final String SCORE_PACMANS = "scorePacmans";
            public static final String SCORE_FANTOMES = "scoreFantomes";
            public static final String VIES_PACMANS = "viesPacmans";
            public static final String CAPSULE_ACTIVE = "capsuleActive";

            public static final String SENS_MOUVEMENT = "sensMouvement";
        }

        /** Attributs pour les positions des agents */
        public final class PositionAgent {
            public static final String X = "x";
            public static final String Y = "y";
            public static final String DIR = "dir";
        }

        /** Attributs pour les plateaux */
        public final class Plateau {
            public static final String SIZE_X = "size_x";
            public static final String SIZE_Y = "size_y";
            public static final String WALLS = "walls";
            public static final String FOOD = "food";
            public static final String CAPSULES = "capsules";
            public static final String PACMAN_START = "pacman_start";
            public static final String GHOSTS_START = "ghosts_start";
        }

        public final class ScoreFinPartie {
            public static final String VAINQUEUR = "vainqueur";
            public static final String SCORE_FANTOME = "scoreFantome";
            public static final String SCORE_PACMAN = "scorePacman";
        }
    }
}

