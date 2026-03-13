package vdi.lane.soft_delete;

import org.slf4j.Logger;
import vdi.core.kafka.EventMessage;
import vdi.core.kafka.EventSource;
import vdi.logging.LoggingExt;
import vdi.logging.MarkedLogger;
import vdi.model.meta.UserID;

public class SoftDeleteContext {
  private final EventMessage msg;
  private final Logger logger;

  public SoftDeleteContext(EventMessage msg, Logger logger) {
    String[] loggerMarks = {
      LoggingExt.PrefixEventID + "=" + msg.getEventID(),
      LoggingExt.PrefixUserID + "=" + msg.getUserID(),
      LoggingExt.PrefixDatasetID + "=" + msg.getDatasetIdString()
    };

    this.msg = msg;
    this.logger = (logger instanceof MarkedLogger)
      ? ((MarkedLogger) logger).copy(loggerMarks)
      : new MarkedLogger(loggerMarks, logger);
  }

  public long getEventId() {
    return msg.getEventID();
  }

  public UserID getOwnerId() {
    return msg.getUserID();
  }

  public String getDatasetId() {
    return msg.getDatasetIdString();
  }

  public EventSource getEventSource() {
    return msg.getEventSource();
  }

  public Logger getLogger() {
    return logger;
  }
}
