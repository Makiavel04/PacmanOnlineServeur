import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class ServerController {
    private static int port = 8080;
    private static ServerSocket ecoute;
    private static Vector<ClientHandler> clients;
    private static Vector<MatchThread> matches;

    public ServerController(int p){
        ServerController.port = p;
        ServerController.clients = new Vector<ClientHandler>();
        ServerController.matches = new Vector<MatchThread>();
        System.out.println("Server set up");
        try {
            while (true) {
                ecoute = new ServerSocket(port);
                Socket so = ecoute.accept();
                ClientHandler clientHandler = new ClientHandler(so);
                clients.add(clientHandler);
                int idClient = clientHandler.getID();
                System.out.println("New client: "+ idClient +" on the server");
                if (!findMatch(idClient)) {
                    int matchId = createMatch(idClient);
                    connectToMatch(idClient, matchId);
                }
            }
        } catch (IOException e) {
            System.out.println("Problem\n"+ e);
        }
    }

    public boolean findMatch (int idClient) {
        for (int i = 0; i < matches.size(); i++) {
            MatchThread match = matches.get(i);
            if (!match.isFilled()) {
                match.connectClient(idClient);
                System.out.println("Match: " + i +"found for client: "+idClient);
                return true;
            }
        }
        return false;
    }

    public int createMatch (int idClient) {
        MatchThread match = new MatchThread(idClient);
        matches.add(match);
        match.connectClient(idClient);
        match.start();
        return match.getID();
    }

    public void connectToMatch (int idClient, int idMatch) {
        MatchThread match = matches.get(idMatch);
        match.connectClient(idClient);
        System.out.println("Client: " + idClient +"successfully connected to match: "+ idMatch);
    }

    public static ClientHandler getClient(int idClient) throws Exception {
        for (int i = 0; i < clients.size(); i++) {
            ClientHandler c = clients.get(i);
            if (c.getID() == idClient) {
                return c;
            }        
        }
        throw new Exception("Client not found");
    }

    public void removeClient(int idClient){
        for (int i = 0; i < clients.size(); i++) {
            ClientHandler c = clients.get(i);
            if (c.getID() == idClient) {
                clients.remove(i);
                System.out.println("Client " + idClient + " removed successfully");
            }
        }
    }

}
