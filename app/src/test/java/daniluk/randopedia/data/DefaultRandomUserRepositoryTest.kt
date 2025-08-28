

package daniluk.randopedia.data

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import daniluk.randopedia.data.local.database.RandomUser
import daniluk.randopedia.data.local.database.RandomUserDao

/**
 * Unit tests for [DefaultRandomUserRepository].
 */
@OptIn(ExperimentalCoroutinesApi::class) // TODO: Remove when stable
class DefaultRandomUserRepositoryTest {

    @Test
    fun randomUsers_newItemSaved_itemIsReturned() = runTest {
        val repository = DefaultRandomUserRepository(FakeRandomUserDao())

        repository.add("Repository")

        assertEquals(repository.randomUsers.first().size, 1)
    }

}

private class FakeRandomUserDao : RandomUserDao {

    private val data = mutableListOf<RandomUser>()

    override fun getRandomUsers(): Flow<List<RandomUser>> = flow {
        emit(data)
    }

    override suspend fun insertRandomUser(item: RandomUser) {
        data.add(0, item)
    }
}
