/*
 * Copyright (c) 2024-2025 gematik GmbH
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
 */

package de.gematik.ncpeh.api.mock.builder;

import de.gematik.ncpeh.api.request.RetrieveDocumentRequest;
import ihe.iti.xds_b._2007.RetrieveDocumentSetRequestType;
import ihe.iti.xds_b._2007.RetrieveDocumentSetResponseType;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Optional;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import oasis.names.tc.ebxml_regrep.xsd.rs._3.RegistryResponseType;
import org.springframework.http.MediaType;

@Data
@Accessors(fluent = true)
public class RetrieveDocumentMessagesBuilder {

  private static final String STATUS_SUCCESS =
      "urn:oasis:names:tc:ebxml-regrep:ResponseStatusType:Success";

  private String documentUniqueId;

  private String additionalDocumentUniqueId;

  private String repositoryUniqueId;

  private String homeCommunityId = "urn:oid:1.2.276.0.76.4.291";

  public RetrieveDocumentMessagesBuilder useDataFrom(
      final RetrieveDocumentRequest retrieveDocumentRequest) {
    return documentUniqueId(retrieveDocumentRequest.documentUniqueId())
        .additionalDocumentUniqueId(retrieveDocumentRequest.additionalDocumentUniqueId())
        .homeCommunityId(retrieveDocumentRequest.homeCommunityId())
        .repositoryUniqueId(retrieveDocumentRequest.repositoryUniqueId());
  }

  public RetrieveDocumentSetRequestType buildRequest() {
    final var request = new RetrieveDocumentSetRequestType();

    Optional.ofNullable(documentUniqueId())
        .ifPresent(duid -> request.getDocumentRequest().add(buildDocumentRequest(duid)));
    Optional.ofNullable(additionalDocumentUniqueId())
        .ifPresent(aduid -> request.getDocumentRequest().add(buildDocumentRequest(aduid)));

    return request;
  }

  public RetrieveDocumentSetResponseType buildResponse() {
    final var response = new RetrieveDocumentSetResponseType();

    final var registryResponse = new RegistryResponseType();
    registryResponse.setStatus(STATUS_SUCCESS);

    response.setRegistryResponse(registryResponse);

    Optional.ofNullable(documentUniqueId())
        .ifPresent(duid -> response.getDocumentResponse().add(buildDocumentResponse(duid)));
    Optional.ofNullable(additionalDocumentUniqueId())
        .ifPresent(aduid -> response.getDocumentResponse().add(buildDocumentResponse(aduid)));

    return response;
  }

  private RetrieveDocumentSetRequestType.DocumentRequest buildDocumentRequest(
      final String uniqueId) {
    final var documentRequest = new RetrieveDocumentSetRequestType.DocumentRequest();

    documentRequest.setDocumentUniqueId(uniqueId);
    documentRequest.setRepositoryUniqueId(repositoryUniqueId());
    documentRequest.setHomeCommunityId(homeCommunityId());

    return documentRequest;
  }

  private RetrieveDocumentSetResponseType.DocumentResponse buildDocumentResponse(
      final String uniqueId) {
    final var documentResponse = new RetrieveDocumentSetResponseType.DocumentResponse();

    documentResponse.setDocumentUniqueId(uniqueId);
    documentResponse.setRepositoryUniqueId(repositoryUniqueId());
    documentResponse.setHomeCommunityId(homeCommunityId());

    final var lvlInfo = CDALevelInfo.fromDocumentUniqueId(uniqueId);

    documentResponse.setMimeType(lvlInfo.mimeType());
    documentResponse.setDocument(lvlInfo.readDocument());

    return documentResponse;
  }

  @RequiredArgsConstructor
  @Getter
  @Accessors(fluent = true)
  @Slf4j
  enum CDALevelInfo {
    LEVEL_1("^PS.PDF", MediaType.APPLICATION_PDF_VALUE, "Patient_Summary_CDA1.pdf"),
    LEVEL_3("^PS.XML", MediaType.TEXT_XML_VALUE, "Patient_Summary_CDA3.xml");

    private final String idMarker;

    private final String mimeType;

    private final String documentFileName;

    private static final String COMMENT_REGEX = "(?s)<!--.*?-->";

    public boolean documentIsOfLevel(final String documentUniqueId) {
      return documentUniqueId.endsWith(idMarker());
    }

    @SneakyThrows
    public byte[] readDocument() {
      try (final InputStream io = HttpMessageFactory.readMessageFileSafely(documentFileName)) {
        log.debug("name {}", this.name());
        if (this.name().equals(LEVEL_1.name())) {
          return io.readAllBytes();
        }
        // remove comments from xml file
        return new String(io.readAllBytes(), StandardCharsets.UTF_8)
            .replaceAll(COMMENT_REGEX, "")
            .getBytes(StandardCharsets.UTF_8);
      }
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
