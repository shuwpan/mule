/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.module.reboot;

import static org.mule.runtime.jpms.api.JpmsUtils.validateNoBootModuleLayerTweaking;

import org.mule.runtime.module.reboot.api.MuleContainerBootstrapUtils;
import org.mule.runtime.module.reboot.internal.MuleContainerWrapper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.Properties;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.tanukisoftware.wrapper.WrapperManager;

/**
 * Determine which is the main class to run and delegate control to the Java Service Wrapper. If OSGi is not being used to boot
 * with, configure the classpath based on the libraries in $MULE_HOME/lib/*
 * <p/>
 * Note: this class is intentionally kept free of any external library dependencies and therefore repeats a few utility methods.
 */
public class MuleContainerBootstrap {

  public static final String MULE_HOME_DIRECTORY_PROPERTY = "mule.home";
  public static final String MULE_BASE_DIRECTORY_PROPERTY = "mule.base";

  private static final String MULE_MODULE_REBOOT_POM_FILE_PATH =
      "META-INF/maven/org.mule.module/mule-module-reboot/pom.properties";

  public static final String CLI_OPTIONS[][] = {{"main", "true", "Main Class"},
      {"production", "false", "Modify the system class loader for production use (as in Mule 2.x)"},
      {"version", "false", "Show product and version information"}};

  public static void main(String[] args) throws Exception {
    // Optionally remove existing handlers attached to j.u.l root logger
    SLF4JBridgeHandler.removeHandlersForRootLogger(); // (since SLF4J 1.6.5)

    // add SLF4JBridgeHandler to j.u.l's root logger, should be done once during
    // the initialization phase of your application
    SLF4JBridgeHandler.install();

    validateNoBootModuleLayerTweaking();

    // Parse any command line options based on the list above.
    CommandLine commandLine = parseCommandLine(args);
    // Any unrecognized arguments get passed through to the next class (e.g., to the OSGi Framework).
    String[] remainingArgs = commandLine.getArgs();

    prepareBootstrapPhase(commandLine);

    System.out.println("Starting the Mule Container...");
    WrapperManager.start(new MuleContainerWrapper(), remainingArgs);
  }

  private static void prepareBootstrapPhase(CommandLine commandLine) throws Exception {
    prepareBootstrapPhase();
  }

  private static void prepareBootstrapPhase() throws Exception {
    setSystemMuleVersion();
  }

  public static File lookupMuleHome() throws Exception {
    File muleHome = null;
    String muleHomeVar = System.getProperty(MULE_HOME_DIRECTORY_PROPERTY);

    if (muleHomeVar != null && !muleHomeVar.trim().equals("") && !muleHomeVar.equals("%MULE_HOME%")) {
      muleHome = new File(muleHomeVar).getCanonicalFile();
    }

    if (muleHome == null) {
      throw new IllegalArgumentException("The system property " + MULE_HOME_DIRECTORY_PROPERTY
          + " is not set.");
    }
    if (!muleHome.exists()) {
      throw new IllegalArgumentException("The system property " + MULE_HOME_DIRECTORY_PROPERTY
          + " does not contain a valid directory (" + muleHome.getAbsolutePath() + ").");
    }
    if (!muleHome.isDirectory()) {
      throw new IllegalArgumentException("The system property " + MULE_HOME_DIRECTORY_PROPERTY
          + " does not contain a valid directory (" + muleHome.getAbsolutePath() + ").");
    }
    return muleHome;
  }

  public static File lookupMuleBase() throws Exception {
    File muleBase = null;
    String muleBaseVar = System.getProperty("mule.base");

    if (muleBaseVar != null && !muleBaseVar.trim().equals("") && !muleBaseVar.equals("%MULE_BASE%")) {
      muleBase = new File(muleBaseVar).getCanonicalFile();
    }
    return muleBase;
  }

  /**
   * @return The Mule Runtime wrapper pid
   *
   * @since 4.3.0
   */
  public static long getWrapperPID() {
    return WrapperManager.getWrapperPID();
  }

  /**
   * @return The Mule Runtime JVM PID
   *
   * @since 4.3.0
   */
  public static long getJavaPID() {
    return WrapperManager.getJavaPID();
  }

  private static void setSystemMuleVersion() {
    InputStream propertiesStream = null;
    try {
      URL mavenPropertiesUrl =
          MuleContainerBootstrapUtils.getResource(MULE_MODULE_REBOOT_POM_FILE_PATH, MuleContainerWrapper.class);
      propertiesStream = mavenPropertiesUrl.openStream();

      Properties mavenProperties = new Properties();
      mavenProperties.load(propertiesStream);

      System.setProperty("mule.version", mavenProperties.getProperty("version"));
      System.setProperty("mule.reference.version", mavenProperties.getProperty("version") + '-' + (new Date()).getTime());
    } catch (Exception ignore) {
      // ignore;
    } finally {
      if (propertiesStream != null) {
        try {
          propertiesStream.close();
        } catch (IOException iox) {
          // ignore
        }
      }
    }
  }

  /**
   * Parse any command line arguments using the Commons CLI library.
   */
  private static CommandLine parseCommandLine(String[] args) throws ParseException {
    Options options = new Options();
    for (String[] element : CLI_OPTIONS) {
      options.addOption(element[0], "true".equalsIgnoreCase(element[1]), element[2]);
    }
    return new BasicParser().parse(options, args, true);
  }

  public static void dispose() {
    // Do nothing
  }
}
