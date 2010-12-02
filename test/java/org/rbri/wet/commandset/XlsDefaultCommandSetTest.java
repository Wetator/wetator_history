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

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.rbri.wet.backend.WetBackend.Browser;
import org.rbri.wet.test.AbstractWebServerTest;
import org.rbri.wet.test.junit.BrowserRunner;
import org.rbri.wet.test.junit.BrowserRunner.Browsers;

/**
 * @author frank.danek
 */
@RunWith(BrowserRunner.class)
public class XlsDefaultCommandSetTest extends AbstractWebServerTest {

  private static final String BASE_FOLDER = "test/excel/";

  @Test
  public void ajaxJquery() {
    executeTestFile("ajax_jquery.xls");

    Assert.assertEquals(34, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void ajaxPrototype() {
    executeTestFile("ajax_prototype.xls");

    Assert.assertEquals(10, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void anchorInsidePage() {
    executeTestFile("anchor_inside_page.xls");

    Assert.assertEquals(14, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void assertContent() {
    executeTestFile("assert_content.xls");

    Assert.assertEquals(52, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void assertDeselectedCheckbox() {
    executeTestFile("assert_deselected_checkbox.xls");

    Assert.assertEquals(27, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void assertDeselectedMultipleSelect() {
    executeTestFile("assert_deselected_multipleSelect.xls");

    Assert.assertEquals(35, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void assertDeselectedRadio() {
    executeTestFile("assert_deselected_radio.xls");

    Assert.assertEquals(20, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void assertDeselectedSingleSelect() {
    executeTestFile("assert_deselected_singleSelect.xls");

    Assert.assertEquals(29, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void assertDisabled() {
    executeTestFile("assert_disabled.xls");

    Assert.assertEquals(54, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void assertSelectedCheckbox() {
    executeTestFile("assert_selected_checkbox.xls");

    Assert.assertEquals(27, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void assertSelectedMultipleSelect() {
    executeTestFile("assert_selected_multipleSelect.xls");

    Assert.assertEquals(35, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void assertSelectedRadio() {
    executeTestFile("assert_selected_radio.xls");

    Assert.assertEquals(20, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void assertSelectedSingleSelect() {
    executeTestFile("assert_selected_singleSelect.xls");

    Assert.assertEquals(29, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void assertSet() {
    executeTestFile("assert_set.xls");

    Assert.assertEquals(81, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void assertTitle() {
    executeTestFile("assert_title.xls");

    Assert.assertEquals(21, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void clickAfterText() {
    executeTestFile("click_after_text.xls");

    Assert.assertEquals(280, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void clickOn() {
    executeTestFile("click_on.xls");

    Assert.assertEquals(153, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void deselectCheckbox() {
    executeTestFile("deselect_checkbox.xls");

    Assert.assertEquals(53, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void deselectMultipleSelect() {
    executeTestFile("deselect_multipleSelect.xls");

    Assert.assertEquals(73, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  @Browsers({ Browser.FIREFOX_3_6 })
  public void eventClickFF36() {
    executeTestFile("ff3/event_click.xls");

    Assert.assertEquals(63, getSteps());
    Assert.assertEquals(16, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  @Browsers({ Browser.INTERNET_EXPLORER_6 })
  public void eventClickIE6() {
    executeTestFile("ie6/event_click.xls");

    Assert.assertEquals(63, getSteps());
    Assert.assertEquals(16, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  @Browsers({ Browser.INTERNET_EXPLORER_8 })
  public void eventClickIE8() {
    executeTestFile("ie8/event_click.xls");

    Assert.assertEquals(63, getSteps());
    Assert.assertEquals(16, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  @Browsers({ Browser.FIREFOX_3_6 })
  public void eventDeselectFF36() {
    executeTestFile("ff3/event_deselect.xls");

    Assert.assertEquals(20, getSteps());
    Assert.assertEquals(2, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  @Browsers({ Browser.INTERNET_EXPLORER_6 })
  public void eventDeselectIE6() {
    executeTestFile("ie6/event_deselect.xls");

    Assert.assertEquals(20, getSteps());
    Assert.assertEquals(2, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  @Browsers({ Browser.INTERNET_EXPLORER_8 })
  public void eventDeselectIE8() {
    executeTestFile("ie8/event_deselect.xls");

    Assert.assertEquals(20, getSteps());
    Assert.assertEquals(2, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void eventHandlerOLD() {
    executeTestFile("event_handler.xls");

    Assert.assertEquals(72, getSteps());
    Assert.assertEquals(14, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  @Browsers({ Browser.INTERNET_EXPLORER_6 })
  public void eventHandlerOLDIE6() {
    executeTestFile("ie6/event_handler.xls");

    Assert.assertEquals(81, getSteps());
    Assert.assertEquals(16, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  @Browsers({ Browser.FIREFOX_3_6 })
  public void eventHandlerOLDFF36() {
    executeTestFile("ff3/event_handler.xls");

    Assert.assertEquals(81, getSteps());
    Assert.assertEquals(19, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  @Browsers({ Browser.FIREFOX_3_6 })
  public void eventMouseOverFF36() {
    executeTestFile("ff3/event_mouseOver.xls");

    Assert.assertEquals(74, getSteps());
    Assert.assertEquals(5, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  @Browsers({ Browser.INTERNET_EXPLORER_6 })
  public void eventMouseOverIE6() {
    executeTestFile("ie6/event_mouseOver.xls");

    Assert.assertEquals(74, getSteps());
    Assert.assertEquals(16, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  @Browsers({ Browser.INTERNET_EXPLORER_8 })
  public void eventMouseOverIE8() {
    executeTestFile("ie8/event_mouseOver.xls");

    Assert.assertEquals(74, getSteps());
    Assert.assertEquals(16, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  @Browsers({ Browser.FIREFOX_3_6 })
  public void eventSelectFF36() {
    executeTestFile("ff3/event_select.xls");

    Assert.assertEquals(30, getSteps());
    Assert.assertEquals(4, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  @Browsers({ Browser.INTERNET_EXPLORER_6 })
  public void eventSelectIE6() {
    executeTestFile("ie6/event_select.xls");

    Assert.assertEquals(30, getSteps());
    Assert.assertEquals(4, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  @Browsers({ Browser.INTERNET_EXPLORER_8 })
  public void eventSelectIE8() {
    executeTestFile("ie8/event_select.xls");

    Assert.assertEquals(30, getSteps());
    Assert.assertEquals(4, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void execJava() {
    executeTestFile("exec_java.xls");

    Assert.assertEquals(29, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void flowSimpleLogin() {
    executeTestFile("flow_simple_login.xls");

    Assert.assertEquals(7, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void flowSimpleSearch() {
    executeTestFile("flow_simple_search.xls");

    Assert.assertEquals(12, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void goBack() {
    executeTestFile("go_back.xls");

    Assert.assertEquals(10, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void header() {
    executeTestFile("header.xls");

    Assert.assertEquals(3, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void htmlunitJavascript() {
    executeTestFile("htmlunit_javascript.xls");

    Assert.assertEquals(4, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void jquery() {
    executeTestFile("jquery.xls");

    Assert.assertEquals(9, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void jsError() {
    executeTestFile("js_error.xls");

    Assert.assertEquals(34, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void jsLibs() {
    executeTestFile("js_libs.xls");

    Assert.assertEquals(6, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void mouseOverAfter() {
    executeTestFile("mouse_over_after.xls");

    Assert.assertEquals(33, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void mouseOver() {
    executeTestFile("mouse_over.xls");

    Assert.assertEquals(18, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void normalizeCommand() {
    executeTestFile("normalize_command.xls");

    Assert.assertEquals(6, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void onfocusOnblur() {
    executeTestFile("onfocus_onblur.xls");

    Assert.assertEquals(56, getSteps());
    Assert.assertEquals(11, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void onkey() {
    executeTestFile("onkey.xls");

    Assert.assertEquals(65, getSteps());
    Assert.assertEquals(13, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void openUrl() {
    executeTestFile("open_url.xls");

    Assert.assertEquals(21, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void redirect() {
    executeTestFile("redirect.xls");

    Assert.assertEquals(12, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void selectCheckboxAfter() {
    executeTestFile("select_checkbox_after.xls");

    Assert.assertEquals(50, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void selectCheckbox() {
    executeTestFile("select_checkbox.xls");

    Assert.assertEquals(53, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void selectMultipleSelect() {
    executeTestFile("select_multipleSelect.xls");

    Assert.assertEquals(73, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void selectRadio() {
    executeTestFile("select_radio.xls");

    Assert.assertEquals(40, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void selectSingleSelect() {
    executeTestFile("select_singleSelect.xls");

    Assert.assertEquals(68, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void setUpload() {
    executeTestFile("set_upload.xls");

    Assert.assertEquals(69, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void set() {
    executeTestFile("set.xls");

    Assert.assertEquals(217, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void useModule() {
    executeTestFile("use_module.xls");

    Assert.assertEquals(7, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void wait1() {
    executeTestFile("wait.xls");

    Assert.assertEquals(4, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  private void executeTestFile(String aTestFileName) {
    executeTestFile(new File(BASE_FOLDER + aTestFileName));
  }
}
