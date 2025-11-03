package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum DatasetPublicationType {
  @JsonProperty("pmid")
  PMID("pmid"),

  @JsonProperty("doi")
  DOI("doi");

  public final String value;

  public String getValue() {
    return this.value;
  }

  DatasetPublicationType(String name) {
    this.value = name;
  }
}
