<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="html" encoding="UTF-8" doctype-public="-//W3C//DTD HTML 4.01 Transitional//EN" okmit-xml-declaration="yes"/>

	<!-- global variable -->
	<xsl:variable name="duration.total"
	              select="sum(exceltests/testcase/command/@duration)"/>

	<xsl:template match="/">
		<html>
			<head>
				<META http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
				<META http-equiv="content-style-type" content="text/css"/>
				<title>WebTester - Test Result Overview</title>
				<style type="text/css"><![CDATA[
				BODY {FONT-SIZE: 10pt; FONT-FAMILY: Arial, Helvetica, sans-serif;}
				TH {FONT-WEIGHT: bold; BACKGROUND-COLOR: #ccddff; text-align: center;}
				TD.light {BACKGROUND-COLOR: #f8f8f8;}
				TD.message {BACKGROUND-COLOR: #f8f8f8;COLOR: #666666;FONT-SIZE: 10pt;}
				TD.properties {BACKGROUND-COLOR: #f8f8f8; FONT-SIZE: 10pt;}
				TD.error {BACKGROUND-COLOR: #F14F12;}
				TD.comment {BACKGROUND-COLOR: #eeeeee;}
				h3 {COLOR: #768bc2; margin-top:2px;}
				h4 {COLOR: #768bc2;}
				p.blue {COLOR: #768bc2;}
				PRE.text {FONT-FAMILY: Courier new, monospace, sans-serif;FONT-WEIGHT: bold;WHITE-SPACE: pre;}
				TABLE{font-size: 10pt;empty-cells: show;}
				A {COLOR: #768bc2;TEXT-DECORATION: none;}
				A:link {COLOR: #768bc2;TEXT-DECORATION: none;}
				A:visited {COLOR: #768bc2;TEXT-DECORATION: none;}
				A:active {COLOR: #768bc2;TEXT-DECORATION: none;}
				A:hover {TEXT-DECORATION: underline;}
				A.linkToError {FONT-SIZE: smaller;}
				img {border: 0;}
				DIV.header { color: #768bc2; margin-left: 10px; }
				DIV.header IMG { margin-left: -10px; border:0; }
				DIV.colorBar { height: 1em; border: 0; margin-left: 2px; margin-right: 1px; }
				]]></style>
				<script type="text/javascript" language="JavaScript"><![CDATA[
				function showOrHideModule(imageForModule, id) {
					if (imageForModule.src.indexOf("collapseall.png") != -1) {
						var trModule=document.getElementById(id);
						trModule.style.display = "none";
						imageForModule.src = imageForModule.src.substr(0, imageForModule.src.lastIndexOf("/")+1)+"expandall.png";
					} else if (imageForModule.src.indexOf("expandall.png") != -1) {
						var trModule=document.getElementById(id);
						trModule.style.display = "";
						imageForModule.src = imageForModule.src.substr(0, imageForModule.src.lastIndexOf("/")+1)+"collapseall.png";
					}
				}
			    ]]></script>
			</head>

			<body style="margin: 4px;">
			<p><img src="images/wet.png" alt="WebTester"/></p>

				<!-- Header and summary table -->
				<table cellpadding="0" border="0" cellspacing="0" width="100%">
					<tr>
						<td valign="top">
							<xsl:call-template name="summary"/>
						</td>
						<td><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
						<td valign="top" width="40%">
							<xsl:call-template name="histogram"/>
						</td>
					</tr>
				</table>

				<xsl:call-template name="OverviewTable"/>

				<!-- All individual test results -->
				<xsl:for-each select="exceltests/testcase">
					<xsl:call-template name="testresult">
						<xsl:with-param name="currentCaseNo" select="position()"/>
					</xsl:call-template>
					
				</xsl:for-each>

				<!-- Footer & fun -->
				<hr/>
				<xsl:text>Created using&#32;</xsl:text>
				<xsl:value-of select="exceltests/@version"/>
				<xsl:text>.</xsl:text>


			</body>
		</html>

	</xsl:template>

	<xsl:template name="summary">

				<h3>Result Summary</h3>

				<xsl:variable name="tests.total" select="count(/exceltests/testcase)"/>
				<xsl:variable name="tests.failed" select="count(exceltests/testcase[boolean(./command/error)])"/>
				<xsl:variable name="tests.ok" select="$tests.total - $tests.failed"/>

				<table cellpadding="4" border="0" cellspacing="0" width="100%" style="border:1px solid #999;">
					<tr>
						<th align="center">Result</th>
						<th align="right">#</th>
						<th><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></th>
						<th><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></th>
						<th>Graph</th>
					</tr>
					<tr>
						<td class="light" align="center">
							<img src="images/ok.png" width="12" height="10" alt="ok"/>
						</td>
						<td class="light" align="right">
							<xsl:value-of select="$tests.ok"/>
						</td>
						<td class="light" align="right">
							<xsl:text disable-output-escaping="yes">&amp;nbsp;(</xsl:text>
							<xsl:value-of select="round($tests.ok * 100 div $tests.total)"/>
							<xsl:text>%)</xsl:text>
						</td>
						<xsl:call-template name="colorBar">
							<xsl:with-param name="percentage" select="$tests.ok * 100 div $tests.total"/>
							<xsl:with-param name="color" select="'lightgreen'"/>
							<xsl:with-param name="title" select="'Successful steps'"/>
						</xsl:call-template>
					</tr>
					<tr>
						<td class="light" align="center">
							<img src="images/failed.png" width="12" height="10" alt="failed"/>
						</td>
						<td class="light" align="right">
							<xsl:value-of select="$tests.failed"/>
						</td>
						<td class="light" align="right">
							<xsl:text disable-output-escaping="yes">&amp;nbsp;(</xsl:text>
							<xsl:value-of select="round($tests.failed * 100 div $tests.total)"/>
							<xsl:text>%)</xsl:text>
						</td>
						<xsl:call-template name="colorBar">
							<xsl:with-param name="percentage" select="$tests.failed * 100 div $tests.total"/>
							<xsl:with-param name="color" select="'#F14F12'"/>
							<xsl:with-param name="title" select="'Failed steps'"/>
						</xsl:call-template>
					</tr>
					<tr>
						<td class="light">
							<b>Sum</b>
						</td>
						<td class="light" align="right">
							<b>
								<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
								<xsl:value-of select="$tests.total"/>
							</b>
						</td>
						<td class="light" align="right">
							<b></b>
						</td>
						<td class="light"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
						<td class="light"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
					</tr>
					<tr>
						<td class="light" colspan="5"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
					</tr>
					<tr>
						<td class="light" colspan="5" style="FONT-WEIGHT: bold;">
							Tests started at&#32;
							<xsl:value-of select="exceltests/@startdate"/>.&#32;
						</td>
					</tr>
					<tr>
						<td class="light" colspan="5" style="FONT-WEIGHT: bold;">
							Total time:&#32;
							<xsl:call-template name="time">
								<xsl:with-param name="msecs" select="exceltests/@duration"/>
							</xsl:call-template>.
						</td>
					</tr>
				</table>
				<xsl:call-template name="properties"/>

	</xsl:template>

	<xsl:template name="histogram">
		<h3>Server Roundtrip Timing Profile</h3>

		<xsl:variable name="duration.command" select="exceltests//testcase//command[@name='Open Url' or @name='Click On']"/>

		<xsl:variable name="last.steps"
		              select="count($duration.command[@duration > 30000])"/>
		<xsl:variable name="third.steps"
		              select="count($duration.command[@duration > 10000][30000 >= @duration])"/>
		<xsl:variable name="second.steps"
		              select="count($duration.command[@duration > 5000][10000 >= @duration])"/>
		<xsl:variable name="first.steps"
		              select="count($duration.command[@duration > 1000][5000 >= @duration])"/>
		<xsl:variable name="begin.steps"
		              select="count($duration.command[1000 >= @duration])"/>

		<table cellpadding="4" border="0" cellspacing="0" width="100%" style="border:1px solid #999;">
			<tr>
				<th align="right">Secs</th>
				<th align="right">#</th>
				<th align="right">%</th>
				<th><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></th>
				<th>Histogram</th>
			</tr>

			<tr>
				<td class="light" align="right"><xsl:text disable-output-escaping="yes">0&amp;nbsp;-&amp;nbsp;&amp;nbsp;&amp;nbsp;1&amp;nbsp;</xsl:text></td>
				<td class="light" align="right"><xsl:value-of select="$begin.steps"/></td>
				<td class="light" align="right"><xsl:value-of select="round($begin.steps * 100 div count($duration.command))"/></td>
				<xsl:call-template name="colorBar">
					<xsl:with-param name="percentage" select="$begin.steps * 100 div count($duration.command)"/>
					<xsl:with-param name="color" select="'ccddff'"/>
				</xsl:call-template>
			</tr>

			<tr>
				<td class="light" align="right"><xsl:text disable-output-escaping="yes">1&amp;nbsp;-&amp;nbsp;&amp;nbsp;&amp;nbsp;5&amp;nbsp;</xsl:text></td>
				<td class="light" align="right"><xsl:value-of select="$first.steps"/></td>
				<td class="light" align="right"><xsl:value-of select="round($first.steps * 100 div count($duration.command))"/></td>
				<xsl:call-template name="colorBar">
					<xsl:with-param name="percentage" select="$first.steps * 100 div count($duration.command)"/>
					<xsl:with-param name="color" select="'ccddff'"/>
				</xsl:call-template>
			</tr>

			<tr>
				<td class="light" align="right"><xsl:text disable-output-escaping="yes">5&amp;nbsp;-&amp;nbsp;10&amp;nbsp;</xsl:text></td>
				<td class="light" align="right"><xsl:value-of select="$second.steps"/></td>
				<td class="light" align="right"><xsl:value-of select="round($second.steps * 100 div count($duration.command))"/></td>
				<xsl:call-template name="colorBar">
					<xsl:with-param name="percentage" select="$second.steps * 100 div count($duration.command)"/>
					<xsl:with-param name="color" select="'ccddff'"/>
				</xsl:call-template>
			</tr>

			<tr>
				<td class="light" align="right"><xsl:text disable-output-escaping="yes">10&amp;nbsp;-&amp;nbsp;30&amp;nbsp;</xsl:text></td>
				<td class="light" align="right"><xsl:value-of select="$third.steps"/></td>
				<td class="light" align="right"><xsl:value-of select="round($third.steps * 100 div count($duration.command))"/></td>
				<xsl:call-template name="colorBar">
					<xsl:with-param name="percentage" select="$third.steps * 100 div count($duration.command)"/>
					<xsl:with-param name="color" select="'ccddff'"/>
				</xsl:call-template>
			</tr>

			<tr>
				<td class="light" align="right"><xsl:text disable-output-escaping="yes">&amp;nbsp;&amp;gt;&amp;nbsp;30&amp;nbsp;</xsl:text></td>
				<td class="light" align="right"><xsl:value-of select="$last.steps"/></td>
				<td class="light" align="right"><xsl:value-of select="round($last.steps * 100 div count($duration.command))"/></td>
				<xsl:call-template name="colorBar">
					<xsl:with-param name="percentage" select="$last.steps * 100 div count($duration.command)"/>
					<xsl:with-param name="color" select="'ccddff'"/>
				</xsl:call-template>
			</tr>

			<tr>
				<td class="light">
					<b>Sum</b>
				</td>
				<td class="light" align="right">
					<b>
						<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
						<xsl:value-of select="count($duration.command)"/>
					</b>
				</td>
				<td class="light"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
				<td class="light"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
				<td class="light"><b>
				Avg<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
				<xsl:value-of select="round(sum($duration.command/@duration) div count($duration.command)) "/>
				<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>msecs
				</b></td>
			</tr>
		</table>
	</xsl:template>

	<xsl:template name="properties">
		<p class="blue">Configuration<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
			<img src="images/expandall.png" onclick="showOrHideModule(this, 'properties')" alt="show Configuration"/>
		</p>
		<table id="properties" cellpadding="2" border="0" cellspacing="0" width="100%" style="border:1px solid #999; display: none;">
			<tr>
				<th>Key</th>
				<th>Value</th>
			</tr>
			<xsl:for-each select="/exceltests/properties/property">
				<xsl:sort select="@key"/>
				<tr>
					<td class="properties"><xsl:value-of select="@key"/></td>
					<td class="properties"><xsl:value-of select="@value"/></td>
				</tr>
			</xsl:for-each>
		</table>
	</xsl:template>

	<xsl:template name="OverviewTable">
		<a name="overview"><br/></a>
		<h3>Overview</h3>
		<table cellpadding="4" border="0" cellspacing="0" style="border:1px solid #999;">
			<tr>
				<th>No</th>
				<th></th>
				<th>Name</th>
				<th>Secs</th>
				<th>Steps</th>
				<th><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></th>
				<th>Graph</th>
			</tr>
			<xsl:for-each select="/exceltests/testcase">
				<xsl:call-template name="overview">
					<xsl:with-param name="currentCaseNo" select="position()"/>
				</xsl:call-template>
			</xsl:for-each>
		</table>
	</xsl:template>

	<xsl:template name="overview">
		<xsl:param name="currentCaseNo"/>
		<tr>
			<td class="light" align="right">
				<xsl:number/>
			</td>
			<td class="light" align="center">
				<xsl:call-template name="successIndicator"/>
			</td>
			<td class="light">
				<a>
					<xsl:attribute name="href">
						<xsl:text>#testspec</xsl:text>
						<xsl:number/>
					</xsl:attribute>
					<xsl:value-of select="@name"/>
				</a>
			</td>
			<xsl:variable name="duration" select="sum(./command/@duration)"/>
			<td class="light" align="right">
				<xsl:value-of select="$duration div 1000"/>
			</td>
			<td class="light" align="right">
 				<xsl:value-of select="count(./command[@name != 'Comment'])"/>
			</td>

			<td width="2px" class="light"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
			<td width="80%" class="light">
			<table align="left" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<xsl:for-each select="./command">
						<xsl:choose>
							<xsl:when test="@name = 'Comment'">
							</xsl:when>
							<xsl:when test="sum(error) = 0">
								<td bgcolor="lightgreen" width="2px" style="border:1px solid #999; border-right-style: none;">
									<a>
										<xsl:attribute name="href">
											<xsl:text>#</xsl:text>
											<xsl:value-of select="$currentCaseNo"/>
											<xsl:text>_</xsl:text>
											<xsl:value-of select="@row"/>
										</xsl:attribute>
										<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
									</a>
								</td>
							</xsl:when>
							<xsl:otherwise>
								<td bgcolor="#F14F12" width="2px" style="border:1px solid #999; border-right-style: none;">
									<a>
										<xsl:attribute name="href">
											<xsl:text>#</xsl:text>
											<xsl:value-of select="$currentCaseNo"/>
											<xsl:text>_</xsl:text>
											<xsl:value-of select="@row"/>
										</xsl:attribute>
										<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
									</a>
								</td>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:for-each>
					<td style="border-left:1px solid #999;"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
				</tr>
			</table>
			</td>
		</tr>
	</xsl:template>

	<xsl:template name="testresult">
		<xsl:param name="currentCaseNo"/>
		<hr/>
		<h3>
			<xsl:call-template name="successIndicator"/>
			<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
			<a>
				<xsl:attribute name="name">
					<xsl:text>testspec</xsl:text>
					<xsl:number/>
				</xsl:attribute>
				<xsl:value-of select="@name"/>
			</a>
		</h3>
		<xsl:call-template name="testcasetable">
			<xsl:with-param name="currentCaseNo" select="$currentCaseNo"/>
		</xsl:call-template>

		<p>
			<a href="#overview">
			<img src="images/top.png" width="11" height="10" alt="top"/>
			Back to Test Report Overview</a>
		</p>
	</xsl:template>

	<xsl:template name="testcasetable">
		<xsl:param name="currentCaseNo"/>
		<xsl:if test="count(command) > 0">
			<table cellpadding="2" border="0" cellspacing="0" width="100%" style="border:1px solid #999;">
				<tr>
					<th style="text-align:right;">Line</th>
					<th/>
					<th style="text-align:left;">Command</th>
					<th/>
					<th style="text-align:left;"><xsl:text disable-output-escaping="yes">Parameter&amp;nbsp;1</xsl:text></th>
					<th style="text-align:left;"><xsl:text disable-output-escaping="yes">Parameter&amp;nbsp;2</xsl:text></th>
					<th style="text-align:right;">Duration</th>
				</tr>
				<xsl:for-each select="./command">
					<xsl:call-template name="command">
						<xsl:with-param name="currentCaseNo" select="$currentCaseNo"/>
					</xsl:call-template>
				</xsl:for-each>
			</table>
		</xsl:if>
	</xsl:template>

	<xsl:template name="command">
		<xsl:param name="currentCaseNo"/>
		<xsl:variable name="lineStyle">
			<xsl:choose>
					<xsl:when test="@name = 'Comment'">
						<xsl:text>comment</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>light</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
		</xsl:variable>

		<tr>
			<xsl:text disable-output-escaping="yes">&lt;td class="</xsl:text>
			<xsl:value-of select="$lineStyle" />
			<xsl:text disable-output-escaping="yes">" style="border-top:1px solid #999;" align="right"&gt;</xsl:text>
			<a>
				<xsl:attribute name="name">
					<xsl:value-of select="$currentCaseNo"/>
					<xsl:text>_</xsl:text>
					<xsl:value-of select="@row"/>
				</xsl:attribute>
			</a>
			<b><xsl:value-of select="@row"/></b>
			
			<xsl:text disable-output-escaping="yes">&lt;/td&gt;</xsl:text>

			<xsl:text disable-output-escaping="yes">&lt;td class="</xsl:text>
			<xsl:value-of select="$lineStyle" />
			<xsl:text disable-output-escaping="yes">" style="border-top:1px solid #999;" align="center"&gt;</xsl:text>
				<xsl:choose>
					<xsl:when test="@name = 'Comment'">
					</xsl:when>
					<xsl:when test="(count(./error)) &gt; 0">
						<img src="images/failed.png" width="12" height="10" alt="failed"/>
					</xsl:when>
					<xsl:otherwise>
						<img src="images/ok.png" width="12" height="10" alt="ok"/>
					</xsl:otherwise>
				</xsl:choose>
			<xsl:text disable-output-escaping="yes">&lt;/td&gt;</xsl:text>

			<xsl:text disable-output-escaping="yes">&lt;td class="</xsl:text>
			<xsl:value-of select="$lineStyle" />
			<xsl:text disable-output-escaping="yes">" style="border-top:1px solid #999;"&gt;</xsl:text>
				<xsl:value-of select="@name"/>
			<xsl:text disable-output-escaping="yes">&lt;/td&gt;</xsl:text>

			<xsl:text disable-output-escaping="yes">&lt;td class="</xsl:text>
			<xsl:value-of select="$lineStyle" />
	 		<xsl:text disable-output-escaping="yes">" style="border-top:1px solid #999;" align="center"&gt;</xsl:text>
				<xsl:choose>
			 		 <xsl:when test="(count(./testcase)) &gt; 0">
		 		 		 <img src="images/expandall.png" alt="show Module">
				 		 		 <xsl:attribute name="onclick">
		 		 		 		 <xsl:text>showOrHideModule(this, &apos;tr_</xsl:text>
		 		 		 		 <xsl:number from="/exceltests" level="any"/>
		 		 		 		 <xsl:text>&apos;);</xsl:text>
				 		 		 </xsl:attribute>
		 		 		 </img>
			 		 </xsl:when>
			 		<xsl:when test="boolean(@page)">
						<a target="_blank">
							<xsl:attribute name="href">
								<xsl:value-of select="@page"/>
							</xsl:attribute>
							<img src="images/response.png" alt="view response"/></a>
					</xsl:when>

			 		 <xsl:otherwise><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></xsl:otherwise>
			 	</xsl:choose>
	 		<xsl:text disable-output-escaping="yes">&lt;/td&gt;</xsl:text>

			<xsl:text disable-output-escaping="yes">&lt;td class="</xsl:text>
			<xsl:value-of select="$lineStyle" />
			<xsl:text disable-output-escaping="yes">" style="border-top:1px solid #999;"&gt;</xsl:text>
				<xsl:choose>
				<xsl:when test="string-length(@parameter1) &gt; 1">
					<xsl:value-of select="@parameter1"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>-</xsl:text>
				</xsl:otherwise>
				</xsl:choose>
			<xsl:text disable-output-escaping="yes">&lt;/td&gt;</xsl:text>

			<xsl:text disable-output-escaping="yes">&lt;td class="</xsl:text>
			<xsl:value-of select="$lineStyle" />
			<xsl:text disable-output-escaping="yes">" style="border-top:1px solid #999;"&gt;</xsl:text>
				<xsl:choose>
				<xsl:when test="string-length(@parameter2) &gt; 1">
					<xsl:value-of select="@parameter2"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>-</xsl:text>
				</xsl:otherwise>
				</xsl:choose>
			<xsl:text disable-output-escaping="yes">&lt;/td&gt;</xsl:text>

			<xsl:text disable-output-escaping="yes">&lt;td class="</xsl:text>
			<xsl:value-of select="$lineStyle" />
			<xsl:text disable-output-escaping="yes">" style="border-top:1px solid #999;" align="right"&gt;</xsl:text>
				&#32;
				<xsl:value-of select="@duration div 1000"/>
			<xsl:text disable-output-escaping="yes">&lt;/td&gt;</xsl:text>
		</tr>
		<xsl:if test="(count(./error)) &gt; 0">
			<tr>
				<td class="light"/>
				<td class="light"/>
				<td class="light" style="text-align:right;">
					<xsl:variable name="tmpRow">
						<xsl:value-of select="@row"/>
					</xsl:variable>
					<xsl:for-each select="../command[((count(./error)) &gt; 0) and (@row &lt; $tmpRow)]">
						<a>
							<xsl:if test="position()=last()">
								<xsl:attribute name="href">
									<xsl:text>#</xsl:text>
									<xsl:value-of select="$currentCaseNo"/>
									<xsl:text>_</xsl:text>
									<xsl:value-of select="@row"/>
								</xsl:attribute>
								<img src="images/previous.png" width="11" height="10" alt="previous error"/>
							</xsl:if>
						</a>
					</xsl:for-each>
					<xsl:for-each select="../command[((count(./error)) &gt; 0) and (@row &gt; $tmpRow)]">
						<a>
							<xsl:if test="position()=1">
								<xsl:attribute name="href">
									<xsl:text>#</xsl:text>
									<xsl:value-of select="$currentCaseNo"/>
									<xsl:text>_</xsl:text>
									<xsl:value-of select="@row"/>
								</xsl:attribute>
								<img src="images/next.png" width="11" height="10" alt="next error"/>
							</xsl:if>
						</a>
					</xsl:for-each>
				</td>
				<td class="light"/>
				<td class="error" colspan="4">
					<xsl:value-of select="error/errormessage"/>
				</td>
			</tr>
		</xsl:if>

		<xsl:for-each select="messages/message">
			<tr>
				<td class="light"/>
				<td class="light"/>
				<td class="light"/>
				<td class="light"/>
				<td class="message" colspan="4">
					<xsl:value-of select="."/>
				</td>
			</tr>
		</xsl:for-each>
 		 <xsl:if test="(count(./testcase)) &gt; 0">
	 		 <tr style="display: none">
 		 		 <xsl:attribute name="id">
	 		 		 <xsl:text>tr_</xsl:text>
	 		 		 <xsl:number from="/exceltests" level="any"/>
 		 		 </xsl:attribute>
 		 		 <td class="light"/>
 		 		 <td class="light"/>
 		 		 <td class="light"/>
 		 		 <td class="light"/>
 		 		 <td class="light" colspan="4">
	 		 		 <xsl:for-each select="testcase">
 		 		 		 <xsl:call-template name="testcasetable"/>
	 		 		 </xsl:for-each>
 		 		 </td>
	 		 </tr>
 		 </xsl:if>

	</xsl:template>

	<xsl:template name="successIndicator">
		<xsl:choose>
			<xsl:when test="(count(./command/error)) &gt; 0">
				<img src="images/failed.png" width="12" height="10" alt="failed"/>
			</xsl:when>
			<xsl:otherwise>
				<img src="images/ok.png" width="12" height="10" alt="ok"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>


	<xsl:template name="colorBar">
		<xsl:param name="percentage"/>
		<xsl:param name="color"/>
		<xsl:param name="title"/>

		<td width="2px" class="light"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
		<td width="80%" class="light">
			<xsl:if test="$percentage > 0">
				<table width="{$percentage}%" style="border:1px solid #999;" border="0" cellpadding="0" cellspacing="0">
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
			</xsl:if>
		</td>
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
				<xsl:value-of select="$msecs div 1000"/>
				<xsl:text> seconds</xsl:text>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>



</xsl:stylesheet>
