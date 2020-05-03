package io.imalik8088.github.ktorstarter

import com.fasterxml.jackson.databind.SerializationFeature
import io.imalik8088.github.ktorstarter.controller.userRoutes
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.OAuthAccessTokenResponse
import io.ktor.auth.OAuthServerSettings
import io.ktor.auth.authenticate
import io.ktor.auth.authentication
import io.ktor.auth.oauth
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.features.CORS
import io.ktor.features.CallLogging
import io.ktor.features.Compression
import io.ktor.features.ContentNegotiation
import io.ktor.features.deflate
import io.ktor.features.gzip
import io.ktor.features.minimumSize
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.jackson.jackson
import io.ktor.request.path
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import org.koin.dsl.module
import org.koin.ktor.ext.Koin
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo
import org.slf4j.event.Level

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {

    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }

    install(Compression) {
        gzip {
            priority = 1.0
        }
        deflate {
            priority = 10.0
            minimumSize(1024) // condition
        }
    }

    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/") }
    }

    install(CORS) {
        method(HttpMethod.Options)
        method(HttpMethod.Put)
        method(HttpMethod.Delete)
        method(HttpMethod.Patch)
        header(HttpHeaders.Authorization)
        header("MyCustomHeader")
        allowCredentials = true
        anyHost() // @TODO: Don't do this in production if possible. Try to limit it.
    }

    install(Authentication) {
        oauth(keycloakOAuth) {
            client = HttpClient(Apache)
            providerLookup = { keycloakProvider }
            urlProvider = { "http://localhost:8080/oauth" }
        }
    }

    install(Koin) {
        modules(module)
    }

    routing {
        userRoutes()

        get("/") {
            call.respondText(
                """Click <a href="/oauth">here</a> to get tokens, user and password <b>test123</b>""",
                ContentType.Text.Html
            )
        }

        authenticate(keycloakOAuth) {
            get("/oauth") {
                val principal = call.authentication.principal<OAuthAccessTokenResponse.OAuth2>()

                call.respondText("Access Token = ${principal?.accessToken}")
            }
        }

        static("/static") {
            resources("static")
        }
    }
}

const val API_VERSION = "/api/v1"

val module = module {
    single { KMongo.createClient("mongodb://localhost:27017").coroutine }
}

const val KEYCLOAK_ADDRESS = "http://localhost:18080"

val keycloakProvider = OAuthServerSettings.OAuth2ServerSettings(
    name = "keycloak",
    authorizeUrl = "$KEYCLOAK_ADDRESS/auth/realms/ktor-starter/protocol/openid-connect/auth",
    accessTokenUrl = "$KEYCLOAK_ADDRESS/auth/realms/ktor-starter/protocol/openid-connect/token",
    clientId = "book-service",
    clientSecret = "141029f0-7e99-45db-89d2-4eb117d5a95c",  // NEEDS TO BE REPLACED BY THE GENERATED CLIENT SECRET FROM KEYCLOAK
    accessTokenRequiresBasicAuth = false,
    requestMethod = HttpMethod.Post, // must POST to token endpoint
    defaultScopes = listOf("roles")
)
const val keycloakOAuth = "keycloakOAuth"
