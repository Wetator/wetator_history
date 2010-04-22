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


package org.rbri.wet.ant;

import java.io.File;

import org.apache.tools.ant.Project;
import org.rbri.wet.Version;
import org.rbri.wet.commandset.WetCommandSet;
import org.rbri.wet.core.WetCommand;
import org.rbri.wet.core.WetContext;
import org.rbri.wet.core.WetEngine;
import org.rbri.wet.core.WetEngineProgressListener;
import org.rbri.wet.exception.AssertionFailedException;


/**
 * Simple progress listener that writes to the ant output system.
 *
 * @author rbri
 */
public final class AntOutProgressListener implements WetEngineProgressListener {
    private Wetator antTask;
    private long stepsCount;
    private long errorCount;
    private long failureCount;

    public AntOutProgressListener(Wetator aWetator) {
        antTask = aWetator;
    }

    public void engineSetup(WetEngine aWetEngine) {
        println(Version.getProductName() + " " + Version.getVersion() + " ant task");
        println("  using " + com.gargoylesoftware.htmlunit.Version.getProductName() + " version " + com.gargoylesoftware.htmlunit.Version.getProductVersion());
        File tmpConfigFile = aWetEngine.getConfigFile();
        if (null != tmpConfigFile) {
            println("  Config: '" + tmpConfigFile.getAbsolutePath() + "'");
        }
        println(" ");
    }


    public void contextTestEnd() {
        // print summary
        println("   Steps: " + stepsCount + ",  Failures: " + failureCount + ",  Errors: " + errorCount);
        println(" ");
    }


    public void contextExecuteCommandStart(WetContext aWetContext, WetCommand aWommand) {
        stepsCount++;
    }


    public void contextExecuteCommandEnd() {
    }


    public void contextExecuteCommandError(Throwable aThrowable) {
        errorCount++;
    }


    public void contextExecuteCommandFailure(AssertionFailedException anAssertionFailedException) {
        failureCount++;
    }


    public void contextExecuteCommandSuccess() {
    }


    public void contextTestStart(String aFileName) {
        println("Test: " + aFileName);
        stepsCount = 0;
        errorCount = 0;
        failureCount = 0;
    }


    public void engineTestStart() {
    }


    public void engineResponseStored(String aResponseFileName) {
    }


    public void engineTestEnd() {
    }


    public void engineFinish() {
    }


    public void commandSetSetup(WetCommandSet wetCommandSet) {
    }


    public void warn(String aMessageKey, String[] aParameterArray) {
    }


    public void info(String aMessageKey, String[] aParameterArray) {
    }


    protected void println(String aString) {
         antTask.log(aString, Project.MSG_INFO);
    }
}
