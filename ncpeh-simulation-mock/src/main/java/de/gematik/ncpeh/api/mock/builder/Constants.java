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

package de.gematik.ncpeh.api.mock.builder;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import lombok.experimental.UtilityClass;
import org.springframework.http.MediaType;

@UtilityClass
public class Constants {
  public static final URI PSEUDO_URI = URI.create("http://pseudoDn:12345/pseudoPath");
  public static final MediaType APPLICATION_SOAP_XML =
      new MediaType("application", "soap+xml", StandardCharsets.UTF_8);

  public static final String PATIENT_SERVICE_RETRIEVE_MSG =
      new StringBuilder()
          .append(
              "<s:Envelope xmlns:a=\"http://www.w3.org/2005/08/addressing\" xmlns:s=\"http://www.w3.org/2003/05/soap-envelope\"><s:Header xmlns:s=\"http://www.w3.org/2003/05/soap-envelope\"><a:Action s:mustUnderstand=\"1\">urn:ihe:iti:2007:CrossGatewayRetrieve</a:Action><a:MessageID>urn:uuid:1af5e71f-74a6-4d64-8652-739c722822c8</a:MessageID><a:ReplyTo><a:Address>http://www.w3.org/2005/08/addressing/anonymous</a:Address></a:ReplyTo><a:To s:mustUnderstand=\"1\">http://91.52.226.17:8080/services/handleCrossGatewayQuery</a:To></s:Header><s:Body xmlns:s=\"http://www.w3.org/2003/05/soap-envelope\"><xdsb:RetrieveDocumentSetRequest xmlns:lcm=\"urn:oasis:names:tc:ebxml-regrep:xsd:lcm:3.0\" xmlns:rim=\"urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0\" xmlns:rs=\"urn:oasis:names:tc:ebxml-regrep:xsd:rs:3.0\" xmlns:xdsb=\"urn:ihe:iti:xds-b:2007\" xmlns:xop=\"http://www.w3.org/2004/08/xop/include\">\n")
          .append("    <xdsb:DocumentRequest>\n")
          .append(
              "        <xdsb:HomeCommunityId>urn:oid:2.16.17.10.50.112.9.151</xdsb:HomeCommunityId>\n")
          .append(
              "        <xdsb:RepositoryUniqueId>2.16.17.710.850.1000.990.101</xdsb:RepositoryUniqueId>\n")
          .append(
              "        <xdsb:DocumentUniqueId>2.16.620.1.101.10.3.29.60591^1014.2</xdsb:DocumentUniqueId>\n")
          .append("    </xdsb:DocumentRequest>\n")
          .append("</xdsb:RetrieveDocumentSetRequest></s:Body></s:Envelope>")
          .toString();

  public static final String PATIENT_SERVICE_RETRIEVE_RESPONSE =
      new StringBuilder()
          .append("<soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\">\n")
          .append("   <soapenv:Header xmlns:wsa=\"http://www.w3.org/2005/08/addressing\">\n")
          .append(
              "      <wsa:To xmlns:mustUnderstand=\"http://www.w3.org/2003/05/soap-envelope\"\n")
          .append(
              "              mustUnderstand:mustUnderstand=\"true\">http://www.w3.org/2005/08/addressing/anonymous</wsa:To>\n")
          .append("      <wsa:Action>urn:ihe:iti:2007:CrossGatewayRetrieveResponse</wsa:Action>\n")
          .append(
              "      <wsa:MessageID>urn:uuid:5bb9fcd4-32b5-45ee-9548-b2140c0b3e49</wsa:MessageID>\n")
          .append(
              "      <wsa:RelatesTo>urn:uuid:1af5e71f-74a6-4d64-8652-739c722822c8</wsa:RelatesTo>\n")
          .append("   </soapenv:Header>\n")
          .append("   <soapenv:Body>\n")
          .append("      <ns5:RetrieveDocumentSetResponse xmlns:ns5=\"urn:ihe:iti:xds-b:2007\"\n")
          .append(
              "                                       xmlns=\"urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0\"\n")
          .append(
              "                                       xmlns:ns2=\"urn:oasis:names:tc:ebxml-regrep:xsd:rs:3.0\"\n")
          .append(
              "                                       xmlns:ns3=\"urn:oasis:names:tc:ebxml-regrep:xsd:query:3.0\"\n")
          .append(
              "                                       xmlns:ns4=\"urn:oasis:names:tc:ebxml-regrep:xsd:lcm:3.0\"\n")
          .append("                                       xmlns:ns6=\"urn:hl7-org:v3\">\n")
          .append(
              "         <ns2:RegistryResponse status=\"urn:oasis:names:tc:ebxml-regrep:ResponseStatusType:Success\"/>\n")
          .append("         <ns5:DocumentResponse>\n")
          .append(
              "            <ns5:HomeCommunityId>urn:oid:2.16.17.710.850.1000.990.1</ns5:HomeCommunityId>\n")
          .append(
              "            <ns5:RepositoryUniqueId>2.16.17.710.850.1000.990.1</ns5:RepositoryUniqueId>\n")
          .append(
              "            <ns5:DocumentUniqueId>2.16.620.1.101.10.3.29.54259^1001.1</ns5:DocumentUniqueId>\n")
          .append("            <ns5:mimeType>text/xml</ns5:mimeType>\n")
          .append(
              "            <ns5:Document>PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0i..........</ns5:Document>\n")
          .append("         </ns5:DocumentResponse>\n")
          .append("      </ns5:RetrieveDocumentSetResponse>\n")
          .append("   </soapenv:Body>\n")
          .append("</soapenv:Envelope>")
          .toString();
}
