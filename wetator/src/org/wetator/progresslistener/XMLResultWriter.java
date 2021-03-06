/*
 * Copyright (c) 2008-2021 wetator.org
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


package org.wetator.progresslistener;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Deque;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.output.FileWriterWithEncoding;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.wetator.Version;
import org.wetator.backend.IBrowser.BrowserType;
import org.wetator.backend.control.IControl;
import org.wetator.core.Command;
import org.wetator.core.ICommandSet;
import org.wetator.core.IProgressListener;
import org.wetator.core.IScripter;
import org.wetator.core.Parameter;
import org.wetator.core.TestCase;
import org.wetator.core.Variable;
import org.wetator.core.WetatorConfiguration;
import org.wetator.core.WetatorContext;
import org.wetator.core.WetatorEngine;
import org.wetator.core.searchpattern.SearchPattern;
import org.wetator.exception.AssertionException;
import org.wetator.i18n.Messages;
import org.wetator.util.Output;
import org.wetator.util.SecretString;
import org.wetator.util.StringUtil;
import org.wetator.util.VersionUtil;
import org.wetator.util.XMLUtil;

import dk.brics.automaton.Automaton;

/**
 * The class that generates the XML output.
 *
 * @author rbri
 * @author frank.danek
 */
public class XMLResultWriter implements IProgressListener {

  private static final Logger LOG = LogManager.getLogger(XMLResultWriter.class);

  private static final String TAG_WET = "wet";
  private static final String TAG_ABOUT = "about";
  private static final String TAG_LIBS = "libraries";
  private static final String TAG_LIB = "library";
  private static final String TAG_JAVA = "java";
  private static final String TAG_PRODUCT = "product";
  private static final String TAG_VERSION = "version";
  private static final String TAG_BUILD = "build";
  private static final String TAG_START_TIME = "startTime";
  private static final String TAG_TEST_FILE = "testFile";
  private static final String TAG_EXECUTION_TIME = "executionTime";
  private static final String TAG_TESTCASE = "testcase";
  private static final String TAG_TESTRUN = "testrun";
  private static final String TAG_TESTFILE = "testfile";
  private static final String TAG_COMMAND = "command";
  private static final String TAG_FIRST_PARAM = "param0";
  private static final String TAG_SECOND_PARAM = "param1";
  private static final String TAG_THIRD_PARAM = "param2";
  private static final String TAG_DESCRIBE = "describe";
  private static final String TAG_RESPONSE = "response";
  private static final String TAG_HIGHLIGHT = "highlight";
  private static final String TAG_LOG = "log";
  private static final String TAG_LEVEL = "level";
  private static final String TAG_MESSAGE = "message";
  private static final String TAG_FAILURE = "failure";

  private static final String TAG_ERROR = "error";
  private static final String TAG_ERROR_DETAILS = "details";
  private static final String TAG_CONFIGURATION = "configuration";
  private static final String TAG_VARIABLES = "variables";
  private static final String TAG_VARIABLE = "variable";
  private static final String TAG_PROPERTY = "property";
  private static final String TAG_COMMAND_SET = "commandSet";
  private static final String TAG_CONTROL = "control";
  private static final String TAG_MIME_TYPE = "mimetype";
  private static final String TAG_IGNORED = "ignored";

  private Output output;
  private XMLUtil xmlUtil;
  private File resultFile;
  private File outputDir;
  private List<String> xslTemplates;

  private long tagId;
  private long executionStartTime;
  private Deque<Long> commandExecutionStartTimes;

  /**
   * The constructor.
   */
  public XMLResultWriter() {
    tagId = 0;
    commandExecutionStartTimes = new ArrayDeque<>();
  }

  @Override
  public void init(final WetatorEngine aWetatorEngine) {
    try {
      final WetatorConfiguration tmpConfiguration = aWetatorEngine.getConfiguration();

      outputDir = tmpConfiguration.getOutputDir();
      xslTemplates = tmpConfiguration.getXslTemplates();
      resultFile = new File(outputDir, "wetresult.xml");

      final Writer tmpWriter = new FileWriterWithEncoding(resultFile, "UTF-8"); // NOPMD
      output = new Output(tmpWriter, "  ");
      xmlUtil = new XMLUtil();

      // start writing
      output.println("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
      output.println();

      printlnStartTag(TAG_WET);

      // about Wetator
      printlnStartTag(TAG_ABOUT);

      printlnNode(TAG_PRODUCT, Version.getProductName());
      printlnNode(TAG_VERSION, Version.getVersion());
      printlnNode(TAG_BUILD, Version.getBuild());

      // Wetator libs
      printlnStartTag(TAG_LIBS);

      StringBuilder tmpInfo = new StringBuilder();
      final String[] tmpClassNames = { "com.gargoylesoftware.htmlunit.WebClient",
          "net.sourceforge.htmlunit.corejs.javascript.Function", "net.sourceforge.htmlunit.cyberneko.HTMLElements",
          "com.gargoylesoftware.css.parser.CSSOMParser" };
      for (final String tmpClassName : tmpClassNames) {
        tmpInfo.setLength(0);
        try {
          final Class<?> tmpClass = Class.forName(tmpClassName);
          // @formatter:off
          tmpInfo.append(VersionUtil.determineVersionFromJarFileName(tmpClass))
              .append(" (")
              .append(VersionUtil.determineCreationDateFromJarFileName(tmpClass))
              .append(')'); // NOPMD
          // @formatter:on
        } catch (final ClassNotFoundException e) {
          tmpInfo.append("Class '").append(tmpClassName).append("' not found in classpath.");
        }
        printlnNode(TAG_LIB, tmpInfo.toString());
      }

      final String[] tmpJars = { "commons-lang3-\\S+jar", "commons-text-\\S+jar", "commons-codec-\\S+jar",
          "commons-io-\\S+jar", "httpcore-\\S+jar", "httpclient-\\S+jar", "httpmime-\\S+jar", "log4j-api-\\S+jar",
          "log4j-core-\\S+jar" };
      for (final String tmpJar : tmpJars) {
        tmpInfo.setLength(0);
        // @formatter:off
        tmpInfo.append(VersionUtil.determineTitleFromJarManifest(tmpJar, null))
            .append(' ')
            .append(VersionUtil.determineVersionFromJarManifest(tmpJar, null));
        // @formatter:on
        printlnNode(TAG_LIB, tmpInfo.toString());
      }

      printlnNode(TAG_LIB, VersionUtil.determineVersionFromJarFileName(Automaton.class));

      // @formatter:off
      tmpInfo = new StringBuilder(org.apache.poi.Version.getProduct())
          .append(' ')
          .append(org.apache.poi.Version.getVersion());
      // @formatter:on
      printlnNode(TAG_LIB, tmpInfo.toString());

      // @formatter:off
      tmpInfo = new StringBuilder("Apache PDFBox ")
          .append(org.apache.pdfbox.util.Version.getVersion());
      // @formatter:on
      printlnNode(TAG_LIB, tmpInfo.toString());

      // @formatter:off
      tmpInfo = new StringBuilder(VersionUtil.determineBundleNameFromJarManifest("fontbox\\S+jar", null))
          .append(' ')
          .append(VersionUtil.determineBundleVersionFromJarManifest("fontbox\\S+jar", null));
      // @formatter:on
      printlnNode(TAG_LIB, tmpInfo.toString());

      printlnNode(TAG_LIB, org.apache.xmlcommons.Version.getVersion());
      printlnNode(TAG_LIB, org.apache.xerces.impl.Version.getVersion());
      printlnNode(TAG_LIB, org.apache.xalan.Version.getVersion());

      printlnEndTag(TAG_LIBS);

      // java info
      printlnStartTag(TAG_JAVA);
      final Set<Object> tmpKeys = System.getProperties().keySet();
      final List<String> tmpProperties = new ArrayList<>(tmpKeys.size());
      for (final Object tmpObject : tmpKeys) {
        tmpProperties.add(tmpObject.toString());
      }
      Collections.sort(tmpProperties);
      for (final String tmpProperty : tmpProperties) {
        String tmpValue = System.getProperty(tmpProperty);
        tmpValue = tmpValue.replace("\n", "\\n");
        tmpValue = tmpValue.replace("\r", "\\r");
        tmpValue = tmpValue.replace("\t", "\\t");
        printConfigurationProperty(tmpProperty, tmpValue);
      }
      printlnEndTag(TAG_JAVA);

      printlnEndTag(TAG_ABOUT);
    } catch (final IOException e) {
      LOG.error(e.getMessage(), e);
    }
  }

  @Override
  public void start(final WetatorEngine aWetatorEngine) {
    try {
      final WetatorConfiguration tmpConfiguration = aWetatorEngine.getConfiguration();

      // print the configuration
      printlnStartTag(TAG_CONFIGURATION);

      final File tmpSrc = tmpConfiguration.getSourceFile();
      if (null != tmpSrc) {
        printConfigurationProperty("configuration file", FilenameUtils.normalize(tmpSrc.getAbsolutePath()));
      }

      printConfigurationProperty(WetatorConfiguration.PROPERTY_BASE_URL, tmpConfiguration.getBaseUrl());
      for (final BrowserType tmpBrowserType : tmpConfiguration.getBrowserTypes()) {
        printConfigurationProperty(WetatorConfiguration.PROPERTY_BROWSER_TYPE, tmpBrowserType.getLabel());
      }
      printConfigurationProperty(WetatorConfiguration.PROPERTY_ACCEPT_LANGUAGE, tmpConfiguration.getAcceptLanaguage());
      printConfigurationProperty(WetatorConfiguration.PROPERTY_OUTPUT_DIR,
          FilenameUtils.normalize(tmpConfiguration.getOutputDir().getAbsolutePath()));
      printConfigurationProperty(WetatorConfiguration.PROPERTY_JAVASCRIPT_TIMEOUT,
          tmpConfiguration.getJsTimeoutInSeconds() + "s");
      printConfigurationProperty(WetatorConfiguration.PROPERTY_HTTP_TIMEOUT,
          tmpConfiguration.getHttpTimeoutInSeconds() + "s");
      // TODO jsJob filter patterns

      printConfigurationProperty(WetatorConfiguration.PROPERTY_WPATH_SEPARATOR, tmpConfiguration.getWPathSeparator());

      if (tmpConfiguration.getXslTemplates().isEmpty()) {
        printConfigurationProperty(WetatorConfiguration.PROPERTY_XSL_TEMPLATES, "");
      } else {
        for (final String tmpTemplate : tmpConfiguration.getXslTemplates()) {
          printConfigurationProperty(WetatorConfiguration.PROPERTY_XSL_TEMPLATES, tmpTemplate);
        }
      }

      if (tmpConfiguration.getCommandSets().isEmpty()) {
        printConfigurationProperty(WetatorConfiguration.PROPERTY_COMMAND_SETS, "");
      } else {
        for (final ICommandSet tmpCommandSet : tmpConfiguration.getCommandSets()) {
          printConfigurationProperty(WetatorConfiguration.PROPERTY_COMMAND_SETS, tmpCommandSet.getClass().getName());
        }
      }

      if (tmpConfiguration.getControls().isEmpty()) {
        printConfigurationProperty(WetatorConfiguration.PROPERTY_CONTROLS, "");
      } else {
        for (final Class<? extends IControl> tmpControl : tmpConfiguration.getControls()) {
          printConfigurationProperty(WetatorConfiguration.PROPERTY_CONTROLS, tmpControl.getName());
        }
      }

      if (tmpConfiguration.getScripters().isEmpty()) {
        printConfigurationProperty(WetatorConfiguration.PROPERTY_SCRIPTERS, "");
      } else {
        for (final IScripter tmpScripter : tmpConfiguration.getScripters()) {
          printConfigurationProperty(WetatorConfiguration.PROPERTY_SCRIPTERS, tmpScripter.getClass().getName());
        }
      }

      printConfigurationProperty(WetatorConfiguration.PROPERTY_PROXY_HOST, tmpConfiguration.getProxyHost());
      printConfigurationProperty(WetatorConfiguration.PROPERTY_PROXY_PORT,
          Integer.toString(tmpConfiguration.getProxyPort()));
      if (tmpConfiguration.getProxyHostsToBypass() == null || tmpConfiguration.getProxyHostsToBypass().isEmpty()) {
        printConfigurationProperty(WetatorConfiguration.PROPERTY_PROXY_HOSTS_TO_BYPASS, "");
      } else {
        for (final String tmpHost : tmpConfiguration.getProxyHostsToBypass()) {
          printConfigurationProperty(WetatorConfiguration.PROPERTY_PROXY_HOSTS_TO_BYPASS, tmpHost);
        }
      }

      printConfigurationProperty(WetatorConfiguration.PROPERTY_PROXY_USER, tmpConfiguration.getProxyUser());
      printConfigurationProperty(WetatorConfiguration.PROPERTY_BASIC_AUTH_USER, tmpConfiguration.getBasicAuthUser());
      printConfigurationProperty(WetatorConfiguration.PROPERTY_NTLM_USER, tmpConfiguration.getNtlmUser());
      printConfigurationProperty(WetatorConfiguration.PROPERTY_NTLM_WORKSTATION, tmpConfiguration.getNtlmWorkstation());
      printConfigurationProperty(WetatorConfiguration.PROPERTY_NTLM_DOMAIN, tmpConfiguration.getNtlmDomain());

      printConfigurationProperty(WetatorConfiguration.PROPERTY_RETROSPECT,
          Integer.toString(tmpConfiguration.getRetrospect()));

      printConfigurationProperty(WetatorConfiguration.PROPERTY_JS_DEBUGGER,
          Boolean.toString(tmpConfiguration.startJsDebugger()));

      for (final java.util.Map.Entry<String, String> tmpEntry : tmpConfiguration.getMimeTypes().entrySet()) {
        printStartTagOpener(TAG_MIME_TYPE);
        output.print(" extension=\"");
        output.print(xmlUtil.normalizeAttributeValue(tmpEntry.getKey()));
        output.print("\" type=\"");
        output.print(xmlUtil.normalizeAttributeValue(tmpEntry.getValue()));
        output.println("\"/>");
      }

      printlnStartTag(TAG_VARIABLES);

      final List<Variable> tmpVariables = tmpConfiguration.getVariables();
      for (final Variable tmpVariable : tmpVariables) {
        printStartTagOpener(TAG_VARIABLE);
        output.print(" name=\"");
        output.print(xmlUtil.normalizeAttributeValue(tmpVariable.getName()));
        output.print("\" value=\"");

        String tmpValue = tmpVariable.getValue().toString();
        tmpValue = tmpValue.replace("\n", "\\n");
        tmpValue = tmpValue.replace("\r", "\\r");
        tmpValue = tmpValue.replace("\t", "\\t");

        output.print(xmlUtil.normalizeAttributeValue(tmpValue));
        output.println("\"/>");
      }

      printlnEndTag(TAG_VARIABLES);

      final List<ICommandSet> tmpCommandSets = tmpConfiguration.getCommandSets();
      for (final ICommandSet tmpCommandSet : tmpCommandSets) {
        printStartTagOpener(TAG_COMMAND_SET);
        output.print(" class=\"");
        output.print(xmlUtil.normalizeAttributeValue(tmpCommandSet.getClass().toString()));
        output.println("\">");

        output.indent();
        for (final String tmpMessage : tmpCommandSet.getInitializationMessages()) {
          printLogMessage("INFO", tmpMessage);
        }

        printlnEndTag(TAG_COMMAND_SET);
      }

      final List<Class<? extends IControl>> tmpControls = tmpConfiguration.getControls();
      for (final Class<? extends IControl> tmpControl : tmpControls) {
        printStartTagOpener(TAG_CONTROL);
        output.print(" class=\"");
        output.print(xmlUtil.normalizeAttributeValue(tmpControl.getClass().toString()));
        output.println("\"/>");
      }

      printlnEndTag(TAG_CONFIGURATION);

      printlnNode(TAG_START_TIME, StringUtil.formatDate(new Date()));
      for (final TestCase tmpTestCase : aWetatorEngine.getTestCases()) {
        printlnNode(TAG_TEST_FILE, FilenameUtils.normalize(tmpTestCase.getFile().getAbsolutePath()));
      }

      executionStartTime = System.currentTimeMillis();
    } catch (final IOException e) {
      LOG.error(e.getMessage(), e);
    }
  }

  @Override
  public void testCaseStart(final TestCase aTestCase) {
    try {
      printStartTagOpener(TAG_TESTCASE);
      output.print(" name=\"");
      output.print(xmlUtil.normalizeAttributeValue(aTestCase.getName()));
      output.print("\" file=\"");
      output.print(xmlUtil.normalizeAttributeValue(FilenameUtils.normalize(aTestCase.getFile().getAbsolutePath())));
      output.println("\">");
      output.indent();
    } catch (final IOException e) {
      LOG.error(e.getMessage(), e);
    }
  }

  @Override
  public void testRunStart(final String aBrowserName) {
    try {
      printStartTagOpener(TAG_TESTRUN);
      output.print(" browser=\"");
      output.print(xmlUtil.normalizeAttributeValue(aBrowserName));
      output.println("\">");
      output.indent();
    } catch (final IOException e) {
      LOG.error(e.getMessage(), e);
    }
  }

  @Override
  public void testFileStart(final String aFileName) {
    try {
      printStartTagOpener(TAG_TESTFILE);
      output.print(" file=\"");
      output.print(xmlUtil.normalizeAttributeValue(aFileName));
      output.println("\">");
      output.indent();
    } catch (final IOException e) {
      LOG.error(e.getMessage(), e);
    }
  }

  @Override
  public void executeCommandStart(final WetatorContext aContext, final Command aCommand) {
    try {
      printStartTagOpener(TAG_COMMAND);
      output.print(" name=\"");
      output.print(xmlUtil.normalizeAttributeValue(aCommand.getName()));
      output.print("\" line=\"" + aCommand.getLineNo());
      if (aCommand.isComment()) {
        output.print("\" isComment=\"true");
      }
      output.println("\">");
      output.indent();

      Parameter tmpParameter = aCommand.getFirstParameter();
      printStartTag(TAG_FIRST_PARAM);
      if (null != tmpParameter) {
        output.print(xmlUtil.normalizeBodyValue(tmpParameter.getValue(aContext).toString()));
      }
      printEndTag(TAG_FIRST_PARAM);
      output.println();

      tmpParameter = aCommand.getSecondParameter();
      printStartTag(TAG_SECOND_PARAM);
      if (null != tmpParameter) {
        output.print(xmlUtil.normalizeBodyValue(tmpParameter.getValue(aContext).toString()));
      }
      printEndTag(TAG_SECOND_PARAM);
      output.println();

      tmpParameter = aCommand.getThirdParameter();
      printStartTag(TAG_THIRD_PARAM);
      if (null != tmpParameter) {
        output.print(xmlUtil.normalizeBodyValue(tmpParameter.getValue(aContext).toString()));
      }
      printEndTag(TAG_THIRD_PARAM);
      output.println();

      commandExecutionStartTimes.push(System.currentTimeMillis());
    } catch (final IOException e) {
      LOG.error(e.getMessage(), e);
    }
  }

  @Override
  public void executeCommandSuccess() {
    // nothing to do
  }

  @Override
  public void executeCommandIgnored() {
    try {
      printStartTagOpener(TAG_IGNORED);
      output.println("/>");
    } catch (final IOException e) {
      LOG.error(e.getMessage(), e);
    }
  }

  @Override
  public void executeCommandFailure(final AssertionException anAssertionException) {
    try {
      printFailureStart(anAssertionException);

      final Throwable tmpThrowable = anAssertionException.getCause();
      if (null != tmpThrowable) {
        printErrorMessageStack(tmpThrowable);
      }
      printFailureEnd();
      flush();
    } catch (final IOException e) {
      LOG.error(e.getMessage(), e);
    }
  }

  @Override
  public void executeCommandError(final Throwable aThrowable) {
    try {
      printErrorStart(aThrowable);
      printErrorMessageStack(aThrowable.getCause());

      // the stack trace
      printlnNode(TAG_ERROR_DETAILS, ExceptionUtils.getStackTrace(aThrowable));
      printErrorEnd();
      flush();
    } catch (final IOException e) {
      LOG.error(e.getMessage(), e);
    }
  }

  @Override
  public void executeCommandEnd() {
    try {
      printlnNode(TAG_EXECUTION_TIME, Long.toString(System.currentTimeMillis() - commandExecutionStartTimes.pop()));

      printlnEndTag(TAG_COMMAND);
    } catch (final IOException e) {
      LOG.error(e.getMessage(), e);
    }
  }

  @Override
  public void testFileEnd() {
    try {
      printlnEndTag(TAG_TESTFILE);
    } catch (final IOException e) {
      LOG.error(e.getMessage(), e);
    }
  }

  @Override
  public void testRunIgnored() {
    try {
      printStartTagOpener(TAG_IGNORED);
      output.println("/>");
    } catch (final IOException e) {
      LOG.error(e.getMessage(), e);
    }
  }

  @Override
  public void testRunEnd() {
    try {
      printlnEndTag(TAG_TESTRUN);
      flush();
    } catch (final IOException e) {
      LOG.error(e.getMessage(), e);
    }
  }

  @Override
  public void testCaseEnd() {
    try {
      printlnEndTag(TAG_TESTCASE);
      flush();
    } catch (final IOException e) {
      LOG.error(e.getMessage(), e);
    }
  }

  @Override
  public void end(final WetatorEngine aWetatorEngine) {
    try {
      printlnNode(TAG_EXECUTION_TIME, Long.toString(System.currentTimeMillis() - executionStartTime));

      output.println("<!--");
      output.println(SearchPattern.getStatistics());
      output.println("-->");

      printlnEndTag(TAG_WET);
      output.close();

      if (!xslTemplates.isEmpty()) {
        final XSLTransformer tmpXSLTransformer = new XSLTransformer(resultFile);
        tmpXSLTransformer.transform(xslTemplates, outputDir);
      }
    } catch (final IOException e) {
      LOG.error(e.getMessage(), e);
    }
  }

  @Override
  public void responseStored(final String aResponseFileName) {
    try {
      printlnNode(TAG_RESPONSE, aResponseFileName);
    } catch (final IOException e) {
      LOG.error(e.getMessage(), e);
    }
  }

  @Override
  public void highlightedResponse(final String aResponseFileName) {
    try {
      printlnNode(TAG_HIGHLIGHT, aResponseFileName);
    } catch (final IOException e) {
      LOG.error(e.getMessage(), e);
    }
  }

  @Override
  public void error(final Throwable aThrowable) {
    try {
      printErrorStart(aThrowable);
      printErrorMessageStack(aThrowable.getCause());

      // the stack trace
      printlnNode(TAG_ERROR_DETAILS, ExceptionUtils.getStackTrace(aThrowable));
      printErrorEnd();
      flush();
    } catch (final IOException e) {
      LOG.error(e.getMessage(), e);
    }
  }

  @Override
  public void warn(final String aMessageKey, final Object[] aParameters, final String aDetails) {
    try {
      final String tmpMessage = Messages.getMessage(aMessageKey, aParameters);
      if (LOG.isWarnEnabled()) {
        LOG.warn(tmpMessage);
      }
      printlnStartTag(TAG_LOG);

      printlnNode(TAG_LEVEL, "WARN");
      printlnNode(TAG_MESSAGE, tmpMessage);

      if (null != aDetails) {
        // the stack trace
        printlnNode(TAG_ERROR_DETAILS, aDetails);
      }

      printlnEndTag(TAG_LOG);
    } catch (final IOException e) {
      LOG.error(e.getMessage(), e);
    }
  }

  @Override
  public void info(final String aMessageKey, final Object... aParameters) {
    try {
      final String tmpMessage = Messages.getMessage(aMessageKey, aParameters);
      if (LOG.isInfoEnabled()) {
        LOG.info(tmpMessage);
      }
      printLogMessage("INFO", tmpMessage);
    } catch (final IOException e) {
      LOG.error(e.getMessage(), e);
    }
  }

  @Override
  public void htmlDescribe(final String aHtmlDescription) {
    try {
      printStartTag(TAG_DESCRIBE);
      output.print(aHtmlDescription);
      printlnEndTag(TAG_DESCRIBE);
    } catch (final IOException e) {
      LOG.error(e.getMessage(), e);
    }
  }

  private void printLogMessage(final String aLevel, final String aMessage) throws IOException {
    printlnStartTag(TAG_LOG);

    printlnNode(TAG_LEVEL, aLevel);
    printlnNode(TAG_MESSAGE, aMessage);

    printlnEndTag(TAG_LOG);
  }

  private void printFailureStart(final AssertionException anAssertionException) throws IOException {
    printlnStartTag(TAG_FAILURE);

    String tmpMessage = anAssertionException.getMessage();
    if (StringUtils.isBlank(tmpMessage)) {
      tmpMessage = anAssertionException.toString();
    }
    printlnNode(TAG_MESSAGE, tmpMessage);
  }

  private void printFailureEnd() throws IOException {
    printlnEndTag(TAG_FAILURE);
  }

  private void printErrorStart(final Throwable aThrowable) throws IOException {
    printlnStartTag(TAG_ERROR);

    String tmpMessage = aThrowable.getMessage();
    if (StringUtils.isBlank(tmpMessage)) {
      tmpMessage = aThrowable.toString();
    }
    printlnNode(TAG_MESSAGE, tmpMessage);
  }

  private void printErrorEnd() throws IOException {
    printlnEndTag(TAG_ERROR);
  }

  private void printErrorMessageStack(final Throwable aThrowable) {
    if (null == aThrowable) {
      return;
    }
    try {
      printErrorStart(aThrowable);

      final Throwable tmpThrowable = aThrowable.getCause();
      if (null != tmpThrowable) {
        printErrorMessageStack(tmpThrowable);
      }

      printErrorEnd();
    } catch (final IOException e) {
      LOG.error(e.getMessage(), e);
    }
  }

  private void printConfigurationProperty(final String aKey, final String aValue) throws IOException {
    printStartTagOpener(TAG_PROPERTY);
    output.print(" key=\"");
    output.print(xmlUtil.normalizeAttributeValue(aKey));
    if (null != aValue) {
      output.print("\" value=\"");
      output.print(xmlUtil.normalizeAttributeValue(aValue));
    }
    output.println("\" />");
  }

  private void printConfigurationProperty(final String aKey, final SecretString aValue) throws IOException {
    printStartTagOpener(TAG_PROPERTY);
    output.print(" key=\"");
    output.print(xmlUtil.normalizeAttributeValue(aKey));
    if (null != aValue) {
      output.print("\" value=\"");
      output.print(xmlUtil.normalizeAttributeValue(aValue.toString()));
    }
    output.println("\" />");
  }

  private void printlnNode(final String aNodeName, final String aNodeValue) throws IOException {
    printStartTag(aNodeName);
    output.print(xmlUtil.normalizeBodyValue(aNodeValue));
    printEndTag(aNodeName);
    output.println();
  }

  private void printlnStartTag(final String aName) throws IOException {
    printStartTag(aName);
    output.println();
    output.indent();
  }

  private void printStartTag(final String aName) throws IOException {
    printStartTagOpener(aName);
    output.print(">");
  }

  private void printlnEndTag(final String aName) throws IOException {
    output.unindent();
    printEndTag(aName);
    output.println();
  }

  private void printEndTag(final String aName) throws IOException {
    output.print("</").print(aName).print(">");
  }

  private void printStartTagOpener(final String aName) throws IOException {
    output.print("<").print(aName).print(" id=\"" + tagId++).print("\"");
  }

  private void flush() throws IOException {
    output.flush();
  }

}