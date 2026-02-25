package Ressources;

import org.json.JSONObject;

/** Interface pour les classes qui doivent pouvoir ếtre instancié depuis un JSONObject et qui doivent pouvoir être converti en JSONObject */
public interface TransformableJSON {
    /** Toute classe implémentant cette interface doit aussi posséder une méthode static fromJSON(JSONObject json) pour traduire un objet JSON en instance de cette classe */
    // public static TransformableJSON fromJSON(JSONObject json);

    /**
     * Convertit l'objet en JSON
     * @return JSONObject représentant l'objet
     */
    public JSONObject toJSON();
}
