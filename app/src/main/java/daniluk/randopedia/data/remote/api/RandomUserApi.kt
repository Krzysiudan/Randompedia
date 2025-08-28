package daniluk.randopedia.data.remote.api

import daniluk.randopedia.data.remote.model.RandomUserResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface RandomUserApi {
    // Docs: https://randomuser.me/documentation#howto
    // Example: https://randomuser.me/api/?results=10
    @GET("api/")
    suspend fun getRandomUsers(
        @Query("results") results: Int = 10,
        // Request only basic fields by default to save bandwidth/CPU (gender, name, nat)
        @Query("inc") inc: String = "gender,name,nat",
        // Simple pagination support per docs (1-based index)
        @Query("page") page: Int = 1,
        // Optional seed to keep pages deterministic if desired
        @Query("seed") seed: String? = null
    ): RandomUserResponse
}
