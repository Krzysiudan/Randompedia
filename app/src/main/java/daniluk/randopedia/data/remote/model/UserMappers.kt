package daniluk.randopedia.data.remote.model

import daniluk.randopedia.data.local.entity.BookmarkedUserEntity
import daniluk.randopedia.domain.model.User

fun UserDto.toDomain() = User(
    id = login.uuid,
    fullName = "${name.first} ${name.last}",
    email = email,
    country = location.country,
    city = location.city,
    phone = phone,
    avatarUrl = picture.thumbnail,
    photoUrl = picture.large
)

fun User.toEntity() = BookmarkedUserEntity(
    id = id,
    fullName = fullName,
    email = email,
    country = country,
    city = city,
    avatarUrl = avatarUrl,
    photoUrl = photoUrl,
    phone = phone
)

fun BookmarkedUserEntity.toDomain() = User(
    id = id,
    fullName = fullName,
    email = email,
    country = country,
    city = city,
    avatarUrl = avatarUrl,
    photoUrl = photoUrl,
    phone = phone
)