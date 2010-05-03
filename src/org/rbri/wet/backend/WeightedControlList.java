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
 * Then it is possible to sort the list by this criterions.
 *
 * @author rbri
 */
public final class WeightedControlList {

    public enum FoundType {
        BY_TEXT(9999),

        BY_IMG_SRC_ATTRIBUTE(5000),
        BY_IMG_ALT_ATTRIBUTE(5000),
        BY_IMG_TITLE_ATTRIBUTE(5000),

        BY_INNER_IMG_SRC_ATTRIBUTE(4000),
        BY_INNER_IMG_ALT_ATTRIBUTE(4000),
        BY_INNER_IMG_TITLE_ATTRIBUTE(4000),

        BY_LABEL_TEXT(3000),

        BY_LABEL(2000),

        BY_NAME(1000),
        BY_INNER_NAME(900),

        BY_ID(400);

        private int value;

        private FoundType(int aValue) {
            value = aValue;
        }

        public int getValue() {
            return value;
        }

        public String toString() {
            return name();
        }
    }


    public static final class Entry {
        protected Control control;
        protected FoundType foundType;
        protected int coverage;
        protected int distance;

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

    protected static final class EntryComperator implements Comparator<Entry> {
        public int compare(final Entry anEntry1, final Entry anEntry2) {
            int tmpWeightComp = anEntry1.foundType.getValue() - anEntry2.foundType.getValue();

            if (0 == tmpWeightComp) {
                int tmpCoverageComp = anEntry1.coverage - anEntry2.coverage;

                if (0 == tmpCoverageComp) {
                    return anEntry1.distance - anEntry2.distance;
                }
                return tmpCoverageComp;
            }

            return tmpWeightComp;
        }
    }


    private final List<Entry> entries;

    public WeightedControlList() {
        entries = new LinkedList<Entry>();
    }

//    public void addFoundByLabel(Control aControl, int aDistance) {
//        add(aControl, FoundType.BY_LABEL, aDistance);
//    }
//
//
//    public void addFoundByLabelText(Control aControl, int aDistance) {
//        add(aControl, FoundType.BY_LABEL_TEXT, aDistance);
//    }
//
//
//    public void addFoundByName(Control aControl) {
//        // LOG.debug(Messages.getMessage("log.imageMatchedByName", new String[] {aControl.getDescribingText(), aSearch.toString()}));
//        add(aControl, FoundType.BY_NAME, 0);
//    }
//
//
//    public void addFoundByInnerName(Control aControl) {
//        // LOG.debug(Messages.getMessage("log.imageMatchedByName", new String[] {aControl.getDescribingText(), aSearch.toString()}));
//        add(aControl, FoundType.BY_INNER_NAME, 0);
//    }
//
//
//    public void addFoundById(Control aControl) {
//        // LOG.debug(Messages.getMessage("log.imageMatchedById", new String[] {aControl.getDescribingText(), aSearch.toString()}));
//        add(aControl, FoundType.BY_ID, 0);
//    }
//
//
//    public void addFoundByText(Control aControl, int aDistance) {
//        // LOG.debug(Messages.getMessage("log.imageMatchedBySrc", new String[] {aControl.getDescribingText(), aSearch.toString()}));
//        add(aControl, FoundType.BY_TEXT, aDistance);
//    }
//
//
//    public void addFoundByInnerImgAltAttribute(Control aControl, int aDistance) {
//        // LOG.debug(Messages.getMessage("log.imageMatchedByAlt", new String[] {aControl.getDescribingText(), aSearch.toString()}));
//        add(aControl, FoundType.BY_INNER_IMG_ALT_ATTRIBUTE, aDistance);
//    }
//
//
//    public void addFoundByInnerImgTitleAttribute(Control aControl, int aDistance) {
//        // LOG.debug(Messages.getMessage("log.imageMatchedByTitle", new String[] {aControl.getDescribingText(), aSearch.toString()}));
//        add(aControl, FoundType.BY_INNER_IMG_TITLE_ATTRIBUTE, aDistance);
//    }
//
//
//    public void addFoundByInnerImgSrcAttribute(Control aControl) {
//        // LOG.debug(Messages.getMessage("log.imageMatchedBySrc", new String[] {aControl.getDescribingText(), aSearch.toString()}));
//        add(aControl, FoundType.BY_INNER_IMG_SRC_ATTRIBUTE, 0);
//    }
//
//
//    public void addFoundByImgAltAttribute(Control aControl, int aDistance) {
//        // LOG.debug(Messages.getMessage("log.imageMatchedByAlt", new String[] {aControl.getDescribingText(), aSearch.toString()}));
//        add(aControl, FoundType.BY_IMG_ALT_ATTRIBUTE, aDistance);
//    }
//
//
//    public void addFoundByImgTitleAttribute(Control aControl, int aDistance) {
//        // LOG.debug(Messages.getMessage("log.imageMatchedByTitle", new String[] {aControl.getDescribingText(), aSearch.toString()}));
//        add(aControl, FoundType.BY_IMG_TITLE_ATTRIBUTE, aDistance);
//    }
//
//
//    public void addFoundByImgSrcAttribute(Control aControl) {
//        // LOG.debug(Messages.getMessage("log.imageMatchedBySrc", new String[] {aControl.getDescribingText(), aSearch.toString()}));
//        add(aControl, FoundType.BY_IMG_SRC_ATTRIBUTE, 0);
//    }

    public void add(Control aControl, FoundType aFoundType, int aCoverage, int aDistance) {
        Entry tmpEntry = new Entry();
        tmpEntry.control = aControl;
        tmpEntry.foundType = aFoundType;
        tmpEntry.coverage = aCoverage;
        tmpEntry.distance = aDistance;

        entries.add(tmpEntry);
    }


    public List<Entry> getElementsSorted() {
        Collections.sort(entries, new EntryComperator());
        return Collections.unmodifiableList(entries);
    }


    public void addAll(WeightedControlList anOtherWeightedControlList) {
        entries.addAll(anOtherWeightedControlList.entries);
    }

    public boolean isEmpty() {
        return entries.isEmpty();
    }


    public boolean hasManyEntires() {
        return entries.size() > 1;
    }
}