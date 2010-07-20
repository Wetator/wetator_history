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
public class AllTests extends TestCase {
  public static void main(String[] anArgsArray) {
    junit.textui.TestRunner.run(suite());
  }

  public static Test suite() {

    TestSuite tmpSuite = new TestSuite("All Wetator util tests");

    tmpSuite.addTest(AssertTest.suite());
    tmpSuite.addTest(ContentUtilTest.suite());
    tmpSuite.addTest(NormalizedStringTest.suite());
    tmpSuite.addTest(SearchPatternTest.suite());
    tmpSuite.addTest(StringUtilTest.suite());
    tmpSuite.addTest(VariableReplaceUtilUtilTest.suite());
    tmpSuite.addTest(XmlUtilTest.suite());

    return tmpSuite;
  }
}
