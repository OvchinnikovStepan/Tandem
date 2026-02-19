#!/usr/bin/env bash
set -eux

apt-get update
apt-get install -y postgresql-client netcat-openbsd

echo "Waiting for Postgres..."
until pg_isready -h postgres -U auth_user -d auth_db; do
  sleep 2
done

echo "Postgres ready"

mvn flyway:migrate \
  -Dflyway.url=jdbc:postgresql://postgres:5432/auth_db \
  -Dflyway.user=auth_user \
  -Dflyway.password=auth_pass

mvn jooq-codegen:generate
mvn clean package -DskipTests
