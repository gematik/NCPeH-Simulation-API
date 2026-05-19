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

package de.gematik.ncpeh.api.mock.util;

import static de.gematik.ncpeh.api.mock.util.XmlUtils.EVENT_CODE_CLASSIFICATION_SCHEME;
import static de.gematik.ncpeh.api.mock.util.XmlUtils.SLOT_CODING_SCHEME;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import de.gematik.epa.conversion.internal.requests.factories.externalidentifier.ExternalIdentifierScheme;
import de.gematik.ncpeh.api.mock.builder.HttpMessageFactory;
import de.gematik.ncpeh.api.mock.builder.RetrieveDocumentMessagesBuilder;
import de.gematik.ncpeh.api.mock.data.Medication;
import de.gematik.ncpeh.api.request.RetrieveDocumentRequest;
import de.gematik.ncpeh.ehdsi.valuesets.EhdsiSubstitutionCode;
import ihe.iti.xds_b._2007.RetrieveDocumentSetRequestType.DocumentRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.SneakyThrows;
import oasis.names.tc.ebxml_regrep.xsd.query._3.AdhocQueryRequest;
import oasis.names.tc.ebxml_regrep.xsd.query._3.AdhocQueryResponse;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.*;
import org.junit.jupiter.api.Test;

class XmlUtilsTest {

  @Test
  void marshalTest() {
    final var testdata = retrieveDocumentTestdata().buildRequest();

    final var result = assertDoesNotThrow(() -> XmlUtils.marshal(testdata));

    assertNotNull(result);

    final var roundTripResult =
        assertDoesNotThrow(() -> XmlUtils.unmarshal(testdata.getClass(), result));

    assertNotNull(roundTripResult);
    assertEquals(testdata.getDocumentRequest().size(), roundTripResult.getDocumentRequest().size());
    assertArrayEquals(
        testdata.getDocumentRequest().stream().map(DocumentRequest::getDocumentUniqueId).toArray(),
        roundTripResult.getDocumentRequest().stream()
            .map(DocumentRequest::getDocumentUniqueId)
            .toArray());
  }

  @SneakyThrows
  @Test
  void unmarshalTest() {
    try (final var testdata =
        HttpMessageFactory.readMessageFileSafely(
            HttpMessageFactory.FIND_DOCUMENT_PSA_REQUEST_FILE_NAME)) {

      final var result =
          assertDoesNotThrow(
              () -> XmlUtils.unmarshal(AdhocQueryRequest.class, testdata.readAllBytes()));

      assertNotNull(result);
      assertNotNull(result.getResponseOption());
      assertNotNull(result.getAdhocQuery());
      assertFalse(result.getAdhocQuery().getSlot().isEmpty());

      final var roundTripResult = assertDoesNotThrow(() -> XmlUtils.marshal(result));

      assertNotNull(roundTripResult);
    }
  }

  private RetrieveDocumentMessagesBuilder retrieveDocumentTestdata() {
    return RetrieveDocumentMessagesBuilder.fromRequest(
        new RetrieveDocumentRequest(
            null,
            null,
            null,
            null,
            "1.2.276.0.76.3.1.91.2",
            "2.25.2350928502702^2.25.2.PS.1",
            "2.25.2350928502702^2.25.2.PS.2",
            "urn:oid:2.16.17.710.850.1000.990.101"));
  }

  @Test
  void shouldCreateDeepCopyOfExtrinsicObject() {
    // Arrange
    ExtrinsicObjectType original = new ExtrinsicObjectType();
    original.setId("original-id");
    original.setMimeType("application/pdf");

    // Act
    ExtrinsicObjectType clone = XmlUtils.deepCloneJaxbElement(original, ExtrinsicObjectType.class);

    // Assert
    assertAll(
        () -> assertThat(clone, not(sameInstance(original))),
        () -> assertThat(clone.getId(), equalTo("original-id")),
        () -> assertThat(clone.getMimeType(), equalTo("application/pdf")));

    // Modify clone to ensure deep copy
    clone.setId("modified");

    assertThat(original.getId(), equalTo("original-id"));
  }

  @Test
  void shouldExtendRegistryObjectListWithGeneratedEntries() {

    // Arrange
    var response = new oasis.names.tc.ebxml_regrep.xsd.query._3.AdhocQueryResponse();
    var list = new oasis.names.tc.ebxml_regrep.xsd.rim._3.RegistryObjectListType();
    response.setRegistryObjectList(list);

    var factory = new oasis.names.tc.ebxml_regrep.xsd.rim._3.ObjectFactory();

    ExtrinsicObjectType xmlTemplate = new ExtrinsicObjectType();
    xmlTemplate
        .getExternalIdentifier()
        .add(
            new ExternalIdentifierType()
                .withIdentificationScheme(ExternalIdentifierScheme.DOCUMENT_ENTRY_UNIQUE.getId())
                .withId("template-xml"));

    ExtrinsicObjectType pdfTemplate = new ExtrinsicObjectType();
    pdfTemplate
        .getExternalIdentifier()
        .add(
            new ExternalIdentifierType()
                .withIdentificationScheme(ExternalIdentifierScheme.DOCUMENT_ENTRY_UNIQUE.getId())
                .withId("template-pdf"));

    list.getIdentifiable().add(factory.createExtrinsicObject(xmlTemplate));
    list.getIdentifiable().add(factory.createExtrinsicObject(pdfTemplate));

    Set<String> prescriptionIds = Set.of("111", "222", "333");

    // Act
    XmlUtils.extendRegistryObjectList(response, prescriptionIds);

    // Assert
    // 2 entries per prescription
    assertEquals(6, list.getIdentifiable().size());

    var ids =
        list.getIdentifiable().stream()
            .map(e -> e.getValue())
            .map(ExtrinsicObjectType.class::cast)
            .map(e -> e.getExternalIdentifier())
            .flatMap(List::stream)
            .filter(
                e ->
                    ExternalIdentifierScheme.DOCUMENT_ENTRY_UNIQUE
                        .getId()
                        .equals(e.getIdentificationScheme()))
            .map(ExternalIdentifierType::getValue)
            .toList();

    assertEquals(6, ids.size());

    assertThat(ids.stream().filter(id -> id.contains("111|")).count(), is(2L));
    assertThat(ids.stream().filter(id -> id.contains("222|")).count(), is(2L));
    assertThat(ids.stream().filter(id -> id.contains("333|")).count(), is(2L));
  }

  @Test
  void generatedEntriesMustBeIndependentCopies() {

    var response = new AdhocQueryResponse();
    var list = new RegistryObjectListType();
    response.setRegistryObjectList(list);

    var factory = new ObjectFactory();

    ExtrinsicObjectType template1 = new ExtrinsicObjectType();
    template1.setMimeType("text/xml");

    ExtrinsicObjectType template2 = new ExtrinsicObjectType();
    template2.setMimeType("application/pdf");

    list.getIdentifiable().add(factory.createExtrinsicObject(template1));
    list.getIdentifiable().add(factory.createExtrinsicObject(template2));

    XmlUtils.extendRegistryObjectList(response, Set.of("777"));

    var objects =
        list.getIdentifiable().stream().map(e -> (ExtrinsicObjectType) e.getValue()).toList();

    // change one clone → others must not change
    objects.get(0).setMimeType("modified");

    assertThat(objects.get(1).getMimeType(), not(equalTo("modified")));
  }

  @SneakyThrows
  @Test
  void shouldExtractSoapBodyAndUnmarshalPayload() {
    try (final var testdata =
        HttpMessageFactory.readMessageFileSafely(
            HttpMessageFactory.FIND_DOCUMENT_EPED_REQUEST_FILE_NAME)) {
      byte[] testdataAsBytes = testdata.readAllBytes();

      // Act
      byte[] bodyBytes = XmlUtils.extractSoapBody(testdataAsBytes);

      assertNotNull(bodyBytes);
      assertNotEquals(0, bodyBytes.length);

      // Ensure payload can be unmarshalled
      var response =
          assertDoesNotThrow(() -> XmlUtils.unmarshal(AdhocQueryRequest.class, bodyBytes));

      assertNotNull(response);
      assertEquals("LeafClass", response.getResponseOption().getReturnType());
    }
  }

  @Test
  void shouldThrowIfSoapBodyMissing() {

    String invalidSoap =
        """
            <SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/">
              <SOAP-ENV:Header/>
            </SOAP-ENV:Envelope>
            """;
    byte[] bodyBytes = invalidSoap.getBytes();

    assertThrows(RuntimeException.class, () -> XmlUtils.extractSoapBody(bodyBytes));
  }

  @Test
  void shouldExtendRegistryWithMedicationAndCheck() {
    boolean substitutionAllowed = false;
    var response = new AdhocQueryResponse();
    var list = new RegistryObjectListType();
    response.setRegistryObjectList(list);

    var factory = new ObjectFactory();

    ExtrinsicObjectType template1 = new ExtrinsicObjectType();
    template1.setMimeType("text/xml");
    template1.withClassification(
        new ClassificationType()
            .withClassificationScheme(EVENT_CODE_CLASSIFICATION_SCHEME)
            .withSlot(
                new SlotType1()
                    .withName(SLOT_CODING_SCHEME)
                    .withValueList(
                        new ValueListType().withValue(EhdsiSubstitutionCode.CODE_SYSTEM_OID))));

    ExtrinsicObjectType template2 = new ExtrinsicObjectType();
    template2.setMimeType("application/pdf");
    template2.withClassification(
        new ClassificationType()
            .withClassificationScheme(EVENT_CODE_CLASSIFICATION_SCHEME)
            .withSlot(
                new SlotType1()
                    .withName(SLOT_CODING_SCHEME)
                    .withValueList(
                        new ValueListType().withValue(EhdsiSubstitutionCode.CODE_SYSTEM_OID))));

    list.getIdentifiable().add(factory.createExtrinsicObject(template1));
    list.getIdentifiable().add(factory.createExtrinsicObject(template2));

    XmlUtils.extendRegistryObjectList(
        response,
        Set.of("777"),
        Map.of("777", new Medication("Medication name", "12345678", substitutionAllowed)));

    var extrinsicObjects =
        response.getRegistryObjectList().getIdentifiable().stream()
            .map(e -> (ExtrinsicObjectType) e.getValue())
            .toList();

    var classificationSchemeForSubstitutionFromFirstDocument =
        extrinsicObjects.get(0).getClassification().stream()
            .filter(c -> c.getClassificationScheme().equals(EVENT_CODE_CLASSIFICATION_SCHEME))
            .findFirst()
            .orElse(null);
    assertEquals("N", classificationSchemeForSubstitutionFromFirstDocument.getNodeRepresentation());
    var classificationSchemeForSubstitutionFromSecondDocument =
        extrinsicObjects.get(1).getClassification().stream()
            .filter(c -> c.getClassificationScheme().equals(EVENT_CODE_CLASSIFICATION_SCHEME))
            .findFirst()
            .orElse(null);
    assertEquals(
        "N", classificationSchemeForSubstitutionFromSecondDocument.getNodeRepresentation());
  }
}
