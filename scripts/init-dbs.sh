#!/usr/bin/env bash
# The guide's project tree labels this script "Create multiple databases"
# (one per service: users_db / orders_db / products_db). This project was
# built against a single shared database instead (per your request), so
# this script creates just that one — "micro" — idempotently.
#
# Usage: DB_HOST=localhost DB_PORT=5432 DB_USER=postgres ./scripts/init-dbs.sh
set -euo pipefail

DB_HOST="${DB_HOST:-localhost}"
DB_PORT="${DB_PORT:-5432}"
DB_USER="${DB_USER:-postgres}"
DB_NAME="${DB_NAME:-micro}"

echo "Ensuring database '$DB_NAME' exists on $DB_HOST:$DB_PORT..."

if psql -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USER" -lqt | cut -d '|' -f 1 | grep -qw "$DB_NAME"; then
  echo "Database '$DB_NAME' already exists — nothing to do."
else
  psql -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USER" -c "CREATE DATABASE $DB_NAME;"
  echo "Created database '$DB_NAME'."
fi
