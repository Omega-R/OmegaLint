package com.example.lint.checks.detector.uast

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import org.jetbrains.uast.*

class NameResourceLayoutDetector : Detector(), Detector.UastScanner {
    companion object {
        /** Issue describing the problem and pointing to the detector implementation */
        @JvmField
        val ISSUE: Issue = Issue.create(
            // ID: used in @SuppressLint warnings etc
            id = "NameResourceLayout",
            // Title -- shown in the IDE's preference dialog, as category headers in the
            // Analysis results window, etc
            briefDescription = "Something",
            // Full explanation of the issue; you can use some markdown markup such as
            // `monospace`, *italic*, and **bold**.
            explanation = """
                  Wrong layout name.
                  http://wiki.omega-r.club/dev-android-code#rec226396979
                    """,
            category = Category.CORRECTNESS,
            priority = 7,
            severity = Severity.WARNING,
            implementation = Implementation(
                NameResourceLayoutDetector::class.java,
                Scope.JAVA_FILE_SCOPE
            )
        )

        const val SET_CONTENT_VIEW_VAL = "setContentView"
        const val UPPER_ACTIVITY = "Activity"
        const val LOWER_ACTIVITY = "activity"

        const val INFLATE_VAL = "inflate"

        const val LAYOUT_PREFIX_VAL = "R.layout."
        const val UPPER_FRAGMENT = "Fragment"
        const val LOWER_FRAGMENT = "fragment"

        val CAMEL_REGEX = Regex("(?<=[a-zA-Z])[A-Z]")
    }

    override fun getApplicableUastTypes(): List<Class<out UElement?>>? {
        return listOf(UCallExpression::class.java)
    }

    override fun createUastHandler(context: JavaContext): UElementHandler? {
        return object : UElementHandler() {
            override fun visitCallExpression(node: UCallExpression) {
                val file = node.getContainingUFile() ?: return
                var className = file.classes.firstOrNull()?.name ?: return

                val name = node.methodName ?: return

                var layoutName = node.valueArguments.firstOrNull()?.asRenderString() ?: return
                layoutName = layoutName.replace(LAYOUT_PREFIX_VAL, "")

                when (name) {
                    /**
                     * FOR ACTIVITY
                     */
                    SET_CONTENT_VIEW_VAL -> {
                        if (className.contains(UPPER_ACTIVITY)) {
                            className = "$LOWER_ACTIVITY${className.replace(UPPER_ACTIVITY, "")}"

                            if (className.camelToSnakeCase() != layoutName)
                                context.report(
                                    ISSUE,
                                    node,
                                    context.getNameLocation(node.valueArguments.first()),
                                    ISSUE.getExplanation(TextFormat.TEXT)
                                )
                        }
                    }

                    /**
                     * FOR FRAGMENT
                     */
                    INFLATE_VAL -> {
                        if (className.contains(UPPER_FRAGMENT)) {
                            className = "$LOWER_FRAGMENT${className.replace(UPPER_FRAGMENT, "")}"

                            if (className.camelToSnakeCase() != layoutName)
                                context.report(
                                    ISSUE,
                                    node,
                                    context.getNameLocation(node.valueArguments.first()),
                                    ISSUE.getExplanation(TextFormat.TEXT)
                                )
                        }
                    }
                }

            }

            // String extensions
            fun String.camelToSnakeCase(): String {
                return CAMEL_REGEX.replace(this) {
                    "_${it.value}"
                }.toLowerCase()
            }
        }


    }
}

