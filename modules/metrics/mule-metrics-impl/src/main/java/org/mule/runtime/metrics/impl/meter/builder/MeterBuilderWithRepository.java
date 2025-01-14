/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.metrics.impl.meter.builder;

import org.mule.runtime.metrics.api.meter.builder.MeterBuilder;
import org.mule.runtime.metrics.impl.meter.repository.MeterRepository;

/**
 * A builder that has an internal repository for builders.
 */
public interface MeterBuilderWithRepository extends MeterBuilder {

  /**
   * @param meterRepository the meter repository to use.
   * @return the corresponding {@link MeterBuilder}
   */
  MeterBuilder withMeterRepository(MeterRepository meterRepository);
}
