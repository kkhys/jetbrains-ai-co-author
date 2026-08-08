package me.kkhys.aiCoAuthor

import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.ActionUiKind
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

    fun testActionPerformedShowsWarningWithoutDocument() {
        val action = GenerateCommitMessageAction()
        val messages = mutableListOf<String>()
        val event =
            createActionEvent(
                withProject = true,
                commitMessageControl = createCommitMessageControl(messages),
                document = null,
            )
        action.actionPerformed(event)
        assertTrue("Should not modify message when document is missing", messages.isEmpty())
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
        assertEquals(1, messages.size)
        assertEquals("feat: add feature\n\n$COAUTHORED_BY_TRAILER", messages[0])
    }

    fun testActionPerformedSkipsWhenTrailerExists() {
        val action = GenerateCommitMessageAction()
        val document = EditorFactory.getInstance().createDocument("feat: add feature\n\n$COAUTHORED_BY_TRAILER")
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
        assertEquals(1, messages.size)
        assertEquals(COAUTHORED_BY_TRAILER, messages[0])
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
        assertEquals("Co-Authored-By: Claude <noreply@anthropic.com>", COAUTHORED_BY_TRAILER)
        assertTrue(
            "Trailer should match Git Co-Authored-By format",
            COAUTHORED_BY_TRAILER.matches(Regex("Co-Authored-By: .+ <.+@.+>")),
        )
    }

    fun testAppendTrailerWithEmptyText() {
        val result = GenerateCommitMessageAction.appendTrailer("")
        assertEquals(COAUTHORED_BY_TRAILER, result)
    }

    fun testAppendTrailerWithWhitespaceOnly() {
        val result = GenerateCommitMessageAction.appendTrailer("   \n  \t  ")
        assertEquals(COAUTHORED_BY_TRAILER, result)
    }

    fun testAppendTrailerWithExistingText() {
        val result = GenerateCommitMessageAction.appendTrailer("feat: add new feature")
        assertEquals("feat: add new feature\n\n$COAUTHORED_BY_TRAILER", result)
    }

    fun testAppendTrailerTrimsTrailingWhitespace() {
        val result = GenerateCommitMessageAction.appendTrailer("feat: add new feature   \n  ")
        assertEquals("feat: add new feature\n\n$COAUTHORED_BY_TRAILER", result)
    }

    fun testAppendTrailerWithMultilineText() {
        val message = "feat: add feature\n\nDetailed description\nwith multiple lines"
        val result = GenerateCommitMessageAction.appendTrailer(message)
        assertEquals("$message\n\n$COAUTHORED_BY_TRAILER", result)
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
        return AnActionEvent.createEvent(dataContext, Presentation(), "test", ActionUiKind.NONE, null)
    }
}
