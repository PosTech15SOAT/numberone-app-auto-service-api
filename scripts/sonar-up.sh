#!/usr/bin/env bash
set -euo pipefail

echo "Subindo SonarQube local para analise de qualidade e seguranca..."
echo "Este processo pode demorar alguns minutos na primeira inicializacao."
echo

# Preferimos o Docker Compose v2: docker compose.
# Em instalacoes antigas, pode ser necessario adaptar para docker-compose.
docker compose -f docker-compose.sonar.yml up -d

echo
echo "SonarQube solicitado com sucesso."
echo "URL: http://localhost:9000"
echo
echo "Login inicial:"
echo "  usuario: admin"
echo "  senha: admin"
echo
echo "No primeiro acesso, o SonarQube deve solicitar a troca da senha."
echo "Depois de acessar, crie um token em: My Account > Security."
