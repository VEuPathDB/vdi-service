package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.time.OffsetDateTime;
import java.util.List;

@JsonDeserialize(
    as = DatasetDetailsImpl.class
)
public interface DatasetDetails extends DatasetMetaBase {
  @JsonProperty(JsonField.NAME)
  String getName();

  @JsonProperty(JsonField.NAME)
  void setName(String name);

  @JsonProperty(JsonField.SUMMARY)
  String getSummary();

  @JsonProperty(JsonField.SUMMARY)
  void setSummary(String summary);

  @JsonProperty(JsonField.ORIGIN)
  String getOrigin();

  @JsonProperty(JsonField.ORIGIN)
  void setOrigin(String origin);

  @JsonProperty(JsonField.PROJECT_IDS)
  List<String> getProjectIds();

  @JsonProperty(JsonField.PROJECT_IDS)
  void setProjectIds(List<String> projectIds);

  @JsonProperty(JsonField.DEPENDENCIES)
  List<DatasetDependency> getDependencies();

  @JsonProperty(JsonField.DEPENDENCIES)
  void setDependencies(List<DatasetDependency> dependencies);

  @JsonProperty(JsonField.CATEGORY)
  String getCategory();

  @JsonProperty(JsonField.CATEGORY)
  void setCategory(String category);

  @JsonProperty(JsonField.CONTACTS)
  List<DatasetContact> getContacts();

  @JsonProperty(JsonField.CONTACTS)
  void setContacts(List<DatasetContact> contacts);

  @JsonProperty(JsonField.DESCRIPTION)
  String getDescription();

  @JsonProperty(JsonField.DESCRIPTION)
  void setDescription(String description);

  @JsonProperty(JsonField.HYPERLINKS)
  List<DatasetHyperlink> getHyperlinks();

  @JsonProperty(JsonField.HYPERLINKS)
  void setHyperlinks(List<DatasetHyperlink> hyperlinks);

  @JsonProperty(JsonField.ORGANISMS)
  List<String> getOrganisms();

  @JsonProperty(JsonField.ORGANISMS)
  void setOrganisms(List<String> organisms);

  @JsonProperty(JsonField.PUBLICATIONS)
  List<DatasetPublication> getPublications();

  @JsonProperty(JsonField.PUBLICATIONS)
  void setPublications(List<DatasetPublication> publications);

  @JsonProperty(JsonField.SHORT_NAME)
  String getShortName();

  @JsonProperty(JsonField.SHORT_NAME)
  void setShortName(String shortName);

  @JsonProperty(JsonField.SHORT_ATTRIBUTION)
  String getShortAttribution();

  @JsonProperty(JsonField.SHORT_ATTRIBUTION)
  void setShortAttribution(String shortAttribution);

  @JsonProperty(JsonField.PROPERTIES)
  com.fasterxml.jackson.databind.node.ObjectNode getProperties();

  @JsonProperty(JsonField.PROPERTIES)
  void setProperties(com.fasterxml.jackson.databind.node.ObjectNode properties);

  @JsonProperty(JsonField.DATASET_ID)
  String getDatasetId();

  @JsonProperty(JsonField.DATASET_ID)
  void setDatasetId(String datasetId);

  @JsonProperty(JsonField.OWNER)
  DatasetOwner getOwner();

  @JsonProperty(JsonField.OWNER)
  void setOwner(DatasetOwner owner);

  @JsonProperty(JsonField.IMPORT_MESSAGES)
  List<String> getImportMessages();

  @JsonProperty(JsonField.IMPORT_MESSAGES)
  void setImportMessages(List<String> importMessages);

  @JsonProperty(JsonField.SHARES)
  List<ShareOffer> getShares();

  @JsonProperty(JsonField.SHARES)
  void setShares(List<ShareOffer> shares);

  @JsonProperty(JsonField.DATASET_TYPE)
  DatasetTypeOutput getDatasetType();

  @JsonProperty(JsonField.DATASET_TYPE)
  void setDatasetType(DatasetTypeOutput datasetType);

  @JsonProperty(JsonField.VISIBILITY)
  DatasetVisibility getVisibility();

  @JsonProperty(JsonField.VISIBILITY)
  void setVisibility(DatasetVisibility visibility);

  @JsonProperty(JsonField.STATUS)
  DatasetStatusInfo getStatus();

  @JsonProperty(JsonField.STATUS)
  void setStatus(DatasetStatusInfo status);

  @JsonProperty(JsonField.CREATED)
  OffsetDateTime getCreated();

  @JsonProperty(JsonField.CREATED)
  void setCreated(OffsetDateTime created);

  @JsonProperty(JsonField.SOURCE_URL)
  String getSourceUrl();

  @JsonProperty(JsonField.SOURCE_URL)
  void setSourceUrl(String sourceUrl);

  @JsonProperty(JsonField.ORIGINAL_ID)
  String getOriginalId();

  @JsonProperty(JsonField.ORIGINAL_ID)
  void setOriginalId(String originalId);

  @JsonProperty(JsonField.REVISION_HISTORY)
  List<DatasetRevision> getRevisionHistory();

  @JsonProperty(JsonField.REVISION_HISTORY)
  void setRevisionHistory(List<DatasetRevision> revisionHistory);
}
