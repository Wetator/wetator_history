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

/**
 * XmlUtil contains some useful helpers for XML-File handling.
 * 
 * @author rbri
 */
public class XmlUtil {

    /**
     * Escape the the given string. For use as body text.<br>
     * Sample: <code>normalizeBodyValue("&lt;\\abc&gt;")</code> returns
     * <code>"&amp;lt;\abc&amp;gt;"</code>
     * 
     * @param aString the String to be normalized or null
     * @return a new String
     */
    public static String normalizeBodyValue(String aString) {
        StringBuffer tmpResult = null;
        int tmpLength;
        char tmpChar;

        if (aString == null) {
            return "";
        }

        tmpLength = aString.length();

        // we have some kind of optimization here
        // if there is no special character inside, then we
        // are returning the original one
        int i = 0;
        while (i < tmpLength) {
            tmpChar = aString.charAt(i);

            if ((int)tmpChar < 32 && (int)tmpChar != 9 && (int)tmpChar != 10 && (int)tmpChar != 13) {
                // ignore
                tmpResult = new StringBuffer(aString.substring(0, i));
                i++;
                break;
            } else if (tmpChar == '<') {
                tmpResult = new StringBuffer(aString.substring(0, i));
                tmpResult.append("&lt;");
                i++;
                break;
            } else if (tmpChar == '>') {
                tmpResult = new StringBuffer(aString.substring(0, i));
                tmpResult.append("&gt;");
                i++;
                break;
            } else if (tmpChar == '&') {
                tmpResult = new StringBuffer(aString.substring(0, i));
                tmpResult.append("&amp;");
                i++;
                break;
            }
            i++;
        }

        while (i < tmpLength) {
            tmpChar = aString.charAt(i);

            if ((int)tmpChar > 31 || (int)tmpChar == 9 || (int)tmpChar == 10 || (int)tmpChar == 13) {

                switch (tmpChar) {
                case '<': {
                    tmpResult.append("&lt;");
                    break;
                }
                case '>': {
                    tmpResult.append("&gt;");
                    break;
                }
                case '&': {
                    tmpResult.append("&amp;");
                    break;
                }

                default: {
                    tmpResult.append(tmpChar);
                }
                }
            }
            i++;
        }

        if (null == tmpResult) {
            return aString;
        }
        return tmpResult.toString();
    }

    /**
     * Escape the <code>toString</code> of the given String. For use in an
     * attribute value.<br>
     * Sample: <code>normalizeBodyValue("&lt;\\abc&gt;")</code> returns
     * <code>&amp;lt;&amp;apos;abc&amp;gt;</code>
     * 
     * @param aString
     *            the String to be normalized or null
     * 
     * @return a new String
     */
    public static String normalizeAttributeValue(String aString) {
        StringBuffer tmpResult = null;
        int tmpLength;
        char tmpChar;

        if (aString == null) {
            return "";
        }

        tmpLength = aString.length();

        int i = 0;
        for (; i < tmpLength; i++) {
            tmpChar = aString.charAt(i);

            if ((int) tmpChar < 32 && (int) tmpChar != 9 && (int) tmpChar != 10 && (int) tmpChar != 13) {
                // ignore
                tmpResult = new StringBuffer(aString.substring(0, i));
                i++;
                break;
            } else if (tmpChar == '<') {
                tmpResult = new StringBuffer(aString.substring(0, i));
                tmpResult.append("&lt;");
                i++;
                break;
            } else if (tmpChar == '>') {
                tmpResult = new StringBuffer(aString.substring(0, i));
                tmpResult.append("&gt;");
                i++;
                break;
            } else if (tmpChar == '&') {
                tmpResult = new StringBuffer(aString.substring(0, i));
                tmpResult.append("&amp;");
                i++;
                break;
            } else if (tmpChar == '\'') {
                tmpResult = new StringBuffer(aString.substring(0, i));
                tmpResult.append("&apos;");
                i++;
                break;
            } else if (tmpChar == '"') {
                tmpResult = new StringBuffer(aString.substring(0, i));
                tmpResult.append("&quot;");
                i++;
                break;
            }
        }

        for (; i < tmpLength; i++) {
            tmpChar = aString.charAt(i);

            if ((int) tmpChar > 31 || (int) tmpChar == 9 || (int) tmpChar == 10 || (int) tmpChar == 13) {

                switch (tmpChar) {
                case '<': {
                    tmpResult.append("&lt;");
                    break;
                }
                case '>': {
                    tmpResult.append("&gt;");
                    break;
                }
                case '&': {
                    tmpResult.append("&amp;");
                    break;
                }
                case '\'': {
                    tmpResult.append("&apos;");
                    break;
                }
                case '"': {
                    tmpResult.append("&quot;");
                    break;
                }

                default: {
                    tmpResult.append(tmpChar);
                }
                }
            }
        }

        if (null == tmpResult) {
            return aString;
        }
        return tmpResult.toString();
    }
}
