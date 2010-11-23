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


package org.rbri.wet.backend.htmlunit.finder;

import java.util.List;

import org.rbri.wet.backend.WPath;
import org.rbri.wet.backend.WeightedControlList;
import org.rbri.wet.backend.htmlunit.HtmlUnitControlRepository;
import org.rbri.wet.backend.htmlunit.control.HtmlUnitBaseControl;
import org.rbri.wet.backend.htmlunit.matcher.AbstractHtmlUnitElementMatcher.MatchResult;
import org.rbri.wet.backend.htmlunit.matcher.ByIdMatcher;
import org.rbri.wet.backend.htmlunit.util.FindSpot;
import org.rbri.wet.backend.htmlunit.util.HtmlPageIndex;
import org.rbri.wet.core.searchpattern.SearchPattern;

import com.gargoylesoftware.htmlunit.html.HtmlElement;

/**
 * This finder is a generic finder for all {@link HtmlElement}s not known by the {@link HtmlUnitControlRepository}. Only
 * instances of {@link HtmlUnitBaseControl} are
 * returned (so no specific subclasses). This finder supports just two find methods:
 * <ul>
 * <li>by id</li>
 * <li>by text (the first {@link HtmlElement} which matches the path and which's text contains the search pattern)</li>
 * </ul>
 * 
 * @author rbri
 * @author frank.danek
 */
public class UnknownHtmlUnitControlsFinder extends AbstractHtmlUnitControlsFinder {

  private HtmlUnitControlRepository controlRepository;

  /**
   * The constructor.
   * 
   * @param aHtmlPageIndex the {@link HtmlPageIndex} index of the page
   * @param aControlRepository the repository of known controls
   */
  public UnknownHtmlUnitControlsFinder(HtmlPageIndex aHtmlPageIndex, HtmlUnitControlRepository aControlRepository) {
    super(aHtmlPageIndex);

    controlRepository = aControlRepository;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.backend.htmlunit.finder.AbstractHtmlUnitControlsFinder#find(WPath)
   */
  @Override
  public WeightedControlList find(WPath aWPath) {
    WeightedControlList tmpFoundControls = new WeightedControlList();

    SearchPattern tmpSearchPattern = aWPath.getNode(aWPath.size() - 1).getSearchPattern();
    SearchPattern tmpPathSearchPattern = SearchPattern.createFromWPath(aWPath, aWPath.size() - 1);

    FindSpot tmpPathSpot = htmlPageIndex.firstOccurence(tmpPathSearchPattern);
    if (null == tmpPathSpot) {
      return tmpFoundControls;
    }

    // search with id
    for (HtmlElement tmpHtmlElement : htmlPageIndex.getAllVisibleHtmlElements()) {
      if (controlRepository == null || controlRepository.getForHtmlElement(tmpHtmlElement) == null) {
        List<MatchResult> tmpMatches = new ByIdMatcher(htmlPageIndex, tmpPathSearchPattern, tmpPathSpot,
            tmpSearchPattern).matches(tmpHtmlElement);
        for (MatchResult tmpMatch : tmpMatches) {
          tmpFoundControls.add(new HtmlUnitBaseControl<HtmlElement>(tmpMatch.getHtmlElement()),
              tmpMatch.getFoundType(), tmpMatch.getCoverage(), tmpMatch.getDistance());
        }
      }
    }

    FindSpot tmpHitSpot = htmlPageIndex.firstOccurence(tmpSearchPattern, Math.max(0, tmpPathSpot.endPos));
    while ((null != tmpHitSpot) && (tmpHitSpot.endPos > -1)) {
      // found a hit

      // find the first element that surrounds this
      for (HtmlElement tmpHtmlElement : htmlPageIndex.getAllVisibleHtmlElementsBottomUp()) {
        FindSpot tmpNodeSpot = htmlPageIndex.getPosition(tmpHtmlElement);
        if ((tmpNodeSpot.startPos <= tmpHitSpot.startPos) && (tmpHitSpot.endPos <= tmpNodeSpot.endPos)) {
          // found one
          String tmpTextBefore = htmlPageIndex.getTextBeforeIncludingMyself(tmpHtmlElement);
          FindSpot tmpLastOccurence = tmpSearchPattern.lastOccurenceIn(tmpTextBefore);
          int tmpCoverage = tmpTextBefore.length() - tmpLastOccurence.endPos;

          tmpTextBefore = tmpTextBefore.substring(0, tmpLastOccurence.startPos);
          int tmpDistance = tmpPathSearchPattern.noOfCharsAfterLastOccurenceIn(tmpTextBefore);

          if (controlRepository == null || controlRepository.getForHtmlElement(tmpHtmlElement) == null) {
            tmpFoundControls.add(new HtmlUnitBaseControl<HtmlElement>(tmpHtmlElement),
                WeightedControlList.FoundType.BY_TEXT, tmpCoverage, tmpDistance);
          }
          break;
        }
      }

      tmpHitSpot = htmlPageIndex.firstOccurence(tmpSearchPattern, tmpHitSpot.startPos + 1);
    }
    return tmpFoundControls;
  }
}
