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

import org.rbri.wet.exception.WetException;


/**
 * Scripter enum.
 *
 * @author tobwoerk
 */
public enum Scripter
{
    /**
     * XML
     */
    XML ("xml"),
    /**
     * Excel
     */
    XSL ("xsl");

    private Scripter(String aName)
    {
        name = aName;
    }

    private String name;

    /**
     * @see java.lang.Enum#toString()
     */
    @Override
	public String toString()
    {
        return name;
    }

    /**
     * @return the according script creator
     */
    public WetScripter get()
    {
        switch(this)
        {
            case XML: return new XmlScripter();
            case XSL: return new ExcelScripter();
            default: return null;
        }
    }

    /**
     * @param aName the file extension to get a scripter for
     * @return an according scripter
     * @throws WetException if no scripter found for aName
     */
    public static WetScripter get(String aName) throws WetException {
    	if (XML.name.equals(aName)) {
    		return XML.get();
    	}
    	if (XSL.name.equals(aName)) {
    		return XSL.get();
    	}
    	throw new WetException("No scripter found for file extension ." + aName + ".");
    }
}
