

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

//    @Test
//    fun repository_add_insertsIntoDao() = runTest {
//        val dao = FakeRandomUserDao()
//        val repository = DefaultRandomUserRepository(dao, FakeRandomUserApi)
//
//        repository.add("Repository")
//
//        assertEquals(1, dao.data.size)
//        assertEquals("Repository", dao.data.first().fullName)
//    }

}

private object FakeRandomUserApi : daniluk.randopedia.data.remote.api.RandomUserApi {
    override suspend fun getUsers(
        page: Int,
        results: Int,
        seed: String,
        inc: String
    ): daniluk.randopedia.data.remote.model.RandomUserResponse {
        return daniluk.randopedia.data.remote.model.RandomUserResponse(
            results = emptyList(),
            info = daniluk.randopedia.data.remote.model.InfoDto()
        )
    }
}

private class FakeRandomUserDao : RandomUserDao {

    val data = mutableListOf<RandomUser>()

    override fun getRandomUsers(): Flow<List<RandomUser>> = flow {
        emit(data)
    }

    override fun getAllBookmarks(): Flow<List<RandomUser>> = flow {
        emit(data)
    }

    override fun getBookmarkedIds(): Flow<List<String>> = flow {
        emit(data.map { it.id })
    }

    override suspend fun insertRandomUser(item: RandomUser) {
        // Simulate REPLACE on unique id
        val idx = data.indexOfFirst { it.id == item.id }
        if (idx >= 0) data.removeAt(idx)
        data.add(0, item)
    }

    override suspend fun deleteById(id: String) {
        val idx = data.indexOfFirst { it.id == id }
        if (idx >= 0) data.removeAt(idx)
    }
}
