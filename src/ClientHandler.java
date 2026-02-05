import java.io.IOException;
import java.net.Socket;

public class ClientHandler {

    public static int idCpt = 0;
    private int idClient;
    ClientListener listener;
    ClientIssuer issuer;
    Socket so;

    public ClientHandler(Socket so) {
        this.idClient = ClientHandler.idCpt++;
        this.so = so;
        this.listener = new ClientListener(so, this);
        this.issuer = new ClientIssuer(so, this);
        openConnexion();
        listener.start();
        System.out.println("New client: " + idClient + " created");
    }

    public int getID() {
        return this.idClient;
    }

    public void openConnexion() {
        try {
            this.listener.openConnexion();
            this.issuer.openConnexion();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeConnexion() {
        try {
            so.close(); 
            System.out.println("Socket closed");
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
    }

}
