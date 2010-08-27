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


package org.rbri.wet.util;

import java.io.File;
import java.util.List;

import org.rbri.wet.Version;
import org.rbri.wet.core.WetCommand;
import org.rbri.wet.core.WetConfiguration;
import org.rbri.wet.core.WetContext;
import org.rbri.wet.core.WetEngine;
import org.rbri.wet.core.WetProgressListener;
import org.rbri.wet.exception.AssertionFailedException;

/**
 * Simple progress listener that writes to stdout.
 * 
 * @author rbri
 */
public class StdOutProgressListener implements WetProgressListener {

  private static final int DOTS_PER_LINE = 100;

  private long stepsCount;
  private long errorCount;
  private long failureCount;
  private int dotCount;
  private int contextDeep;

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.core.WetProgressListener#setup(org.rbri.wet.core.WetEngine)
   */
  public void setup(WetEngine aWetEngine) {
    println(Version.getProductName() + " " + Version.getVersion());
    println("  using " + com.gargoylesoftware.htmlunit.Version.getProductName() + " version "
        + com.gargoylesoftware.htmlunit.Version.getProductVersion());

    stepsCount = 0;
    errorCount = 0;
    failureCount = 0;
    contextDeep = 0;

    File tmpConfigFile = aWetEngine.getConfigFile();
    if (null != tmpConfigFile) {
      println("  Config:     '" + tmpConfigFile.getAbsolutePath() + "'");

      WetConfiguration tmpConfiguration = aWetEngine.getWetConfiguration();
      println("   OutputDir: '" + tmpConfiguration.getOutputDir().getAbsolutePath() + "'");
      boolean tmpFirst = true;
      for (String tmpTemplate : tmpConfiguration.getXslTemplates()) {
        if (tmpFirst) {
          println("   Templates: '" + tmpTemplate + "'");
          tmpFirst = false;
        } else {
          println("              '" + tmpTemplate + "'");
        }
      }
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.core.WetProgressListener#start(java.util.List)
   */
  public void start(List<File> aTestFilesList) {
    if (aTestFilesList.isEmpty()) {
      println("   TestFiles: none");
      return;
    }

    boolean tmpFirst = true;
    for (File tmpTestFile : aTestFilesList) {
      if (tmpFirst) {
        println("   TestFiles: '" + tmpTestFile.getAbsolutePath() + "'");
        tmpFirst = false;
      } else {
        println("              '" + tmpTestFile.getAbsolutePath() + "'");
      }
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.core.WetProgressListener#testCaseStart(String)
   */
  public void testCaseStart(String aTestName) {
    println("Test: '" + aTestName + "'");
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.core.WetProgressListener#testRunStart(String)
   */
  public void testRunStart(String aBrowserName) {
    println("    " + aBrowserName);
    print("    ");
    dotCount = 1;
    contextDeep = 0;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.core.WetProgressListener#testFileStart(String)
   */
  public void testFileStart(String aFileName) {
    contextDeep++;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.core.WetProgressListener#executeCommandStart(org.rbri.wet.core.WetContext,
   *      org.rbri.wet.core.WetCommand)
   */
  public void executeCommandStart(WetContext aWetContext, WetCommand aWommand) {
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.core.WetProgressListener#executeCommandEnd()
   */
  public void executeCommandEnd() {
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.core.WetProgressListener#executeCommandError(java.lang.Throwable)
   */
  public void executeCommandError(Throwable aThrowable) {
    stepsCount++;
    errorCount++;
    printProgressSign("E");
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.core.WetProgressListener#executeCommandFailure(org.rbri.wet.exception.AssertionFailedException)
   */
  public void executeCommandFailure(AssertionFailedException anAssertionFailedException) {
    stepsCount++;
    failureCount++;
    printProgressSign("F");
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.core.WetProgressListener#executeCommandSuccess()
   */
  public void executeCommandSuccess() {
    stepsCount++;
    printProgressSign(".");
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.core.WetProgressListener#testFileEnd()
   */
  public void testFileEnd() {
    contextDeep--;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.core.WetProgressListener#testRunEnd()
   */
  public void testRunEnd() {
    println("");
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.core.WetProgressListener#testEnd()
   */
  public void testCaseEnd() {
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.core.WetProgressListener#end()
   */
  public void end() {
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.core.WetProgressListener#finish()
   */
  public void finish() {
    // print summary
    println("");
    println("Steps: " + stepsCount + ",  Failures: " + failureCount + ",  Errors: " + errorCount);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.core.WetProgressListener#responseStored(java.lang.String)
   */
  public void responseStored(String aResponseFileName) {
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.core.WetProgressListener#warn(java.lang.String, java.lang.String[])
   */
  public void warn(String aMessageKey, String[] aParameterArray) {
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.core.WetProgressListener#info(java.lang.String, java.lang.String[])
   */
  public void info(String aMessageKey, String[] aParameterArray) {
  }

  /**
   * The worker that does the real output
   * 
   * @param aString the output
   */
  protected void println(String aString) {
    System.out.println(aString);
  }

  /**
   * The worker that does the real output
   * 
   * @param aString the output
   */
  protected void print(String aString) {
    System.out.print(aString);
  }

  /**
   * The worker that does the real output
   * 
   * @param aProgressSign the output
   */
  protected void printProgressSign(String aProgressSign) {
    if (dotCount == DOTS_PER_LINE) {
      println(aProgressSign);
      print("    ");
      dotCount = 1;
      return;
    }
    print(aProgressSign);
    dotCount++;
  }
}
