package br.com.fiap.numberone.shared.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
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
					API REST do Tech Challenge Fase 3 para gestao de oficina mecanica.

					Em producao, a autenticacao e validada pelo API Gateway/Authorizer. A API recebe somente
					o contexto de identidade confiavel e aplica autorizacao por papel, permissao e propriedade.
					O sistema cobre cadastro de clientes e veiculos,
					catalogo de servicos automotivos, estoque de pecas e insumos, ordem de servico,
					orcamento, aprovacao e acompanhamento autenticado da OS.

					No profile local, uma identidade ADMIN controlada por configuracao e usada apenas para desenvolvimento.
					"""))
			.servers(List.of(
				new Server()
					.url("http://localhost:8080")
					.description("Ambiente local com Docker Compose ou Maven")
			))
			.components(new Components());
	}
}
