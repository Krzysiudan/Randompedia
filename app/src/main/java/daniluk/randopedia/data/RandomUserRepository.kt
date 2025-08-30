package daniluk.randopedia.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import daniluk.randopedia.data.local.database.RandomUser
import daniluk.randopedia.data.local.database.RandomUserDao
import daniluk.randopedia.data.model.User
import daniluk.randopedia.data.model.toDomain
import daniluk.randopedia.data.remote.PagingUsersSource
import daniluk.randopedia.data.remote.api.RandomUserApi
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface RandomUserRepository {

    fun pager(): Pager<Int, User>

    // Persist/bookmark operations
    suspend fun add(user: User)

    // Bookmarks overlays/flows (defaults keep binary compatibility for tests)
    fun bookmarkedIds(): Flow<Set<String>> = kotlinx.coroutines.flow.flowOf(emptySet())
    fun bookmarks(): Flow<List<User>> = kotlinx.coroutines.flow.flowOf(emptyList())
    suspend fun removeById(id: String) {}
}

class DefaultRandomUserRepository @Inject constructor(
    private val randomUserDao: RandomUserDao,
    private val randomUserApi: RandomUserApi,
) : RandomUserRepository {

    override fun pager(): Pager<Int, User> = Pager(
        config = PagingConfig(pageSize = 25, enablePlaceholders = false),
        pagingSourceFactory = { PagingUsersSource(api = randomUserApi) }
    )

    override suspend fun add(user: User) {
        randomUserDao.insertRandomUser(
            RandomUser(
                id = user.id,
                fullName = user.fullName,
                email = user.email,
                country = user.country,
                city = user.city,
                age = user.age,
                avatarUrl = user.avatarUrl,
                photoUrl = user.photoUrl
            )
        )
    }

    override fun bookmarkedIds(): Flow<Set<String>> =
        randomUserDao.getBookmarkedIds().map { it.toSet() }

    override fun bookmarks(): Flow<List<User>> =
        randomUserDao.getAllBookmarks().map { list ->
            list.map {
                User(
                    id = it.id,
                    fullName = it.fullName,
                    email = it.email,
                    country = it.country,
                    city = it.city,
                    age = it.age,
                    avatarUrl = it.avatarUrl,
                    photoUrl = it.photoUrl
                )
            }
        }

    override suspend fun removeById(id: String) {
        randomUserDao.deleteById(id)
    }
}
