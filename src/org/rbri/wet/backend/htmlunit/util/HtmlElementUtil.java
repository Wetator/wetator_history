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

import org.apache.commons.lang.StringUtils;

import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlButtonInput;
import com.gargoylesoftware.htmlunit.html.HtmlCheckBoxInput;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlFileInput;
import com.gargoylesoftware.htmlunit.html.HtmlImage;
import com.gargoylesoftware.htmlunit.html.HtmlImageInput;
import com.gargoylesoftware.htmlunit.html.HtmlOption;
import com.gargoylesoftware.htmlunit.html.HtmlOptionGroup;
import com.gargoylesoftware.htmlunit.html.HtmlParagraph;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlRadioButtonInput;
import com.gargoylesoftware.htmlunit.html.HtmlResetInput;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import com.gargoylesoftware.htmlunit.html.HtmlSpan;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextArea;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

/**
 * Helper methods to work with the HtmlElements page.
 * 
 * @author exxws
 */
public class HtmlElementUtil {

  /**
   * Private constructor; this util has only static methods
   */
  private HtmlElementUtil() {
    super();
  }

  public static String getDescribingTextForHtmlAnchor(HtmlAnchor anHtmlAnchor) {
    StringBuilder tmpResult = new StringBuilder();

    tmpResult.append("[HtmlAnchor");

    // TODO this handles only the most common situations
    if (anHtmlAnchor.getFirstChild() instanceof HtmlImage) {
      tmpResult.append(" '");
      tmpResult.append("image: ");
      tmpResult.append(((HtmlImage) anHtmlAnchor.getFirstChild()).getSrcAttribute());
      tmpResult.append("'");
    }

    String tmpText = anHtmlAnchor.asText();
    if (StringUtils.isNotEmpty(tmpText)) {
      tmpResult.append(" '");
      tmpResult.append(tmpText);
      tmpResult.append("'");
    }

    addId(tmpResult, anHtmlAnchor);
    addName(tmpResult, anHtmlAnchor);

    tmpResult.append("]");
    return tmpResult.toString();
  }

  public static String getDescribingTextForHtmlButton(HtmlButton anHtmlButton) {
    StringBuilder tmpResult = new StringBuilder();

    tmpResult.append("[HtmlButton");

    // TODO this handles only the most common situations
    if (anHtmlButton.getFirstChild() instanceof HtmlImage) {
      tmpResult.append(" '");
      tmpResult.append("image: ");
      tmpResult.append(((HtmlImage) anHtmlButton.getFirstChild()).getSrcAttribute());
      tmpResult.append("'");
    }
    if (StringUtils.isNotEmpty(anHtmlButton.asText())) {
      tmpResult.append(" '");
      tmpResult.append(anHtmlButton.asText());
      tmpResult.append("'");
    } else if (StringUtils.isNotEmpty(anHtmlButton.getValueAttribute())) {
      tmpResult.append(" '");
      tmpResult.append(anHtmlButton.getValueAttribute());
      tmpResult.append("'");
    }

    addId(tmpResult, anHtmlButton);
    addName(tmpResult, anHtmlButton);

    tmpResult.append("]");
    return tmpResult.toString();
  }

  public static String getDescribingTextForHtmlButtonInput(HtmlButtonInput anHtmlButtonInput) {
    StringBuilder tmpResult = new StringBuilder();

    tmpResult.append("[HtmlButtonInput '");
    tmpResult.append(anHtmlButtonInput.getValueAttribute());
    tmpResult.append("'");

    addId(tmpResult, anHtmlButtonInput);
    addName(tmpResult, anHtmlButtonInput);

    tmpResult.append("]");
    return tmpResult.toString();
  }

  public static String getDescribingTextForHtmlCheckBoxInput(HtmlCheckBoxInput anHtmlCheckBoxInput) {
    StringBuilder tmpResult = new StringBuilder();

    tmpResult.append("[HtmlCheckBoxInput");

    addId(tmpResult, anHtmlCheckBoxInput);
    addName(tmpResult, anHtmlCheckBoxInput);

    tmpResult.append("]");
    return tmpResult.toString();
  }

  public static String getDescribingTextForHtmlFileInput(HtmlFileInput anHtmlFileInput) {
    StringBuilder tmpResult = new StringBuilder();

    tmpResult.append("[HtmlFileInput");

    addId(tmpResult, anHtmlFileInput);
    addName(tmpResult, anHtmlFileInput);

    tmpResult.append("]");
    return tmpResult.toString();
  }

  public static String getDescribingTextForHtmlImage(HtmlImage anHtmlImage) {
    StringBuilder tmpResult = new StringBuilder();

    tmpResult.append("[HtmlImage '");
    tmpResult.append(anHtmlImage.getSrcAttribute());
    tmpResult.append("'");

    addId(tmpResult, anHtmlImage);
    addName(tmpResult, anHtmlImage);

    tmpResult.append("]");
    return tmpResult.toString();
  }

  public static String getDescribingTextForHtmlImageInput(HtmlImageInput anHtmlImageInput) {
    StringBuilder tmpResult = new StringBuilder();

    tmpResult.append("[HtmlImageInput '");
    tmpResult.append(anHtmlImageInput.getValueAttribute());
    tmpResult.append("'");

    tmpResult.append(" (src='");
    tmpResult.append(anHtmlImageInput.getSrcAttribute());
    tmpResult.append("')");

    addId(tmpResult, anHtmlImageInput);
    addName(tmpResult, anHtmlImageInput);

    tmpResult.append("]");
    return tmpResult.toString();
  }

  public static String getDescribingTextForHtmlParagraph(HtmlParagraph anHtmlParagraph) {
    StringBuilder tmpResult = new StringBuilder();

    tmpResult.append("[HtmlParagraph");

    String tmpText = anHtmlParagraph.asText();
    if (StringUtils.isNotEmpty(tmpText)) {
      tmpResult.append(" '");
      tmpResult.append(tmpText);
      tmpResult.append("'");
    }

    addId(tmpResult, anHtmlParagraph);
    addName(tmpResult, anHtmlParagraph);

    tmpResult.append("]");
    return tmpResult.toString();
  }

  public static String getDescribingTextForHtmlPasswordInput(HtmlPasswordInput anHtmlPasswordInput) {
    StringBuilder tmpResult = new StringBuilder();

    tmpResult.append("[HtmlPasswordInput");

    addId(tmpResult, anHtmlPasswordInput);
    addName(tmpResult, anHtmlPasswordInput);

    tmpResult.append("]");
    return tmpResult.toString();
  }

  public static String getDescribingTextForHtmlRadioButtonInput(HtmlRadioButtonInput anHtmlRadioButtonInput) {
    StringBuilder tmpResult = new StringBuilder();

    tmpResult.append("[HtmlRadioButtonInput '");
    tmpResult.append(anHtmlRadioButtonInput.getValueAttribute());
    tmpResult.append("'");

    addId(tmpResult, anHtmlRadioButtonInput);
    addName(tmpResult, anHtmlRadioButtonInput);

    tmpResult.append("]");
    return tmpResult.toString();
  }

  public static String getDescribingTextForHtmlResetInput(HtmlResetInput anHtmlResetInput) {
    StringBuilder tmpResult = new StringBuilder();

    tmpResult.append("[HtmlResetInput '");
    tmpResult.append(anHtmlResetInput.getValueAttribute());
    tmpResult.append("'");

    addId(tmpResult, anHtmlResetInput);
    addName(tmpResult, anHtmlResetInput);

    tmpResult.append("]");
    return tmpResult.toString();
  }

  public static String getDescribingTextForHtmlSelect(HtmlSelect anHtmlSelect) {
    StringBuilder tmpResult = new StringBuilder();

    tmpResult.append("[HtmlSelect");

    addId(tmpResult, anHtmlSelect);
    addName(tmpResult, anHtmlSelect);

    tmpResult.append("]");
    return tmpResult.toString();
  }

  public static String getDescribingTextForHtmlSpan(HtmlSpan anHtmlSpan) {
    StringBuilder tmpResult = new StringBuilder();

    tmpResult.append("[HtmlSpan '");
    tmpResult.append(anHtmlSpan.asText());
    tmpResult.append("'");

    addId(tmpResult, anHtmlSpan);
    addName(tmpResult, anHtmlSpan);

    tmpResult.append("]");
    return tmpResult.toString();
  }

  public static String getDescribingTextForHtmlSubmitInput(HtmlSubmitInput anHtmlSubmitInput) {
    StringBuilder tmpResult = new StringBuilder();

    tmpResult.append("[HtmlSubmitInput '");
    tmpResult.append(anHtmlSubmitInput.getValueAttribute());
    tmpResult.append("'");

    addId(tmpResult, anHtmlSubmitInput);
    addName(tmpResult, anHtmlSubmitInput);

    tmpResult.append("]");
    return tmpResult.toString();
  }

  public static String getDescribingTextForHtmlTextArea(HtmlTextArea anHtmlTextArea) {
    StringBuilder tmpResult = new StringBuilder();

    tmpResult.append("[HtmlTextArea");

    addId(tmpResult, anHtmlTextArea);
    addName(tmpResult, anHtmlTextArea);

    tmpResult.append("]");
    return tmpResult.toString();
  }

  public static String getDescribingTextForHtmlTextInput(HtmlTextInput anHtmlTextInput) {
    StringBuilder tmpResult = new StringBuilder();

    tmpResult.append("[HtmlTextInput");

    addId(tmpResult, anHtmlTextInput);
    addName(tmpResult, anHtmlTextInput);

    tmpResult.append("]");
    return tmpResult.toString();
  }

  public static String getDescribingTextForHtmlOption(HtmlOption anHtmlOption) {
    StringBuilder tmpResult = new StringBuilder();

    tmpResult.append("[HtmlOption '");

    tmpResult.append(anHtmlOption.asText());
    tmpResult.append("'");

    addId(tmpResult, anHtmlOption);
    addName(tmpResult, anHtmlOption);

    tmpResult.append("]");
    return tmpResult.toString();
  }

  public static String getDescribingTextForHtmlOptionGroup(HtmlOptionGroup anHtmlOptionGroup) {
    StringBuilder tmpResult = new StringBuilder();

    tmpResult.append("[HtmlOptionGroup '");

    tmpResult.append(anHtmlOptionGroup.getLabelAttribute());
    tmpResult.append("'");

    addId(tmpResult, anHtmlOptionGroup);
    addName(tmpResult, anHtmlOptionGroup);

    tmpResult.append("]");
    return tmpResult.toString();
  }

  private static void addId(StringBuilder aStringBuilder, HtmlElement anHtmlElement) {
    String tmpId = anHtmlElement.getAttribute("id");
    if ((null != tmpId) && (tmpId.length() > 0)) {
      aStringBuilder.append(" (id='");
      aStringBuilder.append(tmpId);
      aStringBuilder.append("')");
    }
  }

  private static void addName(StringBuilder aStringBuilder, HtmlElement anHtmlElement) {
    String tmpName = anHtmlElement.getAttribute("name");
    if ((null != tmpName) && (tmpName.length() > 0)) {
      aStringBuilder.append(" (name='");
      aStringBuilder.append(tmpName);
      aStringBuilder.append("')");
    }
  }
}