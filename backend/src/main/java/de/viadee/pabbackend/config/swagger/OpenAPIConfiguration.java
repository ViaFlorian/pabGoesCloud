package de.viadee.pabbackend.config.swagger;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
    info = @Info(
        title = "Projektabrechnung (viadee.de)",
        description = "REST-API der Anwendung Projektabrechng",
        version = "1.0.0"),
    servers = @Server(url = "http://localhost:8080"))
class OpenAPIConfiguration {

}
