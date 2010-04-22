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


package org.rbri.wet.commandset;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.reflect.MethodUtils;
import org.rbri.wet.backend.Control;
import org.rbri.wet.backend.ControlFinder;
import org.rbri.wet.backend.WeightedControlList;
import org.rbri.wet.backend.WetBackend;
import org.rbri.wet.core.WetCommand;
import org.rbri.wet.core.WetContext;
import org.rbri.wet.core.result.WetResultWriter;
import org.rbri.wet.core.variable.Variable;
import org.rbri.wet.exception.AssertionFailedException;
import org.rbri.wet.exception.WetException;
import org.rbri.wet.util.Assert;
import org.rbri.wet.util.SecretString;
import org.rbri.wet.util.StringUtil;


/**
 * The implementation of all build in command that WeT
 * supports at the moment.
 *
 * @author rbri
 */
public final class DefaultCommandSet extends AbstractCommandSet {
    // private static final Log LOG = LogFactory.getLog(DefaultCommandSet.class);


    public DefaultCommandSet() {
        super();
    }

    protected void registerCommands() {
        registerCommand("Open Url", new CommandOpenUrl());
        registerCommand("Use Module", new CommandUseModule());
        registerCommand("Click On", new CommandClickOn());
        registerCommand("Set", new CommandSet());
        registerCommand("Select", new CommandSelect());
        registerCommand("Mouse Over", new CommandMouseOver());

        registerCommand("Assert Title", new CommandAssertTitle());
        registerCommand("Assert Content", new CommandAssertContent());
        registerCommand("Assert Disabled", new CommandAssertDisabled());
        registerCommand("Assert Set", new CommandAssertSet());
        registerCommand("Assert Selected", new CommandAssertSelected());
        registerCommand("Assert Deselected", new CommandAssertDeselected());

        registerCommand("Exec Java", new CommandExecJava());

        // for backward compatibility
        registerCommand("Comment", new CommandComment());
    }


    public final class CommandOpenUrl implements WetCommandImplementation {
        public void execute(WetContext aWetContext, WetCommand aWetCommand) throws WetException, AssertionFailedException  {

            SecretString tmpUrlParam = aWetCommand.getRequiredFirstParameterValue(aWetContext);
            aWetCommand.warnUnusedSecondParameter(aWetContext);

            try {
                // create the complete URL
                String tmpUrlToLower = tmpUrlParam.toLowerCase(Locale.ENGLISH);
                if (tmpUrlToLower.startsWith("http://") || tmpUrlToLower.startsWith("https://")) {
                    aWetContext.informListenersWarn("absoluteUrl", new String[] {tmpUrlParam.toString()});
                } else {
                    if (!tmpUrlParam.startsWith("/")) {
                        tmpUrlParam.prefixWith("/", "/");
                    }

                    tmpUrlParam.prefixWith(aWetContext.getWetConfiguration().getBaseUrl());
                }

                URL tmpUrl = new URL(tmpUrlParam.getValue());

                WetBackend tmpBackend = getWetBackend(aWetContext);
                tmpBackend.openUrl(tmpUrl);
            } catch (MalformedURLException e) {
                Assert.fail("invalidUrl", new String[] {tmpUrlParam.toString(), e.getMessage()});
            }
        }
    }


    public final class CommandUseModule implements WetCommandImplementation {
        public void execute(WetContext aWetContext, WetCommand aWetCommand) throws WetException, AssertionFailedException {

            SecretString tmpModuleParam = aWetCommand.getRequiredFirstParameterValue(aWetContext);
            List<SecretString> tmpModuleParameters = aWetCommand.getSecondParameterValues(aWetContext);

            String tmpModule = tmpModuleParam.getValue();

            File tmpFile = new File(tmpModule);

            if (!tmpFile.isAbsolute()) {
                tmpFile = new File(aWetContext.getFile().getParent(), tmpModule);
                aWetContext.informListenersInfo("useModule", new String[] {tmpFile.getAbsolutePath()});
            }

            // setup the new context
            WetContext tmpWetContext = new WetContext(aWetContext, tmpFile);

            int i = 1;
            for (SecretString secretString : tmpModuleParameters) {
                Variable tmpVariable = new Variable("" + i, secretString);
                tmpWetContext.addVariable(tmpVariable);
                i++;
            }
            tmpWetContext.execute();
        }
    }


    public final class CommandSet implements WetCommandImplementation {
        public void execute(WetContext aWetContext, WetCommand aWetCommand) throws WetException, AssertionFailedException {

            List<SecretString> tmpSearchParam = aWetCommand.getFirstParameterValues(aWetContext);
            SecretString tmpValueParam = aWetCommand.getSecondParameterValue(aWetContext);
            if (null == tmpValueParam) {
                tmpValueParam = new SecretString("", "");
            }

            WetBackend tmpBackend = getWetBackend(aWetContext);
            ControlFinder tmpElementFinder = tmpBackend.getControlFinder();

            // TextInputs / PasswordInputs / TextAreas / FileInputs
            WeightedControlList tmpFoundElements = tmpElementFinder.getAllSetables(tmpSearchParam);

            Control tmpControl = getRequiredFirstHtmlElementFrom(aWetContext, tmpFoundElements, tmpSearchParam);
            if (tmpSearchParam.isEmpty()) {
                aWetContext.informListenersWarn("firstElementUsed", new String[] {tmpControl.getDescribingText()});
            }
            tmpControl.setValue(tmpValueParam, aWetContext.getFile().getParentFile());
        }
    }


    public final class CommandClickOn implements WetCommandImplementation {
        public void execute(WetContext aWetContext, WetCommand aWetCommand) throws WetException, AssertionFailedException {

            List<SecretString> tmpSearchParam = aWetCommand.getRequiredFirstParameterValues(aWetContext);
            aWetCommand.warnUnusedSecondParameter(aWetContext);

            WetBackend tmpBackend = getWetBackend(aWetContext);
            ControlFinder tmpControlFinder = tmpBackend.getControlFinder();

            // Buttons / Link / Image
            WeightedControlList tmpFoundElements = tmpControlFinder.getAllClickables(tmpSearchParam);

            // Text
            tmpFoundElements.addAll(tmpControlFinder.getFirstClickableTextElement(tmpSearchParam));

            Control tmpControl = getRequiredFirstHtmlElementFrom(aWetContext, tmpFoundElements, tmpSearchParam);
            tmpControl.click();


        }
    }


    public final class CommandSelect implements WetCommandImplementation {
        public void execute(WetContext aWetContext, WetCommand aWetCommand) throws WetException, AssertionFailedException {

            List<SecretString> tmpSearchParam = aWetCommand.getRequiredFirstParameterValues(aWetContext);
            aWetCommand.warnUnusedSecondParameter(aWetContext);

            WetBackend tmpBackend = getWetBackend(aWetContext);
            ControlFinder tmpControlFinder = tmpBackend.getControlFinder();

            // (Select)Options / Checkboxes / Radiobuttons
            WeightedControlList tmpFoundElements = tmpControlFinder.getAllSelectables(tmpSearchParam);

            Control tmpControl = getRequiredFirstHtmlElementFrom(aWetContext, tmpFoundElements, tmpSearchParam);
            tmpControl.select();
        }
    }


    public final class CommandMouseOver implements WetCommandImplementation {
        public void execute(WetContext aWetContext, WetCommand aWetCommand) throws WetException, AssertionFailedException {

            List<SecretString> tmpSearchParam = aWetCommand.getRequiredFirstParameterValues(aWetContext);
            aWetCommand.warnUnusedSecondParameter(aWetContext);

            WetBackend tmpBackend = getWetBackend(aWetContext);
            ControlFinder tmpControlFinder = tmpBackend.getControlFinder();

            WeightedControlList tmpFoundElements = tmpControlFinder.getAllElementsForText(tmpSearchParam);

            Control tmpControl = getRequiredFirstHtmlElementFrom(aWetContext, tmpFoundElements, tmpSearchParam);
            tmpControl.mouseOver();
        }
    }


    // only for backward compatibility
    public final class CommandComment implements WetCommandImplementation {
        public void execute(WetContext aWetContext, WetCommand aWetCommand) throws WetException, AssertionFailedException  {
            // nothing to do
        }
    }


    public final class CommandAssertTitle implements WetCommandImplementation {

        public void execute(WetContext aWetContext, WetCommand aWetCommand) throws WetException, AssertionFailedException {

            List<SecretString> tmpExpected = aWetCommand.getRequiredFirstParameterValues(aWetContext);
            aWetCommand.warnUnusedSecondParameter(aWetContext);

            WetBackend tmpBackend = getWetBackend(aWetContext);
            String tmpCurrentTitle = tmpBackend.getCurrentTitle();

            Assert.assertListMatch(tmpExpected, tmpCurrentTitle);
        }
    }


    public final class CommandAssertContent implements WetCommandImplementation {
        public void execute(WetContext aWetContext, WetCommand aWetCommand) throws WetException, AssertionFailedException {

            List<SecretString> tmpExpected = aWetCommand.getRequiredFirstParameterValues(aWetContext);
            aWetCommand.warnUnusedSecondParameter(aWetContext);

            WetBackend tmpBackend = getWetBackend(aWetContext);
            String tmpCurrentContent = tmpBackend.getCurrentContentAsString();

            Assert.assertListMatch(tmpExpected, tmpCurrentContent);
        }
    }


    public final class CommandAssertDisabled implements WetCommandImplementation {
        public void execute(WetContext aWetContext, WetCommand aWetCommand) throws WetException, AssertionFailedException {

            List<SecretString> tmpSearchParam = aWetCommand.getRequiredFirstParameterValues(aWetContext);
            aWetCommand.warnUnusedSecondParameter(aWetContext);

            WetBackend tmpBackend = getWetBackend(aWetContext);
            ControlFinder tmpControlFinder = tmpBackend.getControlFinder();

            // TextInputs / PasswordInputs / TextAreas / FileInputs
            WeightedControlList tmpFoundElements = tmpControlFinder.getAllSetables(tmpSearchParam);
            tmpFoundElements.addAll(tmpControlFinder.getAllSelectables(tmpSearchParam));
            tmpFoundElements.addAll(tmpControlFinder.getAllClickables(tmpSearchParam));

            // search for special elements
            // e.g. selects by label, name, id
            tmpFoundElements.addAll(tmpControlFinder.getAllOtherControls(tmpSearchParam));

            // clickable Text
            tmpFoundElements.addAll(tmpControlFinder.getFirstClickableTextElement(tmpSearchParam));

            Control tmpControl = getRequiredFirstHtmlElementFrom(aWetContext, tmpFoundElements, tmpSearchParam);

            boolean tmpIsDisabled = tmpControl.isDisabled();
            Assert.assertTrue(tmpIsDisabled, "elementNotDisabled", new String[] {tmpControl.getDescribingText()});
        }
    }


    public final class CommandAssertSet implements WetCommandImplementation {
        public void execute(WetContext aWetContext, WetCommand aWetCommand) throws WetException, AssertionFailedException {

            List<SecretString> tmpSearchParam = aWetCommand.getRequiredFirstParameterValues(aWetContext);
            SecretString tmpValueParam = aWetCommand.getRequiredSecondParameterValue(aWetContext);

            WetBackend tmpBackend = getWetBackend(aWetContext);
            ControlFinder tmpControlFinder = tmpBackend.getControlFinder();

            // TextInputs / PasswordInputs / TextAreas / FileInputs
            WeightedControlList tmpFoundElements = tmpControlFinder.getAllSetables(tmpSearchParam);

            Control tmpControl = getRequiredFirstHtmlElementFrom(aWetContext, tmpFoundElements, tmpSearchParam);

            String tmpValue = tmpControl.getValue();

            // TODO improve secret handling
            Assert.assertEquals(tmpValueParam.getValue(), tmpValue, "expectedValueNotFound", null);
        }
    }


    public final class CommandAssertSelected implements WetCommandImplementation {
        public void execute(WetContext aWetContext, WetCommand aWetCommand) throws WetException, AssertionFailedException {
            List<SecretString> tmpSearchParam = aWetCommand.getRequiredFirstParameterValues(aWetContext);
            aWetCommand.warnUnusedSecondParameter(aWetContext);

            WetBackend tmpBackend = getWetBackend(aWetContext);
            ControlFinder tmpControlFinder = tmpBackend.getControlFinder();

            // (Select)Options / Checkboxes / Radiobuttons
            WeightedControlList tmpFoundElements = tmpControlFinder.getAllSelectables(tmpSearchParam);

            Control tmpControl = getRequiredFirstHtmlElementFrom(aWetContext, tmpFoundElements, tmpSearchParam);

            boolean tmpIsSelected = tmpControl.isSelected();
            Assert.assertTrue(tmpIsSelected, "elementNotSelected", new String[] {tmpControl.getDescribingText()});
        }
    }


    public final class CommandAssertDeselected implements WetCommandImplementation {
        public void execute(WetContext aWetContext, WetCommand aWetCommand) throws WetException, AssertionFailedException {
            List<SecretString> tmpSearchParam = aWetCommand.getRequiredFirstParameterValues(aWetContext);
            aWetCommand.warnUnusedSecondParameter(aWetContext);

            WetBackend tmpBackend = getWetBackend(aWetContext);
            ControlFinder tmpControlFinder = tmpBackend.getControlFinder();

            // (Select)Options / Checkboxes / Radiobuttons
            WeightedControlList tmpFoundElements = tmpControlFinder.getAllSelectables(tmpSearchParam);

            Control tmpControl = getRequiredFirstHtmlElementFrom(aWetContext, tmpFoundElements, tmpSearchParam);

            boolean tmpIsSelected = tmpControl.isSelected();
            Assert.assertFalse(tmpIsSelected, "elementNotDeselected", new String[] {tmpControl.getDescribingText()});
        }
    }


    public final class CommandExecJava implements WetCommandImplementation {
        public void execute(WetContext aWetContext, WetCommand aWetCommand) throws WetException, AssertionFailedException {
            SecretString tmpCall = aWetCommand.getRequiredFirstParameterValue(aWetContext);
            List<SecretString> tmpMethodParameters = aWetCommand.getSecondParameterValues(aWetContext);

            String tmpCallString = tmpCall.toString();
            int tmpLastDotPos = tmpCallString.lastIndexOf(".");
            if (tmpLastDotPos < 0) {
            	Assert.fail("javaExecSyntax", new String[] {tmpCallString});
            }

            String tmpClassName = tmpCallString.substring(0, tmpLastDotPos);
            if (StringUtils.isEmpty(tmpClassName)) {
            	Assert.fail("javaExecSyntax", new String[] {tmpCallString});
            }

            String tmpMethodName = tmpCallString.substring(tmpLastDotPos + 1);
            if (StringUtils.isEmpty(tmpMethodName)) {
            	Assert.fail("javaExecSyntax", new String[] {tmpCallString});
            }

        	Object[] tmpParams = new String[tmpMethodParameters.size()];
        	Class[] tmpParamTypes = new Class[tmpMethodParameters.size()];
            int i = 0;
            for (SecretString tmpSecret : tmpMethodParameters) {
            	tmpParams[i] = tmpSecret.toString();
            	tmpParamTypes[i] = String.class;
            	i++;
			}

			try {
				Class tmpClass = ClassUtils.getClass(tmpClassName);
				Method tmpMethod = MethodUtils.getMatchingAccessibleMethod(tmpClass, tmpMethodName, tmpParamTypes);
				if (null == tmpMethod) {
					tmpMethod = MethodUtils.getMatchingAccessibleMethod(tmpClass, tmpMethodName, new Class[] {String[].class});
	            	tmpParams = new Object[] {tmpParams};
				}
				if (null == tmpMethod) {
		            Assert.fail("javaExecMethodNotFound", new String[] {tmpClassName, tmpMethodName});
				}

				Object tmpReceiver = null;
				if (!Modifier.isStatic(tmpMethod.getModifiers())) {
					tmpReceiver = tmpClass.newInstance();
				}

				// time to execute
				Object tmpResult = tmpMethod.invoke(tmpReceiver, tmpParams);
				if (Void.TYPE != tmpMethod.getReturnType()) {
					if (null == tmpResult) {
						aWetContext.informListenersInfo("javaExecResult", new String[] {"null"});
					} else {
						aWetContext.informListenersInfo("javaExecResult", new String[] {tmpResult.toString()});
					}
				}
			} catch (ClassNotFoundException e) {
	            Assert.fail("javaExecClassNotFound", new String[] {tmpClassName});
			} catch (IllegalArgumentException e) {
	            Assert.fail("javaExecIllegalArgument", new String[] {tmpClassName, tmpMethodName, tmpMethodParameters.toString(), e.getMessage()});
			} catch (IllegalAccessException e) {
	            Assert.fail("javaExecIllegalAccess", new String[] {tmpClassName, tmpMethodName, tmpMethodParameters.toString(), e.getMessage()});
			} catch (InvocationTargetException e) {
	            Assert.fail("javaExecInvocationTarget", new String[] {tmpClassName, tmpMethodName, tmpMethodParameters.toString(), e.getMessage()});
			} catch (InstantiationException e) {
	            Assert.fail("javaExecInstantiation", new String[] {tmpClassName, tmpMethodName, tmpMethodParameters.toString(), e.getMessage()});
			}
        }
    }


    public void initialize(Properties aConfiguration) {
        // nothing to do at the moment
    }


    public void cleanup() {
        // nothing to do at the moment
    }


    public void printConfiguration(WetResultWriter aWetResultWriter) throws IOException {
        // nothing to do at the moment
    }
}
