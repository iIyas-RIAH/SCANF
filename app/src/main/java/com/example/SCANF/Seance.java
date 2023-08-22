package com.example.SCANF;

import java.util.Date;


class Seance {
    private int ID_Seance;
    private String Classe;
    private int ID_Professeur;
    private String date;
    private String temps;
    private String matiere;

    public Seance(int idseance, String classe) {
    }

    public Seance(int ID_Seance, String Classe, int ID_Professeur, String date, String temps, String matiere) {
        this.ID_Seance = ID_Seance;
        this.Classe = Classe;
        this.ID_Professeur = ID_Professeur;
        this.date = date;
        this.temps = temps;
        this.matiere = matiere;
    }

    public Seance() {

    }

    public int getID_Seance() { return ID_Seance; }

    public void setID_Seance(int ID_Seance) { this.ID_Seance = ID_Seance; }

    public String getClasse() { return Classe; }

    public void setClasse(String Classe) { this.Classe = Classe; }

    public int getID_Professeur() {
        return ID_Professeur;
    }

    public void setID_Professeur(int ID_Professeur) {
        this.ID_Professeur = ID_Professeur;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMatiere() { return matiere; }

    public void setMatiere(String matiere) { this.matiere = matiere; }

    public String getTemps() {
        return temps;
    }

    public void setTemps(String temps) {
        this.temps = temps;
    }

}
