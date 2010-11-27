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


package org.rbri.wet.backend.htmlunit;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.rbri.wet.backend.htmlunit.control.HtmlUnitAnchor;
import org.rbri.wet.backend.htmlunit.control.HtmlUnitBaseControl;
import org.rbri.wet.backend.htmlunit.control.HtmlUnitBaseControl.ForHtmlElement;
import org.rbri.wet.backend.htmlunit.util.PageUtil;

import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * @author frank.danek
 */
public class HtmlUnitControlRepositoryTest {

  @Test
  public void getForHtmlElementNotFound() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<a id='myId' href='snoopy.php'>TestAnchor</a>"
        + "</form>" + "</body></html>";

    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    HtmlElement tmpHtmlElement = tmpHtmlPage.getElementById("myId");

    HtmlUnitControlRepository tmpRepository = new HtmlUnitControlRepository();

    Assert.assertNull(tmpRepository.getForHtmlElement(tmpHtmlElement));
  }

  @Test
  public void getForHtmlElementByElement() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<a id='myId' href='snoopy.php'>TestAnchor</a>"
        + "</form>" + "</body></html>";

    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    HtmlElement tmpHtmlElement = tmpHtmlPage.getElementById("myId");

    HtmlUnitControlRepository tmpRepository = new HtmlUnitControlRepository();
    tmpRepository.add(HtmlUnitAnchor.class);

    Assert.assertEquals(HtmlUnitAnchor.class, tmpRepository.getForHtmlElement(tmpHtmlElement));
  }

  @Test
  public void getForHtmlElementByElementAndAttribute() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<a id='myId' href='snoopy.php'>TestAnchor</a>"
        + "<a id='myId2' href='snoopy.php'>TestAnchor</a>" + "</form>" + "</body></html>";

    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    HtmlElement tmpHtmlElement = tmpHtmlPage.getElementById("myId2");

    HtmlUnitControlRepository tmpRepository = new HtmlUnitControlRepository();
    tmpRepository.add(HtmlUnitAnchor.class);
    tmpRepository.add(TestControl.class);

    Assert.assertEquals(TestControl.class, tmpRepository.getForHtmlElement(tmpHtmlElement));
  }

  @Test
  public void getForHtmlElementByElementAndAttributeFallBack() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<a id='myId' href='snoopy.php'>TestAnchor</a>"
        + "<a id='myId2' href='snoopy.php'>TestAnchor</a>" + "</form>" + "</body></html>";

    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    HtmlElement tmpHtmlElement = tmpHtmlPage.getElementById("myId");

    HtmlUnitControlRepository tmpRepository = new HtmlUnitControlRepository();
    tmpRepository.add(HtmlUnitAnchor.class);
    tmpRepository.add(TestControl.class);

    Assert.assertEquals(HtmlUnitAnchor.class, tmpRepository.getForHtmlElement(tmpHtmlElement));
  }

  /**
   * @author frank.danek
   */
  @ForHtmlElement(value = HtmlAnchor.class, attributeName = "id", attributeValues = { "myId2" })
  private class TestControl extends HtmlUnitBaseControl<HtmlAnchor> {

    /**
     * The constructor.
     * 
     * @param anHtmlElement the {@link HtmlElement} from the backend
     */
    public TestControl(HtmlAnchor anHtmlElement) {
      super(anHtmlElement);
    }
  }
}
