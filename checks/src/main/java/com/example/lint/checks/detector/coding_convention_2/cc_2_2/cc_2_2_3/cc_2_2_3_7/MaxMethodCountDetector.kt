package com.example.lint.checks.detector.coding_convention_2.cc_2_2.cc_2_2_3.cc_2_2_3_7

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*

import org.jetbrains.uast.UClass
import org.jetbrains.uast.UElement

class MaxMethodCountDetector : Detector(), Detector.UastScanner {
    companion object {
        /** Issue describing the problem and pointing to the detector implementation */
        @JvmField
        val ISSUE: Issue = Issue.create(
            id = "MaxMethodCount",
            briefDescription = "Class methods count does not match the coding convention",
            explanation = """
                  Class should has 30 methods or less.
                  http://wiki.omega-r.club/dev-android-code#rec228195879
                    """,
            category = Category.CORRECTNESS,
            priority = 7,
            severity = Severity.WARNING,
            implementation = Implementation(
                MaxMethodCountDetector::class.java,
                Scope.JAVA_FILE_SCOPE
            )
        )

        private const val MAX_METHOD_COUNT = 30
    }

    override fun getApplicableUastTypes(): List<Class<out UElement?>>? {
        return listOf(UClass::class.java)
    }

    override fun createUastHandler(context: JavaContext): UElementHandler? {
        return object : UElementHandler() {
            override fun visitClass(node: UClass) {
                val methods = node.methods
                if (methods.size > MAX_METHOD_COUNT) {
                    context.report(ISSUE, node, context.getNameLocation(node), ISSUE.getExplanation(TextFormat.TEXT))
                }
            }
        }
    }
}

