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

    /**
     * Tourne en permanence pour recevoir les messages du client et les traiter en fonction de leur action
     */
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

    /**
     * Ouvre la connexion en initialisant le BufferedReader pour recevoir les messages du client
     * @throws IOException si une erreur d'entrée/sortie se produit lors de l'ouverture de la connexion
     */
    public void ouvrirConnection() throws IOException {
        try {
            bReader = new BufferedReader(new InputStreamReader(so.getInputStream()));
        }
        catch (IOException e) {
            throw new IOException(e.getMessage());
        }
    }

    /**
     * Ferme le BufferedReader pour libérer les ressources associées à la connexion avec le client
     * @throws IOException si une erreur d'entrée/sortie se produit lors de la fermeture du BufferedReader
     */
    public void fermerReader() throws IOException {
        try {
            bReader.close();
        }
        catch (IOException e) {
            throw new IOException(e.getMessage());
        }
    }
}
