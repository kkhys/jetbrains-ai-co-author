package me.kkhys.aiCoAuthor

import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.actionSystem.Presentation
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.vcs.CommitMessageI
import com.intellij.openapi.vcs.VcsDataKeys
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import me.kkhys.aiCoAuthor.actions.GenerateCommitMessageAction
import me.kkhys.aiCoAuthor.config.CoAuthorConfig

class AiCoAuthorPluginTest : BasePlatformTestCase() {
    fun testActionRegistration() {
        val action = ActionManager.getInstance().getAction("AddAiCoAuthor")
        assertNotNull("Action should be registered in ActionManager", action)
        assertTrue(
            "Registered action should be GenerateCommitMessageAction",
            action is GenerateCommitMessageAction,
        )
    }

    fun testActionPresentation() {
        val action = ActionManager.getInstance().getAction("AddAiCoAuthor")!!
        val presentation = action.templatePresentation
        assertEquals("Add AI Co-Author", presentation.text)
        assertEquals("Add Claude as co-author to your commit message", presentation.description)
    }

    fun testActionUpdateThread() {
        val action = GenerateCommitMessageAction()
        assertEquals(ActionUpdateThread.BGT, action.actionUpdateThread)
    }

    fun testUpdateEnabledWithProject() {
        val action = GenerateCommitMessageAction()
        val event = createActionEvent(withProject = true)
        action.update(event)
        assertTrue(event.presentation.isEnabledAndVisible)
    }

    fun testUpdateDisabledWithoutProject() {
        val action = GenerateCommitMessageAction()
        val event = createActionEvent(withProject = false)
        action.update(event)
        assertFalse(event.presentation.isEnabledAndVisible)
    }

    fun testActionPerformedShowsWarningWithoutCommitControl() {
        val action = GenerateCommitMessageAction()
        val event = createActionEvent(withProject = true)
        action.actionPerformed(event)
    }

    fun testActionPerformedAddsTrailer() {
        val action = GenerateCommitMessageAction()
        val document = EditorFactory.getInstance().createDocument("feat: add feature")
        val messages = mutableListOf<String>()
        val event =
            createActionEvent(
                withProject = true,
                commitMessageControl = createCommitMessageControl(messages),
                document = document,
            )
        action.actionPerformed(event)
        val trailer = CoAuthorConfig.getCoAuthoredByTrailer()
        assertEquals(1, messages.size)
        assertEquals("feat: add feature\n\n$trailer", messages[0])
    }

    fun testActionPerformedSkipsWhenTrailerExists() {
        val action = GenerateCommitMessageAction()
        val trailer = CoAuthorConfig.getCoAuthoredByTrailer()
        val document = EditorFactory.getInstance().createDocument("feat: add feature\n\n$trailer")
        val messages = mutableListOf<String>()
        val event =
            createActionEvent(
                withProject = true,
                commitMessageControl = createCommitMessageControl(messages),
                document = document,
            )
        action.actionPerformed(event)
        assertTrue("Should not modify message when trailer already exists", messages.isEmpty())
    }

    fun testActionPerformedWithEmptyMessage() {
        val action = GenerateCommitMessageAction()
        val document = EditorFactory.getInstance().createDocument("")
        val messages = mutableListOf<String>()
        val event =
            createActionEvent(
                withProject = true,
                commitMessageControl = createCommitMessageControl(messages),
                document = document,
            )
        action.actionPerformed(event)
        val trailer = CoAuthorConfig.getCoAuthoredByTrailer()
        assertEquals(1, messages.size)
        assertEquals(trailer, messages[0])
    }

    fun testActionPerformedReturnsWithoutProject() {
        val action = GenerateCommitMessageAction()
        val document = EditorFactory.getInstance().createDocument("")
        val messages = mutableListOf<String>()
        val event =
            createActionEvent(
                withProject = false,
                commitMessageControl = createCommitMessageControl(messages),
                document = document,
            )
        action.actionPerformed(event)
        assertTrue("Should not modify message when project is null", messages.isEmpty())
    }

    fun testCoAuthoredByTrailerFormat() {
        val trailer = CoAuthorConfig.getCoAuthoredByTrailer()
        assertEquals("Co-Authored-By: Claude <noreply@anthropic.com>", trailer)
        assertTrue(
            "Trailer should match Git Co-Authored-By format",
            trailer.matches(Regex("Co-Authored-By: .+ <.+@.+>")),
        )
    }

    fun testCoAuthoredByTrailerCustomValues() {
        val trailer = CoAuthorConfig.getCoAuthoredByTrailer("GPT", "noreply@openai.com")
        assertEquals("Co-Authored-By: GPT <noreply@openai.com>", trailer)
    }

    fun testBuildCommitMessageWithEmptyText() {
        val trailer = CoAuthorConfig.getCoAuthoredByTrailer()
        val result = GenerateCommitMessageAction.buildCommitMessage("", trailer)
        assertEquals(trailer, result)
    }

    fun testBuildCommitMessageWithWhitespaceOnly() {
        val trailer = CoAuthorConfig.getCoAuthoredByTrailer()
        val result = GenerateCommitMessageAction.buildCommitMessage("   \n  \t  ", trailer)
        assertEquals(trailer, result)
    }

    fun testBuildCommitMessageWithExistingText() {
        val trailer = CoAuthorConfig.getCoAuthoredByTrailer()
        val result = GenerateCommitMessageAction.buildCommitMessage("feat: add new feature", trailer)
        assertEquals("feat: add new feature\n\n$trailer", result)
    }

    fun testBuildCommitMessageTrimsTrailingWhitespace() {
        val trailer = CoAuthorConfig.getCoAuthoredByTrailer()
        val result = GenerateCommitMessageAction.buildCommitMessage("feat: add new feature   \n  ", trailer)
        assertEquals("feat: add new feature\n\n$trailer", result)
    }

    fun testBuildCommitMessageWithMultilineText() {
        val trailer = CoAuthorConfig.getCoAuthoredByTrailer()
        val message = "feat: add feature\n\nDetailed description\nwith multiple lines"
        val result = GenerateCommitMessageAction.buildCommitMessage(message, trailer)
        assertEquals("$message\n\n$trailer", result)
    }

    fun testProjectInitialization() {
        assertNotNull("Project should be initialized", project)
        assertTrue("Project should be properly initialized", project.isInitialized)
        assertFalse("Project should not be disposed", project.isDisposed)
    }

    private fun createCommitMessageControl(messages: MutableList<String>): CommitMessageI =
        CommitMessageI { message ->
            messages.add(message ?: "")
        }

    private fun createActionEvent(
        withProject: Boolean,
        commitMessageControl: CommitMessageI? = null,
        document: Document? = null,
    ): AnActionEvent {
        val dataContext =
            DataContext { dataId ->
                when (dataId) {
                    CommonDataKeys.PROJECT.name -> if (withProject) project else null
                    VcsDataKeys.COMMIT_MESSAGE_CONTROL.name -> commitMessageControl
                    VcsDataKeys.COMMIT_MESSAGE_DOCUMENT.name -> document
                    else -> null
                }
            }
        return AnActionEvent.createFromDataContext("test", Presentation(), dataContext)
    }
}
