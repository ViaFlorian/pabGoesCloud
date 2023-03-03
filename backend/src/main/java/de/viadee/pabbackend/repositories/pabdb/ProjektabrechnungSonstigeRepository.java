package de.viadee.pabbackend.repositories.pabdb;

import de.viadee.pabbackend.entities.ProjektabrechnungSonstige;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjektabrechnungSonstigeRepository extends
    CrudRepository<ProjektabrechnungSonstige, Long> {

  @Query("""
      SELECT ProjektabrechnungSonstige.*
                                     FROM ProjektabrechnungSonstige
                                              INNER JOIN Projektabrechnung ON ProjektabrechnungSonstige.ProjektabrechnungID = Projektabrechnung.ID
                                     WHERE Projektabrechnung.ProjektID = :projektId
                                       AND Monat = :monat
                                       AND JAHR = :jahr
                                       AND COALESCE(MitarbeiterID, -1) = COALESCE(:mitarbeiterID, -1)
      """)
  ProjektabrechnungSonstige ladeProjektabrechnungSonstigeByUndMonatJahrProjektIdUndMitarbeiterId(
      Long projektId, Integer monat, Integer jahr, long mitarbeiterID);

  ProjektabrechnungSonstige findByProjektabrechnungIdAndMitarbeiterId(
      final Long projektabrechnungId, final Long mitarbeiterId);

  @Query("""
      SELECT *
       FROM ProjektabrechnungSonstige
       WHERE ProjektabrechnungID = :projektabrechnungId
         AND MitarbeiterID IS NULL
      """)
  ProjektabrechnungSonstige findByProjektabrechnungIdOhneMitarbeiterbezug(
      final Long projektabrechnungId);

  Iterable<ProjektabrechnungSonstige> findAllByProjektabrechnungId(final Long projektabrechnungId);
}
