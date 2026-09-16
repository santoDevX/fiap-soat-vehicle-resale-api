#!/bin/bash
set -euxo pipefail

dnf update -y
dnf install -y docker
systemctl enable docker
systemctl start docker
usermod -aG docker ec2-user

# Script de deploy: usado aqui no boot e depois via SSH manual, se precisar.
# Mantém a lógica de deploy num único lugar em vez de duplicar.
cat > /usr/local/bin/deploy-api.sh <<'EOS'
#!/bin/bash
set -euo pipefail

AWS_REGION="${aws_region}"
ECR_REPOSITORY="${ecr_repository}"
APP_PORT="${app_port}"
DB_HOST="${db_host}"
DB_PORT="${db_port}"
DB_NAME="${db_name}"
DB_USER="${db_username}"
DB_PASS="${db_password}"

# Retry: logo apos o boot, a credencial da instance role pode levar alguns
# segundos pra ficar disponivel via metadata. Sem isso, a primeira chamada
# aws cli falha e o script inteiro morre (cloud-init reporta "error").
retry() {
  local attempts=10 delay=6 i=1
  until "$@"; do
    if [ "$i" -ge "$attempts" ]; then
      echo "Falhou apos $attempts tentativas: $*" >&2
      return 1
    fi
    echo "Tentativa $i/$attempts falhou, tentando de novo em $${delay}s: $*" >&2
    sleep "$delay"
    i=$((i + 1))
  done
}

ACCOUNT_ID=$(retry aws sts get-caller-identity --query Account --output text --region "$AWS_REGION")
ECR_REGISTRY="$ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com"

retry bash -c "aws ecr get-login-password --region '$AWS_REGION' | docker login --username AWS --password-stdin '$ECR_REGISTRY'"

retry docker pull "$ECR_REGISTRY/$ECR_REPOSITORY:latest"

docker stop vehicle-api 2>/dev/null || true
docker rm vehicle-api 2>/dev/null || true

docker run -d \
  --name vehicle-api \
  --restart unless-stopped \
  -p "$APP_PORT":8080 \
  -e SPRING_DATASOURCE_URL="jdbc:postgresql://$DB_HOST:$DB_PORT/$DB_NAME" \
  -e SPRING_DATASOURCE_USERNAME="$DB_USER" \
  -e SPRING_DATASOURCE_PASSWORD="$DB_PASS" \
  -e SPRING_JPA_HIBERNATE_DDL_AUTO=validate \
  "$ECR_REGISTRY/$ECR_REPOSITORY:latest"
EOS

chmod +x /usr/local/bin/deploy-api.sh

# Primeiro (e, por enquanto, unico) deploy acontece no boot da instancia.
/usr/local/bin/deploy-api.sh
