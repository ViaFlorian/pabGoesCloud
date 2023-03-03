package de.viadee.pabbackend.entities;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

public class ErgebnisB006Pdf implements Comparable<ErgebnisB006Pdf> {

  private Mitarbeiter mitarbeiter;

  private Map<Integer, BigDecimal> stellenfaktoren = new HashMap<>();

  private List<ErgebnisB006ProjektstundensummenProMonat> listeProjektstundensummenProMonatJR;

  private List<ErgebnisB006StundenkontoProMonat> listeStundenkontoProMonatJR;

  private List<ErgebnisB006UrlaubskontoProMonat> listeUrlaubskontoJR;

  private BigDecimal gesamtsummeNormaleStundenJanuar;

  private BigDecimal gesamtsummeNormaleStundenFebruar;

  private BigDecimal gesamtsummeNormaleStundenMaerz;

  private BigDecimal gesamtsummeNormaleStundenApril;

  private BigDecimal gesamtsummeNormaleStundenMai;

  private BigDecimal gesamtsummeNormaleStundenJuni;

  private BigDecimal gesamtsummeNormaleStundenJuli;

  private BigDecimal gesamtsummeNormaleStundenAugust;

  private BigDecimal gesamtsummeNormaleStundenSeptember;

  private BigDecimal gesamtsummeNormaleStundenOktober;

  private BigDecimal gesamtsummeNormaleStundenNovember;

  private BigDecimal gesamtsummeNormaleStundenDezember;

  private BigDecimal gesamtsummeAngReiseJanuar;

  private BigDecimal gesamtsummeAngReiseFebruar;

  private BigDecimal gesamtsummeAngReiseMaerz;

  private BigDecimal gesamtsummeAngReiseApril;

  private BigDecimal gesamtsummeAngReiseMai;

  private BigDecimal gesamtsummeAngReiseJuni;

  private BigDecimal gesamtsummeAngReiseJuli;

  private BigDecimal gesamtsummeAngReiseAugust;

  private BigDecimal gesamtsummeAngReiseSeptember;

  private BigDecimal gesamtsummeAngReiseOktober;

  private BigDecimal gesamtsummeAngReiseNovember;

  private BigDecimal gesamtsummeAngReiseDezember;

  private Integer jahr;

  private Integer aktuellesJahr;

  private Integer hoechsterAbrechnungsmonatDesJahres;

  public Integer getJahr() {
    return jahr;
  }

  public void setJahr(final Integer jahr) {
    this.jahr = jahr;
  }

  public Integer getJahrZweistellig() {
    return jahr % 100;
  }

  public Integer getHoechsterAbrechnungsmonatDesJahres() {
    return hoechsterAbrechnungsmonatDesJahres;
  }

  public void setHoechsterAbrechnungsmonatDesJahres(
      Integer hoechsterAbgeschlossenerMonatDesJahres) {
    this.hoechsterAbrechnungsmonatDesJahres = hoechsterAbgeschlossenerMonatDesJahres;
  }

  public Mitarbeiter getMitarbeiter() {
    return mitarbeiter;
  }

  public void setMitarbeiter(Mitarbeiter mitarbeiter) {
    this.mitarbeiter = mitarbeiter;
  }

  public String getMitarbeiterFullName() {
    return mitarbeiter == null ? "" : mitarbeiter.getFullName();
  }

  public Integer getMitarbeiterPersonalnummer() {
    return mitarbeiter == null ? 0 : mitarbeiter.getPersonalNr();
  }

  public void setStellenfaktor(Integer monat, BigDecimal stellenfaktor) {
    stellenfaktoren.put(monat, stellenfaktor);
  }

  public BigDecimal getStellenfaktorJanuar() {
    return stellenfaktoren.get(1);
  }

  public BigDecimal getStellenfaktorFebruar() {
    return stellenfaktoren.get(2);
  }

  public BigDecimal getStellenfaktorMaerz() {
    return stellenfaktoren.get(3);
  }

  public BigDecimal getStellenfaktorApril() {
    return stellenfaktoren.get(4);
  }

  public BigDecimal getStellenfaktorMai() {
    return stellenfaktoren.get(5);
  }

  public BigDecimal getStellenfaktorJuni() {
    return stellenfaktoren.get(6);
  }

  public BigDecimal getStellenfaktorJuli() {
    return stellenfaktoren.get(7);
  }

  public BigDecimal getStellenfaktorAugust() {
    return stellenfaktoren.get(8);
  }

  public BigDecimal getStellenfaktorSeptember() {
    return stellenfaktoren.get(9);
  }

  public BigDecimal getStellenfaktorOktober() {
    return stellenfaktoren.get(10);
  }

  public BigDecimal getStellenfaktorNovember() {
    return stellenfaktoren.get(11);
  }

  public BigDecimal getStellenfaktorDezember() {
    return stellenfaktoren.get(12);
  }

  public BigDecimal getGesamtsummeStunden() {
    BigDecimal ergebnis = null;
    if (gesamtsummeNormaleStundenJanuar != null) {
      ergebnis = gesamtsummeNormaleStundenJanuar;
    }
    if (gesamtsummeNormaleStundenFebruar != null) {
      ergebnis = ergebnis == null ? gesamtsummeNormaleStundenFebruar
          : ergebnis.add(gesamtsummeNormaleStundenFebruar);
    }
    if (gesamtsummeNormaleStundenMaerz != null) {
      ergebnis = ergebnis == null ? gesamtsummeNormaleStundenMaerz
          : ergebnis.add(gesamtsummeNormaleStundenMaerz);
    }
    if (gesamtsummeNormaleStundenApril != null) {
      ergebnis = ergebnis == null ? gesamtsummeNormaleStundenApril
          : ergebnis.add(gesamtsummeNormaleStundenApril);
    }
    if (gesamtsummeNormaleStundenMai != null) {
      ergebnis = ergebnis == null ? gesamtsummeNormaleStundenMai
          : ergebnis.add(gesamtsummeNormaleStundenMai);
    }
    if (gesamtsummeNormaleStundenJuni != null) {
      ergebnis = ergebnis == null ? gesamtsummeNormaleStundenJuni
          : ergebnis.add(gesamtsummeNormaleStundenJuni);
    }
    if (gesamtsummeNormaleStundenJuli != null) {
      ergebnis = ergebnis == null ? gesamtsummeNormaleStundenJuli
          : ergebnis.add(gesamtsummeNormaleStundenJuli);
    }
    if (gesamtsummeNormaleStundenAugust != null) {
      ergebnis = ergebnis == null ? gesamtsummeNormaleStundenAugust
          : ergebnis.add(gesamtsummeNormaleStundenAugust);
    }
    if (gesamtsummeNormaleStundenSeptember != null) {
      ergebnis = ergebnis == null ? gesamtsummeNormaleStundenSeptember
          : ergebnis.add(gesamtsummeNormaleStundenSeptember);
    }
    if (gesamtsummeNormaleStundenOktober != null) {
      ergebnis = ergebnis == null ? gesamtsummeNormaleStundenOktober
          : ergebnis.add(gesamtsummeNormaleStundenOktober);
    }
    if (gesamtsummeNormaleStundenNovember != null) {
      ergebnis = ergebnis == null ? gesamtsummeNormaleStundenNovember
          : ergebnis.add(gesamtsummeNormaleStundenNovember);
    }
    if (gesamtsummeNormaleStundenDezember != null) {
      ergebnis = ergebnis == null ? gesamtsummeNormaleStundenDezember
          : ergebnis.add(gesamtsummeNormaleStundenDezember);
    }

    return ergebnis;
  }

  ;

  public BigDecimal getGesamtsummeAngReise() {
    BigDecimal ergebnis = null;
    if (gesamtsummeAngReiseJanuar != null) {
      ergebnis = gesamtsummeAngReiseJanuar;
    }
    if (gesamtsummeAngReiseFebruar != null) {
      ergebnis = ergebnis == null ? gesamtsummeAngReiseFebruar
          : ergebnis.add(gesamtsummeAngReiseFebruar);
    }
    if (gesamtsummeAngReiseMaerz != null) {
      ergebnis = ergebnis == null ? gesamtsummeAngReiseMaerz
          : ergebnis.add(gesamtsummeAngReiseMaerz);
    }
    if (gesamtsummeAngReiseApril != null) {
      ergebnis = ergebnis == null ? gesamtsummeAngReiseApril
          : ergebnis.add(gesamtsummeAngReiseApril);
    }
    if (gesamtsummeAngReiseMai != null) {
      ergebnis = ergebnis == null ? gesamtsummeAngReiseMai
          : ergebnis.add(gesamtsummeAngReiseMai);
    }
    if (gesamtsummeAngReiseJuni != null) {
      ergebnis = ergebnis == null ? gesamtsummeAngReiseJuni
          : ergebnis.add(gesamtsummeAngReiseJuni);
    }
    if (gesamtsummeAngReiseJuli != null) {
      ergebnis = ergebnis == null ? gesamtsummeAngReiseJuli
          : ergebnis.add(gesamtsummeAngReiseJuli);
    }
    if (gesamtsummeAngReiseAugust != null) {
      ergebnis = ergebnis == null ? gesamtsummeAngReiseAugust
          : ergebnis.add(gesamtsummeAngReiseAugust);
    }
    if (gesamtsummeAngReiseSeptember != null) {
      ergebnis = ergebnis == null ? gesamtsummeAngReiseSeptember
          : ergebnis.add(gesamtsummeAngReiseSeptember);
    }
    if (gesamtsummeAngReiseOktober != null) {
      ergebnis = ergebnis == null ? gesamtsummeAngReiseOktober
          : ergebnis.add(gesamtsummeAngReiseOktober);
    }
    if (gesamtsummeAngReiseNovember != null) {
      ergebnis = ergebnis == null ? gesamtsummeAngReiseNovember
          : ergebnis.add(gesamtsummeAngReiseNovember);
    }
    if (gesamtsummeAngReiseDezember != null) {
      ergebnis = ergebnis == null ? gesamtsummeAngReiseDezember
          : ergebnis.add(gesamtsummeAngReiseDezember);
    }

    return ergebnis;
  }

  ;

  public BigDecimal getGesamtsummeNormaleStundenJanuar() {
    return gesamtsummeNormaleStundenJanuar;
  }

  public void setGesamtsummeNormaleStundenJanuar(final BigDecimal gesamtsummeNormaleStundenJanuar) {
    this.gesamtsummeNormaleStundenJanuar = gesamtsummeNormaleStundenJanuar;
  }

  public BigDecimal getGesamtsummeNormaleStundenFebruar() {
    return gesamtsummeNormaleStundenFebruar;
  }

  public void setGesamtsummeNormaleStundenFebruar(
      final BigDecimal gesamtsummeNormaleStundenFebruar) {
    this.gesamtsummeNormaleStundenFebruar = gesamtsummeNormaleStundenFebruar;
  }

  public BigDecimal getGesamtsummeNormaleStundenMaerz() {
    return gesamtsummeNormaleStundenMaerz;
  }

  public void setGesamtsummeNormaleStundenMaerz(final BigDecimal gesamtsummeNormaleStundenMaerz) {
    this.gesamtsummeNormaleStundenMaerz = gesamtsummeNormaleStundenMaerz;
  }

  public BigDecimal getGesamtsummeNormaleStundenApril() {
    return gesamtsummeNormaleStundenApril;
  }

  public void setGesamtsummeNormaleStundenApril(final BigDecimal gesamtsummeNormaleStundenApril) {
    this.gesamtsummeNormaleStundenApril = gesamtsummeNormaleStundenApril;
  }

  public BigDecimal getGesamtsummeNormaleStundenMai() {
    return gesamtsummeNormaleStundenMai;
  }

  public void setGesamtsummeNormaleStundenMai(final BigDecimal gesamtsummeNormaleStundenMai) {
    this.gesamtsummeNormaleStundenMai = gesamtsummeNormaleStundenMai;
  }

  public BigDecimal getGesamtsummeNormaleStundenJuni() {
    return gesamtsummeNormaleStundenJuni;
  }

  public void setGesamtsummeNormaleStundenJuni(final BigDecimal gesamtsummeNormaleStundenJuni) {
    this.gesamtsummeNormaleStundenJuni = gesamtsummeNormaleStundenJuni;
  }

  public BigDecimal getGesamtsummeNormaleStundenJuli() {
    return gesamtsummeNormaleStundenJuli;
  }

  public void setGesamtsummeNormaleStundenJuli(final BigDecimal gesamtsummeNormaleStundenJuli) {
    this.gesamtsummeNormaleStundenJuli = gesamtsummeNormaleStundenJuli;
  }

  public BigDecimal getGesamtsummeNormaleStundenAugust() {
    return gesamtsummeNormaleStundenAugust;
  }

  public void setGesamtsummeNormaleStundenAugust(final BigDecimal gesamtsummeNormaleStundenAugust) {
    this.gesamtsummeNormaleStundenAugust = gesamtsummeNormaleStundenAugust;
  }

  public BigDecimal getGesamtsummeNormaleStundenSeptember() {
    return gesamtsummeNormaleStundenSeptember;
  }

  public void setGesamtsummeNormaleStundenSeptember(
      final BigDecimal gesamtsummeNormaleStundenSeptember) {
    this.gesamtsummeNormaleStundenSeptember = gesamtsummeNormaleStundenSeptember;
  }

  public BigDecimal getGesamtsummeNormaleStundenOktober() {
    return gesamtsummeNormaleStundenOktober;
  }

  public void setGesamtsummeNormaleStundenOktober(
      final BigDecimal gesamtsummeNormaleStundenOktober) {
    this.gesamtsummeNormaleStundenOktober = gesamtsummeNormaleStundenOktober;
  }

  public BigDecimal getGesamtsummeNormaleStundenNovember() {
    return gesamtsummeNormaleStundenNovember;
  }

  public void setGesamtsummeNormaleStundenNovember(
      final BigDecimal gesamtsummeNormaleStundenNovember) {
    this.gesamtsummeNormaleStundenNovember = gesamtsummeNormaleStundenNovember;
  }

  public BigDecimal getGesamtsummeNormaleStundenDezember() {
    return gesamtsummeNormaleStundenDezember;
  }

  public void setGesamtsummeNormaleStundenDezember(
      final BigDecimal gesamtsummeNormaleStundenDezember) {
    this.gesamtsummeNormaleStundenDezember = gesamtsummeNormaleStundenDezember;
  }

  public BigDecimal getGesamtsummeAngReiseJanuar() {
    return gesamtsummeAngReiseJanuar;
  }

  public void setGesamtsummeAngReiseJanuar(final BigDecimal gesamtsummeAngReiseJanuar) {
    this.gesamtsummeAngReiseJanuar = gesamtsummeAngReiseJanuar;
  }

  public BigDecimal getGesamtsummeAngReiseFebruar() {
    return gesamtsummeAngReiseFebruar;
  }

  public void setGesamtsummeAngReiseFebruar(final BigDecimal gesamtsummeAngReiseFebruar) {
    this.gesamtsummeAngReiseFebruar = gesamtsummeAngReiseFebruar;
  }

  public BigDecimal getGesamtsummeAngReiseMaerz() {
    return gesamtsummeAngReiseMaerz;
  }

  public void setGesamtsummeAngReiseMaerz(final BigDecimal gesamtsummeAngReiseMaerz) {
    this.gesamtsummeAngReiseMaerz = gesamtsummeAngReiseMaerz;
  }

  public BigDecimal getGesamtsummeAngReiseApril() {
    return gesamtsummeAngReiseApril;
  }

  public void setGesamtsummeAngReiseApril(final BigDecimal gesamtsummeAngReiseApril) {
    this.gesamtsummeAngReiseApril = gesamtsummeAngReiseApril;
  }

  public BigDecimal getGesamtsummeAngReiseMai() {
    return gesamtsummeAngReiseMai;
  }

  public void setGesamtsummeAngReiseMai(final BigDecimal gesamtsummeAngReiseMai) {
    this.gesamtsummeAngReiseMai = gesamtsummeAngReiseMai;
  }

  public BigDecimal getGesamtsummeAngReiseJuni() {
    return gesamtsummeAngReiseJuni;
  }

  public void setGesamtsummeAngReiseJuni(final BigDecimal gesamtsummeAngReiseJuni) {
    this.gesamtsummeAngReiseJuni = gesamtsummeAngReiseJuni;
  }

  public BigDecimal getGesamtsummeAngReiseJuli() {
    return gesamtsummeAngReiseJuli;
  }

  public void setGesamtsummeAngReiseJuli(final BigDecimal gesamtsummeAngReiseJuli) {
    this.gesamtsummeAngReiseJuli = gesamtsummeAngReiseJuli;
  }

  public BigDecimal getGesamtsummeAngReiseAugust() {
    return gesamtsummeAngReiseAugust;
  }

  public void setGesamtsummeAngReiseAugust(final BigDecimal gesamtsummeAngReiseAugust) {
    this.gesamtsummeAngReiseAugust = gesamtsummeAngReiseAugust;
  }

  public BigDecimal getGesamtsummeAngReiseSeptember() {
    return gesamtsummeAngReiseSeptember;
  }

  public void setGesamtsummeAngReiseSeptember(final BigDecimal gesamtsummeAngReiseSeptember) {
    this.gesamtsummeAngReiseSeptember = gesamtsummeAngReiseSeptember;
  }

  public BigDecimal getGesamtsummeAngReiseOktober() {
    return gesamtsummeAngReiseOktober;
  }

  public void setGesamtsummeAngReiseOktober(final BigDecimal gesamtsummeAngReiseOktober) {
    this.gesamtsummeAngReiseOktober = gesamtsummeAngReiseOktober;
  }

  public BigDecimal getGesamtsummeAngReiseNovember() {
    return gesamtsummeAngReiseNovember;
  }

  public void setGesamtsummeAngReiseNovember(final BigDecimal gesamtsummeAngReiseNovember) {
    this.gesamtsummeAngReiseNovember = gesamtsummeAngReiseNovember;
  }

  public BigDecimal getGesamtsummeAngReiseDezember() {
    return gesamtsummeAngReiseDezember;
  }

  public void setGesamtsummeAngReiseDezember(final BigDecimal gesamtsummeAngReiseDezember) {
    this.gesamtsummeAngReiseDezember = gesamtsummeAngReiseDezember;
  }

  public JRBeanCollectionDataSource getListeProjektstundensummenProMonatJR() {
    return new JRBeanCollectionDataSource(listeProjektstundensummenProMonatJR);
  }

  public void setListeProjektstundensummenProMonatJR(
      final List<ErgebnisB006ProjektstundensummenProMonat> listeProjektstundensummenProMonatJR) {
    this.listeProjektstundensummenProMonatJR = listeProjektstundensummenProMonatJR;
  }

  public JRBeanCollectionDataSource getListeStundenkontoProMonatJR() {
    return new JRBeanCollectionDataSource(listeStundenkontoProMonatJR);
  }

  public void setListeStundenkontoProMonatJR(
      final List<ErgebnisB006StundenkontoProMonat> listeStundenkontoProMonatJR) {
    this.listeStundenkontoProMonatJR = listeStundenkontoProMonatJR;
  }

  public JRBeanCollectionDataSource getListeUrlaubskontoJR() {
    return new JRBeanCollectionDataSource(listeUrlaubskontoJR);
  }

  public void setListeUrlaubskontoJR(
      final List<ErgebnisB006UrlaubskontoProMonat> listeUrlaubskontoJR) {
    this.listeUrlaubskontoJR = listeUrlaubskontoJR;
  }

  @Override
  public int compareTo(ErgebnisB006Pdf that) {
    if (this.mitarbeiter.compareTo(that.mitarbeiter) < 0) {
      return -1;
    } else if (this.mitarbeiter.compareTo(that.mitarbeiter) > 0) {
      return 1;
    }
    return 0;
  }

  public Integer getAktuellesJahr() {
    return aktuellesJahr;
  }

  public void setAktuellesJahr(final Integer aktuellesJahr) {
    this.aktuellesJahr = aktuellesJahr;
  }
}
