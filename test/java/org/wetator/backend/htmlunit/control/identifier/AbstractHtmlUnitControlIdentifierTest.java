/*
 * Copyright (c) 2008-2011 www.wetator.org
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


package org.wetator.backend.htmlunit.control.identifier;

import java.io.IOException;
import java.util.List;

import org.wetator.backend.WPath;
import org.wetator.backend.WeightedControlList;
import org.wetator.backend.htmlunit.util.HtmlPageIndex;
import org.wetator.backend.htmlunit.util.PageUtil;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * @author frank.danek
 */
public abstract class AbstractHtmlUnitControlIdentifierTest {

  protected AbstractHtmlUnitControlIdentifier identifier;

  protected WeightedControlList identify(String aHtmlCode, String anHtmlElementId, WPath aWPath) throws IOException {
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(aHtmlCode);
    HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    HtmlElement tmpHtmlElement = tmpHtmlPage.getElementById(anHtmlElementId);

    identifier.initialize(tmpHtmlPageIndex);
    return identifier.identify(aWPath, tmpHtmlElement);
  }

  protected WeightedControlList identify(String aHtmlCode, String anHtmlElementIdOrName, int anIndex, WPath aWPath)
      throws IOException {
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(aHtmlCode);
    HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    List<HtmlElement> tmpHtmlElements = tmpHtmlPage.getElementsByIdAndOrName(anHtmlElementIdOrName);
    HtmlElement tmpHtmlElement = tmpHtmlElements.get(anIndex);

    identifier.initialize(tmpHtmlPageIndex);
    return identifier.identify(aWPath, tmpHtmlElement);
  }

  protected WeightedControlList identify(String aHtmlCode, WPath aWPath, String... anHtmlElementIds) throws IOException {
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(aHtmlCode);
    HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    WeightedControlList tmpControls = new WeightedControlList();
    for (String tmpHtmlElementId : anHtmlElementIds) {
      HtmlElement tmpHtmlElement = tmpHtmlPage.getElementById(tmpHtmlElementId);

      identifier.initialize(tmpHtmlPageIndex);
      tmpControls.addAll(identifier.identify(aWPath, tmpHtmlElement));
    }
    return tmpControls;
  }

  protected WeightedControlList identify(String aHtmlCode, WPath aWPath, int anIndex, String... anHtmlElementIdOrNames)
      throws IOException {
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(aHtmlCode);
    HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    WeightedControlList tmpControls = new WeightedControlList();
    for (String tmpHtmlElementIdOrName : anHtmlElementIdOrNames) {
      List<HtmlElement> tmpHtmlElements = tmpHtmlPage.getElementsByIdAndOrName(tmpHtmlElementIdOrName);
      HtmlElement tmpHtmlElement = tmpHtmlElements.get(anIndex);

      identifier.initialize(tmpHtmlPageIndex);
      tmpControls.addAll(identifier.identify(aWPath, tmpHtmlElement));
    }
    return tmpControls;
  }
}