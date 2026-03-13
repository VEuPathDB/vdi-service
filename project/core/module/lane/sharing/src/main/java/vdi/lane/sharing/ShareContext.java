package vdi.lane.sharing;

import org.slf4j.Logger;
import vdi.core.kafka.EventMessage;
import vdi.core.kafka.EventSource;
import vdi.core.s3.DatasetObjectStore;
import vdi.logging.LoggingExt;
import vdi.logging.MarkedLogger;
import vdi.model.meta.UserID;

public class ShareContext {
  private final EventMessage msg;
  private final DatasetObjectStore store;
  private final MarkedLogger realLogger;

  private MarkedLogger ctxLogger;

  public ShareContext(
    EventMessage msg,
    DatasetObjectStore store,
    Logger logger
  ) {
    this.msg = msg;
    this.store = store;

    String[] loggerMarks = {
      LoggingExt.markString(LoggingExt.PrefixEventID, msg.getEventID()),
      LoggingExt.markString(LoggingExt.PrefixUserID, msg.getUserID()),
      LoggingExt.markString(LoggingExt.PrefixDatasetID, msg.getDatasetIdString())
    };

    this.realLogger = (logger instanceof MarkedLogger)
      ? ((MarkedLogger) logger).copy(loggerMarks)
      : new MarkedLogger(loggerMarks, logger);
    this.ctxLogger = this.realLogger;
  }

  public long getEventId() {
    return this.msg.getEventID();
  }

  public Logger getLogger() {
    return this.ctxLogger;
  }

  public UserID getOwnerId() {
    return this.msg.getUserID();
  }

  public String getDatasetId() {
    return this.msg.getDatasetIdString();
  }

  public DatasetObjectStore getStore() {
    return this.store;
  }

  public EventSource getEventSource() {
    return this.msg.getEventSource();
  }

  public void setLoggerContext(String installTargetId) {
    this.ctxLogger = realLogger.copy(new String[] {
      LoggingExt.markString(LoggingExt.PrefixInstallTarget, installTargetId)
    });
  }

  public void clearLoggerContext() {
    this.ctxLogger = realLogger;
  }
}
