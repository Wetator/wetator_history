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


package org.rbri.wet.backend;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * List to store HtmlElements together with some 'weight' information.
 * Then it is possible to sort the list by this criterion.
 * 
 * @author rbri
 */
public final class WeightedControlList {

  /**
   * Enum for the different found by types.
   * Smaller values are more important.
   */
  public enum FoundType {
    /** found by text match */
    BY_TEXT(9999),

    /** found by image source attribute match */
    BY_IMG_SRC_ATTRIBUTE(5000),
    /** found by image alt attribute match */
    BY_IMG_ALT_ATTRIBUTE(5000),
    /** found by image title attribute match */
    BY_IMG_TITLE_ATTRIBUTE(5000),

    /** found by inner image source attribute match */
    BY_INNER_IMG_SRC_ATTRIBUTE(4000),
    /** found by inner image alt attribute match */
    BY_INNER_IMG_ALT_ATTRIBUTE(4000),
    /** found by inner image title attribute match */
    BY_INNER_IMG_TITLE_ATTRIBUTE(4000),

    /** found by label text match */
    BY_LABEL_TEXT(3000),

    /** found by label match */
    BY_LABEL(2000),

    /** found by name match */
    BY_NAME(1000),
    /** found by inner name match */
    BY_INNER_NAME(900),

    /** found by id match */
    BY_ID(400);

    private int value;

    private FoundType(int aValue) {
      value = aValue;
    }

    /**
     * Getter for the entry value.
     * 
     * @return the current value
     */
    public int getValue() {
      return value;
    }

    @Override
    public String toString() {
      return name();
    }
  }

  /**
   * The class for the WeightedControlList entries.
   */
  public static final class Entry {
    private Control control;
    private FoundType foundType;
    private int coverage;
    private int distance;

    /**
     * Returns the encapsulated Control
     * 
     * @return the control
     */
    public Control getControl() {
      return control;
    }

    @Override
    public String toString() {
      StringBuilder tmpResult = new StringBuilder();
      tmpResult.append(control.getDescribingText());
      tmpResult.append(" found by: " + foundType.toString());
      tmpResult.append(" coverage: " + coverage);
      tmpResult.append(" distance: " + distance);
      return tmpResult.toString();
    }
  }

  /**
   * The comparator used to sort WeightedControlList entries
   */
  private static final class EntryComperator implements Comparator<Entry> {
    @Override
    public int compare(final Entry anEntry1, final Entry anEntry2) {
      int tmpWeightComp = anEntry1.foundType.getValue() - anEntry2.foundType.getValue();

      if (0 == tmpWeightComp) {
        int tmpCoverageComp = anEntry1.coverage - anEntry2.coverage;

        if (0 == tmpCoverageComp) {
          int tmpDistanceComp = anEntry1.distance - anEntry2.distance;

          if (0 == tmpDistanceComp) {
            return anEntry1.control.getDescribingText().compareTo(anEntry2.control.getDescribingText());
          }

          return tmpDistanceComp;
        }

        return tmpCoverageComp;
      }

      return tmpWeightComp;
    }
  }

  private final List<Entry> entries;

  /**
   * Constructor
   */
  public WeightedControlList() {
    entries = Collections.synchronizedList(new LinkedList<Entry>());
  }

  /**
   * Creates a new entry and add the entry to this list.
   * 
   * @param aControl the control
   * @param aFoundType the found type
   * @param aCoverage the coverage
   * @param aDistance the distance
   */
  public void add(Control aControl, FoundType aFoundType, int aCoverage, int aDistance) {
    Entry tmpEntry = new Entry();
    tmpEntry.control = aControl;
    tmpEntry.foundType = aFoundType;
    tmpEntry.coverage = aCoverage;
    tmpEntry.distance = aDistance;

    entries.add(tmpEntry);
  }

  /**
   * Returns a new list of Entries sorted by weight.
   * 
   * @return a new list
   */
  public List<Entry> getElementsSorted() {
    Collections.sort(entries, new EntryComperator());

    List<Entry> tmpResult = new LinkedList<Entry>();
    for (Entry tmpEntry : entries) {
      Control tmpControl = tmpEntry.getControl();

      boolean tmpNotPresent = true;
      for (Entry tmpResultEntry : tmpResult) {
        Control tmpResultControl = tmpResultEntry.getControl();
        if (tmpResultControl.hasSameBackendControl(tmpControl)) {
          tmpNotPresent = false;
          break;
        }
      }
      if (tmpNotPresent) {
        tmpResult.add(tmpEntry);
      }
    }

    return tmpResult;
  }

  /**
   * Adds all elements form anOtherWeightedControlList to this list.
   * 
   * @param anOtherWeightedControlList the list of entries to add
   */
  public void addAll(WeightedControlList anOtherWeightedControlList) {
    entries.addAll(anOtherWeightedControlList.entries);
  }

  /**
   * Returns true, if the list is empty
   * 
   * @return true or false
   */
  public boolean isEmpty() {
    return entries.isEmpty();
  }
}