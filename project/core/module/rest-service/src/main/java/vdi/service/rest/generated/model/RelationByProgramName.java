package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonTypeName("program-name")
@JsonDeserialize(
    as = RelationByProgramNameImpl.class
)
public interface RelationByProgramName extends ImplicitRelation {
  RelationByProgramName.TypeType _DISCRIMINATOR_TYPE_NAME = RelationByProgramName.TypeType.PROGRAMNAME;

  @JsonProperty(JsonField.TYPE)
  TypeType getType();

  enum TypeType {
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

    TypeType(String name) {
      this.value = name;
    }
  }
}
