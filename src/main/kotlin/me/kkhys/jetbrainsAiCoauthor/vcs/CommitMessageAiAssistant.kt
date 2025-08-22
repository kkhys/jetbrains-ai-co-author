package me.kkhys.jetbrainsAiCoauthor.vcs

import com.intellij.openapi.vcs.checkin.CheckinHandler
import com.intellij.openapi.vcs.checkin.CheckinHandlerFactory
import com.intellij.openapi.ui.Messages

/**
 * CheckinHandlerFactory that provides AI-assisted commit message generation functionality
 * 
 * @description Adds AI assistant features to the commit dialog and provides
 *              functionality to automatically generate commit messages based on changes
 * @since 1.0.0
 */
class CommitMessageAiAssistant : CheckinHandlerFactory() {
    
    /**
     * Creates a CheckinHandler for commit processing
     * 
     * @description Creates a CheckinHandler with AI assistant functionality
     * @param panel The commit dialog panel
     * @param commitContext Commit context information
     * @return CheckinHandler with AI assistant features
     */
    override fun createHandler(
        panel: com.intellij.openapi.vcs.CheckinProjectPanel, 
        commitContext: com.intellij.openapi.vcs.changes.CommitContext
    ): CheckinHandler {
        return AiAssistantCheckinHandler()
    }
    
    /**
     * CheckinHandler implementation with AI assistance features
     */
    private class AiAssistantCheckinHandler : CheckinHandler() {
        
        /**
         * Pre-commit processing - demonstration purposes
         * 
         * @description Currently displays a demo message about AI functionality during commits
         *              Future plans include implementing more advanced AI assistance features
         * @return Whether to continue or cancel the commit
         */
        override fun beforeCheckin(): ReturnResult {
            // Demo: Notify user about AI Co-Author availability
            val response = Messages.showYesNoDialog(
                "AI Co-Author: Commit message generation feature is available.\n\n" +
                "In the future, you will be able to auto-generate commit messages with one click.\n\n" +
                "Do you want to continue with this commit?",
                "AI Co-Author",
                Messages.getQuestionIcon()
            )
            
            return if (response == Messages.YES) {
                ReturnResult.COMMIT
            } else {
                ReturnResult.CANCEL
            }
        }
    }
}