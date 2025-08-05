package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(
    as = DatasetPublicationImpl.class
)
public interface DatasetPublication {
  @JsonProperty(JsonField.IDENTIFIER)
  String getIdentifier();

  @JsonProperty(JsonField.IDENTIFIER)
  void setIdentifier(String identifier);

  @JsonProperty(
      value = JsonField.TYPE,
      defaultValue = "pmid"
  )
  TypeType getType();

  @JsonProperty(
      value = JsonField.TYPE,
      defaultValue = "pmid"
  )
  void setType(TypeType type);

  @JsonProperty(JsonField.CITATION)
  String getCitation();

  @JsonProperty(JsonField.CITATION)
  void setCitation(String citation);

  @JsonProperty(
      value = JsonField.IS_PRIMARY,
      defaultValue = "false"
  )
  boolean getIsPrimary();

  @JsonProperty(
      value = JsonField.IS_PRIMARY,
      defaultValue = "false"
  )
  void setIsPrimary(boolean isPrimary);

  enum TypeType {
    @JsonProperty("pmid")
    PMID("pmid"),

    @JsonProperty("doi")
    DOI("doi");

    private String name;

    TypeType(String name) {
      this.name = name;
    }
  }
}
