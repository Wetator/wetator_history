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


package org.rbri.wet.commandset;

import java.net.URL;
import java.util.Properties;

import org.rbri.wet.backend.ControlFinder;
import org.rbri.wet.backend.WPath;
import org.rbri.wet.backend.WeightedControlList;
import org.rbri.wet.backend.WetBackend;
import org.rbri.wet.backend.control.Control;
import org.rbri.wet.core.WetCommand;
import org.rbri.wet.core.WetContext;
import org.rbri.wet.exception.AssertionFailedException;
import org.rbri.wet.util.Assert;
import org.rbri.wet.util.SecretString;

/**
 * The implementation of all experimental commands that Wetator
 * supports at the moment.<br>
 * We are not sure, that these commands are useful extension of
 * the current command set. So we have this set to play a bit.
 * 
 * @author rbri
 */
public final class IncubatorCommandSet extends AbstractCommandSet {

  /**
   * Constructor of the default command set
   */
  public IncubatorCommandSet() {
    super();
  }

  @Override
  protected void registerCommands() {
    registerCommand("Assert Focus", new CommandAssertFocus());
    registerCommand("Save Bookmark", new CommandSaveBookmark());
    registerCommand("Open Bookmark", new CommandOpenBookmark());
  }

  /**
   * Command 'Assert Focus'
   */
  public final class CommandAssertFocus implements WetCommandImplementation {
    /**
     * {@inheritDoc}
     * 
     * @see org.rbri.wet.commandset.WetCommandImplementation#execute(org.rbri.wet.core.WetContext,
     *      org.rbri.wet.core.WetCommand)
     */
    @Override
    public void execute(WetContext aWetContext, WetCommand aWetCommand) throws AssertionFailedException {
      WPath tmpWPath = new WPath(aWetCommand.getRequiredFirstParameterValues(aWetContext));
      aWetCommand.assertNoUnusedSecondParameter(aWetContext);

      WetBackend tmpBackend = getWetBackend(aWetContext);
      ControlFinder tmpControlFinder = tmpBackend.getControlFinder();

      // TextInputs / PasswordInputs / TextAreas / FileInputs
      WeightedControlList tmpFoundElements = tmpControlFinder.getAllSettables(tmpWPath);
      tmpFoundElements.addAll(tmpControlFinder.getAllSelectables(tmpWPath));
      tmpFoundElements.addAll(tmpControlFinder.getAllClickables(tmpWPath));

      // search for special elements
      // e.g. selects by label, name, id
      tmpFoundElements.addAll(tmpControlFinder.getAllOtherControls(tmpWPath));

      // clickable Text
      tmpFoundElements.addAll(tmpControlFinder.getAllControlsForText(tmpWPath));

      Control tmpControl = getRequiredFirstHtmlElementFrom(aWetContext, tmpFoundElements, tmpWPath);

      boolean tmpIsDisabled = tmpControl.hasFocus(aWetContext);
      Assert.assertTrue(tmpIsDisabled, "elementNotFocused", new String[] { tmpControl.getDescribingText() });
    }
  }

  /**
   * Command 'Open Bookmark'
   */
  public final class CommandOpenBookmark implements WetCommandImplementation {
    /**
     * {@inheritDoc}
     * 
     * @see org.rbri.wet.commandset.WetCommandImplementation#execute(org.rbri.wet.core.WetContext,
     *      org.rbri.wet.core.WetCommand)
     */
    @Override
    public void execute(WetContext aWetContext, WetCommand aWetCommand) throws AssertionFailedException {
      SecretString tmpBookmarkName = aWetCommand.getRequiredFirstParameterValue(aWetContext);
      aWetCommand.assertNoUnusedSecondParameter(aWetContext);

      WetBackend tmpBackend = getWetBackend(aWetContext);
      URL tmpUrl = tmpBackend.getBookmark(tmpBookmarkName.getValue());
      Assert.assertNotNull(tmpUrl, "unknownBookmark", new String[] { tmpBookmarkName.getValue() });

      aWetContext.informListenersInfo("openUrl", new String[] { tmpUrl.toString() });
      tmpBackend.openUrl(tmpUrl);

      tmpBackend.saveCurrentWindowToLog();
    }
  }

  /**
   * Command 'Save Bookmark'
   */
  public final class CommandSaveBookmark implements WetCommandImplementation {
    /**
     * {@inheritDoc}
     * 
     * @see org.rbri.wet.commandset.WetCommandImplementation#execute(org.rbri.wet.core.WetContext,
     *      org.rbri.wet.core.WetCommand)
     */
    @Override
    public void execute(WetContext aWetContext, WetCommand aWetCommand) throws AssertionFailedException {
      SecretString tmpBookmarkName = aWetCommand.getRequiredFirstParameterValue(aWetContext);
      aWetCommand.assertNoUnusedSecondParameter(aWetContext);

      WetBackend tmpBackend = getWetBackend(aWetContext);
      tmpBackend.bookmarkPage(tmpBookmarkName.getValue());
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.commandset.WetCommandSet#initialize(java.util.Properties)
   */
  @Override
  public void initialize(Properties aConfiguration) {
    // nothing to do at the moment
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.commandset.WetCommandSet#cleanup()
   */
  @Override
  public void cleanup() {
    // nothing to do at the moment
  }
}
