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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

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
      aResponse.getWriter().println("<title>Wetator / Request Snoopy / Jetty</title>");
      aResponse.getWriter().println("</head>");
      aResponse.getWriter().println("<body>");

      aResponse.getWriter().println("<h1>Wetator / Request Snoopy Jetty</h1>");
      aResponse.getWriter().println("<h1>GET Parameters</h1>");
      aResponse.getWriter().println("<table border='0' cellpadding='4' cellspacing='4'>");
      aResponse.getWriter().println("<tr>");
      aResponse.getWriter().println("<th>Key</th>");
      aResponse.getWriter().println("<th>Value</th>");
      aResponse.getWriter().println("</tr>");

      // a small hack to distinguish between get and post parameters
      Set<String> tmpGetParameterNames = determineGetParameterNames(aRequest);

      List<String> tmpParameterNames = Collections.list((Enumeration<String>) aRequest.getParameterNames());
      Collections.sort(tmpParameterNames);
      for (String tmpName : tmpParameterNames) {
        if (tmpGetParameterNames.contains(tmpName)) {
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
      }
      aResponse.getWriter().println("</table>");

      aResponse.getWriter().println("<h1>POST Parameters</h1>");
      aResponse.getWriter().println("<table border='0' cellpadding='4' cellspacing='4'>");
      aResponse.getWriter().println("<tr>");
      aResponse.getWriter().println("<th>Key</th>");
      aResponse.getWriter().println("<th>Value</th>");
      aResponse.getWriter().println("</tr>");

      for (String tmpName : tmpParameterNames) {
        if (!tmpGetParameterNames.contains(tmpName)) {
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
      }
      aResponse.getWriter().println("</table>");

      aResponse.getWriter().println("<h1>Headers</h1>");
      aResponse.getWriter().println("<table border='0' cellpadding='4' cellspacing='4'>");
      aResponse.getWriter().println("<tr>");
      aResponse.getWriter().println("<th>Key</th>");
      aResponse.getWriter().println("<th>Value</th>");
      aResponse.getWriter().println("</tr>");
      List<String> tmpHeaderNames = Collections.list((Enumeration<String>) aRequest.getHeaderNames());
      Collections.sort(tmpHeaderNames);
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

  /**
   * HttpUtils.parseQueryString is deprecated.
   * So we build our own based on tomcats servlet api impl.
   * 
   * @param aRequest the request to read from
   * @return a set with all get parameter names
   */
  private Set<String> determineGetParameterNames(HttpServletRequest aRequest) {
    String tmpQueryString = aRequest.getQueryString();

    if (tmpQueryString == null) {
      throw new IllegalArgumentException();
    }
    Set<String> tmpParamNames = new HashSet<String>();
    StringTokenizer tmpTokenizer = new StringTokenizer(tmpQueryString, "&");
    while (tmpTokenizer.hasMoreTokens()) {
      String tmpPair = tmpTokenizer.nextToken();
      int tmpPos = tmpPair.indexOf('=');
      if (tmpPos == -1) {
        throw new IllegalArgumentException();
      }
      String tmpKey = parseName(tmpPair.substring(0, tmpPos));
      tmpParamNames.add(tmpKey);
    }

    return tmpParamNames;
  }

  /**
   * Parse a name in the query string.
   */
  private static String parseName(String aString) {
    StringBuffer tmpResult = new StringBuffer();
    for (int i = 0; i < aString.length(); i++) {
      char tmpChar = aString.charAt(i);
      switch (tmpChar) {
        case '+':
          tmpResult.append(' ');
          break;
        case '%':
          try {
            tmpResult.append((char) Integer.parseInt(aString.substring(i + 1, i + 3), 16));
            i += 2;
          } catch (NumberFormatException e) {
            throw new IllegalArgumentException();
          } catch (StringIndexOutOfBoundsException e) {
            String tmpRest = aString.substring(i);
            tmpResult.append(tmpRest);
            if (tmpRest.length() == 2) {
              i++;
            }
          }

          break;
        default:
          tmpResult.append(tmpChar);
          break;
      }
    }
    return tmpResult.toString();
  }

}
