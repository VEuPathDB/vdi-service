package org.veupathdb.service.vdi.generated.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Date;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "datasetID",
    "owner",
    "datasetType",
    "name",
    "summary",
    "description",
    "sourceUrl",
    "origin",
    "projectIDs",
    "visibility",
    "importMessages",
    "status",
    "shares",
    "files",
    "created"
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

  @JsonProperty("sourceUrl")
  private String sourceUrl;

  @JsonProperty("origin")
  private String origin;

  @JsonProperty("projectIDs")
  private List<String> projectIDs;

  @JsonProperty("visibility")
  private DatasetVisibility visibility;

  @JsonProperty(
      value = "importMessages",
      defaultValue = "[\n"
              + "\n"
              + "]"
  )
  private List<String> importMessages;

  @JsonProperty("status")
  private DatasetStatusInfo status;

  @JsonProperty("shares")
  private List<ShareOffer> shares;

  @JsonProperty("files")
  private List<FileSummary> files;

  @JsonProperty("created")
  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX"
  )
  @JsonDeserialize(
      using = TimestampDeserializer.class
  )
  private Date created;

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

  @JsonProperty("sourceUrl")
  public String getSourceUrl() {
    return this.sourceUrl;
  }

  @JsonProperty("sourceUrl")
  public void setSourceUrl(String sourceUrl) {
    this.sourceUrl = sourceUrl;
  }

  @JsonProperty("origin")
  public String getOrigin() {
    return this.origin;
  }

  @JsonProperty("origin")
  public void setOrigin(String origin) {
    this.origin = origin;
  }

  @JsonProperty("projectIDs")
  public List<String> getProjectIDs() {
    return this.projectIDs;
  }

  @JsonProperty("projectIDs")
  public void setProjectIDs(List<String> projectIDs) {
    this.projectIDs = projectIDs;
  }

  @JsonProperty("visibility")
  public DatasetVisibility getVisibility() {
    return this.visibility;
  }

  @JsonProperty("visibility")
  public void setVisibility(DatasetVisibility visibility) {
    this.visibility = visibility;
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

  @JsonProperty("files")
  public List<FileSummary> getFiles() {
    return this.files;
  }

  @JsonProperty("files")
  public void setFiles(List<FileSummary> files) {
    this.files = files;
  }

  @JsonProperty("created")
  public Date getCreated() {
    return this.created;
  }

  @JsonProperty("created")
  public void setCreated(Date created) {
    this.created = created;
  }
}
