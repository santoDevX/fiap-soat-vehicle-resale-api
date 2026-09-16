output "api_public_ip" {
  description = "IP público (fixo) da EC2 que roda a API."
  value       = aws_eip.api.public_ip
}

output "db_endpoint" {
  description = "Endpoint do RDS Postgres."
  value       = aws_db_instance.postgres.address
}
