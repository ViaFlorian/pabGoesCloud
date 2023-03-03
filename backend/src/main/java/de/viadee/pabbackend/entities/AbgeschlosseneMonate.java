package de.viadee.pabbackend.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;

@Table("AbgeschlosseneMonate")
public class AbgeschlosseneMonate {

    @Id
    @Column("ID")
    private Long id;

    @Column("Jahr")
    private Integer jahr;

    @Column("Monat")
    private Integer monat;

    @Column("AbgeschlossenAm")
    private LocalDate abgeschlossenAm;

    @Column("AbgeschlossenVonMitarbeiterID")
    private Long abgeschlossenVonMitarbeiterId;

    @Column("LodasExport")
    private byte[] lodasExport;

    @Column("LodasErzeugtMitarbeiterID")
    private Long lodasErzeugtMitarbeiterId;

    @Column("JahresuebersichtVersendetAm")
    private LocalDate jahresuebersichtVersendetAm;

    @Column("JahresuebersichtVersendetMitarbeiterID")
    private Long jahresuebersichtVersendetMitarbeiterId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getJahr() {
        return jahr;
    }

    public void setJahr(Integer jahr) {
        this.jahr = jahr;
    }

    public Integer getMonat() {
        return monat;
    }

    public void setMonat(Integer monat) {
        this.monat = monat;
    }

    public LocalDate getAbgeschlossenAm() {
        return abgeschlossenAm;
    }

    public void setAbgeschlossenAm(LocalDate abgeschlossenAm) {
        this.abgeschlossenAm = abgeschlossenAm;
    }

    public Long getAbgeschlossenVonMitarbeiterId() {
        return abgeschlossenVonMitarbeiterId;
    }

    public void setAbgeschlossenVonMitarbeiterId(Long abgeschlossenVonMitarbeiterId) {
        this.abgeschlossenVonMitarbeiterId = abgeschlossenVonMitarbeiterId;
    }

    public byte[] getLodasExport() {
        return lodasExport;
    }

    public void setLodasExport(byte[] lodasExport) {
        this.lodasExport = lodasExport;
    }

    public Long getLodasErzeugtMitarbeiterId() {
        return lodasErzeugtMitarbeiterId;
    }

    public void setLodasErzeugtMitarbeiterId(Long lodasErzeugtMitarbeiterId) {
        this.lodasErzeugtMitarbeiterId = lodasErzeugtMitarbeiterId;
    }

    public LocalDate getJahresuebersichtVersendetAm() {
        return jahresuebersichtVersendetAm;
    }

    public void setJahresuebersichtVersendetAm(LocalDate jahresuebersichtVersendetAm) {
        this.jahresuebersichtVersendetAm = jahresuebersichtVersendetAm;
    }

    public Long getJahresuebersichtVersendetMitarbeiterId() {
        return jahresuebersichtVersendetMitarbeiterId;
    }

    public void setJahresuebersichtVersendetMitarbeiterId(Long jahresuebersichtVersendetMitarbeiterId) {
        this.jahresuebersichtVersendetMitarbeiterId = jahresuebersichtVersendetMitarbeiterId;
    }
}
