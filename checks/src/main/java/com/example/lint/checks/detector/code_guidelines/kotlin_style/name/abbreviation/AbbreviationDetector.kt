package com.example.lint.checks.detector.code_guidelines.kotlin_style.name.abbreviation

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import org.jetbrains.uast.UElement

@Suppress("UnstableApiUsage")
class AbbreviationDetector : Detector(), Detector.UastScanner {
    companion object {
        /** Issue describing the problem and pointing to the detector implementation */
        @JvmField
        val ISSUE: Issue = Issue.create(
            // ID: used in @SuppressLint warnings etc
            id = "NON_USE_ABBREVIATIONS.",
            // Title -- shown in the IDE's preference dialog, as category headers in the
            // Analysis results window, etc
            briefDescription = "Use this abbreviation does not match the coding convention",
            // Full explanation of the issue; you can use some markdown markup such as
            // `monospace`, *italic*, and **bold**.
            explanation = """
                  Don't use abbreviations.
                  http://wiki.omega-r.club/dev-android-code#rec228153340
                    """,
            category = Category.CORRECTNESS,
            priority = 7,
            severity = Severity.INFORMATIONAL,
            implementation = Implementation(
                AbbreviationDetector::class.java,
                Scope.JAVA_FILE_SCOPE
            )
        )
        private const val CONTEXT_ABBREVIATION = "ctx"
        private const val CONTEXT_CORRECTLY_NAME = "context"
    }

    override fun getApplicableUastTypes(): List<Class<out UElement?>>? {
        return listOf(UElement::class.java)
    }

    override fun createUastHandler(context: JavaContext): UElementHandler? {
        // Note: Visiting UAST nodes is a pretty general purpose mechanism;
        // Lint has specialized support to do common things like "visit every class
        // that extends a given super class or implements a given interface", and
        // "visit every call site that calls a method by a given name" etc.
        // Take a careful look at UastScanner and the various existing lint check
        // implementations before doing things the "hard way".
        // Also be aware of context.getJavaEvaluator() which provides a lot of
        // utility functionality.
        return object : UElementHandler() {
            override fun visitElement(node: UElement) {
                val name = node.asLogString()

                if (name == CONTEXT_ABBREVIATION) {
                    context.report(
                        ISSUE,
                        node,
                        context.getNameLocation(node),
                        ISSUE.getExplanation(TextFormat.TEXT),
                        createContextFix()
                    )
                }

            }

            private fun createContextFix(): LintFix? {
                return fix().replace().text(CONTEXT_ABBREVIATION).with(CONTEXT_CORRECTLY_NAME).build()
            }
        }
    }
}
