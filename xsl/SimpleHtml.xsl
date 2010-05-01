<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output method="html" encoding="UTF-8" doctype-public="-//Wf3C//DTD HTML 4.01 Transitional//EN" omit-xml-declaration="yes"/>

    <xsl:variable name="noOfStepsInLine" select="150"/>

    <xsl:template match="/">
        <html>
            <head>
                <META http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
                <META http-equiv="content-style-type" content="text/css"/>
                <title>Wetator - Test Result</title>
                <style type="text/css"><![CDATA[
                BODY {BACKGROUND-COLOR: #f8f8f8; FONT-SIZE: 10pt; FONT-FAMILY: Arial, Helvetica, sans-serif; margin: 4px;}
                TABLE {font-size: 10pt; empty-cells: show; border-collapse: collapse; }
                TH {FONT-WEIGHT: bold; BACKGROUND-COLOR: #ccddff; text-align: center;}
                TD.step {border:1px solid #999; color: #000000; text-align: center;}
                TD.light {BACKGROUND-COLOR: #f8f8f8;}
                TD.topBorder {border-top: 1px solid #999;}
                TD.message {BACKGROUND-COLOR: #fff8dc; COLOR: #666666; FONT-SIZE: 10pt;}
                TD.properties {BACKGROUND-COLOR: #f8f8f8; FONT-SIZE: 10pt;}
                TD.error {BACKGROUND-COLOR: #F14F12;}
                TD.comment {BACKGROUND-COLOR: #eeeeee;}
                h1 {FONT-SIZE: 12pt; COLOR: #000000; margin-top:20px;}
                h2 {FONT-SIZE: 10pt; COLOR: #4682b4; margin-top:16px;}
                p.blue {COLOR: #768bc2;}
                PRE.text {FONT-FAMILY: Courier new, monospace, sans-serif;FONT-WEIGHT: bold;WHITE-SPACE: pre;}
                A {COLOR: #768bc2; TEXT-DECORATION: none;}
                A:link {COLOR: #768bc2; TEXT-DECORATION: none;}
                A:visited {COLOR: #768bc2; TEXT-DECORATION: none;}
                A:active {COLOR: #768bc2; TEXT-DECORATION: none;}
                A:hover {TEXT-DECORATION: none;}
                A.linkToCommand {font-size: smaller; display: block;}
                img {border: 0;}
                DIV.header { color: #768bc2; margin-left: 10px; }
                DIV.header IMG { margin-left: -10px; border:0; }
                DIV.colorBar { height: 1em; border: 0; margin-left: 2px; margin-right: 1px; }

                .smallBorder {border: 1px solid #999;}
                .bold {font-weight: bold };
                ]]></style>

                <script type="text/javascript" language="JavaScript"><![CDATA[
                function showOrHide(image, id) {
                    if (image.src.indexOf("collapseall.png") != -1) {
                        var tmpElement=document.getElementById(id);
                        tmpElement.style.display = "none";
                        image.src = image.src.substr(0, image.src.lastIndexOf("/")+1)+"expandall.png";
                    } else if (image.src.indexOf("expandall.png") != -1) {
                        var tmpElement=document.getElementById(id);
                        tmpElement.style.display = "";
                        image.src = image.src.substr(0, image.src.lastIndexOf("/")+1)+"collapseall.png";
                    }
                }
                function makeVisible(id) {
                    tmpImage = document.getElementById('showHide_' + id);
                    if (tmpImage.src.indexOf("expandall.png") != -1) {
                        showOrHide(tmpImage, id);
                    }
                }
                ]]></script>
            </head>

            <body>
                <center><p><img src="images/wetator.png" alt="Wetator"/></p></center>

                <!-- Overview -->
                <a name="overview"></a>
                <h1>Overview</h1>

				<xsl:variable name="testcase.total" select="count(/wet/testcase)"/>
				<xsl:variable name="testcase.failed" select="count(wet/testcase[boolean(descendant-or-self::error)])"/>

				<xsl:variable name="testcase.failedPercent" select="ceiling($testcase.failed * 100 div $testcase.total)"/>
                <xsl:variable name="testcase.okPercent" select="100 - $testcase.failedPercent"/>

                <xsl:variable name="testcase.stepsTotal" select="count(/wet/testcase/command[not(@isComment)])"/>
                <xsl:variable name="testcase.stepsFailed" select="count(wet/testcase/command[boolean(descendant-or-self::error)])"/>

                <xsl:variable name="testcase.stepsFailedPercent" select="ceiling($testcase.stepsFailed * 100 div $testcase.stepsTotal)"/>
                <xsl:variable name="testcase.stepsOkPercent" select="100 - $testcase.stepsFailedPercent"/>

                <table cellpadding="4" cellspacing="0" class="smallBorder" border="0">
                    <tr>
                        <th colspan="6">
                            Tests started at:&#32;
                            <xsl:value-of select="wet/startTime"/>
                        </th>
                        <th colspan="2" style="text-align: left;">
                            Total time:&#32;
                            <xsl:call-template name="time">
                                <xsl:with-param name="msecs" select="wet/executionTime"/>
                            </xsl:call-template>.
                        </th>
                    </tr>

					<tr>
                        <td colspan="3" class="bold">Distribution Test Case Level (<xsl:value-of select="$testcase.total"/> test<xsl:if  test="$testcase.total != 1">s</xsl:if> run)</td>
                        <td>
                            <span style="color: #F14F12; font-weight: bold;">
                                <xsl:value-of select="$testcase.failed"/>
                            </span>
                        </td>
                        <td>
                            <span style="color: green; font-weight: bold;">
                                <xsl:value-of select="$testcase.total - $testcase.failed"/>
                            </span>
                        </td>
                        <td>
                        </td>

                        <td>
                            <table cellpadding="0" cellspacing="0" width="100%">
                            <tr>
                                <xsl:if test="$testcase.failedPercent > 0">
                                    <td class="smallBorder"  style="text-align: center;">
                                        <xsl:attribute name="width">
                                            <xsl:value-of select="$testcase.failedPercent"/>%
                                        </xsl:attribute>
                                        <xsl:attribute name="bgcolor">
                                            <xsl:value-of select="'#F14F12'"/>
                                        </xsl:attribute>
                                        <xsl:attribute name="title">
                                            <xsl:value-of select="'Failed test cases'"/>
                                        </xsl:attribute>
                                        <xsl:value-of select="$testcase.failedPercent"/>%
                                    </td>
                                </xsl:if>
                                <xsl:if test="$testcase.failedPercent &lt; 100">
                                    <td class="smallBorder" style="text-align: center;">
                                        <xsl:attribute name="width">
                                            <xsl:value-of select="$testcase.okPercent"/>%
                                        </xsl:attribute>
                                        <xsl:attribute name="bgcolor">
                                            <xsl:value-of select="'lightgreen'"/>
                                        </xsl:attribute>
                                        <xsl:attribute name="title">
                                            <xsl:value-of select="'Successful test cases'"/>
                                        </xsl:attribute>
                                        <xsl:value-of select="$testcase.okPercent"/>%
                                    </td>
                                </xsl:if>
                                <td></td>
                            </tr>
                            </table>
                        </td>
                    </tr>

                    <tr>
                        <td colspan="3" class="bold">Distribution TestStep Level (<xsl:value-of select="$testcase.stepsTotal"/> step<xsl:if  test="$testcase.stepsTotal != 1">s</xsl:if> in total)</td>

                        <td>
                            <span style="color: #F14F12; font-weight: bold;">
                                <xsl:value-of select="$testcase.stepsFailed"/>
                            </span>
                        </td>
                        <td>
                            <span style="color: green; font-weight: bold;">
                                <xsl:value-of select="$testcase.stepsTotal - $testcase.stepsFailed"/>
                            </span>
                        </td>
                        <td>
                        </td>

                        <td>
                            <table cellpadding="0" cellspacing="0" width="100%">
                            <tr>
                                <xsl:if test="$testcase.stepsFailedPercent > 0">
                                    <td class="smallBorder"  style="text-align: center;">
                                        <xsl:attribute name="width">
                                            <xsl:value-of select="$testcase.stepsFailedPercent"/>%
                                        </xsl:attribute>
                                        <xsl:attribute name="bgcolor">
                                            <xsl:value-of select="'#F14F12'"/>
                                        </xsl:attribute>
                                        <xsl:attribute name="title">
                                            <xsl:value-of select="'Failed steps'"/>
                                        </xsl:attribute>
                                        <xsl:value-of select="$testcase.stepsFailedPercent"/>%
                                    </td>
                                </xsl:if>
                                <xsl:if test="$testcase.stepsFailedPercent &lt; 100">
                                    <td class="smallBorder" style="text-align: center;">
                                        <xsl:attribute name="width">
                                            <xsl:value-of select="$testcase.stepsOkPercent"/>%
                                        </xsl:attribute>
                                        <xsl:attribute name="bgcolor">
                                            <xsl:value-of select="'lightgreen'"/>
                                        </xsl:attribute>
                                        <xsl:attribute name="title">
                                            <xsl:value-of select="'Successful steps'"/>
                                        </xsl:attribute>
                                        <xsl:value-of select="$testcase.stepsOkPercent"/>%
                                    </td>
                                </xsl:if>
                                <td></td>
                            </tr>
                            </table>
                        </td>
                    </tr>

                    <tr>
                        <th>No</th>
                        <th><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></th>
                        <th>Name</th>
                        <th colspan="2">Steps</th>
                        <th><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></th>
                        <th>Graph</th>
                        <th>Duration</th>
                    </tr>

                    <xsl:for-each select="/wet/testcase">
                        <xsl:call-template name="testcaseOverview"/>
                    </xsl:for-each>

                    <tr>
                        <td class="bold">Sum</td>
                        <td><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
                        <td><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
                        <td align="right" class="bold">
                             <xsl:value-of select="count(/wet/testcase/command[not(@isComment)])"/>
                        </td>
                        <td align="right" class="bold">
                             <xsl:value-of select="count(descendant::command[not(@isComment)])"/>
                        </td>
                        <td><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
                        <td><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>

                        <xsl:variable name="duration" select="sum(/wet/testcase/command/executionTime)"/>
                        <td align="right" class="bold">
                            <xsl:call-template name="time">
                                <xsl:with-param name="msecs" select="$duration"/>
                            </xsl:call-template>
                        </td>
                    </tr>
                </table>

                <!-- configuration -->
                <xsl:call-template name="configuration"/>

                <!-- All individual test results -->
                <a name="details"><br/></a>
                <h1>Result Details</h1>
                <xsl:for-each select="wet/testcase">
                    <xsl:call-template name="testresult" />
                </xsl:for-each>

                <!-- Footer -->
                <hr/>
                <xsl:text>Created using&#32;</xsl:text>
                <xsl:value-of select="wet/about/product"/>
                <xsl:text>&#32;version:&#32;</xsl:text>
                <xsl:value-of select="wet/about/version"/>
                <xsl:text>.</xsl:text>
            </body>
        </html>
    </xsl:template>


    <xsl:template name="configuration">
        <table cellpadding="4" cellspacing="0" border="0" style="margin-top: 10px">
        <tr>
            <td>
                <span class="bold">Configuration</span>
                <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                <img src="images/expandall.png" onclick="showOrHide(this, 'configuration')" alt="show/hide Configuration"/>
            </td>
            <td width="50px"></td>
            <td>
                <span class="bold">Variables</span>
                <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                <img src="images/expandall.png" onclick="showOrHide(this, 'variables')" alt="show/hide Variables"/>
            </td>
        </tr>

        <tr>
            <td valign="top">
                <table id="configuration" cellpadding="2" cellspacing="0" class="smallBorder" style="display: none; margin-left: 20px; margin-bottom: 20px">
                    <tr>
                        <th>Key</th>
                        <th></th>
                        <th>Value</th>
                    </tr>

                    <xsl:for-each select="/wet/configuration/property">
                        <tr>
                            <td><xsl:value-of select="@key"/></td>
                            <td><xsl:text disable-output-escaping="yes">&amp;nbsp;&amp;nbsp;</xsl:text></td>
                            <td><xsl:value-of select="@value"/></td>
                        </tr>
                    </xsl:for-each>
                </table>
            </td>
            <td></td>
            <td valign="top">
                <table id="variables" cellpadding="2" cellspacing="0" class="smallBorder" style="display: none; margin-left: 20px">
                    <tr>
                        <th>Name</th>
                        <th></th>
                        <th>Value</th>
                    </tr>

                    <xsl:for-each select="/wet/configuration/variables/variable">
                        <xsl:sort select="@name"/>
                        <tr>
                            <td><xsl:value-of select="@name"/></td>
                            <td><xsl:text disable-output-escaping="yes">&amp;nbsp;&amp;nbsp;</xsl:text></td>
                            <td><xsl:value-of select="@value"/></td>
                        </tr>
                    </xsl:for-each>
                </table>
            </td>
        </tr>
        </table>
    </xsl:template>

    <xsl:template name="testcaseOverview">
        <tr>
            <td align="right"><xsl:number/></td>
            <td align="center">
                <xsl:call-template name="successIndicator"/>
            </td>
            <td>
                <a>
                    <xsl:attribute name="href">
                        <xsl:text>#testspec</xsl:text>
                        <xsl:number/>
                    </xsl:attribute>
                    <xsl:value-of select="@name"/>
                </a>
            </td>
            <td align="right">
                 <xsl:value-of select="count(command[not(@isComment)])"/>
            </td>
            <td align="right">
                 (<xsl:value-of select="count(descendant::command[not(@isComment)])"/>)
            </td>

            <td width="2px"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
            <td width="80%">
                <xsl:variable name="linelength" select="0"/>

                <table align="left" cellpadding="0" cellspacing="0">
                    <tr>
                        <xsl:for-each select="./command[not(@isComment)]">
                            <xsl:variable name="noOfErrors" select="sum(descendant-or-self::error)"/>
                            <xsl:variable name="noOfSubSteps" select="count(descendant::command[not(@isComment)])"/>

                            <!-- start new line if needed -->
                            <xsl:if test="(position() mod $noOfStepsInLine = 1) and (position() &gt; 1)">
                                <xsl:text disable-output-escaping="yes">&lt;/tr&gt;&lt;tr&gt;</xsl:text>
                            </xsl:if>

                            <td class="step">
                                <xsl:attribute name="bgcolor">
                                    <xsl:choose>
                                        <xsl:when test="$noOfErrors = 0">
                                            <xsl:text>lightgreen</xsl:text>
                                        </xsl:when>
                                        <xsl:otherwise>
                                            <xsl:text>#F14F12</xsl:text>
                                        </xsl:otherwise>
                                    </xsl:choose>
                                </xsl:attribute>
                                <xsl:attribute name="width">
                                    <xsl:text>4px</xsl:text>
                                </xsl:attribute>

                                <xsl:element name="a">
                                    <xsl:attribute name="class">linkToCommand</xsl:attribute>
                                    <xsl:attribute name="href">
                                        <xsl:text>#</xsl:text>
                                        <xsl:value-of select="@id"/>
                                    </xsl:attribute>
                                    <xsl:choose>
                                        <xsl:when test="$noOfSubSteps &gt; 0">
                                            <xsl:value-of select="$noOfSubSteps"/>
                                        </xsl:when>
                                        <xsl:otherwise>
                                            <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                                        </xsl:otherwise>
                                    </xsl:choose>
                                </xsl:element>
                            </td>


                        </xsl:for-each>
                        <td style="border-left:1px solid #999;"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
                    </tr>
                </table>
            </td>
            <xsl:variable name="duration" select="sum(./command/executionTime)"/>
            <td align="right">
                <xsl:call-template name="time">
                    <xsl:with-param name="msecs" select="$duration"/>
                </xsl:call-template>
            </td>
        </tr>
    </xsl:template>


    <xsl:template name="testresult">
        <h2>
            <xsl:call-template name="successIndicator"/>
            <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
            <a>
                <xsl:attribute name="name">
                    <xsl:text>testspec</xsl:text>
                    <xsl:number/>
                </xsl:attribute>
                <xsl:value-of select="@name"/>
            </a>
        </h2>
        <xsl:call-template name="testcaseTable" />

        <p>
            <a href="#overview">
            <img src="images/top.png" width="11" height="10" alt="top"/>
            Back to Test Report Overview</a>
        </p>
    </xsl:template>

    <xsl:template name="testcaseTable">
        <xsl:if test="count(command) > 0">
            <table cellpadding="2" cellspacing="0" width="100%" class="smallBorder">
                <tr>
                    <th>Line</th>
                    <th><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></th>
                    <th>Command</th>
                    <th><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></th>
                    <th><xsl:text disable-output-escaping="yes">Parameter&amp;nbsp;1</xsl:text></th>
                    <th><xsl:text disable-output-escaping="yes">Parameter&amp;nbsp;2</xsl:text></th>
                    <th>Duration</th>
                </tr>
                <xsl:for-each select="./command">
                    <xsl:call-template name="command"/>
                </xsl:for-each>
            </table>
        </xsl:if>
    </xsl:template>

    <xsl:template name="command">
        <xsl:variable name="lineStyle">
            <xsl:choose>
                <xsl:when test="@isComment">
                    <xsl:text>comment; topBorder</xsl:text>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:text>light, topBorder</xsl:text>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>

        <tr>
            <xsl:text disable-output-escaping="yes">&lt;td class="</xsl:text>
            <xsl:value-of select="$lineStyle" />
            <xsl:text disable-output-escaping="yes">" align="right"&gt;</xsl:text>
            <a>
                <xsl:attribute name="name">
                    <xsl:value-of select="@id"/>
                </xsl:attribute>
            </a>
            <xsl:value-of select="@line"/>

            <xsl:text disable-output-escaping="yes">&lt;/td&gt;</xsl:text>

            <xsl:text disable-output-escaping="yes">&lt;td class="</xsl:text>
            <xsl:value-of select="$lineStyle" />
            <xsl:text disable-output-escaping="yes">" align="center"&gt;</xsl:text>
                <xsl:choose>
                    <xsl:when test="@isComment">
                        <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                    </xsl:when>
                    <xsl:when test="(count(descendant-or-self::error)) &gt; 0">
                        <img src="./images/failed.png" width="12" height="10" alt="failed"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <img src="./images/ok.png" width="12" height="10" alt="ok"/>
                    </xsl:otherwise>
                </xsl:choose>
            <xsl:text disable-output-escaping="yes">&lt;/td&gt;</xsl:text>

            <xsl:text disable-output-escaping="yes">&lt;td class="</xsl:text>
            <xsl:value-of select="$lineStyle" />
            <xsl:text disable-output-escaping="yes">" &gt;</xsl:text>
                <xsl:value-of select="@name"/>
            <xsl:text disable-output-escaping="yes">&lt;/td&gt;</xsl:text>

            <xsl:text disable-output-escaping="yes">&lt;td class="</xsl:text>
            <xsl:value-of select="$lineStyle" />
             <xsl:text disable-output-escaping="yes">" align="center"&gt;</xsl:text>
                <xsl:choose>
                    <xsl:when test="(count(./testcase)) &gt; 0">
                        <img src="images/expandall.png" alt="Show/Hide sub testcase">
                            <xsl:attribute name="id">
                                <xsl:text>showHide_testcase_</xsl:text>
                                <xsl:value-of select="testcase/@id" />
                            </xsl:attribute>
                              <xsl:attribute name="onclick">
                                   <xsl:text>showOrHide(this, 'testcase_</xsl:text>
                                <xsl:value-of select="testcase/@id" />
                                   <xsl:text>');</xsl:text>
                              </xsl:attribute>
                        </img>
                      </xsl:when>
                     <xsl:when test="(count(./response)) &gt; 0">
                         <xsl:for-each select="./response">
                            <a target="_blank">
                                <xsl:attribute name="href">
                                    <xsl:value-of select="."/>
                                </xsl:attribute>
                                <img src="images/response.png" alt="view response"/>
                            </a>
                        </xsl:for-each>
                    </xsl:when>

                      <xsl:otherwise>
                          <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                      </xsl:otherwise>
                 </xsl:choose>
             <xsl:text disable-output-escaping="yes">&lt;/td&gt;</xsl:text>

            <xsl:text disable-output-escaping="yes">&lt;td class="</xsl:text>
            <xsl:value-of select="$lineStyle" />
            <xsl:text disable-output-escaping="yes">" &gt;</xsl:text>
                <xsl:choose>
                <xsl:when test="string-length(param0) &gt; 0">
                    <xsl:value-of select="param0"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:text>-</xsl:text>
                </xsl:otherwise>
                </xsl:choose>
            <xsl:text disable-output-escaping="yes">&lt;/td&gt;</xsl:text>

            <xsl:text disable-output-escaping="yes">&lt;td class="</xsl:text>
            <xsl:value-of select="$lineStyle" />
            <xsl:text disable-output-escaping="yes">" &gt;</xsl:text>
                <xsl:choose>
                    <xsl:when test="string-length(param1) &gt; 0">
                        <xsl:value-of select="param1"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:text>-</xsl:text>
                    </xsl:otherwise>
                </xsl:choose>
            <xsl:text disable-output-escaping="yes">&lt;/td&gt;</xsl:text>

            <xsl:text disable-output-escaping="yes">&lt;td class="</xsl:text>
            <xsl:value-of select="$lineStyle" />
            <xsl:text disable-output-escaping="yes">" align="right"&gt;</xsl:text>
                &#32;
                <xsl:call-template name="time">
                    <xsl:with-param name="msecs" select="executionTime"/>
                </xsl:call-template>
            <xsl:text disable-output-escaping="yes">&lt;/td&gt;</xsl:text>
        </tr>
        <xsl:if test="count(descendant-or-self::error) &gt; 0">
            <tr>
                <td class="light"/>
                <td class="light"/>
                <td class="light" style="text-align:right;">
                    <!-- link to previous error if exists -->
                    <xsl:choose>
                        <xsl:when test="ancestor::command[count(descendant-or-self::error) &gt; 0]">
                            <xsl:for-each select="ancestor::command[count(descendant-or-self::error) &gt; 0][1]">
                                <a>
                                    <xsl:attribute name="href">
                                        <xsl:text>#</xsl:text>
                                        <xsl:value-of select="@id"/>
                                    </xsl:attribute>
                                       <xsl:attribute name="onclick">
                                            <xsl:text>makeVisible('testcase_</xsl:text>
                                         <xsl:value-of select="parent::testcase/@id" />
                                            <xsl:text>');</xsl:text>
                                       </xsl:attribute>

                                    <img src="images/previous.png" width="11" height="10" alt="previous error"/>
                                </a>
                            </xsl:for-each>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:for-each select="preceding::command[count(descendant-or-self::error) &gt; 0][1]">
                                <a>
                                    <xsl:attribute name="href">
                                        <xsl:text>#</xsl:text>
                                        <xsl:value-of select="@id"/>
                                    </xsl:attribute>
                                       <xsl:attribute name="onclick">
                                            <xsl:text>makeVisible('testcase_</xsl:text>
                                         <xsl:value-of select="parent::testcase/@id" />
                                            <xsl:text>');</xsl:text>
                                       </xsl:attribute>

                                    <img src="images/previous.png" width="11" height="10" alt="previous error"/>
                                </a>
                            </xsl:for-each>
                        </xsl:otherwise>
                    </xsl:choose>

                    <!-- link to next error if exists -->
                    <xsl:choose>
                        <xsl:when test="count(descendant::command/error) &gt; 0">
                            <xsl:for-each select="descendant::command[count(descendant-or-self::error) &gt; 0][1]">
                                <a>
                                    <xsl:attribute name="href">
                                        <xsl:text>#</xsl:text>
                                        <xsl:value-of select="@id"/>
                                    </xsl:attribute>
                                       <xsl:attribute name="onclick">
                                            <xsl:text>makeVisible('testcase_</xsl:text>
                                         <xsl:value-of select="parent::testcase/@id" />
                                            <xsl:text>');</xsl:text>
                                       </xsl:attribute>

                                    <img src="images/next.png" width="11" height="10" alt="next error"/>
                                </a>
                            </xsl:for-each>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:for-each select="following::command[count(descendant-or-self::error) &gt; 0][1]">
                                <a>
                                    <xsl:attribute name="href">
                                        <xsl:text>#</xsl:text>
                                        <xsl:value-of select="@id"/>
                                    </xsl:attribute>
                                       <xsl:attribute name="onclick">
                                            <xsl:text>makeVisible('testcase_</xsl:text>
                                         <xsl:value-of select="parent::testcase/@id" />
                                            <xsl:text>');</xsl:text>
                                       </xsl:attribute>

                                    <img src="images/next.png" width="11" height="10" alt="next error"/>
                                </a>
                            </xsl:for-each>
                        </xsl:otherwise>
                    </xsl:choose>
                </td>
                <td class="light"/>
                <td class="error" colspan="4">
                    <xsl:value-of select="error/message"/>
                </td>
            </tr>
        </xsl:if>

        <xsl:for-each select="log">
            <tr>
                <td class="light"/>
                <td class="light"/>
                <td class="light"/>
                <td class="light"/>
                <td class="message" colspan="4">
                    <xsl:value-of select="./message"/>
                </td>
            </tr>
        </xsl:for-each>

        <xsl:if test="(count(./testcase)) &gt; 0">
              <tr style="display: none">
                   <xsl:attribute name="id">
                       <xsl:text>testcase_</xsl:text>
                     <xsl:value-of select="testcase/@id" />
                   </xsl:attribute>
                   <td class="light"/>
                   <td class="light"/>
                   <td class="light"/>
                   <td class="light"/>
                   <td class="light" colspan="4">
                       <xsl:for-each select="testcase">
                            <xsl:call-template name="testcaseTable"/>
                       </xsl:for-each>
                   </td>
              </tr>
          </xsl:if>

    </xsl:template>

    <!-- subroutines -->
    <xsl:template name="successIndicator">
        <xsl:choose>
            <xsl:when test="count(descendant-or-self::error) &gt; 0">
                <img src="./images/failed.png" width="12" height="10" alt="failed"/>
            </xsl:when>
            <xsl:otherwise>
                <img src="./images/ok.png" width="12" height="10" alt="ok"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>


    <xsl:template name="colorBar">
        <xsl:param name="percentage"/>
        <xsl:param name="color"/>
        <xsl:param name="title"/>

        <xsl:choose>
            <xsl:when test="$percentage>0">
                <table width="{$percentage}%" class="smallBorder" border="0" cellpadding="0" cellspacing="0">
                <tr>
                    <td bgcolor="{$color}">
                        <xsl:if test="$title">
                            <xsl:attribute name="title">
                                <xsl:value-of select="$title"/>
                            </xsl:attribute>
                        </xsl:if>
                        <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                    </td>
                </tr>
                </table>
            </xsl:when>
            <xsl:otherwise>
                <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template name="time">
        <xsl:param name="msecs"/>

        <xsl:choose>
            <xsl:when test="$msecs > 5000">
                <xsl:variable name="base" select="round($msecs div 1000)"/>
                <xsl:variable name="hours" select="floor($base div 3600)"/>
                <xsl:variable name="mins" select="floor(($base - $hours*3600) div 60)"/>
                <xsl:variable name="secs" select="floor(($base - $hours*3600) - $mins*60)"/>

                <xsl:if test="10 > $hours">0</xsl:if>
                <xsl:value-of select="$hours"/>
                <xsl:text>:</xsl:text>
                <xsl:if test="10 > $mins">0</xsl:if>
                <xsl:value-of select="$mins"/>
                <xsl:text>:</xsl:text>
                <xsl:if test="10 > $secs">0</xsl:if>
                <xsl:value-of select="$secs"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="round($msecs div 100) div 10"/>
                <xsl:text> secs</xsl:text>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

</xsl:stylesheet>
