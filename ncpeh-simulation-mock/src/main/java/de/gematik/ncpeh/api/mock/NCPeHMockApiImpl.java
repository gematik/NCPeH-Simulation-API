/*
 * Copyright (c) 2022 gematik GmbH
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

package de.gematik.ncpeh.api.mock;

import de.gematik.ncpeh.api.NcpehSimulatorApi;
import de.gematik.ncpeh.api.mock.builder.HttpMessageFactory;
import de.gematik.ncpeh.api.mock.builder.SimulatorCommunicationDataBuilder;
import de.gematik.ncpeh.api.request.FindDocumentsRequest;
import de.gematik.ncpeh.api.request.IdentifyPatientRequest;
import de.gematik.ncpeh.api.request.RetrieveDocumentRequest;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import org.springframework.stereotype.Service;

/**
 * Implementation of the NCPeH Trigger Interface API It's all Spring and Apache CXF magic, so pretty
 * slim implementation
 */
@Service
public class NCPeHMockApiImpl implements NcpehSimulatorApi {

  @Override
  public Response identifyPatient(IdentifyPatientRequest request) {
    return mockIt();
  }

  @Override
  public Response findDocuments(FindDocumentsRequest request) {
    return mockIt();
  }

  @Override
  public Response retrieveDocument(RetrieveDocumentRequest request) {
    return mockIt();
  }

  /**
   * Create a good case response to be used in all three operations. Data, which came in the
   * request, doesn't matter here, it is always the same response, which is created.
   *
   * @return {@link Response}
   */
  private Response mockIt() {
    var pseudoSimRequest = HttpMessageFactory.buildStandardRequest();

    return okResponseBuilder()
        .entity(
            SimulatorCommunicationDataBuilder.newInstance()
                .requestMessage(pseudoSimRequest)
                .responseMessage(pseudoSimRequest.getResponse())
                .build())
        .build();
  }

  private static ResponseBuilder okResponseBuilder() {
    return Response.ok().type(MediaType.APPLICATION_JSON_TYPE);
  }
}
