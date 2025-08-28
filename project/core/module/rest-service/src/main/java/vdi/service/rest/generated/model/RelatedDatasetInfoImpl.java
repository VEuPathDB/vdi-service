package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.time.OffsetDateTime;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "datasetId",
    "owner",
    "type",
    "name",
    "summary",
    "created",
    "relatedBy"
})
public class RelatedDatasetInfoImpl implements RelatedDatasetInfo {
  @JsonProperty(JsonField.DATASET_ID)
  private String datasetId;

  @JsonProperty(JsonField.OWNER)
  private DatasetOwner owner;

  @JsonProperty(JsonField.TYPE)
  private DatasetTypeOutput type;

  @JsonProperty(JsonField.NAME)
  private String name;

  @JsonProperty(JsonField.SUMMARY)
  private String summary;

  @JsonProperty(JsonField.CREATED)
  private OffsetDateTime created;

  @JsonProperty(JsonField.RELATED_BY)
  private ImplicitRelation relatedBy;

  @JsonIgnore
  private Map<String, Object> additionalProperties = new ExcludingMap();

  @JsonProperty(JsonField.DATASET_ID)
  public String getDatasetId() {
    return this.datasetId;
  }

  @JsonProperty(JsonField.DATASET_ID)
  public void setDatasetId(String datasetId) {
    this.datasetId = datasetId;
  }

  @JsonProperty(JsonField.OWNER)
  public DatasetOwner getOwner() {
    return this.owner;
  }

  @JsonProperty(JsonField.OWNER)
  public void setOwner(DatasetOwner owner) {
    this.owner = owner;
  }

  @JsonProperty(JsonField.TYPE)
  public DatasetTypeOutput getType() {
    return this.type;
  }

  @JsonProperty(JsonField.TYPE)
  public void setType(DatasetTypeOutput type) {
    this.type = type;
  }

  @JsonProperty(JsonField.NAME)
  public String getName() {
    return this.name;
  }

  @JsonProperty(JsonField.NAME)
  public void setName(String name) {
    this.name = name;
  }

  @JsonProperty(JsonField.SUMMARY)
  public String getSummary() {
    return this.summary;
  }

  @JsonProperty(JsonField.SUMMARY)
  public void setSummary(String summary) {
    this.summary = summary;
  }

  @JsonProperty(JsonField.CREATED)
  public OffsetDateTime getCreated() {
    return this.created;
  }

  @JsonProperty(JsonField.CREATED)
  public void setCreated(OffsetDateTime created) {
    this.created = created;
  }

  @JsonProperty(JsonField.RELATED_BY)
  public ImplicitRelation getRelatedBy() {
    return this.relatedBy;
  }

  @JsonProperty(JsonField.RELATED_BY)
  public void setRelatedBy(ImplicitRelation relatedBy) {
    this.relatedBy = relatedBy;
  }

  @JsonAnyGetter
  public Map<String, Object> getAdditionalProperties() {
    return additionalProperties;
  }

  @JsonAnySetter
  public void setAdditionalProperties(String key, Object value) {
    this.additionalProperties.put(key, value);
  }
}
