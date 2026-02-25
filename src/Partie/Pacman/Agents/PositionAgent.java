package Partie.Pacman.Agents;

import java.io.Serializable;

import org.json.JSONObject;

import Ressources.RequetesJSON;
import Ressources.TransformableJSON;

/** Position d'un agent */
public class PositionAgent implements Serializable, TransformableJSON{

	private static final long serialVersionUID = 1L;

	private int x;
	private int y;
	private int dir;

	/**
	 * Création d'une position à partir des 3 informations nécessaires
	 * @param x coordonnée x
	 * @param y coordonnée y
	 * @param dir direction vers laquelle on est orienté
	 */
	public PositionAgent(int x, int y, int dir) {
		this.x = x;
		this.y = y;
		this.dir = dir;
	}

	/**
	 * Copie d'un position existante
	 * @param other position à copier
	 */
	public PositionAgent(PositionAgent other) {
		this.x = other.x;
		this.y = other.y;
		this.dir = other.dir;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getDir() {
		return dir;
	}

	public void setDir(int dir) {
		this.dir = dir;
	}

	public String toString() {
		return "(" + x + "," + y + ")";
	}
	
	public boolean equals(PositionAgent other) {
		return (x == other.x) && (y == other.y);
	}


	// --- JSON ---
	public static PositionAgent fromJSON(JSONObject json) {
		try {
			int x = json.getInt(RequetesJSON.Attributs.PositionAgent.X);
			int y = json.getInt(RequetesJSON.Attributs.PositionAgent.Y);
			int dir = json.getInt(RequetesJSON.Attributs.PositionAgent.DIR);
			return new PositionAgent(x, y, dir);
		} catch (Exception e) {
			System.out.println("Error parsing JSON to PositionAgent: " + e.getMessage());
			return null;
		}
	}
	
	@Override
	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
		json.put(RequetesJSON.Attributs.PositionAgent.X, this.x);
		json.put(RequetesJSON.Attributs.PositionAgent.Y, this.y);
		json.put(RequetesJSON.Attributs.PositionAgent.DIR, this.dir);
		return json;
	}


}
