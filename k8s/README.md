# Kubernetes da API

Os manifests da aplicacao usam Kustomize para compartilhar a configuracao
comum e isolar homologacao e producao no mesmo cluster EKS.

## Estrutura

```text
k8s/
|-- base/
|   |-- configmap.yaml
|   |-- deployment.yaml
|   |-- hpa.yaml
|   |-- poddisruptionbudget.yaml
|   |-- service.yaml
|   |-- serviceaccount.yaml
|   `-- kustomization.yaml
`-- overlays/
    |-- homolog/
    `-- production/
```

O banco PostgreSQL e externo ao cluster e pertence ao repositorio
`postech15soat-infra-database`. O API Gateway e o Authorizer pertencem ao fluxo
de autenticacao. Este repositorio administra apenas a aplicacao principal.

## Recursos

- Deployment com duas replicas e rolling update sem indisponibilidade;
- probes de startup, readiness e liveness em `/api/public/health`;
- requests e limits de CPU/memoria;
- afinidade preferencial para distribuir pods entre nodes;
- Service `ClusterIP`, sem acesso publico direto;
- HPA entre duas e cinco replicas por CPU e memoria;
- PodDisruptionBudget com pelo menos uma replica disponivel;
- ServiceAccount sem token montado no pod;
- labels de ambiente e observabilidade.

O HPA exige o Metrics Server no cluster. A instalacao desse componente e do
agente Datadog/New Relic deve ser tratada como add-on compartilhado no
repositorio de infraestrutura cloud.

## Validacao local

Com `kubectl` instalado:

```bash
kubectl kustomize k8s/overlays/homolog
kubectl kustomize k8s/overlays/production
```

Validacao de schema sem cluster, usando Kubeconform:

```bash
kubectl kustomize k8s/overlays/homolog > homolog.yaml
docker run --rm -v "$PWD:/workspace" ghcr.io/yannh/kubeconform:v0.8.0 \
  -strict -summary -kubernetes-version 1.36.0 /workspace/homolog.yaml
```

## Fluxo de deploy

O workflow `.github/workflows/deploy.yml` executa:

1. testes automatizados;
2. autenticacao temporaria no AWS Academy;
3. build da imagem Docker;
4. push da imagem com tag igual ao SHA do commit;
5. configuracao do acesso ao EKS;
6. criacao idempotente de ConfigMap e Secret de runtime;
7. aplicacao do overlay correspondente a branch;
8. espera pelo rollout e smoke test do health endpoint.

| Branch | GitHub environment | Namespace |
| --- | --- | --- |
| `develop` | `homolog` | `numberone-homolog` |
| `main` | `production` | `numberone-production` |

## Configuracao dos GitHub environments

Configure os mesmos nomes em `homolog` e `production`, alterando os valores
quando os ambientes tiverem dependencias diferentes.

### Secrets

| Nome | Uso |
| --- | --- |
| `AWS_ACCESS_KEY_ID` | Credencial temporaria do AWS Academy |
| `AWS_SECRET_ACCESS_KEY` | Credencial temporaria do AWS Academy |
| `AWS_SESSION_TOKEN` | Token obrigatorio da sessao do Learner Lab |
| `DB_USERNAME` | Usuario do PostgreSQL/RDS |
| `DB_PASSWORD` | Senha do PostgreSQL/RDS |
| `MAIL_USERNAME` | Usuario SMTP; pode ser vazio quando autenticacao estiver desabilitada |
| `MAIL_PASSWORD` | Senha SMTP; pode ser vazia quando autenticacao estiver desabilitada |

### Variables

| Nome | Exemplo/uso |
| --- | --- |
| `AWS_REGION` | `us-east-1` |
| `AWS_ACCOUNT_ID` | ID da conta do Learner Lab |
| `ECR_REPOSITORY` | `numberone-auto-service-api` |
| `EKS_CLUSTER_NAME` | `numberone-lab-eks` |
| `DB_HOST` | Endpoint do RDS sem protocolo nem porta |
| `DB_PORT` | `5432` |
| `DB_NAME` | `numberone` |
| `MAIL_HOST` | Host SMTP acessivel pelo cluster |
| `MAIL_PORT` | `587` ou a porta do provedor escolhido |
| `MAIL_SMTP_AUTH` | `true` ou `false` |
| `MAIL_SMTP_STARTTLS` | `true` ou `false` |
| `APP_MAIL_FROM` | Remetente usado nas notificacoes |
| `SERVICE_ORDER_APPROVAL_BASE_URL` | URL base usada nos links de aprovacao |

As credenciais AWS expiram ao encerrar a sessao do Learner Lab e precisam ser
atualizadas antes de executar novamente o deploy.

## Seguranca

- nenhum JWT ou segredo administrativo e armazenado no cluster;
- os valores sensiveis sao materializados a partir de GitHub Secrets;
- o provider de identidade da aplicacao e `gateway`;
- o Service nao recebe Load Balancer publico;
- o API Gateway deve remover ou sobrescrever headers de identidade enviados
  pelo cliente antes de encaminhar a requisicao.
