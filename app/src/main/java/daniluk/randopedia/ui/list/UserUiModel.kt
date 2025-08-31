package daniluk.randopedia.ui.list

import daniluk.randopedia.domain.model.User

/**
 * UI model for the user list that carries bookmark state alongside the network item.
 */
data class UserUiModel(
    val user: User,
    val isBookmarked: Boolean
)
