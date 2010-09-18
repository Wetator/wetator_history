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
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

/**
 * @author frank.danek
 */
public class SnoopyHandler extends AbstractHandler {

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.jetty.server.Handler#handle(java.lang.String, org.eclipse.jetty.server.Request,
   *      javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
   */
  @SuppressWarnings("unchecked")
  @Override
  public void handle(String aTarget, Request aBaseRequest, HttpServletRequest aRequest, HttpServletResponse aResponse)
      throws IOException, ServletException {
    if (aBaseRequest.isHandled()) {
      return;
    }

    if (aTarget.endsWith("snoopy.php")) {
      aBaseRequest.setHandled(true);
      aResponse.getWriter().println(
          "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"DTD/xhtml1-transitional.dtd\">");
      aResponse.getWriter().println("<html>");
      aResponse.getWriter().println("<head>");
      aResponse.getWriter().println("<title>Wetator / Request Snoopy</title>");
      aResponse.getWriter().println("</head>");
      aResponse.getWriter().println("<body>");

      aResponse.getWriter().println("<h1>GET Parameters</h1>");
      aResponse.getWriter().println("<table border='0' cellpadding='4' cellspacing='4'>");
      aResponse.getWriter().println("<tr>");
      aResponse.getWriter().println("<th>Key</th>");
      aResponse.getWriter().println("<th>Value</th>");
      aResponse.getWriter().println("</tr>");
      List<String> tmpParameterNames = Collections.list((Enumeration<String>) aRequest.getParameterNames());
      for (String tmpName : tmpParameterNames) {
        aResponse.getWriter().println("<tr>");
        aResponse.getWriter().println("<td>");
        aResponse.getWriter().println(tmpName);
        aResponse.getWriter().println("</td>");
        aResponse.getWriter().println("<td>");
        String[] tmpValues = aRequest.getParameterValues(tmpName);
        if (tmpValues.length != 0) {
          for (String tmpValue : tmpValues) {
            aResponse.getWriter().println(tmpValue);
          }
        }
        aResponse.getWriter().println("</td>");
        aResponse.getWriter().println("</tr>");
      }
      // Map<String, String[]> tmpParameters = aRequest.getParameterMap();
      // for (Entry<String, String[]> tmpEntry : tmpParameters.entrySet()) {
      // aResponse.getWriter().println("<tr>");
      // aResponse.getWriter().println("<td>");
      // aResponse.getWriter().println(tmpEntry.getKey());
      // aResponse.getWriter().println("</td>");
      // aResponse.getWriter().println("<td>");
      // if (tmpEntry.getValue() != null && tmpEntry.getValue().length != 0) {
      // for (String tmpValue : tmpEntry.getValue()) {
      // aResponse.getWriter().println(tmpValue);
      // }
      // }
      // aResponse.getWriter().println("</td>");
      // aResponse.getWriter().println("</tr>");
      // }
      aResponse.getWriter().println("</table>");

      aResponse.getWriter().println("<h1>Headers</h1>");
      aResponse.getWriter().println("<table border='0' cellpadding='4' cellspacing='4'>");
      aResponse.getWriter().println("<tr>");
      aResponse.getWriter().println("<th>Key</th>");
      aResponse.getWriter().println("<th>Value</th>");
      aResponse.getWriter().println("</tr>");
      List<String> tmpHeaderNames = Collections.list((Enumeration<String>) aRequest.getHeaderNames());
      for (String tmpEntry : tmpHeaderNames) {
        aResponse.getWriter().println("<tr>");
        aResponse.getWriter().println("<td>");
        aResponse.getWriter().println(tmpEntry);
        aResponse.getWriter().println("</td>");
        aResponse.getWriter().println("<td>");
        aResponse.getWriter().println(aRequest.getHeader(tmpEntry));
        aResponse.getWriter().println("</td>");
        aResponse.getWriter().println("</tr>");
      }
      aResponse.getWriter().println("</table>");

      aResponse.getWriter().println("</body>");
      aResponse.getWriter().println("</html>");
    }
  }

}
