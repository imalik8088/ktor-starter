package io.imalik8088.meinchanda.controller

import com.fasterxml.jackson.annotation.JsonIgnore
import io.imalik8088.meinchanda.API_VERSION
import io.imalik8088.meinchanda.Constants.Companion.DATABASE
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route
import org.bson.codecs.pojo.annotations.BsonId
import org.koin.ktor.ext.inject
import org.litote.kmongo.coroutine.CoroutineClient
import org.litote.kmongo.eq
import org.slf4j.LoggerFactory
import java.util.*

val ENDPOINT = "$API_VERSION/users"

fun Route.userRoutes() {

    val LOG = LoggerFactory.getLogger("UserController")
    val client: CoroutineClient by inject()
    val COLLECTION = "users"

    route(ENDPOINT) {
        get {

            val users = client.getDatabase(DATABASE)
                .getCollection<User>(COLLECTION)
                .find()
                .toList()

            call.respond(HttpStatusCode.OK, users)
        }

        get("/{email}") {
            val emailParam = call.parameters.get("email")
            val user = client.getDatabase(DATABASE).getCollection<User>(COLLECTION).findOne(User::email eq emailParam)

            if (user != null) {
                call.respond(HttpStatusCode.OK, user)
            } else {
                call.respond(HttpStatusCode.NotFound, "")
            }
        }

        post<RegisterRequest>("/register") { request ->
            val user = User(
                username = request.username,
                password = request.password,
                email = request.email
            )
            client.getDatabase(DATABASE)
                .getCollection<User>(COLLECTION)
                .insertOne(user)

            call.respond(HttpStatusCode.Created)
        }

    }
}

data class User(
    @BsonId val id: UUID = UUID.randomUUID(),
    val username: String,
    @JsonIgnore val password: String?,
    val email: String?
)

data class RegisterRequest(val username: String, val password: String, val email: String?)
