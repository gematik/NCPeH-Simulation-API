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

package de.gematik.ncpeh.api.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Arrays;
import lombok.NonNull;

@Schema(enumAsRef = true, example = "urn:oasis:names:tc:xspa:1.0:subject:organization")
public enum Saml2AttributeName {
  SUBJECT_ID("urn:oasis:names:tc:xspa:1.0:subject:subject-id"),
  SUBJECT_ROLE("urn:oasis:names:tc:xacml:2.0:subject:role"),
  SUBJECT_ORGANIZATION("urn:oasis:names:tc:xspa:1.0:subject:organization"),
  SUBJECT_ORGANIZATION_ID("urn:oasis:names:tc:xspa:1.0:subject:organization-id"),
  SUBJECT_ON_BEHALF_OF("urn:ehdsi:names:subject:on-behalf-of"),
  HEALTHCARE_FACILITY_TYPE("urn:ehdsi:names:subject:healthcare-facility-type"),
  ENVIRONMENT_LOCALITY("urn:oasis:names:tc:xspa:1.0:environment:locality"),
  SUBJECT_CLINICAL_SPECIALTY("urn:ehdsi:names:subject:clinical-speciality");

  private final String attributeName;

  Saml2AttributeName(final String attributeName) {
    this.attributeName = attributeName;
  }

  @JsonValue
  public String getAttributeName() {
    return this.attributeName;
  }

  @JsonCreator
  public static Saml2AttributeName fromString(@NonNull final String attributeName) {
    return Arrays.stream(values())
        .filter(value -> attributeName.equalsIgnoreCase(value.getAttributeName()))
        .findFirst()
        .orElseThrow(
            () ->
                new IllegalArgumentException(
                    String.format("No Saml2 attribute name %s is known", attributeName)));
  }

  public static final int VALUE_COUNT = Saml2AttributeName.values().length;
}
