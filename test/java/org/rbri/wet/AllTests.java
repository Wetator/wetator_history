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


package org.rbri.wet;

import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;
import junit.framework.TestCase;

import org.junit.runner.JUnitCore;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * @author rbri
 * @author frank.danek
 */
@RunWith(Suite.class)
@SuiteClasses( { org.rbri.wet.backend.AllTests.class, //
    org.rbri.wet.commandset.AllTests.class, //
    org.rbri.wet.core.AllTests.class, //
    org.rbri.wet.i18n.AllTests.class, //
    org.rbri.wet.scripter.AllTests.class, //
    org.rbri.wet.util.AllTests.class })
public class AllTests extends TestCase {

  /**
   * @param anArgsArray ignored
   */
  public static void main(String[] anArgsArray) {
    System.out.println(org.rbri.wet.Version.getFullProductName());
    JUnitCore.main(AllTests.class.getName());
  }

  /**
   * @return the test suite
   */
  // TODO remove when migrated to junit4
  public static Test suite() {
    return new JUnit4TestAdapter(AllTests.class);
  }
}
