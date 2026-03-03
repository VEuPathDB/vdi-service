package vdi.service.rest.config;

import org.gusdb.fgputil.db.platform.SupportedPlatform;
import org.veupathdb.lib.container.jaxrs.config.DbOptions;
import org.veupathdb.lib.container.jaxrs.config.DbOptionsImpl;
import org.veupathdb.lib.container.jaxrs.config.Options;
import vdi.config.raw.ManifestConfig;
import vdi.config.raw.common.LDAPConfig;
import vdi.config.raw.db.DatabaseConnectionConfig;
import vdi.config.raw.db.DirectDatabaseConnectionConfig;
import vdi.config.raw.db.LDAPDatabaseConnectionConfig;
import vdi.config.raw.vdi.InstallTargetConfig;
import vdi.core.config.StackConfig;
import vdi.core.config.core.*;
import vdi.model.field.SecretString;

import java.util.Optional;
import java.util.stream.Collectors;

public class ServiceConfig extends Options {
  private final StackConfig stackConfig;

  private final ManifestConfig manifestConfig;

  private final UploadConfig uploads;

  private final String[] projects;

  public ServiceConfig(StackConfig stackConfig, ManifestConfig manifestConfig) {
    this.stackConfig = stackConfig;
    this.manifestConfig = manifestConfig;
    this.uploads = new UploadConfig(stackConfig.getVdi().getRestService());
    this.projects = stackConfig.getVdi()
      .getInstallTargets()
      .stream()
      .map(InstallTargetConfig::getTargetName)
      .toArray(String[]::new);
  }

  public StackConfig getStackConfig() {
    return stackConfig;
  }

  public ManifestConfig getManifestConfig() {
    return manifestConfig;
  }

  public ContainerCoreConfig getCoreConfig() {
    return stackConfig.getCore();
  }

  public boolean isAuthEnabled() {
    return Optional.ofNullable(stackConfig.getCore().getAuthentication())
      .map(AuthenticationConfig::getAuthEnabled)
      .orElse(true);
  }

  public UploadConfig getUploads() {
    return uploads;
  }

  public boolean isTraceEnabled() {
    return stackConfig.getVdi().getRestService().getEnableJerseyTrace();
  }

  public String[] getProjects() {
    return projects;
  }

  @Override
  public Optional<String> getAdminAuthToken() {
    return Optional.ofNullable(getCoreConfig().getAuthentication())
      .map(AuthenticationConfig::getAdminToken)
      .map(SecretString::getAsString);
  }

  @Override
  public Optional<Integer> getServerPort() {
    return Optional.ofNullable(getCoreConfig().getHttp())
      .map(ServerConfig::getPortAsInt)
      .or(() -> Optional.of(80));
  }

  @Override
  public boolean getCorsEnabled() {
    return Optional.ofNullable(getCoreConfig().getHttp())
      .map(ServerConfig::getEnableCORS)
      .orElse(false);
  }

  @Override
  public Optional<String> getOAuthUrl() {
    return Optional.ofNullable(getCoreConfig().getAuthentication())
      .map(AuthenticationConfig::getOauth)
      .map(OAuthConfig::getUrl);
  }

  @Override
  public Optional<String> getOAuthClientId() {
    return Optional.ofNullable(getCoreConfig().getAuthentication())
      .map(AuthenticationConfig::getOauth)
      .map(OAuthConfig::getClientID);
  }

  @Override
  public Optional<String> getOAuthClientSecret() {
    return Optional.ofNullable(getCoreConfig().getAuthentication())
      .map(AuthenticationConfig::getOauth)
      .map(OAuthConfig::getClientSecret)
      .map(SecretString::getAsString);
  }

  @Override
  public Optional<String> getKeyStoreFile() {
    return Optional.ofNullable(getCoreConfig().getAuthentication())
      .map(AuthenticationConfig::getOauth)
      .map(OAuthConfig::getKeystoreFile);
  }

  @Override
  public Optional<String> getKeyStorePassPhrase() {
    return Optional.ofNullable(getCoreConfig().getAuthentication())
      .map(AuthenticationConfig::getOauth)
      .map(OAuthConfig::getKeystorePass)
      .map(SecretString::getAsString);
  }

  @Override
  public DbOptions getAppDbOpts() {
    return Optional.ofNullable(getCoreConfig().getDatabases())
      .map(CoreDatabaseSet::getApplicationDB)
      .map(ServiceConfig::toDBOptions)
      .orElseThrow();
  }

  @Override
  public DbOptions getAcctDbOpts() {
    return Optional.ofNullable(getCoreConfig().getDatabases())
      .map(CoreDatabaseSet::getAccountDB)
      .map(ServiceConfig::toDBOptions)
      .orElseThrow();
  }

  @Override
  public DbOptions getUserDbOpts() {
    return Optional.ofNullable(getCoreConfig().getDatabases())
      .map(CoreDatabaseSet::getUserDB)
      .map(ServiceConfig::toDBOptions)
      .orElseThrow();
  }

  @Override
  public String getUserDbSchema() {
    return Optional.ofNullable(getCoreConfig().getDatabases())
      .map(CoreDatabaseSet::getUserDB)
      .map(DatabaseConnectionConfig::getSchema)
      .orElseGet(super::getUserDbSchema);
  }

  @Override
  public Optional<String> getLdapServers() {
    return Optional.ofNullable(getCoreConfig().getLdap())
      .map(LDAPConfig::getServers)
      .map(addresses -> addresses.stream()
        .map(it -> String.format("%s:%d", it.getHost(), Optional.ofNullable(it.getPortAsInt()).orElse(389)))
        .collect(Collectors.joining(",")));
  }

  @Override
  public Optional<String> getDbLookupBaseDn() {
    return super.getDbLookupBaseDn();
  }

  private static DbOptions toDBOptions(DatabaseConnectionConfig config) {
    if (config instanceof LDAPDatabaseConnectionConfig ldap)
      return new DbOptionsImpl(
        ldap.getLookupCN(),
        null, // host
        null, // port
        null, // db name
        ldap.getUsername(),
        ldap.getPassword().getAsString(),
        null, // platform
        ldap.getPoolSizeAsInt(),
        "app-db", // display name
        200 // page size
      );

    if (config instanceof DirectDatabaseConnectionConfig direct)
      return new DbOptionsImpl(
        null, // lookup cn
        direct.getServer().getHost(),
        direct.getServer().getPortAsInt(),
        direct.getDbName(),
        direct.getUsername(),
        direct.getPassword().getAsString(),
        SupportedPlatform.toPlatform(direct.getPlatform()),
        direct.getPoolSizeAsInt(),
        "app-db", // display name
        200 // page size
      );

    throw new IllegalStateException("app db is not correctly configured");
  }
}
