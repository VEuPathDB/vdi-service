package vdi.lane.reconciliation;

import org.slf4j.Logger;
import vdi.core.kafka.EventMessage;
import vdi.core.kafka.EventSource;
import vdi.logging.LoggingExt;
import vdi.logging.MarkedLogger;
import vdi.model.meta.UserID;

public class ReconcilerContext {
  private final long eventId;
  private final String datasetId;
  private final UserID ownerId;
  private final EventSource source;
  private final MarkedLogger logger;

  public ReconcilerContext(EventMessage event, Logger logger) {
    this(event.getEventID(), event.getDatasetIdString(), event.getUserID(), event.getEventSource(), logger);
  }

  public ReconcilerContext(
    long eventId,
    String datasetId,
    UserID ownerId,
    EventSource source,
    Logger logger
  ) {
    this.eventId = eventId;
    this.datasetId = datasetId;
    this.ownerId = ownerId;
    this.source = source;

    String[] loggerMarks = {
      LoggingExt.markString(LoggingExt.PrefixEventID, eventId),
      LoggingExt.markString(LoggingExt.PrefixUserID, ownerId),
      LoggingExt.markString(LoggingExt.PrefixDatasetID, datasetId)
    };

    this.logger = (logger instanceof MarkedLogger)
      ? ((MarkedLogger) logger).copy(loggerMarks)
      : new MarkedLogger(loggerMarks, logger);
  }

  public long getEventId() {
    return eventId;
  }

  public String getDatasetId() {
    return datasetId;
  }

  public UserID getOwnerId() {
    return ownerId;
  }

  public EventSource getSource() {
    return source;
  }

  public Logger getLogger() {
    return logger;
  }
}
