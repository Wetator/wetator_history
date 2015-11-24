/*
 * Copyright (c) 2008-2015 wetator.org
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


package org.wetator.exception;

/**
 * This exception is thrown for problems caused by invalid user input.
 * 
 * @author frank.danek
 * @author tobwoerk
 */
public class InvalidInputException extends Exception {

  private static final long serialVersionUID = -9053664552481228065L;

  /**
   * The constructor.
   * 
   * @param aMessage the message text
   */
  public InvalidInputException(final String aMessage) {
    super(aMessage);
  }

  /**
   * The constructor.
   * 
   * @param aMessage the message text
   * @param aThrowable the reason of this exception
   */
  public InvalidInputException(final String aMessage, final Throwable aThrowable) {
    super(aMessage, aThrowable);
  }
}
