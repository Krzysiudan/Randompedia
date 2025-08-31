package daniluk.randopedia.domain

/**
 * Small app-level errors we surface from the data layer.
 * Keep it tiny and user-friendly.
 */
sealed class AppError(message: String? = null, cause: Throwable? = null) : Exception(message, cause) {
    /** Network-related issue (no internet, timeout, DNS, etc.) */
    class Network(cause: Throwable? = null) : AppError(cause = cause)

    /** Storage/DB related issue (Room, cursor, migrations, disk full, etc.) */
    class Storage(cause: Throwable? = null) : AppError(cause = cause)

    /** Anything unknown/unexpected */
    class Unknown(cause: Throwable? = null) : AppError(cause = cause)
}

fun AppError.userMessage(): String = when (this) {
    is AppError.Network -> "Network error. Check your connection."
    is AppError.Storage -> "Storage error. Please try again."
    is AppError.Unknown -> "Something went wrong. Please try again."
}

/** Map any Throwable to an AppError variant */
fun Throwable.toAppError(): AppError = when (this) {
    is AppError -> this
    is java.io.IOException -> AppError.Network(this)
    is android.database.SQLException, is android.database.sqlite.SQLiteException -> AppError.Storage(this)
    else -> AppError.Unknown(this)
}
