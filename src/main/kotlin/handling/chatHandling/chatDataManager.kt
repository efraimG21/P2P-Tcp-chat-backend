package handling.chatHandling

import com.mongodb.MongoException
import com.mongodb.client.MongoCollection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import models.Chat
import models.Message
import org.litote.kmongo.*
import routing.logger

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
            try {
                chatCollection.insertOne(newChat)
            } catch (e: MongoException) {
                logger.error("Error creating new chat: ${e.message}")
                throw e
            }
        }
        return newChat
    }

    suspend fun addMessage(chatUid: String, message: Message) {
        withContext(Dispatchers.IO) {
            try {
                chatCollection.updateOne(
                    Chat::_id eq chatUid,
                    push(Chat::messages, message)
                )
            } catch (e: MongoException) {
                logger.error("Error adding message: ${e.message}")
                throw e
            }
        }
    }

    suspend fun getAllChats(uid: String): List<Chat> {
        return withContext(Dispatchers.IO) {
            try {
                chatCollection.find(
                    "{ \$or: [ { 'usersUid.first': '$uid' }, { 'usersUid.second': '$uid' } ] }"
                )
                    .descendingSort(Chat::messages / Message::timeStamp)
                    .toList()
            } catch (e: MongoException) {
                logger.error("Error fetching chats: ${e.message}")
                emptyList()
            }
        }
    }
}
