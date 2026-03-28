package me.kkhys.aiCoAuthor.config

object CoAuthorConfig {
    const val DEFAULT_COAUTHOR_NAME = "Claude"
    const val DEFAULT_COAUTHOR_EMAIL = "noreply@anthropic.com"
    const val NOTIFICATION_GROUP_ID = "AI Co-Author"

    fun getCoAuthoredByTrailer(
        name: String = DEFAULT_COAUTHOR_NAME,
        email: String = DEFAULT_COAUTHOR_EMAIL,
    ): String = "Co-Authored-By: $name <$email>"
}
