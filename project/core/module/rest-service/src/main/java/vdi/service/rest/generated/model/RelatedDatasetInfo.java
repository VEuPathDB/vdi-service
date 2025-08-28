package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.time.OffsetDateTime;
import java.util.Map;

@JsonDeserialize(
    as = RelatedDatasetInfoImpl.class
)
public interface RelatedDatasetInfo {
  @JsonProperty(JsonField.DATASET_ID)
  String getDatasetId();

  @JsonProperty(JsonField.DATASET_ID)
  void setDatasetId(String datasetId);

  @JsonProperty(JsonField.OWNER)
  DatasetOwner getOwner();

  @JsonProperty(JsonField.OWNER)
  void setOwner(DatasetOwner owner);

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

  @JsonAnyGetter
  Map<String, Object> getAdditionalProperties();

  @JsonAnySetter
  void setAdditionalProperties(String key, Object value);
}
