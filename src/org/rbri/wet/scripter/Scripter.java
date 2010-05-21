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
    XML (new XmlScripter()),
    /**
     * Excel
     */
    XSL (new ExcelScripter());

    private WetScripter wetScripter;

    private Scripter(WetScripter aWetScripter) {
    	wetScripter = aWetScripter;
    }

	public WetScripter getWetScripter() {
		return wetScripter;
	}

}
