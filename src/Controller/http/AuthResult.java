package Controller.http;

public class AuthResult {
    public int id;
    public boolean result;
    public String username;
    public String couleur;
    public String erreur;

    public AuthResult(int id, boolean result, String username, String couleur, String erreur) {
        this.id = id;
        this.result = result;
        this.username = username;
        this.couleur = couleur;
        this.erreur = erreur;
    }

    
}
