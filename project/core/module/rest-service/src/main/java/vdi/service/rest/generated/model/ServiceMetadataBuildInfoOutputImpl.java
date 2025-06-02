package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "gitTag",
    "gitCommit",
    "gitBranch",
    "gitUrl",
    "buildId",
    "buildNumber",
    "buildTime"
})
public class ServiceMetadataBuildInfoOutputImpl implements ServiceMetadataBuildInfoOutput {
  @JsonProperty(JsonField.GIT_TAG)
  private String gitTag;

  @JsonProperty(JsonField.GIT_COMMIT)
  private String gitCommit;

  @JsonProperty(JsonField.GIT_BRANCH)
  private String gitBranch;

  @JsonProperty(JsonField.GIT_URL)
  private String gitUrl;

  @JsonProperty(JsonField.BUILD_ID)
  private String buildId;

  @JsonProperty(JsonField.BUILD_NUMBER)
  private String buildNumber;

  @JsonProperty(JsonField.BUILD_TIME)
  private String buildTime;

  @JsonProperty(JsonField.GIT_TAG)
  public String getGitTag() {
    return this.gitTag;
  }

  @JsonProperty(JsonField.GIT_TAG)
  public void setGitTag(String gitTag) {
    this.gitTag = gitTag;
  }

  @JsonProperty(JsonField.GIT_COMMIT)
  public String getGitCommit() {
    return this.gitCommit;
  }

  @JsonProperty(JsonField.GIT_COMMIT)
  public void setGitCommit(String gitCommit) {
    this.gitCommit = gitCommit;
  }

  @JsonProperty(JsonField.GIT_BRANCH)
  public String getGitBranch() {
    return this.gitBranch;
  }

  @JsonProperty(JsonField.GIT_BRANCH)
  public void setGitBranch(String gitBranch) {
    this.gitBranch = gitBranch;
  }

  @JsonProperty(JsonField.GIT_URL)
  public String getGitUrl() {
    return this.gitUrl;
  }

  @JsonProperty(JsonField.GIT_URL)
  public void setGitUrl(String gitUrl) {
    this.gitUrl = gitUrl;
  }

  @JsonProperty(JsonField.BUILD_ID)
  public String getBuildId() {
    return this.buildId;
  }

  @JsonProperty(JsonField.BUILD_ID)
  public void setBuildId(String buildId) {
    this.buildId = buildId;
  }

  @JsonProperty(JsonField.BUILD_NUMBER)
  public String getBuildNumber() {
    return this.buildNumber;
  }

  @JsonProperty(JsonField.BUILD_NUMBER)
  public void setBuildNumber(String buildNumber) {
    this.buildNumber = buildNumber;
  }

  @JsonProperty(JsonField.BUILD_TIME)
  public String getBuildTime() {
    return this.buildTime;
  }

  @JsonProperty(JsonField.BUILD_TIME)
  public void setBuildTime(String buildTime) {
    this.buildTime = buildTime;
  }
}
