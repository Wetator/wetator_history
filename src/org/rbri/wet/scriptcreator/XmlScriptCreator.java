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


package org.rbri.wet.scriptcreator;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

import org.rbri.wet.core.WetCommand;
import org.rbri.wet.exception.WetException;
import org.rbri.wet.scripter.XmlScripter;

/**
 * Creates a Wetator test script in XML format from the given commands<br/>
 * with the given file name and DTD in the given output directory.
 * 
 * @author tobwoerk
 */
public class XmlScriptCreator implements WetScriptCreator {

  private List<WetCommand> commands;
  private String fileName;
  private String dtd;
  private File outputDir;

  private static final String R_TEST_CASE = "testcase";

  private static final String ENCODING = "UTF-8";
  private static final String VERSION = "1.0";

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.scriptcreator.WetScriptCreator#createScript()
   */
  @Override
  public void createScript() throws WetException {
    XMLOutputFactory tmpFactory = XMLOutputFactory.newInstance();
    try {
      File tmpFile = new File(outputDir, fileName + ".xml");
      XMLStreamWriter tmpWriter = tmpFactory.createXMLStreamWriter(new FileOutputStream(tmpFile), ENCODING);

      tmpWriter.writeStartDocument(ENCODING, VERSION);
      tmpWriter.writeCharacters("\n");
      if (null != dtd) {
        tmpWriter.writeDTD("<!DOCTYPE " + R_TEST_CASE + " " + dtd + ">");
        tmpWriter.writeCharacters("\n");
      }
      tmpWriter.writeCharacters("\n");
      tmpWriter.writeStartElement(R_TEST_CASE);
      tmpWriter.writeCharacters("\n");
      for (WetCommand tmpCommand : commands) {
        tmpWriter.writeCharacters("    ");
        tmpWriter.writeStartElement(XmlScripter.E_STEP);
        tmpWriter.writeAttribute(XmlScripter.A_COMMAND, tmpCommand.getName().replace(' ', '_'));
        if (tmpCommand.isComment() && !"Comment".equals(tmpCommand.getName())) {
          tmpWriter.writeAttribute(XmlScripter.A_COMMENT, "true");
        }
        if (tmpCommand.getFirstParameter() != null) {
          final String tmpCharacterDataPattern = ".*[<>&]";
          String tmpParameter = tmpCommand.getFirstParameter().getValue();
          if (tmpParameter.matches(tmpCharacterDataPattern)) {
            tmpWriter.writeCData(tmpParameter);
          } else {
            tmpWriter.writeCharacters(tmpParameter);
          }
          if (tmpCommand.getSecondParameter() != null) {
            tmpWriter.writeStartElement(XmlScripter.E_OPTIONAL_PARAMETER);
            String tmpOptionalParameter = tmpCommand.getSecondParameter().getValue();
            if (tmpOptionalParameter.matches(tmpCharacterDataPattern)) {
              tmpWriter.writeCData(tmpOptionalParameter);
            } else {
              tmpWriter.writeCharacters(tmpOptionalParameter);
            }
            tmpWriter.writeEndElement();
          }
        }
        tmpWriter.writeEndElement();
        tmpWriter.writeCharacters("\n");
      }
      tmpWriter.writeEndElement();
      tmpWriter.writeEndDocument();
      tmpWriter.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.scriptcreator.WetScriptCreator#setCommands(java.util.List)
   */
  @Override
  public void setCommands(List<WetCommand> aCommandList) throws WetException {
    commands = aCommandList;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.scriptcreator.WetScriptCreator#setFileName(java.lang.String)
   */
  @Override
  public void setFileName(String aFileName) {
    fileName = aFileName;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.scriptcreator.WetScriptCreator#setOutputDir(java.lang.String)
   */
  @Override
  public void setOutputDir(String anOutputDir) {
    outputDir = new File(anOutputDir);
  }

  /**
   * @param aDtd
   *        the DTD to set (name only expected, including keyword)
   */
  public void setDtd(String aDtd) {
    dtd = aDtd;
  }
}
