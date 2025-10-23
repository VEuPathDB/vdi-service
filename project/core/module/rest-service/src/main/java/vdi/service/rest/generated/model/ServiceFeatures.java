package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;

@JsonDeserialize(
    as = ServiceFeaturesImpl.class
)
public interface ServiceFeatures {
  @JsonProperty("supportedArchiveTypes")
  List<String> getSupportedArchiveTypes();

  @JsonProperty("supportedArchiveTypes")
  void setSupportedArchiveTypes(List<String> supportedArchiveTypes);
}
