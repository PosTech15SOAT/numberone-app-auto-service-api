#!/usr/bin/env bash
set -euo pipefail

echo "Parando SonarQube local..."

# Preferimos o Docker Compose v2: docker compose.
# Em instalacoes antigas, pode ser necessario adaptar para docker-compose.
docker compose -f docker-compose.sonar.yml down

echo
echo "Containers removidos. Os volumes foram preservados por padrao."
echo "Para limpar tambem os dados persistidos, execute manualmente:"
echo "  docker compose -f docker-compose.sonar.yml down -v"
