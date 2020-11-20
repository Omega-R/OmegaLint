package com.omegar.lint.checks.detector.code_guidelines.xml_style.name_resource.theme_and_style

import com.android.resources.ResourceFolderType
import com.android.tools.lint.detector.api.*
import org.w3c.dom.Element

class NameResourceStyleXmlDetector : ResourceXmlDetector() {

    companion object {
        val ISSUE = Issue.create(
            id = "OMEGA_NAME_RESOURCE_STYLE_CORRECTLY",
            briefDescription = "Inheritance warning",
            explanation = """
                It is desirable to carry out inheritance through the name.
                http://wiki.omega-r.club/dev-android-code#rec228391441
                """,
            category = Category.CORRECTNESS,
            severity = Severity.WARNING,
            implementation = Implementation(
                NameResourceStyleXmlDetector::class.java,
                Scope.RESOURCE_FILE_SCOPE
            )
        )

        private const val ATTRIBUTE_NAME_VAL = "name"
        private const val SUFFIX_STYLE = "Style"
        private const val PARENT_VAL = "parent"
        private val SUFFIX_STYLE_REGEX = Regex(""".*$SUFFIX_STYLE$""")

        private const val REPORT_MESSAGE = "Delete Style from name\nhttp://wiki.omega-r.club/dev-android-code#rec228391441"
    }

    override fun appliesTo(folderType: ResourceFolderType): Boolean {
        return folderType == ResourceFolderType.VALUES
    }

    override fun getApplicableElements(): Collection<String>? {
        return setOf("style")
    }

    override fun visitElement(context: XmlContext, element: Element) {
        val name = element.getAttribute(ATTRIBUTE_NAME_VAL) ?: return
        if (name.matches(SUFFIX_STYLE_REGEX)) {
            context.report(
                issue = ISSUE,
                scope = element,
                location = context.getNameLocation(element),
                message = REPORT_MESSAGE,
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
            location = context.getNameLocation(element),
            message = ISSUE.getExplanation(TextFormat.TEXT),
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