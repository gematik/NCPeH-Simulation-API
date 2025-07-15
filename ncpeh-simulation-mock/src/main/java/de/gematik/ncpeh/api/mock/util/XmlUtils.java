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

package de.gematik.ncpeh.api.mock.util;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElementDecl;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.NoSuchElementException;
import javax.xml.stream.XMLInputFactory;
import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@UtilityClass
@Slf4j
@Accessors(fluent = true)
public class XmlUtils {

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
}
