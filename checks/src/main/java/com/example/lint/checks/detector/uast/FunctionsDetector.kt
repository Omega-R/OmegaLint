package com.example.lint.checks.detector.uast

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import org.jetbrains.uast.UElement
import org.jetbrains.uast.UMethod
import org.jetbrains.uast.getUCallExpression

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
                        ISSUE, node, context.getLocation(body), "Function body is empty. Write the function body."

                    )
                }

            }
        }
    }
}