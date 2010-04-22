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


package org.rbri.wet.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author rbri
 */
public class ContentUtilTest extends TestCase {

    public ContentUtilTest(String name) {
        super(name);
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        return new TestSuite(ContentUtilTest.class);
    }

    public void testGetPdfContentAsString() throws FileNotFoundException, IOException {
    	StringBuilder tmpExpected = new StringBuilder();
    	tmpExpected.append("This is the content of a simple PDF file.");
    	tmpExpected.append("\r\n");
    	tmpExpected.append("This file is used to test WeT.");
    	tmpExpected.append("\r\n");

    	String tmpContent = ContentUtil.getPdfContentAsString(new FileInputStream("webpages/testcases/download/wet_test.pdf"));
        assertEquals(tmpExpected.toString(), tmpContent);
    }

    public void testGetXlsContentAsString() throws FileNotFoundException, IOException {
    	StringBuilder tmpExpected = new StringBuilder();
    	tmpExpected.append("[Tab1]");
    	tmpExpected.append("\n");
    	tmpExpected.append("Wetator");
    	tmpExpected.append("\n");
    	tmpExpected.append("[Tab2]");
    	tmpExpected.append("\n");
    	tmpExpected.append("Wetator Test");
    	tmpExpected.append("\n");
    	tmpExpected.append("[Tab2]");
    	tmpExpected.append("\n");
    	tmpExpected.append("[Tab2]");

    	String tmpContent = ContentUtil.getXlsContentAsString(new FileInputStream("webpages/testcases/download/wet_test.xls"));
        assertEquals(tmpExpected.toString(), tmpContent);
    }

}
