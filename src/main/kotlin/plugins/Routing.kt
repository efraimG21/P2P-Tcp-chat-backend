package plugins

import chatHandling.ChatDataManager
import com.mongodb.client.MongoCollection
import io.ktor.server.application.*
import io.ktor.server.routing.*
import models.Chat
import models.User
import routing.chatRouting
import routing.userRouting
import routing.webSocketRouting
import socketHandling.WebSocketManager
import userHandling.UserDataManager

fun Application.configureRouting(usersCollection: MongoCollection<User>, chatCollection: MongoCollection<Chat>) {
    val userDataManager = UserDataManager(usersCollection)
    val chatDataManager = ChatDataManager(chatCollection)
    val webSocketManager = WebSocketManager(userDataManager, chatDataManager)

    routing {
        userRouting(userDataManager)
        chatRouting(chatDataManager)
        webSocketRouting(webSocketManager)
    }
}
