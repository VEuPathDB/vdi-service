package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "name",
    "version",
    "category",
    "maxFileSize",
    "usesDataProperties",
    "allowedFileExtensions"
})
public class PluginDataTypeImpl implements PluginDataType {
  @JsonProperty(JsonField.NAME)
  private String name;

  @JsonProperty(JsonField.VERSION)
  private String version;

  @JsonProperty(JsonField.CATEGORY)
  private String category;

  @JsonProperty(JsonField.MAX_FILE_SIZE)
  private Long maxFileSize;

  @JsonProperty(JsonField.USES_DATA_PROPERTIES)
  private Boolean usesDataProperties;

  @JsonProperty(JsonField.ALLOWED_FILE_EXTENSIONS)
  private List<String> allowedFileExtensions;

  @JsonProperty(JsonField.NAME)
  public String getName() {
    return this.name;
  }

  @JsonProperty(JsonField.NAME)
  public void setName(String name) {
    this.name = name;
  }

  @JsonProperty(JsonField.VERSION)
  public String getVersion() {
    return this.version;
  }

  @JsonProperty(JsonField.VERSION)
  public void setVersion(String version) {
    this.version = version;
  }

  @JsonProperty(JsonField.CATEGORY)
  public String getCategory() {
    return this.category;
  }

  @JsonProperty(JsonField.CATEGORY)
  public void setCategory(String category) {
    this.category = category;
  }

  @JsonProperty(JsonField.MAX_FILE_SIZE)
  public Long getMaxFileSize() {
    return this.maxFileSize;
  }

  @JsonProperty(JsonField.MAX_FILE_SIZE)
  public void setMaxFileSize(Long maxFileSize) {
    this.maxFileSize = maxFileSize;
  }

  @JsonProperty(JsonField.USES_DATA_PROPERTIES)
  public Boolean getUsesDataProperties() {
    return this.usesDataProperties;
  }

  @JsonProperty(JsonField.USES_DATA_PROPERTIES)
  public void setUsesDataProperties(Boolean usesDataProperties) {
    this.usesDataProperties = usesDataProperties;
  }

  @JsonProperty(JsonField.ALLOWED_FILE_EXTENSIONS)
  public List<String> getAllowedFileExtensions() {
    return this.allowedFileExtensions;
  }

  @JsonProperty(JsonField.ALLOWED_FILE_EXTENSIONS)
  public void setAllowedFileExtensions(List<String> allowedFileExtensions) {
    this.allowedFileExtensions = allowedFileExtensions;
  }
}
