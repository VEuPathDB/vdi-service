package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.time.OffsetDateTime;

@JsonDeserialize(
    as = RelatedDatasetInfoImpl.class
)
public interface RelatedDatasetInfo {
  @JsonProperty(JsonField.DATASET_ID)
  String getDatasetId();

  @JsonProperty(JsonField.DATASET_ID)
  void setDatasetId(String datasetId);

  @JsonProperty(JsonField.TYPE)
  DatasetTypeOutput getType();

  @JsonProperty(JsonField.TYPE)
  void setType(DatasetTypeOutput type);

  @JsonProperty(JsonField.NAME)
  String getName();

  @JsonProperty(JsonField.NAME)
  void setName(String name);

  @JsonProperty(JsonField.SUMMARY)
  String getSummary();

  @JsonProperty(JsonField.SUMMARY)
  void setSummary(String summary);

  @JsonProperty(JsonField.CREATED)
  OffsetDateTime getCreated();

  @JsonProperty(JsonField.CREATED)
  void setCreated(OffsetDateTime created);

  @JsonProperty(JsonField.RELATED_BY)
  ImplicitRelation getRelatedBy();

  @JsonProperty(JsonField.RELATED_BY)
  void setRelatedBy(ImplicitRelation relatedBy);
}
