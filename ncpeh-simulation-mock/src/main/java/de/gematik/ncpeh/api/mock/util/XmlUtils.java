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

import de.gematik.epa.conversion.internal.requests.factories.externalidentifier.ExternalIdentifierScheme;
import de.gematik.ncpeh.api.mock.NCPeHMockApiImpl;
import de.gematik.ncpeh.api.mock.data.Medication;
import de.gematik.ncpeh.ehdsi.valuesets.EhdsiSubstitutionCode;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElementDecl;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import oasis.names.tc.ebxml_regrep.xsd.query._3.AdhocQueryResponse;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.ClassificationType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.ExtrinsicObjectType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.ObjectFactory;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.RegistryObjectListType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.SlotType1;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.ValueListType;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

@UtilityClass
@Slf4j
@Accessors(fluent = true)
public class XmlUtils {

  public static final String EVENT_CODE_CLASSIFICATION_SCHEME =
      "urn:uuid:2c6b8cb7-8b2a-4051-b291-b1ae6a575ef4";
  public static final String SLOT_CODING_SCHEME = "codingScheme";
  private static final String OBJECT_FACTORY_CLASS_NAME = "ObjectFactory";

  @Getter(lazy = true)
  private static final XMLInputFactory xmlInputFactory = newXmlInputFactory();

  @SneakyThrows
  public static byte[] marshal(@NonNull Object xmlObject) {
    var jaxbCtx = JAXBContext.newInstance(xmlObject.getClass());
    var jaxbMarshaller = jaxbCtx.createMarshaller();
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    if (jaxbCtx.createJAXBIntrospector().isElement(xmlObject)) {
      jaxbMarshaller.marshal(xmlObject, outputStream);
    } else {
      jaxbMarshaller.marshal(toJaxbElement(xmlObject), outputStream);
    }
    return outputStream.toByteArray();
  }

  @SneakyThrows
  public static <T> T unmarshal(@NonNull Class<T> objectType, byte[] marshalledObject) {
    var jaxbUnmarshaller = JAXBContext.newInstance(objectType).createUnmarshaller();
    var inputStream = new ByteArrayInputStream(marshalledObject);
    return jaxbUnmarshaller
        .unmarshal(xmlInputFactory().createXMLStreamReader(inputStream), objectType)
        .getValue();
  }

  /**
   * This method creates a {@link JAXBElement} for an object. This is necessary for marshalling the
   * object, if its class does not have the {@link jakarta.xml.bind.annotation.XmlRootElement}
   * annotation.<br>
   * There is a {@code @SuppressWarnings("unchecked")} annotation at this method. However, the cast
   * can't fail, as there are proper checks implemented, but these are just not recognized by the
   * IDE.
   *
   * @param xmlObject the object for which the JAXBElement is created
   * @param <T> class type of the object
   * @return {@link JAXBElement} to the object
   */
  @SneakyThrows
  @SuppressWarnings("unchecked")
  private static <T> JAXBElement<T> toJaxbElement(@NonNull T xmlObject) {
    Class<T> objClass = (Class<T>) xmlObject.getClass();
    var pckg = objClass.getPackageName();
    var objFac =
        objClass
            .getClassLoader()
            .loadClass(pckg + "." + OBJECT_FACTORY_CLASS_NAME)
            .getConstructor()
            .newInstance();

    var jaxbElementMethod =
        Arrays.stream(objFac.getClass().getDeclaredMethods())
            .filter(method -> method.isAnnotationPresent(XmlElementDecl.class))
            .filter(
                method -> {
                  if (method.getGenericReturnType() instanceof ParameterizedType returnType) {
                    return returnType.getRawType().equals(JAXBElement.class)
                        && returnType.getActualTypeArguments().length == 1
                        && Arrays.stream(returnType.getActualTypeArguments())
                            .allMatch(typeArg -> typeArg.equals(objClass));
                  }
                  return false;
                })
            .filter(
                method -> {
                  var prmTypes = method.getParameterTypes();
                  return prmTypes.length == 1
                      && Arrays.stream(prmTypes).allMatch(prmType -> prmType.equals(objClass));
                })
            .findFirst()
            .orElseThrow(
                () ->
                    new NoSuchElementException(
                        "No JAXBElement factory method found for class " + objClass));

    var result = jaxbElementMethod.invoke(objFac, xmlObject);

    return (JAXBElement<T>) result;
  }

  private static XMLInputFactory newXmlInputFactory() {
    XMLInputFactory factory = XMLInputFactory.newInstance();
    factory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
    factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, Boolean.FALSE);
    return factory;
  }

  /**
   * Extends the {@link RegistryObjectListType} inside the given {@link AdhocQueryResponse} by
   * creating two {@link ExtrinsicObjectType} entries for each provided prescription ID.
   *
   * <p>The method assumes that the existing {@code RegistryObjectList} already contains exactly two
   * {@code ExtrinsicObjectType} elements which serve as templates (typically one representing a PDF
   * document and one representing an XML document).
   *
   * <p>For each prescription ID:
   *
   * <ul>
   *   <li>The two template {@code ExtrinsicObjectType} instances are deep-cloned.
   *   <li>The {@code id} of each clone is rewritten to follow the format: {@code
   *       <OID_ASSIGNING_AUTHORITY_EPED>^<prescriptionId>|eP.XML} or {@code
   *       <OID_ASSIGNING_AUTHORITY_EPED>^<prescriptionId>|eP.PDF}.
   *   <li>The cloned and modified instances are added to the registry object list.
   * </ul>
   *
   * <p>The original template elements are removed from the registry object list.
   *
   * @param adhocQueryResponse the {@link AdhocQueryResponse} containing the {@link
   *     RegistryObjectListType} to extend
   * @param prescriptionIds set of prescription identifiers for which document entries shall be
   *     generated; must not be {@code null}
   * @throws IllegalStateException if the registry object list does not contain exactly two {@code
   *     ExtrinsicObjectType} template elements
   * @throws RuntimeException if cloning of an {@code ExtrinsicObjectType} fails
   */
  public static void extendRegistryObjectList(
      AdhocQueryResponse adhocQueryResponse, Set<String> prescriptionIds) {
    extendRegistryObjectList(adhocQueryResponse, prescriptionIds, Map.of());
  }

  /**
   * Extends the {@link RegistryObjectListType} inside the given {@link AdhocQueryResponse} by
   * creating two {@link ExtrinsicObjectType} entries for each provided prescription ID and adding
   * metadata classifications if needed.
   *
   * @param adhocQueryResponse the {@link AdhocQueryResponse} containing the {@link
   *     RegistryObjectListType} to extend
   * @param prescriptionIds set of prescription identifiers for which document entries shall be
   *     generated; must not be {@code null}
   * @param medicationByPrescriptionId optional map of medication metadata by prescription ID
   */
  public static void extendRegistryObjectList(
      AdhocQueryResponse adhocQueryResponse,
      Set<String> prescriptionIds,
      Map<String, Medication> medicationByPrescriptionId) {
    RegistryObjectListType registryObjectList = adhocQueryResponse.getRegistryObjectList();

    // Extract existing ExtrinsicObjects, Assume there are exactly two
    List<ExtrinsicObjectType> templates =
        registryObjectList.getIdentifiable().stream()
            .filter(e -> ExtrinsicObjectType.class.equals(e.getDeclaredType()))
            .map(JAXBElement::getValue)
            .map(ExtrinsicObjectType.class::cast)
            .toList();
    if (templates.size() != 2) {
      throw new IllegalStateException("Expected exactly 2 template ExtrinsicObjectTypes");
    }

    registryObjectList.getIdentifiable().clear();
    if (!registryObjectList.getIdentifiable().isEmpty()) {
      throw new IllegalStateException("Expected ObjectList is empty");
    }

    ObjectFactory factory = new ObjectFactory();

    for (String prescriptionId : prescriptionIds) {
      Medication medication =
          Optional.of(medicationByPrescriptionId).map(map -> map.get(prescriptionId)).orElse(null);
      boolean isFirst = true;
      for (ExtrinsicObjectType template : templates) {
        ExtrinsicObjectType clone = deepCloneJaxbElement(template, ExtrinsicObjectType.class);
        addForbiddenSubstitutionClassificationIfNeeded(clone, medication);
        String newId =
            NCPeHMockApiImpl.OID_ASSIGNING_AUTHORITY_EPED
                + "^"
                + prescriptionId
                + "|"
                + (isFirst ? "eP.XML" : "eP.PDF");
        clone.getExternalIdentifier().stream()
            .filter(
                e ->
                    e.getIdentificationScheme() != null
                        && e.getIdentificationScheme()
                            .equals(ExternalIdentifierScheme.DOCUMENT_ENTRY_UNIQUE.getId()))
            .forEach(ei -> ei.setValue(newId));
        registryObjectList.getIdentifiable().add(factory.createExtrinsicObject(clone));
        isFirst = false;
      }
    }
  }

  private static void addForbiddenSubstitutionClassificationIfNeeded(
      ExtrinsicObjectType extrinsicObject, Medication medication) {
    if (medication == null || medication.substitutionAllowed()) {
      return;
    }

    ClassificationType substitutionClassification =
        extrinsicObject.getClassification().stream()
            .filter(c -> EVENT_CODE_CLASSIFICATION_SCHEME.equals(c.getClassificationScheme()))
            .filter(
                c ->
                    c.getSlot().stream()
                        .anyMatch(
                            slot ->
                                SLOT_CODING_SCHEME.equals(slot.getName())
                                    && slot.getValueList() != null
                                    && slot.getValueList()
                                        .getValue()
                                        .contains(EhdsiSubstitutionCode.CODE_SYSTEM_OID)))
            .findFirst()
            .orElse(null);
    if (substitutionClassification != null) {
      applyForbiddenSubstitutionMetadata(substitutionClassification);
      return;
    }

    substitutionClassification =
        new ClassificationType().withClassificationScheme(EVENT_CODE_CLASSIFICATION_SCHEME);
    substitutionClassification.setId("urn:uuid:" + UUID.randomUUID());
    substitutionClassification.setClassifiedObject(extrinsicObject.getId());
    applyForbiddenSubstitutionMetadata(substitutionClassification);

    extrinsicObject.getClassification().add(substitutionClassification);
  }

  private static void applyForbiddenSubstitutionMetadata(
      ClassificationType substitutionClassification) {
    substitutionClassification.setNodeRepresentation(EhdsiSubstitutionCode.NONE.getCode());

    SlotType1 codingSchemeSlot =
        substitutionClassification.getSlot().stream()
            .filter(slot -> SLOT_CODING_SCHEME.equals(slot.getName()))
            .findFirst()
            .orElseGet(
                () -> {
                  SlotType1 slot = new SlotType1().withName(SLOT_CODING_SCHEME);
                  slot.setValueList(new ValueListType());
                  substitutionClassification.getSlot().add(slot);
                  return slot;
                });
    if (codingSchemeSlot.getValueList() == null) {
      codingSchemeSlot.setValueList(new ValueListType());
    }
    codingSchemeSlot.getValueList().getValue().clear();
    codingSchemeSlot.getValueList().getValue().add(EhdsiSubstitutionCode.CODE_SYSTEM_OID);

    if (substitutionClassification.getName() != null) {
      substitutionClassification
          .getName()
          .getLocalizedString()
          .forEach(ls -> ls.setValue(EhdsiSubstitutionCode.NONE.getDisplayName()));
    }
  }

  /**
   * Creates a deep copy of the given JAXB-annotated object by marshalling and unmarshalling it.
   *
   * <p>This method can be used for any JAXB-mapped type. All nested elements are fully copied and
   * no shared references remain.
   *
   * @param original the object to clone
   * @param type the concrete class of the object
   * @param <T> the JAXB type
   * @return a deep copy of the provided instance
   * @throws RuntimeException if marshalling or unmarshalling fails
   */
  public static <T> T deepCloneJaxbElement(T original, Class<T> type) {
    try {
      byte[] bytes = XmlUtils.marshal(original);
      return XmlUtils.unmarshal(type, bytes);
    } catch (Exception e) {
      throw new RuntimeException("Error cloning instance of type " + type.getName(), e);
    }
  }

  public static byte[] extractSoapBody(byte[] soapMessage) {
    try {
      var dbf = DocumentBuilderFactory.newInstance();
      dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
      dbf.setNamespaceAware(true);

      var builder = dbf.newDocumentBuilder();
      Document doc = builder.parse(new ByteArrayInputStream(soapMessage));

      var root = doc.getDocumentElement();

      // Detect whether this is actually a SOAP envelope
      String rootNs = root.getNamespaceURI();
      String rootName = root.getLocalName();

      boolean isSoapEnvelope =
          "Envelope".equals(rootName)
              && ("http://schemas.xmlsoap.org/soap/envelope/".equals(rootNs)
                  || "http://www.w3.org/2003/05/soap-envelope".equals(rootNs));

      // If not SOAP → already payload → return unchanged
      if (!isSoapEnvelope) {
        return soapMessage;
      }

      // Find SOAP Body
      NodeList bodies = root.getElementsByTagNameNS(rootNs, "Body");
      if (bodies.getLength() == 0) {
        throw new IllegalArgumentException("SOAP Body not found");
      }

      Node body = bodies.item(0);

      // Find first element inside Body (skip whitespace/comments)
      Node payload = null;
      NodeList children = body.getChildNodes();
      for (int i = 0; i < children.getLength(); i++) {
        Node n = children.item(i);
        if (n.getNodeType() == Node.ELEMENT_NODE) {
          payload = n;
          break;
        }
      }

      if (payload == null) {
        throw new IllegalArgumentException("SOAP Body empty");
      }

      Transformer transformer = TransformerFactory.newDefaultInstance().newTransformer();
      transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");

      var out = new ByteArrayOutputStream();
      transformer.transform(new DOMSource(payload), new StreamResult(out));

      return out.toByteArray();

    } catch (Exception e) {
      throw new RuntimeException("Failed to extract SOAP body", e);
    }
  }
}
