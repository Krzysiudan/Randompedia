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
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

interface RandomUserRepository {

    fun pager(): Pager<Int, User>

    // Persist/bookmark operations
    suspend fun add(user: User)

    // Bookmarks overlays/flows (defaults keep binary compatibility for tests)
    fun bookmarkedIds(): Flow<Set<String>> = flowOf(emptySet())
    suspend fun removeById(id: String)

    // Full list of bookmarked users
    fun bookmarkedUsers(): Flow<List<User>> = flowOf(emptyList())
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
                photoUrl = user.photoUrl,
                phone = user.phone
            )
        )
    }

    override fun bookmarkedIds(): Flow<Set<String>> =
        randomUserDao.getBookmarkedIds().map { it.toSet() }

    override suspend fun removeById(id: String) {
        randomUserDao.deleteById(id)
    }

    override fun bookmarkedUsers(): Flow<List<User>> =
        randomUserDao.getRandomUsers().map { list ->
            list.map { e ->
                User(
                    id = e.id,
                    fullName = e.fullName,
                    email = e.email,
                    country = e.country,
                    city = e.city,
                    age = e.age,
                    avatarUrl = e.avatarUrl,
                    photoUrl = e.photoUrl,
                    phone = e.phone
                )
            }
        }
}
