/*
 * Copyright (c) 2008-2020 wetator.org
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

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.wetator.backend.WeightedControlList;
import org.wetator.backend.WeightedControlList.Entry;
import org.wetator.exception.InvalidInputException;
import org.wetator.util.SecretString;

/**
 * @author rbri
 * @author frank.danek
 */
public class HtmlUnitInputRadioButtonIdentifierTest extends AbstractHtmlUnitControlIdentifierTest {

  @Before
  public void setupIdentifier() {
    identifier = new HtmlUnitInputRadioButtonIdentifier();
  }

  @Test
  public void byId() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input id='MyRadioButtonId1' name='MyRadioButtonName' value='value1' type='radio'>RadioButton1"
        + "<input id='MyRadioButtonId2' name='MyRadioButtonName' value='value2' type='radio'>RadioButton2"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("MyRadioButtonId2");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, tmpSearch, "MyRadioButtonId1",
        "MyRadioButtonId2");

    final List<Entry> tmpEntriesSorted = tmpFound.getEntriesSorted();
    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlRadioButtonInput 'value2' (id='MyRadioButtonId2') (name='MyRadioButtonName')] found by: BY_ID deviation: 0 distance: 12 start: 12 hierarchy: 0>1>3>4>7 index: 7",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void byLabelingTextAfter() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input id='MyRadioButtonId1' name='MyRadioButtonName' value='value1' type='radio'>RadioButton1"
        + "<input id='MyRadioButtonId2' name='MyRadioButtonName' value='value2' type='radio'>RadioButton2"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("RadioButton1");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, tmpSearch, "MyRadioButtonId1",
        "MyRadioButtonId2");

    final List<Entry> tmpEntriesSorted = tmpFound.getEntriesSorted();
    assertEquals(2, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlRadioButtonInput 'value1' (id='MyRadioButtonId1') (name='MyRadioButtonName')] found by: BY_LABELING_TEXT deviation: 0 distance: 0 start: 0 hierarchy: 0>1>3>4>5 index: 5",
        tmpEntriesSorted.get(0).toString());
    assertEquals(
        "[HtmlRadioButtonInput 'value2' (id='MyRadioButtonId2') (name='MyRadioButtonName')] found by: BY_TEXT deviation: 0 distance: 12 start: 12 hierarchy: 0>1>3>4>7 index: 7",
        tmpEntriesSorted.get(1).toString());
  }

  @Test
  public void byHtmlLabel_Text() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<label id='MyLabelId1' for='MyRadioButtonId1'>FirstLabelText</label>"
        + "<input id='MyRadioButtonId1' name='MyRadioButtonName' value='value1' type='radio'>RadioButton1"
        + "<label id='MyLabelId2' for='MyRadioButtonId2'>SecondLabelText</label>"
        + "<input id='MyRadioButtonId2' name='MyRadioButtonName' value='value2' type='radio'>RadioButton2"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("SecondLabelText");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, tmpSearch, "MyLabelId1",
        "MyLabelId2");

    final List<Entry> tmpEntriesSorted = tmpFound.getEntriesSorted();
    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlRadioButtonInput 'value2' (id='MyRadioButtonId2') (name='MyRadioButtonName')] found by: BY_LABEL_ELEMENT deviation: 0 distance: 27 start: 43 hierarchy: 0>1>3>4>11 index: 11",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void byHtmlLabel_Text_invisible() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<label id='MyLabelId1' for='MyRadioButtonId1'>FirstLabelText</label>"
        + "<input id='MyRadioButtonId1' name='MyRadioButtonName' value='value1' type='radio'>RadioButton1"
        + "<label id='MyLabelId2' for='MyRadioButtonId2'>SecondLabelText</label>"
        + "<input id='MyRadioButtonId2' name='MyRadioButtonName' value='value2' type='radio' style='display: none;'>RadioButton2"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("SecondLabelText");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, tmpSearch, "MyLabelId1",
        "MyLabelId2");

    final List<Entry> tmpEntriesSorted = tmpFound.getEntriesSorted();
    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlRadioButtonInput 'value2' (id='MyRadioButtonId2') (name='MyRadioButtonName')] by [HtmlLabel 'SecondLabelText' (id='MyLabelId2') (for='MyRadioButtonId2')] found by: BY_LABEL_ELEMENT deviation: 0 distance: 27 start: 43 hierarchy: 0>1>3>4>11 index: 11",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void byHtmlLabelChild_Text() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<label id='MyLabelId1'>FirstLabelText"
        + "<input id='MyRadioButtonId1' name='MyRadioButtonName' value='value1' type='radio'>RadioButton1"
        + "</label>"
        + "<label id='MyLabelId2'>SecondLabelText"
        + "<input id='MyRadioButtonId2' name='MyRadioButtonName' value='value2' type='radio'>RadioButton2"
        + "</label>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("SecondLabelText");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, tmpSearch, "MyLabelId1",
        "MyLabelId2");

    final List<Entry> tmpEntriesSorted = tmpFound.getEntriesSorted();
    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlRadioButtonInput 'value2' (id='MyRadioButtonId2') (name='MyRadioButtonName')] found by: BY_LABEL_ELEMENT deviation: 13 distance: 27 start: 43 hierarchy: 0>1>3>4>9>11 index: 11",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void byHtmlLabelChild_Text_invisible() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<label id='MyLabelId1'>FirstLabelText"
        + "<input id='MyRadioButtonId1' name='MyRadioButtonName' value='value1' type='radio'>RadioButton1"
        + "</label>"
        + "<label id='MyLabelId2'>SecondLabelText"
        + "<input id='MyRadioButtonId2' name='MyRadioButtonName' value='value2' type='radio' style='display: none;'>RadioButton2"
        + "</label>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("SecondLabelText");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, tmpSearch, "MyLabelId1",
        "MyLabelId2");

    final List<Entry> tmpEntriesSorted = tmpFound.getEntriesSorted();
    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlRadioButtonInput 'value2' (id='MyRadioButtonId2') (name='MyRadioButtonName')] by [HtmlLabel 'SecondLabelTextuncheckedRadioButton2' (id='MyLabelId2')] found by: BY_LABEL_ELEMENT deviation: 12 distance: 27 start: 43 hierarchy: 0>1>3>4>9>11 index: 11",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void byWholeTextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<p>Marker</p>"
        + "<input id='otherId' name='otherName' value='otherValue' type='submit'>"
        + "<p>Some text ...</p>"
        + "<input id='myId' name='myName' value='myValue' type='radio'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("Marker");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, tmpSearch, "myId");

    final List<Entry> tmpEntriesSorted = tmpFound.getEntriesSorted();
    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlRadioButtonInput 'myValue' (id='myId') (name='myName')] found by: BY_TEXT deviation: 25 distance: 31 start: 31 hierarchy: 0>1>3>4>10 index: 10",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void byWholeTextBefore_wildcardOnly() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<p>Marker</p>"
        + "<input id='myId' name='myName' value='myValue' type='radio'>"
        + "<p>Some text ...</p>"
        + "<input id='otherId' name='otherName' value='otherValue' type='radio'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("Marker > ");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, tmpSearch, "myId", "otherId");

    final List<Entry> tmpEntriesSorted = tmpFound.getEntriesSorted();
    assertEquals(2, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlRadioButtonInput 'myValue' (id='myId') (name='myName')] found by: BY_TEXT deviation: 0 distance: 0 start: 6 hierarchy: 0>1>3>4>7 index: 7",
        tmpEntriesSorted.get(0).toString());
    assertEquals(
        "[HtmlRadioButtonInput 'otherValue' (id='otherId') (name='otherName')] found by: BY_TEXT deviation: 0 distance: 14 start: 20 hierarchy: 0>1>3>4>10 index: 10",
        tmpEntriesSorted.get(1).toString());
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
        + "          <td id='cell_1_2'><input id='MyRadioButtonId_1_2' name='MyRadioButtonName_1_2' value='value_1_2' type='radio'></td>"
        + "          <td id='cell_1_3'><input id='MyRadioButtonId_1_3' name='MyRadioButtonName_1_3' value='value_1_3' type='radio'></td>"
        + "        </tr>"
        + "        <tr>"
        + "          <td id='cell_2_1'>row_2</td>"
        + "          <td id='cell_2_2'><input id='MyRadioButtonId_2_2' name='MyRadioButtonName_2_2' value='value_2_2' type='radio'></td>"
        + "          <td id='cell_2_3'><input id='MyRadioButtonId_2_3' name='MyRadioButtonName_2_3' value='value_2_3' type='radio'></td>"
        + "        </tr>"
        + "      </tbody>"
        + "    </table>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("[header_3; row_2]");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, tmpSearch, "MyRadioButtonId_1_2",
        "MyRadioButtonId_1_3", "MyRadioButtonId_2_2", "MyRadioButtonId_2_3");

    final List<Entry> tmpEntriesSorted = tmpFound.getEntriesSorted();
    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlRadioButtonInput 'value_2_3' (id='MyRadioButtonId_2_3') (name='MyRadioButtonName_2_3')] found by: BY_TABLE_COORDINATE deviation: 0 distance: 38 start: 38 hierarchy: 0>1>3>5>22>36>44>45 index: 45",
        tmpEntriesSorted.get(0).toString());
  }
}
