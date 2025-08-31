package daniluk.randopedia.data

import androidx.paging.PagingData
import daniluk.randopedia.data.local.BookmarksLocalDataSource
import daniluk.randopedia.data.remote.RemoteUsersDataSource
import daniluk.randopedia.domain.RandomUserRepository
import daniluk.randopedia.domain.model.User
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RandomUserRepositoryImpl @Inject constructor(
    private val bookmarksLocalDataSource: BookmarksLocalDataSource,
    private val remoteUsersDataSource: RemoteUsersDataSource
) : RandomUserRepository {

    override fun userPagingFlow(): Flow<PagingData<User>> = remoteUsersDataSource.userPagingSource()

    override suspend fun addBookmark(user: User) {
        bookmarksLocalDataSource.addBookmark(user)
    }

    override fun bookmarkedUserIds(): Flow<Set<String>> =
        bookmarksLocalDataSource.bookmarkedUserIds()

    override suspend fun removeBookmarkById(id: String) {
        bookmarksLocalDataSource.removeBookmarkById(id)
    }

    override fun bookmarkedUsers(): Flow<List<User>> =
        bookmarksLocalDataSource.bookmarkedUsers()
}
