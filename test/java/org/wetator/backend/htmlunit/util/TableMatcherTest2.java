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
public class TableMatcherTest2 {

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

    for (HtmlElement tmpHtmlElement : tmpHtmlPageIndex.getAllVisibleHtmlElements()) {

      FindSpot tmpNodeSpot = tmpHtmlPageIndex.getPosition(tmpHtmlElement);
      if (null != tmpPathSpot && tmpPathSpot.endPos <= tmpNodeSpot.startPos) {

        String tmpValueCell = tmpHtmlPageIndex.getAsText(tmpHtmlElement);

        if (StringUtils.isNotEmpty(tmpValueCell)) {
          int tmpCoverage;
          tmpCoverage = tmpSearchPattern.noOfSurroundingCharsIn(tmpValueCell);
          if (tmpCoverage > -1) {
            String tmpTextBefore = tmpHtmlPageIndex.getTextBefore(tmpHtmlElement);
            int tmpDistance = tmpPathSearchPattern.noOfCharsAfterLastOccurenceIn(tmpTextBefore);
            tmpMatches.add(new MatchResult(tmpHtmlElement, FoundType.BY_TEXT, tmpCoverage, tmpDistance));
          }
        }
      }
    }

    List<MatchResult> tmpRealMatches = new ArrayList<MatchResult>();
    Map<HtmlTableCell, HtmlTableCell> tmpAlreadyProcessed = new HashMap<HtmlTableCell, HtmlTableCell>();

    for (MatchResult tmpMatchResult : tmpMatches) {
      HtmlElement tmpHtmlElement = tmpMatchResult.getHtmlElement();

      HtmlTableCell tmpCell = findEnclosingCell(tmpHtmlElement);
      if (tmpCell == null) {
        // element is not within a cell
        continue;
      }
      HtmlTableRow tmpRow = tmpCell.getEnclosingRow();
      HtmlTable tmpTable = tmpRow.getEnclosingTable();
      int tmpXStart = findCellInRow(tmpRow, tmpCell);
      int tmpXEnd = tmpXStart + tmpCell.getColumnSpan();
      int tmpYStart = findRowInTable(tmpTable, tmpRow);
      int tmpYEnd = tmpYStart + tmpCell.getRowSpan();

      HtmlTableCell tmpCellX = null;
      boolean tmpFoundX = false;
      for (int i = tmpXStart; i < tmpXEnd; i++) {
        for (int j = 0; j < tmpTable.getRowCount(); j++) {
          HtmlTableCell tmpCellX2 = tmpTable.getCellAt(j, i);
          if (tmpSearchPatternCoordX.matches(tmpHtmlPageIndex.getAsText(tmpCellX2))) {
            tmpFoundX = true;
            tmpCellX = tmpCellX2;
            break;
          }
        }
        if (tmpFoundX) {
          break;
        }
      }

      HtmlTableCell tmpCellY = null;
      boolean tmpFoundY = false;
      for (int i = tmpYStart; i < tmpYEnd; i++) {
        for (int j = 0; j < tmpTable.getRow(i).getCells().size(); j++) {
          HtmlTableCell tmpCellY2 = tmpTable.getCellAt(i, j);
          if (tmpSearchPatternCoordY.matches(tmpHtmlPageIndex.getAsText(tmpCellY2))) {
            tmpFoundY = true;
            tmpCellY = tmpCellY2;
            break;
          }
        }
        if (tmpFoundY) {
          break;
        }
      }

      if (tmpFoundX && tmpFoundY) {
        // if (tmpAlreadyProcessed.get(tmpCellX) == tmpCellY) {
        // // we have already found this match
        // continue;
        // }

        tmpRealMatches.add(tmpMatchResult);

        // tmpAlreadyProcessed.put(tmpCellX, tmpCellY);
      }
    }

    return tmpRealMatches;
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
