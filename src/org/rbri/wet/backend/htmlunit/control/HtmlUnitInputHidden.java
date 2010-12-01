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


package org.rbri.wet.backend.htmlunit.control;

import java.io.File;

import net.sourceforge.htmlunit.corejs.javascript.WrappedException;

import org.rbri.wet.backend.control.Settable;
import org.rbri.wet.backend.htmlunit.control.HtmlUnitBaseControl.ForHtmlElement;
import org.rbri.wet.backend.htmlunit.util.ExceptionUtil;
import org.rbri.wet.backend.htmlunit.util.HtmlElementUtil;
import org.rbri.wet.core.WetContext;
import org.rbri.wet.exception.AssertionFailedException;
import org.rbri.wet.util.Assert;
import org.rbri.wet.util.SecretString;

import com.gargoylesoftware.htmlunit.ScriptException;
import com.gargoylesoftware.htmlunit.html.HtmlHiddenInput;

/**
 * This is the implementation of the HTML element 'input hidden' (&lt;input type="hidden"&gt;) using HtmlUnit as
 * backend. There is no identifier for this control as it may not be set via wetator.
 * 
 * @author rbri
 * @author frank.danek
 */
@ForHtmlElement(HtmlHiddenInput.class)
public class HtmlUnitInputHidden extends HtmlUnitBaseControl<HtmlHiddenInput> implements Settable {

  /**
   * The constructor.
   * 
   * @param anHtmlElement the {@link HtmlHiddenInput} from the backend
   */
  public HtmlUnitInputHidden(HtmlHiddenInput anHtmlElement) {
    super(anHtmlElement);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.backend.htmlunit.control.HtmlUnitBaseControl#getDescribingText()
   */
  @Override
  public String getDescribingText() {
    return HtmlElementUtil.getDescribingTextForHtmlHiddenInput(getHtmlElement());
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.backend.control.Settable#setValue(org.rbri.wet.core.WetContext, org.rbri.wet.util.SecretString,
   *      java.io.File)
   */
  @Override
  public void setValue(WetContext aWetContext, SecretString aValue, File aDirectory) throws AssertionFailedException {
    HtmlHiddenInput tmpHtmlHiddenInput = getHtmlElement();

    Assert.assertTrue(!tmpHtmlHiddenInput.isDisabled(), "elementDisabled", new String[] { getDescribingText() });
    Assert.assertTrue(!tmpHtmlHiddenInput.isReadOnly(), "elementReadOnly", new String[] { getDescribingText() });

    try {
      String tmpValue = aValue.getValue();
      tmpHtmlHiddenInput.setAttribute("value", tmpValue);

      // wait for silence
      aWetContext.getWetBackend().waitForImmediateJobs();
    } catch (ScriptException e) {
      aWetContext.getWetBackend().addFailure("javascriptError", new String[] { e.getMessage() }, e);
    } catch (WrappedException e) {
      Exception tmpScriptException = ExceptionUtil.getScriptExceptionCauseIfPossible(e);
      aWetContext.getWetBackend().addFailure("javascriptError", new String[] { tmpScriptException.getMessage() },
          tmpScriptException);
    } catch (AssertionFailedException e) {
      aWetContext.getWetBackend().addFailure(e);
    } catch (Throwable e) {
      aWetContext.getWetBackend().addFailure("serverError", new String[] { e.getMessage(), getDescribingText() }, e);
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.backend.control.Settable#assertValue(org.rbri.wet.core.WetContext,
   *      org.rbri.wet.util.SecretString)
   */
  @Override
  public void assertValue(WetContext aWetContext, SecretString anExpectedValue) throws AssertionFailedException {
    Assert.assertEquals(anExpectedValue, getHtmlElement().getValueAttribute(), "expectedValueNotFound", null);
  }
}
