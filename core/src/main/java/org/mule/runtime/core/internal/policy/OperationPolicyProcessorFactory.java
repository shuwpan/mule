/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.core.internal.policy;

import org.mule.runtime.core.api.policy.Policy;
import org.mule.runtime.core.api.processor.Processor;
import org.mule.runtime.core.api.processor.ReactiveProcessor;

/**
 * Factory for {@link OperationPolicy} instances.
 *
 * @since 4.0
 */
public interface OperationPolicyProcessorFactory {

  /**
   * Creates a {@link Processor} to execute the {@code policy}.
   *
   * @param policy        the policy from which the {@link OperationPolicy} gets created.
   * @param nextProcessor the next-operation processor implementation
   *
   * @return an {@link OperationPolicy} that performs the common logic related to policies.
   */
  ReactiveProcessor createOperationPolicy(Policy policy, ReactiveProcessor nextProcessor);

}
