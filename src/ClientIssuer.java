import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import org.json.JSONObject;

public class ClientIssuer extends Thread{

    private Socket so;
    private ClientHandler clientHandler;
    private PrintWriter pWriter;

    public ClientIssuer(Socket so, ClientHandler c) {
        this.so = so;
    }

    @Override
    public void run() {
        System.out.println("ClientIssuer thread started");
    }

    public void openConnexion() throws IOException {
        try {
            pWriter = new PrintWriter(so.getOutputStream(), true);
        }
        catch (IOException e) {
            throw new IOException(e.getMessage());
        }
    }

    public void sendConnexionResponse(boolean response) {
        JSONObject jsonObject = createJSONObject();
        jsonObject.put("reponseAuthentification", response);
        pWriter.println(jsonObject.toString());
    }

    public void sendFindMatchResponse(boolean response) {
        JSONObject jsonObject = createJSONObject();
        jsonObject.put("reponseDemandePartie", response);
        pWriter.println(jsonObject.toString());
    }

    public void notifyMatchStarted() {
        JSONObject jsonObject = createJSONObject();
        jsonObject.put("debutPartie", true);
        pWriter.println(jsonObject.toString());
    }

    public JSONObject createJSONObject() {
        JSONObject obj = new JSONObject();
        obj.put("client", clientHandler.getID());
        return obj;
    }

}
