package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;

@JsonDeserialize(
    as = DatasetZipDetailsImpl.class
)
public interface DatasetZipDetails {
  @JsonProperty(JsonField.ZIP_SIZE)
  long getZipSize();

  @JsonProperty(JsonField.ZIP_SIZE)
  void setZipSize(long zipSize);

  @JsonProperty(JsonField.CONTENTS)
  List<DatasetFileDetails> getContents();

  @JsonProperty(JsonField.CONTENTS)
  void setContents(List<DatasetFileDetails> contents);
}
