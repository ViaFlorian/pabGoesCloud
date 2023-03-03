package de.viadee.pabbackend.entities;

import java.util.List;

public class ExcelImportErgebnis {

    private List<Projektstunde> importierteProjektstunden;

    private List<Projektstunde> importierteRufbereitschaft;

    private Arbeitsnachweis arbeitsnachweis;

    private List<Projektstunde> importierteSonderarbeitszeiten;

    private List<Beleg> importierteBelege;

    private List<Projektstunde> importierteReisezeiten;

    private List<Abwesenheit> importierteAbwesenheiten;

    private List<Fehlerlog> fehlerlog;

    public List<Projektstunde> getImportierteReisezeiten() {
        return importierteReisezeiten;
    }

    public void setImportierteReisezeiten(final List<Projektstunde> importierteReisezeiten) {
        this.importierteReisezeiten = importierteReisezeiten;

    }

    public List<Projektstunde> getImportierteProjektstunden() {
        return importierteProjektstunden;
    }

    public void setImportierteProjektstunden(final List<Projektstunde> importierteProjektstunden) {
        this.importierteProjektstunden = importierteProjektstunden;

    }

    public List<Projektstunde> getImportierteRufbereitschaft() {
        return importierteRufbereitschaft;
    }

    public void setImportierteRufbereitschaft(final List<Projektstunde> importierteRufbereitschaft) {
        this.importierteRufbereitschaft = importierteRufbereitschaft;

    }

    public Arbeitsnachweis getArbeitsnachweis() {
        return arbeitsnachweis;
    }

    public void setArbeitsnachweis(final Arbeitsnachweis arbeitsnachweis) {
        this.arbeitsnachweis = arbeitsnachweis;

    }

    public List<Projektstunde> getImportierteSonderarbeitszeiten() {
        return importierteSonderarbeitszeiten;
    }

    public void setImportierteSonderarbeitszeiten(final List<Projektstunde> importierteSonderarbeitszeiten) {
        this.importierteSonderarbeitszeiten = importierteSonderarbeitszeiten;

    }

    public List<Beleg> getImportierteBelege() {
        return importierteBelege;
    }

    public void setImportierteBelege(final List<Beleg> importierteBelege) {
        this.importierteBelege = importierteBelege;

    }

    public List<Abwesenheit> getImportierteAbwesenheiten() {
        return importierteAbwesenheiten;
    }

    public void setImportierteAbwesenheiten(final List<Abwesenheit> importierteAbwesenheiten) {
        this.importierteAbwesenheiten = importierteAbwesenheiten;

    }

    public List<Fehlerlog> getFehlerlog() {
        return fehlerlog;
    }

    public void setFehlerlog(final List<Fehlerlog> fehlerlog) {
        this.fehlerlog = fehlerlog;

    }

}
