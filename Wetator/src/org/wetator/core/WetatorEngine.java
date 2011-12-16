/*
 * Copyright (c) 2008-2011 wetator.org
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


package org.wetator.core;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wetator.backend.IBrowser;
import org.wetator.backend.IBrowser.BrowserType;
import org.wetator.backend.htmlunit.HtmlUnitBrowser;
import org.wetator.core.IScripter.IsSupportedResult;
import org.wetator.exception.AssertionFailedException;
import org.wetator.exception.ResourceException;
import org.wetator.exception.WetatorException;
import org.wetator.progresslistener.XMLResultWriter;

/**
 * The engine that makes the monster running.<br/>
 * Everything that is in common use for the whole test process is stored here.
 * 
 * @author rbri
 * @author frank.danek
 */
public class WetatorEngine {

  private static final Log LOG = LogFactory.getLog(WetatorEngine.class);

  private static final String PROPERTY_TEST_CONFIG = "wetator.config";
  private static final String CONFIG_FILE_DEFAULT_NAME = "wetator.config";

  private String configFileName;
  private Map<String, String> externalProperties;
  private List<TestCase> testCases;

  private WetatorConfiguration configuration;
  private IBrowser browser;
  private List<ICommandSet> commandSets;
  private List<IScripter> scripter;
  private List<IProgressListener> progressListener;

  /**
   * The constructor.
   */
  public WetatorEngine() {
    testCases = new LinkedList<TestCase>();
    progressListener = new LinkedList<IProgressListener>();
  }

  /**
   * Initializes the wetator engine. The configuration is read from the configuration file got by
   * {@link #getConfigFile()}.
   * 
   * @throws org.wetator.exception.ConfigurationException in case of problems with the configuration
   */
  public void init() {
    init(readConfiguration());
  }

  /**
   * Initializes the wetator engine using the given configuration.
   * 
   * @param aConfiguration the configuration to use
   */
  public void init(final WetatorConfiguration aConfiguration) {
    informListenersInit();

    configuration = aConfiguration;
    if (configFileName == null) {
      configFileName = "";
    }

    // setup the scripter
    scripter = getConfiguration().getScripters();

    // setup the command sets
    commandSets = getConfiguration().getCommandSets();

    // setup the browser
    final IBrowser tmpBrowser = createBrowser();
    setBrowser(tmpBrowser);
  }

  private WetatorConfiguration readConfiguration() {
    final File tmpConfigFile = getConfigFile();
    return new WetatorConfiguration(tmpConfigFile, getExternalProperties());
  }

  /**
   * @return the {@link IBrowser} to use for executing the tests
   */
  protected IBrowser createBrowser() {
    return new HtmlUnitBrowser(this);
  }

  /**
   * @return the list of all test cases
   */
  public List<TestCase> getTestCases() {
    return testCases;
  }

  /**
   * Adds a test file to be executed.
   * 
   * @param aName the name of the test file to be added
   * @param aFile the test file to be added
   * @throws ResourceException if the test file does not exist or is not readable
   */
  public void addTestCase(final String aName, final File aFile) {
    if (!aFile.exists()) {
      throw new ResourceException("The test file '" + aFile.getAbsolutePath() + "' does not exist.");
    }
    if (!aFile.isFile() || !aFile.canRead()) {
      throw new ResourceException("The test file '" + aFile.getAbsolutePath() + "' is not readable.");
    }
    testCases.add(new TestCase(aName, aFile));
  }

  /**
   * Executes the tests.
   */
  public void executeTests() {
    addDefaultProgressListeners();

    informListenersStart();
    try {
      for (TestCase tmpTestCase : getTestCases()) {
        final File tmpFile = tmpTestCase.getFile();
        LOG.info("Executing tests from file '" + tmpFile.getAbsolutePath() + "'");
        informListenersTestCaseStart(tmpTestCase.getName());
        try {
          for (BrowserType tmpBrowserType : getConfiguration().getBrowserTypes()) {
            informListenersTestRunStart(tmpBrowserType.getLabel());
            try {
              // new session for every (root) file and browser
              getBrowser().startNewSession(tmpBrowserType);

              // setup the context
              final WetatorContext tmpWetatorContext = createWetatorContext(tmpFile, tmpBrowserType);
              tmpWetatorContext.execute();
            } catch (final RuntimeException e) {
              // TODO this way we continue with the next browser. is this correct?
              informListenersError(e);
            } finally {
              informListenersTestRunEnd();
            }
          }
        } catch (final Throwable e) {
          // this is the last place to handle exceptions for a test case
          informListenersError(e);
        } finally {
          informListenersTestCaseEnd();
        }
      }
    } finally {
      informListenersEnd();
    }
  }

  /**
   * Adds the default {@link IProgressListener}.
   * <ul>
   * <li>{@link XMLResultWriter}</li>
   * </ul>
   */
  protected void addDefaultProgressListeners() {
    // create new result writer and call the init() method.
    final XMLResultWriter tmpResultWriter = new XMLResultWriter();
    tmpResultWriter.init(this);
    addProgressListener(tmpResultWriter);
  }

  /**
   * @param aFile the file to execute
   * @param aBrowserType the browser type to use
   * @return the {@link WetatorContext} to use for executing the given file
   */
  protected WetatorContext createWetatorContext(final File aFile, final BrowserType aBrowserType) {
    return new WetatorContext(this, aFile, aBrowserType);
  }

  /**
   * Reads all commands of the given file and returns them in the same order they occur in the file.
   * 
   * @param aFile the file to read the commands from.
   * @return a list of {@link Command}s.
   * @throws org.wetator.exception.ResourceException in case of problems reading the file
   * @throws WetatorException if no {@link IScripter} can be found for the given file or an error occurs parsing the
   *         given file
   */
  protected List<Command> readCommandsFromFile(final File aFile) {
    final IScripter tmpScripter = createScripter(aFile);

    tmpScripter.script(aFile);
    final List<Command> tmpResult = tmpScripter.getCommands();

    return tmpResult;
  }

  private IScripter createScripter(final File aFile) {
    final List<IScripter.IsSupportedResult> tmpResults = new LinkedList<IScripter.IsSupportedResult>();
    for (IScripter tmpScripter : scripter) {
      final IScripter.IsSupportedResult tmpResult = tmpScripter.isSupported(aFile);
      if (IScripter.IS_SUPPORTED == tmpResult) {
        return tmpScripter;
      }
      tmpResults.add(tmpResult);
    }

    // construct a detailed error message
    final StringBuilder tmpMessage = new StringBuilder("No scripter found for file '");
    tmpMessage.append(aFile.getAbsolutePath()).append("' (");

    boolean tmpIsFirst = true;
    for (IsSupportedResult tmpIsSupportedResult : tmpResults) {
      if (!tmpIsFirst) {
        tmpMessage.append("; ");
      }
      tmpMessage.append(tmpIsSupportedResult.getMessage());
      tmpIsFirst = false;
    }

    tmpMessage.append(").");

    throw new WetatorException(tmpMessage.toString());
  }

  /**
   * @param aCommandName the name of the {@link ICommandImplementation}
   * @return the {@link ICommandImplementation} for the given name or null if none was found
   */
  protected ICommandImplementation getCommandImplementationFor(final String aCommandName) {
    for (ICommandSet tmpCommandSet : commandSets) {
      final ICommandImplementation tmpCommandImplementation = tmpCommandSet.getCommandImplementationFor(aCommandName);
      if (null != tmpCommandImplementation) {
        return tmpCommandImplementation;
      }
    }
    return null;
  }

  /**
   * Returns the configuration file. The configuration file name is searched in:
   * <ol>
   * <li>{@link #getConfigFileName()}</li>
   * <li>the system property <code>wetator.config</code></li>
   * <li>the default configuration file name 'wetator.config'</li>
   * </ol>
   * 
   * @return the configuration file
   */
  public File getConfigFile() {
    String tmpConfigName = getConfigFileName();

    // config was initialized directly
    if ("".equals(tmpConfigName)) {
      return null;
    }

    // ok try harder
    if (null == tmpConfigName) {
      tmpConfigName = System.getProperty(PROPERTY_TEST_CONFIG, CONFIG_FILE_DEFAULT_NAME);
    }

    File tmpConfigFile;
    tmpConfigFile = new File(tmpConfigName);
    return tmpConfigFile;
  }

  /**
   * @return the {@link IBrowser}
   */
  public IBrowser getBrowser() {
    return browser;
  }

  /**
   * @param aBrowser the browser to set
   */
  public void setBrowser(final IBrowser aBrowser) {
    browser = aBrowser;
  }

  /**
   * @return the configuration
   */
  public WetatorConfiguration getConfiguration() {
    return configuration;
  }

  /**
   * @return the configFileName
   */
  public String getConfigFileName() {
    return configFileName;
  }

  /**
   * @param aConfigFileName the configFileName to set
   */
  public void setConfigFileName(final String aConfigFileName) {
    configFileName = aConfigFileName;
  }

  /**
   * @return the externalProperties
   */
  public Map<String, String> getExternalProperties() {
    return externalProperties;
  }

  /**
   * @param aExternalProperties the externalProperties to set
   */
  public void setExternalProperties(final Map<String, String> aExternalProperties) {
    externalProperties = aExternalProperties;
  }

  /**
   * Adds the given {@link IProgressListener} as listener. If this listener is already added it will not be
   * added again but the listener added first will be taken.
   * 
   * @param aProgressListener the listener to add
   */
  public void addProgressListener(final IProgressListener aProgressListener) {
    if (progressListener.contains(aProgressListener)) {
      return;
    }
    progressListener.add(aProgressListener);
  }

  /**
   * Informs all listeners about 'init'.
   */
  protected void informListenersInit() {
    for (IProgressListener tmpListener : progressListener) {
      tmpListener.init(this);
    }
  }

  /**
   * Informs all listeners about 'start'.
   */
  protected void informListenersStart() {
    for (IProgressListener tmpListener : progressListener) {
      tmpListener.start(this);
    }
  }

  /**
   * Informs all listeners about 'testStart'.
   * 
   * @param aTestName the file name of the test started.
   */
  protected void informListenersTestCaseStart(final String aTestName) {
    for (IProgressListener tmpListener : progressListener) {
      tmpListener.testCaseStart(aTestName);
    }
  }

  /**
   * Informs all listeners about 'testRunStart'.
   * 
   * @param aBrowserName the browser name of the test started.
   */
  protected void informListenersTestRunStart(final String aBrowserName) {
    for (IProgressListener tmpListener : progressListener) {
      tmpListener.testRunStart(aBrowserName);
    }
  }

  /**
   * Informs all listeners about 'testFileStart'.
   * 
   * @param aFileName the file name of the test started.
   */
  protected void informListenersTestFileStart(final String aFileName) {
    for (IProgressListener tmpListener : progressListener) {
      tmpListener.testFileStart(aFileName);
    }
  }

  /**
   * Informs all listeners about 'executeCommandStart'.
   * 
   * @param aContext the {@link WetatorContext} used to execute the command.
   * @param aCommand the {@link Command} to be executed.
   */
  protected void informListenersExecuteCommandStart(final WetatorContext aContext, final Command aCommand) {
    for (IProgressListener tmpListener : progressListener) {
      tmpListener.executeCommandStart(aContext, aCommand);
    }
  }

  /**
   * Informs all listeners about 'executeCommandEnd'.
   */
  protected void informListenersExecuteCommandEnd() {
    for (IProgressListener tmpListener : progressListener) {
      tmpListener.executeCommandEnd();
    }
  }

  /**
   * Informs all listeners about 'executeCommandSuccess'.
   */
  protected void informListenersExecuteCommandSuccess() {
    for (IProgressListener tmpListener : progressListener) {
      tmpListener.executeCommandSuccess();
    }
  }

  /**
   * Informs all listeners about 'executeCommandIgnored'.
   */
  protected void informListenersExecuteCommandIgnored() {
    for (IProgressListener tmpListener : progressListener) {
      tmpListener.executeCommandIgnored();
    }
  }

  /**
   * Informs all listeners about 'executeCommandFailure'.
   * 
   * @param anAssertionFailedException The exception thrown by the failed command.
   */
  protected void informListenersExecuteCommandFailure(final AssertionFailedException anAssertionFailedException) {
    for (IProgressListener tmpListener : progressListener) {
      tmpListener.executeCommandFailure(anAssertionFailedException);
    }
  }

  /**
   * Informs all listeners about 'executeCommandError'.
   * 
   * @param aThrowable The exception thrown by the command.
   */
  protected void informListenersExecuteCommandError(final Throwable aThrowable) {
    for (IProgressListener tmpListener : progressListener) {
      tmpListener.executeCommandError(aThrowable);
    }
  }

  /**
   * Informs all listeners about 'testFileEnd'.
   */
  protected void informListenersTestFileEnd() {
    for (IProgressListener tmpListener : progressListener) {
      tmpListener.testFileEnd();
    }
  }

  /**
   * Informs all listeners about 'testRunEnd'.
   */
  protected void informListenersTestRunEnd() {
    for (IProgressListener tmpListener : progressListener) {
      tmpListener.testRunEnd();
    }
  }

  /**
   * Informs all listeners about 'testEnd'.
   */
  protected void informListenersTestCaseEnd() {
    for (IProgressListener tmpListener : progressListener) {
      tmpListener.testCaseEnd();
    }
  }

  /**
   * Informs all listeners about 'end'.
   */
  protected void informListenersEnd() {
    for (IProgressListener tmpListener : progressListener) {
      tmpListener.end(this);
    }
  }

  /**
   * Informs all listeners about 'error'.
   * 
   * @param aThrowable the exception thrown
   */
  public void informListenersError(final Throwable aThrowable) {
    for (IProgressListener tmpListener : progressListener) {
      tmpListener.error(aThrowable);
    }
  }

  /**
   * Informs all listeners about 'warn'.
   * 
   * @param aMessageKey the message key of the warning.
   * @param aParameterArray the message parameters.
   */
  public void informListenersWarn(final String aMessageKey, final String[] aParameterArray) {
    for (IProgressListener tmpListener : progressListener) {
      tmpListener.warn(aMessageKey, aParameterArray);
    }
  }

  /**
   * Informs all listeners about 'info'.
   * 
   * @param aMessageKey the message key of the warning.
   * @param aParameterArray the message parameters.
   */
  public void informListenersInfo(final String aMessageKey, final String[] aParameterArray) {
    for (IProgressListener tmpListener : progressListener) {
      tmpListener.info(aMessageKey, aParameterArray);
    }
  }

  /**
   * Informs all listeners about 'engineResponseStored'.
   * 
   * @param aResponseFileName the file name of the stored response.
   */
  public void informListenersResponseStored(final String aResponseFileName) {
    for (IProgressListener tmpListener : progressListener) {
      tmpListener.responseStored(aResponseFileName);
    }
  }
}