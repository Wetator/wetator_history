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

import org.eclipse.jetty.http.HttpStatus.Code;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.rbri.wet.test.AbstractWebServerTest;

/**
 * @author frank.danek
 */
public class RedirectHandler extends AbstractHandler {

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

    if (aTarget.endsWith("redirect_header.php")) {
      aBaseRequest.setHandled(true);
      aResponse.setStatus(Code.MOVED_TEMPORARILY.getCode());
      String tmpTarget = aBaseRequest.getParameter("target");
      if (tmpTarget != null && !"".equals(tmpTarget)) {
        aResponse.setHeader("Location", tmpTarget);
      } else {
        aResponse.setHeader("Location", "http://localhost:" + AbstractWebServerTest.PORT);
      }
    } else if (aTarget.endsWith("redirect_js.php")) {
      aBaseRequest.setHandled(true);
      String tmpTarget = aBaseRequest.getParameter("target");
      String tmpWait = aBaseRequest.getParameter("wait");
      aResponse.getWriter().println(
          "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"DTD/xhtml1-transitional.dtd\">");
      aResponse.getWriter().println("<html>");
      aResponse.getWriter().println("<head>");
      aResponse.getWriter().println("<script type=\"text/javascript\">");
      aResponse.getWriter().println("function redirect(){");
      if (tmpTarget != null && !"".equals(tmpTarget)) {
        aResponse.getWriter().println("window.location = '" + tmpTarget + "'");
      } else {
        aResponse.getWriter().println("window.location = 'http://localhost:" + AbstractWebServerTest.PORT + "'");
      }
      aResponse.getWriter().println("}");
      aResponse.getWriter().println("function startRedirect() {");
      if (tmpTarget != null && !"".equals(tmpTarget)) {
        aResponse.getWriter().println("setTimeout('redirect()', " + tmpWait + ");");
      } else {
        aResponse.getWriter().println("setTimeout('redirect()', 444);");
      }
      aResponse.getWriter().println("}");
      aResponse.getWriter().println("</script>");
      aResponse.getWriter().println("</head>");
      aResponse.getWriter().println("<body onLoad=\"startRedirect();\">");
      aResponse.getWriter().println("</body>");
      aResponse.getWriter().println("</html>");
    } else if (aTarget.endsWith("redirect_meta.php")) {
      aBaseRequest.setHandled(true);
      String tmpTarget = aBaseRequest.getParameter("target");
      aResponse.getWriter().println(
          "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"DTD/xhtml1-transitional.dtd\">");
      aResponse.getWriter().println("<html>");
      aResponse.getWriter().println("<head>");
      if (tmpTarget != null && !"".equals(tmpTarget)) {
        aResponse.getWriter().println("<meta http-equiv='refresh' content='4; URL=" + tmpTarget + "'/>");
      } else {
        aResponse.getWriter().println(
            "<meta http-equiv='refresh' content='4; URL=http://localhost:" + AbstractWebServerTest.PORT + "'/>");
      }
      aResponse.getWriter().println("</head>");
      aResponse.getWriter().println("<body>");
      aResponse.getWriter().println("</body>");
      aResponse.getWriter().println("</html>");
    }
  }

}
