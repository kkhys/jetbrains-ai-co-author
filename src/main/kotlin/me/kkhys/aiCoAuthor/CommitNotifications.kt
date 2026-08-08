package me.kkhys.aiCoAuthor

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project

object CommitNotifications {
    // Must match the notificationGroup id declared in plugin.xml.
    private const val GROUP_ID = "AI Co-Author"

    fun notifyTrailerAdded(project: Project) {
        notificationGroup()
            .createNotification(
                "AI Co-Author Added",
                "Claude has been added as co-author to your commit message.",
                NotificationType.INFORMATION,
            ).notify(project)
    }

    fun warnManualAdditionRequired(project: Project) {
        notificationGroup()
            .createNotification(
                "Could not add AI Co-Author automatically",
                "Please manually add: $COAUTHORED_BY_TRAILER to your commit message.",
                NotificationType.WARNING,
            ).notify(project)
    }

    private fun notificationGroup() =
        NotificationGroupManager
            .getInstance()
            .getNotificationGroup(GROUP_ID)
}
