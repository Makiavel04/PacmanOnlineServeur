import java.util.Vector;

public class MatchThread extends Thread {
    public static int idCpt = 0;

    private int idMatch;
    private int idClientCreator;
    private int playerNumberWaited;
    private int currentPlayerNumber;
    private Vector<ClientHandler> clients;

    public MatchThread(int idClient) {
        this.idMatch = MatchThread.idCpt++;
        this.idClientCreator = idClient;
        this.playerNumberWaited = 2; // à changer plus tard selon la map
        this.currentPlayerNumber = 0;
    }   

    @Override
    public void run() {
        System.out.println("Match thread started");
    }

    public int getID() {
        return this.idMatch;
    }

    public boolean connectClient(int idClient) {
        try {
            clients.add(ServerController.getClient(idClient));
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isFilled() {
        return this.currentPlayerNumber < this.playerNumberWaited;
    }

}
