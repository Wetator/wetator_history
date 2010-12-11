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
import java.util.Iterator;
import java.util.List;

import org.wetator.backend.WPath;
import org.wetator.backend.WeightedControlList.FoundType;
import org.wetator.backend.htmlunit.matcher.AbstractHtmlUnitElementMatcher.MatchResult;
import org.wetator.backend.htmlunit.matcher.ByTextMatcher;
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
public class TableFinder2 {

  public List<MatchResult> findInTableWPathByText(String aHtmlPage, List<SecretString> aSearch) throws IOException {
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(aHtmlPage);
    HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    WPath tmpPath = new WPath(aSearch);

    List<MatchResult> tmpRealMatches = new ArrayList<MatchResult>();

    for (HtmlElement tmpHtmlElement : tmpHtmlPageIndex.getAllVisibleHtmlElements()) {

      // TODO Identifier starts here
      SearchPattern tmpSearchPattern = tmpPath.getNode(tmpPath.size() - 1).getSearchPattern();
      SearchPattern tmpPathSearchPattern = SearchPattern.createFromWPath(tmpPath, tmpPath.size() - 2);
      FindSpot tmpPathSpot = tmpHtmlPageIndex.firstOccurence(tmpPathSearchPattern);

      if (null == tmpPathSpot) {
        return tmpRealMatches;
      }

      List<MatchResult> tmpMatches = new ArrayList<MatchResult>();
      tmpMatches.addAll(new ByTextMatcher(tmpHtmlPageIndex, tmpPathSearchPattern, tmpPathSpot, tmpSearchPattern)
          .matches(tmpHtmlElement));

      // TODO Identifier coord postprocessing starts here
      SecretString tmpTableString = tmpPath.getNode(tmpPath.size() - 2);

      System.out.println("coords: " + tmpTableString);

      String[] tmpCoords = tmpTableString.getValue().split(";");
      SecretString tmpCoordX = new SecretString(tmpCoords[0].substring(1).trim(), false);
      SecretString tmpCoordY = new SecretString(tmpCoords[1].substring(0, tmpCoords[1].length() - 1).trim(), false);

      System.out.println("coordX: " + tmpCoordX);
      System.out.println("coordY: " + tmpCoordY);

      SearchPattern tmpSearchPatternCoordX = tmpCoordX.getSearchPattern();
      SearchPattern tmpSearchPatternCoordY = tmpCoordY.getSearchPattern();

      for (MatchResult tmpMatchResult : tmpMatches) {
        HtmlElement tmpFoundHtmlElement = tmpMatchResult.getHtmlElement();

        if (isElementInTable(tmpFoundHtmlElement, tmpSearchPatternCoordX, tmpSearchPatternCoordY, tmpHtmlPageIndex)) {
          tmpRealMatches.add(tmpMatchResult);
        }
      }
      // TODO Identifier coord postprocessing ends here
      // TODO Identifier ends here
    }

    // TODO where to do?
    removeParents(tmpRealMatches);

    return tmpRealMatches;
  }

  public List<MatchResult> findByTable(String aHtmlPage, List<SecretString> aSearch) throws IOException {
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(aHtmlPage);
    HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    WPath tmpPath = new WPath(aSearch);

    SearchPattern tmpPathSearchPattern = SearchPattern.createFromWPath(tmpPath, tmpPath.size() - 1);
    SecretString tmpTableString = tmpPath.getNode(tmpPath.size() - 1);

    System.out.println("coords: " + tmpTableString);

    FindSpot tmpPathSpot = tmpHtmlPageIndex.firstOccurence(tmpPathSearchPattern);

    List<MatchResult> tmpMatches = new ArrayList<MatchResult>();

    for (HtmlElement tmpHtmlElement : tmpHtmlPageIndex.getAllVisibleHtmlElements()) {

      // TODO ByTableCoordinatesMatcher starts here
      FindSpot tmpNodeSpot = tmpHtmlPageIndex.getPosition(tmpHtmlElement);
      if (null != tmpPathSpot && tmpPathSpot.endPos <= tmpNodeSpot.startPos) {

        String[] tmpCoords = tmpTableString.getValue().split(";");
        SecretString tmpCoordX = new SecretString(tmpCoords[0].substring(1).trim(), false);
        SecretString tmpCoordY = new SecretString(tmpCoords[1].substring(0, tmpCoords[1].length() - 1).trim(), false);

        System.out.println("coordX: " + tmpCoordX);
        System.out.println("coordY: " + tmpCoordY);

        SearchPattern tmpSearchPatternCoordX = tmpCoordX.getSearchPattern();
        SearchPattern tmpSearchPatternCoordY = tmpCoordY.getSearchPattern();

        if (isElementInTable(tmpHtmlElement, tmpSearchPatternCoordX, tmpSearchPatternCoordY, tmpHtmlPageIndex)) {
          int tmpCoverage = 0;
          if (tmpCoverage > -1) {
            String tmpTextBefore = tmpHtmlPageIndex.getTextBefore(tmpHtmlElement);
            int tmpDistance = tmpPathSearchPattern.noOfCharsAfterLastOccurenceIn(tmpTextBefore);
            tmpMatches.add(new MatchResult(tmpHtmlElement, FoundType.BY_TEXT, tmpCoverage, tmpDistance));
          }
        }
      }
      // TODO ByTableCoordinatesMatcher ends here
    }

    // TODO where to do?
    removeParents(tmpMatches);

    return tmpMatches;
  }

  private boolean isElementInTable(HtmlElement aHtmlElement, SearchPattern aSearchPatternCoordX,
      SearchPattern aSearchPatternCoordY, HtmlPageIndex aHtmlPageIndex) {
    HtmlTableCell tmpCell = findEnclosingCell(aHtmlElement);
    boolean tmpFoundX = false;
    boolean tmpFoundY = false;
    while (tmpCell != null) {
      HtmlTableRow tmpRow = tmpCell.getEnclosingRow();
      HtmlTable tmpTable = tmpRow.getEnclosingTable();

      if (!tmpFoundX) {
        int tmpXStart = findCellInRow(tmpRow, tmpCell);
        int tmpXEnd = tmpXStart + tmpCell.getColumnSpan();
        for (int i = tmpXStart; i < tmpXEnd; i++) {
          for (int j = 0; j < tmpTable.getRowCount(); j++) {
            HtmlTableCell tmpCellX2 = tmpTable.getCellAt(j, i);
            if (aSearchPatternCoordX.matches(aHtmlPageIndex.getAsText(tmpCellX2))) {
              tmpFoundX = true;
              break;
            }
          }
          if (tmpFoundX) {
            break;
          }
        }
      }

      if (!tmpFoundY) {
        int tmpYStart = findRowInTable(tmpTable, tmpRow);
        int tmpYEnd = tmpYStart + tmpCell.getRowSpan();
        for (int i = tmpYStart; i < tmpYEnd; i++) {
          for (int j = 0; j < tmpTable.getRow(i).getCells().size(); j++) {
            HtmlTableCell tmpCellY2 = tmpTable.getCellAt(i, j);
            if (aSearchPatternCoordY.matches(aHtmlPageIndex.getAsText(tmpCellY2))) {
              tmpFoundY = true;
              break;
            }
          }
          if (tmpFoundY) {
            break;
          }
        }
      }

      if (tmpFoundX && tmpFoundY) {
        return true;
      }

      tmpCell = findEnclosingCell(tmpRow);
    }

    return false;
  }

  private void removeParents(List<MatchResult> aMatches) {
    for (Iterator<MatchResult> tmpIterator = aMatches.iterator(); tmpIterator.hasNext();) {
      MatchResult tmpMatchResult2 = tmpIterator.next();
      boolean tmpFound = false;
      for (MatchResult tmpMatchResult3 : aMatches) {
        HtmlElement tmpParent = tmpMatchResult3.getHtmlElement().getEnclosingElement(
            tmpMatchResult2.getHtmlElement().getTagName());
        while (tmpParent != null) {
          if (tmpParent == tmpMatchResult2.getHtmlElement()) {
            tmpFound = true;
            break;
          }
          tmpParent = tmpParent.getEnclosingElement(tmpMatchResult2.getHtmlElement().getTagName());
        }
        if (tmpFound) {
          break;
        }
      }
      if (tmpFound) {
        tmpIterator.remove();
      }
    }
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
}
