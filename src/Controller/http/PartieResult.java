package Controller.http;

import java.util.ArrayList;

public class PartieResult {
    public Integer scorePacmans;
    public Integer scoreFantomes;
    public ArrayList<Integer> idPacmans;
    public ArrayList<Integer> idFantomes;
    public String vainqueur;
    public PartieResult(int scorePacmans, int scoreFantomes, ArrayList<Integer> idPacmans, ArrayList<Integer> idFantomes, String vainqueur) {
        this.scorePacmans = scorePacmans;
        this.scoreFantomes = scoreFantomes;
        this.idPacmans = idPacmans;
        this.idFantomes = idFantomes;
        this.vainqueur = vainqueur;
    }
}
