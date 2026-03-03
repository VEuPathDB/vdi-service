package vdi.service.rest.health;

import org.slf4j.LoggerFactory;
import org.veupathdb.lib.container.jaxrs.health.Dependency;
import org.veupathdb.lib.container.jaxrs.health.ExternalDependency;
import vdi.core.health.RemoteDependencies;

import java.util.Iterator;

public class DependencySource implements org.veupathdb.lib.container.jaxrs.health.DependencySource {
  @Override
  public Iterator<Dependency> iterator() {
    return new Iterator<>() {
      private final Iterator<vdi.core.health.Dependency> raw = RemoteDependencies.INSTANCE.iterator();

      @Override
      public boolean hasNext() {
        return raw.hasNext();
      }

      @Override
      public Dependency next() {
        return new RemoteDependency(raw.next());
      }
    };
  }

  private static class RemoteDependency extends ExternalDependency {
    private final vdi.core.health.Dependency raw;

    public RemoteDependency(vdi.core.health.Dependency raw) {
      super(raw.getName());
      this.raw = raw;
    }

    @Override
    public TestResult test() {
      LoggerFactory.getLogger(getClass()).info("checking health for external dependency {}", name);

      var status = switch(raw.checkStatus()) {
        case Ok -> Status.ONLINE;
        case NotOk -> Status.OFFLINE;
        case Unknown -> Status.UNKNOWN;
      };

      var reachable = (status == Status.UNKNOWN && pinger.isReachable(raw.getHost(), raw.getPortAsInt()))
        || status == Status.ONLINE;

      return new TestResult(this, reachable, status, raw.getExtra());
    }

    @Override
    public void close() {}
  }
}
