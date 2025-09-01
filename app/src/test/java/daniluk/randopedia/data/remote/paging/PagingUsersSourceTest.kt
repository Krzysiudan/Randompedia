package daniluk.randopedia.data.remote.paging

import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import daniluk.randopedia.data.remote.api.RandomUserApi
import daniluk.randopedia.data.remote.model.InfoDto
import daniluk.randopedia.data.remote.model.LocationDto
import daniluk.randopedia.data.remote.model.LoginDto
import daniluk.randopedia.data.remote.model.NameDto
import daniluk.randopedia.data.remote.model.PictureDto
import daniluk.randopedia.data.remote.model.RandomUserResponse
import daniluk.randopedia.data.remote.model.UserDto
import daniluk.randopedia.domain.model.User
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PagingUsersSourceTest {

    private fun dto(uuid: String, first: String = "John", last: String = "Doe") = UserDto(
        gender = "male",
        name = NameDto(first = first, last = last),
        location = LocationDto(city = "NYC", country = "USA"),
        email = "john@example.com",
        login = LoginDto(uuid = uuid),
        phone = "123",
        picture = PictureDto(large = "L", medium = "M", thumbnail = "T"),
        nat = "US"
    )

    @Test
    fun load_success_firstPage_hasNextKey() = runTest {
        val api = mockk<RandomUserApi>()
        val response = RandomUserResponse(results = listOf(dto("id1"), dto("id2")), info = InfoDto(page = 1))
        coEvery { api.getUsers(page = 1, results = 25) } returns response

        val source = PagingUsersSource(api)
        val result = source.load(PagingSource.LoadParams.Refresh(key = null, loadSize = 25, placeholdersEnabled = false))

        assertTrue(result is PagingSource.LoadResult.Page)
        val page = result as PagingSource.LoadResult.Page
        assertEquals(null, page.prevKey)
        assertEquals(2, page.data.size)
        assertEquals("id1", page.data[0].id)
        assertEquals(2, page.nextKey)
    }

    @Test
    fun load_success_emptyResults_hasNoNextKey() = runTest {
        val api = mockk<RandomUserApi>()
        coEvery { api.getUsers(page = 2, results = 25) } returns RandomUserResponse(results = emptyList(), info = InfoDto(page = 2))

        val source = PagingUsersSource(api)
        val result = source.load(PagingSource.LoadParams.Append(key = 2, loadSize = 25, placeholdersEnabled = false))
        val page = result as PagingSource.LoadResult.Page
        assertEquals(1, page.prevKey)
        assertNull(page.nextKey)
        assertTrue(page.data.isEmpty())
    }

    @Test
    fun load_error_isMappedToError() = runTest {
        val api = mockk<RandomUserApi>()
        coEvery { api.getUsers(any(), any()) } throws IllegalStateException("network")

        val source = PagingUsersSource(api)
        val result = source.load(PagingSource.LoadParams.Refresh(key = null, loadSize = 25, placeholdersEnabled = false))
        assertTrue(result is PagingSource.LoadResult.Error)
    }

    @Test
    fun getRefreshKey_computesFromClosestPage() {
        val source = PagingUsersSource(mockk(relaxed = true))
        val users = listOf(
            User(id = "1", fullName = "A B", email = "a@b", country = "C", city = "D", avatarUrl = "t", photoUrl = "l", phone = "p"),
            User(id = "2", fullName = "A B", email = "a@b", country = "C", city = "D", avatarUrl = "t", photoUrl = "l", phone = "p")
        )
        val pages = listOf(
            PagingSource.LoadResult.Page(data = users, prevKey = null, nextKey = 2)
        )
        val state = PagingState(
            pages = pages,
            anchorPosition = 1, // within the first page
            config = PagingConfig(pageSize = 25),
            leadingPlaceholderCount = 0
        )
        val key = source.getRefreshKey(state)
        assertEquals(1, key)
    }

    @Test
    fun load_prepend_success_updatesKeys() = runTest {
        val api = mockk<RandomUserApi>()
        coEvery { api.getUsers(page = 1, results = 25) } returns
                RandomUserResponse(results = listOf(dto("a"), dto("b")), info = InfoDto(page = 1))

        val source = PagingUsersSource(api)
        val result = source.load(PagingSource.LoadParams.Prepend(key = 1, loadSize = 25, placeholdersEnabled = false))
        result as PagingSource.LoadResult.Page
        assertNull(result.prevKey)
        assertEquals(2, result.nextKey) // or whatever your policy is for prepend
        coVerify { api.getUsers(page = 1, results = 25) }
    }
}
