package chatHandling

import com.mongodb.client.MongoCollection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import models.Chat
import models.Message
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import org.litote.kmongo.or
import org.litote.kmongo.push

class ChatDataManager(private val chatCollection: MongoCollection<Chat>) {

    suspend fun getChat(uid1: String, uid2: String): Chat {
        return withContext(Dispatchers.IO) {
            val existingChat = chatCollection.findOne(
                or(
                    Chat::usersUid eq Pair(uid1, uid2),
                    Chat::usersUid eq Pair(uid2, uid1)
                )
            )
            existingChat ?: createNewChat(uid1, uid2)
        }
    }

    private suspend fun createNewChat(uid1: String, uid2: String): Chat {
        val newChat = Chat(usersUid = Pair(uid1, uid2), messages = emptyList())
        withContext(Dispatchers.IO) {
            chatCollection.insertOne(newChat)
        }
        return newChat
    }

    suspend fun addMessage(chatUid: String, message: Message) {
        withContext(Dispatchers.IO) {
            chatCollection.updateOne(
                Chat::_id eq chatUid,
                push(Chat::messages, message)
            )
        }
    }
}