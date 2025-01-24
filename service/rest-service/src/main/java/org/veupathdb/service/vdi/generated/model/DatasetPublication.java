package org.veupathdb.service.vdi.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(
    as = DatasetPublicationImpl.class
)
public interface DatasetPublication {
  @JsonProperty("citation")
  String getCitation();

  @JsonProperty("citation")
  void setCitation(String citation);

  @JsonProperty("pubMedId")
  String getPubMedId();

  @JsonProperty("pubMedId")
  void setPubMedId(String pubMedId);
}
