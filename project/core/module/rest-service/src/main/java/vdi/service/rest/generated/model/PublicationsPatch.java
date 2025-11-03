package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;

@JsonDeserialize(
    as = PublicationsPatchImpl.class
)
public interface PublicationsPatch {
  @JsonProperty("value")
  List<DatasetPublication> getValue();

  @JsonProperty("value")
  void setValue(List<DatasetPublication> value);
}
