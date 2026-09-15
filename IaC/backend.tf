# Backend remoto: obrigatório porque quem roda "terraform apply" é o
# GitHub Actions (runner efêmero, uma máquina nova a cada execução). Sem
# state remoto, o Terraform perderia a memória do que já criou a cada run.
#
# O bucket abaixo precisa existir ANTES do primeiro "terraform init" (veja
# o passo manual único no README.md). Blocos de backend não aceitam
# variáveis, então o nome fica hardcoded aqui.
#
# use_lockfile = true usa o lock nativo do backend S3 (Terraform >= 1.10),
# então não precisamos de uma tabela DynamoDB só pra isso.
terraform {
  backend "s3" {
    bucket       = "fiap-vehicle-resale-tfstate"
    key          = "vehicle-resale-api/terraform.tfstate"
    region       = "us-east-2"
    encrypt      = true
    use_lockfile = true
  }
}
