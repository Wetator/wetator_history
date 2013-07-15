/*
 * Copyright (c) 2008-2013 wetator.org
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


package org.wetator.util;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.wetator.core.Variable;

/**
 * @author rbri
 */
public class VariableReplaceUtilUtilTest {

  @Test
  public void replaceVariable_null() {
    String tmpStringWithPlaceholders;
    List<Variable> tmpVariables;

    tmpStringWithPlaceholders = null;
    tmpVariables = null;

    org.junit.Assert.assertNull(VariableReplaceUtil.replaceVariables(tmpStringWithPlaceholders, tmpVariables, false));
  }

  @Test
  public void replaceVariable_Empty_null() {
    String tmpStringWithPlaceholders;
    List<Variable> tmpVariables;

    tmpStringWithPlaceholders = "";
    tmpVariables = null;

    org.junit.Assert.assertEquals("",
        VariableReplaceUtil.replaceVariables(tmpStringWithPlaceholders, tmpVariables, false));
  }

  @Test
  public void replaceVariable_Static_null() {
    String tmpStringWithPlaceholders;
    List<Variable> tmpVariables;

    tmpStringWithPlaceholders = "abc";
    tmpVariables = null;

    org.junit.Assert.assertEquals("abc",
        VariableReplaceUtil.replaceVariables(tmpStringWithPlaceholders, tmpVariables, false));
  }

  @Test
  public void replaceVariable_OneVar_null() {
    String tmpStringWithPlaceholders;
    List<Variable> tmpVariables;

    tmpStringWithPlaceholders = "a ${var0} bc";
    tmpVariables = null;

    org.junit.Assert.assertEquals("a ${var0} bc",
        VariableReplaceUtil.replaceVariables(tmpStringWithPlaceholders, tmpVariables, false));
  }

  @Test
  public void replaceVariable_OneVar_Unknown() {
    List<Variable> tmpVariables = new LinkedList<Variable>();
    Variable tmpVariable = new Variable("var0", "value0", false);
    tmpVariables.add(tmpVariable);

    String tmpStringWithPlaceholders = "a ${unknown} bc";

    org.junit.Assert.assertEquals("a ${unknown} bc",
        VariableReplaceUtil.replaceVariables(tmpStringWithPlaceholders, tmpVariables, false));
  }

  @Test
  public void replaceVariable_OneStartSeq_null() {
    String tmpStringWithPlaceholders;
    List<Variable> tmpVariables;

    tmpStringWithPlaceholders = "a ${var0bc";
    tmpVariables = null;

    org.junit.Assert.assertEquals("a ${var0bc",
        VariableReplaceUtil.replaceVariables(tmpStringWithPlaceholders, tmpVariables, false));
  }

  @Test
  public void replaceVariable_TwoEndSeq_null() {
    String tmpStringWithPlaceholders;
    List<Variable> tmpVariables;

    tmpStringWithPlaceholders = "a ${var}}0bc";
    tmpVariables = null;

    org.junit.Assert.assertEquals("a ${var}}0bc",
        VariableReplaceUtil.replaceVariables(tmpStringWithPlaceholders, tmpVariables, false));
  }

  @Test
  public void replaceVariable_TwoVar_null() {
    String tmpStringWithPlaceholders;
    List<Variable> tmpVariables;

    tmpStringWithPlaceholders = "a ${var0} b ${var1} c";
    tmpVariables = null;

    org.junit.Assert.assertEquals("a ${var0} b ${var1} c",
        VariableReplaceUtil.replaceVariables(tmpStringWithPlaceholders, tmpVariables, false));
  }

  @Test
  public void replaceVariable_OnlyOneVar() {
    String tmpStringWithPlaceholders;
    List<Variable> tmpVariables;
    Variable tmpVariable;

    tmpStringWithPlaceholders = "${var0}";
    tmpVariables = new LinkedList<Variable>();
    tmpVariable = new Variable("var0", "value0", false);
    tmpVariables.add(tmpVariable);

    org.junit.Assert.assertEquals("value0",
        VariableReplaceUtil.replaceVariables(tmpStringWithPlaceholders, tmpVariables, false));
  }

  @Test
  public void replaceVariable_VarAtStart() {
    String tmpStringWithPlaceholders;
    List<Variable> tmpVariables;
    Variable tmpVariable;

    tmpStringWithPlaceholders = "${var0}abc";
    tmpVariables = new LinkedList<Variable>();
    tmpVariable = new Variable("var0", "value0", false);
    tmpVariables.add(tmpVariable);

    org.junit.Assert.assertEquals("value0abc",
        VariableReplaceUtil.replaceVariables(tmpStringWithPlaceholders, tmpVariables, false));
  }

  @Test
  public void replaceVariable_VarAtEnd() {
    String tmpStringWithPlaceholders;
    List<Variable> tmpVariables;
    Variable tmpVariable;

    tmpStringWithPlaceholders = "${var0}de";
    tmpVariables = new LinkedList<Variable>();
    tmpVariable = new Variable("var0", "value0", false);
    tmpVariables.add(tmpVariable);

    org.junit.Assert.assertEquals("value0de",
        VariableReplaceUtil.replaceVariables(tmpStringWithPlaceholders, tmpVariables, false));
  }

  @Test
  public void replaceVariable_TwoVars() {
    String tmpStringWithPlaceholders;
    List<Variable> tmpVariables;
    Variable tmpVariable;

    tmpStringWithPlaceholders = "${var0}${var1}";
    tmpVariables = new LinkedList<Variable>();
    tmpVariable = new Variable("var0", "value0", false);
    tmpVariables.add(tmpVariable);
    tmpVariable = new Variable("var1", "value1", false);
    tmpVariables.add(tmpVariable);

    org.junit.Assert.assertEquals("value0value1",
        VariableReplaceUtil.replaceVariables(tmpStringWithPlaceholders, tmpVariables, false));
  }

  @Test
  public void replaceVariable_ReusedVars() {
    String tmpStringWithPlaceholders;
    List<Variable> tmpVariables;
    Variable tmpVariable;

    tmpStringWithPlaceholders = "${var0}${var1} ${var0}";
    tmpVariables = new LinkedList<Variable>();
    tmpVariable = new Variable("var0", "value0", false);
    tmpVariables.add(tmpVariable);
    tmpVariable = new Variable("var1", "value1", false);
    tmpVariables.add(tmpVariable);

    org.junit.Assert.assertEquals("value0value1 value0",
        VariableReplaceUtil.replaceVariables(tmpStringWithPlaceholders, tmpVariables, false));
  }

  @Test
  public void replaceVariable_VarMagic() {
    String tmpStringWithPlaceholders;
    List<Variable> tmpVariables;
    Variable tmpVariable;

    tmpStringWithPlaceholders = "${var0}";
    tmpVariables = new LinkedList<Variable>();
    tmpVariable = new Variable("var0", "${var1}", false);
    tmpVariables.add(tmpVariable);
    tmpVariable = new Variable("var1", "value1", false);
    tmpVariables.add(tmpVariable);

    org.junit.Assert.assertEquals("value1",
        VariableReplaceUtil.replaceVariables(tmpStringWithPlaceholders, tmpVariables, false));
  }
}
