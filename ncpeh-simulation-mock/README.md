<img align="right" width="200" height="37" src="../Gematik_Logo_Flag.png" alt="Gematik Logo"/> <br/> 

# NCPeH-Simulation-Mock

This module contains a simple mock, to illustrate the implementation of the NCPeH-Simulation-API
and provide implementors of a client using the API something to test with.

The mock does not verify or validate requests and the responses it returns are technically correct,
but they are created from templates and do not reflect the data submitted in the request.

As it is a Spring Boot application, it can simply be started using the `java -jar` command.

Per default the URL to call the API operations is: `http://<hostname or IP>:8082/rest/triggerInterface/<operation name>`  

The built versions can also be found on [Maven Central](https://repo1.maven.org/maven2/de/gematik/api/ncpeh-simulation-mock/).
