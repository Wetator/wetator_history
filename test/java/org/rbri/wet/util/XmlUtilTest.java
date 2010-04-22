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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author rbri
 */
public class XmlUtilTest extends TestCase {

    public XmlUtilTest(String name) {
        super(name);
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        return new TestSuite(XmlUtilTest.class);
    }

    public void testNormalizeBodyValue_NormalChars() {
        assertEquals("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ", XmlUtil.normalizeBodyValue("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"));
    }

    public void testNormalizeBodyValue_SpecialChars() {
        assertEquals("&lt;&gt;&amp;", XmlUtil.normalizeBodyValue("<>&"));
    }

    public void testNormalizeBodyValue_ForbiddenChars() {
        String tmpInput = "\u0000\u0001\u0002\u0003\u0004\u0005\u0006\u0007\u0008\u0009\r\u000B\u000C\n\u000E\u000F"
                + "\u0010\u0011\u0012\u0013\u0014\u0015\u0016\u0017\u0018\u0019\u001A\u001B\u001C\u001D\u001E\u001F" + "\u0020\u0010";

        assertEquals("\t\r\n ", XmlUtil.normalizeBodyValue(tmpInput));
    }

    public void testNormalizeBodyValue_Empty() {
        assertEquals("", XmlUtil.normalizeBodyValue(null));
    }

    public void testNormalizeBody_EscapeMany() {
        assertEquals("ab&lt;de&lt;", XmlUtil.normalizeBodyValue("ab<de<"));
    }

    public void testNormalizeBody_EscapeOne() {
        assertEquals("ab&gt;de", XmlUtil.normalizeBodyValue("ab>de"));
    }

    public void testNormalizeBody_EscapeAmpersand() {
        assertEquals("ab&amp;de", XmlUtil.normalizeBodyValue("ab&de"));
    }

    public void testNormalizeBodyValueChar_Empty() {
        assertEquals("", XmlUtil.normalizeBodyValue(null));
    }

    public void testNormalizeAttributeValue_NormalChars() {
        assertEquals("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ", XmlUtil.normalizeAttributeValue("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"));
    }

    public void testNormalizeAttributeValue_SpecialChars() {
        assertEquals("&lt;&gt;&amp;&quot;&apos;", XmlUtil.normalizeAttributeValue("<>&\"'"));
    }

    public void testNormalizeAttributeValue_ForbiddenChars() {
        String tmpInput = "\u0000\u0001\u0002\u0003\u0004\u0005\u0006\u0007\u0008\u0009\r\u000B\u000C\n\u000E\u000F"
                + "\u0010\u0011\u0012\u0013\u0014\u0015\u0016\u0017\u0018\u0019\u001A\u001B\u001C\u001D\u001E\u001F" + "\u0020\u0010";

        assertEquals("\t\r\n ", XmlUtil.normalizeAttributeValue(tmpInput));
    }

    public void testNormalizeAttributeValue_EscapeMany() {
        assertEquals("ab&gt;de&lt;", XmlUtil.normalizeAttributeValue("ab>de<"));
    }

    public void testNormalizeAttributeValue_EscapeOne() {
        assertEquals("ab&amp;de", XmlUtil.normalizeAttributeValue("ab&de"));
    }

    public void testNormalizeAttributeValue_EscapeQuote() {
        assertEquals("ab&quot;de", XmlUtil.normalizeAttributeValue("ab\"de"));
    }

    public void testNormalizeAttributeValue_EscapeApostroph() {
        assertEquals("ab&apos;de", XmlUtil.normalizeAttributeValue("ab'de"));
    }

    public void testNormalizeAttributeValue_Null() {
        assertEquals("", XmlUtil.normalizeAttributeValue(null));
    }

    public void testForEmma() {
        new XmlUtilEmma();
    }

    static class XmlUtilEmma extends XmlUtil {
        protected XmlUtilEmma() {
            super();
        }
    }
}
