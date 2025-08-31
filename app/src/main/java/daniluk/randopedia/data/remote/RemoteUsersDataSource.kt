package daniluk.randopedia.data.remote

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import daniluk.randopedia.data.remote.api.RandomUserApi
import daniluk.randopedia.data.remote.paging.PagingUsersSource
import daniluk.randopedia.domain.model.User
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RemoteUsersDataSource @Inject constructor(
    private val randomUserApi: RandomUserApi
) {

    fun userPagingSource(): Flow<PagingData<User>> = Pager(
        config = PagingConfig(pageSize = 25, enablePlaceholders = false),
        pagingSourceFactory = { PagingUsersSource(api = randomUserApi) }
    ).flow
}