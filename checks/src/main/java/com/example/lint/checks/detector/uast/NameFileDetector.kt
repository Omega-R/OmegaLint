package com.example.lint.checks.detector.uast

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import org.jetbrains.uast.UClass
import org.jetbrains.uast.UElement

@Suppress("UnstableApiUsage")
class NameFileDetector : Detector(), Detector.UastScanner {
    companion object {
        /** Issue describing the problem and pointing to the detector implementation */
        @JvmField
        val ISSUE: Issue = Issue.create(
            // ID: used in @SuppressLint warnings etc
            id = "FileName",
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
                NameFileDetector::class.java,
                Scope.JAVA_FILE_SCOPE
            )
        )

        const val ACTIVITY_VALUE = "Activity"
        const val FRAGMENT_VALUE = "Fragment"
        const val VIEW_VALUE = "View"
        const val SERVICE_VALUE = "Service"
        const val PRESENTER_VALUE = "Presenter"
        const val PROVIDER_VALUE = "Provider"
    }

    override fun getApplicableUastTypes(): List<Class<out UElement?>>? {
        return listOf(UClass::class.java)
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
            override fun visitClass(node: UClass) {
                /**
                 * UpperCamelCase check
                 */
                val name = node.name ?: return

                if (name.contains(Regex("[A-Z][A-Z]")))
                    context.report(
                        ISSUE, node, context.getNameLocation(node),
                        "Rename this file. File name should match UpperCamelCase.",
                        createFix(name)
                    )


                /**
                 * Parent suffix check
                 */
                val superClass = node.javaPsi.superClass ?: return

                if (!superClass.isInterface && (superClass.name != "Object")) {
                    val part = superClass.name ?: return

                    when {
                        part.matches(Regex(".*${ACTIVITY_VALUE}$")) ->
                            if (!name.matches(Regex(".*${ACTIVITY_VALUE}$"))) {
                                makeContextReport(value = ACTIVITY_VALUE, node)
                            }

                        part.matches(Regex(".*${FRAGMENT_VALUE}$")) ->
                            if (!name.matches(Regex(".*${FRAGMENT_VALUE}$"))) {
                                makeContextReport(value = FRAGMENT_VALUE, node)
                            }

                        part.matches(Regex(".*${VIEW_VALUE}$")) ->
                            if (!name.matches(Regex(".*${VIEW_VALUE}$"))) {
                                makeContextReport(value = VIEW_VALUE, node)
                            }

                        part.matches(Regex(".*${SERVICE_VALUE}$")) ->
                            if (!name.matches(Regex(".*${SERVICE_VALUE}$"))) {
                                makeContextReport(value = SERVICE_VALUE, node)
                            }

                        part.matches(Regex(".*${PRESENTER_VALUE}$")) ->
                            if (!name.matches(Regex(".*${PRESENTER_VALUE}$"))) {
                                makeContextReport(value = PRESENTER_VALUE, node)
                            }

                        part.matches(Regex(".*${PROVIDER_VALUE}$")) ->
                            if (!name.matches(Regex(".*${PROVIDER_VALUE}$"))) {
                                makeContextReport(value = PROVIDER_VALUE, node)
                            }

                        else -> if (!name.contains(part)) { // this if doesn't work
                            makeContextReport(part, node)
                        }

                    }
                }

            }

            private fun makeContextReport(value: String, node: UClass) {
                return context.report(
                    ISSUE, node, context.getNameLocation(node),
                    "Class name should end with $value"
                )
            }

            private fun createFix(string: String): LintFix? {
                return fix().replace().text(string).build()
            }
        }
    }
}
