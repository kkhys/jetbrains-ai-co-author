package me.kkhys.aiCoAuthor.services

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project
import me.kkhys.aiCoAuthor.config.CoAuthorConfig

/**
 * Service for handling plugin notifications
 *
 * @description Centralizes notification logic for the AI Co-Author plugin,
 *              providing consistent messaging and notification management
 * @since 1.0.0
 */
object NotificationService {
    /**
     * Shows a success notification when AI co-author is added
     *
     * @param project The current project context
     */
    fun showSuccessNotification(project: Project) {
        getNotificationGroup()
            .createNotification(
                "AI Co-Author Added",
                "Claude has been added as co-author to your commit message.",
                NotificationType.INFORMATION,
            ).notify(project)
    }

    /**
     * Shows a warning notification when AI co-author cannot be added automatically
     *
     * @param project The current project context
     * @param coAuthoredBy The co-authored-by trailer that should be added manually
     */
    fun showWarningNotification(
        project: Project,
        coAuthoredBy: String,
    ) {
        getNotificationGroup()
            .createNotification(
                "Could not add AI Co-Author automatically",
                "Please manually add: $coAuthoredBy to your commit message.",
                NotificationType.WARNING,
            ).notify(project)
    }

    /**
     * Shows an error notification for unexpected errors
     *
     * @param project The current project context
     * @param error The error message to display
     */
    fun showErrorNotification(
        project: Project,
        error: String,
    ) {
        getNotificationGroup()
            .createNotification(
                "AI Co-Author Error",
                "An error occurred: $error",
                NotificationType.ERROR,
            ).notify(project)
    }

    /**
     * Gets the notification group for this plugin
     *
     * @return The notification group instance
     */
    private fun getNotificationGroup() =
        NotificationGroupManager
            .getInstance()
            .getNotificationGroup(CoAuthorConfig.NOTIFICATION_GROUP_ID)
}
