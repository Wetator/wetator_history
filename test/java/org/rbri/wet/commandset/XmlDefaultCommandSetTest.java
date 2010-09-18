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
import org.rbri.wet.test.AbstractWebServerTest;

/**
 * @author frank.danek
 */
public class XmlDefaultCommandSetTest extends AbstractWebServerTest {

  private static final String BASE_FOLDER = "test/xml/";

  @Test
  public void ajaxJquery() throws Exception {
    executeTestFile("ajax_jquery.xml");

    Assert.assertEquals(34, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void ajaxPrototype() throws Exception {
    executeTestFile("ajax_prototype.xml");

    Assert.assertEquals(10, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void anchorInsidePage() throws Exception {
    executeTestFile("anchor_inside_page.xml");

    Assert.assertEquals(14, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void assertContent() throws Exception {
    executeTestFile("assert_content.xml");

    Assert.assertEquals(41, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void assertDisabled() throws Exception {
    executeTestFile("assert_disabled.xml");

    Assert.assertEquals(51, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void assertSelectedCheckbox() throws Exception {
    executeTestFile("assert_selected_checkbox.xml");

    Assert.assertEquals(27, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void assertSelectedMultipleSelect() throws Exception {
    executeTestFile("assert_selected_multipleSelect.xml");

    Assert.assertEquals(35, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void assertSelectedRadio() throws Exception {
    executeTestFile("assert_selected_radio.xml");

    Assert.assertEquals(20, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void assertSelectedSingleSelect() throws Exception {
    executeTestFile("assert_selected_singleSelect.xml");

    Assert.assertEquals(29, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void assertSet() throws Exception {
    executeTestFile("assert_set.xml");

    Assert.assertEquals(81, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void assertTitle() throws Exception {
    executeTestFile("assert_title.xml");

    Assert.assertEquals(15, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void clickAfterText() throws Exception {
    executeTestFile("click_after_text.xml");

    Assert.assertEquals(280, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void clickOn() throws Exception {
    executeTestFile("click_on.xml");

    Assert.assertEquals(153, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void execJava() throws Exception {
    executeTestFile("exec_java.xml");

    Assert.assertEquals(23, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void flowSimpleLogin() {
    executeTestFile("flow_simple_login.xml");

    Assert.assertEquals(7, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void flowSimpleSearch() {
    executeTestFile("flow_simple_search.xml");

    Assert.assertEquals(12, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void goBack() {
    executeTestFile("go_back.xml");

    Assert.assertEquals(10, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void header() {
    executeTestFile("header.xml");

    Assert.assertEquals(3, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void htmlunitJavascript() {
    executeTestFile("htmlunit_javascript.xml");

    Assert.assertEquals(4, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void jquery() {
    executeTestFile("jquery.xml");

    Assert.assertEquals(9, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void jsError() {
    executeTestFile("js_error.xml");

    Assert.assertEquals(34, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void jsLibs() {
    executeTestFile("js_libs.xml");

    Assert.assertEquals(6, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void mouseOverAfter() {
    executeTestFile("mouse_over_after.xml");

    Assert.assertEquals(33, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void mouseOver() {
    executeTestFile("mouse_over.xml");

    Assert.assertEquals(18, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void normalizeCommand() {
    executeTestFile("normalize_command.xml");

    Assert.assertEquals(6, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void onfocusOnblur() {
    executeTestFile("onfocus_onblur.xml");

    Assert.assertEquals(56, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void onkey() {
    executeTestFile("onkey.xml");

    Assert.assertEquals(65, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void openUrl() throws Exception {
    executeTestFile("open_url.xml");

    Assert.assertEquals(18, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void redirect() {
    executeTestFile("redirect.xml");

    Assert.assertEquals(12, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void selectCheckboxAfter() {
    executeTestFile("select_checkbox_after.xml");

    Assert.assertEquals(50, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void selectCheckbox() {
    executeTestFile("select_checkbox.xml");

    Assert.assertEquals(53, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void selectMultipleSelect() {
    executeTestFile("select_multipleSelect.xml");

    Assert.assertEquals(73, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void selectRadio() {
    executeTestFile("select_radio.xml");

    Assert.assertEquals(40, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void selectSingleSelect() {
    executeTestFile("select_singleSelect.xml");

    Assert.assertEquals(68, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void setUpload() {
    executeTestFile("set_upload.xml");

    Assert.assertEquals(69, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void set() {
    executeTestFile("set.xml");

    Assert.assertEquals(214, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void useModule() {
    executeTestFile("use_module.xml");

    Assert.assertEquals(7, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void wait1() {
    executeTestFile("wait.xml");

    Assert.assertEquals(4, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  private void executeTestFile(String aTestFileName) {
    executeTestFile(new File(BASE_FOLDER + aTestFileName));
  }
}
