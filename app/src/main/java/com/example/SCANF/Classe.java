package com.example.SCANF;

class Classe {

    private int ID_Classe;
    private String Libelle;

    public Classe() {
    }

    public Classe(int ID_Classe, String libelle) {
        this.ID_Classe = ID_Classe;
        this.Libelle = libelle;
    }

    public int getID_Classe() {
        return ID_Classe;
    }

    public void setID_Classe(int ID_Classe) {
        this.ID_Classe = ID_Classe;
    }

    public String getLibelle() {
        return Libelle;
    }

    public void setLibelle(String libelle) {
        Libelle = libelle;
    }
}
