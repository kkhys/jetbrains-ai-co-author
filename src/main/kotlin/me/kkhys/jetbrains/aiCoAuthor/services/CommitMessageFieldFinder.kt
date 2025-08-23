package me.kkhys.jetbrains.aiCoAuthor.services

import com.intellij.ui.EditorTextField
import me.kkhys.jetbrains.aiCoAuthor.config.CoAuthorConfig
import java.awt.Component
import java.awt.Container
import javax.swing.JFrame
import javax.swing.JTextArea

/**
 * Service for finding commit message text fields in the UI
 * 
 * @description Specialized service for locating commit message text components
 *              within IntelliJ's UI hierarchy using heuristic detection
 * @since 1.0.0
 */
class CommitMessageFieldFinder {
    
    /**
     * Finds and returns the first commit message field found in the UI
     * 
     * @return The commit message text component or null if not found
     */
    fun findCommitMessageField(): Component? {
        return try {
            val frames = JFrame.getFrames()
            for (frame in frames) {
                val field = searchComponentTree(frame)
                if (field != null) {
                    return field
                }
            }
            null
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Recursively searches the component tree for commit message fields
     * 
     * @param component The root component to search from
     * @return The first commit message field found, or null
     */
    private fun searchComponentTree(component: Component): Component? {
        when (component) {
            is EditorTextField -> {
                if (isLikelyCommitMessageField(component)) {
                    return component
                }
            }
            is JTextArea -> {
                if (isLikelyCommitMessageField(component)) {
                    return component
                }
            }
            is Container -> {
                for (child in component.components) {
                    val result = searchComponentTree(child)
                    if (result != null) {
                        return result
                    }
                }
            }
        }
        return null
    }
    
    /**
     * Determines if a component is likely a commit message field using heuristics
     * 
     * @param component The component to evaluate
     * @return true if the component appears to be a commit message field
     */
    private fun isLikelyCommitMessageField(component: Component): Boolean {
        return when (component) {
            is EditorTextField -> {
                val text = component.text
                component.isVisible && 
                (text.isEmpty() || text.length < CoAuthorConfig.MAX_COMMIT_MESSAGE_LENGTH)
            }
            is JTextArea -> {
                val text = component.text ?: ""
                component.isVisible && 
                component.isEditable && 
                component.rows > CoAuthorConfig.MIN_TEXTAREA_ROWS &&
                (text.isEmpty() || text.length < CoAuthorConfig.MAX_COMMIT_MESSAGE_LENGTH)
            }
            else -> false
        }
    }
    
    /**
     * Gets the current text from a text component
     * 
     * @param component The text component
     * @return The current text content, or empty string if invalid component
     */
    fun getTextFromComponent(component: Component): String {
        return when (component) {
            is EditorTextField -> component.text
            is JTextArea -> component.text ?: ""
            else -> ""
        }
    }
    
    /**
     * Sets text in a text component
     * 
     * @param component The text component to update
     * @param text The new text content
     * @return true if the text was set successfully
     */
    fun setTextInComponent(component: Component, text: String): Boolean {
        return try {
            when (component) {
                is EditorTextField -> {
                    component.text = text
                    true
                }
                is JTextArea -> {
                    component.text = text
                    true
                }
                else -> false
            }
        } catch (e: Exception) {
            false
        }
    }
}