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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import lombok.SneakyThrows;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.Test;

class PDFBuilderTest {

  @Test
  void build_createsPDFWithCorrectContent() {
    // Arrange
    final PDFBuilder builder = PDFBuilder.builder().name("Max Patientmann").birthdate("20.02.1975");

    // Act
    final var testee = assertDoesNotThrow(() -> extractText(builder.build()));

    // Assert
    assertTrue(testee.contains("NCPeH Patient Summary CDA"));
    assertTrue(testee.contains("Patient Name: Max Patientmann"));
    assertTrue(testee.contains("Birthday: 20.02.1975"));
  }

  @Test
  void build_createsPDFWithSpecialCharacterContent() {
    // Arrange
    final PDFBuilder builder =
        PDFBuilder.builder()
            .name("Gräfin Maude Adelheid Lilo Johanna Gõdofský")
            .birthdate("20.02.1975");

    // Act
    final var testee = assertDoesNotThrow(() -> extractText(builder.build()));

    // Assert
    assertTrue(testee.contains("NCPeH Patient Summary CDA"));
    assertTrue(testee.contains("Patient Name: Gräfin Maude Adelheid Lilo Johanna Gõdofský"));
    assertTrue(testee.contains("Birthday: 20.02.1975"));
  }

  @SneakyThrows
  @Test
  void build_handlesEmptyFieldsGracefully() {
    // Arrange
    final PDFBuilder builder = PDFBuilder.builder();

    // Act
    final var testee = assertDoesNotThrow(() -> extractText(builder.build()));

    // Assert
    assertTrue(testee.contains("NCPeH Patient Summary CDA"));
    assertTrue(testee.contains("Patient Name: "));
    assertTrue(testee.contains("Birthday: "));
  }

  public String extractText(final byte[] data) {
    try (final PDDocument document = Loader.loadPDF(data)) {
      final PDFTextStripper pdfStripper = new PDFTextStripper();
      return pdfStripper.getText(document);
    } catch (final IOException e) {
      throw new RuntimeException("Failed to extract text from PDF", e);
    }
  }
}
