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

import org.rbri.wet.exception.WetException;


/**
 * Script creator enum.
 *
 * @author tobwoerk
 */
public enum ScriptCreator
{
    /**
     * XML
     */
    XML ("xml");

    private ScriptCreator(String aName)
    {
        name = aName;
    }

    /**
     * @see java.lang.Enum#toString()
     */
    @Override
	public String toString()
    {
        return name;
    }

    private String name;

    /**
     * @return the according script creator
     */
    public WetScriptCreator get()
    {
        switch(this)
        {
            case XML: return new XmlScriptCreator();
            default: return null;
        }
    }

    /**
     * @param aName the file extension to get a script creator for
     * @return an according script creator
     * @throws WetException if no script creator found for aName
     */
    public static WetScriptCreator get(String aName) throws WetException {
    	if (XML.name.equals(aName)) {
    		return XML.get();
    	}
    	throw new WetException("No script creator found for file extension ." + aName + ".");
    }
}
