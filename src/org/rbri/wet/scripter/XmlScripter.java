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

package org.rbri.wet.scripter;

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
import org.rbri.wet.core.Parameter;
import org.rbri.wet.core.WetCommand;
import org.rbri.wet.exception.WetException;

/**
 * Scripter for XML files
 *
 * @author tobwoerk
 */
public final class XmlScripter implements WetScripter {

	private static final String XML_FILE_EXTENSION = ".xml";

	private static final String E_STEP = "step";
	private static final String E_PARAMETER = "parameter";
	private static final String E_OPTIONAL_PARAMETER = "optionalParameter";
	private static final String A_COMMAND = "command";
	private static final String A_COMMENT = "comment";

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
	 * @throws WetException
	 *             if commands cannot be read
	 * @see WetScripter#setFile(File)
	 */
	public void setFile(File aFile) throws WetException {
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
	 * @see WetScripter#isSupported(File)
	 */
	public boolean isSupported(File aFile) {
		String tmpFileName;
		boolean tmpResult;

		tmpFileName = aFile.getName().toLowerCase();
		tmpResult = tmpFileName.endsWith(XML_FILE_EXTENSION);

		return tmpResult;
	}

	private List<WetCommand> readCommands() throws WetException {
		LinkedList<WetCommand> tmpResult = new LinkedList<WetCommand>();

		try {
			inputStream = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			throw new WetException("File '" + getFile().getAbsolutePath() + "' not available.", e);
		}
		XMLInputFactory tmpFactory = XMLInputFactory.newInstance();
		try {
			reader = tmpFactory.createXMLStreamReader(inputStream);
		} catch (XMLStreamException e) {
			throw new WetException("Error creating reader for file '" + getFile().getAbsolutePath() + "'.", e);
		}

		try {
			WetCommand tmpWetCommand = null;
			while (reader.hasNext()) {
				switch (reader.next()) {
					case XMLStreamConstants.START_ELEMENT: {
						if (E_STEP.equals(reader.getLocalName())) {
							String tmpCommandName = reader.getAttributeValue(null, A_COMMAND).replace('_', ' ');
							System.out.println(tmpCommandName);

							// comment handling
							boolean tmpIsComment = A_COMMENT.equals(tmpCommandName.toLowerCase());
							if (!tmpIsComment) {
								String tmpIsCommentAsString = reader.getAttributeValue(null, A_COMMENT);
								if (StringUtils.isNotEmpty(tmpIsCommentAsString)) {
									tmpIsComment = Boolean.valueOf(tmpIsCommentAsString).booleanValue();
								}
							}

							// build WetCommand
							tmpWetCommand = new WetCommand(tmpCommandName, tmpIsComment);
							tmpWetCommand.setLineNo(tmpResult.size() + 1);
						} else if (E_PARAMETER.equals(reader.getLocalName())) {
							String tmpParameters = reader.getElementText();
							tmpWetCommand.setFirstParameter(new Parameter(tmpParameters));
						} else if (E_OPTIONAL_PARAMETER.equals(reader.getLocalName())) {
							String tmpOptionalParameters = reader.getElementText();
							tmpWetCommand.setSecondParameter(new Parameter(tmpOptionalParameters));
						}
						break;
					}
					case XMLStreamConstants.END_ELEMENT: {
						if (E_STEP.equals(reader.getLocalName())) {
							tmpResult.add(tmpWetCommand);
						}
					}
				}
			}

			return tmpResult;
		} catch (XMLStreamException e) {
			throw new WetException("Error parsing file '" + getFile().getAbsolutePath() + "'.", e);
		} finally {
			try {
				reader.close();
				inputStream.close();
			} catch (Exception e) {
				throw new WetException("Problem closing resources for file '" + getFile().getAbsolutePath() + "'.", e);
			}
		}
	}

	/**
	 * @see org.rbri.wet.scripter.WetScripter#getCommands()
	 */
	public List<WetCommand> getCommands() {
		return commands;
	}

	/**
	 * @see org.rbri.wet.scripter.WetScripter#initialize(java.util.Properties)
	 */
	public void initialize(Properties aConfiguration) {
		// nothing to do
	}
}
