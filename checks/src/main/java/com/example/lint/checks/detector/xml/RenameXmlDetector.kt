package com.example.lint.checks.detector.xml

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import org.jetbrains.uast.UElement

@Suppress("UnstableApiUsage")
class RenameXmlDetector : Detector(), Detector.XmlScanner {
    companion object {
        /** Issue describing the problem and pointing to the detector implementation */
        @JvmField
        val ISSUE: Issue = Issue.create(
            // ID: used in @SuppressLint warnings etc
            id = "RenameXmlDetector",
            // Title -- shown in the IDE's preference dialog, as category headers in the
            // Analysis results window, etc
            briefDescription = "Use this abbreviation does not match the coding convention",
            // Full explanation of the issue; you can use some markdown markup such as
            // `monospace`, *italic*, and **bold**.
            explanation = """
                  Don't use abbreviations.
                    """,
            category = Category.CORRECTNESS,
            priority = 7,
            severity = Severity.INFORMATIONAL,
            implementation = Implementation(
                RenameXmlDetector::class.java,
                Scope.JAVA_FILE_SCOPE
            )
        )
    }

    override fun getApplicableUastTypes(): List<Class<out UElement?>>? {
        return listOf(UElement::class.java)
    }

    override fun createUastHandler(context: JavaContext): UElementHandler? {
        return object : UElementHandler() {
            override fun visitElement(node: UElement) {
                val name = node.asLogString()

                if (name.contains("ctx", false)) {
                    context.report(
                        ISSUE,
                        node,
                        context.getNameLocation(node),
                        "Don't use abbreviations. Rename this argument to 'context'.",
                        createContextFix()
                    )
                }
            }

            private fun createContextFix(): LintFix? {
                return fix().replace().text("ctx").with("context").build()
            }
        }
    }
}
