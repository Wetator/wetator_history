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


package org.rbri.wet.backend.htmlunit.util;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.rbri.wet.util.NormalizedContent;

import com.gargoylesoftware.htmlunit.html.DomComment;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.DomText;
import com.gargoylesoftware.htmlunit.html.HtmlApplet;
import com.gargoylesoftware.htmlunit.html.HtmlBody;
import com.gargoylesoftware.htmlunit.html.HtmlBreak;
import com.gargoylesoftware.htmlunit.html.HtmlCheckBoxInput;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlFileInput;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlHead;
import com.gargoylesoftware.htmlunit.html.HtmlHiddenInput;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlLegend;
import com.gargoylesoftware.htmlunit.html.HtmlListItem;
import com.gargoylesoftware.htmlunit.html.HtmlOption;
import com.gargoylesoftware.htmlunit.html.HtmlOptionGroup;
import com.gargoylesoftware.htmlunit.html.HtmlOrderedList;
import com.gargoylesoftware.htmlunit.html.HtmlParagraph;
import com.gargoylesoftware.htmlunit.html.HtmlRadioButtonInput;
import com.gargoylesoftware.htmlunit.html.HtmlScript;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import com.gargoylesoftware.htmlunit.html.HtmlStyle;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableCell;
import com.gargoylesoftware.htmlunit.html.HtmlTableDataCell;
import com.gargoylesoftware.htmlunit.html.HtmlTableHeader;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;
import com.gargoylesoftware.htmlunit.html.HtmlTitle;
import com.gargoylesoftware.htmlunit.html.HtmlUnorderedList;
import com.gargoylesoftware.htmlunit.html.SubmittableElement;

/**
 * The text representation of a page text.
 * Indexed by form controls to speed up
 * the calculation of text before and after.
 * 
 * @author rbri
 */
public class DomNodeText {
    
    private NormalizedContent text;
    private List<DomNode> nodes;
    private Map<DomNode, Integer> startPositions;
    private Map<DomNode, Integer> endPositions;
    
    public DomNodeText(final DomNode aDomNode) {
        text = new NormalizedContent();
        nodes = new LinkedList<DomNode>();
        startPositions = new HashMap<DomNode, Integer>();
        endPositions = new HashMap<DomNode, Integer>();

        parseDomNode(aDomNode);
    }


    public String getText() {
        return text.toString().trim();
    }
    

    public String getTextBefore(final DomNode aDomNode) {
        Integer tmpEndPos = startPositions.get(aDomNode);
        if (null == tmpEndPos) {
            return null;
        }
        return text.substring(0, tmpEndPos).trim();
    }
    

    public String getLabelTextBefore(final HtmlElement aHtmlElement) {
        Integer tmpEndPos = startPositions.get(aHtmlElement);
        if (null == tmpEndPos) {
            return null;
        }

        HtmlForm tmpCurrentForm = aHtmlElement.getEnclosingForm();
        int tmpStartPos = 0;
        ListIterator<DomNode> tmpIter = nodes.listIterator(nodes.indexOf(aHtmlElement));
        while(tmpIter.hasPrevious()) {
            DomNode tmpNode = tmpIter.previous();
            
            if (tmpNode instanceof HtmlBody) {
                // don't use the end pos of the body
                tmpStartPos = startPositions.get(tmpNode);
                break;
            }

            // we have to stop if we found some other (visible) form control
            if ((tmpNode instanceof SubmittableElement)
                    && !(tmpNode instanceof HtmlHiddenInput)) {
                tmpStartPos = endPositions.get(tmpNode);
                break;
            }

            // we have to stop if we are reaching an element of another form
            if (tmpNode instanceof HtmlElement) {
                HtmlForm tmpForm = ((HtmlElement)tmpNode).getEnclosingForm();
                // we are reaching another form
                if ((null != tmpForm) && (tmpForm != tmpCurrentForm)) {
                    tmpStartPos = endPositions.get(tmpNode);
                    break;
                }
            }
        }
        
        return text.substring(tmpStartPos, tmpEndPos).trim();
    }
    

    public String getLabelTextAfter(final HtmlElement aHtmlElement) {
        Integer tmpStartPos = endPositions.get(aHtmlElement);
        if (null == tmpStartPos) {
            return null;
        }

        HtmlForm tmpCurrentForm = aHtmlElement.getEnclosingForm();
        int tmpEndPos = text.length();
        ListIterator<DomNode> tmpIter = nodes.listIterator(nodes.indexOf(aHtmlElement));
        // start with the next element
        tmpIter.next();

        while(tmpIter.hasNext()) {
            DomNode tmpNode = tmpIter.next();
            
            // we have to stop if we found some other (visible) form control
            if ((tmpNode instanceof SubmittableElement)
                    && !(tmpNode instanceof HtmlHiddenInput)) {
                tmpEndPos = startPositions.get(tmpNode);
                break;
            }

            // we have to stop if we are reaching an element of another form
            if (tmpNode instanceof HtmlElement) {
                HtmlForm tmpForm = ((HtmlElement)tmpNode).getEnclosingForm();
                // we are reaching another form
                if ((null != tmpCurrentForm) && (tmpForm != tmpCurrentForm)) {
                    tmpEndPos = startPositions.get(tmpNode);
                    break;
                }
            }
        }
        
        return text.substring(tmpStartPos, tmpEndPos).trim();
    }

    
    public String getAsText(final DomNode aDomNode) {
        Integer tmpStartPos = startPositions.get(aDomNode);
        if (null == tmpStartPos) {
            return null;
        }
        Integer tmpEndPos = endPositions.get(aDomNode);
        
        return text.substring(tmpStartPos, tmpEndPos).trim();
    }
    
    private void parseDomNode(final DomNode aDomNode) {
        nodes.add(aDomNode);
        // mark pos before
        startPositions.put(aDomNode, Integer.valueOf(text.length()));
        
        if (aDomNode.isDisplayed()) {
            if (aDomNode instanceof HtmlHiddenInput
                    || aDomNode instanceof HtmlApplet
                    || aDomNode instanceof HtmlScript
                    || aDomNode instanceof HtmlStyle
                    || aDomNode instanceof HtmlFileInput
                    || aDomNode instanceof DomComment

                    || aDomNode instanceof HtmlHead
                    || aDomNode instanceof HtmlTitle
                ) {
                    // nothing
            } else if (aDomNode instanceof DomText) {
                appendDomText((DomText) aDomNode);
            } else if (aDomNode instanceof HtmlBreak) {
            	text.append(" ");
            } else if (aDomNode instanceof HtmlSelect) {
                appendHtmlSelect((HtmlSelect) aDomNode);
            } else if (aDomNode instanceof HtmlOptionGroup) {
                appendHtmlOptionGroup((HtmlOptionGroup) aDomNode);
            } else if (aDomNode instanceof HtmlLegend) {
                appendHtmlLegend((HtmlLegend) aDomNode);
            } else if (aDomNode instanceof HtmlCheckBoxInput) {
                appendHtmlCheckBoxInput((HtmlCheckBoxInput) aDomNode);
            } else if (aDomNode instanceof HtmlRadioButtonInput) {
                appendHtmlRadioButtonInput((HtmlRadioButtonInput) aDomNode);
            } else if (aDomNode instanceof HtmlInput) {
                appendHtmlInput((HtmlInput) aDomNode);
            } else if (aDomNode instanceof HtmlOrderedList) {
                appendHtmlOrderedList((HtmlOrderedList) aDomNode);
            } else {
                boolean tmpIsBlock = (aDomNode instanceof HtmlDivision) 
                                    || (aDomNode instanceof HtmlParagraph)
                                    || (aDomNode instanceof HtmlTable)
                                    || (aDomNode instanceof HtmlTableRow)
                                    || (aDomNode instanceof HtmlTableHeader)
                                    || (aDomNode instanceof HtmlTableDataCell)
                                    || (aDomNode instanceof HtmlTableCell)
                                    || (aDomNode instanceof HtmlUnorderedList)
                                    || (aDomNode instanceof HtmlListItem);
                if (tmpIsBlock) {
                	text.append(" ");
                }
                parseChildren(aDomNode);
                if (tmpIsBlock) {
                	text.append(" ");
                }
            }
        }

        // mark pos after
        endPositions.put(aDomNode, Integer.valueOf(text.length()));
    }


    private void parseChildren(final DomNode aNode) {
        for (DomNode tmpChild : aNode.getChildren()) {
            parseDomNode(tmpChild);
        }
    }

    private void appendDomText(final DomText aDomText) {
    	text.append(aDomText.getData());
    }

    private void appendHtmlInput(final HtmlInput aHtmlInput) {
    	text.append(aHtmlInput.getValueAttribute());
    }
    
    private void appendHtmlLegend(final HtmlLegend aHtmlLegend) {
        parseChildren(aHtmlLegend);
        text.append(" ");
    }
    
    private void appendHtmlOptionGroup(final HtmlOptionGroup aHtmlOptionGroup) {
        String tmpLabel = aHtmlOptionGroup.getLabelAttribute();
        text.append(tmpLabel);
    }

    private void appendHtmlCheckBoxInput(final HtmlCheckBoxInput aHtmlCheckBoxInput) {
        parseChildren(aHtmlCheckBoxInput);
        text.append(" ");
    }

    private void appendHtmlRadioButtonInput(final HtmlRadioButtonInput aHtmlRadioButtonInput) {
        parseChildren(aHtmlRadioButtonInput);
        text.append(" ");
    }

    private void appendHtmlSelect(final HtmlSelect anHtmlSelect) {
        for (final DomNode tmpItem : anHtmlSelect.getHtmlElementDescendants()) {
            if (    (tmpItem instanceof HtmlOption) || (tmpItem instanceof HtmlOptionGroup) ) {
              text.append(" ");
              parseDomNode(tmpItem);
            }
        }
        text.append(" ");
    }

    /**
     * Appends a &lt;ol&gt; taking care to numerate it.
     * @param htmlOrderedList the OL element
     */
    private void appendHtmlOrderedList(final HtmlOrderedList htmlOrderedList) {
        text.append(" ");
        int i = 1;
        for (final DomNode tmpItem : htmlOrderedList.getChildren()) {
            if (tmpItem instanceof HtmlListItem) {
                // hack for fixing the start pos
                int tmpSartPos = text.length();
                text.append(String.valueOf(i++));
                text.append(". ");
                parseDomNode(tmpItem);
                startPositions.put(tmpItem, Integer.valueOf(tmpSartPos));
            } else {
                parseDomNode(tmpItem);
            }
        }
        text.append(" ");
    }
}
