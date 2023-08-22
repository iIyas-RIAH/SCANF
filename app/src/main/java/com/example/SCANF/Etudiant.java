package com.example.SCANF;

class Etudiant {

    private int ID_Etudiant;
    private String NomComplet;
    private int NbrAbsence;

    public Etudiant() {
    }

    public Etudiant(int ID_Etudiant, String nomComplet, int nbrAbsence) {
        this.ID_Etudiant = ID_Etudiant;
        NomComplet = nomComplet;
        NbrAbsence = nbrAbsence;
    }

    public int getID_Etudiant() {
        return ID_Etudiant;
    }

    public void setID_Etudiant(int ID_Etudiant) {
        this.ID_Etudiant = ID_Etudiant;
    }

    public String getNomComplet() {
        return NomComplet;
    }

    public void setNomComplet(String nomComplet) {
        NomComplet = nomComplet;
    }

    public int getNbrAbsence() {
        return NbrAbsence;
    }

    public void setNbrAbsence(int nbrAbsence) {
        NbrAbsence = nbrAbsence;
    }
}
