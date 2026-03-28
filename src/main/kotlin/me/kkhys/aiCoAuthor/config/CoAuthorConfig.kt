package me.kkhys.aiCoAuthor.config

/**
 * Configuration for AI Co-Author functionality
 *
 * @description Centralizes configuration values for the AI Co-Author plugin,
 *              providing default values and future extensibility for user customization
 * @since 1.0.0
 */
object CoAuthorConfig {
    /**
     * Default co-author name
     */
    const val DEFAULT_COAUTHOR_NAME = "Claude"

    /**
     * Default co-author email
     */
    const val DEFAULT_COAUTHOR_EMAIL = "noreply@anthropic.com"

    /**
     * Standard Git co-authored-by trailer format
     */
    fun getCoAuthoredByTrailer(
        name: String = DEFAULT_COAUTHOR_NAME,
        email: String = DEFAULT_COAUTHOR_EMAIL,
    ): String = "Co-Authored-By: $name <$email>"

    /**
     * Maximum commit message length for heuristic detection
     */
    const val MAX_COMMIT_MESSAGE_LENGTH = 1000

    /**
     * Minimum textarea rows for commit message detection
     */
    const val MIN_TEXTAREA_ROWS = 1

    /**
     * Notification group ID for plugin notifications
     */
    const val NOTIFICATION_GROUP_ID = "AI Co-Author"
}
