package daniluk.randopedia.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RandomUserResponse(
    @SerialName("results") val results: List<Result> = emptyList(),
    @SerialName("info") val info: Info? = null
) {
    @Serializable
    data class Result(
        @SerialName("gender") val gender: String? = null,
        @SerialName("name") val name: Name? = null,
        @SerialName("nat") val nat: String? = null
    )

    @Serializable
    data class Name(
        @SerialName("title") val title: String? = null,
        @SerialName("first") val first: String? = null,
        @SerialName("last") val last: String? = null
    )

    @Serializable
    data class Info(
        @SerialName("seed") val seed: String? = null,
        @SerialName("results") val results: Int? = null,
        @SerialName("page") val page: Int? = null,
        @SerialName("version") val version: String? = null
    )
}
