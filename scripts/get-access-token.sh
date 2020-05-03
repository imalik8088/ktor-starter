#!/usr/bin/env bash

MY_ACCESS_TOKEN_FULL=$(
  docker exec -t -e CLIENT_SECRET=$1 -e KEYCLOAK_HOST=${2:-localhost} -e REALM=${3:-ktor-starter} ktor-starter-keycloak bash -c '
    curl -s -X POST \
    http://$KEYCLOAK_HOST:8080/auth/realms/$REALM/protocol/openid-connect/token \
    -H "Content-Type: application/x-www-form-urlencoded" \
    -d "username=test123" \
    -d "password=test123" \
    -d "grant_type=password" \
    -d "client_secret=$CLIENT_SECRET" \
    -d "client_id=book-service"
  '
)

MY_ACCESS_TOKEN=$(echo $MY_ACCESS_TOKEN_FULL | jq -r .access_token)
echo "Bearer $MY_ACCESS_TOKEN"
