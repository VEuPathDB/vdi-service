package org.veupathdb.service.vdi.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder("authorization")
public class DeleteCleanupRequestImpl implements DeleteCleanupRequest {
  @JsonProperty("authorization")
  private String authorization;

  @JsonProperty("authorization")
  public String getAuthorization() {
    return this.authorization;
  }

  @JsonProperty("authorization")
  public void setAuthorization(String authorization) {
    this.authorization = authorization;
  }
}
