package com.example.gsb_medecine;

public class Medicament {
    private int codeCIS;
    private String denomination;
    private String formePharmaceutique;
    private String voiesAdmin;
    private String titulaires;
    private String statutAdmin;
    private int nbMolecules;

    // Constructeur

    // Getter et Setter pour codeCIS
    public int getCodeCIS() {
        return codeCIS;
    }

    public void setCodeCIS(int codeCIS) {
        this.codeCIS = codeCIS;
    }

    // Getter et Setter pour denomination
    public String getDenomination() {
        return denomination;
    }

    public void setDenomination(String denomination) {
        this.denomination = denomination;
    }

    // Getter et Setter pour formePharmaceutique
    public String getFormePharmaceutique() {
        return formePharmaceutique;
    }

    public void setFormePharmaceutique(String formePharmaceutique) {
        this.formePharmaceutique = formePharmaceutique;
    }

    // Getter et Setter pour voiesAdmin
    public String getVoiesAdmin() {
        return voiesAdmin;
    }

    public void setVoiesAdmin(String voiesAdmin) {
        this.voiesAdmin = voiesAdmin;
    }

    // Getter et Setter pour titulaires
    public String getTitulaires() {
        return titulaires;
    }

    public void setTitulaires(String titulaires) {
        this.titulaires = titulaires;
    }

    // Getter et Setter pour statut administratif
    public String getStatutAdministratif() {return statutAdmin;}

    public void setStatutAdministratif(String statutAdmin) {
        this.statutAdmin = statutAdmin;
    }

    public String getnbMolecules() {return String.valueOf(nbMolecules);}

    public void setNbMolecule(int nbMolecules) {
        this.nbMolecules = nbMolecules;
    }
}
