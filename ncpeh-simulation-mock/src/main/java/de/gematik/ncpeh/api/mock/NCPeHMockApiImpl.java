/*
 * Copyright 2023 gematik GmbH
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

package de.gematik.ncpeh.api.mock;

import de.gematik.ncpeh.api.NcpehSimulatorApi;
import de.gematik.ncpeh.api.mock.builder.HttpMessageFactory;
import de.gematik.ncpeh.api.mock.builder.RetrieveDocumentMessagesBuilder;
import de.gematik.ncpeh.api.mock.builder.SimulatorCommunicationDataBuilder;
import de.gematik.ncpeh.api.request.FindDocumentsRequest;
import de.gematik.ncpeh.api.request.IdentifyPatientRequest;
import de.gematik.ncpeh.api.request.RetrieveDocumentRequest;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.ResponseBuilder;
import org.springframework.stereotype.Service;

/**
 * Implementation of the NCPeH Trigger Interface API It's all Spring and Apache CXF magic, so pretty
 * slim implementation
 */
@Service
public class NCPeHMockApiImpl implements NcpehSimulatorApi {

  public static final String oidAssigningAuthority_psa = "1.2.276.0.76.4.298";
  public static final String oidAssigningAuthority_eped = "1.2.276.0.76.4.299";

  @Override
  public Response identifyPatient(IdentifyPatientRequest request) {
    if (request.accessCodeAssigningAuthority() != null
        && request.accessCodeAssigningAuthority().equals(oidAssigningAuthority_eped)) {
      return okResponseBuilder()
          .entity(
              SimulatorCommunicationDataBuilder.newInstance()
                  .requestMessage(HttpMessageFactory.buildEPEDIdentifyPatientRequest())
                  .responseMessage(HttpMessageFactory.buildStandardIdentifyPatientResponse())
                  .build())
          .build();
    } else {
      return okResponseBuilder()
          .entity(
              SimulatorCommunicationDataBuilder.newInstance()
                  .requestMessage(HttpMessageFactory.buildPSAIdentifyPatientRequest())
                  .responseMessage(HttpMessageFactory.buildStandardIdentifyPatientResponse())
                  .build())
          .build();
    }
  }

  @Override
  public Response findDocuments(FindDocumentsRequest request) {
    return okResponseBuilder()
        .entity(
            SimulatorCommunicationDataBuilder.newInstance()
                .requestMessage(HttpMessageFactory.buildStandardFindDocumentRequest())
                .responseMessage(HttpMessageFactory.buildStandardFindDocumentResponse())
                .build())
        .build();
  }

  @Override
  public Response retrieveDocument(RetrieveDocumentRequest request) {
    var iheMsgBuilder = new RetrieveDocumentMessagesBuilder().useDataFrom(request);

    return okResponseBuilder()
        .entity(
            SimulatorCommunicationDataBuilder.newInstance()
                .requestMessage(
                    HttpMessageFactory.buildRetrieveDocumentRequest(iheMsgBuilder.buildRequest()))
                .responseMessage(
                    HttpMessageFactory.buildRetrieveDocumentResponse(iheMsgBuilder.buildResponse()))
                .build())
        .build();
  }

  private static ResponseBuilder okResponseBuilder() {
    return Response.ok().type(MediaType.APPLICATION_JSON_TYPE);
  }
}
