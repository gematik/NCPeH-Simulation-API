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

package de.gematik.ncpeh.api.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Arrays;
import lombok.NonNull;

/**
 * Enum representing the two-letter country codes of EU member states according to ISO 3166-1
 * alpha-2.
 *
 * <p>This enum is used to specify the country code in various contexts, such as in API requests and
 * responses.
 */
@Schema(
    enumAsRef = true,
    description =
        "EuCountryCode benennt ein Datenprofil in Form eines zweistelligen EU-Landescodes nach ISO-3166. "
            + "Über das Datenprofil werden die NCPeH-Zertifikate und die homeCommunityId referenziert, "
            + "die der NCPeH-Simulator in einem auszuführenden Request nutzen soll.",
    example = "FR")
public enum EuCountryCode {
  AUSTRIA("AT"),
  BELGIUM("BE"),
  BULGARIA("BG"),
  CROATIA("HR"),
  CYPRUS("CY"),
  CZECHIA("CZ"),
  DENMARK("DK"),
  ESTONIA("EE"),
  FINLAND("FI"),
  FRANCE("FR"),
  GERMANY("DE"),
  GREECE("GR"),
  HUNGARY("HU"),
  IRELAND("IE"),
  ITALY("IT"),
  LATVIA("LV"),
  LITHUANIA("LT"),
  LUXEMBOURG("LU"),
  MALTA("MT"),
  NETHERLANDS("NL"),
  POLAND("PL"),
  PORTUGAL("PT"),
  ROMANIA("RO"),
  SLOVAKIA("SK"),
  SLOVENIA("SI"),
  SPAIN("ES"),
  SWEDEN("SE");

  private final String countryCode;

  EuCountryCode(String countryCode) {
    this.countryCode = countryCode;
  }

  @JsonValue
  public String getCountryCode() {
    return this.countryCode;
  }

  @JsonCreator
  public static EuCountryCode fromValue(@NonNull String countryCode) {
    return Arrays.stream(values())
        .filter(value -> countryCode.equalsIgnoreCase(value.getCountryCode()))
        .findFirst()
        .orElseThrow(
            () ->
                new IllegalArgumentException(
                    String.format("No country code %s known for any EU country", countryCode)));
  }

  public static EuCountryCode fromCountryName(@NonNull String countryName) {
    return Arrays.stream(values())
        .filter(value -> countryName.equalsIgnoreCase(value.name()))
        .findFirst()
        .orElseThrow(
            () ->
                new IllegalArgumentException(
                    String.format("No country name %s known for any EU country", countryName)));
  }
}
