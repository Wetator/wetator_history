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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.wetator.backend.WPath;
import org.wetator.backend.WeightedControlList.FoundType;
import org.wetator.backend.htmlunit.matcher.AbstractHtmlUnitElementMatcher.MatchResult;
import org.wetator.core.searchpattern.SearchPattern;
import org.wetator.util.SecretString;

import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableCell;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;

/**
 * @author frank.danek
 */
public class TableMatcherTest {

  @Test
  public void find() throws IOException {
    String tmpHtmlCode = "<html><body>" //
        + "    <table border='0' cellspacing='20' cellpadding='30'>" //
        + "      <thead>" //
        + "        <tr>" //
        + "          <th id=\"header1\">header1</th>" //
        + "          <th>header2</th>" //
        // + "          <th>header3</th>" //
        + "          <th><table><tr><td>header3</td></tr></table></th>" //
        + "        </tr>" //
        + "      </thead>" //
        + "      <tbody>" //
        + "        <tr>" //
        + "          <td>InputSubmit_1</td>" //
        + "          <td>InputSubmit_1_2</td>" //
        + "          <td>InputSubmit_1_3</td>" //
        // + "          <td>" //
        // + "            <form action=\"snoopy.php\">" //
        // + "              <input type=\"hidden\" name=\"clickOnTable\" value=\"cell_1_2\">" //
        // +
        // "              <input type=\"submit\" id=\"b_i_submit_id\" name=\"b_i_submit_name\" value=\"InputTypeSubmit\">"
        // //
        // + "            </form>" //
        // + "          </td>" //
        // + "          <td>" //
        // + "            <form action=\"snoopy.php\">" //
        // + "              <input type=\"hidden\" name=\"clickOnTable\" value=\"cell_1_3\">" //
        // +
        // "              <input type=\"submit\" id=\"b_i_submit_id\" name=\"b_i_submit_name\" value=\"InputTypeSubmit\">"
        // //
        // + "            </form>" //
        // + "          </td>" //
        + "        </tr>" //
        + "        <tr>" //
        // + "          <td>InputSubmit_2</td>" //
        + "          <td><table><tr><td>InputSubmit_2</td></tr></table></td>" //
        + "          <td>InputSubmit_2_2</td>" //
        + "          <td>InputSubmit_2_3</td>" //
        // + "          <td>" //
        // + "            <form action=\"snoopy.php\">" //
        // + "              <input type=\"hidden\" name=\"clickOnTable\" value=\"cell_2_2\">" //
        // +
        // "              <input type=\"submit\" id=\"b_i_submit_id\" name=\"b_i_submit_name\" value=\"InputTypeSubmit\">"
        // //
        // + "            </form>" //
        // + "          </td>" //
        // + "          <td>" //
        // + "            <form action=\"snoopy.php\">" //
        // + "              <input type=\"hidden\" name=\"clickOnTable\" value=\"cell_2_3\">" //
        // +
        // "              <input type=\"submit\" id=\"b_i_submit_id\" name=\"b_i_submit_name\" value=\"InputTypeSubmit\">"
        // //
        // + "            </form>" //
        // + "          </td>" //
        + "        </tr>" //
        + "      </tbody>" //
        + "    </table>" //
        + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("[header3; InputSubmit_2]", false));
    tmpSearch.add(new SecretString("InputSubmit_2_3", false));

    List<MatchResult> tmpMatches = findInTable(tmpHtmlCode, tmpSearch);

    Assert.assertEquals(1, tmpMatches.size());

    System.out.println(tmpMatches.get(0).getHtmlElement().asText());
    Assert.assertEquals("InputSubmit_2_3", tmpMatches.get(0).getHtmlElement().asText());
    System.out
        .println("coverage: " + tmpMatches.get(0).getCoverage() + " distance: " + tmpMatches.get(0).getDistance());
  }

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
        + "          <td>InputSubmit_1l</td>" //
        + "          <td>InputSubmit_1l</td>" //
        + "        </tr>" //
        + "        <tr>" //
        + "          <td>InputSubmit_2</td>" //
        + "          <td id='InputSubmit_2_2'>InputSubmit_2l</td>" //
        + "          <td id='InputSubmit_2_3'>InputSubmit_2l</td>" //
        + "        </tr>" //
        + "      </tbody>" //
        + "    </table>" //
        + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("[header3; InputSubmit_2]", false));
    tmpSearch.add(new SecretString("InputSubmit_2l", false));

    List<MatchResult> tmpRealMatches = findInTable(tmpHtmlCode, tmpSearch);

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
        + "          <td>InputSubmit_1l</td>" //
        + "          <td>InputSubmit_1l</td>" //
        + "        </tr>" //
        + "        <tr>" //
        + "          <td>InputSubmit_2</td>" //
        + "          <td id='InputSubmit_2_2'>InputSubmit_2l</td>" //
        + "          <td id='InputSubmit_2_3'>InputSubmit_2l</td>" //
        + "        </tr>" //
        + "      </tbody>" //
        + "    </table>" //
        + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("[header3; InputSubmit_2]", false));
    tmpSearch.add(new SecretString("InputSubmit_2l", false));

    List<MatchResult> tmpRealMatches = findInTable(tmpHtmlCode, tmpSearch);

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
        + "          <td>InputSubmit_1l</td>" //
        + "          <td>InputSubmit_1l</td>" //
        + "        </tr>" //
        + "        <tr>" //
        + "          <td><table><tr><td>InputSubmit_2</td></tr></table></td>" //
        + "          <td id='InputSubmit_2_2'>InputSubmit_2l</td>" //
        + "          <td id='InputSubmit_2_3'>InputSubmit_2l</td>" //
        + "        </tr>" //
        + "      </tbody>" //
        + "    </table>" //
        + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("[header3; InputSubmit_2]", false));
    tmpSearch.add(new SecretString("InputSubmit_2l", false));

    List<MatchResult> tmpRealMatches = findInTable(tmpHtmlCode, tmpSearch);

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
        + "          <td>InputSubmit_1l</td>" //
        + "          <td>InputSubmit_1l</td>" //
        + "        </tr>" //
        + "        <tr>" //
        + "          <td>InputSubmit_2</td>" //
        + "          <td id='InputSubmit_2_2'>InputSubmit_2l</td>" //
        + "          <td id='InputSubmit_2_3'><table><tr><td id='InputSubmit_2_3i'>InputSubmit_2l</td></tr></table></td>" //
        + "        </tr>" //
        + "      </tbody>" //
        + "    </table>" //
        + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("[header3; InputSubmit_2]", false));
    tmpSearch.add(new SecretString("InputSubmit_2l", false));

    List<MatchResult> tmpRealMatches = findInTable(tmpHtmlCode, tmpSearch);

    Assert.assertEquals(1, tmpRealMatches.size());

    System.out.println(tmpRealMatches.get(0).getHtmlElement().asText());
    Assert.assertEquals("InputSubmit_2_3i", tmpRealMatches.get(0).getHtmlElement().getId());
    System.out.println("coverage: " + tmpRealMatches.get(0).getCoverage() + " distance: "
        + tmpRealMatches.get(0).getDistance());
  }

  private List<MatchResult> findInTable(String aHtmlPage, List<SecretString> aSearch) throws IOException {
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(aHtmlPage);
    HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    WPath tmpPath = new WPath(aSearch);

    SearchPattern tmpPathSearchPattern = SearchPattern.createFromWPath(tmpPath, tmpPath.size() - 2);
    SecretString tmpTableString = tmpPath.getNode(tmpPath.size() - 2);

    System.out.println("coords: " + tmpTableString);

    String[] tmpCoords = tmpTableString.getValue().split(";");
    SecretString tmpCoordX = new SecretString(tmpCoords[0].substring(1).trim(), false);
    SecretString tmpCoordY = new SecretString(tmpCoords[1].substring(0, tmpCoords[1].length() - 1).trim(), false);

    System.out.println("coordX: " + tmpCoordX);
    System.out.println("coordY: " + tmpCoordY);

    SearchPattern tmpSearchPatternCoordX = tmpCoordX.getSearchPattern();
    SearchPattern tmpSearchPatternCoordY = tmpCoordY.getSearchPattern();

    SearchPattern tmpSearchPattern = tmpPath.getNode(tmpPath.size() - 1).getSearchPattern();
    FindSpot tmpPathSpot = tmpHtmlPageIndex.firstOccurence(tmpPathSearchPattern);

    List<MatchResult> tmpMatches = new ArrayList<MatchResult>();

    Map<HtmlTableCell, HtmlTableCell> tmpAlreadyProcessed = new HashMap<HtmlTableCell, HtmlTableCell>();

    // search for the right column
    for (HtmlElement tmpHtmlElementX : tmpHtmlPageIndex.getAllVisibleHtmlElements()) {
      FindSpot tmpNodeSpotX = tmpHtmlPageIndex.getPosition(tmpHtmlElementX);
      if (null != tmpPathSpot && tmpPathSpot.endPos <= tmpNodeSpotX.startPos) {
        // the element has the path before

        String tmpValueX = tmpHtmlPageIndex.getAsText(tmpHtmlElementX);
        if (StringUtils.isNotEmpty(tmpValueX)) {

          if (tmpSearchPatternCoordX.matches(tmpValueX)) {
            // the element has the text
            HtmlTableCell tmpCellX = findEnclosingCell(tmpHtmlElementX);
            if (tmpCellX == null) {
              // the element is no cell and has no cell as parent
              continue;
            }
            // now we have a candidate for the x coordinate

            // search for the right row
            for (HtmlElement tmpHtmlElementY : tmpHtmlPageIndex.getAllVisibleHtmlElements()) {
              FindSpot tmpNodeSpotY = tmpHtmlPageIndex.getPosition(tmpHtmlElementY);
              if (null != tmpNodeSpotY && tmpNodeSpotX.endPos <= tmpNodeSpotY.startPos) {
                // the element has the x element before

                String tmpValueY = tmpHtmlPageIndex.getAsText(tmpHtmlElementY);
                if (StringUtils.isNotEmpty(tmpValueY)) {

                  if (tmpSearchPatternCoordY.matches(tmpValueY)) {
                    // the element has the text
                    HtmlTableCell tmpCellY = findEnclosingCell(tmpHtmlElementY);
                    if (tmpCellY == null) {
                      // the element is no cell and has no cell as parent
                      continue;
                    }
                    // now we have a candidate for the y coordinate

                    // search for the innermost common table
                    HtmlTable tmpTable = null;
                    HtmlTable tmpTableX = tmpCellX.getEnclosingRow().getEnclosingTable();
                    HtmlTable tmpTableY = tmpCellY.getEnclosingRow().getEnclosingTable();
                    HtmlTableCell tmpCellX2 = tmpCellX;
                    HtmlTableCell tmpCellY2 = tmpCellY;
                    if (tmpTableX == tmpTableY) {
                      tmpTable = tmpTableX;
                    } else {
                      do {
                        tmpCellY2 = tmpCellY;
                        do {
                          tmpCellY2 = findEnclosingCell(tmpTableY);
                          if (tmpCellY2 == null) {
                            continue;
                          }
                          tmpTableY = findEnclosingTable(tmpCellY2);
                        } while (tmpCellY2 != null && tmpTableY != null && tmpTableX != tmpTableY);
                        if (tmpTableX == tmpTableY) {
                          tmpTable = tmpTableX;
                          break;
                        }
                        tmpCellX2 = findEnclosingCell(tmpTableX);
                        if (tmpCellX2 == null) {
                          break;
                        }
                        tmpTableX = findEnclosingTable(tmpCellX2);
                      } while (tmpTableX != null && tmpTableX != tmpTableY);
                    }

                    if (tmpTable == null) {
                      // the two cells do not have a common table
                      continue;
                    }
                    // now we have a common table and both coordinates

                    if (tmpAlreadyProcessed.get(tmpCellX2) == tmpCellY2) {
                      // we have already found this match
                      continue;
                    }

                    int tmpX = findCellInRow(tmpCellX2.getEnclosingRow(), tmpCellX2);
                    System.out.println("found coordX: column " + tmpX + " span " + tmpCellX2.getColumnSpan());

                    int tmpY = findRowInTable(tmpCellY2.getEnclosingRow().getEnclosingTable(),
                        tmpCellY2.getEnclosingRow());
                    System.out.println("found coordY: row " + tmpY + " span " + tmpCellY2.getRowSpan());

                    HtmlElement tmpHtmlElementCell = tmpCellY2.getEnclosingRow().getEnclosingTable()
                        .getCellAt(tmpY, tmpX);

                    String tmpValueCell = tmpHtmlPageIndex.getAsText(tmpHtmlElementCell);

                    if (StringUtils.isNotEmpty(tmpValueCell)) {
                      int tmpCoverage;
                      tmpCoverage = tmpSearchPattern.noOfSurroundingCharsIn(tmpValueCell);
                      if (tmpCoverage > -1) {
                        String tmpTextBefore = tmpHtmlPageIndex.getTextBefore(tmpHtmlElementCell);
                        int tmpDistance = tmpSearchPatternCoordX.noOfCharsAfterLastOccurenceIn(tmpTextBefore);
                        tmpMatches
                            .add(new MatchResult(tmpHtmlElementCell, FoundType.BY_TEXT, tmpCoverage, tmpDistance));

                        tmpAlreadyProcessed.put(tmpCellX2, tmpCellY2);
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
    return tmpMatches;
  }

  private int findCellInRow(HtmlTableRow aRow, HtmlTableCell aCell) {
    int i = 0;
    for (HtmlTableCell tmpCell : aRow.getCells()) {
      if (tmpCell == aCell) {
        return i;
      }
      i++;
    }
    return -1;
  }

  private int findRowInTable(HtmlTable aTable, HtmlTableRow aRow) {
    int i = 0;
    for (HtmlTableRow tmpRow : aTable.getRows()) {
      if (tmpRow == aRow) {
        return i;
      }
      i++;
    }
    return -1;
  }

  private HtmlTableCell findEnclosingCell(HtmlElement aHtmlElement) {
    DomNode tmpParent = aHtmlElement;
    while (tmpParent != null && !(tmpParent instanceof HtmlTableCell)) {
      tmpParent = tmpParent.getParentNode();
    }
    if (tmpParent == null) {
      return null;
    }
    return (HtmlTableCell) tmpParent;
  }

  private HtmlTable findEnclosingTable(HtmlElement aHtmlElement) {
    DomNode tmpParent = aHtmlElement.getParentNode();
    while (tmpParent != null && !(tmpParent instanceof HtmlTable)) {
      tmpParent = tmpParent.getParentNode();
    }
    if (tmpParent == null) {
      return null;
    }
    return (HtmlTable) tmpParent;
  }
}
