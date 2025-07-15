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

package de.gematik.ncpeh.api;

import de.gematik.ncpeh.api.request.FindDocumentsRequest;
import de.gematik.ncpeh.api.request.IdentifyPatientRequest;
import de.gematik.ncpeh.api.request.ProvideAndRegisterSetOfDocumentsRequest;
import de.gematik.ncpeh.api.request.RetrieveDocumentRequest;
import de.gematik.ncpeh.api.request.RetrieveSetOfDocumentsRequest;
import de.gematik.ncpeh.api.response.BadRequestInformation;
import de.gematik.ncpeh.api.response.ErrorInformation;
import de.gematik.ncpeh.api.response.SimulatorCommunicationData;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@OpenAPIDefinition(
    tags = {
      @Tag(name = "XCPD", description = "Endpunkte zum Auslösen von XCPD-Requests"),
      @Tag(name = "XCA", description = "Endpunkte zum Auslösen von XCA-Requests"),
      @Tag(name = "XDR", description = "Endpunkte zum Auslösen von XDR-Requests")
    })
@Path("triggerInterface")
public interface NcpehSimulatorApi {

  @POST
  @Path("identifyPatient")
  @Consumes({"application/json"})
  @Produces({"application/json"})
  @Operation(
      summary = "Abholung von Daten zur Identifikation des Patienten",
      description =
          "Aufruf der Anwendungsfälle \"NCPeH.UC_1 - Versicherten im Behandlungsland für PS-A identifizieren\" "
              + "und \"NCPeH.UC_9 - Versicherten im Behandlungsland für ePeD-A identifizieren\".",
      tags = {"XCPD"},
      requestBody =
          @RequestBody(
              required = true,
              description =
                  "Übergibt die Daten, die der NCPeH Simulator braucht, "
                      + "um den XCPD-Request an den NCPeH-Fachdienst für die Patientenidentifikation erzeugen zu können.",
              content =
                  @Content(
                      mediaType = MediaType.APPLICATION_JSON,
                      schema = @Schema(implementation = IdentifyPatientRequest.class))),
      responses = {
        @ApiResponse(
            responseCode = "200",
            content = @Content(schema = @Schema(implementation = SimulatorCommunicationData.class)),
            description =
                "Es fand eine Kommunikation zwischen NCPeH Simulator und NCPeH Fachdienst statt. "
                    + "Der Request des Simulators an den NCPeH-Fachdienst und die Response werden zurückgegeben."),
        @ApiResponse(
            responseCode = "400",
            content = @Content(schema = @Schema(implementation = BadRequestInformation.class)),
            description = "Die Anfrage ist fehlerhaft."),
        @ApiResponse(
            responseCode = "500",
            content = @Content(schema = @Schema(implementation = ErrorInformation.class)),
            description = "Ein Fehler ist im NCPeH Simulator aufgetreten."),
      })
  Response identifyPatient(IdentifyPatientRequest request);

  @POST
  @Path("findDocuments")
  @Consumes({"application/json"})
  @Produces({"application/json"})
  @Operation(
      summary =
          "Hole alle Metadaten zu Dokumenten, die entsprechend der übergebenen Filterkriterien selektiert wurden.",
      description =
          "Aufruf der Anwendungsfälle \"NCPeH.UC_2 - Metadaten über ePKA MIO auflisten\" und "
              + "\"NCPeH.UC_10 - Einlösbare E-Rezepte des Versicherten aus ePeD-A auflisten\".",
      tags = {"XCA"},
      requestBody =
          @RequestBody(
              required = true,
              description =
                  "Übergibt die Daten, die der NCPeH Simulator braucht, um den AdhocQueryRequest "
                      + "an den NCPeH-Fachdienst für die Suche nach Patientendokumenten zu erzeugen "
                      + "(z.B. Filterkriterien).",
              content =
                  @Content(
                      mediaType = MediaType.APPLICATION_JSON,
                      schema = @Schema(implementation = FindDocumentsRequest.class))),
      responses = {
        @ApiResponse(
            responseCode = "200",
            content = @Content(schema = @Schema(implementation = SimulatorCommunicationData.class)),
            description =
                "Es fand eine Kommunikation zwischen NCPeH Simulator und NCPeH Fachdienst statt. "
                    + "Der Request des Simulators an den NCPeH-Fachdienst und die Response werden zurückgegeben."),
        @ApiResponse(
            responseCode = "400",
            content = @Content(schema = @Schema(implementation = BadRequestInformation.class)),
            description = "Die Anfrage ist fehlerhaft."),
        @ApiResponse(
            responseCode = "500",
            content = @Content(schema = @Schema(implementation = ErrorInformation.class)),
            description = "Ein Fehler ist im NCPeH Simulator aufgetreten."),
      })
  Response findDocuments(FindDocumentsRequest request);

  @POST
  @Path("retrieveDocument")
  @Consumes({"application/json"})
  @Produces({"application/json"})
  @Operation(
      summary =
          "Rufe bis zu zwei Dokumente im eHDSI Pivot-Format ab - als CDA Level 3 (XML) oder als CDA Level 1 (PDF)",
      description =
          "Aufruf der Anwendungsfälle \"NCPeH.UC_3 - ePKA MIO des Versicherten abrufen\" und "
              + "\"NCPeH.UC_4 - ePKA MIO des Versicherten als PDF/A abrufen\".",
      tags = {"XCA"},
      requestBody =
          @RequestBody(
              required = true,
              description =
                  "Übergibt die Daten, die der NCPeH Simulator braucht, um den retrieveDocumentSetRequest "
                      + "an den NCPeH-Fachdienst für das Abholen eines Dokumentes zu erzeugen.",
              content =
                  @Content(
                      mediaType = MediaType.APPLICATION_JSON,
                      schema = @Schema(implementation = RetrieveDocumentRequest.class))),
      responses = {
        @ApiResponse(
            responseCode = "200",
            content = @Content(schema = @Schema(implementation = SimulatorCommunicationData.class)),
            description =
                "Es fand eine Kommunikation zwischen NCPeH Simulator und NCPeH-Fachdienst statt. "
                    + "Der Request des Simulators an den NCPeH-Fachdienst und die Response werden zurückgegeben."),
        @ApiResponse(
            responseCode = "400",
            content = @Content(schema = @Schema(implementation = BadRequestInformation.class)),
            description = "Die Anfrage ist fehlerhaft."),
        @ApiResponse(
            responseCode = "500",
            content = @Content(schema = @Schema(implementation = ErrorInformation.class)),
            description = "Ein Fehler ist im NCPeH Simulator aufgetreten."),
      })
  Response retrieveDocument(RetrieveDocumentRequest request);

  @POST
  @Path("retrieveSetOfDocuments")
  @Consumes({"application/json"})
  @Produces({"application/json"})
  @Operation(
      summary =
          "Rufe Dokumente im eHDSI Pivot-Format ab - als CDA Level 3 (XML) oder als CDA Level 1 (PDF)",
      description =
          "Aufruf des Anwendungsfalls \"NCPeH.UC_11 - Ausgewählte E-Rezepte aus ePeD-A abrufen\".",
      tags = {"XCA"},
      requestBody =
          @RequestBody(
              required = true,
              description =
                  "Übergibt die Daten, die der NCPeH Simulator braucht, um den retrieveDocumentSetRequest "
                      + "an den NCPeH-Fachdienst für das Abholen eines Dokumentes zu erzeugen.",
              content =
                  @Content(
                      mediaType = MediaType.APPLICATION_JSON,
                      schema = @Schema(implementation = RetrieveSetOfDocumentsRequest.class))),
      responses = {
        @ApiResponse(
            responseCode = "200",
            content = @Content(schema = @Schema(implementation = SimulatorCommunicationData.class)),
            description =
                "Es fand eine Kommunikation zwischen NCPeH Simulator und NCPeH-Fachdienst statt. "
                    + "Der Request des Simulators an den NCPeH-Fachdienst und die Response werden zurückgegeben."),
        @ApiResponse(
            responseCode = "400",
            content = @Content(schema = @Schema(implementation = BadRequestInformation.class)),
            description = "Die Anfrage ist fehlerhaft."),
        @ApiResponse(
            responseCode = "500",
            content = @Content(schema = @Schema(implementation = ErrorInformation.class)),
            description = "Ein Fehler ist im NCPeH Simulator aufgetreten."),
      })
  Response retrieveSetOfDocuments(RetrieveSetOfDocumentsRequest request);

  @POST
  @Path("provideAndRegisterSetOfDocuments")
  @Consumes({"application/json"})
  @Produces({"application/json"})
  @Operation(
      summary = "Sende Dispensierdaten für ein oder mehrere Rezepte an den NCPeH-Fachdienst.",
      description =
          "Aufruf des Anwendungsfalls \"NCPeH.UC_12 - Abgabe von Arzneimitteln an Versicherte im Abgabeland\".",
      tags = {"XDR"},
      requestBody =
          @RequestBody(
              required = true,
              description =
                  "Übergibt die Daten, die der NCPeH Simulator braucht, um ein ProvideAndRegisterDocumentSetRequest "
                      + "an den NCPeH-Fachdienst für die Übermittlung von Dispensierdaten zu erzeugen.",
              content =
                  @Content(
                      mediaType = MediaType.APPLICATION_JSON,
                      schema =
                          @Schema(implementation = ProvideAndRegisterSetOfDocumentsRequest.class))),
      responses = {
        @ApiResponse(
            responseCode = "200",
            content = @Content(schema = @Schema(implementation = SimulatorCommunicationData.class)),
            description =
                "Es fand eine Kommunikation zwischen NCPeH Simulator und NCPeH-Fachdienst statt. "
                    + "Der Request des Simulators an den NCPeH-Fachdienst und die Response werden zurückgegeben."),
        @ApiResponse(
            responseCode = "400",
            content = @Content(schema = @Schema(implementation = BadRequestInformation.class)),
            description = "Die Anfrage ist fehlerhaft."),
        @ApiResponse(
            responseCode = "500",
            content = @Content(schema = @Schema(implementation = ErrorInformation.class)),
            description = "Ein Fehler ist im NCPeH Simulator aufgetreten."),
      })
  Response provideAndRegisterSetOfDocuments(ProvideAndRegisterSetOfDocumentsRequest request);
}
