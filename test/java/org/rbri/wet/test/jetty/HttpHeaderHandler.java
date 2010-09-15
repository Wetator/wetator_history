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


package org.rbri.wet.test.jetty;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.http.HttpStatus.Code;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

/**
 * @author frank.danek
 */
public class HttpHeaderHandler extends AbstractHandler {

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.jetty.server.Handler#handle(java.lang.String, org.eclipse.jetty.server.Request,
   *      javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
   */
  @Override
  public void handle(String aTarget, Request aBaseRequest, HttpServletRequest aRequest, HttpServletResponse aResponse)
      throws IOException, ServletException {
    if (aBaseRequest.isHandled()) {
      return;
    }

    if (aTarget.endsWith("http_header.php")) {
      aBaseRequest.setHandled(true);
      String tmpCode = aBaseRequest.getParameter("code");
      Code tmpStatusCode = HttpStatus.getCode(Integer.valueOf(tmpCode));
      aResponse.sendError(tmpStatusCode.getCode(), tmpStatusCode.getMessage());
    }
  }

}
