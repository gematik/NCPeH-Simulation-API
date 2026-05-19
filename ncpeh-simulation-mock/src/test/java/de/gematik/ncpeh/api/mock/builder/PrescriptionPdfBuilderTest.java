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

import static org.junit.jupiter.api.Assertions.*;

import de.gematik.ncpeh.api.mock.data.Medication;
import java.io.IOException;
import java.util.List;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class PrescriptionPdfBuilderTest {

  @Test
  void whenSettingDocumentTitle_shouldSetTitleAndReturnSelf() {
    var builder = PrescriptionPdfBuilder.newInstance();
    var title = "Test Title";
    var result = builder.documentTitle(title);
    assertSame(builder.self(), result);
    assertEquals(title, result.documentTitle());
  }

  @Test
  void whenSettingKvnr_shouldSetKvnrAndReturnSelf() {
    var builder = PrescriptionPdfBuilder.newInstance();
    var kvnr = "X777777777";
    var result = builder.kvnr(kvnr);
    assertSame(builder.self(), result);
    assertEquals(kvnr, result.kvnr);
  }

  @Test
  void writeContent_shouldWriteToContentStream() throws IOException {
    var builder =
        PrescriptionPdfBuilder.newInstance()
            .name("John Doe")
            .birthdate("1990-01-01")
            .kvnr("X123456789")
            .medications(
                List.of(
                    new Medication("Medication A", "123456", true),
                    new Medication("Medication B", "654321", false)));

    var contentStreamMock = Mockito.mock(PDPageContentStream.class);

    assertDoesNotThrow(() -> builder.writeContent(contentStreamMock));

    Mockito.verify(contentStreamMock).showText("Prescription Document");
    Mockito.verify(contentStreamMock).showText("Name: John Doe");
    Mockito.verify(contentStreamMock).showText("Date of Birth: 1990-01-01");
    Mockito.verify(contentStreamMock).showText("KVNR: X123456789");
    Mockito.verify(contentStreamMock).showText("Product Name: Medication A");
    Mockito.verify(contentStreamMock).showText("PZN: 123456");
    Mockito.verify(contentStreamMock).showText("Substitution Allowed: Yes");
    Mockito.verify(contentStreamMock).showText("Product Name: Medication B");
    Mockito.verify(contentStreamMock).showText("PZN: 654321");
    Mockito.verify(contentStreamMock).showText("Substitution Allowed: No");
  }

  @Test
  void newInstance_shouldSetDefaultDocumentTitle() {
    var builder = PrescriptionPdfBuilder.newInstance();
    assertEquals(PrescriptionPdfBuilder.DEFAULT_TITLE, builder.documentTitle());
  }
}
