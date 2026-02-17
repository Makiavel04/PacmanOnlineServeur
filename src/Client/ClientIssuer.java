package Client;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

public class ClientIssuer extends Thread{

    private Socket so;
    private ClientHandler clientHandler;
    private PrintWriter pWriter;
    private LinkedBlockingQueue<String> fileMessages;

    public ClientIssuer(Socket so, ClientHandler c) {
        this.so = so;
        this.clientHandler = c;
        this.fileMessages = new LinkedBlockingQueue<>();
    }

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

    public void ouvrirConnection() throws IOException {
        try {
            pWriter = new PrintWriter(so.getOutputStream(), true);
        }
        catch (IOException e) {
            throw new IOException(e.getMessage());
        }
    }

    public void envoyerRequete(String message) {
        fileMessages.add(message); 
    }
}
