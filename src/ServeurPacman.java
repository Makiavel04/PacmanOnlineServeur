import java.util.Scanner;

import Controller.ServerController;
/** Classe de lancement du serveur */
public class ServeurPacman {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        
        //Port
        System.out.print("Entrez le port de communication pour le serveur de jeu (défaut : 50000) : ");
        String portSaisie = sc.nextLine(); // On lit tout en String pour éviter le bug du \n
        int port = portSaisie.isBlank() ? 50000 : Integer.parseInt(portSaisie.trim());
        
        //URL
        System.out.print("Entrez l'URL du serveur web pour accéder aux fonctions api (défaut : http://localhost:8080/Pacmanweb/api) : ");
        String urlSaisie = sc.nextLine();
        String url = urlSaisie.isBlank() ? "http://localhost:8080/Pacmanweb/api" : urlSaisie.trim();

        System.out.println("\nConfiguration retenue :");
        System.out.println(" > Port Jeu : " + port);
        System.out.println(" > URL API  : " + url);
        System.out.println("------------------------");

        ServerController server = new ServerController(port, url);
        server.launch();    
    }
} 