package Client;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

public class ClientIssuer extends Thread{

    private Socket so;
    private ClientHandler clientHandler;
    private PrintWriter pWriter;
    /**File de messages en attent d'être envoyés au client */
    private LinkedBlockingQueue<String> fileMessages;

    public ClientIssuer(Socket so, ClientHandler c) {
        this.so = so;
        this.clientHandler = c;
        this.fileMessages = new LinkedBlockingQueue<>();
    }

    /**
     * Tourne en permanence pour envoyer les messages de la file d'attente au client
     */
    @Override
    public void run() {//Tourne en permanence pour envoyer les messages de la file d'attente au client
        try{
            while(!this.isInterrupted()){
                String message = fileMessages.take(); //Sommeil en attendant un message => pas de CPU utilisé inutilement et moins couteux que démarrer un thread à chaque message
                pWriter.println(message);
                pWriter.flush();
            }
        }catch (InterruptedException e) {
            System.out.println("ClientIssuer thread interrupted, stopping...");
            this.clientHandler.fermerConnection();
        }
    }

    /** 
     * Ouvre la connexion en initialisant le PrintWriter pour envoyer les messages au client
      * @throws IOException si une erreur d'entrée/sortie se produit lors de l'ouverture de la connexion
     */
    public void ouvrirConnection() throws IOException {
        try {
            pWriter = new PrintWriter(so.getOutputStream(), true);
        }
        catch (IOException e) {
            throw new IOException(e.getMessage());
        }
    }

    /**
     * Ajoute un message à la file d'attente pour qu'il soit envoyé au client
     * @param message message à envoyer au client
     */
    public void envoyerRequete(String message) {
        fileMessages.add(message); 
    }

    /**
     * Ferme le PrintWriter pour libérer les ressources associées à la connexion avec le client
     */
    public void fermerWriter() {
        pWriter.close();
    }
}
