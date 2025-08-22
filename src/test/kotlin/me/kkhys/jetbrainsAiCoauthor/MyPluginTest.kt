package me.kkhys.jetbrainsAiCoauthor

import com.intellij.ide.highlighter.XmlFileType
import com.intellij.psi.xml.XmlFile
import com.intellij.testFramework.TestDataPath
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.intellij.util.PsiErrorElementUtil

/**
 * Basic tests for JetBrains AI Co-Author plugin
 * 
 * @description Tests the basic functionality of the plugin
 * @since 1.0.0
 */
@TestDataPath("\$CONTENT_ROOT/src/test/testData")
class MyPluginTest : BasePlatformTestCase() {

    /**
     * Basic XML file parsing test
     */
    fun testXMLFile() {
        val psiFile = myFixture.configureByText(XmlFileType.INSTANCE, "<foo>bar</foo>")
        val xmlFile = assertInstanceOf(psiFile, XmlFile::class.java)

        assertFalse(PsiErrorElementUtil.hasErrors(project, xmlFile.virtualFile))
        assertNotNull(xmlFile.rootTag)

        xmlFile.rootTag?.let {
            assertEquals("foo", it.name)
            assertEquals("bar", it.value.text)
        }
    }

    /**
     * Refactoring (rename) test
     */
    fun testRename() {
        myFixture.testRename("foo.xml", "foo_after.xml", "a2")
    }

    /**
     * Plugin basic operation verification test
     */
    fun testPluginBasics() {
        // Verify that the project is properly initialized
        assertNotNull(project)
        assertTrue(project.isInitialized)
    }

    override fun getTestDataPath() = "src/test/testData/rename"
}
