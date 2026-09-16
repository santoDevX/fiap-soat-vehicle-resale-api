resource "aws_security_group" "ec2" {
  name        = "${var.project_name}-ec2-sg"
  description = "SG da EC2 que roda a API"
  vpc_id      = data.aws_vpc.default.id

  # trivy:ignore:AVD-AWS-0107 SSH aberto pra internet de proposito: quem
  # conecta e o runner do GitHub Actions, com IP dinamico a cada execucao.
  # O acesso continua exigindo a chave privada (sem senha), decisao
  # documentada em IaC/README.md.
  ingress {
    description = "SSH"
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = [var.allowed_ssh_cidr]
  }

  ingress {
    description = "API"
    from_port   = var.app_port
    to_port     = var.app_port
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  # trivy:ignore:AVD-AWS-0104 egress irrestrito de proposito: a instancia
  # precisa alcancar a internet (ECR, RDS, repositorios do dnf) sem VPC
  # endpoints dedicados, fora de escopo pra este projeto academico.
  egress {
    description = "Todo trafego de saida"
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "${var.project_name}-ec2-sg"
  }
}

# Sem bloco "egress": o RDS nao inicia conexao nenhuma pra fora (backup,
# patch e snapshot sao geridos pelo plano de controle da AWS, fora da
# rede da instancia), entao nao precisa de nenhuma regra de saida.
resource "aws_security_group" "rds" {
  name        = "${var.project_name}-rds-sg"
  description = "SG do RDS Postgres, so acessivel a partir da EC2 da API"
  vpc_id      = data.aws_vpc.default.id

  ingress {
    description     = "Postgres a partir da EC2 da API"
    from_port       = 5432
    to_port         = 5432
    protocol        = "tcp"
    security_groups = [aws_security_group.ec2.id]
  }

  tags = {
    Name = "${var.project_name}-rds-sg"
  }
}
