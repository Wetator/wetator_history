/*
 * Copyright (c) 2008-2021 wetator.org
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


package org.wetator.core;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

import org.wetator.exception.InvalidInputException;
import org.wetator.i18n.Messages;
import org.wetator.util.SecretString;

/**
 * The Command;
 * our class that represents a command read from an source file.
 *
 * @author rbri
 * @author frank.danek
 */
public final class Command {
  private String name;
  private boolean comment;
  private Parameter firstParameter;
  private Parameter secondParameter;
  private Parameter thirdParameter;
  private int lineNo;

  /**
   * The constructor.
   *
   * @param aName the name of the command
   * @param anIsCommentFlag true if the command is a comment
   */
  public Command(final String aName, final boolean anIsCommentFlag) {
    name = aName;
    comment = anIsCommentFlag;

    // debug
    lineNo = -1;
  }

  /**
   * @return the lineNo
   */
  public int getLineNo() {
    return lineNo;
  }

  /**
   * @param aLineNo the lineNo to set
   */
  public void setLineNo(final int aLineNo) {
    lineNo = aLineNo;
  }

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * @return the isComment flag
   */
  public boolean isComment() {
    return comment;
  }

  /**
   * @return the firstParameter
   */
  public Parameter getFirstParameter() {
    return firstParameter;
  }

  /**
   * @param aFirstParameter the firstParameter to set
   */
  public void setFirstParameter(final Parameter aFirstParameter) {
    firstParameter = aFirstParameter;
  }

  /**
   * @return the secondParameter
   */
  public Parameter getSecondParameter() {
    return secondParameter;
  }

  /**
   * @param aSecondParameter the secondParameter to set
   */
  public void setSecondParameter(final Parameter aSecondParameter) {
    secondParameter = aSecondParameter;
  }

  /**
   * @return the thirdParameter
   */
  public Parameter getThirdParameter() {
    return thirdParameter;
  }

  /**
   * @param aThirdParameter the thirdParameter to set
   */
  public void setThirdParameter(final Parameter aThirdParameter) {
    thirdParameter = aThirdParameter;
  }

  /**
   * Creates a printable String from the command.
   * This takes care of secrets.
   *
   * @param aContext the context
   * @return the string
   */
  public String toPrintableString(final WetatorContext aContext) {
    final StringBuilder tmpResult = new StringBuilder("[Command '").append(getName()).append('\'');
    if (comment) {
      tmpResult.append(" COMMENT");
    }

    Parameter tmpParameter = getFirstParameter();
    tmpResult.append(" firstParam: '");
    if (null != tmpParameter) {
      tmpResult.append(tmpParameter.getValue(aContext).toString());
    }
    tmpResult.append('\''); // NOPMD

    tmpParameter = getSecondParameter();
    tmpResult.append(" '");
    if (null != tmpParameter) {
      tmpResult.append(getSecondParameter().getValue(aContext).toString());
    }
    tmpResult.append('\'');

    tmpParameter = getThirdParameter();
    if (null != tmpParameter) {
      tmpResult.append(" '").append(getThirdParameter().getValue(aContext).toString()).append('\'');
    }

    tmpResult.append(']');
    return tmpResult.toString();
  }

  /**
   * Returns the first parameter as secret string.
   * The parameter is taken at whole, not parsed.
   *
   * @param aContext the context
   * @return a secret string or null if the first parameter was not set
   */
  public SecretString getFirstParameterValue(final WetatorContext aContext) {
    final Parameter tmpFirstParameter = getFirstParameter();

    if (null == tmpFirstParameter) {
      return new SecretString();
    }

    return tmpFirstParameter.getValue(aContext);
  }

  /**
   * Returns the first parameter as secret string.
   * The parameter is taken at whole, not parsed.
   *
   * @param aContext the context
   * @return a secret string
   * @throws InvalidInputException if the first parameter is not set
   */
  public SecretString getRequiredFirstParameterValue(final WetatorContext aContext) throws InvalidInputException {
    final Parameter tmpFirstParameter = getFirstParameter();

    if (null == tmpFirstParameter) {
      invalidInput("emptyFirstParameter", getName());
    }

    return tmpFirstParameter.getValue(aContext);
  }

  /**
   * Returns the list of secret strings parsed from the second parameter.
   *
   * @param aContext the context
   * @return the list of secret strings (never null)
   */
  public List<SecretString> getSecondParameterValues(final WetatorContext aContext) {
    final Parameter tmpSecondParameter = getSecondParameter();

    final List<SecretString> tmpResult = new LinkedList<>();
    if (null == tmpSecondParameter) {
      return tmpResult;
    }

    final List<Parameter.Part> tmpParts = tmpSecondParameter.getParts();

    for (final Parameter.Part tmpPart : tmpParts) {
      tmpResult.add(tmpPart.getValue(aContext));
    }

    return tmpResult;
  }

  /**
   * Returns the second parameter as secret string.
   * The parameter is taken at whole, not parsed.
   *
   * @param aContext the context
   * @return a secret string or null if the second parameter was not set
   */
  public SecretString getSecondParameterValue(final WetatorContext aContext) {
    final Parameter tmpSecondParameter = getSecondParameter();

    if (null == tmpSecondParameter) {
      return null;
    }

    return tmpSecondParameter.getValue(aContext);
  }

  /**
   * Returns the second parameter as long.
   *
   * @param aContext the context
   * @return a Long (or null if not set)
   * @throws InvalidInputException if the second parameter is not convertible into a long
   */
  public Long getSecondParameterLongValue(final WetatorContext aContext) throws InvalidInputException {
    final Parameter tmpSecondParameter = getSecondParameter();

    if (null == tmpSecondParameter) {
      return null;
    }

    final SecretString tmpSecondValue = tmpSecondParameter.getValue(aContext);
    if (tmpSecondValue.isEmpty()) {
      return null;
    }

    try {
      final BigDecimal tmpValue = new BigDecimal(tmpSecondValue.getValue());
      return Long.valueOf(tmpValue.longValueExact());
    } catch (final NumberFormatException | ArithmeticException e) {
      invalidInput("integerParameterExpected", getName(), tmpSecondParameter.getValue(aContext).toString(), "2");
    }
    return null;
  }

  /**
   * Returns the list of secret strings parsed from the second parameter.
   *
   * @param aContext the context
   * @return the list of secret strings (never null)
   * @throws InvalidInputException if the second parameter is not set
   */
  public List<SecretString> getRequiredSecondParameterValues(final WetatorContext aContext)
      throws InvalidInputException {
    final Parameter tmpSecondParameter = getSecondParameter();

    if (null == tmpSecondParameter) {
      invalidInput("emptySecondParameter", getName());
    }

    return getSecondParameterValues(aContext);
  }

  /**
   * Returns the second parameter as secret string.
   * The parameter is taken at whole, not parsed.
   *
   * @param aContext the context
   * @return a secret string
   * @throws InvalidInputException if the second parameter is not set
   */
  public SecretString getRequiredSecondParameterValue(final WetatorContext aContext) throws InvalidInputException {
    final Parameter tmpSecondParameter = getSecondParameter();

    if (null == tmpSecondParameter) {
      invalidInput("emptySecondParameter", getName());
    }

    return tmpSecondParameter.getValue(aContext);
  }

  /**
   * Asserts that the second parameter is not set.
   *
   * @param aContext the context
   * @throws InvalidInputException if the second parameter is set
   */
  public void checkNoUnusedSecondParameter(final WetatorContext aContext) throws InvalidInputException {
    final Parameter tmpParameter = getSecondParameter();
    if (null != tmpParameter) {
      invalidInput("unusedParameter", getName(), tmpParameter.getValue(aContext).toString(), "2");
    }
  }

  /**
   * Asserts that the third parameter is not set.
   *
   * @param aContext the context
   * @throws InvalidInputException if the third parameter is set
   */
  public void checkNoUnusedThirdParameter(final WetatorContext aContext) throws InvalidInputException {
    final Parameter tmpParameter = getThirdParameter();
    if (null != tmpParameter) {
      invalidInput("unusedParameter", getName(), tmpParameter.getValue(aContext).toString(), "3");
    }
  }

  private static void invalidInput(final String aMessageKey, final Object... aParameters) throws InvalidInputException {
    final String tmpMessage = Messages.getMessage(aMessageKey, aParameters);
    throw new InvalidInputException(tmpMessage);
  }
}