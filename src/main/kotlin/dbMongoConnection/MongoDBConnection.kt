package dbMongoConnection

import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import models.Chat
import models.User
import org.litote.kmongo.KMongo
import org.litote.kmongo.getCollection
import org.slf4j.LoggerFactory

class MongoDBConnection {
    private val logger = LoggerFactory.getLogger("MongoDBConnection")
    private lateinit var client: com.mongodb.client.MongoClient
    private lateinit var db: MongoDatabase
    lateinit var usersCollection: MongoCollection<User>
    lateinit var chatCollection: MongoCollection<Chat>
    private val connectionString = "mongodb://localhost:27017"

    init {
        connectToMongoDB()
    }

    private fun connectToMongoDB() {
        runBlocking {
            var isConnected = false
            var attempts = 0
            while (!isConnected && attempts < MAX_CONNECTION_ATTEMPTS) {
                try {
                    client = KMongo.createClient(connectionString = connectionString)
                    db = client.getDatabase("Chats")
                    usersCollection = db.getCollection<User>("User")
                    chatCollection = db.getCollection<Chat>("Chat")
                    deleteCollectionsFromDB()

                    isConnected = true
                    logger.info("MongoDB connection initialized successfully.")
                } catch (e: Exception) {
                    attempts++
                    logger.error("Error occurred while establishing MongoDB connection: ${e.message}")
                    delay(RECONNECTION_DELAY_MS)
                }
            }
            if (!isConnected) {
                throw RuntimeException("Failed to connect to MongoDB after $MAX_CONNECTION_ATTEMPTS attempts.")
            }
        }
    }

    fun close() {
        runBlocking {
            deleteCollectionsFromDB()
            client.close()
            logger.info("MongoDB connection closed.")
        }
    }

    private fun deleteCollectionsFromDB() {
//        usersCollection.deleteMany(or(User::uid ne null, User::uid eq null))
//        chatCollection.deleteMany(Chat::uid ne null)
    }

    companion object {
        private const val MAX_CONNECTION_ATTEMPTS = 5
        private const val RECONNECTION_DELAY_MS = 5000L // 5 seconds
    }
}
