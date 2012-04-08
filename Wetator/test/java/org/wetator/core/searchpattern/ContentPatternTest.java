/*
 * Copyright (c) 2008-2011 wetator.org
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


package org.wetator.core.searchpattern;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.wetator.exception.AssertionFailedException;
import org.wetator.util.SecretString;

/**
 * @author rbri
 */
public class ContentPatternTest {

  @Test
  public void matches() throws AssertionFailedException {
    List<SecretString> tmpExpected = new LinkedList<SecretString>();
    tmpExpected.add(new SecretString("a", "a"));
    tmpExpected.add(new SecretString("b", "b"));
    tmpExpected.add(new SecretString("c", "c"));
    ContentPattern tmpPattern = new ContentPattern(tmpExpected);

    tmpPattern.matches("a b c");
    tmpPattern.matches("x a b c y");

    tmpPattern.matches("a bb c");

    tmpPattern.matches("a c b c");
    tmpPattern.matches("b c a b c");
  }

  @Test
  public void matches_Dots() throws AssertionFailedException {
    List<SecretString> tmpExpected = new LinkedList<SecretString>();
    tmpExpected.add(new SecretString("def", "def"));
    tmpExpected.add(new SecretString("...", "..."));
    ContentPattern tmpPattern = new ContentPattern(tmpExpected);

    tmpPattern.matches(" abc def ghi ... xyz");
  }

  @Test
  public void matches_WrongOrder() {
    List<SecretString> tmpExpected = new LinkedList<SecretString>();
    tmpExpected.add(new SecretString("a", "a"));
    tmpExpected.add(new SecretString("b", "b"));
    tmpExpected.add(new SecretString("c", "c"));
    tmpExpected.add(new SecretString("d", "d"));
    ContentPattern tmpPattern = new ContentPattern(tmpExpected);

    try {
      tmpPattern.matches("a c b d");
      org.junit.Assert.fail("AssertionFailedException expected");
    } catch (AssertionFailedException e) {
      org.junit.Assert.assertEquals(
          "Expected content(s) {not found} or [in wrong order]: 'a, b, [c], d' (content: 'a c b d').", e.getMessage());
    }
  }

  @Test
  public void match_NotFound() {
    List<SecretString> tmpExpected = new LinkedList<SecretString>();
    tmpExpected.add(new SecretString("a", "a"));
    tmpExpected.add(new SecretString("b", "b"));
    tmpExpected.add(new SecretString("c", "c"));
    ContentPattern tmpPattern = new ContentPattern(tmpExpected);

    try {
      tmpPattern.matches("a b");
      org.junit.Assert.fail("AssertionFailedException expected");
    } catch (AssertionFailedException e) {
      org.junit.Assert.assertEquals(
          "Expected content(s) {not found} or [in wrong order]: 'a, b, {c}' (content: 'a b').", e.getMessage());
    }
  }

  @Test
  public void match_WrongOrderNotFound() {
    List<SecretString> tmpExpected = new LinkedList<SecretString>();
    tmpExpected.add(new SecretString("a", "a"));
    tmpExpected.add(new SecretString("b", "b"));
    tmpExpected.add(new SecretString("c", "c"));
    tmpExpected.add(new SecretString("d", "d"));
    ContentPattern tmpPattern = new ContentPattern(tmpExpected);

    try {
      tmpPattern.matches("c a d");
      org.junit.Assert.fail("AssertionFailedException expected");
    } catch (AssertionFailedException e) {
      org.junit.Assert.assertEquals(
          "Expected content(s) {not found} or [in wrong order]: 'a, {b}, [c], d' (content: 'c a d').", e.getMessage());
    }

    try {
      tmpPattern.matches("a c d");
      org.junit.Assert.fail("AssertionFailedException expected");
    } catch (AssertionFailedException e) {
      org.junit.Assert.assertEquals(
          "Expected content(s) {not found} or [in wrong order]: 'a, {b}, c, d' (content: 'a c d').", e.getMessage());
    }
  }

  // @Test
  // public void testAssertListMatch_Negated() throws AssertionFailedException {
  // List<SecretString> tmpExpected = new LinkedList<SecretString>();
  // tmpExpected.add(new SecretString("a", "a"));
  // tmpExpected.add(new SecretString("~b", "~b"));
  //
  // Assert.assertListMatch(tmpExpected, "a");
  // Assert.assertListMatch(tmpExpected, "a c");
  // }
  //
  // @Test
  // public void testAssertListMatch_Negated2() throws AssertionFailedException {
  // List<SecretString> tmpExpected = new LinkedList<SecretString>();
  // tmpExpected.add(new SecretString("~a", "~a"));
  // tmpExpected.add(new SecretString("b", "b"));
  //
  // Assert.assertListMatch(tmpExpected, "b");
  // Assert.assertListMatch(tmpExpected, "c b");
  // }
  //
  // @Test
  // public void testAssertListMatch_Negated3() throws AssertionFailedException {
  // List<SecretString> tmpExpected = new LinkedList<SecretString>();
  // tmpExpected.add(new SecretString("a", "a"));
  // tmpExpected.add(new SecretString("~b", "~b"));
  // tmpExpected.add(new SecretString("c", "c"));
  //
  // Assert.assertListMatch(tmpExpected, "a c");
  // Assert.assertListMatch(tmpExpected, "a c b");
  // Assert.assertListMatch(tmpExpected, "b a c");
  // }
  //
  // @Test
  // public void testAssertListMatch_NegatedFailes() {
  // List<SecretString> tmpExpected = new LinkedList<SecretString>();
  // tmpExpected.add(new SecretString("a", "a"));
  // tmpExpected.add(new SecretString("~b", "~b"));
  //
  // try {
  // Assert.assertListMatch(tmpExpected, "a c b");
  // org.junit.Assert.fail("AssertionFailedException expected");
  // } catch (AssertionFailedException e) {
  // org.junit.Assert.assertEquals(
  // "Expected content(s) {not found} or [in wrong order]: 'a, ~{b}' (content: 'a c b').", e.getMessage());
  // }
  //
  // try {
  // Assert.assertListMatch(tmpExpected, "c");
  // org.junit.Assert.fail("AssertionFailedException expected");
  // } catch (AssertionFailedException e) {
  // org.junit.Assert.assertEquals("Expected content(s) {not found} or [in wrong order]: '{a}, ~b' (content: 'c').",
  // e.getMessage());
  // }
  // }
  //
  // @Test
  // public void testAssertListMatch_NegatedFailes2() {
  // List<SecretString> tmpExpected = new LinkedList<SecretString>();
  // tmpExpected.add(new SecretString("~a", "~a"));
  // tmpExpected.add(new SecretString("b", "b"));
  //
  // try {
  // Assert.assertListMatch(tmpExpected, "a c b");
  // org.junit.Assert.fail("AssertionFailedException expected");
  // } catch (AssertionFailedException e) {
  // org.junit.Assert.assertEquals(
  // "Expected content(s) {not found} or [in wrong order]: '~{a}, b' (content: 'a c b').", e.getMessage());
  // }
  //
  // try {
  // Assert.assertListMatch(tmpExpected, "c");
  // org.junit.Assert.fail("AssertionFailedException expected");
  // } catch (AssertionFailedException e) {
  // org.junit.Assert.assertEquals("Expected content(s) {not found} or [in wrong order]: '~a, {b}' (content: 'c').",
  // e.getMessage());
  // }
  // }
  //
  // @Test
  // public void testAssertListMatch_NegatedFailes3() {
  // List<SecretString> tmpExpected = new LinkedList<SecretString>();
  // tmpExpected.add(new SecretString("a", "a"));
  // tmpExpected.add(new SecretString("~b", "~b"));
  // tmpExpected.add(new SecretString("c", "c"));
  //
  // try {
  // Assert.assertListMatch(tmpExpected, "a c b");
  // org.junit.Assert.fail("AssertionFailedException expected");
  // } catch (AssertionFailedException e) {
  // org.junit.Assert.assertEquals(
  // "Expected content(s) {not found} or [in wrong order]: '~{a}, b' (content: 'a c b').", e.getMessage());
  // }
  //
  // try {
  // Assert.assertListMatch(tmpExpected, "c");
  // org.junit.Assert.fail("AssertionFailedException expected");
  // } catch (AssertionFailedException e) {
  // org.junit.Assert.assertEquals("Expected content(s) {not found} or [in wrong order]: '~a, {b}' (content: 'c').",
  // e.getMessage());
  // }
  // }
}
