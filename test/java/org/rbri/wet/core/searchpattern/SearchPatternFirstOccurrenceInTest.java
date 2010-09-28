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


package org.rbri.wet.core.searchpattern;

import junit.framework.Assert;

import org.junit.Test;
import org.rbri.wet.backend.htmlunit.util.FindSpot;

/**
 * @author rbri
 */
public class SearchPatternFirstOccurrenceInTest {

  @Test
  public void test_Null() {
    String tmpMatcher = null;

    // match all
    SearchPattern tmpPattern = new SearchPattern((String) null);
    Assert.assertEquals(FindSpot.NOT_FOUND, tmpPattern.firstOccurenceIn(tmpMatcher));
    tmpPattern = new SearchPattern("");
    Assert.assertEquals(FindSpot.NOT_FOUND, tmpPattern.firstOccurenceIn(tmpMatcher));

    // static string
    tmpPattern = new SearchPattern("f");
    Assert.assertEquals(FindSpot.NOT_FOUND, tmpPattern.firstOccurenceIn(tmpMatcher));

    // regexp
    tmpPattern = new SearchPattern("f*x");
    Assert.assertEquals(FindSpot.NOT_FOUND, tmpPattern.firstOccurenceIn(tmpMatcher));
  }

  @Test
  public void test_Empty() {
    String tmpMatcher = "";

    // match all
    SearchPattern tmpPattern = new SearchPattern((String) null);
    Assert.assertEquals(FindSpot.NOT_FOUND, tmpPattern.firstOccurenceIn(tmpMatcher));
    tmpPattern = new SearchPattern("");
    Assert.assertEquals(FindSpot.NOT_FOUND, tmpPattern.firstOccurenceIn(tmpMatcher));

    // static string
    tmpPattern = new SearchPattern("f");
    Assert.assertEquals(FindSpot.NOT_FOUND, tmpPattern.firstOccurenceIn(tmpMatcher));

    // regexp
    tmpPattern = new SearchPattern("f*x");
    Assert.assertEquals(FindSpot.NOT_FOUND, tmpPattern.firstOccurenceIn(tmpMatcher));
  }

  @Test
  public void test_OneChar() {
    String tmpMatcher = "X";

    // match all
    SearchPattern tmpPattern = new SearchPattern((String) null);
    Assert.assertEquals(FindSpot.NOT_FOUND, tmpPattern.firstOccurenceIn(tmpMatcher));
    tmpPattern = new SearchPattern("");
    Assert.assertEquals(FindSpot.NOT_FOUND, tmpPattern.firstOccurenceIn(tmpMatcher));

    // static string
    tmpPattern = new SearchPattern("f");
    Assert.assertEquals(null, tmpPattern.firstOccurenceIn(tmpMatcher));
    tmpPattern = new SearchPattern("X");
    Assert.assertEquals(new FindSpot(0, 1), tmpPattern.firstOccurenceIn(tmpMatcher));

    // regexp
    tmpPattern = new SearchPattern("f*x");
    Assert.assertEquals(null, tmpPattern.firstOccurenceIn(tmpMatcher));
    tmpPattern = new SearchPattern("X*");
    Assert.assertEquals(new FindSpot(0, 1), tmpPattern.firstOccurenceIn(tmpMatcher));
    tmpPattern = new SearchPattern("*X");
    Assert.assertEquals(new FindSpot(0, 1), tmpPattern.firstOccurenceIn(tmpMatcher));
    tmpPattern = new SearchPattern("*X*");
    Assert.assertEquals(new FindSpot(0, 1), tmpPattern.firstOccurenceIn(tmpMatcher));
  }

  @Test
  public void test_Text() {
    String tmpMatcher = "Wetator";

    // match all
    SearchPattern tmpPattern = new SearchPattern((String) null);
    Assert.assertEquals(FindSpot.NOT_FOUND, tmpPattern.firstOccurenceIn(tmpMatcher));
    tmpPattern = new SearchPattern("");
    Assert.assertEquals(FindSpot.NOT_FOUND, tmpPattern.firstOccurenceIn(tmpMatcher));

    // static string
    tmpPattern = new SearchPattern("Wetator");
    Assert.assertEquals(new FindSpot(0, 7), tmpPattern.firstOccurenceIn(tmpMatcher));
    tmpPattern = new SearchPattern("X");
    Assert.assertEquals(null, tmpPattern.firstOccurenceIn(tmpMatcher));
    tmpPattern = new SearchPattern("wetator");
    Assert.assertEquals(null, tmpPattern.firstOccurenceIn(tmpMatcher));
    tmpPattern = new SearchPattern("Wet");
    Assert.assertEquals(new FindSpot(0, 3), tmpPattern.firstOccurenceIn(tmpMatcher));
    tmpPattern = new SearchPattern("eta");
    Assert.assertEquals(new FindSpot(1, 4), tmpPattern.firstOccurenceIn(tmpMatcher));

    // regexp
    tmpPattern = new SearchPattern("f*x");
    Assert.assertEquals(null, tmpPattern.firstOccurenceIn(tmpMatcher));
    tmpPattern = new SearchPattern("Wetator*");
    Assert.assertEquals(new FindSpot(0, 7), tmpPattern.firstOccurenceIn(tmpMatcher));
    tmpPattern = new SearchPattern("*Wetator");
    Assert.assertEquals(new FindSpot(0, 7), tmpPattern.firstOccurenceIn(tmpMatcher));
    tmpPattern = new SearchPattern("*Wetator*");
    Assert.assertEquals(new FindSpot(0, 7), tmpPattern.firstOccurenceIn(tmpMatcher));
    tmpPattern = new SearchPattern("*et*or*");
    Assert.assertEquals(new FindSpot(0, 7), tmpPattern.firstOccurenceIn(tmpMatcher));
    tmpPattern = new SearchPattern("et*o");
    Assert.assertEquals(new FindSpot(1, 6), tmpPattern.firstOccurenceIn(tmpMatcher));
    tmpPattern = new SearchPattern("W*t");
    Assert.assertEquals(new FindSpot(0, 3), tmpPattern.firstOccurenceIn(tmpMatcher));
  }
}
