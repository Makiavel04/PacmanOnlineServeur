import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import org.json.JSONException;
import org.json.JSONObject;

public class ClientListener extends Thread {

    private Socket so;
    private ClientHandler clientHandler;
    private BufferedReader bReader;

    public ClientListener (Socket so, ClientHandler c) {
        this.so = so;
        this.clientHandler = c;
    }

    @Override
    public void run() {
        System.out.println("ClientListener thread started");
        while(true) {
            try {
                String line = bReader.readLine();
                if (line==null) break;
                JSONObject objReq = new JSONObject(line);
                String action = objReq.getString("action");
                switch (action) {
                    case ("demanderAuthentification"):
                        break;
                    case("demanderPartie"):
                        break;
                    case ("envoyerAction"):
                        break;
                    default:
                        break;
                }
            }
            catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }
        clientHandler.closeConnexion();

    }

    public void openConnexion() throws IOException {
        try {
            bReader = new BufferedReader(new InputStreamReader(so.getInputStream()));
        }
        catch (IOException e) {
            throw new IOException(e.getMessage());
        }
    }
}
