package com.example.lint.checks.detector.coding_convention_2.cc_2_2.cc_2_2_3.cc_2_2_3_5

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import org.jetbrains.uast.UElement
import org.jetbrains.uast.UMethod

class EmptyBodyFunctionDetector : Detector(), Detector.UastScanner {
    companion object {
        /** Issue describing the problem and pointing to the detector implementation */
        @JvmField
        val ISSUE: Issue = Issue.create(
            id = "EmptyBodyFunction",
            briefDescription =
            "Function body is empty.It does not match the coding convention. Function body should not be empty.",
            explanation = """
                  Function body is empty. Write function body or add comment and ignore annotation.
                  http://wiki.omega-r.club/dev-android-code#rec228194993
                    """,
            category = Category.CORRECTNESS,
            priority = 7,
            severity = Severity.WARNING,
            implementation = Implementation(
                EmptyBodyFunctionDetector::class.java,
                Scope.JAVA_FILE_SCOPE
            )
        )

        private val EMPTY_BODY_REGEX = Regex("""\{\s*}""")
    }

    override fun getApplicableUastTypes(): List<Class<out UElement?>>? {
        return listOf(UMethod::class.java)
    }

    override fun createUastHandler(context: JavaContext): UElementHandler? {
        return object : UElementHandler() {
            override fun visitMethod(node: UMethod) {
                val body = node.uastBody ?: return
                if (body.asRenderString().matches(EMPTY_BODY_REGEX)) {
                    context.report(ISSUE, node, context.getLocation(body), ISSUE.getExplanation(TextFormat.TEXT))
                }
            }
        }
    }
}