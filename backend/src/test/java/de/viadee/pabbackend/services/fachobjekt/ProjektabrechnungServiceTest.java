package de.viadee.pabbackend.services.fachobjekt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.viadee.pabbackend.common.MsSqlTestContainersTest;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jdbc.repository.config.EnableJdbcAuditing;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@EnableJdbcAuditing
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SqlConfig(transactionManager = "pabDbTransactionManager")
public class ProjektabrechnungServiceTest extends MsSqlTestContainersTest {

  @Autowired
  private ProjektabrechnungService projektabrechnungService;

  @Test
  @Transactional("pabDbTransactionManager")
  @Rollback()
  @Sql(scripts = {
      "file:src/test/resources/testdaten/services/datenbank/projektabrechnungFehlendTestdaten.sql"})
  public void testFehlendeProjektabrechnungenFuerZeitraum_ZeitraumNachAbrechnungsmonat() {
    LocalDate abWann = LocalDate.now();
    LocalDate bisWann = LocalDate.now();

    List fehlendeProjektabrechnungen = projektabrechnungService.fehlendeProjektabrechnungen(abWann,
        bisWann);

    assertTrue(fehlendeProjektabrechnungen.isEmpty());
  }

  @Test
  @Transactional("pabDbTransactionManager")
  @Rollback()
  @Sql(scripts = {
      "file:src/test/resources/testdaten/services/datenbank/projektabrechnungFehlendTestdaten.sql"})
  public void testFehlendeProjektabrechnungenFuerZeitraum_ZeitraumVorAbrechnungsmonat() {
    LocalDate abWann = LocalDate.now().minusMonths(3);
    LocalDate bisWann = LocalDate.now().minusMonths(2);

    List fehlendeProjektabrechnungen = projektabrechnungService.fehlendeProjektabrechnungen(abWann,
        bisWann);

    assertTrue(fehlendeProjektabrechnungen.isEmpty());
  }

  @Test
  @Transactional("pabDbTransactionManager")
  @Rollback()
  @Sql(scripts = {
      "file:src/test/resources/testdaten/services/datenbank/projektabrechnungFehlendTestdaten.sql"})
  public void testFehlendeProjektabrechnungenFuerZeitraum_ZeitraumEntsprichtAbrechnungsmonat() {
    LocalDate abWann = LocalDate.now().minusMonths(1);
    LocalDate bisWann = LocalDate.now().minusMonths(1);

    List fehlendeProjektabrechnungen = projektabrechnungService.fehlendeProjektabrechnungen(abWann,
        bisWann);

    assertFalse(fehlendeProjektabrechnungen.isEmpty());
    assertEquals(1, fehlendeProjektabrechnungen.size());
  }

  @Test
  @Transactional("pabDbTransactionManager")
  @Rollback()
  @Sql(scripts = {
      "file:src/test/resources/testdaten/services/datenbank/projektabrechnungFehlendTestdaten.sql"})
  public void testFehlendeProjektabrechnungenFuerZeitraum_ZeitraumEnthaeltAbrechnungsmonat() {
    LocalDate abWann = LocalDate.now().minusMonths(2);
    LocalDate bisWann = LocalDate.now();

    List fehlendeProjektabrechnungen = projektabrechnungService.fehlendeProjektabrechnungen(abWann,
        bisWann);

    assertFalse(fehlendeProjektabrechnungen.isEmpty());
    assertEquals(1, fehlendeProjektabrechnungen.size());
  }

}
