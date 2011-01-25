/*
 * Copyright (c) 2008-2011 wetator.org
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


package org.wetator.scripter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.lang.StringUtils;
import org.wetator.core.Parameter;
import org.wetator.core.WetCommand;
import org.wetator.exception.WetException;

/**
 * Scripter for XML files.
 * 
 * @author tobwoerk
 */
public final class XmlScripter implements WetScripter {

  private static final String WET_FILE_EXTENSION = ".wet";
  private static final String XML_FILE_EXTENSION = ".xml";

  /**
   * The element name for test step.
   */
  public static final String E_STEP = "step";
  /**
   * The element name for optional parameter.
   */
  public static final String E_OPTIONAL_PARAMETER = "optionalParameter";
  /**
   * The element name for second optional parameter.
   */
  public static final String E_OPTIONAL_PARAMETER2 = "optionalParameter2";
  /**
   * The attribute name for command.
   */
  public static final String A_COMMAND = "command";
  /**
   * The attribute name for comment.
   */
  public static final String A_COMMENT = "comment";

  private File file;
  private InputStream inputStream;
  private XMLStreamReader reader;

  private List<WetCommand> commands;

  /**
   * Standard constructor.
   */
  public XmlScripter() {
    super();
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.scripter.WetScripter#setFile(java.io.File)
   */
  @Override
  public void setFile(final File aFile) throws WetException {
    file = aFile;

    commands = readCommands();
  }

  /**
   * @return the file
   */
  public File getFile() {
    return file;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.scripter.WetScripter#isSupported(java.io.File)
   */
  @Override
  public boolean isSupported(final File aFile) {
    String tmpFileName;
    boolean tmpResult;

    tmpFileName = aFile.getName().toLowerCase();
    tmpResult = tmpFileName.endsWith(WET_FILE_EXTENSION) || tmpFileName.endsWith(XML_FILE_EXTENSION);

    return tmpResult;
  }

  private List<WetCommand> readCommands() throws WetException {
    final List<WetCommand> tmpResult = new LinkedList<WetCommand>();

    try {
      inputStream = new FileInputStream(file);
    } catch (final FileNotFoundException e) {
      throw new WetException("File '" + getFile().getAbsolutePath() + "' not available.", e);
    }
    final XMLInputFactory tmpFactory = XMLInputFactory.newInstance();
    try {
      reader = tmpFactory.createXMLStreamReader(inputStream);
    } catch (final XMLStreamException e) {
      throw new WetException("Error creating reader for file '" + getFile().getAbsolutePath() + "'.", e);
    }

    try {
      WetCommand tmpWetCommand = null;
      while (reader.hasNext()) {
        if (reader.next() == XMLStreamConstants.START_ELEMENT) {
          if (E_STEP.equals(reader.getLocalName())) {
            final String tmpCommandName = reader.getAttributeValue(null, A_COMMAND).replace('_', ' ');

            // comment handling
            boolean tmpIsComment = A_COMMENT.equals(tmpCommandName.toLowerCase());
            if (!tmpIsComment) {
              final String tmpIsCommentAsString = reader.getAttributeValue(null, A_COMMENT);
              if (StringUtils.isNotEmpty(tmpIsCommentAsString)) {
                tmpIsComment = Boolean.valueOf(tmpIsCommentAsString).booleanValue();
              }
            }

            // build WetCommand
            tmpWetCommand = new WetCommand(tmpCommandName, tmpIsComment);
            tmpWetCommand.setLineNo(tmpResult.size() + 1);

            // go to CHARACTER event (parameter) if there
            final StringBuilder tmpParameters = new StringBuilder("");
            while (reader.next() == XMLStreamConstants.CHARACTERS) {
              tmpParameters.append(reader.getText());
            }
            if (!"".equals(tmpParameters)) {
              tmpWetCommand.setFirstParameter(new Parameter(tmpParameters.toString()));
            }
          }
          if (E_OPTIONAL_PARAMETER.equals(reader.getLocalName())) {
            final String tmpOptionalParameter = reader.getElementText();
            if (null == tmpWetCommand) {
              throw new WetException("Error parsing file '" + getFile().getAbsolutePath()
                  + "'. Unexpected optional parameter '" + tmpOptionalParameter + "'.");
            }

            tmpWetCommand.setSecondParameter(new Parameter(tmpOptionalParameter));
            reader.next();
          }
          if (E_OPTIONAL_PARAMETER2.equals(reader.getLocalName())) {
            final String tmpOptionalParameter = reader.getElementText();
            if (null == tmpWetCommand) {
              throw new WetException("Error parsing file '" + getFile().getAbsolutePath()
                  + "'. Unexpected optional parameter 2 '" + tmpOptionalParameter + "'.");
            }

            tmpWetCommand.setThirdParameter(new Parameter(tmpOptionalParameter));
            reader.next();
          }
        }
        if (reader.getEventType() == XMLStreamConstants.END_ELEMENT && E_STEP.equals(reader.getLocalName())) {
          tmpResult.add(tmpWetCommand);
        }
      }

      return tmpResult;
    } catch (final XMLStreamException e) {
      throw new WetException("Error parsing file '" + getFile().getAbsolutePath() + "' (" + e.getMessage() + ").", e);
    } finally {
      try {
        reader.close();
        inputStream.close();
      } catch (final Exception e) {
        throw new WetException("Problem closing resources for file '" + getFile().getAbsolutePath() + "'.", e);
      }
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.scripter.WetScripter#getCommands()
   */
  @Override
  public List<WetCommand> getCommands() {
    return commands;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.scripter.WetScripter#initialize(java.util.Properties)
   */
  @Override
  public void initialize(final Properties aConfiguration) {
    // nothing to do
  }
}
