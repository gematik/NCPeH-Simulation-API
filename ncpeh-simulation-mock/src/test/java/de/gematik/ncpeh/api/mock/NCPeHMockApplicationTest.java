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

package de.gematik.ncpeh.api.mock;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.DeserializationFeature;
import de.gematik.ncpeh.api.mock.builder.HttpMessageFactory;
import de.gematik.ncpeh.api.mock.builder.SimulatorCommunicationDataBuilder;
import de.gematik.ncpeh.api.request.IdentifyPatientRequest;
import jakarta.ws.rs.core.MediaType;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import javax.xml.namespace.QName;
import lombok.SneakyThrows;
import org.apache.cxf.Bus;
import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.endpoint.EndpointImpl;
import org.apache.cxf.ext.logging.LoggingInInterceptor;
import org.apache.cxf.ext.logging.LoggingOutInterceptor;
import org.apache.cxf.io.CachedOutputStream;
import org.apache.cxf.jaxrs.JAXRSBindingFactory;
import org.apache.cxf.message.Exchange;
import org.apache.cxf.message.ExchangeImpl;
import org.apache.cxf.message.Message;
import org.apache.cxf.message.MessageImpl;
import org.apache.cxf.service.Service;
import org.apache.cxf.service.ServiceImpl;
import org.apache.cxf.service.model.BindingInfo;
import org.apache.cxf.service.model.EndpointInfo;
import org.apache.cxf.service.model.InterfaceInfo;
import org.apache.cxf.service.model.ServiceInfo;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;
import org.springframework.http.HttpMessage;

class NCPeHMockApplicationTest {

  private static final String ENABLE_LOGGING_KEY = "org.apache.cxf.logging.enable";

  @Test
  void jsonProviderTest() {
    final var tstObj = new NCPeHMockApplication();

    final var jsonProvider =
        assertDoesNotThrow(
            tstObj::jsonProvider, "Method NCPeHMockApplication.jsonProvider threw an exception");

    final var objectMapper =
        jsonProvider.locateMapper(IdentifyPatientRequest.class, MediaType.APPLICATION_JSON_TYPE);
    assertNotNull(objectMapper, "JSON provider does not contain object mapper.");

    assertTrue(
        objectMapper.isEnabled(DeserializationFeature.READ_ENUMS_USING_TO_STRING),
        "Feature READ_ENUMS_USING_TO_STRING is not enabled");
  }

  @Test
  void createOpenApiFeatureTest() {
    final var tstObj = new NCPeHMockApplication();

    final var openApiFeature =
        assertDoesNotThrow(
            tstObj::createOpenApiFeature,
            "Method NCPeHMockApplication.createOpenApiFeature threw an exception");

    assertNotNull(openApiFeature);
  }

  @Test
  void loggingFeatureTest() {
    final var tstObj = new NCPeHMockApplication();

    final var loggingFeature =
        assertDoesNotThrow(
            tstObj::loggingFeature,
            "Method NCPeHMockApplication.loggingFeature threw an exception");

    assertNotNull(loggingFeature);
  }

  @Test
  void loggingFeatureSenderTest() {
    final var tstObj = new NCPeHMockApplication();

    final var loggingFeature =
        assertDoesNotThrow(
            tstObj::loggingFeature,
            "Method NCPeHMockApplication.loggingFeature threw an exception");

    final var bus = new SpringBus();
    loggingFeature.initialize(bus);

    final var inMsg = createInMessage(bus);

    final var loggingInInterceptor =
        bus.getInInterceptors().stream()
            .filter(interceptor -> interceptor instanceof LoggingInInterceptor)
            .map(LoggingInInterceptor.class::cast)
            .findFirst()
            .orElseThrow(() -> new AssertionFailedError("LoggingFeature initialization failed"));

    assertDoesNotThrow(() -> loggingInInterceptor.handleMessage(inMsg));

    final var outMsg = createOutMessage(inMsg);

    final var logOutInterceptor =
        bus.getOutInterceptors().stream()
            .filter(interceptor -> interceptor instanceof LoggingOutInterceptor)
            .map(LoggingOutInterceptor.class::cast)
            .findFirst()
            .orElseThrow(() -> new AssertionFailedError("LoggingFeature initialization failed"));

    logOutInterceptor.handleMessage(outMsg);

    assertDoesNotThrow(() -> outMsg.getContent(OutputStream.class).close());
  }

  // region private

  @SneakyThrows
  private Message createInMessage(final Bus bus) {
    final var request = HttpMessageFactory.buildPSAFindDocumentRequest();
    final Message msg = new MessageImpl();

    setMessageProperties(msg, request);
    msg.putIfAbsent(Message.HTTP_REQUEST_METHOD, request.getMethod().name());
    msg.setId("loggingFeatureSenderTest InMessage");
    msg.putIfAbsent(Message.REQUEST_URL, request.getURI().toString());

    final var cachedOutStream = new CachedOutputStream();
    cachedOutStream.resetOut(request.getRequestBody(), false);
    msg.setContent(CachedOutputStream.class, cachedOutStream);

    createExchange(bus, request.getURI().toString()).setInMessage(msg);

    return msg;
  }

  private Message createOutMessage(final Message inMessage) {
    final var response = HttpMessageFactory.buildStandardFindDocumentResponsePSA(null);

    final Message msg = new MessageImpl();

    setMessageProperties(msg, response);
    msg.setId("loggingFeatureSenderTest OutMessage");
    msg.putIfAbsent(Message.RESPONSE_CODE, response.getStatusCode().value());

    final var httpResponseMsg =
        SimulatorCommunicationDataBuilder.wrapHttpResponse(
            HttpMessageFactory.buildStandardFindDocumentResponsePSA(null));
    final var msgAsOutputStream = new ByteArrayOutputStream();
    msgAsOutputStream.writeBytes(httpResponseMsg.messageContent().httpBody());
    msg.setContent(OutputStream.class, msgAsOutputStream);

    inMessage.getExchange().setOutMessage(msg);

    return msg;
  }

  private void setMessageProperties(final Message msg, final HttpMessage httpMessage) {
    msg.putIfAbsent(ENABLE_LOGGING_KEY, Boolean.TRUE);
    msg.putIfAbsent(Message.ENCODING, StandardCharsets.UTF_8.name());
    msg.putIfAbsent(Message.CONTENT_TYPE, MediaType.APPLICATION_JSON_TYPE.toString());
    msg.putIfAbsent(Message.PROTOCOL_HEADERS, httpMessage.getHeaders());
  }

  @SneakyThrows
  private Exchange createExchange(final Bus bus, final String address) {
    final var exchange = new ExchangeImpl();

    final var name = new QName("loggingTestPort");

    final var serviceInfo = new ServiceInfo();
    serviceInfo.setName(name);
    serviceInfo.setInterface(new InterfaceInfo(serviceInfo, name));

    final var endpointInfo = new EndpointInfo();
    endpointInfo.setAddress(address);
    endpointInfo.setService(serviceInfo);
    endpointInfo.setName(name);
    endpointInfo.setBinding(new BindingInfo(serviceInfo, JAXRSBindingFactory.JAXRS_BINDING_ID));

    final var service = new ServiceImpl(serviceInfo);
    final var endpoint = new EndpointImpl(bus, service, endpointInfo);
    service.setEndpoints(Map.of(name, endpoint));

    exchange.put(Service.class, service);
    exchange.put(Endpoint.class, endpoint);
    exchange.put(Bus.class, bus);
    exchange.putIfAbsent("org.apache.cxf.resource.operation.name", "findDocuments");

    return exchange;
  }

  // endregion private
}
