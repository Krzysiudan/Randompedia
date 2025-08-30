package daniluk.randopedia.data.model

import daniluk.randopedia.data.remote.model.UserDto
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,
    val fullName: String,
    val email: String,
    val country: String,
    val city: String,
    val age: Int,
    val avatarUrl: String,
    val photoUrl: String
)

fun UserDto.toDomain() = User(
    id = login.uuid,
    fullName = "${name.first} ${name.last}",
    email = email,
    country = location.country,
    city = location.city,
    age = dob.age,
    avatarUrl = picture.thumbnail,
    photoUrl = picture.large
)