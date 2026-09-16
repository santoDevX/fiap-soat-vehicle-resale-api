# Chave gerada uma vez fora do Terraform (ssh-keygen) e distribuída via
# GitHub Secrets (EC2_SSH_PUBLIC_KEY / EC2_SSH_PRIVATE_KEY). Mais simples
# que o Terraform gerar a chave a cada apply: a chave privada nunca precisa
# ser lida de volta do state ou de um output.
resource "aws_key_pair" "ec2" {
  key_name   = "${var.project_name}-key"
  public_key = var.ec2_ssh_public_key
}

# IP fixo: sem ele, a chave SSH do deploy.yml perderia o alvo toda vez que a
# instância reiniciasse.
resource "aws_eip" "api" {
  domain = "vpc"

  tags = {
    Name = "${var.project_name}-eip"
  }
}

resource "aws_eip_association" "api" {
  instance_id   = aws_instance.api.id
  allocation_id = aws_eip.api.id
}

data "aws_iam_policy_document" "ec2_assume" {
  statement {
    actions = ["sts:AssumeRole"]

    principals {
      type        = "Service"
      identifiers = ["ec2.amazonaws.com"]
    }
  }
}

resource "aws_iam_role" "ec2" {
  name               = "${var.project_name}-ec2-role"
  assume_role_policy = data.aws_iam_policy_document.ec2_assume.json
}

resource "aws_iam_role_policy_attachment" "ecr_read" {
  role       = aws_iam_role.ec2.name
  policy_arn = "arn:aws:iam::aws:policy/AmazonEC2ContainerRegistryReadOnly"
}

resource "aws_iam_instance_profile" "ec2" {
  name = "${var.project_name}-ec2-profile"
  role = aws_iam_role.ec2.name
}

resource "aws_instance" "api" {
  ami                    = data.aws_ami.al2023.id
  instance_type          = var.instance_type
  subnet_id              = data.aws_subnets.default.ids[0]
  vpc_security_group_ids = [aws_security_group.ec2.id]
  key_name               = aws_key_pair.ec2.key_name
  iam_instance_profile   = aws_iam_instance_profile.ec2.name

  user_data = templatefile("${path.module}/scripts/user_data.sh.tpl", {
    aws_region     = var.aws_region
    ecr_repository = var.ecr_repository_name
    db_host        = aws_db_instance.postgres.address
    db_port        = 5432
    db_name        = var.db_name
    db_username    = var.db_username
    db_password    = var.db_password
    app_port       = var.app_port
  })

  user_data_replace_on_change = true

  # IMDSv2 obrigatorio: exige token pra consultar o metadata service,
  # mitigando SSRF que tentaria roubar credenciais da instance role.
  metadata_options {
    http_tokens = "required"
  }

  root_block_device {
    encrypted = true
  }

  tags = {
    Name = "${var.project_name}-api"
  }
}
