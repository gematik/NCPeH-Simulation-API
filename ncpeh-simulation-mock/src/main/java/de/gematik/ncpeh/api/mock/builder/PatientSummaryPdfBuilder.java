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

import java.io.IOException;
import java.util.Optional;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

/** Builder for Patient Summary PDF documents. */
@Data(staticConstructor = "newInstance")
@EqualsAndHashCode(callSuper = true)
@Accessors(fluent = true)
public final class PatientSummaryPdfBuilder extends BasePdfBuilder<PatientSummaryPdfBuilder> {

  private PatientSummaryPdfBuilder() {
    documentTitle = "NCPeH Patient Summary CDA";
  }

  @Override
  protected void writeContent(final PDPageContentStream contentStream) throws IOException {
    contentStream.showText(documentTitle);
    contentStream.newLine();
    contentStream.showText("Patient Name: ".concat(Optional.ofNullable(name).orElse("")));
    contentStream.newLine();
    contentStream.showText("Birthday: ".concat(Optional.ofNullable(birthdate).orElse("")));
    contentStream.endText();
  }
}
