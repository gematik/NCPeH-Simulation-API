/*
 * Copyright (Change Date see Readme), gematik GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ******
 *
 * For additional notes and disclaimer from gematik and in case of changes
 * by gematik, find details in the "Readme" file.
 */

package de.gematik.ncpeh.api.mock.builder;

import static org.apache.pdfbox.pdmodel.font.Standard14Fonts.FontName.HELVETICA_BOLD;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

/**
 * Base Builder for simple PDF documents used in the NCPeH Mock Server.
 *
 * @param <T> The concrete builder type extending this base class.
 */
@Slf4j
@Data
@Accessors(fluent = true)
public abstract class BasePdfBuilder<T extends BasePdfBuilder<T>> {

  // document info
  protected String documentTitle;

  // patient info
  protected String name;
  protected String birthdate;
  protected String kvnr;

  /** Protected constructor to prevent direct instantiation. */
  protected BasePdfBuilder() {}

  private static void applyBasicFormatting(final PDPageContentStream contentStream)
      throws IOException {
    contentStream.setFont(new PDType1Font(HELVETICA_BOLD), 12);
    contentStream.beginText();
    contentStream.setLeading(14.5f);
    contentStream.newLineAtOffset(100, 700);
  }

  @SuppressWarnings("unchecked")
  protected T self() {
    return (T) this;
  }

  public T documentTitle(final String documentTitle) {
    this.documentTitle = documentTitle;
    return self();
  }

  public T name(final String name) {
    this.name = name;
    return self();
  }

  public T birthdate(final String birthdate) {
    this.birthdate = birthdate;
    return self();
  }

  public T kvnr(final String kvnr) {
    this.kvnr = kvnr;
    return self();
  }

  /**
   * Builds the PDF document and returns it as a byte array.
   *
   * @return byte array representing the PDF document
   */
  public byte[] build() {
    var byteArrayOutputStream = new ByteArrayOutputStream();

    try (final var document = new PDDocument()) {
      var page = new PDPage(PDRectangle.A4);
      document.addPage(page);
      setCustomId(document);

      try (final var contentStream = new PDPageContentStream(document, page)) {
        applyBasicFormatting(contentStream);
        writeContent(contentStream);
      }

      document.save(byteArrayOutputStream);
    } catch (final IOException e) {
      log.error("Error while creating PDF", e);
    }

    return byteArrayOutputStream.toByteArray();
  }

  private void setCustomId(final PDDocument document) {
    var trailer = document.getDocument().getTrailer();
    var idArray = new COSArray();
    idArray.add(new COSString("custom-id-part-1"));
    idArray.add(new COSString("custom-id-part-2"));
    trailer.setItem(COSName.ID, idArray);
  }

  protected abstract void writeContent(PDPageContentStream contentStream) throws IOException;
}
