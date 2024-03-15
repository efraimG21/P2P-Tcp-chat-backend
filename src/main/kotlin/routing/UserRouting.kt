package routing

import io.ktor.server.routing.*

fun Route.userRouting() {
    route("/user-handling") {
        get("/get/{uid?}") {}
        post("/sign-on") {}

        get("/user-list") {}
        get("is-exists/{uid?}") {}
    }
}