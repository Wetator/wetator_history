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


package org.wetator.backend.htmlunit.matcher;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.wetator.backend.WeightedControlList.FoundType;
import org.wetator.backend.htmlunit.matcher.AbstractHtmlUnitElementMatcher.MatchResult;
import org.wetator.backend.htmlunit.matcher.ByHtmlLabelMatcher.ByHtmlLabelMatchResult;
import org.wetator.backend.htmlunit.util.HtmlPageIndex;
import org.wetator.core.searchpattern.SearchPattern;
import org.wetator.exception.InvalidInputException;
import org.wetator.util.FindSpot;
import org.wetator.util.SecretString;

import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

/**
 * @author frank.danek
 */
public class ByHtmlLabelMatcherTest extends AbstractMatcherTest {

  private boolean matchInvisible;

  @Test
  public void not() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p>Some text .... </p>"
        + "<label id='myLabelId' for='myId'>myLabel</label>"
        + "<input id='myId' type='text'>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("not");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myLabelId");

    Assert.assertEquals(0, tmpMatches.size());
  }

  @Test
  public void full() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p>Some text .... </p>"
        + "<label id='myLabelId' for='myId'>myLabel</label>"
        + "<input id='myId' type='text'>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("myLabel");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myLabelId");

    Assert.assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_LABEL_ELEMENT, 0, 14, 14, tmpMatches.get(0));
    assertLabelEquals("myLabelId", tmpMatches.get(0));
  }

  @Test
  public void wildcardRight() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p>Some text .... </p>"
        + "<label id='myLabelId' for='myId'>myLabel</label>"
        + "<input id='myId' type='text'>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("myLab*");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myLabelId");

    Assert.assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_LABEL_ELEMENT, 0, 14, 14, tmpMatches.get(0));
    assertLabelEquals("myLabelId", tmpMatches.get(0));
  }

  @Test
  public void wildcardLeft() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p>Some text .... </p>"
        + "<label id='myLabelId' for='myId'>myLabel</label>"
        + "<input id='myId' type='text'>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("*Label");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myLabelId");

    Assert.assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_LABEL_ELEMENT, 0, 14, 14, tmpMatches.get(0));
    assertLabelEquals("myLabelId", tmpMatches.get(0));
  }

  @Test
  public void part() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p>Some text .... </p>"
        + "<label id='myLabelId' for='myId'>myLabel</label>"
        + "<input id='myId' type='text'>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("yLabe");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myLabelId");

    Assert.assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_LABEL_ELEMENT, 2, 14, 14, tmpMatches.get(0));
    assertLabelEquals("myLabelId", tmpMatches.get(0));
  }

  @Test
  public void empty_TextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<label id='otherLabelId' for='otherId'>myLabel</label>"
        + "<input id='otherId' type='text'>"
        + "<p>Some text .... </p>"
        + "<label id='myLabelId' for='myId'>myLabel</label>"
        + "<input id='myId' type='text'>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("Some text > ");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myLabelId", "otherLabelId");

    Assert.assertEquals(0, tmpMatches.size());
  }

  @Test
  public void full_TextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<label id='otherLabelId' for='otherId'>myLabel</label>"
        + "<input id='otherId' type='text'>"
        + "<p>Some text .... </p>"
        + "<label id='myLabelId' for='myId'>myLabel</label>"
        + "<input id='myId' type='text'>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("Some text > myLabel");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myLabelId", "otherLabelId");

    Assert.assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_LABEL_ELEMENT, 0, 5, 22, tmpMatches.get(0));
    assertLabelEquals("myLabelId", tmpMatches.get(0));
  }

  @Test
  public void wildcardRight_TextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<label id='otherLabelId' for='otherId'>myLabel</label>"
        + "<input id='otherId' type='text'>"
        + "<p>Some text .... </p>"
        + "<label id='myLabelId' for='myId'>myLabel</label>"
        + "<input id='myId' type='text'>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("Some text > myLab*");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myLabelId", "otherLabelId");

    Assert.assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_LABEL_ELEMENT, 0, 5, 22, tmpMatches.get(0));
    assertLabelEquals("myLabelId", tmpMatches.get(0));
  }

  @Test
  public void wildcardLeft_TextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<label id='otherLabelId' for='otherId'>myLabel</label>"
        + "<input id='otherId' type='text'>"
        + "<p>Some text .... </p>"
        + "<label id='myLabelId' for='myId'>myLabel</label>"
        + "<input id='myId' type='text'>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("Some text > *Label");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myLabelId", "otherLabelId");

    Assert.assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_LABEL_ELEMENT, 0, 5, 22, tmpMatches.get(0));
    assertLabelEquals("myLabelId", tmpMatches.get(0));
  }

  @Test
  public void part_TextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<label id='otherLabelId' for='otherId'>myLabel</label>"
        + "<input id='otherId' type='text'>"
        + "<p>Some text .... </p>"
        + "<label id='myLabelId' for='myId'>myLabel</label>"
        + "<input id='myId' type='text'>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("Some text > yLabe");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myLabelId", "otherLabelId");

    Assert.assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_LABEL_ELEMENT, 2, 5, 22, tmpMatches.get(0));
    assertLabelEquals("myLabelId", tmpMatches.get(0));
  }

  @Test
  public void full_WrongTextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<label id='otherLabelId' for='otherId'>myLabel</label>"
        + "<input id='otherId' type='text'>"
        + "<p>Some text .... </p>"
        + "<label id='myLabelId' for='myId'>myLabel</label>"
        + "<input id='myId' type='text'>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("wrong text > myLabel");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myLabelId", "otherLabelId");

    Assert.assertEquals(0, tmpMatches.size());
  }

  @Test
  public void full_NoTextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<label id='myLabelId' for='myId'>myLabel</label>"
        + "<input id='myId' type='text'>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("wrong text > myLabel");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myLabelId");

    Assert.assertEquals(0, tmpMatches.size());
  }

  @Test
  public void matchInvisibleNot() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p>Some text .... </p>"
        + "<label id='myLabelId' for='myId'>myLabel</label>"
        + "<input id='myId' type='text' style='display: none;'>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("myLabel");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myLabelId");

    Assert.assertEquals(0, tmpMatches.size());
  }

  @Test
  public void matchInvisible() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p>Some text .... </p>"
        + "<label id='myLabelId' for='myId'>myLabel</label>"
        + "<input id='myId' type='text' style='display: none;'>"
        + "</body></html>";
    // @formatter:on

    matchInvisible = true;

    final SecretString tmpSearch = new SecretString("myLabel");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myLabelId");

    Assert.assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_LABEL_ELEMENT, 0, 14, 14, tmpMatches.get(0));
    assertLabelEquals("myLabelId", tmpMatches.get(0));
  }

  @Test
  public void childNot() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<label id='myLabelId'>myLabel"
        + "<input id='myId' type='text'>"
        + "</label>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("not");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myLabelId");

    Assert.assertEquals(0, tmpMatches.size());
  }

  @Test
  public void childFull() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<label id='myLabelId'>myLabel"
        + "<input id='myId' type='text'>"
        + "</label>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("myLabel");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myLabelId");

    Assert.assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_LABEL_ELEMENT, 0, 0, 0, tmpMatches.get(0));
    assertLabelEquals("myLabelId", tmpMatches.get(0));
  }

  @Test
  public void childWildcardRight() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<label id='myLabelId'>myLabel"
        + "<input id='myId' type='text'>"
        + "</label>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("myLab*");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myLabelId");

    Assert.assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_LABEL_ELEMENT, 0, 0, 0, tmpMatches.get(0));
    assertLabelEquals("myLabelId", tmpMatches.get(0));
  }

  @Test
  public void childWildcardLeft() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<label id='myLabelId'>myLabel"
        + "<input id='myId' type='text'>"
        + "</label>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("*Label");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myLabelId");

    Assert.assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_LABEL_ELEMENT, 0, 0, 0, tmpMatches.get(0));
    assertLabelEquals("myLabelId", tmpMatches.get(0));
  }

  @Test
  public void childPart() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<label id='myLabelId'>myLabel"
        + "<input id='myId' type='text'>"
        + "</label>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("yLabe");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myLabelId");

    Assert.assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_LABEL_ELEMENT, 2, 0, 0, tmpMatches.get(0));
    assertLabelEquals("myLabelId", tmpMatches.get(0));
  }

  @Test
  public void childEmpty_TextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<label id='otherLabelId'>myLabel"
        + "<input id='otherId' type='text'>"
        + "</label>"
        + "<p>Some text .... </p>"
        + "<label id='myLabelId'>myLabel"
        + "<input id='myId' type='text'>"
        + "</label>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("Some text > ");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myLabelId", "otherLabelId");

    Assert.assertEquals(0, tmpMatches.size());
  }

  @Test
  public void childFull_TextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<label id='otherLabelId'>myLabel"
        + "<input id='otherId' type='text'>"
        + "</label>"
        + "<p>Some text .... </p>"
        + "<label id='myLabelId'>myLabel"
        + "<input id='myId' type='text'>"
        + "</label>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("Some text > myLabel");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myLabelId", "otherLabelId");

    Assert.assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_LABEL_ELEMENT, 0, 5, 22, tmpMatches.get(0));
    assertLabelEquals("myLabelId", tmpMatches.get(0));
  }

  @Test
  public void childWildcardRight_TextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<label id='otherLabelId'>myLabel"
        + "<input id='otherId' type='text'>"
        + "</label>"
        + "<p>Some text .... </p>"
        + "<label id='myLabelId'>myLabel"
        + "<input id='myId' type='text'>"
        + "</label>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("Some text > myLab*");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myLabelId", "otherLabelId");

    Assert.assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_LABEL_ELEMENT, 0, 5, 22, tmpMatches.get(0));
    assertLabelEquals("myLabelId", tmpMatches.get(0));
  }

  @Test
  public void childWildcardLeft_TextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<label id='otherLabelId'>myLabel"
        + "<input id='otherId' type='text'>"
        + "</label>"
        + "<p>Some text .... </p>"
        + "<label id='myLabelId'>myLabel"
        + "<input id='myId' type='text'>"
        + "</label>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("Some text > *Label");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myLabelId", "otherLabelId");

    Assert.assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_LABEL_ELEMENT, 0, 5, 22, tmpMatches.get(0));
    assertLabelEquals("myLabelId", tmpMatches.get(0));
  }

  @Test
  public void childPart_TextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<label id='otherLabelId'>myLabel"
        + "<input id='otherId' type='text'>"
        + "</label>"
        + "<p>Some text .... </p>"
        + "<label id='myLabelId'>myLabel"
        + "<input id='myId' type='text'>"
        + "</label>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("Some text > yLabe");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myLabelId", "otherLabelId");

    Assert.assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_LABEL_ELEMENT, 2, 5, 22, tmpMatches.get(0));
    assertLabelEquals("myLabelId", tmpMatches.get(0));
  }

  @Test
  public void childFull_WrongTextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<label id='otherLabelId'>myLabel"
        + "<input id='otherId' type='text'>"
        + "</label>"
        + "<p>Some text .... </p>"
        + "<label id='myLabelId'>myLabel"
        + "<input id='myId' type='text'>"
        + "</label>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("wrong text > myLabel");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myLabelId", "otherLabelId");

    Assert.assertEquals(0, tmpMatches.size());
  }

  @Test
  public void childFull_NoTextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<label id='otherLabelId'>myLabel"
        + "<input id='otherId' type='text'>"
        + "</label>"
        + "<p>Some text .... </p>"
        + "<label id='myLabelId'>myLabel"
        + "<input id='myId' type='text'>"
        + "</label>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("wrong text > myLabel");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myLabelId", "otherLabelId");

    Assert.assertEquals(0, tmpMatches.size());
  }

  @Test
  public void childMatchInvisibleNot() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<label id='myLabelId'>myLabel"
        + "<input id='myId' type='text' style='display: none;'>"
        + "</label>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("myLabel");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myLabelId");

    Assert.assertEquals(0, tmpMatches.size());
  }

  @Test
  public void childMatchInvisible() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<label id='myLabelId'>myLabel"
        + "<input id='myId' type='text' style='display: none;'>"
        + "</label>"
        + "</body></html>";
    // @formatter:on

    matchInvisible = true;

    final SecretString tmpSearch = new SecretString("myLabel");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myLabelId");

    Assert.assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_LABEL_ELEMENT, 0, 0, 0, tmpMatches.get(0));
    assertLabelEquals("myLabelId", tmpMatches.get(0));
  }

  @Override
  protected AbstractHtmlUnitElementMatcher createMatcher(final HtmlPageIndex aHtmlPageIndex,
      final SearchPattern aPathSearchPattern, final FindSpot aPathSpot, final SearchPattern aSearchPattern) {
    return new ByHtmlLabelMatcher(aHtmlPageIndex, aPathSearchPattern, aPathSpot, aSearchPattern, HtmlTextInput.class,
        matchInvisible);
  }

  private static void assertLabelEquals(final String anExpectedId, final MatchResult anActualMatch) {
    assertTrue(anActualMatch instanceof ByHtmlLabelMatchResult);
    assertEquals(anExpectedId, ((ByHtmlLabelMatchResult) anActualMatch).getLabel().getId());
  }
}
