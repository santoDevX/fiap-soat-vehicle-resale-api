variable "aws_region" {
  description = "Região AWS. Deve bater com env.AWS_REGION em .github/workflows/deploy.yml."
  type        = string
  default     = "us-east-2"
}

variable "project_name" {
  description = "Prefixo usado no nome dos recursos."
  type        = string
  default     = "vehicle-resale-api"
}

variable "ecr_repository_name" {
  description = "Nome do repositório ECR (já existente, criado pelo deploy.yml)."
  type        = string
  default     = "vehicle-resale-api"
}

variable "instance_type" {
  description = "Tipo da instância EC2 que roda a API. t3.micro é free tier (750h/mês nos primeiros 12 meses)."
  type        = string
  default     = "t3.micro"
}

variable "db_instance_class" {
  description = "Classe da instância RDS. db.t3.micro é free tier (750h/mês nos primeiros 12 meses)."
  type        = string
  default     = "db.t3.micro"
}

variable "db_name" {
  description = "Nome do banco Postgres."
  type        = string
  default     = "vehicledb"
}

variable "db_username" {
  description = "Usuário master do Postgres."
  type        = string
  default     = "vehicleuser"
}

variable "app_port" {
  description = "Porta em que a API escuta (mapeada 1:1 na EC2)."
  type        = number
  default     = 8080
}

variable "allowed_ssh_cidr" {
  description = "CIDR autorizado a acessar a porta 22 da EC2. Default liberado pra internet porque quem conecta é o runner do GitHub Actions (IP dinâmico a cada execução); o acesso continua exigindo a chave privada correspondente."
  type        = string
  default     = "0.0.0.0/0"
}

variable "db_password" {
  description = "Senha do usuário master do Postgres. Vem do secret DB_PASSWORD do GitHub Actions (TF_VAR_db_password), definida uma vez por você."
  type        = string
  sensitive   = true
}

variable "ec2_ssh_public_key" {
  description = "Chave pública SSH (formato OpenSSH) autorizada na EC2. Gerada uma vez com ssh-keygen; vem do secret EC2_SSH_PUBLIC_KEY do GitHub Actions."
  type        = string
}
