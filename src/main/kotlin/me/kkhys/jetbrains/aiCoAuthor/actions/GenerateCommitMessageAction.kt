package me.kkhys.jetbrains.aiCoAuthor.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.vcs.changes.ChangeListManager
import com.intellij.ui.EditorTextField
import java.awt.Component
import java.awt.Container
import javax.swing.*

/**
 * Action to add AI co-author to commit messages
 * 
 * @description Provides a button in the VCS commit dialog to add Claude as a
 *              co-author using the standard Git co-authored-by trailer format
 * @since 1.0.0
 */
class GenerateCommitMessageAction : AnAction("🤖 Add AI Co-Author", 
    "Add Claude as co-author to your commit message", 
    null) {
    
    /**
     * Called when the action is performed (button clicked)
     */
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val coAuthoredBy = "Co-Authored-By: Claude <noreply@anthropic.com>"
        
        ApplicationManager.getApplication().invokeLater {
            val success = tryComponentTreeSearch(coAuthoredBy)
            
            // Show notification based on success
            val notificationGroup = com.intellij.notification.NotificationGroupManager.getInstance()
                .getNotificationGroup("AI Co-Author")
            
            if (success) {
                notificationGroup.createNotification(
                    "AI Co-Author Added",
                    "Claude has been added as co-author to your commit message.",
                    com.intellij.notification.NotificationType.INFORMATION
                ).notify(project)
            } else {
                notificationGroup.createNotification(
                    "Could not add AI Co-Author automatically",
                    "Please manually add: $coAuthoredBy to your commit message.",
                    com.intellij.notification.NotificationType.WARNING
                ).notify(project)
            }
        }
    }
    
    /**
     * Search all top-level windows for the commit message field
     */
    private fun tryComponentTreeSearch(coAuthoredBy: String): Boolean {
        return try {
            val frames = JFrame.getFrames()
            for (frame in frames) {
                if (searchComponentTree(frame, coAuthoredBy)) {
                    return true
                }
            }
            false
        } catch (_: Exception) {
            false
        }
    }
    
    /**
     * Recursively search component tree for commit message fields
     */
    private fun searchComponentTree(component: Component, coAuthoredBy: String): Boolean {
        when (component) {
            is EditorTextField -> {
                // Check if this looks like a commit message field
                if (isLikelyCommitMessageField(component)) {
                    updateTextComponent(component, coAuthoredBy)
                    return true
                }
            }
            is JTextArea -> {
                // Check if this looks like a commit message field
                if (isLikelyCommitMessageField(component)) {
                    updateTextComponent(component, coAuthoredBy)
                    return true
                }
            }
            is Container -> {
                // Recursively search children
                for (child in component.components) {
                    if (searchComponentTree(child, coAuthoredBy)) {
                        return true
                    }
                }
            }
        }
        return false
    }
    
    /**
     * Check if a component is likely a commit message field
     */
    private fun isLikelyCommitMessageField(component: Component): Boolean {
        return when (component) {
            is EditorTextField -> {
                val text = component.text
                // Look for commit-related hints or multi-line capability
                component.isVisible && (text.isEmpty() || text.length < 1000)
            }
            is JTextArea -> {
                val text = component.text ?: ""
                // Multi-line text area that's editable and visible
                component.isVisible && component.isEditable && component.rows > 1 && 
                (text.isEmpty() || text.length < 1000)
            }
            else -> false
        }
    }
    
    /**
     * Update text component with co-author
     */
    private fun updateTextComponent(component: Any, coAuthoredBy: String) {
        val currentText = when (component) {
            is EditorTextField -> component.text
            is JTextArea -> component.text ?: ""
            else -> return
        }
        
        if (!currentText.contains("Co-Authored-By: Claude")) {
            val updatedText = if (currentText.trim().isEmpty()) {
                coAuthoredBy
            } else {
                "${currentText.trim()}\n\n$coAuthoredBy"
            }
            
            SwingUtilities.invokeLater {
                when (component) {
                    is EditorTextField -> component.text = updatedText
                    is JTextArea -> component.text = updatedText
                }
            }
        }
    }
    
    /**
     * Controls when the action is available
     */
    override fun update(e: AnActionEvent) {
        val project = e.project
        val presentation = e.presentation
        
        // Enable when we have a project with VCS enabled
        presentation.isEnabledAndVisible = project != null && 
            ChangeListManager.getInstance(project).areChangeListsEnabled()
    }
}
