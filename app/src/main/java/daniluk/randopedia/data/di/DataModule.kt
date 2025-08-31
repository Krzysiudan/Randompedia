package daniluk.randopedia.data.di

import androidx.paging.Pager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import daniluk.randopedia.data.RandomUserRepository
import daniluk.randopedia.data.DefaultRandomUserRepository
import daniluk.randopedia.data.model.User
import javax.inject.Inject
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {

    @Singleton
    @Binds
    fun bindsRandomUserRepository(
        randomUserRepository: DefaultRandomUserRepository
    ): RandomUserRepository
}

class FakeRandomUserRepository @Inject constructor() : RandomUserRepository {

    override fun pager(): Pager<Int, User> {
        TODO("Not yet implemented")
    }

    override suspend fun add(user: User) {
        throw NotImplementedError()
    }

    override fun bookmarkedIds(): Flow<Set<String>> = flowOf(emptySet())

    override suspend fun removeById(id: String) { /* no-op */ }

    override fun bookmarkedUsers(): Flow<List<User>> = flowOf(emptyList())
}

val fakeRandomUsers = listOf("One", "Two", "Three")
