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


package org.wetator.util;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

import javax.swing.text.BadLocationException;

import org.apache.commons.io.IOUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.junit.Test;

/**
 * Test for {@link ContentUtil}.
 *
 * @author rbri
 */
public class ContentUtilTest {

  @Test
  public void getPdfTitleAsString() throws IOException {
    final String tmpTitle = ContentUtil
        .getPdfTitleAsString(new FileInputStream("test/webpage/download/wet_test_title.pdf"));
    org.junit.Assert.assertEquals("WETATOR Titel Test", tmpTitle);
  }

  @Test
  public void getPdfTitleAsStringEmpty() throws IOException {
    final String tmpTitle = ContentUtil.getPdfTitleAsString(new FileInputStream("test/webpage/download/wet_test.pdf"));
    org.junit.Assert.assertEquals("", tmpTitle);
  }

  @Test
  public void getPdfTitleAsStringEncryptedReadable() throws IOException {
    final String tmpTitle = ContentUtil
        .getPdfTitleAsString(new FileInputStream("test/webpage/download/not_editable.pdf"));
    org.junit.Assert.assertEquals("WETATOR PDF Test", tmpTitle);
  }

  @Test
  public void getPdfTitleAsStringEncryptedNotReadable() throws IOException {
    final String tmpTitle = ContentUtil
        .getPdfTitleAsString(new FileInputStream("test/webpage/download/can_not_extract_content.pdf"));
    org.junit.Assert.assertEquals("WETATOR PDF Test", tmpTitle);
  }

  @Test
  public void getPdfContentAsString() throws IOException {
    final StringBuilder tmpExpected = new StringBuilder();
    tmpExpected.append("This is the content of a simple PDF file.");
    tmpExpected.append(" ");
    tmpExpected.append("This file is used to test WeT.");

    final String tmpContent = ContentUtil
        .getPdfContentAsString(new FileInputStream("test/webpage/download/wet_test.pdf"), 4000);
    org.junit.Assert.assertEquals(tmpExpected.toString(), tmpContent);
  }

  @Test
  public void getPdfContentAsStringEncryptedReadable() throws FileNotFoundException, IOException {
    final String tmpContent = ContentUtil
        .getPdfContentAsString(new FileInputStream("test/webpage/download/not_editable.pdf"), 40);
    org.junit.Assert.assertEquals("WETATOR", tmpContent);
  }

  @Test
  public void getPdfContentAsStringEncryptedNotReadable() {
    try {
      System.out.println(ContentUtil
          .getPdfContentAsString(new FileInputStream("test/webpage/download/can_not_extract_content.pdf"), 40));
      org.junit.Assert.fail("IOException expected");
    } catch (final IOException e) {
      org.junit.Assert.assertEquals("Content extraction forbidden for the given PDF document.", e.getMessage());
    }
  }

  @Test
  public void getZippedPdfContentAsString() throws IOException {
    final StringBuilder tmpExpected = new StringBuilder();
    tmpExpected.append("[wet_test.pdf]");
    tmpExpected.append(" ");
    tmpExpected.append("This is the content of a simple PDF file.");
    tmpExpected.append(" ");
    tmpExpected.append("This file is used to test WeT.");

    final String tmpContent = ContentUtil.getZipContentAsString(
        new FileInputStream("test/webpage/download/wet_test_pdf.zip"), StandardCharsets.UTF_8, Locale.ENGLISH, 4000);
    org.junit.Assert.assertEquals(tmpExpected.toString(), tmpContent);
  }

  @Test
  public void getPdfContentAsStringError() {
    try {
      ContentUtil.getPdfContentAsString(new FileInputStream("test/webpage/download/wet_test.xls"), 4000);
      org.junit.Assert.fail("IOException expected");
    } catch (final IOException e) {
      org.junit.Assert.assertEquals("java.io.IOException: Error: Header doesn't contain versioninfo", e.toString());
    }
  }

  @Test
  public void getZippedPdfContentAsStringError() {
    try {
      ContentUtil.getZipContentAsString(new FileInputStream("test/webpage/download/wet_test_pdf_error.zip"),
          StandardCharsets.UTF_8, Locale.ENGLISH, 4000);
      org.junit.Assert.fail("IOException expected");
    } catch (final IOException e) {
      org.junit.Assert.assertEquals("java.io.IOException: Can't convert the zipped pdf 'wet_test.pdf' into text "
          + "(reason: java.io.IOException: Error: Header doesn't contain versioninfo).", e.toString());
    }
  }

  @Test
  public void getRtfContentAsString() throws IOException, BadLocationException {
    final StringBuilder tmpExpected = new StringBuilder();
    tmpExpected.append("Wetator is great.");

    final String tmpContent = ContentUtil
        .getRtfContentAsString(new FileInputStream("test/webpage/download/wet_test.rtf"), 4000);
    org.junit.Assert.assertEquals(tmpExpected.toString(), tmpContent);
  }

  @Test
  public void getZippedRtfContentAsString() throws IOException {
    final StringBuilder tmpExpected = new StringBuilder();
    tmpExpected.append("[wet_test.rtf]");
    tmpExpected.append(" ");
    tmpExpected.append("Wetator is great.");

    final String tmpContent = ContentUtil.getZipContentAsString(
        new FileInputStream("test/webpage/download/wet_test_rtf.zip"), StandardCharsets.UTF_8, Locale.ENGLISH, 4000);
    org.junit.Assert.assertEquals(tmpExpected.toString(), tmpContent);
  }

  @Test
  public void getRtfContentAsStringError() throws IOException, BadLocationException {
    final String tmpContent = ContentUtil
        .getRtfContentAsString(new FileInputStream("test/webpage/download/wet_test.xls"), 4000);
    org.junit.Assert.assertEquals("", tmpContent);
  }

  @Test
  public void getWordContentAsString() throws IOException, InvalidFormatException {
    final StringBuilder tmpExpected = new StringBuilder();
    tmpExpected.append("Wetator is great.");

    final String tmpContent = ContentUtil
        .getWordContentAsString(new FileInputStream("test/webpage/download/wet_test.docx"), 4000);
    org.junit.Assert.assertEquals(tmpExpected.toString(), tmpContent);
  }

  @Test
  public void getZippedWordContentAsString() throws IOException {
    final StringBuilder tmpExpected = new StringBuilder();
    tmpExpected.append("[wet_test.docx]");
    tmpExpected.append(" ");
    tmpExpected.append("Wetator is great.");

    final String tmpContent = ContentUtil.getZipContentAsString(
        new FileInputStream("test/webpage/download/wet_test_docx.zip"), StandardCharsets.UTF_8, Locale.ENGLISH, 4000);
    org.junit.Assert.assertEquals(tmpExpected.toString(), tmpContent);
  }

  @Test
  public void getWordContentAsStringError() throws IOException {
    try {
      ContentUtil.getWordContentAsString(new FileInputStream("test/webpage/download/wet_test.xls"), 4000);
      org.junit.Assert.fail("InvalidFormatException expected");
    } catch (final InvalidFormatException e) {
      // expected
    }
  }

  @Test
  public void getXlsContentAsStringDE() throws IOException, InvalidFormatException {
    final StringBuilder tmpExpected = new StringBuilder();
    tmpExpected.append("[Tab1] Wetator Page 1");
    tmpExpected.append(" ");
    tmpExpected.append("[Tab2] Wetator Test Page2 Web application testing is fun");
    tmpExpected.append(" ");
    tmpExpected.append("[Data Test]");
    tmpExpected.append(" String plain text");
    tmpExpected.append(" String(int) 4711");
    tmpExpected.append(" int 123");
    tmpExpected.append(" float 14,3");
    tmpExpected.append(" float (rounded) 1,70");
    tmpExpected.append(" currency 4,33 ???");
    tmpExpected.append(" percent 3%");
    tmpExpected.append(" date 7/14/11");
    tmpExpected.append(" date (formated) 14-Jul-11");
    tmpExpected.append(" formula 124,70");

    final String tmpContent = ContentUtil
        .getExcelContentAsString(new FileInputStream("test/webpage/download/wet_test.xls"), Locale.GERMAN, 4000);
    org.junit.Assert.assertEquals(tmpExpected.toString(), tmpContent);
  }

  @Test
  public void getXlsContentAsStringEN() throws IOException, InvalidFormatException {
    final StringBuilder tmpExpected = new StringBuilder();
    tmpExpected.append("[Tab1] Wetator Page 1");
    tmpExpected.append(" ");
    tmpExpected.append("[Tab2] Wetator Test Page2 Web application testing is fun");
    tmpExpected.append(" ");
    tmpExpected.append("[Data Test]");
    tmpExpected.append(" String plain text");
    tmpExpected.append(" String(int) 4711");
    tmpExpected.append(" int 123");
    tmpExpected.append(" float 14.3");
    tmpExpected.append(" float (rounded) 1.70");
    tmpExpected.append(" currency 4.33 ???");
    tmpExpected.append(" percent 3%");
    tmpExpected.append(" date 7/14/11");
    tmpExpected.append(" date (formated) 14-Jul-11");
    tmpExpected.append(" formula 124.70");

    final String tmpContent = ContentUtil
        .getExcelContentAsString(new FileInputStream("test/webpage/download/wet_test.xls"), Locale.ENGLISH, 4000);
    org.junit.Assert.assertEquals(tmpExpected.toString(), tmpContent);
  }

  @Test
  public void getZippedXlsContentAsStringDE() throws IOException {
    final StringBuilder tmpExpected = new StringBuilder();
    tmpExpected.append("[wet_test.xls]");
    tmpExpected.append(" ");
    tmpExpected.append("[Tab1] Wetator Page 1");
    tmpExpected.append(" ");
    tmpExpected.append("[Tab2] Wetator Test Page2 Web application testing is fun");
    tmpExpected.append(" ");
    tmpExpected.append("[Data Test]");
    tmpExpected.append(" String plain text");
    tmpExpected.append(" String(int) 4711");
    tmpExpected.append(" int 123");
    tmpExpected.append(" float 14,3");
    tmpExpected.append(" float (rounded) 1,70");
    tmpExpected.append(" currency 4,33 ???");
    tmpExpected.append(" percent 3%");
    tmpExpected.append(" date 7/14/11");
    tmpExpected.append(" date (formated) 14-Jul-11");
    tmpExpected.append(" formula 124,70");

    final String tmpContent = ContentUtil.getZipContentAsString(
        new FileInputStream("test/webpage/download/wet_test_xls.zip"), StandardCharsets.UTF_8, Locale.GERMAN, 4000);
    org.junit.Assert.assertEquals(tmpExpected.toString(), tmpContent);
  }

  @Test
  public void getXlsContentAsStringError() {
    try {
      ContentUtil.getExcelContentAsString(new FileInputStream("test/webpage/download/wet_test.pdf"), Locale.ENGLISH,
          4000);
      org.junit.Assert.fail("InvalidFormatException expected");
    } catch (final InvalidFormatException | IOException e) {
      org.junit.Assert.assertEquals(
          "java.io.IOException: Your InputStream was neither an OLE2 stream, nor an OOXML stream", e.toString());
    }
  }

  @Test
  public void getZippedXlsContentAsStringError() {
    try {
      ContentUtil.getZipContentAsString(new FileInputStream("test/webpage/download/wet_test_xls_error.zip"),
          StandardCharsets.UTF_8, Locale.ENGLISH, 4000);
      org.junit.Assert.fail("IOException expected");
    } catch (final IOException e) {
      org.junit.Assert.assertEquals(
          "java.io.IOException: Can't convert the zipped xls 'wet_test.xls' into text "
              + "(reason: java.io.IOException: Your InputStream was neither an OLE2 stream, nor an OOXML stream).",
          e.toString());
    }
  }

  @Test
  public void getXlsxContentAsStringDE() throws IOException, InvalidFormatException {
    final StringBuilder tmpExpected = new StringBuilder();
    tmpExpected.append("[Tab1] Wetator Page 1");
    tmpExpected.append(" ");
    tmpExpected.append("[Tab2] Wetator Test Page2 Web application testing is fun");
    tmpExpected.append(" ");
    tmpExpected.append("[Data Test]");
    tmpExpected.append(" String plain text");
    tmpExpected.append(" String(int) 4711");
    tmpExpected.append(" int 123");
    tmpExpected.append(" float 14,3");
    tmpExpected.append(" float (rounded) 1,70");
    tmpExpected.append(" currency 4,33 ???");
    tmpExpected.append(" percent 3%");
    tmpExpected.append(" date 7/14/11");
    tmpExpected.append(" date (formated) 14-Jul-11");
    tmpExpected.append(" formula 124,70");

    final String tmpContent = ContentUtil
        .getExcelContentAsString(new FileInputStream("test/webpage/download/wet_test.xlsx"), Locale.GERMAN, 4000);
    org.junit.Assert.assertEquals(tmpExpected.toString(), tmpContent);
  }

  @Test
  public void getXlsxContentAsStringEN() throws IOException, InvalidFormatException {
    final StringBuilder tmpExpected = new StringBuilder();
    tmpExpected.append("[Tab1] Wetator Page 1");
    tmpExpected.append(" ");
    tmpExpected.append("[Tab2] Wetator Test Page2 Web application testing is fun");
    tmpExpected.append(" ");
    tmpExpected.append("[Data Test]");
    tmpExpected.append(" String plain text");
    tmpExpected.append(" String(int) 4711");
    tmpExpected.append(" int 123");
    tmpExpected.append(" float 14.3");
    tmpExpected.append(" float (rounded) 1.70");
    tmpExpected.append(" currency 4.33 ???");
    tmpExpected.append(" percent 3%");
    tmpExpected.append(" date 7/14/11");
    tmpExpected.append(" date (formated) 14-Jul-11");
    tmpExpected.append(" formula 124.70");

    final String tmpContent = ContentUtil
        .getExcelContentAsString(new FileInputStream("test/webpage/download/wet_test.xlsx"), Locale.ENGLISH, 4000);
    org.junit.Assert.assertEquals(tmpExpected.toString(), tmpContent);
  }

  @Test
  public void getZippedXlsxContentAsStringDE() throws IOException {
    final StringBuilder tmpExpected = new StringBuilder();
    tmpExpected.append("[wet_test.xlsx]");
    tmpExpected.append(" ");
    tmpExpected.append("[Tab1] Wetator Page 1");
    tmpExpected.append(" ");
    tmpExpected.append("[Tab2] Wetator Test Page2 Web application testing is fun");
    tmpExpected.append(" ");
    tmpExpected.append("[Data Test]");
    tmpExpected.append(" String plain text");
    tmpExpected.append(" String(int) 4711");
    tmpExpected.append(" int 123");
    tmpExpected.append(" float 14,3");
    tmpExpected.append(" float (rounded) 1,70");
    tmpExpected.append(" currency 4,33 ???");
    tmpExpected.append(" percent 3%");
    tmpExpected.append(" date 7/14/11");
    tmpExpected.append(" date (formated) 14-Jul-11");
    tmpExpected.append(" formula 124,70");

    final String tmpContent = ContentUtil.getZipContentAsString(
        new FileInputStream("test/webpage/download/wet_test_xlsx.zip"), StandardCharsets.UTF_8, Locale.GERMAN, 4000);
    org.junit.Assert.assertEquals(tmpExpected.toString(), tmpContent);
  }

  @Test
  public void getXlsxContentAsStringError() {
    try {
      ContentUtil.getExcelContentAsString(new FileInputStream("test/webpage/download/wet_test.pdf"), Locale.ENGLISH,
          4000);
      org.junit.Assert.fail("InvalidFormatException expected");
    } catch (final InvalidFormatException | IOException e) {
      org.junit.Assert.assertEquals(
          "java.io.IOException: Your InputStream was neither an OLE2 stream, nor an OOXML stream", e.toString());
    }
  }

  @Test
  public void getZippedXlsxContentAsStringError() {
    try {
      ContentUtil.getZipContentAsString(new FileInputStream("test/webpage/download/wet_test_xlsx_error.zip"),
          StandardCharsets.UTF_8, Locale.ENGLISH, 4000);
      org.junit.Assert.fail("IOException expected");
    } catch (final IOException e) {
      org.junit.Assert.assertEquals(
          "java.io.IOException: Can't convert the zipped xls 'wet_test.xlsx' into text "
              + "(reason: java.io.IOException: Your InputStream was neither an OLE2 stream, nor an OOXML stream).",
          e.toString());
    }
  }

  @Test
  public void getTxtContentAsString() {
    final StringBuilder tmpExpected = new StringBuilder();
    tmpExpected.append("Some content line two Hallo Wetator.");

    final String tmpContent = ContentUtil.getTxtContentAsString("Some content\rline two\r\n\tHallo\tWetator.", 4000);
    org.junit.Assert.assertEquals(tmpExpected.toString(), tmpContent);
  }

  @Test
  public void getZippedTxtContentAsString() throws IOException {
    final StringBuilder tmpExpected = new StringBuilder();
    tmpExpected.append("[download.txt]");
    tmpExpected.append(" ");
    tmpExpected.append("This is the content of a simple text file. ");
    tmpExpected.append("This file is used to test WeT.");

    final String tmpContent = ContentUtil.getZipContentAsString(
        new FileInputStream("test/webpage/download/wet_test_txt.zip"), StandardCharsets.UTF_8, Locale.ENGLISH, 4000);
    org.junit.Assert.assertEquals(tmpExpected.toString(), tmpContent);
  }

  @Test
  public void getTxtContentAsStringFromStream() throws IOException {
    InputStream tmpInput = new FileInputStream("test/webpage/download/wet_test.pdf");
    String tmpContent = ContentUtil.getTxtContentAsString(tmpInput, StandardCharsets.UTF_8, 4000);
    org.junit.Assert.assertEquals(4004, tmpContent.length());
    org.junit.Assert.assertTrue(tmpContent, tmpContent.startsWith("%PDF-1.4"));

    tmpInput = new FileInputStream("test/webpage/download/wet_test.xls");
    tmpContent = ContentUtil.getTxtContentAsString(tmpInput, StandardCharsets.UTF_8, 4000);
    org.junit.Assert.assertEquals(4004, tmpContent.length());

    final StringWriter tmpWriter = new StringWriter();
    for (int i = 0; i < 44; i++) {
      tmpWriter.append("0123456789");
    }
    tmpInput = new ByteArrayInputStream(tmpWriter.toString().getBytes(StandardCharsets.UTF_8));
    tmpContent = ContentUtil.getTxtContentAsString(tmpInput, StandardCharsets.UTF_8, 4000);
    org.junit.Assert.assertEquals(440, tmpContent.length());
    org.junit.Assert.assertTrue(tmpContent, tmpContent.startsWith("0123456789"));
  }

  @Test
  public void isTxt() throws IOException {
    String tmpText = "Some readable text for testing WETATOR.";
    org.junit.Assert.assertTrue(ContentUtil.isTxt(tmpText));

    tmpText = IOUtils.toString(new FileInputStream("test/webpage/download/wet_test.pdf"), StandardCharsets.UTF_8);
    org.junit.Assert.assertFalse(ContentUtil.isTxt(tmpText));

    tmpText = IOUtils.toString(new FileInputStream("test/webpage/download/wet_test.xls"), StandardCharsets.UTF_8);
    org.junit.Assert.assertFalse(ContentUtil.isTxt(tmpText));
  }

  @Test
  public void getZippedContentAsStringMixedContent() throws IOException {
    final StringBuilder tmpExpected = new StringBuilder();
    tmpExpected.append("[wet_test.csv]");
    tmpExpected.append(" ");
    tmpExpected.append("Col1, Col2 text1, text2");

    tmpExpected.append(" ");
    tmpExpected.append("[wet_test.pdf]");
    tmpExpected.append(" ");
    tmpExpected.append("This is the content of a simple PDF file.");
    tmpExpected.append(" ");
    tmpExpected.append("This file is used to test WeT.");

    tmpExpected.append(" ");
    tmpExpected.append("[wet_test.xls]");
    tmpExpected.append(" ");
    tmpExpected.append("[Tab1] Wetator Page 1");
    tmpExpected.append(" ");
    tmpExpected.append("[Tab2] Wetator Test Page2 Web application testing is fun");
    tmpExpected.append(" ");
    tmpExpected.append("[Data Test]");
    tmpExpected.append(" String plain text");
    tmpExpected.append(" String(int) 4711");
    tmpExpected.append(" int 123");
    tmpExpected.append(" float 14,3");
    tmpExpected.append(" float (rounded) 1,70");
    tmpExpected.append(" currency 4,33 ???");
    tmpExpected.append(" percent 3%");
    tmpExpected.append(" date 7/14/11");
    tmpExpected.append(" date (formated) 14-Jul-11");
    tmpExpected.append(" formula 124,70");

    tmpExpected.append(" ");
    tmpExpected.append("[wet_test.xml]");
    tmpExpected.append(" ");
    tmpExpected.append("<?xml version=\"1.0\"?> <wetator> <Test>Simple xml content</Test> </wetator>");

    final String tmpContent = ContentUtil.getZipContentAsString(
        new FileInputStream("test/webpage/download/wet_test_mix.zip"), StandardCharsets.UTF_8, Locale.GERMAN, 4000);
    org.junit.Assert.assertEquals(tmpExpected.toString(), tmpContent);
  }

  @Test
  public void determineLocaleFromRequestNull() {
    final Locale tmpLocale = ContentUtil.determineLocaleFromRequestHeader(null);
    org.junit.Assert.assertNull(tmpLocale);
  }

  @Test
  public void determineLocaleFromRequest() {
    final Locale tmpLocale = ContentUtil.determineLocaleFromRequestHeader("de");
    org.junit.Assert.assertEquals(Locale.GERMAN, tmpLocale);
  }

  @Test
  public void determineLocaleFromRequest2() {
    final Locale tmpLocale = ContentUtil.determineLocaleFromRequestHeader("DE");
    org.junit.Assert.assertEquals(Locale.GERMAN, tmpLocale);
  }

  @Test
  public void determineLocaleFromRequestEmpty() {
    final Locale tmpLocale = ContentUtil.determineLocaleFromRequestHeader("");
    org.junit.Assert.assertNull(tmpLocale);
  }

  @Test
  public void determineLocaleFromRequestUnknown() {
    final Locale tmpLocale = ContentUtil.determineLocaleFromRequestHeader("1234");
    org.junit.Assert.assertNull(tmpLocale);
  }

  @Test
  public void determineLocaleFromRequestMany() {
    final Locale tmpLocale = ContentUtil.determineLocaleFromRequestHeader("da,en");
    org.junit.Assert.assertEquals(new Locale("da", ""), tmpLocale);
  }

  @Test
  public void determineLocaleFromRequestOperaStyle() {
    final Locale tmpLocale = ContentUtil.determineLocaleFromRequestHeader("da;1.0,en;0.9");
    org.junit.Assert.assertEquals(new Locale("da", ""), tmpLocale);
  }

  @Test
  public void determineLocaleFromRequestIEStyle() {
    final Locale tmpLocale = ContentUtil.determineLocaleFromRequestHeader("en-nz,de;q=0.5");
    org.junit.Assert.assertEquals(new Locale("en", "NZ"), tmpLocale);
  }

  @Test
  public void determineLocaleFromRequestIEStyle2() {
    final Locale tmpLocale = ContentUtil.determineLocaleFromRequestHeader("en-us");
    org.junit.Assert.assertEquals(new Locale("en", "US"), tmpLocale);
  }

  @Test
  public void determineLocaleFromRequestIEStyle3() {
    final Locale tmpLocale = ContentUtil.determineLocaleFromRequestHeader("en-gb");
    org.junit.Assert.assertEquals(new Locale("en", "GB"), tmpLocale);
  }
}
