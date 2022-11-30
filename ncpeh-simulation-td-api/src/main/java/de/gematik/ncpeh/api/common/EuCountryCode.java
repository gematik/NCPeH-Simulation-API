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

package de.gematik.ncpeh.api.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Arrays;
import lombok.NonNull;

@Schema(
    enumAsRef = true,
    description =
        "EuCountryCode benennt ein Datenprofil in Form eines zweistelligen EU-Landescodes. "
            + "Über das Datenprofil werden die NCPeH-Zertifikate und die homeCommunityId referenziert, "
            + "die der NCPeH-Simulator in einem auszuführenden Request nutzen soll.")
public enum EuCountryCode {
  BELGIUM("BE"),
  BULGARIA("BG"),
  CZECH("CZ"),
  DENMARK("DK"),
  GERMANY("DE"),
  ESTONIA("EE"),
  IRELAND("IE"),
  GREECE("EL"),
  SPAIN("ES"),
  FRANCE("FR"),
  CROATIA("HR"),
  ITALY("IT"),
  CYPRESS("CY"),
  LATVIA("LV"),
  LITHUANIA("LT"),
  LUXEMBOURG("LU"),
  HUNGARY("HU"),
  MALTA("MT"),
  NETHERLANDS("NL"),
  AUSTRIA("AT"),
  POLAND("PL"),
  PORTUGAL("PT"),
  ROMANIA("RO"),
  SLOVENIA("SI"),
  SLOVAKIA("SK"),
  FINLAND("FI"),
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
}
