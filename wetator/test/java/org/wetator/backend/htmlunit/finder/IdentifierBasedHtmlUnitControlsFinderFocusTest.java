/*
 * Copyright (c) 2008-2020 wetator.org
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


package org.wetator.backend.htmlunit.finder;

import static org.wetator.backend.htmlunit.finder.HtmlCodeCreator.CONTENT;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.wetator.backend.ControlFeature;
import org.wetator.backend.WPath;
import org.wetator.backend.htmlunit.control.identifier.AbstractMatcherBasedIdentifier;
import org.wetator.backend.htmlunit.control.identifier.HtmlUnitAnchorIdentifier;
import org.wetator.backend.htmlunit.control.identifier.HtmlUnitButtonIdentifier;
import org.wetator.backend.htmlunit.control.identifier.HtmlUnitInputButtonIdentifier;
import org.wetator.backend.htmlunit.control.identifier.HtmlUnitInputCheckBoxIdentifier;
import org.wetator.backend.htmlunit.control.identifier.HtmlUnitInputImageIdentifier;
import org.wetator.backend.htmlunit.control.identifier.HtmlUnitInputRadioButtonIdentifier;
import org.wetator.backend.htmlunit.control.identifier.HtmlUnitInputResetIdentifier;
import org.wetator.backend.htmlunit.control.identifier.HtmlUnitInputSubmitIdentifier;
import org.wetator.backend.htmlunit.control.identifier.HtmlUnitInputTextIdentifier;
import org.wetator.backend.htmlunit.control.identifier.HtmlUnitOptionIdentifier;
import org.wetator.backend.htmlunit.control.identifier.HtmlUnitSelectIdentifier;
import org.wetator.backend.htmlunit.finder.WeightedControlListEntryAssert.ExpectedControl;
import org.wetator.backend.htmlunit.finder.WeightedControlListEntryAssert.SortedEntryExpectation;

import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlBody;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlButtonInput;
import com.gargoylesoftware.htmlunit.html.HtmlCheckBoxInput;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlImageInput;
import com.gargoylesoftware.htmlunit.html.HtmlLabel;
import com.gargoylesoftware.htmlunit.html.HtmlOption;
import com.gargoylesoftware.htmlunit.html.HtmlRadioButtonInput;
import com.gargoylesoftware.htmlunit.html.HtmlResetInput;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import com.gargoylesoftware.htmlunit.html.HtmlSpan;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTableDataCell;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

/**
 * Tests for element weighting during {@link IdentifierBasedHtmlUnitControlsFinder#find(WPath)} concerning
 * {@link ControlFeature#FOCUS}.
 *
 * @author tobwoerk
 */
@RunWith(Parameterized.class)
public class IdentifierBasedHtmlUnitControlsFinderFocusTest extends AbstractIdentifierBasedHtmlUnitControlsFinderTest {

  @Parameter(0)
  public Object htmlCode;
  @Parameter(1)
  public SortedEntryExpectation expected;
  @Parameter(2)
  public Object identifiers;

  @BeforeClass
  public static void listenToFocus() {
    HtmlCodeCreator.listenToFocus();
  }

  @Parameters(name = "{0}")
  public static Collection<Object[]> provideParameters() {
    listenToFocus();

    final Object[][] tmpData = new Object[][] { //
    // @formatter:off
      //++++++++++++
      // w/ tabindex

      { a("anchor-before").a("anchor", CONTENT).a("anchor-after"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlAnchor.class, "anchor")),
        HtmlUnitAnchorIdentifier.class
      },

      { button("button-before").button("button", CONTENT).button("button-after"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlButton.class, "button")),
        HtmlUnitButtonIdentifier.class
      },

      { checkbox("checkbox-before") + CONTENT + checkbox("checkbox-after").label("checkbox-label", CONTENT).noListen().checkbox("checkbox-label"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlCheckBoxInput.class, "checkbox-label"),
            new ExpectedControl(HtmlCheckBoxInput.class, "checkbox-before"),
            new ExpectedControl(HtmlCheckBoxInput.class, "checkbox-after"),
            new ExpectedControl(HtmlBody.class),
            new ExpectedControl(HtmlLabel.class, "lbl-checkbox-label")), // FIXME [UNKNOWN] adjust as soon as included in MouseActionListeningHtmlUnitControlsFinder
        HtmlUnitInputCheckBoxIdentifier.class
      },

      { div("div-before").div("div", CONTENT).div("div-after"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlDivision.class, "div")),
        null
      },

      { inputButton("inputButton-before").inputButton("inputButton", CONTENT).inputButton("inputButton-after"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlButtonInput.class, "inputButton")),
        HtmlUnitInputButtonIdentifier.class
      },

      { inputImage("inputImg-before").inputImage("inputImg", CONTENT).inputImage("inputImg-after"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlImageInput.class, "inputImg")),
        HtmlUnitInputImageIdentifier.class
      },

      { inputReset("inputReset-before").inputReset("inputReset", CONTENT).inputReset("inputReset-after"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlResetInput.class, "inputReset")),
        HtmlUnitInputResetIdentifier.class
      },

      { inputSubmit("inputSubmit-before").inputSubmit("inputSubmit", CONTENT).inputSubmit("inputSubmit-after"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlSubmitInput.class, "inputSubmit")),
        HtmlUnitInputSubmitIdentifier.class
      },

      { inputText("inputText-before").inputText("inputText", CONTENT).inputText("inputText-after"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlTextInput.class, "inputText"),
            new ExpectedControl(HtmlTextInput.class, "inputText-after")),
        HtmlUnitInputTextIdentifier.class
      },

      { image("img-before").image("img", CONTENT).image("img-after"),
        null, // tabindex currently not supported
        null
      },

      { label("before").label("main", CONTENT).label("after"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlLabel.class, "lbl-main")),
        null
      },

      { radio("radio-before") + CONTENT + radio("radio-after").label("radio-label", CONTENT).noListen().radio("radio-label"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlRadioButtonInput.class, "radio-label"),
            new ExpectedControl(HtmlRadioButtonInput.class, "radio-before"),
            new ExpectedControl(HtmlRadioButtonInput.class, "radio-after"),
            new ExpectedControl(HtmlBody.class),
            new ExpectedControl(HtmlLabel.class, "lbl-radio-label")), // FIXME [UNKNOWN] adjust as soon as included in MouseActionListeningHtmlUnitControlsFinder
        HtmlUnitInputRadioButtonIdentifier.class
      },

      { select("select-before") + CONTENT + select("select").option("option-before").option("option", CONTENT).option("option-after") + select("select-after"),
        // tabindex currently not supported
        new SortedEntryExpectation(
            new ExpectedControl(HtmlOption.class, "select-option"),
            new ExpectedControl(HtmlSelect.class, "select"),
            new ExpectedControl(HtmlSelect.class, "select-after"),
            new ExpectedControl(HtmlBody.class)),
        Arrays.asList(HtmlUnitSelectIdentifier.class, HtmlUnitOptionIdentifier.class)
      },

      { span("span-before").span("span", CONTENT).span("span-after"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlSpan.class, "span")),
        null
      },

      { table("table-before") + CONTENT + table("table").tr("tr", 1) + table("table-after"),
        // tabindex currently not supported
        new SortedEntryExpectation(
            new ExpectedControl(HtmlBody.class),
            new ExpectedControl(HtmlTableDataCell.class, "table-tr-td")), // FIXME [UNKNOWN] adjust as soon as included in MouseActionListeningHtmlUnitControlsFinder
        null
      },

      //+++++++++++++
      // w/o tabindex

      { a("anchor-before").noListen().a("anchor", CONTENT).noListen().a("anchor-after").noListen(),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlAnchor.class, "anchor")),
        HtmlUnitAnchorIdentifier.class
      },

      { button("button-before").noListen().button("button", CONTENT).noListen().button("button-after").noListen(),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlButton.class, "button")),
        HtmlUnitButtonIdentifier.class
      },

      { checkbox("checkbox-before").noListen() + CONTENT + checkbox("checkbox-after").noListen().label("checkbox-label", CONTENT).noListen().checkbox("checkbox-label").noListen(),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlCheckBoxInput.class, "checkbox-label"),
            new ExpectedControl(HtmlCheckBoxInput.class, "checkbox-before"),
            new ExpectedControl(HtmlCheckBoxInput.class, "checkbox-after"),
            new ExpectedControl(HtmlBody.class),
            new ExpectedControl(HtmlLabel.class, "lbl-checkbox-label")), // FIXME [UNKNOWN] adjust as soon as included in MouseActionListeningHtmlUnitControlsFinder
        HtmlUnitInputCheckBoxIdentifier.class
      },

      { div("div-before").noListen().div("div", CONTENT).noListen().div("div-after").noListen(),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlDivision.class, "div")),
        null
      },

      { inputButton("inputButton-before").noListen().inputButton("inputButton", CONTENT).noListen().inputButton("inputButton-after").noListen(),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlButtonInput.class, "inputButton")),
        HtmlUnitInputButtonIdentifier.class
      },

      { inputImage("inputImg-before").noListen().inputImage("inputImg", CONTENT).noListen().inputImage("inputImg-after").noListen(),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlImageInput.class, "inputImg")),
        HtmlUnitInputImageIdentifier.class
      },

      { inputReset("inputReset-before").noListen().inputReset("inputReset", CONTENT).noListen().inputReset("inputReset-after").noListen(),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlResetInput.class, "inputReset")),
        HtmlUnitInputResetIdentifier.class
      },

      { inputSubmit("inputSubmit-before").noListen().inputSubmit("inputSubmit", CONTENT).noListen().inputSubmit("inputSubmit-after").noListen(),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlSubmitInput.class, "inputSubmit")),
        HtmlUnitInputSubmitIdentifier.class
      },

      { inputText("inputText-before").noListen().inputText("inputText", CONTENT).noListen().inputText("inputText-after").noListen(),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlTextInput.class, "inputText"),
            new ExpectedControl(HtmlTextInput.class, "inputText-after")),
        HtmlUnitInputTextIdentifier.class
      },

      { image("img-before").noListen().image("img", CONTENT).noListen().image("img-after").noListen(),
        null,
        null
      },

      { label("before").noListen().label("main", CONTENT).noListen().label("after").noListen(),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlLabel.class, "lbl-main")),
        null
      },

      { radio("radio-before").noListen() + CONTENT + radio("radio-after").noListen().label("radio-label", CONTENT).noListen().radio("radio-label").noListen(),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlRadioButtonInput.class, "radio-label"),
            new ExpectedControl(HtmlRadioButtonInput.class, "radio-before"),
            new ExpectedControl(HtmlRadioButtonInput.class, "radio-after"),
            new ExpectedControl(HtmlBody.class),
            new ExpectedControl(HtmlLabel.class, "lbl-radio-label")), // FIXME [UNKNOWN] adjust as soon as included in MouseActionListeningHtmlUnitControlsFinder
        HtmlUnitInputRadioButtonIdentifier.class
      },

      { select("select-before").noListen() + CONTENT + select("select").noListen().option("option-before").noListen().option("option", CONTENT).noListen().option("option-after").noListen() + select("select-after").noListen(),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlOption.class, "select-option"),
            new ExpectedControl(HtmlSelect.class, "select"),
            new ExpectedControl(HtmlSelect.class, "select-after"),
            new ExpectedControl(HtmlBody.class)),
        Arrays.asList(HtmlUnitSelectIdentifier.class, HtmlUnitOptionIdentifier.class)
      },

      { span("span-before").noListen().span("span", CONTENT).noListen().span("span-after").noListen(),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlSpan.class, "span")),
        null
      },

      { table("table-before").noListen() + CONTENT + table("table").noListen().tr("tr", 1).noListen() + table("table-after").noListen(),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlBody.class),
            new ExpectedControl(HtmlTableDataCell.class, "table-tr-td")), // FIXME [UNKNOWN] adjust as soon as included in MouseActionListeningHtmlUnitControlsFinder
        null
      }
      // @formatter:on
    };

    return Arrays.asList(tmpData);
  }

  @Override
  protected IdentifierBasedHtmlUnitControlsFinder createFinder() {
    return new IdentifierBasedHtmlUnitControlsFinder(htmlPageIndex, null);
  }

  @Override
  @SuppressWarnings("unchecked")
  protected void setup(final String anHtmlCode) throws IOException {
    super.setup(anHtmlCode);

    if (identifiers instanceof Class) {
      addIdentifiers((Class<? extends AbstractMatcherBasedIdentifier>) identifiers);
    } else if (identifiers != null) {
      addIdentifiers((List<Class<? extends AbstractMatcherBasedIdentifier>>) identifiers);
    }
  }

  @Test
  public void checkFoundElementsFocus() throws Exception {
    checkFoundElements(htmlCode, expected);
  }
}
