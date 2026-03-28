package me.kkhys.aiCoAuthor.services

import me.kkhys.aiCoAuthor.config.CoAuthorConfig
import javax.swing.SwingUtilities

/**
 * Core service for AI Co-Author functionality
 *
 * @description Provides the main business logic for adding AI co-author trailers
 *              to commit messages, coordinating between UI components and messaging
 * @since 1.0.0
 */
class AICoAuthorService {
    private val fieldFinder = CommitMessageFieldFinder()

    /**
     * Attempts to add AI co-author to the commit message
     *
     * @param onSuccess Callback to execute when co-author is successfully added
     * @param onFailure Callback to execute when co-author addition fails, receives the trailer text
     * @param onError Callback to execute when an unexpected error occurs, receives error message
     */
    fun addAiCoAuthor(
        onSuccess: () -> Unit = {},
        onFailure: (String) -> Unit = {},
        onError: (String) -> Unit = {},
    ) {
        try {
            val coAuthoredBy = CoAuthorConfig.getCoAuthoredByTrailer()
            val commitField = fieldFinder.findCommitMessageField()

            if (commitField == null) {
                onFailure(coAuthoredBy)
                return
            }

            val currentText = fieldFinder.getTextFromComponent(commitField)

            if (currentText.contains("Co-Authored-By: Claude")) {
                onSuccess() // Already contains co-author, treat as success
                return
            }

            val updatedText = buildUpdatedCommitMessage(currentText, coAuthoredBy)

            SwingUtilities.invokeLater {
                val success = fieldFinder.setTextInComponent(commitField, updatedText)
                if (success) {
                    onSuccess()
                } else {
                    onFailure(coAuthoredBy)
                }
            }
        } catch (e: Exception) {
            onError("Unexpected error: ${e.message}")
        }
    }

    /**
     * Builds the updated commit message with co-author trailer
     *
     * @param currentText The current commit message text
     * @param coAuthoredBy The co-authored-by trailer to add
     * @return The updated commit message with proper formatting
     */
    private fun buildUpdatedCommitMessage(
        currentText: String,
        coAuthoredBy: String,
    ): String =
        if (currentText.trim().isEmpty()) {
            coAuthoredBy
        } else {
            "${currentText.trim()}\n\n$coAuthoredBy"
        }

    /**
     * Checks if a commit message already contains an AI co-author
     *
     * @param commitMessage The commit message to check
     * @return true if the message already contains a Claude co-author
     */
    fun hasAiCoAuthor(commitMessage: String): Boolean = commitMessage.contains("Co-Authored-By: Claude", ignoreCase = true)

    /**
     * Gets the standard AI co-author trailer
     *
     * @return The formatted co-authored-by trailer string
     */
    fun getCoAuthorTrailer(): String = CoAuthorConfig.getCoAuthoredByTrailer()
}
