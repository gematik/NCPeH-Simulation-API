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

package de.gematik.ncpeh.api.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Arrays;
import lombok.NonNull;

@Schema(enumAsRef = true, name = "SlotName")
public enum SlotName {
  FORMAT_CODE("$XDSDocumentEntryFormatCode"),
  ENTRY_TYPE_CODE("$XDSDocumentEntryTypeCode"),
  PRACTICE_SETTING_CODE("$XDSDocumentEntryPracticeSettingCode"),
  CREATION_TIME_FROM("$XDSDocumentEntryCreationTimeFrom"),
  CREATION_TIME_TO("$XDSDocumentEntryCreationTimeTo"),
  SERVICE_START_TIME_FROM("$XDSDocumentEntryServiceStartTimeFrom"),
  SERVICE_START_TIME_TO("$XDSDocumentEntryServiceStartTimeTo"),
  SERVICE_STOP_TIME_FROM("$XDSDocumentEntryServiceStopTimeFrom"),
  SERVICE_STOP_TIME_TO("$XDSDocumentEntryServiceStopTimeTo"),
  HEALTHCARE_FACILITY_TYPE_CODE("$XDSDocumentEntryHealthcareFacilityTypeCode"),
  EVENT_CODE_LIST("$XDSDocumentEntryEventCodeList"),
  CONFIDENTIALITY_CODE("$XDSDocumentEntryConfidentialityCode"),
  AUTHOR_PERSON("$XDSDocumentEntryAuthorPerson"),
  TYPE("$XDSDocumentEntryType");

  private final String name;

  SlotName(String name) {
    this.name = name;
  }

  @JsonValue
  public String getName() {
    return this.name;
  }

  @JsonCreator
  public static SlotName fromValue(@NonNull String name) {
    return Arrays.stream(values())
        .filter(value -> name.equalsIgnoreCase(value.getName()))
        .findFirst()
        .orElseThrow(
            () -> new IllegalArgumentException(String.format("No slot name %s is known", name)));
  }
}
