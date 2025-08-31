

package daniluk.randopedia.ui.randomuser


import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.runCurrent
import org.junit.Assert.assertEquals
import org.junit.Test
import daniluk.randopedia.data.RandomUserRepository

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@OptIn(ExperimentalCoroutinesApi::class) // TODO: Remove when stable
class RandomUserViewModelTest {
    @Test
    fun onBookmarkClicked_insertsWithoutError() = runTest {
        val repo = FakeRandomUserRepository()
        val viewModel = UsersListViewModel(repo)
        viewModel.onBookmarkClicked(
            daniluk.randopedia.data.model.User(
                id = "1",
                fullName = "John Doe",
                email = "john@example.com",
                country = "USA",
                city = "NYC",
                age = 30,
                avatarUrl = "",
                photoUrl = ""
            )
        )
        runCurrent()
        assertEquals(1, repo.savedUsers.size)
    }
}

private class FakeRandomUserRepository : RandomUserRepository {

    val savedUsers = mutableListOf<daniluk.randopedia.data.model.User>()

    override fun pager(): androidx.paging.Pager<Int, daniluk.randopedia.data.model.User> =
        androidx.paging.Pager(config = androidx.paging.PagingConfig(pageSize = 1)) { error("not used") }

    override suspend fun add(user: daniluk.randopedia.data.model.User) {
        savedUsers.add(user)
    }

    override fun bookmarkedIds(): kotlinx.coroutines.flow.Flow<Set<String>> = kotlinx.coroutines.flow.flowOf(savedUsers.map { it.id }.toSet())

    override suspend fun removeById(id: String) {
        savedUsers.removeAll { it.id == id }
    }

    override fun bookmarkedUsers(): kotlinx.coroutines.flow.Flow<List<daniluk.randopedia.data.model.User>> = kotlinx.coroutines.flow.flowOf(savedUsers.toList())
}
