package com.example.lint.checks.detector.uast

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import org.jetbrains.uast.UElement
import org.jetbrains.uast.UMethod
import org.jetbrains.uast.UParameter

@Suppress("UnstableApiUsage")
class PositionArgumentDetector : Detector(), Detector.UastScanner {
    companion object {
        /** Issue describing the problem and pointing to the detector implementation */
        @JvmField
        val ISSUE: Issue = Issue.create(
            // ID: used in @SuppressLint warnings etc
            id = "ContextArgumentPosition",
            // Title -- shown in the IDE's preference dialog, as category headers in the
            // Analysis results window, etc
            briefDescription = "The file name does not match the coding convention",
            // Full explanation of the issue; you can use some markdown markup such as
            // `monospace`, *italic*, and **bold**.
            explanation = """
                  Class names are recorded in UpperCamelCase.
                    """,
            category = Category.CORRECTNESS,
            priority = 6,
            severity = Severity.WARNING,
            implementation = Implementation(
                PositionArgumentDetector::class.java,
                Scope.JAVA_FILE_SCOPE
            )
        )
    }

    override fun getApplicableUastTypes(): List<Class<out UElement?>>? {
        return listOf(UParameter::class.java)
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
            override fun visitParameter(node: UParameter) {
                val parent = node.uastParent as? UMethod ?: return
                val params = parent.uastParameters

                if ((params[0] != node) && ((node.name == "context") || (node.name == "ctx"))) {
                    context.report(
                        ISSUE, node as UElement, context.getNameLocation(node),
                        "Context argument should be the first"
                    )
                }

                if ((params[params.size - 1] != node) && (node.name == "callback")) {
                    context.report(
                        ISSUE,
                        node as UElement,
                        context.getNameLocation(node),
                        "Callback argument should be the last.",
                    )
                }
            }
        }
    }
}
