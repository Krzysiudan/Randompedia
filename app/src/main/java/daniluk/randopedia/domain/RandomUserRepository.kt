package daniluk.randopedia.domain

import androidx.paging.Pager
import androidx.paging.PagingData
import daniluk.randopedia.domain.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

interface RandomUserRepository {
    suspend fun addBookmark(user: User)
    suspend fun removeBookmarkById(id: String)
    fun userPagingFlow(): Flow<PagingData<User>>
    fun bookmarkedUserIds(): Flow<Set<String>> = flowOf(emptySet())

    fun bookmarkedUsers(): Flow<List<User>> = flowOf(emptyList())
}
