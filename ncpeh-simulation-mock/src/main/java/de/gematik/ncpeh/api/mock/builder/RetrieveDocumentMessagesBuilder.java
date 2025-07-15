/*
 * Copyright 2024-2025 gematik GmbH
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

import de.gematik.ncpeh.api.mock.data.Patient;
import de.gematik.ncpeh.api.request.RetrieveDocumentRequest;
import de.gematik.ncpeh.api.request.RetrieveSetOfDocumentsRequest;
import ihe.iti.xds_b._2007.RetrieveDocumentSetRequestType;
import ihe.iti.xds_b._2007.RetrieveDocumentSetResponseType;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import oasis.names.tc.ebxml_regrep.xsd.rs._3.RegistryResponseType;
import org.springframework.http.MediaType;

@Data
@Accessors(fluent = true)
public final class RetrieveDocumentMessagesBuilder {

  record DocumentationInfo(
      String documentUniqueId, String repositoryUniqueId, String homeCommunityId) {}

  private static final String STATUS_SUCCESS =
      "urn:oasis:names:tc:ebxml-regrep:ResponseStatusType:Success";

  private List<DocumentationInfo> documentationInfos = new ArrayList<>();

  private Patient patient;

  private RetrieveDocumentMessagesBuilder() {}

  public static RetrieveDocumentMessagesBuilder buildFromRequest(
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
    return buildFromRequest(retrieveDocumentRequest).patient(patient);
  }

  public static RetrieveDocumentMessagesBuilder buildFromRequestAndPatient(
      final RetrieveSetOfDocumentsRequest retrieveDocumentRequest, final Patient patient) {
    return new RetrieveDocumentMessagesBuilder()
        .documentationInfos(
            retrieveDocumentRequest.documentRequestSet().stream()
                .map(
                    req ->
                        new DocumentationInfo(
                            req.documentUniqueId(),
                            req.repositoryUniqueId(),
                            req.homeCommunityId()))
                .toList())
        .patient(patient);
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
        info -> response.getDocumentResponse().add(buildDocumentResponse(info)));

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
      final DocumentationInfo info) {
    final var documentResponse = new RetrieveDocumentSetResponseType.DocumentResponse();

    documentResponse.setDocumentUniqueId(info.documentUniqueId);
    documentResponse.setRepositoryUniqueId(info.repositoryUniqueId);
    documentResponse.setHomeCommunityId(info.homeCommunityId);

    final var lvlInfo = CDALevelInfo.fromDocumentUniqueId(info.documentUniqueId);

    documentResponse.setMimeType(lvlInfo.mimeType());
    documentResponse.setDocument(lvlInfo.readOrCreateDocument(patient));

    return documentResponse;
  }

  public static final String COMMENT_REGEX = "(?s)<!--.*?-->";

  @RequiredArgsConstructor
  @Getter
  @Accessors(fluent = true)
  @Slf4j
  enum CDALevelInfo {
    LEVEL_1(
        "^PS.PDF",
        MediaType.APPLICATION_PDF_VALUE,
        pat ->
            PDFBuilder.builder()
                .name(Optional.ofNullable(pat).map(p -> p.name().toString()).orElse(""))
                .birthdate(
                    Optional.ofNullable(pat)
                        .map(p -> p.birthdate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")))
                        .orElse(""))
                .build()),
    LEVEL_3(
        "^PS.XML",
        MediaType.TEXT_XML_VALUE,
        pat -> {
          try (final InputStream io =
              HttpMessageFactory.readMessageFileSafely("Patient_Summary_CDA3.xml")) {
            log.debug("name LEVEL_3");
            // remove comments from xml file
            return new String(io.readAllBytes(), StandardCharsets.UTF_8)
                .replaceAll(COMMENT_REGEX, "")
                .getBytes(StandardCharsets.UTF_8);
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        });

    private final String idMarker;

    private final String mimeType;

    private final Function<Patient, byte[]> readOrCreateDocumentFunction;

    public boolean documentIsOfLevel(final String documentUniqueId) {
      return documentUniqueId.endsWith(idMarker());
    }

    public byte[] readOrCreateDocument(final Patient patient) {
      return readOrCreateDocumentFunction.apply(patient);
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
  }
}
