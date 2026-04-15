package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;

@JsonDeserialize(
    as = DatasetSourcesPatchImpl.class
)
public interface DatasetSourcesPatch {
  @JsonProperty("value")
  List<DatasetSource> getValue();

  @JsonProperty("value")
  void setValue(List<DatasetSource> value);
}
