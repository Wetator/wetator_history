/*
 * Copyright (c) 2008-2017 wetator.org
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


package org.wetator.commandset;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wetator.backend.IBrowser.BrowserType;
import org.wetator.exception.InvalidInputException;
import org.wetator.test.AbstractWebServerTest;
import org.wetator.test.junit.BrowserRunner;
import org.wetator.test.junit.BrowserRunner.Browsers;

/**
 * @author frank.danek
 */
@RunWith(BrowserRunner.class)
public class XmlIncubatorCommandSetTest extends AbstractWebServerTest {

  private static final String BASE_FOLDER = "test/xml/incubator/";

  @Test
  @Browsers({ BrowserType.FIREFOX_45, BrowserType.INTERNET_EXPLORER })
  public void bookmark() throws InvalidInputException {
    executeTestFile("bookmark.xml");

    Assert.assertEquals(11, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  @Browsers({ BrowserType.FIREFOX_45, BrowserType.INTERNET_EXPLORER })
  public void assertFocus() throws InvalidInputException {
    executeTestFile("assert_focus.xml");

    Assert.assertEquals(6, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  @Browsers({ BrowserType.FIREFOX_45, BrowserType.INTERNET_EXPLORER })
  public void execJs() throws InvalidInputException {
    executeTestFile("exec_js.xml");

    Assert.assertEquals(6, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  @Browsers({ BrowserType.FIREFOX_45 })
  public void assertApplet() throws InvalidInputException {
    executeTestFile("assert_applet.xml");

    Assert.assertEquals(6, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  @Browsers({ BrowserType.FIREFOX_45 })
  public void confirm() throws InvalidInputException {
    executeTestFile("confirm.xml");

    Assert.assertEquals(20, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  private void executeTestFile(final String aTestFileName) throws InvalidInputException {
    executeTestFile(new File(BASE_FOLDER + aTestFileName));
  }
}
