/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.runtime.tracing.level.impl.config;

import static org.mule.test.allure.AllureConstants.Profiling.PROFILING;
import static org.mule.test.allure.AllureConstants.Profiling.ProfilingServiceStory.TRACING_CONFIGURATION;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import org.mule.runtime.core.api.MuleContext;
import org.mule.runtime.tracing.level.api.config.TracingLevel;

import java.util.HashMap;

import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.Test;

@Feature(PROFILING)
@Story(TRACING_CONFIGURATION)
public class FileTracingLevelConfigurationTestCase {

  private static final String CONF_FOLDER = "conf";
  private static final String TRACING_LEVEL_CONF = "tracing-level.conf";
  private static final String TRACING_LEVEL_EMPTY_CONF = "tracing-level-empty.conf";
  private static final String TRACING_LEVEL_WITH_OVERRIDES_CONF = "tracing-level-with-overrides.conf";
  private static final String TRACING_LEVEL_WITH_WRONG_OVERRIDE_CONF = "tracing-level-with-wrong-override.conf";
  private static final String TRACING_LEVEL_WITH_DUPLICATE_OVERRIDE_CONF = "tracing-level-with-duplicate-override.conf";
  private static final String NON_EXISTENT_CONF = "non-existent.conf";
  private static final String WRONG_LEVEL_CONF = "wrong-level.conf";
  private static final String LOCATION_1 = "location1";
  private static final String LOCATION_2 = "location2";
  private static final TracingLevel DEFAULT_LEVEL = TracingLevel.MONITORING;

  @Test
  public void whenLevelIsSpecifiedInFileItIsReturned() {
    FileTracingLevelConfiguration fileTracingLevelConfiguration =
        new TestFileTracingLevelConfiguration(mock(MuleContext.class));
    assertThat(fileTracingLevelConfiguration.getTracingLevel(), equalTo(TracingLevel.OVERVIEW));
  }

  @Test
  public void whenNoPropertyIsInTheFileDefaultLevelIsReturned() {
    FileTracingLevelConfiguration fileTracingLevelConfiguration =
        new TestEmptyFileTracingLevelConfiguration(mock(MuleContext.class));
    assertThat(fileTracingLevelConfiguration.getTracingLevel(), is(DEFAULT_LEVEL));
  }

  @Test
  public void whenNoFileExistsDefaultLevelIsReturned() {
    FileTracingLevelConfiguration fileTracingLevelConfiguration =
        new TestNoFileTracingLevelConfiguration(mock(MuleContext.class));
    assertThat(fileTracingLevelConfiguration.getTracingLevel(), is(DEFAULT_LEVEL));
  }

  @Test
  public void whenLevelIsWrongInFileDefaultLevelIsReturned() {
    FileTracingLevelConfiguration fileTracingLevelConfiguration =
        new TestWrongLevelTracingLevelConfiguration(mock(MuleContext.class));
    assertThat(fileTracingLevelConfiguration.getTracingLevel(), is(DEFAULT_LEVEL));
  }

  @Test
  public void whenNoFileExistsTracingLevelOverridesIsEmpty() {
    FileTracingLevelConfiguration fileTracingLevelConfiguration =
        new TestNoFileTracingLevelConfiguration(mock(MuleContext.class));
    assertTrue(fileTracingLevelConfiguration.getTracingLevelOverrides().isEmpty());
  }

  @Test
  public void whenNoPropertyIsInTheFileTracingLevelOverridesIsEmpty() {
    FileTracingLevelConfiguration fileTracingLevelConfiguration =
        new TestEmptyFileTracingLevelConfiguration(mock(MuleContext.class));
    assertTrue(fileTracingLevelConfiguration.getTracingLevelOverrides().isEmpty());
  }

  @Test
  public void whenOnlyTheLevelIsSpecifiedInTheFileTracingLevelOverridesIsEmpty() {
    FileTracingLevelConfiguration fileTracingLevelConfiguration =
        new TestFileTracingLevelConfiguration(mock(MuleContext.class));
    assertTrue(fileTracingLevelConfiguration.getTracingLevelOverrides().isEmpty());
  }

  @Test
  public void whenALocationOverrideIsSpecifiedInTheFileTheOverrideIsReturned() {
    FileTracingLevelConfiguration fileTracingLevelConfiguration =
        new TestFileTracingLevelWithOverridesConfiguration(mock(MuleContext.class));
    HashMap<String, TracingLevel> tracingLevelOverrides = fileTracingLevelConfiguration.getTracingLevelOverrides();

    assertFalse(tracingLevelOverrides.isEmpty());
    assertTrue(tracingLevelOverrides.containsKey(LOCATION_1));
    assertEquals(TracingLevel.MONITORING, tracingLevelOverrides.get(LOCATION_1));
    assertTrue(tracingLevelOverrides.containsKey(LOCATION_2));
    assertEquals(TracingLevel.DEBUG, tracingLevelOverrides.get(LOCATION_2));
  }

  @Test
  public void whenAWrongLocationOverrideIsSpecifiedInTheFileTheOverrideIsNotReturned() {
    FileTracingLevelConfiguration fileTracingLevelConfiguration =
        new TestFileTracingLevelWithWrongOverrideConfiguration(mock(MuleContext.class));
    HashMap<String, TracingLevel> tracingLevelOverrides = fileTracingLevelConfiguration.getTracingLevelOverrides();

    assertFalse(tracingLevelOverrides.isEmpty());
    assertFalse(tracingLevelOverrides.containsKey(LOCATION_1));
    assertTrue(tracingLevelOverrides.containsKey(LOCATION_2));
    assertEquals(TracingLevel.DEBUG, tracingLevelOverrides.get(LOCATION_2));
  }

  @Test
  public void whenALocationOverrideIsSpecifiedAndDuplicatedInTheFileTheLastOverrideIsReturned() {
    FileTracingLevelConfiguration fileTracingLevelConfiguration =
        new TestFileTracingLevelWithDuplicateOverrideConfiguration(mock(MuleContext.class));
    HashMap<String, TracingLevel> tracingLevelOverrides = fileTracingLevelConfiguration.getTracingLevelOverrides();

    assertFalse(tracingLevelOverrides.isEmpty());
    assertTrue(tracingLevelOverrides.containsKey(LOCATION_1));
    assertEquals(TracingLevel.DEBUG, tracingLevelOverrides.get(LOCATION_1));
    assertTrue(tracingLevelOverrides.containsKey(LOCATION_2));
    assertEquals(TracingLevel.DEBUG, tracingLevelOverrides.get(LOCATION_2));
  }

  /**
   * {@link FileTracingLevelConfiguration} used for testing a file with a defined level
   */
  private static class TestFileTracingLevelConfiguration extends FileTracingLevelConfiguration {

    public static final String TEST_CONF_FILE_NAME = TRACING_LEVEL_CONF;

    public TestFileTracingLevelConfiguration(MuleContext muleContext) {
      super(muleContext);
    }

    @Override
    protected String getPropertiesFileName() {
      return TEST_CONF_FILE_NAME;
    }

    @Override
    protected ClassLoader getExecutionClassLoader(MuleContext muleContext) {
      return Thread.currentThread().getContextClassLoader();
    }

    @Override
    protected String getConfFolder() {
      return CONF_FOLDER;
    }
  }

  /**
   * {@link FileTracingLevelConfiguration} used for testing the case when the written level does not exist
   */
  private static class TestWrongLevelTracingLevelConfiguration extends FileTracingLevelConfiguration {

    public static final String TEST_CONF_FILE_NAME = WRONG_LEVEL_CONF;

    public TestWrongLevelTracingLevelConfiguration(MuleContext muleContext) {
      super(muleContext);
    }

    @Override
    protected String getPropertiesFileName() {
      return TEST_CONF_FILE_NAME;
    }

    @Override
    protected ClassLoader getExecutionClassLoader(MuleContext muleContext) {
      return Thread.currentThread().getContextClassLoader();
    }

    @Override
    protected String getConfFolder() {
      return CONF_FOLDER;
    }
  }

  /**
   * {@link FileTracingLevelConfiguration} used for testing an empty file.
   */
  private static class TestEmptyFileTracingLevelConfiguration extends FileTracingLevelConfiguration {

    public static final String TEST_CONF_FILE_NAME = TRACING_LEVEL_EMPTY_CONF;

    public TestEmptyFileTracingLevelConfiguration(MuleContext muleContext) {
      super(muleContext);
    }

    @Override
    protected String getPropertiesFileName() {
      return TEST_CONF_FILE_NAME;
    }

    @Override
    protected ClassLoader getExecutionClassLoader(MuleContext muleContext) {
      return Thread.currentThread().getContextClassLoader();
    }

    @Override
    protected String getConfFolder() {
      return CONF_FOLDER;
    }
  }

  /**
   * {@link FileTracingLevelConfiguration} used for testing the case where the file does not exist
   */
  private static class TestNoFileTracingLevelConfiguration extends FileTracingLevelConfiguration {

    public static final String TEST_CONF_FILE_NAME = NON_EXISTENT_CONF;

    public TestNoFileTracingLevelConfiguration(MuleContext muleContext) {
      super(muleContext);
    }

    @Override
    protected String getPropertiesFileName() {
      return TEST_CONF_FILE_NAME;
    }

    @Override
    protected ClassLoader getExecutionClassLoader(MuleContext muleContext) {
      return Thread.currentThread().getContextClassLoader();
    }

    @Override
    protected String getConfFolder() {
      return CONF_FOLDER;
    }
  }

  /**
   * {@link FileTracingLevelConfiguration} used for testing the case where the file does not exist
   */
  private static class TestFileTracingLevelWithOverridesConfiguration extends FileTracingLevelConfiguration {

    public static final String TEST_CONF_FILE_NAME = TRACING_LEVEL_WITH_OVERRIDES_CONF;

    public TestFileTracingLevelWithOverridesConfiguration(MuleContext muleContext) {
      super(muleContext);
    }

    @Override
    protected String getPropertiesFileName() {
      return TEST_CONF_FILE_NAME;
    }

    @Override
    protected ClassLoader getExecutionClassLoader(MuleContext muleContext) {
      return Thread.currentThread().getContextClassLoader();
    }

    @Override
    protected String getConfFolder() {
      return CONF_FOLDER;
    }
  }

  /**
   * {@link FileTracingLevelConfiguration} used for testing the case where the file does not exist
   */
  private static class TestFileTracingLevelWithWrongOverrideConfiguration extends FileTracingLevelConfiguration {

    public static final String TEST_CONF_FILE_NAME = TRACING_LEVEL_WITH_WRONG_OVERRIDE_CONF;

    public TestFileTracingLevelWithWrongOverrideConfiguration(MuleContext muleContext) {
      super(muleContext);
    }

    @Override
    protected String getPropertiesFileName() {
      return TEST_CONF_FILE_NAME;
    }

    @Override
    protected ClassLoader getExecutionClassLoader(MuleContext muleContext) {
      return Thread.currentThread().getContextClassLoader();
    }

    @Override
    protected String getConfFolder() {
      return CONF_FOLDER;
    }
  }

  /**
   * {@link FileTracingLevelConfiguration} used for testing the case where the file does not exist
   */
  private static class TestFileTracingLevelWithDuplicateOverrideConfiguration extends FileTracingLevelConfiguration {

    public static final String TEST_CONF_FILE_NAME = TRACING_LEVEL_WITH_DUPLICATE_OVERRIDE_CONF;

    public TestFileTracingLevelWithDuplicateOverrideConfiguration(MuleContext muleContext) {
      super(muleContext);
    }

    @Override
    protected String getPropertiesFileName() {
      return TEST_CONF_FILE_NAME;
    }

    @Override
    protected ClassLoader getExecutionClassLoader(MuleContext muleContext) {
      return Thread.currentThread().getContextClassLoader();
    }

    @Override
    protected String getConfFolder() {
      return CONF_FOLDER;
    }
  }
}
