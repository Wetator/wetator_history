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

/**
 * @author rbri
 */
public class SearchPatternMatchesAtEndTest {

  @Test
  public void nullPattern() {
    String tmpMatcher = null;

    // match all
    SearchPattern tmpPattern = new SearchPattern((String) null);
    Assert.assertFalse(tmpPattern.matchesAtEnd(tmpMatcher));
    tmpPattern = new SearchPattern("");
    Assert.assertFalse(tmpPattern.matchesAtEnd(tmpMatcher));

    // static string
    tmpPattern = new SearchPattern("f");
    Assert.assertFalse(tmpPattern.matchesAtEnd(tmpMatcher));

    // regexp
    tmpPattern = new SearchPattern("f*x");
    Assert.assertFalse(tmpPattern.matchesAtEnd(tmpMatcher));
  }

  @Test
  public void empty() {
    String tmpMatcher = "";

    // match all
    SearchPattern tmpPattern = new SearchPattern((String) null);
    Assert.assertFalse(tmpPattern.matchesAtEnd(tmpMatcher));
    tmpPattern = new SearchPattern("");
    Assert.assertFalse(tmpPattern.matchesAtEnd(tmpMatcher));

    // static string
    tmpPattern = new SearchPattern("f");
    Assert.assertFalse(tmpPattern.matchesAtEnd(tmpMatcher));

    // regexp
    tmpPattern = new SearchPattern("f*x");
    Assert.assertFalse(tmpPattern.matchesAtEnd(tmpMatcher));
  }

  @Test
  public void oneChar() {
    String tmpMatcher = "X";

    // match all
    SearchPattern tmpPattern = new SearchPattern((String) null);
    Assert.assertFalse(tmpPattern.matchesAtEnd(tmpMatcher));
    tmpPattern = new SearchPattern("");
    Assert.assertFalse(tmpPattern.matchesAtEnd(tmpMatcher));

    // static string
    tmpPattern = new SearchPattern("f");
    Assert.assertFalse(tmpPattern.matchesAtEnd(tmpMatcher));
    tmpPattern = new SearchPattern("X");
    Assert.assertTrue(tmpPattern.matchesAtEnd(tmpMatcher));

    // regexp
    tmpPattern = new SearchPattern("f*x");
    Assert.assertFalse(tmpPattern.matchesAtEnd(tmpMatcher));
    tmpPattern = new SearchPattern("X*");
    Assert.assertTrue(tmpPattern.matchesAtEnd(tmpMatcher));
    tmpPattern = new SearchPattern("*X");
    Assert.assertTrue(tmpPattern.matchesAtEnd(tmpMatcher));
    tmpPattern = new SearchPattern("*X*");
    Assert.assertTrue(tmpPattern.matchesAtEnd(tmpMatcher));
  }

  @Test
  public void text() {
    String tmpMatcher = "Wetator";

    // match all
    SearchPattern tmpPattern = new SearchPattern((String) null);
    Assert.assertFalse(tmpPattern.matchesAtEnd(tmpMatcher));
    tmpPattern = new SearchPattern("");
    Assert.assertFalse(tmpPattern.matchesAtEnd(tmpMatcher));

    // static string
    tmpPattern = new SearchPattern("Wetator");
    Assert.assertTrue(tmpPattern.matchesAtEnd(tmpMatcher));
    tmpPattern = new SearchPattern("X");
    Assert.assertFalse(tmpPattern.matchesAtEnd(tmpMatcher));
    tmpPattern = new SearchPattern("wetator");
    Assert.assertFalse(tmpPattern.matchesAtEnd(tmpMatcher));
    tmpPattern = new SearchPattern("tor");
    Assert.assertTrue(tmpPattern.matchesAtEnd(tmpMatcher));
    tmpPattern = new SearchPattern("ato");
    Assert.assertFalse(tmpPattern.matchesAtEnd(tmpMatcher));

    // regexp
    tmpPattern = new SearchPattern("f*x");
    Assert.assertFalse(tmpPattern.matchesAtEnd(tmpMatcher));
    tmpPattern = new SearchPattern("*Wetator");
    Assert.assertTrue(tmpPattern.matchesAtEnd(tmpMatcher));
    tmpPattern = new SearchPattern("Wetator*");
    Assert.assertTrue(tmpPattern.matchesAtEnd(tmpMatcher));
    tmpPattern = new SearchPattern("*Wetator*");
    Assert.assertTrue(tmpPattern.matchesAtEnd(tmpMatcher));
    tmpPattern = new SearchPattern("*We*or*");
    Assert.assertTrue(tmpPattern.matchesAtEnd(tmpMatcher));
    tmpPattern = new SearchPattern("et*or");
    Assert.assertTrue(tmpPattern.matchesAtEnd(tmpMatcher));
    tmpPattern = new SearchPattern("e*r");
    Assert.assertTrue(tmpPattern.matchesAtEnd(tmpMatcher));

    tmpPattern = new SearchPattern("?etator");
    Assert.assertTrue(tmpPattern.matchesAtEnd(tmpMatcher));
    tmpPattern = new SearchPattern("?Wetator");
    Assert.assertFalse(tmpPattern.matchesAtEnd(tmpMatcher));
    tmpPattern = new SearchPattern("Wetato?");
    Assert.assertTrue(tmpPattern.matchesAtEnd(tmpMatcher));
    tmpPattern = new SearchPattern("Wetator?");
    Assert.assertFalse(tmpPattern.matchesAtEnd(tmpMatcher));
    tmpPattern = new SearchPattern("?etato?");
    Assert.assertTrue(tmpPattern.matchesAtEnd(tmpMatcher));
    tmpPattern = new SearchPattern("?e???o?");
    Assert.assertTrue(tmpPattern.matchesAtEnd(tmpMatcher));
    tmpPattern = new SearchPattern("et??or");
    Assert.assertTrue(tmpPattern.matchesAtEnd(tmpMatcher));
    tmpPattern = new SearchPattern("t?r");
    Assert.assertTrue(tmpPattern.matchesAtEnd(tmpMatcher));
  }
}
