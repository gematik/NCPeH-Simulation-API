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

package de.gematik.ncpeh.api.mock.builder;

import jakarta.xml.soap.MessageFactory;
import jakarta.xml.soap.SOAPBody;
import jakarta.xml.soap.SOAPMessage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.w3c.dom.Document;

@UtilityClass
public class SoapMessageFactory {

  MessageFactory messageFactory;
  DocumentBuilder builder;

  static {
    init();
  }

  @SneakyThrows
  private static void init() {
    messageFactory = MessageFactory.newInstance();
    final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setNamespaceAware(true);
    builder = factory.newDocumentBuilder();
  }

  @SneakyThrows
  public static ByteArrayOutputStream createSoapMessage(final InputStream is) {
    final SOAPMessage soapMessage = messageFactory.createMessage();
    final SOAPBody soapBody = soapMessage.getSOAPBody();
    soapBody.addDocument(parseXMLToDocument(is));
    soapMessage.saveChanges();
    final ByteArrayOutputStream os = new ByteArrayOutputStream();
    soapMessage.writeTo(os);
    return os;
  }

  private static Document parseXMLToDocument(final InputStream xmlData) throws Exception {
    return builder.parse(xmlData);
  }
}
