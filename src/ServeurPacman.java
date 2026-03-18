import Controller.ServerController;
/** Classe de lancement du serveur */
public class ServeurPacman {
    public static void main(String[] args) {
        ServerController server = new ServerController(8080);
        server.launch();    
    }
}