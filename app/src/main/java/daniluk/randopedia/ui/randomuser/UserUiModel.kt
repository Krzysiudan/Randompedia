package daniluk.randopedia.ui.randomuser

import daniluk.randopedia.data.model.User

/**
 * UI model for the user list that carries bookmark state alongside the network item.
 */
data class UserUiModel(
    val user: User,
    val isBookmarked: Boolean
)
