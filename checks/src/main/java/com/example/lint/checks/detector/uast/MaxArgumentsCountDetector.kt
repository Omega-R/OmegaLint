package com.example.lint.checks.detector.uast

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import org.jetbrains.uast.UElement
import org.jetbrains.uast.UMethod

class MaxArgumentsCountDetector : Detector(), Detector.UastScanner {
    companion object {
        /** Issue describing the problem and pointing to the detector implementation */
        @JvmField
        val ISSUE: Issue = Issue.create(
            // ID: used in @SuppressLint warnings etc
            id = "MaxArgumentsCount",
            // Title -- shown in the IDE's preference dialog, as category headers in the
            // Analysis results window, etc
            briefDescription = "Arguments count does not match the coding convention",
            // Full explanation of the issue; you can use some markdown markup such as
            // `monospace`, *italic*, and **bold**.
            explanation = """
                  Don't use abbreviations.
                    """,
            category = Category.CORRECTNESS,
            priority = 7,
            severity = Severity.WARNING,
            implementation = Implementation(
                MaxArgumentsCountDetector::class.java,
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
            }
        }
    }
}