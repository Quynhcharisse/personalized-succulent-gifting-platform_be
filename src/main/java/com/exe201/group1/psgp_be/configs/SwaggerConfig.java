package com.exe201.group1.psgp_be.configs;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                title = "PSGP",
                version = "1.0",
                description = "Personalized succulent gifting platform"
        ),

        servers = {
                @Server (
                        description = "localhost",
                        url = "http://localhost:8080/"
                ),
                @Server (
                        description = "Deploy",
                        url = "https://succulentapp.orangeglacier-1e02abb7.southeastasia.azurecontainerapps.io/"
                )
        }
)
public class SwaggerConfig {

}
