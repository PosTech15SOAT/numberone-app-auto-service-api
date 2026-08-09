#!/usr/bin/env bash
set -euo pipefail

EVIDENCE_DIR="doc/security/evidencias"

mkdir -p "${EVIDENCE_DIR}"

echo "Pasta de evidencias preparada em: ${EVIDENCE_DIR}"
echo
echo "Capture manualmente no dashboard do SonarQube as seguintes telas:"
echo "  - sonar-dashboard.png"
echo "  - sonar-quality-gate.png"
echo "  - sonar-issues.png"
echo "  - sonar-security-hotspots.png"
echo "  - sonar-coverage.png"
echo
echo "Essas imagens devem ser salvas em: ${EVIDENCE_DIR}"
echo
echo "Depois, preencha o relatorio final em:"
echo "  doc/security/relatorio-vulnerabilidades.md"
