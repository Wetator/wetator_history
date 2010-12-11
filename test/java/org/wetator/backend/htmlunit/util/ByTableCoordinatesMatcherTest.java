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


package org.wetator.backend.htmlunit.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.wetator.backend.htmlunit.matcher.AbstractHtmlUnitElementMatcher.MatchResult;
import org.wetator.util.SecretString;

/**
 * @author frank.danek
 */
public class ByTableCoordinatesMatcherTest {

  @Test
  public void findInTablePlain() throws IOException {
    String tmpHtmlCode = "<html><body>" //
        + "    <table border='0' cellspacing='20' cellpadding='30'>" //
        + "      <thead>" //
        + "        <tr>" //
        + "          <th id=\"header1\">header1</th>" //
        + "          <th>header2</th>" //
        + "          <th>header3</th>" //
        + "        </tr>" //
        + "      </thead>" //
        + "      <tbody>" //
        + "        <tr>" //
        + "          <td>InputSubmit_1</td>" //
        + "          <td id='InputSubmit_1_2'>InputSubmit_l</td>" //
        + "          <td id='InputSubmit_1_3'>InputSubmit_l</td>" //
        + "        </tr>" //
        + "        <tr>" //
        + "          <td>InputSubmit_2</td>" //
        + "          <td id='InputSubmit_2_2'>InputSubmit_l</td>" //
        + "          <td id='InputSubmit_2_3'>InputSubmit_l</td>" //
        + "        </tr>" //
        + "      </tbody>" //
        + "    </table>" //
        + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("[header3; InputSubmit_2]", false));

    List<MatchResult> tmpRealMatches = new TableFinder2().findByTable(tmpHtmlCode, tmpSearch);

    Assert.assertEquals(1, tmpRealMatches.size());

    System.out.println(tmpRealMatches.get(0).getHtmlElement().asText());
    Assert.assertEquals("InputSubmit_2_3", tmpRealMatches.get(0).getHtmlElement().getId());
    System.out.println("coverage: " + tmpRealMatches.get(0).getCoverage() + " distance: "
        + tmpRealMatches.get(0).getDistance());
  }

  @Test
  public void findInTableNestedX() throws IOException {
    String tmpHtmlCode = "<html><body>" //
        + "    <table border='0' cellspacing='20' cellpadding='30'>" //
        + "      <thead>" //
        + "        <tr>" //
        + "          <th id=\"header1\">header1</th>" //
        + "          <th>header2</th>" //
        + "          <th><table><tr><td>header3</td></tr></table></th>" //
        + "        </tr>" //
        + "      </thead>" //
        + "      <tbody>" //
        + "        <tr>" //
        + "          <td>InputSubmit_1</td>" //
        + "          <td id='InputSubmit_1_2'>InputSubmit_l</td>" //
        + "          <td id='InputSubmit_1_3'>InputSubmit_l</td>" //
        + "        </tr>" //
        + "        <tr>" //
        + "          <td>InputSubmit_2</td>" //
        + "          <td id='InputSubmit_2_2'>InputSubmit_l</td>" //
        + "          <td id='InputSubmit_2_3'>InputSubmit_l</td>" //
        + "        </tr>" //
        + "      </tbody>" //
        + "    </table>" //
        + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("[header3; InputSubmit_2]", false));

    List<MatchResult> tmpRealMatches = new TableFinder2().findByTable(tmpHtmlCode, tmpSearch);

    Assert.assertEquals(1, tmpRealMatches.size());

    System.out.println(tmpRealMatches.get(0).getHtmlElement().asText());
    Assert.assertEquals("InputSubmit_2_3", tmpRealMatches.get(0).getHtmlElement().getId());
    System.out.println("coverage: " + tmpRealMatches.get(0).getCoverage() + " distance: "
        + tmpRealMatches.get(0).getDistance());
  }

  @Test
  public void findInTableNestedY() throws IOException {
    String tmpHtmlCode = "<html><body>" //
        + "    <table border='0' cellspacing='20' cellpadding='30'>" //
        + "      <thead>" //
        + "        <tr>" //
        + "          <th id=\"header1\">header1</th>" //
        + "          <th>header2</th>" //
        + "          <th>header3</th>" //
        + "        </tr>" //
        + "      </thead>" //
        + "      <tbody>" //
        + "        <tr>" //
        + "          <td>InputSubmit_1</td>" //
        + "          <td id='InputSubmit_1_2'>InputSubmit_l</td>" //
        + "          <td id='InputSubmit_1_3'>InputSubmit_l</td>" //
        + "        </tr>" //
        + "        <tr>" //
        + "          <td><table><tr><td>InputSubmit_2</td></tr></table></td>" //
        + "          <td id='InputSubmit_2_2'>InputSubmit_l</td>" //
        + "          <td id='InputSubmit_2_3'>InputSubmit_l</td>" //
        + "        </tr>" //
        + "      </tbody>" //
        + "    </table>" //
        + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("[header3; InputSubmit_2]", false));

    List<MatchResult> tmpRealMatches = new TableFinder2().findByTable(tmpHtmlCode, tmpSearch);

    Assert.assertEquals(1, tmpRealMatches.size());

    System.out.println(tmpRealMatches.get(0).getHtmlElement().asText());
    Assert.assertEquals("InputSubmit_2_3", tmpRealMatches.get(0).getHtmlElement().getId());
    System.out.println("coverage: " + tmpRealMatches.get(0).getCoverage() + " distance: "
        + tmpRealMatches.get(0).getDistance());
  }

  @Test
  public void findInTableNestedCell() throws IOException {
    String tmpHtmlCode = "<html><body>" //
        + "    <table border='0' cellspacing='20' cellpadding='30'>" //
        + "      <thead>" //
        + "        <tr>" //
        + "          <th id=\"header1\">header1</th>" //
        + "          <th>header2</th>" //
        + "          <th>header3</th>" //
        + "        </tr>" //
        + "      </thead>" //
        + "      <tbody>" //
        + "        <tr>" //
        + "          <td>InputSubmit_1</td>" //
        + "          <td id='InputSubmit_1_2'>InputSubmit_l</td>" //
        + "          <td id='InputSubmit_1_3'>InputSubmit_l</td>" //
        + "        </tr>" //
        + "        <tr>" //
        + "          <td>InputSubmit_2</td>" //
        + "          <td id='InputSubmit_2_2'>InputSubmit_l</td>" //
        + "          <td id='InputSubmit_2_3'><table><tr><td id='InputSubmit_2_3i'>InputSubmit_l</td></tr></table></td>" //
        + "        </tr>" //
        + "      </tbody>" //
        + "    </table>" //
        + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("[header3; InputSubmit_2]", false));

    List<MatchResult> tmpRealMatches = new TableFinder2().findByTable(tmpHtmlCode, tmpSearch);

    Assert.assertEquals(1, tmpRealMatches.size());

    System.out.println(tmpRealMatches.get(0).getHtmlElement().asText());
    Assert.assertEquals("InputSubmit_2_3i", tmpRealMatches.get(0).getHtmlElement().getId());
    System.out.println("coverage: " + tmpRealMatches.get(0).getCoverage() + " distance: "
        + tmpRealMatches.get(0).getDistance());
  }

  @Test
  public void findInTableNestedCellMultiple() throws IOException {
    String tmpHtmlCode = "<html><body>" //
        + "    <table border='0' cellspacing='20' cellpadding='30'>" //
        + "      <thead>" //
        + "        <tr>" //
        + "          <th id=\"header1\">header1</th>" //
        + "          <th>header2</th>" //
        + "          <th>header3</th>" //
        + "        </tr>" //
        + "      </thead>" //
        + "      <tbody>" //
        + "        <tr>" //
        + "          <td>InputSubmit_1</td>" //
        + "          <td id='InputSubmit_1_2'>InputSubmit_l</td>" //
        + "          <td id='InputSubmit_1_3'>InputSubmit_l</td>" //
        + "        </tr>" //
        + "        <tr>" //
        + "          <td>InputSubmit_2</td>" //
        + "          <td id='InputSubmit_2_2'>InputSubmit_l</td>" //
        + "          <td id='InputSubmit_2_3'><table><tr><td id='InputSubmit_2_3i1'>InputSubmit_l</td><td id='InputSubmit_2_3i2'>InputSubmit_l</td></tr></table></td>" //
        + "        </tr>" //
        + "      </tbody>" //
        + "    </table>" //
        + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("[header3; InputSubmit_2]", false));

    List<MatchResult> tmpRealMatches = new TableFinder2().findByTable(tmpHtmlCode, tmpSearch);

    Assert.assertEquals(2, tmpRealMatches.size());

    System.out.println(tmpRealMatches.get(0).getHtmlElement().asText());
    Assert.assertEquals("InputSubmit_2_3i1", tmpRealMatches.get(0).getHtmlElement().getId());
    System.out.println("coverage: " + tmpRealMatches.get(0).getCoverage() + " distance: "
        + tmpRealMatches.get(0).getDistance());

    System.out.println(tmpRealMatches.get(1).getHtmlElement().asText());
    Assert.assertEquals("InputSubmit_2_3i2", tmpRealMatches.get(1).getHtmlElement().getId());
    System.out.println("coverage: " + tmpRealMatches.get(1).getCoverage() + " distance: "
        + tmpRealMatches.get(1).getDistance());
  }

  @Test
  public void findInTableNestedTable() throws IOException {
    String tmpHtmlCode = "<html><body>" //
        + "    <table border='0' cellspacing='20' cellpadding='30'>" //
        + "      <tbody>" //
        + "        <tr>" //
        + "          <td>" //
        + "    <table border='0' cellspacing='20' cellpadding='30'>" //
        + "      <thead>" //
        + "        <tr>" //
        + "          <th id='header1'>header1</th>" //
        + "          <th id='header2'>header2</th>" //
        + "          <th id='header3'>header3</th>" //
        + "        </tr>" //
        + "      </thead>" //
        + "      <tbody>" //
        + "        <tr>" //
        + "          <td id='InputSubmit_1_1'>InputSubmit_1</td>" //
        + "          <td id='InputSubmit_1_2'>InputSubmit_l</td>" //
        + "          <td id='InputSubmit_1_3'>InputSubmit_l</td>" //
        + "        </tr>" //
        + "        <tr>" //
        + "          <td id='InputSubmit_2_1'>InputSubmit_2</td>" //
        + "          <td id='InputSubmit_2_2'>InputSubmit_l</td>" //
        + "          <td id='InputSubmit_2_3'>InputSubmit_l</td>" //
        + "        </tr>" //
        + "      </tbody>" //
        + "    </table>" //
        + "          </td>" //
        + "        </tr>" //
        + "      </tbody>" //
        + "    </table>" //
        + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("[header3; InputSubmit_2]", false));

    List<MatchResult> tmpRealMatches = new TableFinder2().findByTable(tmpHtmlCode, tmpSearch);

    Assert.assertEquals(1, tmpRealMatches.size());

    System.out.println(tmpRealMatches.get(0).getHtmlElement().asText());
    Assert.assertEquals("InputSubmit_2_3", tmpRealMatches.get(0).getHtmlElement().getId());
    System.out.println("coverage: " + tmpRealMatches.get(0).getCoverage() + " distance: "
        + tmpRealMatches.get(0).getDistance());
  }

  @Test
  public void findInTableDifferentTableX() throws IOException {
    String tmpHtmlCode = "<html><body>" //
        + "    <table border='0' cellspacing='20' cellpadding='30'>" //
        + "      <tbody>" //
        + "        <tr>" //
        + "          <td>InputSubmito_1_1</td>" //
        + "          <td>InputSubmito_1_2</td>" //
        + "        </tr>" //
        + "        <tr>" //
        + "          <td>InputSubmito_2_1</td>" //
        + "          <td>" //
        + "    <table border='0' cellspacing='20' cellpadding='30'>" //
        + "      <thead>" //
        + "        <tr>" //
        + "          <th id='header1'>header1</th>" //
        + "          <th id='header2'>header2</th>" //
        + "          <th id='header3'>header3</th>" //
        + "        </tr>" //
        + "      </thead>" //
        + "      <tbody>" //
        + "        <tr>" //
        + "          <td id='InputSubmit_1_1'>InputSubmit_1</td>" //
        + "          <td id='InputSubmit_1_2'>InputSubmit_l</td>" //
        + "          <td id='InputSubmit_1_3'>InputSubmit_l</td>" //
        + "        </tr>" //
        + "        <tr>" //
        + "          <td id='InputSubmit_2_1'>InputSubmit_2</td>" //
        + "          <td id='InputSubmit_2_2'>InputSubmit_l</td>" //
        + "          <td id='InputSubmit_2_3'>InputSubmit_l</td>" //
        + "        </tr>" //
        + "      </tbody>" //
        + "    </table>" //
        + "          </td>" //
        + "        </tr>" //
        + "      </tbody>" //
        + "    </table>" //
        + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("[InputSubmito_1_2; InputSubmit_2]", false));

    List<MatchResult> tmpRealMatches = new TableFinder2().findByTable(tmpHtmlCode, tmpSearch);

    Assert.assertEquals(3, tmpRealMatches.size());

    System.out.println(tmpRealMatches.get(0).getHtmlElement().asText());
    Assert.assertEquals("InputSubmit_2_1", tmpRealMatches.get(0).getHtmlElement().getId());
    System.out.println("coverage: " + tmpRealMatches.get(0).getCoverage() + " distance: "
        + tmpRealMatches.get(0).getDistance());

    System.out.println(tmpRealMatches.get(1).getHtmlElement().asText());
    Assert.assertEquals("InputSubmit_2_2", tmpRealMatches.get(1).getHtmlElement().getId());
    System.out.println("coverage: " + tmpRealMatches.get(1).getCoverage() + " distance: "
        + tmpRealMatches.get(1).getDistance());

    System.out.println(tmpRealMatches.get(2).getHtmlElement().asText());
    Assert.assertEquals("InputSubmit_2_3", tmpRealMatches.get(2).getHtmlElement().getId());
    System.out.println("coverage: " + tmpRealMatches.get(2).getCoverage() + " distance: "
        + tmpRealMatches.get(2).getDistance());
  }

  @Test
  public void findInTableDifferentTableY() throws IOException {
    String tmpHtmlCode = "<html><body>" //
        + "    <table border='0' cellspacing='20' cellpadding='30'>" //
        + "      <tbody>" //
        + "        <tr>" //
        + "          <td>InputSubmito_1_1</td>" //
        + "          <td>InputSubmito_1_2</td>" //
        + "        </tr>" //
        + "        <tr>" //
        + "          <td>InputSubmito_2_1</td>" //
        + "          <td>" //
        + "    <table border='0' cellspacing='20' cellpadding='30'>" //
        + "      <thead>" //
        + "        <tr>" //
        + "          <th id='header1'>header1</th>" //
        + "          <th id='header2'>header2</th>" //
        + "          <th id='header3'>header3</th>" //
        + "        </tr>" //
        + "      </thead>" //
        + "      <tbody>" //
        + "        <tr>" //
        + "          <td id='InputSubmit_1_1'>InputSubmit_1</td>" //
        + "          <td id='InputSubmit_1_2'>InputSubmit_l</td>" //
        + "          <td id='InputSubmit_1_3'>InputSubmit_l</td>" //
        + "        </tr>" //
        + "        <tr>" //
        + "          <td id='InputSubmit_2_1'>InputSubmit_2</td>" //
        + "          <td id='InputSubmit_2_2'>InputSubmit_l</td>" //
        + "          <td id='InputSubmit_2_3'>InputSubmit_l</td>" //
        + "        </tr>" //
        + "      </tbody>" //
        + "    </table>" //
        + "          </td>" //
        + "        </tr>" //
        + "      </tbody>" //
        + "    </table>" //
        + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("[header3; InputSubmito_2_1]", false));

    List<MatchResult> tmpRealMatches = new TableFinder2().findByTable(tmpHtmlCode, tmpSearch);

    Assert.assertEquals(3, tmpRealMatches.size());

    System.out.println(tmpRealMatches.get(0).getHtmlElement().asText());
    Assert.assertEquals("header3", tmpRealMatches.get(0).getHtmlElement().getId());
    System.out.println("coverage: " + tmpRealMatches.get(0).getCoverage() + " distance: "
        + tmpRealMatches.get(0).getDistance());

    System.out.println(tmpRealMatches.get(1).getHtmlElement().asText());
    Assert.assertEquals("InputSubmit_1_3", tmpRealMatches.get(1).getHtmlElement().getId());
    System.out.println("coverage: " + tmpRealMatches.get(1).getCoverage() + " distance: "
        + tmpRealMatches.get(1).getDistance());

    System.out.println(tmpRealMatches.get(2).getHtmlElement().asText());
    Assert.assertEquals("InputSubmit_2_3", tmpRealMatches.get(2).getHtmlElement().getId());
    System.out.println("coverage: " + tmpRealMatches.get(2).getCoverage() + " distance: "
        + tmpRealMatches.get(2).getDistance());
  }
}
