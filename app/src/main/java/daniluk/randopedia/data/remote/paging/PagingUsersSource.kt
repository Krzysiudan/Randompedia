package daniluk.randopedia.data.remote.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import daniluk.randopedia.data.remote.api.RandomUserApi
import daniluk.randopedia.data.remote.model.toDomain
import daniluk.randopedia.domain.model.User

class PagingUsersSource(
    private val api: RandomUserApi
) : PagingSource<Int, User>() {

    override fun getRefreshKey(state: PagingState<Int, User>): Int? =
        state.anchorPosition?.let { pos ->
            val page = state.closestPageToPosition(pos)
            page?.prevKey?.plus(1) ?: page?.nextKey?.minus(1)
        }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, User> {
        val page = params.key ?: 1
        return try {
            val resp = api.getUsers(page = page, results = 25)
            val data = resp.results.map { it.toDomain() }
            LoadResult.Page(
                data = data,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (data.isEmpty()) null else page + 1
            )
        } catch (t: Throwable) {
            LoadResult.Error(t)
        }
    }
}