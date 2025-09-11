package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;

@JsonDeserialize(
    as = ContactsPatchImpl.class
)
public interface ContactsPatch {
  @JsonProperty("value")
  List<DatasetContact> getValue();

  @JsonProperty("value")
  void setValue(List<DatasetContact> value);
}
