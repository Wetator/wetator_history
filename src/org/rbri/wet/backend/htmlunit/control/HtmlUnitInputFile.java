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
import java.io.IOException;

import net.sourceforge.htmlunit.corejs.javascript.WrappedException;

import org.apache.commons.lang.StringUtils;
import org.rbri.wet.backend.control.Settable;
import org.rbri.wet.backend.htmlunit.control.HtmlUnitBaseControl.ForHtmlElement;
import org.rbri.wet.backend.htmlunit.control.HtmlUnitBaseControl.IdentifiedBy;
import org.rbri.wet.backend.htmlunit.control.identifier.HtmlUnitInputFileIdentifier;
import org.rbri.wet.backend.htmlunit.util.ExceptionUtil;
import org.rbri.wet.backend.htmlunit.util.HtmlElementUtil;
import org.rbri.wet.core.WetContext;
import org.rbri.wet.exception.AssertionFailedException;
import org.rbri.wet.util.Assert;
import org.rbri.wet.util.SecretString;

import com.gargoylesoftware.htmlunit.ScriptException;
import com.gargoylesoftware.htmlunit.html.HtmlFileInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * This is the implementation of the HTML element 'input file' (&lt;input type="file"&gt;) using HtmlUnit as
 * backend.
 * 
 * @author rbri
 * @author frank.danek
 */
@ForHtmlElement(HtmlFileInput.class)
@IdentifiedBy(HtmlUnitInputFileIdentifier.class)
public class HtmlUnitInputFile extends HtmlUnitBaseControl<HtmlFileInput> implements Settable {

  /**
   * The constructor.
   * 
   * @param anHtmlElement the {@link HtmlFileInput} from the backend
   */
  public HtmlUnitInputFile(HtmlFileInput anHtmlElement) {
    super(anHtmlElement);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.backend.htmlunit.control.HtmlUnitBaseControl#getDescribingText()
   */
  @Override
  public String getDescribingText() {
    return HtmlElementUtil.getDescribingTextForHtmlFileInput(getHtmlElement());
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.backend.control.Settable#setValue(org.rbri.wet.core.WetContext, org.rbri.wet.util.SecretString,
   *      java.io.File)
   */
  @Override
  public void setValue(WetContext aWetContext, SecretString aValue, File aDirectory) throws AssertionFailedException {
    HtmlFileInput tmpHtmlFileInput = getHtmlElement();

    Assert.assertTrue(!tmpHtmlFileInput.isDisabled(), "elementDisabled", new String[] { getDescribingText() });
    Assert.assertTrue(!tmpHtmlFileInput.isReadOnly(), "elementReadOnly", new String[] { getDescribingText() });

    try {
      tmpHtmlFileInput.click();
    } catch (IOException e) {
      aWetContext.getWetBackend().addFailure("serverError", new String[] { e.getMessage(), getDescribingText() }, e);
    } catch (ScriptException e) {
      aWetContext.getWetBackend().addFailure("javascriptError", new String[] { e.getMessage() }, e);
    } catch (WrappedException e) {
      Exception tmpScriptException = ExceptionUtil.getScriptExceptionCauseIfPossible(e);
      aWetContext.getWetBackend().addFailure("javascriptError", new String[] { tmpScriptException.getMessage() },
          tmpScriptException);
    }

    try {
      String tmpValue = aValue.getValue();
      if (StringUtils.isBlank(tmpValue)) {
        tmpHtmlFileInput.setValueAttribute("");
      } else {
        // now we have to determine the correct absolute file path
        File tmpFile = new File(tmpValue);

        if (!tmpFile.isAbsolute() && (null != aDirectory)) {
          // relative paths are relative to the location of the calling file
          tmpFile = new File(aDirectory, aValue.getValue());
        }

        // validate file
        if (!tmpFile.exists()) {
          Assert.fail("fileNotFound", new String[] { tmpFile.getAbsolutePath() });
        }

        // simulate events during file selection via file dialog
        ((HtmlPage) tmpHtmlFileInput.getPage()).setFocusedElement(null);
        tmpHtmlFileInput.setValueAttribute(tmpFile.getAbsolutePath());
        tmpHtmlFileInput.focus();
      }

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
