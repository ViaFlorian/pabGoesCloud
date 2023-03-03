package de.viadee.pabbackend.entities;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("Stadt")
public class Stadt {

  @Id
  @Column("ID")
  private Long id;

  @Column("Name")
  private String name;

  @Column("OffizielleBezeichnung")
  private String offizielleBezeichnung;

  @JsonSerialize(using = ToStringSerializer.class)
  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getOffizielleBezeichnung() {
    return offizielleBezeichnung;
  }
}
