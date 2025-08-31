package daniluk.randopedia.data.local

import daniluk.randopedia.data.local.database.BookmarkedUserDao
import daniluk.randopedia.data.local.entity.BookmarkedUserEntity
import daniluk.randopedia.data.remote.model.toDomain
import daniluk.randopedia.data.remote.model.toEntity
import daniluk.randopedia.domain.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class BookmarksLocalDataSource @Inject constructor(
    private val dao: BookmarkedUserDao
) {

    suspend fun addBookmark(user: User) {
        dao.insertBookmarkedUser(user.toEntity())
    }

    fun bookmarkedUserIds(): Flow<Set<String>> =
        dao.getBookmarkedIds().map { it.toSet() }

    suspend fun removeBookmarkById(id: String) {
        dao.deleteBookmarkById(id)
    }

    fun bookmarkedUsers(): Flow<List<User>> =
        dao.getBookmarkedUsers().map { list ->
            list.map(BookmarkedUserEntity::toDomain)
        }
}