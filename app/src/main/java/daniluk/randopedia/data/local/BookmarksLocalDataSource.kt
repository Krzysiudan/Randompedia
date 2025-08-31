package daniluk.randopedia.data.local

import daniluk.randopedia.data.local.database.BookmarkedUserDao
import daniluk.randopedia.data.local.entity.BookmarkedUserEntity
import daniluk.randopedia.data.remote.model.toDomain
import daniluk.randopedia.data.remote.model.toEntity
import daniluk.randopedia.domain.AppError
import daniluk.randopedia.domain.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class BookmarksLocalDataSource @Inject constructor(
    private val dao: BookmarkedUserDao
) {

    suspend fun addBookmark(user: User) =
        runCatching { dao.insertBookmarkedUser(user.toEntity()) }
            .getOrElse { throw AppError.Storage(it) }

    fun bookmarkedUserIds(): Flow<Set<String>> =
        dao.getBookmarkedIds()
            .map { it.toSet() }
            .catch { t -> throw AppError.Storage(t) }

    suspend fun removeBookmarkById(id: String) =
        runCatching { dao.deleteBookmarkById(id) }
            .getOrElse { throw AppError.Storage(it) }

    fun bookmarkedUsers(): Flow<List<User>> =
        dao.getBookmarkedUsers()
            .map { list -> list.map(BookmarkedUserEntity::toDomain) }
            .catch { t -> throw AppError.Storage(t) }
}