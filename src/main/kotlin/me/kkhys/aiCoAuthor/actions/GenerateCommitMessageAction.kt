package me.kkhys.aiCoAuthor.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.vcs.changes.ChangeListManager
import me.kkhys.aiCoAuthor.services.AICoAuthorService
import me.kkhys.aiCoAuthor.services.NotificationService

/**
 * Action to add AI co-author to commit messages
 *
 * @description Provides a button in the VCS commit dialog to add Claude as a
 *              co-author using the standard Git co-authored-by trailer format.
 *              This refactored version delegates business logic to service classes
 *              for better separation of concerns and testability.
 * @since 1.0.0
 */
class GenerateCommitMessageAction :
    AnAction(
        "Add AI Co-Author",
        "Add Claude as co-author to your commit message",
        null,
    ) {
    private val aiCoAuthorService = AICoAuthorService()

    /**
     * Called when the action is performed (button clicked)
     *
     * @param e The action event containing project context
     */
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return

        ApplicationManager.getApplication().invokeLater {
            aiCoAuthorService.addAiCoAuthor(
                onSuccess = {
                    NotificationService.showSuccessNotification(project)
                },
                onFailure = { coAuthoredBy ->
                    NotificationService.showWarningNotification(project, coAuthoredBy)
                },
                onError = { errorMessage ->
                    NotificationService.showErrorNotification(project, errorMessage)
                },
            )
        }
    }

    /**
     * Controls when the action is available in the UI
     *
     * @param e The action event for context evaluation
     */
    override fun update(e: AnActionEvent) {
        val project = e.project
        val presentation = e.presentation

        // Enable when we have a project with VCS enabled
        presentation.isEnabledAndVisible = project != null &&
            ChangeListManager.getInstance(project).areChangeListsEnabled()
    }
}
