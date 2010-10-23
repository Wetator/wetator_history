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
public class SearchPatternLastOccurrenceInTest {

  @Test
  public void nullPattern() {
    String tmpMatcher = null;

    // match all
    SearchPattern tmpPattern = new SearchPattern((String) null);
    Assert.assertEquals(FindSpot.NOT_FOUND, tmpPattern.lastOccurenceIn(tmpMatcher));
    tmpPattern = new SearchPattern("");
    Assert.assertEquals(FindSpot.NOT_FOUND, tmpPattern.lastOccurenceIn(tmpMatcher));

    // static string
    tmpPattern = new SearchPattern("f");
    Assert.assertEquals(FindSpot.NOT_FOUND, tmpPattern.lastOccurenceIn(tmpMatcher));

    // regexp
    tmpPattern = new SearchPattern("f*x");
    Assert.assertEquals(FindSpot.NOT_FOUND, tmpPattern.lastOccurenceIn(tmpMatcher));
  }

  @Test
  public void empty() {
    String tmpMatcher = "";

    // match all
    SearchPattern tmpPattern = new SearchPattern((String) null);
    Assert.assertEquals(FindSpot.NOT_FOUND, tmpPattern.lastOccurenceIn(tmpMatcher));
    tmpPattern = new SearchPattern("");
    Assert.assertEquals(FindSpot.NOT_FOUND, tmpPattern.lastOccurenceIn(tmpMatcher));

    // static string
    tmpPattern = new SearchPattern("f");
    Assert.assertEquals(FindSpot.NOT_FOUND, tmpPattern.lastOccurenceIn(tmpMatcher));

    // regexp
    tmpPattern = new SearchPattern("f*x");
    Assert.assertEquals(FindSpot.NOT_FOUND, tmpPattern.lastOccurenceIn(tmpMatcher));
  }

  @Test
  public void oneChar() {
    String tmpMatcher = "X";

    // match all
    SearchPattern tmpPattern = new SearchPattern((String) null);
    Assert.assertEquals(FindSpot.NOT_FOUND, tmpPattern.lastOccurenceIn(tmpMatcher));
    tmpPattern = new SearchPattern("");
    Assert.assertEquals(FindSpot.NOT_FOUND, tmpPattern.lastOccurenceIn(tmpMatcher));

    // static string
    tmpPattern = new SearchPattern("f");
    Assert.assertEquals(null, tmpPattern.lastOccurenceIn(tmpMatcher));
    tmpPattern = new SearchPattern("X");
    Assert.assertEquals(new FindSpot(0, 1), tmpPattern.lastOccurenceIn(tmpMatcher));

    // regexp
    tmpPattern = new SearchPattern("f*x");
    Assert.assertEquals(null, tmpPattern.lastOccurenceIn(tmpMatcher));
    tmpPattern = new SearchPattern("X*");
    Assert.assertEquals(new FindSpot(0, 1), tmpPattern.lastOccurenceIn(tmpMatcher));
    tmpPattern = new SearchPattern("*X");
    Assert.assertEquals(new FindSpot(0, 1), tmpPattern.lastOccurenceIn(tmpMatcher));
    tmpPattern = new SearchPattern("*X*");
    Assert.assertEquals(new FindSpot(0, 1), tmpPattern.lastOccurenceIn(tmpMatcher));
  }

  @Test
  public void text() {
    String tmpMatcher = "Wetator";

    // match all
    SearchPattern tmpPattern = new SearchPattern((String) null);
    Assert.assertEquals(FindSpot.NOT_FOUND, tmpPattern.lastOccurenceIn(tmpMatcher));
    tmpPattern = new SearchPattern("");
    Assert.assertEquals(FindSpot.NOT_FOUND, tmpPattern.lastOccurenceIn(tmpMatcher));

    // static string
    tmpPattern = new SearchPattern("Wetator");
    Assert.assertEquals(new FindSpot(0, 7), tmpPattern.lastOccurenceIn(tmpMatcher));
    tmpPattern = new SearchPattern("X");
    Assert.assertEquals(null, tmpPattern.lastOccurenceIn(tmpMatcher));
    tmpPattern = new SearchPattern("wetator");
    Assert.assertEquals(null, tmpPattern.lastOccurenceIn(tmpMatcher));
    tmpPattern = new SearchPattern("tor");
    Assert.assertEquals(new FindSpot(4, 7), tmpPattern.lastOccurenceIn(tmpMatcher));
    tmpPattern = new SearchPattern("ato");
    Assert.assertEquals(new FindSpot(3, 6), tmpPattern.lastOccurenceIn(tmpMatcher));

    // regexp
    tmpPattern = new SearchPattern("f*x");
    Assert.assertEquals(null, tmpPattern.lastOccurenceIn(tmpMatcher));
    tmpPattern = new SearchPattern("*Wetator");
    Assert.assertEquals(new FindSpot(0, 7), tmpPattern.lastOccurenceIn(tmpMatcher));
    tmpPattern = new SearchPattern("Wetator*");
    Assert.assertEquals(new FindSpot(0, 7), tmpPattern.lastOccurenceIn(tmpMatcher));
    tmpPattern = new SearchPattern("*Wetator*");
    Assert.assertEquals(new FindSpot(0, 7), tmpPattern.lastOccurenceIn(tmpMatcher));
    tmpPattern = new SearchPattern("*We*or*");
    Assert.assertEquals(new FindSpot(0, 7), tmpPattern.lastOccurenceIn(tmpMatcher));
    tmpPattern = new SearchPattern("et*o");
    Assert.assertEquals(new FindSpot(1, 6), tmpPattern.lastOccurenceIn(tmpMatcher));
    tmpPattern = new SearchPattern("e*r");
    Assert.assertEquals(new FindSpot(1, 7), tmpPattern.lastOccurenceIn(tmpMatcher));
  }
}
