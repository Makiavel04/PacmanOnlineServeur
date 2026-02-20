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
        }

        public final class Client {
            public static final String ID_CLIENT = "idClient";
            public static final String USERNAME = "username";
        }

        public final class Partie {
            public static final String TOUR = "tour";
        }

    }
}

