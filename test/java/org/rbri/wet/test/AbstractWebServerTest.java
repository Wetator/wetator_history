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

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.rbri.wet.core.WetEngine;
import org.rbri.wet.test.jetty.HttpHeaderHandler;
import org.rbri.wet.test.jetty.RedirectHandler;
import org.rbri.wet.test.jetty.SnoopyHandler;
import org.rbri.wet.util.StdOutProgressListener;

/**
 * @author frank.danek
 */
public abstract class AbstractWebServerTest {

  /** The listener port for the web server. */
  public static final int PORT = Integer.valueOf(System.getProperty("wetator.test.port", "12345"));

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

    tmpResourceHandler.setResourceBase("webpages");

    HandlerList tmpHandlers = new HandlerList();
    tmpHandlers.setHandlers(new Handler[] { new HttpHeaderHandler(), new RedirectHandler(), new SnoopyHandler(),
        tmpResourceHandler, new DefaultHandler() });
    server.setHandler(tmpHandlers);

    server.start();
  }

  @Before
  public void createWetEngine() {
    wetEngine = new WetEngine();

    // configuration is relative to the base dir of the project
    File tmpConfigFile = new File("test/java/org/rbri/wet/test/wetator.config");
    wetEngine.setConfigFileName(tmpConfigFile.getAbsolutePath());

    listener = new JUnitProgressListener();
    wetEngine.addProgressListener(listener);
    wetEngine.addProgressListener(new StdOutProgressListener());
    wetEngine.init();
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
