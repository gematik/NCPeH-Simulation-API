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

import de.gematik.ncpeh.ehdsi.valuesets.EhdsiSubstitutionCode;
import jakarta.xml.bind.JAXBElement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import lombok.experimental.UtilityClass;
import org.hl7.v3.CE;
import org.hl7.v3.ClinicalDocument;
import org.hl7.v3.EN;
import org.hl7.v3.ENXP;
import org.hl7.v3.EnFamily;
import org.hl7.v3.EnGiven;
import org.hl7.v3.PN;
import org.hl7.v3.POCDMT000040Material;
import org.hl7.v3.POCDMT000040Patient;
import org.hl7.v3.POCDMT000040Section;

@UtilityClass
public class PrescriptionCda3Utils {

  /**
   * Pattern to identify the level marker suffix in XDS Document Entry Unique IDs.
   *
   * <p>The following markers are possible: {@code |eP.PDF}, {@code |eP.XML}, {@code ^PS.PDF} or
   * {@code ^PS.XML}
   */
  public static final Pattern XDS_DOCUMENT_ENTRY_UNIQUE_ID_LEVEL_MARKER_PATTERN =
      Pattern.compile("(?:\\|eP|\\^PS)\\.(?:PDF|XML)$");

  private static final String HL7V3_NAMESPACE_URN = "urn:hl7-org:v3";

  /**
   * Strips the level marker from a document UID
   *
   * @param documentUid the document UID, e.g. {@code
   *     1.2.276.0.76.4.299^160.000.000.000.123.76|eP.PDF}
   * @return the document ID without level marker: {@code 1.2.276.0.76.4.299^160.000.000.000.123.76}
   * @throws IllegalArgumentException if the document ID does not have a suffix that matches the
   *     pattern {@linkplain
   *     PrescriptionCda3Utils#XDS_DOCUMENT_ENTRY_UNIQUE_ID_LEVEL_MARKER_PATTERN}
   */
  public static String stripLevelMarkerFromDocumentUid(final String documentUid) {
    var matcher = XDS_DOCUMENT_ENTRY_UNIQUE_ID_LEVEL_MARKER_PATTERN.matcher(documentUid);
    if (matcher.find()) {
      return documentUid.substring(0, matcher.start());
    }
    throw new IllegalArgumentException("Invalid document ID format: " + documentUid);
  }

  /**
   * Extracts the prescription ID from a document UID
   *
   * @param documentUid the document UID, e.g. {@code
   *     1.2.276.0.76.4.299^160.000.000.000.123.76|eP.PDF}
   * @return the prescription ID: {@code 160.000.000.000.123.76}
   * @throws java.lang.IllegalArgumentException if the document ID does not have a suffix that
   *     matches the pattern {@linkplain
   *     PrescriptionCda3Utils#XDS_DOCUMENT_ENTRY_UNIQUE_ID_LEVEL_MARKER_PATTERN} or if the document
   *     ID does not contain a prescription ID part
   */
  public static String extractPrescriptionIdFromDocumentUid(final String documentUid) {
    var strippedUid = stripLevelMarkerFromDocumentUid(documentUid);
    var parts = strippedUid.split("\\^");
    if (parts.length == 2) {
      return parts[1];
    }
    throw new IllegalArgumentException("Invalid document ID format: " + documentUid);
  }

  /**
   * Gets the patient's first name from the ClinicalDocument
   *
   * @param clinicalDocument the ClinicalDocument
   * @return the patient's first name
   */
  public static String getPatientFirstName(final ClinicalDocument clinicalDocument) {
    return getPersonNamePartByType(getNameElement(clinicalDocument), EnGiven.class);
  }

  /**
   * Sets the patient's first name in the ClinicalDocument
   *
   * @param clinicalDocument the ClinicalDocument
   * @param firstName the patient's first name
   */
  public static void setPatientFirstName(
      final ClinicalDocument clinicalDocument, final String firstName) {
    JAXBElement<EnGiven> content =
        new JAXBElement<>(
            new QName(HL7V3_NAMESPACE_URN, "given", XMLConstants.DEFAULT_NS_PREFIX),
            EnGiven.class,
            new EnGiven().withContent(firstName));
    setPatientNameByType(clinicalDocument, EnGiven.class, content);
  }

  /**
   * Gets the patient's last name from the ClinicalDocument
   *
   * @param clinicalDocument the ClinicalDocument
   * @return the patient's last name
   */
  public static String getPatientLastName(final ClinicalDocument clinicalDocument) {
    return getPersonNamePartByType(getNameElement(clinicalDocument), EnFamily.class);
  }

  /**
   * Sets the patient's last name in the ClinicalDocument
   *
   * @param clinicalDocument the ClinicalDocument
   * @param lastName the patient's last name
   */
  public static void setPatientLastName(
      final ClinicalDocument clinicalDocument, final String lastName) {
    JAXBElement<EnFamily> content =
        new JAXBElement<>(
            new QName(HL7V3_NAMESPACE_URN, "family", XMLConstants.DEFAULT_NS_PREFIX),
            EnFamily.class,
            new EnFamily().withContent(lastName));
    setPatientNameByType(clinicalDocument, EnFamily.class, content);
  }

  /**
   * Gets the patient's date of birth from the ClinicalDocument
   *
   * @param clinicalDocument the ClinicalDocument
   * @return the patient's date of birth
   */
  public static LocalDate getPatientDateOfBirth(final ClinicalDocument clinicalDocument) {
    return LocalDate.parse(
        getPatientElement(clinicalDocument).getBirthTime().getValue(),
        DateTimeFormatter.BASIC_ISO_DATE);
  }

  /**
   * Sets the patient's date of birth in the ClinicalDocument
   *
   * @param clinicalDocument the ClinicalDocument
   * @param date the patient's date of birth
   */
  public static void setPatientDateOfBirth(
      final ClinicalDocument clinicalDocument, final LocalDate date) {
    getPatientElement(clinicalDocument)
        .getBirthTime()
        .setValue(date.format(DateTimeFormatter.BASIC_ISO_DATE));
  }

  /**
   * Gets the value describing which type of substitution is allowed for the prescribed medication
   *
   * @param clinicalDocument the ClinicalDocument
   * @return the coded substitution value as CE
   */
  public static CE getSubstitutionValue(final ClinicalDocument clinicalDocument) {
    return (CE)
        getSection(clinicalDocument)
            .getEntry()
            .getFirst()
            .getSubstanceAdministration()
            .getEntryRelationship()
            .stream()
            .filter(
                rel ->
                    Optional.ofNullable(rel.getObservation())
                        .map(
                            obs -> {
                              var code = obs.getCode();
                              return "SUBST".equals(code.getCode())
                                  && "2.16.840.1.113883.5.6".equals(code.getCodeSystem());
                            })
                        .orElse(false))
            .map(rel -> rel.getObservation().getValue().getFirst())
            .findFirst()
            .orElse(null);
  }

  /**
   * Sets the value describing which type of substitution is allowed for the prescribed medication
   *
   * @param clinicalDocument the ClinicalDocument
   * @param value an {@linkplain EhdsiSubstitutionCode} denoting the value to set
   */
  public static void setSubstitutionValue(
      final ClinicalDocument clinicalDocument, final EhdsiSubstitutionCode value) {
    getSubstitutionValue(clinicalDocument)
        .withCodeSystem(EhdsiSubstitutionCode.CODE_SYSTEM_OID)
        .withCodeSystemName(EhdsiSubstitutionCode.CODE_SYSTEM_NAME)
        .withCode(value.getCode())
        .withDisplayName(value.getDisplayName());
  }

  /**
   * Gets the DocumentEntry.UniqueId from the ClinicalDocument
   *
   * @param clinicalDocument the ClinicalDocument
   * @return the DocumentEntry.UniqueId
   */
  public static String getDocumentUid(final ClinicalDocument clinicalDocument) {
    return getSection(clinicalDocument).getElementId().getExtension();
  }

  /**
   * Sets the DocumentEntry.UniqueId in the ClinicalDocument
   *
   * @param clinicalDocument the ClinicalDocument
   * @param documentUid the DocumentEntry.UniqueId to set
   */
  public static void setDocumentUid(
      final ClinicalDocument clinicalDocument, final String documentUid) {
    getSection(clinicalDocument).getElementId().setExtension(documentUid);
  }

  /**
   * Gets the PZN (Pharmazentralnummer) of the prescribed medication from the ClinicalDocument
   *
   * @param clinicalDocument the ClinicalDocument
   * @return the PZN
   */
  public static String getPzn(final ClinicalDocument clinicalDocument) {
    return getManufacturedMaterial(clinicalDocument).getCode().getCode();
  }

  /**
   * Sets the PZN (Pharmazentralnummer) of the prescribed medication in the ClinicalDocument
   *
   * @param clinicalDocument the ClinicalDocument
   * @param pzn the PZN to set
   */
  public static void setPzn(final ClinicalDocument clinicalDocument, final String pzn) {
    getManufacturedMaterial(clinicalDocument).getCode().withCode(pzn);
  }

  /**
   * Gets the display name associated with the PZN of the prescribed medication from the
   * ClinicalDocument
   *
   * @param clinicalDocument the ClinicalDocument
   * @return the PZN display name
   */
  public static String getPznDisplayName(final ClinicalDocument clinicalDocument) {
    return getManufacturedMaterial(clinicalDocument).getCode().getDisplayName();
  }

  /**
   * Sets the display name associated with the PZN of the prescribed medication in the
   * ClinicalDocument
   *
   * @param clinicalDocument the ClinicalDocument
   * @param displayName the PZN display name to set
   */
  public static void setPznDisplayName(
      final ClinicalDocument clinicalDocument, final String displayName) {
    getManufacturedMaterial(clinicalDocument).getCode().withDisplayName(displayName);
  }

  /**
   * Gets the name of the prescribed medication from the ClinicalDocument
   *
   * @param clinicalDocument the ClinicalDocument
   * @return the medication name
   */
  public static String getMedicationName(final ClinicalDocument clinicalDocument) {
    return getManufacturedMaterial(clinicalDocument).getName().getContent().getFirst().toString();
  }

  /**
   * Sets the name of the prescribed medication in the ClinicalDocument
   *
   * @param clinicalDocument the ClinicalDocument
   * @param medicationName the medication name to set
   */
  public static void setMedicationName(
      final ClinicalDocument clinicalDocument, final String medicationName) {
    getManufacturedMaterial(clinicalDocument).setName(new EN().withContent(medicationName));
  }

  private static POCDMT000040Material getManufacturedMaterial(
      final ClinicalDocument clinicalDocument) {
    return getSection(clinicalDocument)
        .getEntry()
        .getFirst()
        .getSubstanceAdministration()
        .getConsumable()
        .getManufacturedProduct()
        .getManufacturedMaterial();
  }

  private static POCDMT000040Section getSection(final ClinicalDocument clinicalDocument) {
    return clinicalDocument
        .getComponent()
        .getStructuredBody()
        .getComponent()
        .getFirst()
        .getSection();
  }

  private static PN getNameElement(final ClinicalDocument clinicalDocument) {
    return getPatientElement(clinicalDocument).getName().getFirst();
  }

  private static POCDMT000040Patient getPatientElement(final ClinicalDocument clinicalDocument) {
    return clinicalDocument.getRecordTarget().getFirst().getPatientRole().getPatient();
  }

  private static String getPersonNamePartByType(
      final PN nameElement, final Class<? extends ENXP> entityNamePartType) {
    return nameElement.getContent().stream()
        .filter(JAXBElement.class::isInstance)
        .map(x -> ((JAXBElement<?>) x).getValue())
        .filter(entityNamePartType::isInstance)
        .map(x -> entityNamePartType.cast(x).getContent())
        .flatMap(Collection::stream)
        .collect(Collectors.joining(" "));
  }

  private static void setPatientNameByType(
      final ClinicalDocument clinicalDocument,
      final Class<? extends ENXP> namePartType,
      final JAXBElement<? extends ENXP> contentElement) {
    var nameElement = getNameElement(clinicalDocument);
    nameElement
        .getContent()
        .removeIf(
            x ->
                x instanceof JAXBElement<?>
                    && namePartType.isInstance(((JAXBElement<?>) x).getValue()));
    nameElement.withContent(contentElement);
  }
}
