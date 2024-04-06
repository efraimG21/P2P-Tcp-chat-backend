package userHandling

import com.mongodb.client.FindIterable
import com.mongodb.client.MongoCollection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import models.User
import org.bson.conversions.Bson
import org.bson.types.ObjectId
import org.litote.kmongo.*
import org.slf4j.LoggerFactory

class UserDataManager(private val usersCollection: MongoCollection<User>) {
    private val logger = LoggerFactory.getLogger("user data manager")

    suspend fun getUser(uid: String): User? {
        return withContext(Dispatchers.IO){
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
                usersCollection.find().toList().find { it._id.equals(uid)} != null
            } catch (e: Exception) {
                logger.error("Error occurred while checking if user exists: ${e.message}")
                false
            }
        }
    }

}
