package com.example.lint.checks.detector.uast

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import org.jetbrains.kotlin.resolve.multiplatform.ALL_MODULES
import org.jetbrains.uast.UClass
import org.jetbrains.uast.UElement
import org.jetbrains.uast.UFile

class AnnotationDetector : Detector(), Detector.UastScanner {
    companion object {
        /** Issue describing the problem and pointing to the detector implementation */
        @JvmField
        val ISSUE: Issue = Issue.create(
            id = "Annotation",
            briefDescription = "If there are multiple annotations for a class / field / method, place each annotation on a new line",
            explanation = """
                  If there are multiple annotations for a class / field / method, place each annotation on a new line.
                  http://wiki.omega-r.club/dev-android-code#rec228389852
                    """,
            category = Category.CORRECTNESS,
            priority = 7,
            severity = Severity.INFORMATIONAL,
            implementation = Implementation(
                AnnotationDetector::class.java,
                Scope.JAVA_FILE_SCOPE
            )
        )

        private val ONE_EXPRESSION_REGEX = Regex("""\s*@\s*([a-z]|[A-Z]|["]|[']|[(]|[)]|[=]|[\s])*@""")
    }

    override fun getApplicableUastTypes(): List<Class<out UElement?>>? {
        return listOf(UClass::class.java)
    }

    override fun createUastHandler(context: JavaContext): UElementHandler? {
        return object : UElementHandler() {
            override fun visitClass(node: UClass) {
                val text = node.parent?.text ?: return
                val lines = text.lines()
                var beginPosition = 0

                lines.forEach { line ->
                    val length = line.length

                    if (line.contains(ONE_EXPRESSION_REGEX)) {
                        context.report(
                            MaxLineLengthDetector.ISSUE, node,
                            context.getRangeLocation(node.parent, beginPosition, length),
                            ISSUE.getExplanation(TextFormat.TEXT)
                        )
                    }

                    beginPosition += line.length
                    beginPosition++ // for adding new string symbol

                }
            }
        }
    }
}