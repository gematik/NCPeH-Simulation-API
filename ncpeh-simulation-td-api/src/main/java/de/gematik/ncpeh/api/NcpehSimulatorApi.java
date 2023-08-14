/*
 * Copyright (c) 2023 gematik GmbH
 * 
 * Licensed under the Apache License, Version 2.0 (the License);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an 'AS IS' BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.gematik.ncpeh.api;

import de.gematik.ncpeh.api.request.FindDocumentsRequest;
import de.gematik.ncpeh.api.request.IdentifyPatientRequest;
import de.gematik.ncpeh.api.request.RetrieveDocumentRequest;
import de.gematik.ncpeh.api.response.ErrorInformation;
import de.gematik.ncpeh.api.response.SimulatorCommunicationData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("triggerInterface")
public interface NcpehSimulatorApi {

  @POST
  @Path("identifyPatient")
  @Consumes({"application/json"})
  @Produces({"application/json"})
  @Operation(
      summary =
          "Aufruf des Anwendungsfalls \"NCPeH.UC_1 - Versicherten im Behandlungsland identifizieren.\" "
              + "Abholung von Daten zur Identifikation des Patienten",
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
          "Aufruf des Anwendungsfalls \"NCPeH.UC_2 - Verfügbare Versichertendatensätze des ePKA MIO auflisten\". "
              + "Hole alle Metadaten zu Dokumenten die entsprechend der übergebenen Filterkriterien selektiert wurden.",
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
          "Aufruf der Anwendungsfälle \"NCPeH.UC_3 - Versichertendatensatz abrufen\" bzw. "
              + "\"NCPeH.UC_4 - Versichertendatensatz als PDF abrufen\". "
              + "Rufe ein Dokument im eHDSI Pivot-Format ab - als CDA Level 3 (XML) oder als CDA Level 1 (PDF)",
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
            content = @Content(schema = @Schema(implementation = ErrorInformation.class)),
            description = "Ein Fehler ist im NCPeH Simulator aufgetreten."),
      })
  Response retrieveDocument(RetrieveDocumentRequest request);
}
