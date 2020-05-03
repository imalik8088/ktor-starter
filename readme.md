# Ktor starter with Keycloak and MongoDB
needs some polish here and there but basically it works 

## Getting Started

```bash
docker-compose up -d

# intialize keycloak
./scripts/init-keycloak.sh [KEYCLOAK_HOST_WITH_PORT]
./scripts/get-access-token.sh CLIENT_SECRET [KEYCLOAK_HOST] [REALM]

# start and open ktor app
http://localhost:8080
```

## Reference
* https://github.com/scottbrady91/ktor-oauth-example/blob/bbe54c046ef190f2e46e6d9b899e2eb2cebb537d/src/main/kotlin/Main.kt
* https://www.scottbrady91.com/Kotlin/Ktor-using-OAuth-2-and-IdentityServer4
* https://medium.com/slickteam/ktor-and-keycloak-authentication-with-openid-ecd415d7a62e
* https://www.baeldung.com/spring-boot-keycloak
