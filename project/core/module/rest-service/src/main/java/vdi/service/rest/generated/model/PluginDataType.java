package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;

@JsonDeserialize(
    as = PluginDataTypeImpl.class
)
public interface PluginDataType extends DatasetTypeOutput {
  @JsonProperty(JsonField.NAME)
  String getName();

  @JsonProperty(JsonField.NAME)
  void setName(String name);

  @JsonProperty(JsonField.VERSION)
  String getVersion();

  @JsonProperty(JsonField.VERSION)
  void setVersion(String version);

  @JsonProperty(JsonField.CATEGORY)
  String getCategory();

  @JsonProperty(JsonField.CATEGORY)
  void setCategory(String category);

  @JsonProperty(JsonField.USES_MAPPING_FILES)
  Boolean getUsesMappingFiles();

  @JsonProperty(JsonField.USES_MAPPING_FILES)
  void setUsesMappingFiles(Boolean usesMappingFiles);

  @JsonProperty(JsonField.MAX_FILE_SIZE)
  Long getMaxFileSize();

  @JsonProperty(JsonField.MAX_FILE_SIZE)
  void setMaxFileSize(Long maxFileSize);

  @JsonProperty(JsonField.ALLOWED_FILE_EXTENSIONS)
  List<String> getAllowedFileExtensions();

  @JsonProperty(JsonField.ALLOWED_FILE_EXTENSIONS)
  void setAllowedFileExtensions(List<String> allowedFileExtensions);
}
