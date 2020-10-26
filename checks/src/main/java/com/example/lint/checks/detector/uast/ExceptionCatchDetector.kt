package com.example.lint.checks.detector.uast

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import org.jetbrains.uast.UCatchClause
import org.jetbrains.uast.UElement

@Suppress("UnstableApiUsage")
class ExceptionCatchDetector : Detector(), Detector.UastScanner {
    companion object {
        /** Issue describing the problem and pointing to the detector implementation */
        @JvmField
        val ISSUE: Issue = Issue.create(
            // ID: used in @SuppressLint warnings etc
            id = "ExceptionCatch",
            // Title -- shown in the IDE's preference dialog, as category headers in the
            // Analysis results window, etc
            briefDescription = "Catch body is empty, it  not match the coding convention",
            // Full explanation of the issue; you can use some markdown markup such as
            // `monospace`, *italic*, and **bold**.
            explanation = """
                  Don't leave blank catch body.
                    """,
            category = Category.CORRECTNESS,
            priority = 7,
            severity = Severity.WARNING,
            implementation = Implementation(
                ExceptionCatchDetector::class.java,
                Scope.JAVA_FILE_SCOPE
            )
        )
    }

    override fun getApplicableUastTypes(): List<Class<out UElement?>>? {
        return listOf(UCatchClause::class.java)
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
            override fun visitCatchClause(node: UCatchClause) {
                val body = node.body
                val string = body.asRenderString()
                if (string == "{\n}") { //string.matches(Regex("{*\\b/\n\\b.*}))) {
                    context.report(
                        ISSUE,
                        body,
                        context.getNameLocation(body),
                        "Catch body is empty. Add exception handling."
                    )
                }
                val parameters = node.parameters
                parameters.forEach {
                    if (it.type.canonicalText == "java.lang.Exception") {
                        if (!string.contains("throw")) {
                            context.report(
                                ISSUE,
                                body,
                                context.getNameLocation(it),
                                "Catch generalized exception. Should throw specific exception in catch body"
                            )
                        }
                    }
                }
            }

            /*private fun createFix(): LintFix {
                return fix().replace().text("}").with("throw \n }").build()
            }*/

        }
    }

}
