package org.veupathdb.service.vdi.generated.model;

import java.lang.IllegalArgumentException;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class ExcludingMap extends HashMap<String, Object> {
  private static final Long serialVersionUID = 1L;

  Set<Pattern> additionalProperties =  new HashSet<Pattern>();

  @Override
  public Object put(String key, Object value) {
    if ( additionalProperties.size() == 0 )  {
      return super.put(key, value);
    }
    else {
      return setProperty(key, value);
    }
  }

  @Override
  public void putAll(Map<? extends String, ?> otherMap) {
    if ( additionalProperties.size() == 0 )  {
      super.putAll(otherMap);
    }
    else {
      for ( Map.Entry<? extends String, ?> entry : otherMap.entrySet() ) {
        setProperty(entry.getKey(), entry.getValue());
      }
    }
  }

  protected void addAcceptedPattern(Pattern pattern) {
    additionalProperties.add(pattern);
  }

  private Object setProperty(String key, Object value) {
    if ( additionalProperties.size() == 0 )  {
      return super.put(key, value);
    }
    else {
      for ( Pattern p : additionalProperties) {
        if ( p.matcher(key).matches() ) {
          return super.put(key, value);
        }
      }
      throw new IllegalArgumentException("property " + key + " is invalid according to RAML type");
    }
  }
}
