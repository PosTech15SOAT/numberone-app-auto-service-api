#!/usr/bin/env bash
set -euo pipefail

if [ -z "${SONAR_TOKEN:-}" ]; then
  echo "Variavel SONAR_TOKEN nao informada."
  echo
  echo "Crie um token no SonarQube em: My Account > Security"
  echo "Depois exporte o token no terminal:"
  echo
  echo "  export SONAR_TOKEN=seu_token_aqui"
  echo
  exit 1
fi

if [ -x "./mvnw" ]; then
  MVN_CMD="./mvnw"
else
  MVN_CMD="mvn"
fi

echo "Executando scan do SonarQube para o projeto NumberOne..."
echo "Comando Maven selecionado: ${MVN_CMD}"
echo

# O caminho padrao do relatorio XML do JaCoCo costuma ser:
# target/site/jacoco/jacoco.xml
#
# Se o projeto tiver relatorios separados para testes unitarios e de integracao,
# ajuste sonar.coverage.jacoco.xmlReportPaths conforme a configuracao real do pom.xml.
#
# Este comando executa testes, gera cobertura JaCoCo e envia a analise para o
# SonarQube local. Dependendo da configuracao real do pom.xml, pode ser necessario
# ajustar goals, profiles ou caminhos de cobertura.
"${MVN_CMD}" clean verify sonar:sonar \
  -Dsonar.projectKey=numberone \
  -Dsonar.projectName=numberone \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.token="${SONAR_TOKEN}" \
  -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco-merged/jacoco.xml
