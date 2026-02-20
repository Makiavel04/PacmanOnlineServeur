package Client;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import Ressources.RequetesJSON;

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
        try {
            while(true) {
                String line = bReader.readLine();
                if (line==null) break;
                JSONObject objReq = new JSONObject(line);
                String action = objReq.getString(RequetesJSON.Attributs.ACTION);
                this.clientHandler.gestionReception(action, objReq);
            }
        clientHandler.fermerConnection();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    public void ouvrirConnection() throws IOException {
        try {
            bReader = new BufferedReader(new InputStreamReader(so.getInputStream()));
        }
        catch (IOException e) {
            throw new IOException(e.getMessage());
        }
    }
}
