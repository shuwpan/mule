/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.container.internal;

import org.mule.runtime.container.api.MuleContainerClassLoaderWrapper;
import org.mule.runtime.module.artifact.api.classloader.ArtifactClassLoader;
import org.mule.runtime.module.artifact.api.classloader.ClassLoaderLookupPolicy;

/**
 * Default implementation of {@link MuleContainerClassLoaderWrapper}.
 *
 * @since 4.6
 */
public class DefaultMuleContainerClassLoaderWrapper implements MuleContainerClassLoaderWrapper {

  private final ArtifactClassLoader containerClassLoader;

  public DefaultMuleContainerClassLoaderWrapper(ArtifactClassLoader containerClassLoader) {
    this.containerClassLoader = containerClassLoader;
  }

  @Override
  public ArtifactClassLoader getContainerClassLoader() {
    return containerClassLoader;
  }

  @Override
  public ClassLoaderLookupPolicy getContainerClassLoaderLookupPolicy() {
    return containerClassLoader.getClassLoaderLookupPolicy();
  }
}
