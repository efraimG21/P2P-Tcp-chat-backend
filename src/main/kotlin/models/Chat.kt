package models

import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

@Serializable
data class Chat(
    @BsonId
    val _id: String = ObjectId().toString(),
    val usersUid: Pair<String, String>,
    val messages: List<Message>
)
