terraform {
  required_version = ">= 1.10.0" # use_lockfile no backend.tf exige isso

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
}

provider "aws" {
  region = var.aws_region
}
