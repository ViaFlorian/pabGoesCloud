package de.viadee.pabbackend.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public final class FormatFactory {

  private FormatFactory() {
  }

  public static DecimalFormat deutschesDezimalzahlenFormatMitZweiNachkommastellen() {

    final String pattern = "###########0.00";

    final DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getNumberInstance(
        Locale.GERMANY);
    decimalFormat.applyPattern(pattern);

    return decimalFormat;

  }

  public static DecimalFormat deutschesDezimalzahlenFormatMitTausenderSeparator() {

    final String pattern = "#,##0.00";

    final DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getNumberInstance(
        Locale.GERMANY);
    decimalFormat.applyPattern(pattern);

    return decimalFormat;

  }

  public static DecimalFormat deutschesDezimalzahlenFormatMitDreiNachkommastellen() {

    final String pattern = "###########0.000";

    final DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getNumberInstance(
        Locale.GERMANY);
    decimalFormat.applyPattern(pattern);

    return decimalFormat;

  }

  public static DecimalFormat deutschesDezimalzahlenFormatMitSechsNachkommastellen() {

    final String pattern = "###########0.000000";

    final DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getNumberInstance(
        Locale.GERMANY);
    decimalFormat.applyPattern(pattern);

    return decimalFormat;

  }

  public static DateTimeFormatter deutscherMonatsnameUndJahrFormatter() {
    return DateTimeFormatter.ofPattern("MMMM yyyy", Locale.GERMAN);
  }

  public static DateTimeFormatter deutschesDatumsformat() {
    return DateTimeFormatter.ofPattern("dd.MM.yyyy");
  }

  public static DateTimeFormatter monatszahlJahreszahlFormat() {
    return DateTimeFormatter.ofPattern("MM/yyyy", Locale.GERMAN);
  }

  public static DateTimeFormatter jahreszahlMonatszahlFormat() {
    return DateTimeFormatter.ofPattern("yyyy/MM", Locale.GERMAN);
  }

}
