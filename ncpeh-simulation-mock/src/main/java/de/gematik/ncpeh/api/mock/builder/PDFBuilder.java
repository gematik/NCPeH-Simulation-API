/*
 * Copyright 2025 gematik GmbH
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
 * For additional notes and disclaimer from gematik and in case of changes by gematik find details in the "Readme" file.
 */

package de.gematik.ncpeh.api.mock.builder;

import static org.apache.pdfbox.pdmodel.font.Standard14Fonts.FontName.HELVETICA_BOLD;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Optional;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

@Slf4j
@Data
@Accessors(fluent = true)
public final class PDFBuilder {

  // Full name of the patient
  private String name;
  private String birthdate;

  private PDFBuilder() {}

  public static PDFBuilder builder() {
    return new PDFBuilder();
  }

  public byte[] build() {
    // Create a ByteArrayOutputStream to hold the PDF data
    final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

    // Create a new document
    try (final PDDocument document = new PDDocument()) {

      // Create a new page
      final PDPage page = new PDPage(PDRectangle.A4);
      document.addPage(page);

      // Access the trailer dictionary
      final COSDictionary trailer = document.getDocument().getTrailer();

      // Create a custom ID
      final COSArray idArray = new COSArray();
      idArray.add(new COSString("custom-id-part-1"));
      idArray.add(new COSString("custom-id-part-2"));

      // Set the ID in the trailer dictionary, need to generate pdfs with same ID for testing
      trailer.setItem(COSName.ID, idArray);

      // Start a new content stream for the page
      try (final PDPageContentStream contentStream = new PDPageContentStream(document, page)) {

        // Set font and size
        contentStream.setFont(new PDType1Font(HELVETICA_BOLD), 12);

        // Write text to the PDF
        contentStream.beginText();
        contentStream.setLeading(14.5f);
        contentStream.newLineAtOffset(100, 700);

        contentStream.showText("NCPeH Patient Summary CDA");
        contentStream.newLine();
        contentStream.showText("Patient Name: ".concat(Optional.ofNullable(name).orElse("")));
        contentStream.newLine();
        contentStream.showText("Birthday: ".concat(Optional.ofNullable(birthdate).orElse("")));
        contentStream.endText();
      }

      // Save the PDF document to the ByteArrayOutputStream
      document.save(byteArrayOutputStream);

    } catch (final IOException e) {
      log.error("Error while reading PDF", e);
    }

    return byteArrayOutputStream.toByteArray();
  }
}
