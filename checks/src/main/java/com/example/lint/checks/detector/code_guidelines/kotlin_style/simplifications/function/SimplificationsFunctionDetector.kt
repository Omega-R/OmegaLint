package com.example.lint.checks.detector.code_guidelines.kotlin_style.simplifications.function

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import com.example.lint.checks.detector.code_guidelines.kotlin_style.restrictions.line_length.MaxLineLengthDetector
import org.jetbrains.uast.UElement
import org.jetbrains.uast.UMethod

class SimplificationsFunctionDetector : Detector(), Detector.UastScanner {
    companion object {
        /** Issue describing the problem and pointing to the detector implementation */
        @JvmField
        val ISSUE: Issue = Issue.create(
            id = "CAN_USE_EXPRESSION_FUNCTION",
            briefDescription = "When a function contains only one expression, it can be represented as an \"expression function\".",
            explanation = """
                  You can change it to "expression function"
                  http://wiki.omega-r.club/dev-android-code#rec228389255
                    """,
            category = Category.CORRECTNESS,
            priority = 7,
            severity = Severity.INFORMATIONAL,
            implementation = Implementation(
                SimplificationsFunctionDetector::class.java,
                Scope.JAVA_FILE_SCOPE
            )
        )

        private val ONE_EXPRESSION_REGEX = Regex("""\{\s*return\s*([a-z]|[A-Z]|["]|[']|[(]|[)]|[=]|[\s])*\s*\}""")
    }

    override fun getApplicableUastTypes(): List<Class<out UElement?>>? {
        return listOf(UMethod::class.java)
    }

    override fun createUastHandler(context: JavaContext): UElementHandler? {
        return object : UElementHandler() {
            override fun visitMethod(node: UMethod) {
                val body = node.uastBody ?: return
                if (body.asRenderString().matches(ONE_EXPRESSION_REGEX)) {
                    context.report(
                        MaxLineLengthDetector.ISSUE, node,
                        context.getNameLocation(node),
                        ISSUE.getExplanation(TextFormat.TEXT)
                    )
                }
            }
        }
    }
}