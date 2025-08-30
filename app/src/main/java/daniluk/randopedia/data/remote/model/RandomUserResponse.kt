package daniluk.randopedia.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
@Serializable
data class RandomUserResponse(val results: List<UserDto>, val info: InfoDto)

@Serializable data class InfoDto(val seed: String? = null, val results: Int? = null, val page: Int? = null, val version: String? = null)

@Serializable
data class UserDto(
    val gender: String? = null,
    val name: NameDto,
    val location: LocationDto,
    val email: String,
    val login: LoginDto,
    val dob: DobDto,
    val phone: String? = null,
    val cell: String? = null,
    val picture: PictureDto,
    val nat: String? = null
)

@Serializable data class NameDto(val title: String? = null, val first: String, val last: String)
@Serializable
data class LocationDto(
    val city: String, val state: String? = null, val country: String,
    val street: StreetDto? = null, val postcode: kotlinx.serialization.json.JsonElement? = null
)
@Serializable data class StreetDto(val number: Int? = null, val name: String? = null)
@Serializable data class LoginDto(val uuid: String)
@Serializable data class DobDto(val date: String, val age: Int)
@Serializable data class PictureDto(val large: String, val medium: String, val thumbnail: String)
