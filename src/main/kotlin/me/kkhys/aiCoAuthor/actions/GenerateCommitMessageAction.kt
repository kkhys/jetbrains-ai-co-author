package me.kkhys.aiCoAuthor.actions

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.vcs.VcsDataKeys
import me.kkhys.aiCoAuthor.config.CoAuthorConfig
import me.kkhys.aiCoAuthor.services.NotificationService

class GenerateCommitMessageAction : AnAction() {
    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val trailer = CoAuthorConfig.getCoAuthoredByTrailer()

        val commitMessageControl = e.getData(VcsDataKeys.COMMIT_MESSAGE_CONTROL)
        val document = e.getData(VcsDataKeys.COMMIT_MESSAGE_DOCUMENT)

        if (commitMessageControl == null || document == null) {
            NotificationService.showWarningNotification(project, trailer)
            return
        }

        val currentText = document.text

        if (currentText.contains(trailer)) {
            return
        }

        commitMessageControl.setCommitMessage(buildCommitMessage(currentText, trailer))
        NotificationService.showSuccessNotification(project)
    }

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabledAndVisible = e.project != null
    }

    companion object {
        fun buildCommitMessage(
            currentText: String,
            trailer: String,
        ): String =
            if (currentText.trim().isEmpty()) {
                trailer
            } else {
                "${currentText.trim()}\n\n$trailer"
            }
    }
}
