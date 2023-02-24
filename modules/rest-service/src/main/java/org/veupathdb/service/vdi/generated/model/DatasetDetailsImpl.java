package org.veupathdb.service.vdi.generated.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "datasetID",
    "owner",
    "datasetType",
    "name",
    "summary",
    "description",
    "projectIDs",
    "importMessages",
    "installMessages",
    "status",
    "shares"
})
public class DatasetDetailsImpl implements DatasetDetails {
  @JsonProperty("datasetID")
  private String datasetID;

  @JsonProperty("owner")
  private DatasetOwner owner;

  @JsonProperty("datasetType")
  private DatasetTypeInfo datasetType;

  @JsonProperty("name")
  private String name;

  @JsonProperty("summary")
  private String summary;

  @JsonProperty("description")
  private String description;

  @JsonProperty("projectIDs")
  private List<String> projectIDs;

  @JsonProperty(
      value = "importMessages",
      defaultValue = "[\n"
              + "\n"
              + "]"
  )
  private List<String> importMessages;

  @JsonProperty("installMessages")
  private DatasetDetails.InstallMessagesType installMessages;

  @JsonProperty("status")
  private DatasetStatusInfo status;

  @JsonProperty("shares")
  private List<ShareOffer> shares;

  @JsonProperty("datasetID")
  public String getDatasetID() {
    return this.datasetID;
  }

  @JsonProperty("datasetID")
  public void setDatasetID(String datasetID) {
    this.datasetID = datasetID;
  }

  @JsonProperty("owner")
  public DatasetOwner getOwner() {
    return this.owner;
  }

  @JsonProperty("owner")
  public void setOwner(DatasetOwner owner) {
    this.owner = owner;
  }

  @JsonProperty("datasetType")
  public DatasetTypeInfo getDatasetType() {
    return this.datasetType;
  }

  @JsonProperty("datasetType")
  public void setDatasetType(DatasetTypeInfo datasetType) {
    this.datasetType = datasetType;
  }

  @JsonProperty("name")
  public String getName() {
    return this.name;
  }

  @JsonProperty("name")
  public void setName(String name) {
    this.name = name;
  }

  @JsonProperty("summary")
  public String getSummary() {
    return this.summary;
  }

  @JsonProperty("summary")
  public void setSummary(String summary) {
    this.summary = summary;
  }

  @JsonProperty("description")
  public String getDescription() {
    return this.description;
  }

  @JsonProperty("description")
  public void setDescription(String description) {
    this.description = description;
  }

  @JsonProperty("projectIDs")
  public List<String> getProjectIDs() {
    return this.projectIDs;
  }

  @JsonProperty("projectIDs")
  public void setProjectIDs(List<String> projectIDs) {
    this.projectIDs = projectIDs;
  }

  @JsonProperty(
      value = "importMessages",
      defaultValue = "[\n"
              + "\n"
              + "]"
  )
  public List<String> getImportMessages() {
    return this.importMessages;
  }

  @JsonProperty(
      value = "importMessages",
      defaultValue = "[\n"
              + "\n"
              + "]"
  )
  public void setImportMessages(List<String> importMessages) {
    this.importMessages = importMessages;
  }

  @JsonProperty("installMessages")
  public DatasetDetails.InstallMessagesType getInstallMessages() {
    return this.installMessages;
  }

  @JsonProperty("installMessages")
  public void setInstallMessages(DatasetDetails.InstallMessagesType installMessages) {
    this.installMessages = installMessages;
  }

  @JsonProperty("status")
  public DatasetStatusInfo getStatus() {
    return this.status;
  }

  @JsonProperty("status")
  public void setStatus(DatasetStatusInfo status) {
    this.status = status;
  }

  @JsonProperty("shares")
  public List<ShareOffer> getShares() {
    return this.shares;
  }

  @JsonProperty("shares")
  public void setShares(List<ShareOffer> shares) {
    this.shares = shares;
  }

  @JsonInclude(JsonInclude.Include.NON_NULL)
  @JsonPropertyOrder
  public static class InstallMessagesTypeImpl implements DatasetDetails.InstallMessagesType {
    @JsonIgnore
    private Map<String, Object> additionalProperties = new ExcludingMap() {
       {
        addAcceptedPattern(Pattern.compile("[a-zA-Z]+"));
      }
    }
    ;

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
      return additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperties(String key, Object value) {
      this.additionalProperties.put(key, value);
    }
  }
}
