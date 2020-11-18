package com.example.lint.checks.detector.code_guidelines.kotlin_style.use_spaces.around_operands

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import com.example.lint.checks.detector.code_guidelines.kotlin_style.restrictions.line_length.MaxLineLengthDetector
import org.jetbrains.uast.UClass
import org.jetbrains.uast.UElement

class SpaceMethodDetector : Detector(), Detector.UastScanner {
    companion object {
        /** Issue describing the problem and pointing to the detector implementation */
        @JvmField
        val ISSUE: Issue = Issue.create(
            id = "OMEGA_USE_SPACES_CORRECTLY",
            briefDescription = "Use spaces around operands ",
            explanation = """
                  About using spaces around operands.
                  http://wiki.omega-r.club/dev-android-code#rec228388172
                    """,
            category = Category.CORRECTNESS,
            priority = 7,
            severity = Severity.WARNING,
            implementation = Implementation(
                SpaceMethodDetector::class.java,
                Scope.JAVA_FILE_SCOPE
            )
        )

        private val END_FUNCTION_DECLARATION_REGEX = Regex("""\s*\{$""")
        private val RIGHT_END_FUNCTION_DECLARATION_REGEX = Regex("""([a-z]|[A-Z]|"|'|\)|=|>)\s\{$""")

        private val RIGHT_FUNCTIONS_OPEN_SCOPE_REGEX = Regex("""([a-z]|[A-Z]|\d)\s*\(""")

        private const val DELETE_SPACES_MESSAGE = "Remove extra spaces."
    }

    override fun getApplicableUastTypes(): List<Class<out UElement?>>? {
        return listOf(UClass::class.java)
    }

    override fun createUastHandler(context: JavaContext): UElementHandler? {
        return object : UElementHandler() {
            private val regexps = arrayOf(".", "::", "?.").map { it to Regex("""\s$it\s""") }.toMap()

            override fun visitClass(node: UClass) {
                val text = node.parent.text ?: return
                val lines = text.lines()
                var beginPosition = 0
                lines.forEach { line ->
                    val length = line.length

                    regexps.forEach { pair ->
                        if (line.contains(pair.value)) {
                            val beforeIndex = line.indexOf(" ${pair.key}")
                            val afterIndex = line.indexOf("${pair.key} ")
                            when {
                                (beforeIndex > 0 && afterIndex <= 0) -> {
                                    context.report(
                                        ISSUE, node,
                                        context.getRangeLocation
                                            (node.parent, beginPosition + beforeIndex, pair.key.length + 1),
                                        "$DELETE_SPACES_MESSAGE ${ISSUE.getExplanation(TextFormat.TEXT)}"
                                    )
                                }

                                (afterIndex > 0 && beforeIndex <= 0) -> {
                                    context.report(
                                        ISSUE, node,
                                        context.getRangeLocation
                                            (node.parent, beginPosition + afterIndex, pair.key.length + 1),
                                        "$DELETE_SPACES_MESSAGE ${ISSUE.getExplanation(TextFormat.TEXT)}"
                                    )
                                }

                                (afterIndex > 0 && beforeIndex > 0) -> {
                                    context.report(
                                        ISSUE, node,
                                        context.getRangeLocation
                                            (node.parent, beginPosition + beforeIndex, pair.key.length + 2),
                                        "$DELETE_SPACES_MESSAGE ${ISSUE.getExplanation(TextFormat.TEXT)}"
                                    )
                                }
                            }

                        }
                    }

                    if (line.contains(RIGHT_FUNCTIONS_OPEN_SCOPE_REGEX)) {
                        val index = line.indexOf(" (")
                        if (index > 0) {
                            context.report(
                                ISSUE, node,
                                context.getRangeLocation(node.parent, beginPosition + index, 2),
                                "$DELETE_SPACES_MESSAGE ${ISSUE.getExplanation(TextFormat.TEXT)} $beginPosition $index $text"
                            )
                        }
                    }

                    if (!line.contains(RIGHT_END_FUNCTION_DECLARATION_REGEX)
                        && line.contains(END_FUNCTION_DECLARATION_REGEX)
                        && !line.matches(END_FUNCTION_DECLARATION_REGEX)
                    ) {
                        context.report(
                            ISSUE, node,
                            context.getRangeLocation(node.parent, beginPosition + length - 1, 1),
                            ISSUE.getExplanation(TextFormat.TEXT)
                        )
                    }

                    beginPosition += length
                    beginPosition++ // for adding new string pair.key
                }
            }
        }
    }
}