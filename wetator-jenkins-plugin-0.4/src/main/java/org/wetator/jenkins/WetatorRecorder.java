/*
 * Copyright (c) wetator.org
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


package org.wetator.jenkins;

import hudson.AbortException;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Action;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;
import hudson.util.FormValidation;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

import org.apache.commons.lang.StringUtils;
import org.apache.tools.ant.types.FileSet;
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.wetator.jenkins.parser.WetatorResultParser;
import org.wetator.jenkins.result.TestResults;

/**
 * The recorder parsing the Wetator results and creating the reports.
 * 
 * @author frank.danek
 */
public class WetatorRecorder extends Recorder {

  /**
   * {@link FileSet} "includes" string, like "foo/bar/*.xml"
   */
  private String testResults;
  /**
   * {@link FileSet} "includes" string, like "foo/bar/*.xml"
   */
  private String testReports;
  private String unstableThreshold;
  private String failureThreshold;

  /**
   * The constructor.
   * 
   * @param testResults the file patter of the test results
   */
  @DataBoundConstructor
  public WetatorRecorder(String testResults, String testReports, String unstableThreshold, String failureThreshold) {
    // the method parameters must be raw (without leading a) to make stapler work
    this.testResults = testResults;
    this.testReports = testReports;
    this.unstableThreshold = unstableThreshold;
    if (this.unstableThreshold == null || "".equals(this.unstableThreshold)) {
      this.unstableThreshold = "0";
    }
    this.failureThreshold = failureThreshold;
  }

  /**
   * @return the testResults
   */
  public String getTestResults() {
    return testResults;
  }

  /**
   * @return the testReports
   */
  public String getTestReports() {
    return testReports;
  }

  /**
   * @return the unstableThreshold
   */
  public String getUnstableThreshold() {
    return unstableThreshold;
  }

  /**
   * @return the failureThreshold
   */
  public String getFailureThreshold() {
    return failureThreshold;
  }

  /**
   * {@inheritDoc}
   * 
   * @see hudson.tasks.BuildStep#getRequiredMonitorService()
   */
  @Override
  public BuildStepMonitor getRequiredMonitorService() {
    return BuildStepMonitor.BUILD;
  }

  /**
   * {@inheritDoc}
   * 
   * @see hudson.tasks.BuildStep#perform(hudson.model.AbstractBuild, hudson.Launcher, hudson.model.BuildListener)
   */
  @Override
  public boolean perform(AbstractBuild<?, ?> aBuild, Launcher aLauncher, BuildListener aListener)
      throws InterruptedException, IOException {
    aListener.getLogger().println(Messages.WetatorRecorder_Recording());
    WetatorBuildReport tmpReport;

    final String tmpTestResults = aBuild.getEnvironment(aListener).expand(testResults);
    String tmpTestReports = null;
    if (StringUtils.isNotBlank(testReports)) {
      tmpTestReports = aBuild.getEnvironment(aListener).expand(testReports);
    }

    try {
      TestResults tmpResult = new WetatorResultParser().parse(tmpTestResults, tmpTestReports, aBuild);
      tmpResult.setName(PluginImpl.TEST_RESULTS_NAME);

      try {
        tmpReport = new WetatorBuildReport(aBuild, tmpResult, aListener);
      } catch (NullPointerException e) {
        throw new AbortException(Messages.WetatorRecorder_BadXML(tmpTestResults));
      }

      if (tmpResult.getPassCount() == 0 && tmpResult.getFailCount() == 0) {
        throw new AbortException(Messages.WetatorRecorder_ResultIsEmpty());
      }
    } catch (AbortException e) {
      if (aBuild.getResult() == Result.FAILURE) {
        // most likely a build failed before it gets to the test phase.
        // don't report confusing error message.
        return true;
      }

      aListener.getLogger().println(e.getMessage());
      aBuild.setResult(Result.FAILURE);
      return true;
    }

    aBuild.getActions().add(tmpReport);

    if (tmpReport.getResults().getFailCount() > Integer.parseInt(unstableThreshold)) {
      aBuild.setResult(Result.UNSTABLE);
    }
    if (failureThreshold != null && !"".equals(failureThreshold)
        && tmpReport.getResults().getFailCount() > Integer.parseInt(failureThreshold)) {
      aBuild.setResult(Result.FAILURE);
    }

    return true;
  }

  /**
   * {@inheritDoc}
   * 
   * @see hudson.tasks.BuildStep#getProjectActions(hudson.model.AbstractProject)
   */
  @Override
  public Collection<Action> getProjectActions(AbstractProject<?, ?> project) {
    return Collections.<Action> singleton(new WetatorProjectReport(project));
  }

  /**
   * {@inheritDoc}
   * 
   * @see hudson.tasks.Recorder#getDescriptor()
   */
  @Override
  public DescriptorImpl getDescriptor() {
    return (DescriptorImpl) super.getDescriptor();
  }

  /**
   * Descriptor for {@link WetatorRecorder}. Used as a singleton.
   * The class is marked as public so that it can be accessed from views.
   */
  @Extension
  public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {
    /**
     * {@inheritDoc}
     * 
     * @see hudson.model.Descriptor#getDisplayName()
     */
    @Override
    public String getDisplayName() {
      return Messages.WetatorRecorder_DisplayName();
    }

    /**
     * {@inheritDoc}
     * 
     * @see hudson.tasks.BuildStepDescriptor#isApplicable(java.lang.Class)
     */
    @Override
    public boolean isApplicable(@SuppressWarnings("rawtypes") Class<? extends AbstractProject> aJobType) {
      return true;
    }

    /**
     * Performs on-the-fly validation on the file mask wildcard.
     * 
     * @param project the owner project
     * @param value the value to check
     * @return the result of the check
     * @throws IOException in case of problems
     */
    public FormValidation doCheckTestResults(@AncestorInPath AbstractProject<?, ?> project, @QueryParameter String value)
        throws IOException {
      // the method parameters must be raw (without leading a) to make stapler work
      return FilePath.validateFileMask(project.getSomeWorkspace(), value);
    }

    /**
     * Performs on-the-fly validation on the unstable threshold.
     * 
     * @param project the owner project
     * @param value the value to check
     * @return the result of the check
     * @throws IOException in case of problems
     */
    public FormValidation doCheckUnstableThreshold(@AncestorInPath AbstractProject<?, ?> project,
        @QueryParameter String value) throws IOException {
      // the method parameters must be raw (without leading a) to make stapler work
      if (value == null || "".equals(value)) {
        return FormValidation.ok();
      }
      return FormValidation.validateNonNegativeInteger(value);
    }

    /**
     * Performs on-the-fly validation on the failure threshold.
     * 
     * @param project the owner project
     * @param value the value to check
     * @return the result of the check
     * @throws IOException in case of problems
     */
    public FormValidation doCheckFailureThreshold(@AncestorInPath AbstractProject<?, ?> project,
        @QueryParameter String value) throws IOException {
      // the method parameters must be raw (without leading a) to make stapler work
      if (value == null || "".equals(value)) {
        return FormValidation.ok();
      }
      return FormValidation.validateNonNegativeInteger(value);
    }
  }
}
