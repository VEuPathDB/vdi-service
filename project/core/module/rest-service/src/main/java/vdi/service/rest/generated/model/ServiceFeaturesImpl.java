package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder("supportedArchiveTypes")
public class ServiceFeaturesImpl implements ServiceFeatures {
  @JsonProperty("supportedArchiveTypes")
  private List<String> supportedArchiveTypes;

  @JsonProperty("supportedArchiveTypes")
  public List<String> getSupportedArchiveTypes() {
    return this.supportedArchiveTypes;
  }

  @JsonProperty("supportedArchiveTypes")
  public void setSupportedArchiveTypes(List<String> supportedArchiveTypes) {
    this.supportedArchiveTypes = supportedArchiveTypes;
  }
}
