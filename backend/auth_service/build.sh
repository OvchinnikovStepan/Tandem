#!/usr/bin/env bash
set -eux

apt-get update
apt-get install -y postgresql-client netcat-openbsd

echo "Waiting for Postgres..."
until pg_isready -h postgres -U auth_user -d auth_db; do
  sleep 2
done

echo "Postgres ready"

echo "Running Flyway migrations..."
mvn flyway:migrate \
  -Dflyway.url=$DB_URL \
  -Dflyway.user=$DB_USER \
  -Dflyway.password=$DB_PASSWORD

echo "Generating jOOQ sources..."
mvn jooq-codegen:generate -Pjooq \
  -Ddb.url=$DB_URL \
  -Ddb.user=$DB_USER \
  -Ddb.password=$DB_PASSWORD


echo "Running tests..."
mvn test


echo "Building final package..."
mvn clean package -DskipTests -Pjooq \
  -Ddb.url=$DB_URL \
  -Ddb.user=$DB_USER \
  -Ddb.password=$DB_PASSWORD