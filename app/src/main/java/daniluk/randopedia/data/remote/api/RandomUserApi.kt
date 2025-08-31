package daniluk.randopedia.data.remote.api

import daniluk.randopedia.data.remote.model.RandomUserResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface RandomUserApi {
    // Docs: https://randomuser.me/documentation#howto
    // Example: https://randomuser.me/api/?results=10
    @GET("api/")
    suspend fun getUsers(
        @Query("page") page: Int,
        @Query("results") results: Int = 25,
        @Query("seed") seed: String = "randopedia", // keep pagination stable
        @Query("inc") inc: String = "name,location,email,login,phone,cell,picture,nat" // trim payload
    ): RandomUserResponse
}
