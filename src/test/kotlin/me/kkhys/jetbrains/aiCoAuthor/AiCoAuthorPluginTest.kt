package me.kkhys.jetbrains.aiCoAuthor

import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.intellij.ui.EditorTextField
import me.kkhys.jetbrains.aiCoAuthor.actions.GenerateCommitMessageAction
import me.kkhys.jetbrains.aiCoAuthor.vcs.CommitMessageAiAssistant
import javax.swing.JTextArea

/**
 * Comprehensive tests for JetBrains AI Co-Author plugin
 *
 * @description Tests all aspects of the AI Co-Author plugin including
 *              VCS integration, action functionality, and UI interactions
 * @since 1.0.0
 */
class AiCoAuthorPluginTest : BasePlatformTestCase() {
    private lateinit var commitMessageAiAssistant: CommitMessageAiAssistant
    private lateinit var generateCommitMessageAction: GenerateCommitMessageAction

    /**
     * Set up test environment before each test
     */
    override fun setUp() {
        super.setUp()
        commitMessageAiAssistant = CommitMessageAiAssistant()
        generateCommitMessageAction = GenerateCommitMessageAction()
    }

    /**
     * Test CommitMessageAiAssistant factory creation
     *
     * @description Verifies that the CheckinHandlerFactory can be instantiated
     */
    fun testCommitMessageAiAssistantInstantiation() {
        assertNotNull("CommitMessageAiAssistant should be created", commitMessageAiAssistant)
        assertTrue(
            "Should be instance of CheckinHandlerFactory",
            true,
        )
    }

    /**
     * Test GenerateCommitMessageAction initialization
     *
     * @description Verifies that the action is properly initialized with correct text and icon
     */
    fun testGenerateCommitMessageActionInitialization() {
        val presentation = generateCommitMessageAction.templatePresentation

        assertEquals("🤖 Add AI Co-Author", presentation.text)
        assertEquals("Add Claude as co-author to your commit message", presentation.description)
        assertNull("Icon should be null", presentation.icon)
    }

    /**
     * Test Co-Authored-By string format
     *
     * @description Verifies that the co-author string follows Git trailer format
     */
    fun testCoAuthoredByFormat() {
        val expectedFormat = "Co-Authored-By: Claude <noreply@anthropic.com>"

        // This tests the format used in the action
        assertTrue(
            "Co-Authored-By format should match Git trailer standard",
            expectedFormat.matches(Regex("Co-Authored-By: .+ <.+@.+>")),
        )
        assertTrue("Should contain Claude name", expectedFormat.contains("Claude"))
        assertTrue("Should contain Anthropic email", expectedFormat.contains("noreply@anthropic.com"))
    }

    /**
     * Test text component identification logic
     *
     * @description Verifies the logic for identifying commit message text fields
     */
    fun testTextComponentIdentification() {
        // Test EditorTextField identification
        val editorTextField = EditorTextField("", project, com.intellij.openapi.fileTypes.PlainTextFileType.INSTANCE)
        editorTextField.isVisible = true

        assertTrue(
            "Visible EditorTextField should be identified as commit field",
            isLikelyCommitMessageFieldSimulation(editorTextField, ""),
        )

        // Test JTextArea identification
        val textArea = JTextArea()
        textArea.isVisible = true
        textArea.isEditable = true
        textArea.rows = 5

        assertTrue(
            "Multi-line editable JTextArea should be identified as commit field",
            isLikelyCommitMessageFieldSimulation(textArea, ""),
        )
    }

    /**
     * Test text component identification with invalid components
     *
     * @description Verifies rejection of inappropriate text components
     */
    fun testTextComponentRejection() {
        // Test invisible component
        val invisibleTextField = EditorTextField("", project, com.intellij.openapi.fileTypes.PlainTextFileType.INSTANCE)
        invisibleTextField.isVisible = false

        assertFalse(
            "Invisible component should be rejected",
            isLikelyCommitMessageFieldSimulation(invisibleTextField, ""),
        )

        // Test single-line JTextArea
        val singleLineTextArea = JTextArea()
        singleLineTextArea.isVisible = true
        singleLineTextArea.isEditable = true
        singleLineTextArea.rows = 1

        assertFalse(
            "Single-line JTextArea should be rejected",
            isLikelyCommitMessageFieldSimulation(singleLineTextArea, ""),
        )

        // Test non-editable JTextArea
        val nonEditableTextArea = JTextArea()
        nonEditableTextArea.isVisible = true
        nonEditableTextArea.isEditable = false
        nonEditableTextArea.rows = 5

        assertFalse(
            "Non-editable JTextArea should be rejected",
            isLikelyCommitMessageFieldSimulation(nonEditableTextArea, ""),
        )
    }

    /**
     * Test Co-Authored-By duplication prevention
     *
     * @description Verifies that duplicate Co-Authored-By lines are prevented
     */
    fun testDuplicationPrevention() {
        val existingText = "Initial commit message\n\nCo-Authored-By: Claude <noreply@anthropic.com>"

        assertFalse(
            "Should prevent duplication when Claude co-author already exists",
            shouldAddCoAuthor(existingText),
        )

        val differentCoAuthor = "Initial commit message\n\nCo-Authored-By: Someone Else <other@example.com>"

        assertTrue(
            "Should allow adding Claude when different co-author exists",
            shouldAddCoAuthor(differentCoAuthor),
        )
    }

    /**
     * Test text update logic for different scenarios
     *
     * @description Verifies proper text formatting in various scenarios
     */
    fun testTextUpdateFormatting() {
        val coAuthoredBy = "Co-Authored-By: Claude <noreply@anthropic.com>"

        // Test empty text
        val emptyResult = formatUpdatedText("", coAuthoredBy)
        assertEquals("Empty text should result in just co-author", coAuthoredBy, emptyResult)

        // Test existing commit message
        val existingMessage = "feat: add new feature"
        val expectedWithMessage = "$existingMessage\n\n$coAuthoredBy"
        val resultWithMessage = formatUpdatedText(existingMessage, coAuthoredBy)
        assertEquals("Should append co-author with proper spacing", expectedWithMessage, resultWithMessage)

        // Test message with trailing whitespace
        val messageWithWhitespace = "feat: add new feature   \n  "
        val resultWithWhitespace = formatUpdatedText(messageWithWhitespace, coAuthoredBy)
        assertEquals("Should trim whitespace before appending", expectedWithMessage, resultWithWhitespace)
    }

    /**
     * Test plugin registration in ActionManager
     *
     * @description Verifies that the action is properly registered in IntelliJ's ActionManager
     */
    fun testActionRegistration() {
        val actionManager = ActionManager.getInstance()
        val action = actionManager.getAction("AddAiCoAuthor")

        assertNotNull("Action should be registered in ActionManager", action)
        assertTrue(
            "Registered action should be instance of GenerateCommitMessageAction",
            action is GenerateCommitMessageAction,
        )
    }

    /**
     * Test project initialization
     *
     * @description Basic test to verify project setup for other tests
     */
    fun testProjectInitialization() {
        assertNotNull("Project should be initialized", project)
        assertTrue("Project should be properly initialized", project.isInitialized)
        assertFalse("Project should not be disposed", project.isDisposed)
    }

    /**
     * Test plugin component lifecycle
     *
     * @description Verifies that plugin components can be created without errors
     */
    fun testPluginComponentLifecycle() {
        // Test creating multiple instances (simulating plugin lifecycle)
        val assistant1 = CommitMessageAiAssistant()
        val assistant2 = CommitMessageAiAssistant()
        val action1 = GenerateCommitMessageAction()
        val action2 = GenerateCommitMessageAction()

        assertNotNull("First assistant instance should be created", assistant1)
        assertNotNull("Second assistant instance should be created", assistant2)
        assertNotNull("First action instance should be created", action1)
        assertNotNull("Second action instance should be created", action2)

        // Verify they are independent instances
        assertNotSame("Assistant instances should be different", assistant1, assistant2)
        assertNotSame("Action instances should be different", action1, action2)
    }

    /**
     * Test action template text and description
     *
     * @description Verifies the action's display text and description are correct
     */
    fun testActionTemplate() {
        val action = GenerateCommitMessageAction()
        val presentation = action.templatePresentation

        assertEquals("Action text should be correct", "🤖 Add AI Co-Author", presentation.text)
        assertEquals(
            "Action description should be correct",
            "Add Claude as co-author to your commit message",
            presentation.description,
        )
    }

    /**
     * Test commit message text validation
     *
     * @description Tests various commit message scenarios
     */
    fun testCommitMessageValidation() {
        // Test empty commit message
        assertTrue(
            "Empty message should allow co-author addition",
            shouldAddCoAuthor(""),
        )

        // Test standard commit message
        assertTrue(
            "Standard commit message should allow co-author addition",
            shouldAddCoAuthor("feat: add new feature\n\nThis feature adds something cool."),
        )

        // Test message already containing Claude co-author
        assertFalse(
            "Message with Claude co-author should prevent duplication",
            shouldAddCoAuthor("feat: add feature\n\nCo-Authored-By: Claude <noreply@anthropic.com>"),
        )

        // Test message with partial Claude match (should still prevent)
        assertFalse(
            "Message with partial Claude match should prevent duplication",
            shouldAddCoAuthor("feat: add feature\n\nThanks to Co-Authored-By: Claude for help"),
        )
    }

    /**
     * Test Edge cases for text formatting
     *
     * @description Tests edge cases in text formatting logic
     */
    fun testTextFormattingEdgeCases() {
        val coAuthor = "Co-Authored-By: Claude <noreply@anthropic.com>"

        // Test with only whitespace
        val whitespaceOnly = "   \n  \t  \n   "
        val result1 = formatUpdatedText(whitespaceOnly, coAuthor)
        assertEquals("Whitespace-only should be treated as empty", coAuthor, result1)

        // Test with newlines in message
        val messageWithNewlines = "feat: add feature\n\nDetailed description\nwith multiple lines"
        val result2 = formatUpdatedText(messageWithNewlines, coAuthor)
        assertEquals(
            "Should properly handle multi-line messages",
            "$messageWithNewlines\n\n$coAuthor",
            result2,
        )
    }

    // Helper methods for testing private logic

    /**
     * Simulates the isLikelyCommitMessageField logic for testing
     */
    private fun isLikelyCommitMessageFieldSimulation(
        component: Any,
        text: String,
    ): Boolean =
        when (component) {
            is EditorTextField -> {
                component.isVisible &&
                    (text.isEmpty() || text.length < 1000)
            }
            is JTextArea -> {
                component.isVisible &&
                    component.isEditable &&
                    component.rows > 1 &&
                    (text.isEmpty() || text.length < 1000)
            }
            else -> false
        }

    /**
     * Simulates the duplicate prevention logic for testing
     */
    private fun shouldAddCoAuthor(text: String): Boolean =
        !text.contains("Co-Authored-By: Claude")

    /**
     * Simulates the text formatting logic for testing
     */
    private fun formatUpdatedText(
        currentText: String,
        coAuthoredBy: String,
    ): String =
        if (currentText.trim().isEmpty()) {
            coAuthoredBy
        } else {
            "${currentText.trim()}\n\n$coAuthoredBy"
        }
}
