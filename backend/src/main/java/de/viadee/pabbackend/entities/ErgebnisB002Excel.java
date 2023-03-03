package de.viadee.pabbackend.entities;

import java.util.List;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

public class ErgebnisB002Excel {

  private List<ErgebnisB002RufbereitschaftenExcel> listeRufbereitschaftenExcel;
  private List<ErgebnisB002SonderarbeitszeitenExcel> listeSonderarbeitszeitenExcel;

  public List<ErgebnisB002RufbereitschaftenExcel> getListeRufbereitschaftenExcel() {
    return listeRufbereitschaftenExcel;
  }

  public void setListeRufbereitschaftenExcel(
      final List<ErgebnisB002RufbereitschaftenExcel> listeRufbereitschaftenExcel) {
    this.listeRufbereitschaftenExcel = listeRufbereitschaftenExcel;
  }

  public JRBeanCollectionDataSource getListeRufbereitschaftenExcelJR() {
    return new JRBeanCollectionDataSource(listeRufbereitschaftenExcel);
  }

  public List<ErgebnisB002SonderarbeitszeitenExcel> getListeSonderarbeitszeitenExcel() {
    return listeSonderarbeitszeitenExcel;
  }

  public void setListeSonderarbeitszeitenExcel(
      final List<ErgebnisB002SonderarbeitszeitenExcel> listeSonderarbeitszeitenExcel) {
    this.listeSonderarbeitszeitenExcel = listeSonderarbeitszeitenExcel;
  }

  public JRBeanCollectionDataSource getListeSonderarbeitszeitenExcelJR() {
    return new JRBeanCollectionDataSource(listeSonderarbeitszeitenExcel);
  }

}
