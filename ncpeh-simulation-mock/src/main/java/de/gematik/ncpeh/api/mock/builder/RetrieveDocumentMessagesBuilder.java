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

import de.gematik.ncpeh.api.mock.data.Medication;
import de.gematik.ncpeh.api.mock.data.Patient;
import de.gematik.ncpeh.api.mock.util.PrescriptionCda3Utils;
import de.gematik.ncpeh.api.mock.util.XmlUtils;
import de.gematik.ncpeh.api.request.RetrieveDocumentRequest;
import de.gematik.ncpeh.api.request.RetrieveSetOfDocumentsRequest;
import de.gematik.ncpeh.ehdsi.valuesets.EhdsiSubstitutionCode;
import ihe.iti.xds_b._2007.RetrieveDocumentSetRequestType;
import ihe.iti.xds_b._2007.RetrieveDocumentSetResponseType;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import oasis.names.tc.ebxml_regrep.xsd.rs._3.RegistryResponseType;
import org.apache.commons.lang3.function.TriFunction;
import org.hl7.v3.BinaryDataEncoding;
import org.hl7.v3.ClinicalDocument;
import org.hl7.v3.ED;
import org.springframework.http.MediaType;

@Data
@Accessors(fluent = true)
public final class RetrieveDocumentMessagesBuilder {

  private static final String COMMENT_REGEX = "(?s)<!--.*?-->";
  private static final String PATIENT_SUMMARY_CDA_3_FILE_NAME = "Patient_Summary_CDA3.xml";
  private static final String PRESCRIPTION_CDA1_FILE_NAME = "Prescription_CDA1.xml";
  private static final String PRESCRIPTION_CDA3_FILE_NAME = "Prescription_CDA3.xml";
  private static final String STATUS_SUCCESS =
      "urn:oasis:names:tc:ebxml-regrep:ResponseStatusType:Success";

  record DocumentationInfo(
      String documentUniqueId, String repositoryUniqueId, String homeCommunityId) {}

  private List<DocumentationInfo> documentationInfos = new ArrayList<>();
  private Map<String, Medication> medicationByPrescriptionId;
  private Patient patient;

  private RetrieveDocumentMessagesBuilder() {}

  public static RetrieveDocumentMessagesBuilder fromRequest(
      final RetrieveDocumentRequest retrieveDocumentRequest) {
    return new RetrieveDocumentMessagesBuilder()
        .documentationInfos(
            Stream.of(
                    new DocumentationInfo(
                        retrieveDocumentRequest.documentUniqueId(),
                        retrieveDocumentRequest.repositoryUniqueId(),
                        retrieveDocumentRequest.homeCommunityId()),
                    new DocumentationInfo(
                        retrieveDocumentRequest.additionalDocumentUniqueId(),
                        retrieveDocumentRequest.repositoryUniqueId(),
                        retrieveDocumentRequest.homeCommunityId()))
                .filter(
                    info -> info.documentUniqueId() != null && !info.documentUniqueId().isBlank())
                .toList());
  }

  public static RetrieveDocumentMessagesBuilder buildFromRequestAndPatient(
      final RetrieveDocumentRequest retrieveDocumentRequest, final Patient patient) {
    return fromRequest(retrieveDocumentRequest).patient(patient);
  }

  public static RetrieveDocumentMessagesBuilder fromRequest(
      final RetrieveSetOfDocumentsRequest retrieveSetOfDocumentsRequest) {
    return new RetrieveDocumentMessagesBuilder()
        .documentationInfos(
            retrieveSetOfDocumentsRequest.documentRequestSet().stream()
                .map(
                    req ->
                        new DocumentationInfo(
                            req.documentUniqueId(),
                            req.repositoryUniqueId(),
                            req.homeCommunityId()))
                .toList());
  }

  public RetrieveDocumentSetRequestType buildRequest() {
    final var request = new RetrieveDocumentSetRequestType();
    documentationInfos.forEach(
        info -> request.getDocumentRequest().add(buildDocumentRequest(info)));
    return request;
  }

  public RetrieveDocumentSetResponseType buildResponse() {
    final var response = new RetrieveDocumentSetResponseType();

    final var registryResponse = new RegistryResponseType();
    registryResponse.setStatus(STATUS_SUCCESS);

    response.setRegistryResponse(registryResponse);

    documentationInfos.forEach(
        info -> {
          var medication =
              medicationByPrescriptionId == null
                  ? null
                  : medicationByPrescriptionId.get(
                      PrescriptionCda3Utils.extractPrescriptionIdFromDocumentUid(
                          info.documentUniqueId()));
          response.getDocumentResponse().add(buildDocumentResponse(info, medication));
        });

    return response;
  }

  private RetrieveDocumentSetRequestType.DocumentRequest buildDocumentRequest(
      final DocumentationInfo info) {
    final var documentRequest = new RetrieveDocumentSetRequestType.DocumentRequest();

    documentRequest.setDocumentUniqueId(info.documentUniqueId);
    documentRequest.setRepositoryUniqueId(info.repositoryUniqueId);
    documentRequest.setHomeCommunityId(info.homeCommunityId);

    return documentRequest;
  }

  private RetrieveDocumentSetResponseType.DocumentResponse buildDocumentResponse(
      final DocumentationInfo info, final Medication medication) {
    final var documentResponse = new RetrieveDocumentSetResponseType.DocumentResponse();

    documentResponse.setDocumentUniqueId(info.documentUniqueId);
    documentResponse.setRepositoryUniqueId(info.repositoryUniqueId);
    documentResponse.setHomeCommunityId(info.homeCommunityId);

    final var lvlInfo = CDALevelInfo.fromDocumentUniqueId(info.documentUniqueId);

    documentResponse.setMimeType(lvlInfo.mimeType());
    documentResponse.setDocument(
        lvlInfo.readOrCreateDocument(patient, info.documentUniqueId, medication));

    return documentResponse;
  }

  @RequiredArgsConstructor
  @Getter
  @Accessors(fluent = true)
  @Slf4j
  enum CDALevelInfo {
    PS_LEVEL_1(
        "^PS.PDF",
        MediaType.APPLICATION_PDF_VALUE,
        (pat, ignored1, ignored2) ->
            PatientSummaryPdfBuilder.newInstance()
                .name(Optional.ofNullable(pat).map(p -> p.name().toString()).orElse(""))
                .birthdate(
                    Optional.ofNullable(pat)
                        .map(p -> p.birthdate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")))
                        .orElse(""))
                .build()),
    PS_LEVEL_3(
        "^PS.XML",
        MediaType.TEXT_XML_VALUE,
        (ignored1, ignored2, ignored3) -> {
          log.debug("name LEVEL_3");
          // remove comments from xml file
          byte[] bytes = readTemplateFile(PATIENT_SUMMARY_CDA_3_FILE_NAME);
          return new String(bytes, StandardCharsets.UTF_8)
              .replaceAll(COMMENT_REGEX, "")
              .getBytes(StandardCharsets.UTF_8);
        }),
    EP_LEVEL_1(
        "|eP.PDF",
        MediaType.TEXT_XML_VALUE,
        (pat, documentUid, medication) -> {
          var clinicalDocument =
              XmlUtils.unmarshal(ClinicalDocument.class, prescriptionCda1Template());

          var pdf =
              PrescriptionPdfBuilder.newInstance()
                  .name(pat.name().toString())
                  .birthdate(pat.birthdate().format(DateTimeFormatter.ofPattern("dd.MM.uuuu")))
                  .kvnr(pat.kvnr())
                  .medications(List.of(medication))
                  .build();

          clinicalDocument
              .getComponent()
              .getNonXMLBody()
              .withText(
                  new ED()
                      .withMediaType("application/pdf")
                      .withRepresentation(BinaryDataEncoding.B_64)
                      .withContent(Base64.getEncoder().encodeToString(pdf)));

          return XmlUtils.marshal(clinicalDocument);
        }),
    EP_LEVEL_3(
        "|eP.XML",
        MediaType.TEXT_XML_VALUE,
        (pat, documentUid, medication) -> {
          var clinicalDocument =
              XmlUtils.unmarshal(ClinicalDocument.class, prescriptionCda3Template());

          PrescriptionCda3Utils.setDocumentUid(clinicalDocument, documentUid);

          if (medication != null) {
            PrescriptionCda3Utils.setMedicationName(clinicalDocument, medication.name());
            PrescriptionCda3Utils.setPzn(clinicalDocument, medication.pzn());
            PrescriptionCda3Utils.setPznDisplayName(clinicalDocument, medication.name());
            PrescriptionCda3Utils.setSubstitutionValue(
                clinicalDocument,
                medication.substitutionAllowed()
                    ? EhdsiSubstitutionCode.GENERIC
                    : EhdsiSubstitutionCode.NONE);
          }

          if (pat != null) {
            PrescriptionCda3Utils.setPatientFirstName(clinicalDocument, pat.name().givennames());
            PrescriptionCda3Utils.setPatientLastName(clinicalDocument, pat.name().lastnames());
            PrescriptionCda3Utils.setPatientDateOfBirth(clinicalDocument, pat.birthdate());
          }

          return XmlUtils.marshal(clinicalDocument);
        });

    @Getter(lazy = true)
    private static final byte[] prescriptionCda3Template =
        readTemplateFile(PRESCRIPTION_CDA3_FILE_NAME);

    @Getter(lazy = true)
    private static final byte[] prescriptionCda1Template =
        readTemplateFile(PRESCRIPTION_CDA1_FILE_NAME);

    private final String idMarker;
    private final String mimeType;
    private final TriFunction<Patient, String, Medication, byte[]> readOrCreateDocumentFunction;

    public boolean documentIsOfLevel(final String documentUniqueId) {
      return documentUniqueId.endsWith(idMarker());
    }

    public byte[] readOrCreateDocument(
        final Patient patient, final String documentUid, final Medication medication) {
      return readOrCreateDocumentFunction.apply(patient, documentUid, medication);
    }

    public static CDALevelInfo fromDocumentUniqueId(@NonNull final String documentUniqueId) {
      return Arrays.stream(CDALevelInfo.values())
          .filter(psl -> psl.documentIsOfLevel(documentUniqueId))
          .findFirst()
          .orElseThrow(
              () ->
                  new IllegalArgumentException(
                      "DocumentUniqueId " + documentUniqueId + " is of no known CDA Level"));
    }

    private static byte[] readTemplateFile(final String fileName) {
      try (final InputStream io = CDALevelInfo.class.getResourceAsStream(fileName)) {
        return Objects.requireNonNull(io).readAllBytes();
      } catch (final IOException e) {
        throw new RuntimeException(e);
      }
    }
  }
}
