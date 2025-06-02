package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(
    as = ServiceMetadataBuildInfoOutputImpl.class
)
public interface ServiceMetadataBuildInfoOutput {
  @JsonProperty(JsonField.GIT_TAG)
  String getGitTag();

  @JsonProperty(JsonField.GIT_TAG)
  void setGitTag(String gitTag);

  @JsonProperty(JsonField.GIT_COMMIT)
  String getGitCommit();

  @JsonProperty(JsonField.GIT_COMMIT)
  void setGitCommit(String gitCommit);

  @JsonProperty(JsonField.GIT_BRANCH)
  String getGitBranch();

  @JsonProperty(JsonField.GIT_BRANCH)
  void setGitBranch(String gitBranch);

  @JsonProperty(JsonField.GIT_URL)
  String getGitUrl();

  @JsonProperty(JsonField.GIT_URL)
  void setGitUrl(String gitUrl);

  @JsonProperty(JsonField.BUILD_ID)
  String getBuildId();

  @JsonProperty(JsonField.BUILD_ID)
  void setBuildId(String buildId);

  @JsonProperty(JsonField.BUILD_NUMBER)
  String getBuildNumber();

  @JsonProperty(JsonField.BUILD_NUMBER)
  void setBuildNumber(String buildNumber);

  @JsonProperty(JsonField.BUILD_TIME)
  String getBuildTime();

  @JsonProperty(JsonField.BUILD_TIME)
  void setBuildTime(String buildTime);
}
