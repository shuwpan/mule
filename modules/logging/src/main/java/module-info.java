/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

/**
 * Mule Logging Module.
 * 
 * @moduleGraph
 * @since 4.6
 */
module org.mule.runtime.logging {

  // Allows usage of Unsafe for caffeine and disruptor libraries, used by the logging framework
  requires jdk.unsupported;

  // Logging framework
  requires transitive org.slf4j;
  requires transitive org.apache.logging.log4j;

  // Log bridges
  requires transitive jul.to.slf4j;
  requires transitive java.logging;
  requires transitive org.apache.commons.logging;

}
