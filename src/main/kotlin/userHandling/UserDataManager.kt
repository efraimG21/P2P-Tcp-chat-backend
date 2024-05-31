package userHandling

import com.mongodb.client.MongoCollection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import models.User
import org.litote.kmongo.*
import org.slf4j.LoggerFactory

class UserDataManager(private val usersCollection: MongoCollection<User>) {
    private val logger = LoggerFactory.getLogger("user data manager")

    suspend fun signOnUser(user: User): Boolean {
        return withContext(Dispatchers.IO) {
            if (isUserDetailsExists(user)) {
                logger.warn("Attempt to sign on user with existing details: $user")
                false
            } else {
                usersCollection.insertOne(user)
                true
            }
        }
    }

    private suspend fun isUserDetailsExists(user: User): Boolean {
        return withContext(Dispatchers.IO) {
            usersCollection.findOne(or(User::name eq user.name, User::port eq user.port, User::ipAddress eq user.ipAddress)) != null
        }
    }

    suspend fun getUser(uid: String): User? {
        return withContext(Dispatchers.IO) {
            usersCollection.findOne(User::_id eq uid)
        }
    }

    suspend fun getUsersList(): List<User> {
        return withContext(Dispatchers.IO) {
            usersCollection.find().toList()
        }
    }

    suspend fun doesUserExist(uid: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                usersCollection.findOne(User::_id eq uid) != null
            } catch (e: Exception) {
                logger.error("Error occurred while checking if user exists: ${e.message}")
                false
            }
        }
    }
}
