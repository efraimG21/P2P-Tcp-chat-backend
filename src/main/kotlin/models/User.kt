package models

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.bson.types.ObjectId

@Serializable
data class User(
    val _id: String,
    val name: String,
    val ipAddress: String,
    val port: String,
)
