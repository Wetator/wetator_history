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


package org.rbri.wet.backend.htmlunit.control.identifier;

import java.util.LinkedList;
import java.util.List;

import org.rbri.wet.backend.WPath;
import org.rbri.wet.backend.WeightedControlList;
import org.rbri.wet.backend.htmlunit.control.HtmlUnitInputHidden;
import org.rbri.wet.backend.htmlunit.matcher.AbstractHtmlUnitElementMatcher.MatchResult;
import org.rbri.wet.backend.htmlunit.matcher.ByIdMatcher;
import org.rbri.wet.backend.htmlunit.matcher.ByNameAttributeMatcher;
import org.rbri.wet.backend.htmlunit.util.FindSpot;
import org.rbri.wet.core.searchpattern.SearchPattern;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlHiddenInput;

/**
 * The identifier for a {@link HtmlUnitInputHidden}.<br />
 * It can be identified by:
 * <ul>
 * <li>it's name</li>
 * <li>it's id</li>
 * </ul>
 * 
 * @author frank.danek
 */
public class HtmlUnitInputHiddenIdentifier extends AbstractHtmlUnitControlIdentifier {

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.backend.htmlunit.control.identifier.AbstractHtmlUnitControlIdentifier#isHtmlElementSupported(com.gargoylesoftware.htmlunit.html.HtmlElement)
   */
  @Override
  public boolean isHtmlElementSupported(HtmlElement aHtmlElement) {
    return aHtmlElement instanceof HtmlHiddenInput;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.backend.htmlunit.control.identifier.AbstractHtmlUnitControlIdentifier#identify(WPath,
   *      com.gargoylesoftware.htmlunit.html.HtmlElement)
   */
  @Override
  public WeightedControlList identify(WPath aWPath, HtmlElement aHtmlElement) {
    SearchPattern tmpSearchPattern = aWPath.getNode(aWPath.size() - 1).getSearchPattern();
    SearchPattern tmpPathSearchPattern = SearchPattern.createFromWPath(aWPath, aWPath.size() - 1);
    FindSpot tmpPathSpot = htmlPageIndex.firstOccurence(tmpPathSearchPattern);

    if (null == tmpPathSpot) {
      return new WeightedControlList();
    }

    List<MatchResult> tmpMatches = new LinkedList<MatchResult>();
    tmpMatches.addAll(new ByNameAttributeMatcher(htmlPageIndex, tmpPathSearchPattern, tmpPathSpot, tmpSearchPattern)
        .matches(aHtmlElement));
    tmpMatches.addAll(new ByIdMatcher(htmlPageIndex, tmpPathSearchPattern, tmpPathSpot, tmpSearchPattern)
        .matches(aHtmlElement));
    WeightedControlList tmpResult = new WeightedControlList();
    for (MatchResult tmpMatch : tmpMatches) {
      tmpResult.add(new HtmlUnitInputHidden((HtmlHiddenInput) tmpMatch.getHtmlElement()), tmpMatch.getFoundType(),
          tmpMatch.getCoverage(), tmpMatch.getDistance());
    }
    return tmpResult;
  }
}
