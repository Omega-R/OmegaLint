package com.example.lint.checks.detector.code_guidelines.kotlin_style.restrictions.class_length

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import org.jetbrains.uast.UClass
import org.jetbrains.uast.UElement

@Suppress("UnstableApiUsage")
class MaxClassLengthDetector : Detector(), Detector.UastScanner {
    companion object {
        /** Issue describing the problem and pointing to the detector implementation */
        @JvmField
        val ISSUE: Issue = Issue.create(
            id = "MaxClassLength",
            briefDescription = "The file name does not match the coding convention",
            explanation = """
                  Количество строк в классе должно быть не больше 900.
                  http://wiki.omega-r.club/dev-android-code#rec228195687
                    """,
            category = Category.CORRECTNESS,
            priority = 6,
            severity = Severity.WARNING,
            implementation = Implementation(
                MaxClassLengthDetector::class.java,
                Scope.JAVA_FILE_SCOPE
            )
        )

        private const val MAX_LINES_COUNT = 900
    }

    override fun getApplicableUastTypes(): List<Class<out UElement?>>? {
        return listOf(UClass::class.java)
    }

    override fun createUastHandler(context: JavaContext): UElementHandler? {
        return object : UElementHandler() {
            override fun visitClass(node: UClass) {

                val lines = node.asRenderString().split("\n")

                /** Need to delete 2 strings, because body "{ }" */
                val size = lines.size

                if (size > MAX_LINES_COUNT) {
                    context.report(ISSUE, node, context.getNameLocation(node), ISSUE.getExplanation(TextFormat.TEXT))
                }
            }
        }
    }
}
