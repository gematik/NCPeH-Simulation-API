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
import de.gematik.epa.conversion.internal.response.RegistryObjectListMapper;
import jakarta.xml.bind.JAXBElement;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.experimental.UtilityClass;
import oasis.names.tc.ebxml_regrep.xsd.query._3.AdhocQueryResponse;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.ExtrinsicObjectType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.RegistryObjectListType;

@UtilityClass
public class IheUtils {
  /**
   * Extracts all documentUniqueIds from the given AdhocQueryResponse
   *
   * @param adhocQueryResponse an AdhocQueryResponse
   * @return a list of documentUniqueIds
   */
  public static List<String> extractDocumentIdsFromAdhocQueryResponse(
      final AdhocQueryResponse adhocQueryResponse) {
    return streamExtrinsicObjects(adhocQueryResponse)
        .map(
            eo ->
                RegistryObjectListMapper.externalIdentifierValue(
                    eo, ExternalIdentifierScheme.DOCUMENT_ENTRY_UNIQUE))
        .toList();
  }

  private static Stream<ExtrinsicObjectType> streamExtrinsicObjects(
      final AdhocQueryResponse adhocQueryResponse) {
    return Optional.ofNullable(adhocQueryResponse)
        .map(AdhocQueryResponse::getRegistryObjectList)
        .map(RegistryObjectListType::getIdentifiable)
        .stream()
        .flatMap(Collection::stream)
        .map(JAXBElement::getValue)
        .filter(ExtrinsicObjectType.class::isInstance)
        .map(ExtrinsicObjectType.class::cast);
  }
}
