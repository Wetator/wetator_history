/*
 * Copyright (c) 2008-2012 wetator.org
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.wetator.backend.WeightedControlList.FoundType;
import org.wetator.backend.htmlunit.matcher.AbstractHtmlUnitElementMatcher.MatchResult;
import org.wetator.backend.htmlunit.util.HtmlPageIndex;
import org.wetator.core.searchpattern.FindSpot;
import org.wetator.core.searchpattern.SearchPattern;
import org.wetator.exception.InvalidInputException;
import org.wetator.util.SecretString;

/**
 * @author frank.danek
 */
public class ByNameAttributeMatcherTest extends AbstractMatcherTest {

  @Test
  public void byNameNot() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input id='myId' name='myName' type='text'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("not", false));

    List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId");

    Assert.assertEquals(0, tmpMatches.size());
  }

  @Test
  public void byName() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input id='myId' name='myName' type='text'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("myName", false));

    List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId");

    Assert.assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_NAME, 0, 0, 0, tmpMatches.get(0));
  }

  @Test
  public void byNameWildcard() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input id='myId' name='myName' type='text'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("myNa*", false));

    List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId");

    Assert.assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_NAME, 0, 0, 0, tmpMatches.get(0));
  }

  @Test
  public void byNamePart() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input id='myId' name='myName' type='text'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("yNam", false));

    List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId");

    Assert.assertEquals(0, tmpMatches.size());
  }

  @Test
  public void byName_TextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<p>Some text .... </p>"
        + "<input id='myId' name='myName' type='text'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Some text", false));
    tmpSearch.add(new SecretString("myName", false));

    List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId");

    Assert.assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_NAME, 0, 5, 14, tmpMatches.get(0));
  }

  @Test
  public void byName_WrongTextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<p>Some text .... </p>"
        + "<input id='myId' name='myName' type='text'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("wrong text", false));
    tmpSearch.add(new SecretString("myName", false));

    List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId");

    Assert.assertEquals(0, tmpMatches.size());
  }

  @Test
  public void byName_NoTextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input id='myId' name='myName' type='text'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("wrong text", false));
    tmpSearch.add(new SecretString("myName", false));

    List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId");

    Assert.assertEquals(0, tmpMatches.size());
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.backend.htmlunit.matcher.AbstractMatcherTest#createMatcher(org.wetator.backend.htmlunit.util.HtmlPageIndex,
   *      org.wetator.core.searchpattern.SearchPattern, org.wetator.core.searchpattern.FindSpot,
   *      org.wetator.core.searchpattern.SearchPattern)
   */
  @Override
  protected AbstractHtmlUnitElementMatcher createMatcher(HtmlPageIndex aHtmlPageIndex,
      SearchPattern aPathSearchPattern, FindSpot aPathSpot, SearchPattern aSearchPattern) {
    return new ByNameAttributeMatcher(aHtmlPageIndex, aPathSearchPattern, aPathSpot, aSearchPattern);
  }
}
