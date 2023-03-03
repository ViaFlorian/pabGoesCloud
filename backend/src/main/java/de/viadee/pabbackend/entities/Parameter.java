package de.viadee.pabbackend.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;


@Table("Parameter")
public class Parameter {

  @Id
  @Column("ID")
  private Integer id;

  @Column("Key")
  private String key;

  @Column("Value")
  private String value;

  public Integer getId() {
    return id;
  }

  public void setId(final Integer id) {
    this.id = id;
  }

  public String getKey() {
    return key;
  }

  public void setKey(final String key) {
    this.key = key;
  }

  public String getValue() {
    return value;
  }

  public void setValue(final String value) {
    this.value = value;
  }
}
