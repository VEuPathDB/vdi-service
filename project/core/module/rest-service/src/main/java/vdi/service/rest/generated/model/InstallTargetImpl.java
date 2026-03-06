package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "id",
    "name"
})
public class InstallTargetImpl implements InstallTarget {
  @JsonProperty(JsonField.ID)
  private String id;

  @JsonProperty(JsonField.NAME)
  private String name;

  @JsonProperty(JsonField.ID)
  public String getId() {
    return this.id;
  }

  @JsonProperty(JsonField.ID)
  public void setId(String id) {
    this.id = id;
  }

  @JsonProperty(JsonField.NAME)
  public String getName() {
    return this.name;
  }

  @JsonProperty(JsonField.NAME)
  public void setName(String name) {
    this.name = name;
  }
}
