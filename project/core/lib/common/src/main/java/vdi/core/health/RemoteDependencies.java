package vdi.core.health;

import java.util.*;
import java.util.function.Supplier;

public final class RemoteDependencies {
  private static final Deque<Dependency> REGISTRY = new ArrayDeque<>();

  public static void register(Dependency dependency) {
    REGISTRY.addLast(dependency);
  }

  public static void register(
    String name,
    String host,
    int port,
    String protocol,
    Supplier<Map<String, Object>> extraFieldSupplier
  ) {
    if (extraFieldSupplier == null)
      REGISTRY.addLast(new StaticDependency(name, protocol, host, port, Collections.emptyMap()));
    else
      REGISTRY.addLast(new DynamicDependency(name, protocol, host, port, extraFieldSupplier));
  }

  public static Iterator<Dependency> iterator() {
    return new Iterator<>() {
      private final Iterator<Dependency> raw = REGISTRY.iterator();

      @Override
      public boolean hasNext() {
        return raw.hasNext();
      }

      @Override
      public Dependency next() {
        return raw.next();
      }
    };
  }
}
