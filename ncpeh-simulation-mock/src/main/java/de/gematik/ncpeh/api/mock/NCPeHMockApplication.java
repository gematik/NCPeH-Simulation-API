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

package de.gematik.ncpeh.api.mock;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/** The main class of the Spring Boot application */
@Slf4j
@SpringBootApplication
public class NCPeHMockApplication {

  /**
   * Create the JsonProvider as Bean, which is then used to serialize and deserialize the data,
   * which are processed at the implemented API interface ({@link NCPeHMockApiImpl}).
   *
   * @return {@link JacksonJaxbJsonProvider}
   */
  @Bean
  public JacksonJaxbJsonProvider jsonProvider() {
    final ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
    objectMapper.registerModule(new JavaTimeModule());

    final JacksonJaxbJsonProvider provider = new JacksonJaxbJsonProvider();
    provider.setMapper(objectMapper);
    provider.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
    return provider;
  }

  public static void main(final String[] args) {
    SpringApplication.run(NCPeHMockApplication.class, args);
  }
}
