package com.example.lint.checks.detector.uast

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import org.jetbrains.uast.UElement
import org.jetbrains.uast.UMethod

class FunctionsDetector : Detector(), Detector.UastScanner {
    companion object {
        /** Issue describing the problem and pointing to the detector implementation */
        @JvmField
        val ISSUE: Issue = Issue.create(
            id = "FunctionCheck",
            briefDescription = "Arguments count does not match the coding convention. Function body should not be empty.",
            explanation = """
                  Arguments count <= 5
                    """,
            category = Category.CORRECTNESS,
            priority = 7,
            severity = Severity.WARNING,
            implementation = Implementation(
                FunctionsDetector::class.java,
                Scope.JAVA_FILE_SCOPE
            )
        )
    }

    override fun getApplicableUastTypes(): List<Class<out UElement?>>? {
        return listOf(UMethod::class.java)
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
            override fun visitMethod(node: UMethod) {
                val params = node.uastParameters
                if (params.size > 5) {
                    context.report(
                        ISSUE, node, context.getNameLocation(node), "Method has too much arguments."
                    )
                }

                val body = node.uastBody ?: return

                if (body.asRenderString() == "{\n}") {
                    context.report(
                        ISSUE, node, context.getLocation(node), "Function body is empty. Write the function body."
                    )
                }

            }
        }
    }
}