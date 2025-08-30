

package daniluk.randopedia.ui.randomuser


import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
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
    fun onUserClicked_insertsWithoutError() = runTest {
        val repo = FakeRandomUserRepository()
        val viewModel = RandomUserViewModel(repo)
        viewModel.onUserClicked(
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
        assertEquals(1, repo.savedUsers.size)
    }
}

private class FakeRandomUserRepository : RandomUserRepository {

    val savedUsers = mutableListOf<daniluk.randopedia.data.model.User>()

    override suspend fun fetchPage(page: Int): List<daniluk.randopedia.data.model.User> = emptyList()

    override fun pager(): androidx.paging.Pager<Int, daniluk.randopedia.data.model.User> =
        androidx.paging.Pager(config = androidx.paging.PagingConfig(pageSize = 1)) { error("not used") }

    override suspend fun add(name: String) {}

    override suspend fun add(user: daniluk.randopedia.data.model.User) {
        savedUsers.add(user)
    }
}
