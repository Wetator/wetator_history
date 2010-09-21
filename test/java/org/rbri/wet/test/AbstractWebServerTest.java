/*
 * Copyright (c) 2008-2010 Ronald Brill
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.rbri.wet.test;

import java.io.File;
import java.util.Properties;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.rbri.wet.core.WetConfiguration;
import org.rbri.wet.core.WetEngine;
import org.rbri.wet.test.jetty.HttpHeaderServlet;
import org.rbri.wet.test.jetty.RedirectServlet;
import org.rbri.wet.test.jetty.SnoopyServlet;
import org.rbri.wet.util.StdOutProgressListener;

/**
 * @author frank.danek
 */
public abstract class AbstractWebServerTest {

  /** The listener port for the web server. */
  public static final int PORT = Integer.valueOf(System.getProperty("wetator.test.port", "4711"));
  private static final String BASE_DIRECTORY = "webpages";

  private static Server server;

  private WetEngine wetEngine;
  private JUnitProgressListener listener;

  /**
   * Starts the web server on the default {@link #PORT}.
   * The given resourceBase is used to be the ROOT directory that serves the default context.
   * <p>
   * <b>Don't forget to stop the returned HttpServer after the test</b>
   * 
   * @throws Exception if the test fails
   */
  @BeforeClass
  public static void startWebServer() throws Exception {
    if (server != null) {
      throw new IllegalStateException("startWebServer() can not be called twice");
    }
    server = new Server(PORT);

    ResourceHandler tmpResourceHandler = new ResourceHandler();
    tmpResourceHandler.setDirectoriesListed(true);
    tmpResourceHandler.setWelcomeFiles(new String[] { "index.html" });
    tmpResourceHandler.setResourceBase(BASE_DIRECTORY);

    ServletContextHandler tmpContextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
    tmpContextHandler.setContextPath("/testcases");
    tmpContextHandler.addServlet(new ServletHolder(new HttpHeaderServlet()), "/http_header.php");
    tmpContextHandler.addServlet(new ServletHolder(new RedirectServlet()), "/redirect_header.php");
    tmpContextHandler.addServlet(new ServletHolder(new RedirectServlet()), "/redirect_js.php");
    tmpContextHandler.addServlet(new ServletHolder(new RedirectServlet()), "/redirect_meta.php");
    tmpContextHandler.addServlet(new ServletHolder(new SnoopyServlet()), "/snoopy.php");

    HandlerList tmpHandlers = new HandlerList();
    tmpHandlers.setHandlers(new Handler[] { tmpContextHandler, tmpResourceHandler, new DefaultHandler() });
    server.setHandler(tmpHandlers);

    server.start();
  }

  @Before
  public void createWetEngine() {
    Properties tmpProperties = new Properties();
    tmpProperties.setProperty(WetConfiguration.PROPERTY_BASE_URL, "http://localhost:" + PORT + "/testcases");
    tmpProperties.setProperty(WetConfiguration.PROPERTY_XSL_TEMPLATES, "./xsl/SimpleHtml.xsl");
    tmpProperties.setProperty(WetConfiguration.PROPERTY_COMMAND_SETS,
        "org.rbri.wet.commandset.SqlCommandSet, org.rbri.wet.commandset.TestCommandSet");
    tmpProperties.setProperty("wetator.db.connections", "wetdb, secondDb");

    tmpProperties.setProperty("wetator.db.wetdb.driver", "org.hsqldb.jdbcDriver");
    tmpProperties.setProperty("wetator.db.wetdb.url", "jdbc:hsqldb:mem:wetdb");
    tmpProperties.setProperty("wetator.db.wetdb.user", "sa");
    tmpProperties.setProperty("wetator.db.wetdb.password", "");

    tmpProperties.setProperty("wetator.db.secondDb.driver", "org.hsqldb.jdbcDriver");
    tmpProperties.setProperty("wetator.db.secondDb.url", "jdbc:hsqldb:mem:second_db");
    tmpProperties.setProperty("wetator.db.secondDb.user", "sa");
    tmpProperties.setProperty("wetator.db.secondDb.password", "");

    tmpProperties.setProperty("$app_user", "dobby");
    tmpProperties.setProperty("$$app_password", "secret");

    tmpProperties.setProperty("$wet", "Wetator");
    tmpProperties.setProperty("$$wet-secret", "Wetator");

    WetConfiguration tmpWetConfiguration = new WetConfiguration(new File("."), tmpProperties, null);

    listener = new JUnitProgressListener();

    wetEngine = new WetEngine();
    wetEngine.addProgressListener(listener);
    wetEngine.addProgressListener(new StdOutProgressListener());
    wetEngine.init(tmpWetConfiguration);
  }

  /**
   * Performs post-test destruction.
   * 
   * @throws Exception if an error occurs
   */
  @AfterClass
  public static void tearDown() throws Exception {
    if (server != null) {
      server.stop();
    }
    server = null;
  }

  protected void executeTestFile(File aTestFile) {
    wetEngine.addTestFile(aTestFile);
    wetEngine.executeTests();
  }

  /**
   * @return the number of errors
   * @see org.rbri.wet.test.JUnitProgressListener#getErrors()
   */
  public int getErrors() {
    return listener.getErrors();
  }

  /**
   * @return the number of failures
   * @see org.rbri.wet.test.JUnitProgressListener#getFailures()
   */
  public int getFailures() {
    return listener.getFailures();
  }

  /**
   * @return the number of steps
   * @see org.rbri.wet.test.JUnitProgressListener#getSteps()
   */
  public int getSteps() {
    return listener.getSteps();
  }
}
