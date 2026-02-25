package Partie.Pacman.Game;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import Partie.Pacman.Agents.PositionAgent;
import Ressources.RequetesJSON;
import Ressources.TransformableJSON;


public class Maze implements Serializable, Cloneable, TransformableJSON {

	private static final long serialVersionUID = 1L;
	/**
	 * Les differentes directions possibles pour les actions et les orientations des
	 * agents
	 */
	public static int NORTH = 0;
	public static int SOUTH = 1;
	public static int EAST = 2;
	public static int WEST = 3;
	public static int STOP = 4;

	private int size_x;
	private int size_y;

	/**
	 * Les elements du labyrinthe
	 */
	private boolean walls[][];
	private boolean food[][];
	private boolean capsules[][];

	/**
	 * Les positions initiales des agents
	 */
	private ArrayList<PositionAgent> pacman_start;
	private ArrayList<PositionAgent> ghosts_start;

	public Maze(String filename) throws Exception {
		try {
			System.out.println("Layout file is " + filename);
			// Lecture du fichier pour determiner la taille du labyrinthe
			InputStream ips = new FileInputStream(filename);
			InputStreamReader ipsr = new InputStreamReader(ips);
			BufferedReader br = new BufferedReader(ipsr);
			String ligne;
			int nbX = 0;
			int nbY = 0;
			while ((ligne = br.readLine()) != null) {
				ligne = ligne.trim();
				if (nbX == 0) {
					nbX = ligne.length();
				} else if (nbX != ligne.length()) {
					ips.close();
					br.close();
					throw new Exception("Wrong Input Format: all lines must have the same size");
				}
				nbY++;
			}
			br.close();
			System.out.println("### Size of maze is " + nbX + ";" + nbY);

			// Initialisation du labyrinthe
			size_x = nbX;
			size_y = nbY;
			walls = new boolean[size_x][size_y];
			food = new boolean[size_x][size_y];
			capsules = new boolean[size_x][size_y];

			pacman_start = new ArrayList<PositionAgent>();
			ghosts_start = new ArrayList<PositionAgent>();

			// Lecture du fichier pour mettre a jour le labyrinthe
			ips = new FileInputStream(filename);
			ipsr = new InputStreamReader(ips);
			br = new BufferedReader(ipsr);
			int y = 0;
			while ((ligne = br.readLine()) != null) {
				ligne = ligne.trim();

				for (int x = 0; x < ligne.length(); x++) {
					if (ligne.charAt(x) == '%')
						walls[x][y] = true;
					else
						walls[x][y] = false;
					if (ligne.charAt(x) == '.')
						food[x][y] = true;
					else
						food[x][y] = false;
					if (ligne.charAt(x) == 'o')
						capsules[x][y] = true;
					else
						capsules[x][y] = false;
					if (ligne.charAt(x) == 'P') {
						pacman_start.add(new PositionAgent(x, y, Maze.NORTH));
					}
					if (ligne.charAt(x) == 'G') {
						ghosts_start.add(new PositionAgent(x, y, Maze.NORTH));
					}
				}
				y++;
			}
			br.close();

			if (pacman_start.size() == 0)
				throw new Exception("Wrong input format: must specify a Pacman start");

			// On verifie que le labyrinthe est clos
			for (int x = 0; x < size_x; x++)
				if (!walls[x][0])
					throw new Exception("Wrong input format: the maze must be closed");
			for (int x = 0; x < size_x; x++)
				if (!walls[x][size_y - 1])
					throw new Exception("Wrong input format: the maze must be closed");
			for (y = 0; y < size_y; y++)
				if (!walls[0][y])
					throw new Exception("Wrong input format: the maze must be closed");
			for (y = 0; y < size_y; y++)
				if (!walls[size_x - 1][y])
					throw new Exception("Wrong input format: the maze must be closed");
			System.out.println("### Maze loaded.");

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Probleme a la lecture du fichier: " + e.getMessage());
		}
	}

	private Maze(int s_x, int s_y, boolean[][] walls, boolean[][] food, boolean[][] capsules, ArrayList<PositionAgent> pacman_start, ArrayList<PositionAgent> ghosts_start) {
		this.size_x = s_x;
		this.size_y = s_y;
		this.walls = walls;
		this.food = food;
		this.capsules = capsules;
		this.pacman_start = pacman_start;
		this.ghosts_start = ghosts_start;
	}

	/**
	 * Renvoie la taille X du labyrtinhe
	 */
	public int getSizeX() {
		return (size_x);
	}

	/**
	 * Renvoie la taille Y du labyrinthe
	 */
	public int getSizeY() {
		return (size_y);
	}

	/**
	 * Permet de savoir si il y a un mur
	 */
	public boolean isWall(int x, int y) {
		assert ((x >= 0) && (x < size_x));
		assert ((y >= 0) && (y < size_y));
		return (walls[x][y]);
	}

	/**
	 * Permet de savoir si il y a de la nourriture
	 */
	public boolean isFood(int x, int y) {
		assert ((x >= 0) && (x < size_x));
		assert ((y >= 0) && (y < size_y));
		return (food[x][y]);
	}

	public void setFood(int x, int y, boolean b) {
		food[x][y] = b;
	}

	public boolean hasFoodLeft(){
		for(int i=0;i<size_x;i++){
			for(int j=0;j<size_y;j++){
				if(this.isFood(i,j) || this.isCapsule(i,j)){
					return true;
				}
			}
		}
		return false;
	}
	

	/**
	 * Permet de savoir si il y a une capsule
	 */
	public boolean isCapsule(int x, int y) {
		assert ((x >= 0) && (x < size_x));
		assert ((y >= 0) && (y < size_y));
		return (capsules[x][y]);
	}

	public void setCapsule(int x, int y, boolean b) {
		capsules[x][y] = b;
	}
	

	/**
	 * Renvoie le nombre de pacmans
	 * 
	 * @return
	 */
	public int getInitNumberOfPacmans() {
		return (pacman_start.size());
	}

	/**
	 * Renvoie le nombre de fantomes
	 * 
	 * @return
	 */
	public int getInitNumberOfGhosts() {
		return (ghosts_start.size());
	}

	public ArrayList<PositionAgent> getPacman_start() {
		return pacman_start;
	}

	public void setPacman_start(ArrayList<PositionAgent> pacman_start) {
		this.pacman_start = pacman_start;
	}

	public ArrayList<PositionAgent> getGhosts_start() {
		return ghosts_start;
	}

	public void setGhosts_start(ArrayList<PositionAgent> ghosts_start) {
		this.ghosts_start = ghosts_start;
	}

	public String toString() {
		String s = "Maze\n";
		s += plateauToString();
		s += "\nPosition agents fantom :";
		for (PositionAgent pa : ghosts_start) {
			s += pa + " ";
		}
		s += "\nPosition agents pacman :";
		for (PositionAgent pa : pacman_start) {
			s += pa + " ";
		}
		return s;
	}

	public String plateauToString() {
		String s = "";
		for (int i = 0; i < size_x; i++) {
			for (int j = 0; j < size_y; j++) {
				if (walls[i][j])
					s += "X";
				else if (food[i][j])
					s += "f";
				else if (capsules[i][j])
					s += "c";
				else
					s += " ";
			}
			s += "\n";
		}
		return s;
	}


	@Override
	public Object clone() throws CloneNotSupportedException {
		return (Maze) super.clone();
	}

	// --- JSON ---
	public static Maze fromJSON(JSONObject json) {
		try{
			int size_x = json.getInt(RequetesJSON.Attributs.Plateau.SIZE_X);
			int size_y = json.getInt(RequetesJSON.Attributs.Plateau.SIZE_Y);

			boolean[][] walls = new boolean[size_x][size_y];
			JSONArray jsonWalls = json.getJSONArray(RequetesJSON.Attributs.Plateau.WALLS);
			for (int i = 0; i < size_x; i++) {
				for (int j = 0; j < size_y; j++) {
					walls[i][j] = jsonWalls.getJSONArray(i).getBoolean(j);
				}
			}

			boolean[][] food = new boolean[size_x][size_y];
			JSONArray jsonFood = json.getJSONArray(RequetesJSON.Attributs.Plateau.FOOD);
			for (int i = 0; i < size_x; i++) {
				for (int j = 0; j < size_y; j++) {
					food[i][j] = jsonFood.getJSONArray(i).getBoolean(j);
				}
			}

			boolean[][] capsules = new boolean[size_x][size_y];
			JSONArray jsonCapsules = json.getJSONArray(RequetesJSON.Attributs.Plateau.CAPSULES);
			for (int i = 0; i < size_x; i++) {
				for (int j = 0; j < size_y; j++) {
					capsules[i][j] = jsonCapsules.getJSONArray(i).getBoolean(j);
				}
			}

			ArrayList<PositionAgent> pacman_start = new ArrayList<>();
			JSONArray jsonPacmanPos = json.getJSONArray(RequetesJSON.Attributs.Plateau.PACMAN_START);
	        for (int i = 0; i < jsonPacmanPos.length(); i++) {
	            pacman_start.add(PositionAgent.fromJSON(jsonPacmanPos.getJSONObject(i)));
	        }

	        ArrayList<PositionAgent> ghosts_start = new ArrayList<>();
			JSONArray jsonGhostsPos = json.getJSONArray(RequetesJSON.Attributs.Plateau.GHOSTS_START);
	        for (int i = 0; i < jsonGhostsPos.length(); i++) {
	            ghosts_start.add(PositionAgent.fromJSON(jsonGhostsPos.getJSONObject(i)));
			}

			return new Maze(size_x, size_y, walls, food, capsules, pacman_start, ghosts_start);
		}catch(Exception e){
			System.out.println("Error parsing JSON to Maze: " + e.getMessage());
			return null;
		}
	}

	@Override
	public JSONObject toJSON() {
		JSONObject json = new JSONObject();

		json.put(RequetesJSON.Attributs.Plateau.SIZE_X, this.size_x);
		json.put(RequetesJSON.Attributs.Plateau.SIZE_Y, this.size_y);

		JSONArray jsonWalls = new JSONArray(this.walls);
		json.put(RequetesJSON.Attributs.Plateau.WALLS, jsonWalls);

		JSONArray jsonFood = new JSONArray(this.food);
		json.put(RequetesJSON.Attributs.Plateau.FOOD, jsonFood);

		JSONArray jsonCapsules = new JSONArray(this.capsules);
		json.put(RequetesJSON.Attributs.Plateau.CAPSULES, jsonCapsules);


		JSONArray jsonPacmanPos = new JSONArray();
        for (PositionAgent pos : this.pacman_start) {
            JSONObject posJson = pos.toJSON();
            jsonPacmanPos.put(posJson);
        }
		json.put(RequetesJSON.Attributs.Plateau.PACMAN_START, jsonPacmanPos);

        JSONArray jsonGhostsPos = new JSONArray();
        for (PositionAgent pos : this.ghosts_start) {
            JSONObject posJson = pos.toJSON();
            jsonGhostsPos.put(posJson);
        }
		json.put(RequetesJSON.Attributs.Plateau.GHOSTS_START, jsonGhostsPos);

		return json;
	}
	
}
