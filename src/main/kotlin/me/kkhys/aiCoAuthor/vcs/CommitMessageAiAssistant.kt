package me.kkhys.aiCoAuthor.vcs

import com.intellij.openapi.vcs.checkin.CheckinHandler
import com.intellij.openapi.vcs.checkin.CheckinHandlerFactory

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
        commitContext: com.intellij.openapi.vcs.changes.CommitContext,
    ): CheckinHandler {
        return AiAssistantCheckinHandler()
    }

    /**
     * Simple CheckinHandler - the actual UI button is provided by GenerateCommitMessageAction
     */
    private class AiAssistantCheckinHandler : CheckinHandler() {
        // This handler ensures AI Co-Author is available in the VCS workflow
        // The visible button is implemented as a VCS action in GenerateCommitMessageAction
    }
}
