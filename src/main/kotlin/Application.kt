import dbMongoConnection.MongoDBConnection
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.runBlocking
import plugins.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}


fun Application.module() {
    val mongoDBConnection = MongoDBConnection()
    runBlocking {
        configureSecurity()
        configureHTTP()
        configureMonitoring()
        configureSerialization()
        configureSockets()
        configureRouting(mongoDBConnection.usersCollection, mongoDBConnection.chatCollection)
    }

    environment.monitor.subscribe(ApplicationStopping) {
        mongoDBConnection.close()
    }
}
