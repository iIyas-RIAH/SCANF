package com.example.SCANF;


class Professeur {

    private int ID_Professeur;
    private String NomComplet;
    private String Email;
    private String Mobile;
    private String Password;
    private String Photo;


    public Professeur() {
    }


    public Professeur(int ID_Professeur, String nomComplet, String email, String mobile, String photo) {
        this.ID_Professeur = ID_Professeur;
        NomComplet = nomComplet;
        Email = email;
        Mobile = mobile;
        Photo = photo;
    }

    public Professeur(int ID_Professeur, String nomComplet, String email, String mobile) {
        this.ID_Professeur = ID_Professeur;
        NomComplet = nomComplet;
        Email = email;
        Mobile = mobile;
    }

    public int getID_Professeur() {
        return ID_Professeur;
    }

    public void setID_Professeur(int ID_Professeur) {
        this.ID_Professeur = ID_Professeur;
    }

    public String getNomComplet() {
        return NomComplet;
    }

    public void setNomComplet(String nomComplet) {
        NomComplet = nomComplet;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getMobile() {
        return Mobile;
    }

    public void setMobile(String mobile) {
        Mobile = mobile;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getPhoto() { return Photo; }

    public void setPhoto(String photo) { Photo = photo; }
}
