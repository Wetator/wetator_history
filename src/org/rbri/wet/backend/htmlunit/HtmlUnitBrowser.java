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


package org.rbri.wet.backend.htmlunit;

import java.io.IOException;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import net.sourceforge.htmlunit.corejs.javascript.WrappedException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rbri.wet.backend.ControlFinder;
import org.rbri.wet.backend.WetBackend;
import org.rbri.wet.backend.htmlunit.util.ContentTypeUtil;
import org.rbri.wet.backend.htmlunit.util.DomNodeText;
import org.rbri.wet.backend.htmlunit.util.ExceptionUtil;
import org.rbri.wet.backend.htmlunit.util.PageUtil;
import org.rbri.wet.core.WetConfiguration;
import org.rbri.wet.core.WetEngine;
import org.rbri.wet.exception.AssertionFailedException;
import org.rbri.wet.exception.WetException;
import org.rbri.wet.util.Assert;
import org.rbri.wet.util.ContentUtil;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.DefaultCredentialsProvider;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.History;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.ScriptException;
import com.gargoylesoftware.htmlunit.SgmlPage;
import com.gargoylesoftware.htmlunit.TextPage;
import com.gargoylesoftware.htmlunit.TopLevelWindow;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.WebWindow;
import com.gargoylesoftware.htmlunit.WebWindowEvent;
import com.gargoylesoftware.htmlunit.html.FrameWindow;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.xml.XmlPage;


/**
 * The HtmlUnit backend.
 *
 * @author rbri
 */
public final class HtmlUnitBrowser implements WetBackend {
    private static final Log LOG = LogFactory.getLog(HtmlUnitBrowser.class);;

    protected static final int MAX_HISTORY_SIZE = 15;

    protected WebClient webClient;
    protected ResponseStore responseStore;
    protected Stack<WebWindow> webWindows;
    protected Map<WebWindow, List<Page>> webWindowHistory;
    protected WetEngine wetEngine;


    // TODO implement close

    public HtmlUnitBrowser(WetEngine aWetEngine) {
        super();

        // setup the backend
        // httpclient should accept all cookies
        System.getProperties().put("apache.commons.httpclient.cookiespec", "COMPATIBILITY");

        wetEngine = aWetEngine;

        // response store
        WetConfiguration tmpConfiguration = wetEngine.getWetConfiguration();
        responseStore = new ResponseStore(tmpConfiguration.getOutputDir(), true);
    }


    public void stop() {
    }


    public void startNewSession() {
        WetConfiguration tmpConfiguration = wetEngine.getWetConfiguration();

        WetBackend.Browser tmpWetBrowser = tmpConfiguration.getBrowser();
        BrowserVersion tmpBrowserVersion = determineBrowserVersionFor(tmpWetBrowser);

        // TODO maybe we have to do more here
        if (null != webClient) {
        	webClient.closeAllWindows();
        }


        if (StringUtils.isNotEmpty(tmpConfiguration.getProxyHost())) {
            String tmpHost = tmpConfiguration.getProxyHost();
            int tmpPort = tmpConfiguration.getProxyPort();

            webClient = new WebClient(tmpBrowserVersion, tmpHost, tmpPort);

            if ((null != tmpConfiguration.getBasicAuthUser())
                    && StringUtils.isNotEmpty(tmpConfiguration.getBasicAuthUser().getValue())) {
                String tmpUser = tmpConfiguration.getBasicAuthUser().getValue();
                String tmpPassword = tmpConfiguration.getBasicAuthPassword().getValue();
                DefaultCredentialsProvider credentialProvider = new DefaultCredentialsProvider();
                credentialProvider.addProxyCredentials(tmpUser, tmpPassword);
                webClient.setCredentialsProvider(credentialProvider);
                // TODO logging
            }

            if ((null != tmpConfiguration.getProxyUser())
                    && StringUtils.isNotEmpty(tmpConfiguration.getProxyUser().getValue())) {
                String tmpUser = tmpConfiguration.getProxyUser().getValue();
                String tmpPassword = tmpConfiguration.getProxyPassword().getValue();
                DefaultCredentialsProvider credentialProvider = new DefaultCredentialsProvider();
                credentialProvider.addProxyCredentials(tmpUser, tmpPassword);
                webClient.setCredentialsProvider(credentialProvider);
                // TODO logging
            }

            Set<String> tmpNonProxyHosts = tmpConfiguration.getProxyHostsToBypass();

            for (String tmpString : tmpNonProxyHosts) {
                webClient.getProxyConfig().addHostsToProxyBypass(tmpString);
            }
        } else {
            webClient = new WebClient(tmpBrowserVersion);
        }

        // setup our own history management
        WebWindow tmpCurrentWindow = webClient.getCurrentWindow();
        webWindows = new Stack<WebWindow>();
        webWindowHistory = new HashMap<WebWindow, List<Page>>();
        webWindowOpened(tmpCurrentWindow);

        // setup our listener
        webClient.addWebWindowListener(new WebWindowListener(this));
        webClient.setAlertHandler(new AlertHandler(wetEngine));

        // set Accept-Language header
        webClient.addRequestHeader("Accept-Language", tmpConfiguration.getAcceptLanaguage());

        // trust all SSL-certificates
        try {
            webClient.setUseInsecureSSL(true);
        } catch (GeneralSecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public String getCurrentContentAsString() throws AssertionFailedException {
        Page tmpPage = getCurrentPage();
        if (tmpPage instanceof HtmlPage) {
            HtmlPage tmpHtmlPage = (HtmlPage) tmpPage;
            String tmpContentAsText = new DomNodeText(tmpHtmlPage).getText();

            return tmpContentAsText;
        }

        if (tmpPage instanceof XmlPage) {
            XmlPage tmpXmlPage = (XmlPage) tmpPage;
            String tmpContentAsText = tmpXmlPage.getContent();
            return tmpContentAsText;
        }

        if (tmpPage instanceof TextPage) {
            TextPage tmpTextPage = (TextPage) tmpPage;
            String tmpContentAsText = tmpTextPage.getContent();
            return tmpContentAsText;
        }

        ContentType tmpContentType = ContentTypeUtil.getContentType(tmpPage);

        if (ContentType.PDF == tmpContentType) {
            try {
                String tmpContentAsText = ContentUtil.getPdfContentAsString(tmpPage.getWebResponse().getContentAsStream());
                return tmpContentAsText;
            } catch (IOException e) {
                Assert.fail("pdfConversionToTextFailed", new String[] {e.getMessage()});
                return null;
            }
        }

        if (ContentType.XLS == tmpContentType) {
            try {
                String tmpContentAsText = ContentUtil.getXlsContentAsString(tmpPage.getWebResponse().getContentAsStream());
                return tmpContentAsText;
            } catch (IOException e) {
                Assert.fail("xlsConversionToTextFailed", new String[] {e.getMessage()});
                return null;
            }
        }

        Assert.fail("unsupportedPageType", new String[] {tmpPage.getWebResponse().getContentType()});
        return null;
    }



    public void openUrl(URL aUrl) throws AssertionFailedException {
        try {
            Page tmpPage = webClient.getPage(aUrl);

            if (tmpPage instanceof SgmlPage) {
                PageUtil.waitForThreads((SgmlPage)tmpPage);
            }
        } catch (ScriptException e) {
            Assert.fail("javascriptError", new String[] {e.getMessage()});
        } catch (WrappedException e) {
            Assert.fail("javascriptError", new String[] {ExceptionUtil.getMessageFromScriptExceptionCauseIfPossible(e)});
        } catch (FailingHttpStatusCodeException e) {
            Assert.fail("serverError", new String[] {aUrl.toString(), e.getMessage()});
        } catch (UnknownHostException e) {
            Assert.fail("unknownHostError", new String[] {aUrl.toString(), e.getMessage()});
        } catch (Throwable e) {
            LOG.fatal("OpenUrl '" + aUrl.toExternalForm() + "'fails", e);
            Assert.fail("serverError", new String[] {aUrl.toString(), e.getMessage()});
        }
    }


    public static final class AlertHandler implements com.gargoylesoftware.htmlunit.AlertHandler {
        private WetEngine wetEngine;

        public AlertHandler(WetEngine aWetEngine) {
            wetEngine = aWetEngine;
        }

        public void handleAlert(Page aPage, String aMessage) {
            LOG.debug("handleAlert " + aMessage);

            String tmpMessage = "";
            if (StringUtils.isNotEmpty(aMessage)) {
                tmpMessage = aMessage;
            }
            String tmpUrl = "";
            try {
                tmpUrl = aPage.getWebResponse().getRequestSettings().getUrl().toExternalForm();
            } catch (NullPointerException e) {
                // ignore
            }

            wetEngine.informListenersInfo("javascriptAlert", new String[] {tmpMessage, tmpUrl});
        }
    }


    private WebWindow getCurrentWebWindow() {
        if (webWindows.empty()) {
            return null;
        }

        WebWindow tmpResult = webWindows.lastElement();
        return tmpResult;
    }


    public void goBackInCurrentWindow(int aSteps) throws AssertionFailedException {
        WebWindow tmpCurrentWindow = getCurrentWebWindow();

        if (null == tmpCurrentWindow) {
            Assert.fail("noWebWindow", null);
        }

        History tmpHistory = tmpCurrentWindow.getHistory();

        final int tmpIndexPos = tmpHistory.getIndex() - aSteps;
        if (tmpIndexPos >= tmpHistory.getLength() || tmpIndexPos < 0) {
            Assert.fail("outsideHistory", new String[] {"" + aSteps, "" + tmpIndexPos, "" + tmpHistory.getLength()});
        }

        try {
            tmpHistory.go(-1 * aSteps);
        } catch (IOException e) {
            Assert.fail("historyFailed", new String[] {e.getMessage()});
        }
    }


    public void saveCurrentWindowToLog() {
        WebWindow tmpCurrentWindow = getCurrentWebWindow();

        if (null != tmpCurrentWindow) {
            try {
                Page tmpPage = tmpCurrentWindow.getEnclosedPage();
                String tmpPageFile = responseStore.storePage(webClient, tmpPage);
                wetEngine.informListenersResponseStored(tmpPageFile);
            } catch (WetException e) {
                LOG.fatal("Problem with window handling. Saving page failed!", e);
            }
        }
    }


    public void savePageToHistory(WebWindow aWebWindow) {
        if (!webWindows.contains(aWebWindow)) {
            LOG.fatal("Problem with window handling. Saving page for unknown window!");
        }

        List<Page> tmpHistoryEntry = webWindowHistory.get(aWebWindow);

        if (tmpHistoryEntry.size() > MAX_HISTORY_SIZE) {
            // remove the oldest one
            tmpHistoryEntry.remove(0);
        }
        tmpHistoryEntry.add(aWebWindow.getEnclosedPage());
    }


    public void webWindowOpened(WebWindow aWebWindow) {
        webWindows.push(aWebWindow);
        webWindowHistory = new HashMap<WebWindow, List<Page>>();
        webWindowHistory.put(aWebWindow, new LinkedList<Page>());
    }


    public void webWindowClosed(WebWindow aWebWindow) {
        webWindows.remove(aWebWindow);
        webWindowHistory.remove(aWebWindow);
    }


    public static final class WebWindowListener implements com.gargoylesoftware.htmlunit.WebWindowListener {
        private HtmlUnitBrowser htmlUnitBrowser;

        public WebWindowListener(HtmlUnitBrowser aHtmlUnitBrowser) {
            super();
            htmlUnitBrowser = aHtmlUnitBrowser;
        }

        public void webWindowClosed(WebWindowEvent anEvent) {
        	Page tmpPage = anEvent.getWebWindow().getEnclosedPage();
        	if (null == tmpPage) {
                LOG.debug("webWindowClosed: (page null)");
        	} else {
	            LOG.debug("webWindowClosed: (url '"
	                    + anEvent.getWebWindow().getEnclosedPage().getWebResponse().getRequestSettings().getUrl() + "')");
        	}
            htmlUnitBrowser.webWindowClosed(anEvent.getWebWindow());
        }

        public void webWindowContentChanged(WebWindowEvent anEvent) {
            LOG.debug("webWindowContentChanged");

            final WebWindow tmpWebWindow = anEvent.getWebWindow();
            final WebResponse tmpWebResponse = anEvent.getNewPage().getWebResponse();
            LOG.debug("Content of window changed to "
                    + tmpWebResponse.getRequestSettings().getUrl() + " ("
                    + tmpWebResponse.getContentType() + ")");

            final boolean tmpIsNew;
            final WebWindow tmpCurrentWindow = htmlUnitBrowser
                    .getCurrentWebWindow();

            if (tmpWebWindow instanceof TopLevelWindow
                    && anEvent.getOldPage() == null) {
                LOG.debug("Content loaded in newly opened window, its content will become current response");
                tmpIsNew = true;
            } else if (tmpCurrentWindow == tmpWebWindow) {
                LOG.debug("Content of current window changed, it will become current response");
                tmpIsNew = true;
            }

            // content loaded in an other window as the "current" one via javascript
            // becomes "current" only if new top window is opened
            else if (htmlUnitBrowser.webClient.getJavaScriptEngine() == null || !htmlUnitBrowser.webClient.getJavaScriptEngine().isScriptRunning()) {
                if (tmpWebWindow instanceof FrameWindow && !HtmlPage.READY_STATE_COMPLETE.equals(((FrameWindow) tmpWebWindow).getEnclosingPage().getDocumentElement().getReadyState())) {
                    LOG.debug("Content of frame window has changed without javascript while enclosing page is loading, it will NOT become current response");
                    LOG.debug("Enclosing page's state: " + ((FrameWindow) tmpWebWindow).getEnclosingPage().getDocumentElement().getReadyState());
                    LOG.debug("Enclosing page's url: " + ((FrameWindow) tmpWebWindow).getEnclosingPage().getWebResponse().getRequestSettings().getUrl());
                    tmpIsNew = false;
                } else {
                    LOG.debug("Content of window changed without javascript, it will become current response");
                    tmpIsNew = true;
                }
            } else {
                LOG.debug("Content of window changed with javascript, it will NOT become current response");
                tmpIsNew = false;
            }

            if (tmpIsNew) {
                htmlUnitBrowser.savePageToHistory(tmpWebWindow);
            }
        }

        public void webWindowOpened(WebWindowEvent anEvent) {
            LOG.debug("webWindowOpened");
            htmlUnitBrowser.webWindowOpened(anEvent.getWebWindow());
        }
    }


    private Page getCurrentPage() throws AssertionFailedException {
        WebWindow tmpWebWindow = webClient.getCurrentWindow();
        if (null == tmpWebWindow) {
            Assert.fail("noWebWindow", null);
        }
        Page tmpPage = tmpWebWindow.getEnclosedPage();
        if (null == tmpPage) {
            Assert.fail("noPageInWebWindow", null);
        }

        return tmpPage;
    }

    private BrowserVersion determineBrowserVersionFor(WetBackend.Browser aWetBrowser) {
        if (WetBackend.Browser.FIREFOX_2 == aWetBrowser) {
            return BrowserVersion.FIREFOX_2;
        }
        if (WetBackend.Browser.FIREFOX_3 == aWetBrowser) {
            return BrowserVersion.FIREFOX_3;
        }
        if (WetBackend.Browser.INTERNET_EXPLORER_6 == aWetBrowser) {
            return BrowserVersion.INTERNET_EXPLORER_6;
        }
        if (WetBackend.Browser.INTERNET_EXPLORER_7 == aWetBrowser) {
            return BrowserVersion.INTERNET_EXPLORER_7;
        }
        if (WetBackend.Browser.INTERNET_EXPLORER_8 == aWetBrowser) {
            return BrowserVersion.INTERNET_EXPLORER_8;
        }
        return BrowserVersion.INTERNET_EXPLORER_6;
    }


    public HtmlPage getCurrentHtmlPage() throws AssertionFailedException {
        Page tmpPage = getCurrentPage();
        if (tmpPage instanceof HtmlPage) {
            return (HtmlPage) tmpPage;
        }

        Assert.fail("noHtmlPage", new String[] {tmpPage.getClass().toString()});
        return null;
    }

    public ControlFinder getControlFinder() throws AssertionFailedException {
        HtmlPage tmpHtmlPage = getCurrentHtmlPage();

        return new HtmlUnitControlFinder(tmpHtmlPage);
    }

    public String getCurrentTitle() throws AssertionFailedException {
        HtmlPage tmpHtmlPage = getCurrentHtmlPage();
        return tmpHtmlPage.getTitleText();
    }
}
