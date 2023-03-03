package de.viadee.pabbackend.repositories.pabdb;

import de.viadee.pabbackend.entities.ErgebnisB002RufbereitschaftenExcel;
import java.util.List;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

public interface ErgebnisB002RufbereitschaftenExcelRepository extends
    CrudRepository<ErgebnisB002RufbereitschaftenExcel, Long> {


  @Query("""
      SELECT 	anw.Monat
      		, anw.Jahr
      		, mitarbeiter.PersonalNr
      		, mitarbeiter.Vorname
      		, mitarbeiter.Nachname
      		, projekt.projektnummer
      		, projekt.bezeichnung AS projektbezeichnung
      		, projekt.ID
      		, anw.ID AS AnwID
      		, rufbereitschaft.TagVon
      		, rufbereitschaft.UhrzeitVon
      		, rufbereitschaft.TagBis
      		, rufbereitschaft.UhrzeitBis
      FROM Arbeitsnachweis anw
            
      LEFT JOIN (
      	SELECT pstd.TagVon, pstd.UhrzeitVon, pstd.TagBis, pstd.UhrzeitBis, pstd.ProjektID, pstd.ArbeitsnachweisID
      	FROM Projektstunde pstd
      	WHERE pstd.ProjektstundeTypID = (select ID from C_ProjektstundeTyp WHERE TextKurz = 'Ruf')
      ) rufbereitschaft
      	ON
      		rufbereitschaft.ArbeitsnachweisID = anw.ID
            
      INNER JOIN (
      	SELECT p.ID, p.Projektnummer, p.Bezeichnung
      	FROM Projekt p
            
      ) projekt
      	ON
      		projekt.ID = rufbereitschaft.ProjektID
            
      INNER JOIN (
      	SELECT ma.ID,  ma.PersonalNr, ma.Vorname, ma.Nachname, ma.SachbearbeiterID
      	FROM Mitarbeiter ma
      ) mitarbeiter
      	ON
      		mitarbeiter.ID = anw.MitarbeiterID
            
      INNER JOIN Mitarbeiter sachbearbeiter
      	ON sachbearbeiter.ID = mitarbeiter.SachbearbeiterID
            
      WHERE (anw.Monat = :monat AND anw.Jahr = :jahr)
      AND mitarbeiter.ID = coalesce(:mitarbeiterId, mitarbeiter.ID)
      AND sachbearbeiter.ID = coalesce(:sachbearbeiterId, sachbearbeiter.ID)
      AND anw.StatusID >= 20
            
      ORDER BY Jahr, Monat, PersonalNr, TagVon, UhrzeitVon
      """)
  List<ErgebnisB002RufbereitschaftenExcel> ladeB002RufbereitschaftenExcel(final int jahr,
      final int monat,
      final Long mitarbeiterId, final Long sachbearbeiterId);
}
