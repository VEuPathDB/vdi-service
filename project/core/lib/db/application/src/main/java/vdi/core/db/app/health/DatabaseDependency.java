package vdi.core.db.app.health;

import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.LoggerFactory;
import vdi.core.db.app.TargetDatabaseReference;
import vdi.core.health.StaticDependency;
import vdi.db.app.TargetDatabaseDetails;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DatabaseDependency extends StaticDependency {
  private final TargetDatabaseReference connection;

  public DatabaseDependency(TargetDatabaseReference connection) {
    this(connection, Collections.<String, Object>emptyMap());
  }

  public DatabaseDependency(TargetDatabaseReference connection, Map<String, Object> extra) {
    super(
      connection.getIdentifier(),
      "",
      connection.getDetails().getServer().getHost(),
      connection.getDetails().getServer().getPortAsInt(),
      appendDbInfo(extra, connection.getDetails(), (HikariDataSource) connection.getDataSource())
    );

    this.connection = connection;
  }

  @Override
  public Status checkStatus() {
    switch (this.connection.getDetails().getPlatform()) {
      case Postgres:
        return this.postgresStatus();
      case Oracle:
        return this.oracleStatus();
      default:
        throw new RuntimeException("illegal state");
    }
  }

  @Deprecated
  private Status oracleStatus() {
    return this.runQuery("SELECT 1 FROM dual");
  }

  private Status postgresStatus() {
    return this.runQuery("SELECT 1");
  }

  private Status runQuery(String query) {
    Connection connection = null;
    Statement  statement  = null;
    try {
      connection = this.connection.getDataSource().getConnection();
      statement = connection.createStatement();
      statement.execute(query);

      return Status.OK;
    } catch (Throwable e) {
      LoggerFactory.getLogger(getClass()).error("database health check failed", e);
      return Status.NOT_OK;
    } finally {
      try {
        if (statement != null)
          statement.close();
      } catch (SQLException ignored) {}
      try {
        if (connection != null)
          connection.close();
      } catch (SQLException ignored) {}
    }
  }

  private static final String EXTRA_FIELD_PLATFORM = "platform";

  private static final String EXTRA_FIELD_HOST = "host";

  private static final String EXTRA_FIELD_CONTROL_SCHEMA = "controlSchema";

  private static final String EXTRA_FIELD_ACTIVE_CONNECTIONS = "activeConnections";

  private static final String EXTRA_FIELD_IDLE_CONNECTIONS = "idleConnections";

  private static final String EXTRA_FIELD_POOL_INSTANCE = "poolInstance";

  private static Map<String, Object> appendDbInfo(
    Map<String, Object> extra,
    TargetDatabaseDetails entry,
    HikariDataSource ref
  ) {
    Map<String, Object> out = new HashMap<String, Object>(extra.size() + 6);

    out.put(EXTRA_FIELD_PLATFORM, entry.getPlatform().name());
    out.put(EXTRA_FIELD_HOST, entry.getServer().getHost());
    out.put(EXTRA_FIELD_CONTROL_SCHEMA, entry.getSchema());
    out.put(EXTRA_FIELD_ACTIVE_CONNECTIONS, ref.getHikariPoolMXBean().getActiveConnections());
    out.put(EXTRA_FIELD_IDLE_CONNECTIONS, ref.getHikariPoolMXBean().getIdleConnections());
    out.put(EXTRA_FIELD_POOL_INSTANCE, System.identityHashCode(ref));

    return out;
  }
}
