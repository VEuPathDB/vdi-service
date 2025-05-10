package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;

@JsonDeserialize(
    as = BrokenDatasetInstallReportBodyImpl.class
)
public interface BrokenDatasetInstallReportBody {
  @JsonProperty(JsonField.DETAILS)
  List<BrokenDatasetInstallDetails> getDetails();

  @JsonProperty(JsonField.DETAILS)
  void setDetails(List<BrokenDatasetInstallDetails> details);

  @JsonProperty(JsonField.IDS)
  List<String> getIds();

  @JsonProperty(JsonField.IDS)
  void setIds(List<String> ids);
}
