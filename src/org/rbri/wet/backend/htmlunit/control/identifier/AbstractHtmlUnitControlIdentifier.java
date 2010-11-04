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


package org.rbri.wet.backend.htmlunit.control.identifier;

import java.util.List;

import org.rbri.wet.backend.WeightedControlList;
import org.rbri.wet.backend.htmlunit.util.DomNodeText;
import org.rbri.wet.exception.WetException;
import org.rbri.wet.util.SecretString;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * XXX add class jdoc
 * 
 * @author frank.danek
 */
public abstract class AbstractHtmlUnitControlIdentifier implements Runnable {

  /**
   * The page to work on.
   */
  protected HtmlPage htmlPage;
  /**
   * The DomNodeText index of the page.
   */
  protected DomNodeText domNodeText;
  /**
   * The list the found controls should be added to.
   */
  protected WeightedControlList foundElements;

  // for asynchronous use
  private boolean initializedForAsynch;
  private List<SecretString> search;
  private HtmlElement htmlElement;

  /**
   * Initializes the identifier.
   * 
   * @param aHtmlPage the page to work on
   * @param aDomNodeText the {@link DomNodeText} index of the page
   * @param aFoundElements the list the found controls should be added to
   */
  public void initialize(HtmlPage aHtmlPage, DomNodeText aDomNodeText, WeightedControlList aFoundElements) {
    htmlPage = aHtmlPage;
    domNodeText = aDomNodeText;
    foundElements = aFoundElements;
  }

  /**
   * Initializes the identifier to work asynchronously.
   * 
   * @param aHtmlPage the page to work on
   * @param aDomNodeText the {@link DomNodeText} index of the page
   * @param aHtmlElement the {@link HtmlElement} to be identified
   * @param aSearch the search used to identify the control
   * @param aFoundElements the list the found controls should be added to
   */
  public void initializeForAsynch(HtmlPage aHtmlPage, DomNodeText aDomNodeText, HtmlElement aHtmlElement,
      List<SecretString> aSearch, WeightedControlList aFoundElements) {
    initialize(aHtmlPage, aDomNodeText, aFoundElements);
    htmlElement = aHtmlElement;
    search = aSearch;
    initializedForAsynch = true;
  }

  /**
   * @param aHtmlElement the {@link HtmlElement} to check
   * @return true if the given element is supported
   */
  public abstract boolean isElementSupported(HtmlElement aHtmlElement);

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Runnable#run()
   */
  @Override
  public void run() {
    if (!initializedForAsynch) {
      throw new WetException(getClass().getName()
          + " is not initialized to work asynchronously. Use initializeForAsynch().");
    }
    identify(search, htmlElement);
  }

  /**
   * Tries to identify the given {@link HtmlElement} with the given search.
   * 
   * @param aSearch the search used to identify the control
   * @param aHtmlElement the {@link HtmlElement} to be identified
   */
  public abstract void identify(List<SecretString> aSearch, HtmlElement aHtmlElement);
}
