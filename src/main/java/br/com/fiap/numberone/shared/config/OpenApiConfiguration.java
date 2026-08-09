package br.com.fiap.numberone.shared.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class OpenApiConfiguration {

	@Bean
	OpenAPI openAPI() {
		return new OpenAPI()
			.info(new Info()
				.title("NumberOne - API da Oficina")
				.version("v1")
				.description("""
					API REST do Tech Challenge Fase 1 para gestao de oficina mecanica.

					O sistema cobre autenticacao administrativa com JWT, cadastro de clientes e veiculos,
					catalogo de servicos automotivos, estoque de pecas e insumos, ordem de servico,
					orcamento, aprovacao e acompanhamento publico da OS.

					Fluxo principal sugerido para testes:
					1. Fazer login em /api/public/auth/login.
					2. Copiar o accessToken retornado.
					3. Clicar em Authorize no Swagger e informar Bearer <token>.
					4. Criar cliente, veiculo, servico e item de estoque.
					5. Criar a ordem de servico, adicionar servicos/insumos, gerar orcamento e acompanhar o status.
					"""))
			.servers(List.of(
				new Server()
					.url("http://localhost:8080")
					.description("Ambiente local com Docker Compose ou Maven")
			))
			.components(new Components()
				.addSecuritySchemes("bearerAuth", new SecurityScheme()
					.type(SecurityScheme.Type.HTTP)
					.scheme("bearer")
					.bearerFormat("JWT")
					.description("JWT emitido pelo endpoint /api/public/auth/login")))
			.addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
	}
}
