/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.config.internal.model.dsl.config;

import static org.mule.runtime.api.util.Preconditions.checkNotNull;
import org.mule.runtime.api.component.Component;
import org.mule.runtime.properties.api.ConfigurationProperty;

/**
 * Represents a configuration attribute.
 *
 * @since 4.0
 */
public class DefaultConfigurationProperty implements ConfigurationProperty {

  private Object source;
  private Object rawValue;
  private String key;

  /**
   * Creates a new configuration value
   *
   * @param source   the source of this configuration attribute. For instance, it may be an {@link Component} if it's source was
   *                 defined in the artifact configuration or it may be the deployment properties configured at deployment time.
   * @param key      the key of the configuration attribute to reference it.
   * @param rawValue the plain configuration value without resolution. A configuration value may contain reference to other
   *                 configuration attributes.
   */
  public DefaultConfigurationProperty(Object source, String key, Object rawValue) {
    checkNotNull(source, "source cannot be null");
    checkNotNull(rawValue, "rawValue cannot be null");
    checkNotNull(key, "key cannot be null");
    this.source = source;
    this.rawValue = rawValue;
    this.key = key;
  }


  @Override
  public Object getSource() {
    return source;
  }

  @Override
  public String getValue() {
    return rawValue.toString();
  }

  @Override
  public String getKey() {
    return key;
  }

}
