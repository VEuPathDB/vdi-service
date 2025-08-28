package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ImplicitRelationType {
  @JsonProperty("publication")
  PUBLICATION("publication"),

  @JsonProperty("program-name")
  PROGRAMNAME("program-name"),

  @JsonProperty("project-name")
  PROJECTNAME("project-name");

  public final String value;

  public String getValue() {
    return this.value;
  }

  ImplicitRelationType(String name) {
    this.value = name;
  }
}
