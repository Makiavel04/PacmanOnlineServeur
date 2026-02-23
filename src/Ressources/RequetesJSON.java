package Ressources;

public final class RequetesJSON {
    public static final String ASK_AUTHENTIFICATION = "demanderAuthentification";
    public static final String RES_AUTHENTIFICATION = "reponseAuthentification";

    public static final String ASK_LISTE_LOBBIES = "demanderListeLobbies";
    public static final String RES_LISTE_LOBBIES = "reponseListeLobbies";

    public static final String ASK_DEMANDE_PARTIE = "demanderPartie";
    public static final String RES_DEMANDE_PARTIE = "reponseDemandePartie";
    public static final String ASK_LANCEMENT_PARTIE = "demanderLancementPartie";

    public static final String MAJ_LOBBY = "majLobby";
        
    public static final String DEBUT_PARTIE = "debutPartie";
    public static final String MAJ_PARTIE = "miseAJourPartie";
    public static final String FIN_PARTIE = "finDePartie";

    public static final String ASK_AJOUT_BOT = "demanderAjoutBot";
    public static final String ASK_RETRAIT_BOT = "demanderRetraitBot";

    public static final String ASK_CHANGEMENT_CAMP = "demanderChangementCamp";

    public final class Attributs {
        public static final String ACTION = "action";

        public static final class Authentification {
            public static final String USERNAME = "username";
            public static final String PASSWORD = "password";
            public static final String ID_CLIENT = "idClient";
            public static final String RESULTAT = "resultat";
        }

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
        }

        public final class Joueur {
            public static final String ID_CLIENT = "idClient";
            public static final String USERNAME = "username";
            public static final String TYPE_AGENT = "typeAgent";
        }

        public final class Partie {
            public static final String TOUR = "tour";

            public static final String PLATEAU = "plateau";
            public static final String SCORE_PACMANS = "scorePacmans";
            public static final String SCORE_FANTOMES = "scoreFantomes";
            public static final String VIES_PACMANS = "viesPacmans";
            public static final String CAPSULE_ACTIVE = "capsuleActive";
        }

    }
}

