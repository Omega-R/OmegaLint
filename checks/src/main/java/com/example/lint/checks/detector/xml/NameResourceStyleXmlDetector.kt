package com.example.lint.checks.detector.xml

import com.android.resources.ResourceFolderType
import com.android.tools.lint.detector.api.*
import org.w3c.dom.Element

class NameResourceStyleXmlDetector : ResourceXmlDetector() {

    companion object {
        val ISSUE = Issue.create(
            id = "NameResourceStyleXml",
            briefDescription = "Detects usages of 'Okay' in string resources",
            explanation = "The word 'OK' should be used instead of 'Okay' in string resources",
            category = Category.CORRECTNESS,
            severity = Severity.WARNING,
            implementation = Implementation(
                NameResourceStyleXmlDetector::class.java,
                Scope.RESOURCE_FILE_SCOPE
            )
        )

        const val ATTRIBUTE_NAME_VAL = "name"
        const val SUFFIX_STYLE = "Style"
        const val PARENT_VAL = "parent"
    }

    override fun appliesTo(folderType: ResourceFolderType): Boolean {
        return folderType == ResourceFolderType.VALUES
    }

    override fun getApplicableElements(): Collection<String>? {
        // Return the set of elements we want to analyze. In this case we want to
        // analyze every `<string>` element that is declared in XML.
        return setOf("style")
    }

    override fun visitElement(context: XmlContext, element: Element) {
        val name = element.getAttribute(ATTRIBUTE_NAME_VAL) ?: return
        if(name.matches(Regex(".*${SUFFIX_STYLE}$"))) {
            context.report(
                issue = ISSUE,
                scope = element,
                location = context.getLocation(element),
                message = "Delete Style from name",
                quickfixData = createDeleteStyleContextFix(name)
            )
        }
        val parent = element.getAttribute(PARENT_VAL) ?: return
        if (parent.isEmpty()) {
            return
        }

        context.report(
            issue = ISSUE,
            scope = element,
            location = context.getLocation(element),
            message = "It is desirable to carry out inheritance through the name.",
            quickfixData = createContextFix(name, parent)
        )

    }

    private fun createDeleteStyleContextFix(name: String): LintFix? {
        return fix()
            .replace().text(name).with(name.removeSuffix(SUFFIX_STYLE))
            .build()
    }

    private fun createContextFix(name: String, parent: String): LintFix? {
        return fix()
            .replace().text("\"$name\" $PARENT_VAL=\"$parent\"").with("\"$parent.$name\"")
            .build()

    }
}