/*
 * Copyright (c) 2008-2021 wetator.org
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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.wetator.backend.WPath;
import org.wetator.backend.WeightedControlList;
import org.wetator.exception.InvalidInputException;
import org.wetator.util.SecretString;

/**
 * @author rbri
 * @author frank.danek
 */
public class HtmlUnitInputTextIdentifierTest extends AbstractHtmlUnitControlIdentifierTest {

  @Before
  public void setupIdentifier() {
    identifier = new HtmlUnitInputTextIdentifier();
  }

  @Test
  public void byId() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input id='myId' name='myName' type='text'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("myId");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "myId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals(
        "[HtmlTextInput (id='myId') (name='myName')] found by: BY_ID deviation: 0 distance: 0 start: 0 index: 5",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byName() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input id='myId' name='myName' type='text'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("myName");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "myId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals(
        "[HtmlTextInput (id='myId') (name='myName')] found by: BY_NAME deviation: 0 distance: 0 start: 0 index: 5",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byIdName() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input id='TextInput' name='TextInput' type='text'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("TextInput");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "TextInput");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals(
        "[HtmlTextInput (id='TextInput') (name='TextInput')] found by: BY_ID deviation: 0 distance: 0 start: 0 index: 5",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byLabelingTextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input id='otherId' name='otherName' type='text'>"
        + "<p>Marker</p>"
        + "<input id='myId' name='myName' type='text'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("Marker");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "otherId", "myId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals(
        "[HtmlTextInput (id='myId') (name='myName')] found by: BY_LABELING_TEXT deviation: 0 distance: 0 start: 6 index: 8",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byPlaceholder() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input id='myId' name='myName' type='text' placeholder='myPlaceholder'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("myPlaceholder");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "myId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals(
        "[HtmlTextInput (id='myId') (name='myName')] found by: BY_PLACEHOLDER deviation: 0 distance: 0 start: 0 index: 5",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byHtmlLabel_Text() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<label id='labelId' for='myId'>Label</label>"
        + "<input id='myId' name='myName' type='text'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("Label");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "labelId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals(
        "[HtmlTextInput (id='myId') (name='myName')] found by: BY_LABEL_ELEMENT deviation: 0 distance: 0 start: 5 index: 7",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byHtmlLabel_Text_Invisible() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<label id='labelId' for='myId'>Label</label>"
        + "<input id='myId' name='myName' type='text' style='display: none;'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("Label");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "labelId");

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void byHtmlLabelChild_Text() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<label id='labelId'>Label"
        + "<input id='myId' name='myName' type='text'>"
        + "</label>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("Label");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "labelId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals(
        "[HtmlTextInput (id='myId') (name='myName')] found by: BY_LABEL_ELEMENT deviation: 0 distance: 0 start: 5 index: 7",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byHtmlLabelChild_Text_Invisible() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<label id='labelId'>Label"
        + "<input id='myId' name='myName' type='text' style='display: none;'>"
        + "</label>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("Label");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "labelId");

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void byWholeTextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<p>Marker</p>"
        + "<input id='otherId' name='otherName' type='submit'>"
        + "<p>Some text ...</p>"
        + "<input id='myId' name='myName' type='text'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("Marker");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "myId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals(
        "[HtmlTextInput (id='myId') (name='myName')] found by: BY_TEXT deviation: 14 distance: 20 start: 20 index: 10",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byWholeTextBefore_wildcardOnly() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<p>Marker</p>"
        + "<input id='myId' name='myName' type='text'>"
        + "<p>Some text ...</p>"
        + "<input id='otherId' name='otherName' type='text'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("Marker > ");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "myId", "otherId");

    Assert.assertEquals(2, tmpFound.getEntriesSorted().size());
    Assert.assertEquals(
        "[HtmlTextInput (id='myId') (name='myName')] found by: BY_TEXT deviation: 0 distance: 0 start: 6 index: 7",
        tmpFound.getEntriesSorted().get(0).toString());
    Assert.assertEquals(
        "[HtmlTextInput (id='otherId') (name='otherName')] found by: BY_TEXT deviation: 0 distance: 14 start: 20 index: 10",
        tmpFound.getEntriesSorted().get(1).toString());
  }

  @Test
  public void byTableCoordinates() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "    <table border='0' cellspacing='20' cellpadding='30'>"
        + "      <thead>"
        + "        <tr>"
        + "          <th id='header_1'>header_1</th>"
        + "          <th id='header_2'>header_2</th>"
        + "          <th id='header_3'>header_3</th>"
        + "        </tr>"
        + "      </thead>"
        + "      <tbody>"
        + "        <tr>"
        + "          <td id='cell_1_1'>row_1</td>"
        + "          <td id='cell_1_2'><input id='myId_1_2' type='text'></td>"
        + "          <td id='cell_1_3'><input id='myId_1_3' type='text'></td>"
        + "        </tr>"
        + "        <tr>"
        + "          <td id='cell_2_1'>row_2</td>"
        + "          <td id='cell_2_2'><input id='myId_2_2' type='text'></td>"
        + "          <td id='cell_2_3'><input id='myId_2_3' type='text'></td>"
        + "        </tr>"
        + "      </tbody>"
        + "    </table>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("[header_3; row_2]");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "myId_1_2", "myId_1_3",
        "myId_2_2", "myId_2_3");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals(
        "[HtmlTextInput (id='myId_2_3')] found by: BY_TABLE_COORDINATE deviation: 0 distance: 38 start: 38 index: 45",
        tmpFound.getEntriesSorted().get(0).toString());
  }
}