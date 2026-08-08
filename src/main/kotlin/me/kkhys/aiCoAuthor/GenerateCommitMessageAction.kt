package me.kkhys.aiCoAuthor

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.vcs.VcsDataKeys

const val COAUTHORED_BY_TRAILER = "Co-Authored-By: Claude <noreply@anthropic.com>"

class GenerateCommitMessageAction : AnAction() {
    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val commitMessageControl = e.getData(VcsDataKeys.COMMIT_MESSAGE_CONTROL)
        val document = e.getData(VcsDataKeys.COMMIT_MESSAGE_DOCUMENT)

        if (commitMessageControl == null || document == null) {
            CommitNotifications.warnManualAdditionRequired(project)
            return
        }

        if (document.text.contains(COAUTHORED_BY_TRAILER)) {
            return
        }

        commitMessageControl.setCommitMessage(appendTrailer(document.text))
        CommitNotifications.notifyTrailerAdded(project)
    }

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabledAndVisible = e.project != null
    }

    companion object {
        fun appendTrailer(commitMessage: String): String {
            val trimmed = commitMessage.trim()
            return if (trimmed.isEmpty()) COAUTHORED_BY_TRAILER else "$trimmed\n\n$COAUTHORED_BY_TRAILER"
        }
    }
}
