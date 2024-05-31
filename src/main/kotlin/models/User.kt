package models

import kotlinx.serialization.Serializable
import org.bson.types.ObjectId

@Serializable
data class User(
    val _id: String = ObjectId().toString(),
    val name: String,
    val ipAddress: String,
    val port: String,
)

