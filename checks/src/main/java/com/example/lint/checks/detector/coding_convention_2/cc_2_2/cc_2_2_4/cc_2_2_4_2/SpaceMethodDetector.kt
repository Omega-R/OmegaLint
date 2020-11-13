package com.example.lint.checks.detector.coding_convention_2.cc_2_2.cc_2_2_4.cc_2_2_4_2

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import com.example.lint.checks.detector.coding_convention_2.cc_2_2.cc_2_2_3.cc_2_2_3_1.MaxLineLengthDetector
import org.jetbrains.uast.UClass
import org.jetbrains.uast.UElement

class SpaceMethodDetector : Detector(), Detector.UastScanner {
    companion object {
        /** Issue describing the problem and pointing to the detector implementation */
        @JvmField
        val ISSUE: Issue = Issue.create(
            id = "SpaceMethod",
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
            override fun visitClass(node: UClass) {
                val text = node.parent.text
                val lines = text.lines()
                var beginPosition = 0
                lines.forEach { line ->
                    val length = line.length

                    val symbolsNotNeedAroundSpace = listOf(
                        ".",
                        "::",
                        "?."
                    )

                    symbolsNotNeedAroundSpace.forEach { symbol ->
                        if (line.contains(Regex("""\s$symbol\s"""))) {
                            val beforeIndex = line.indexOf(" $symbol")
                            val afterIndex = line.indexOf("$symbol ")

                            if (beforeIndex > 0 && afterIndex <= 0) {
                                context.report(
                                    MaxLineLengthDetector.ISSUE, node,
                                    context.getRangeLocation
                                        (node.parent, beginPosition + beforeIndex, symbol.length + 1),
                                    "$DELETE_SPACES_MESSAGE ${ISSUE.getExplanation(TextFormat.TEXT)}"
                                )
                            } else if (afterIndex > 0 && beforeIndex <= 0) {
                                context.report(
                                    MaxLineLengthDetector.ISSUE, node,
                                    context.getRangeLocation
                                        (node.parent, beginPosition + afterIndex, symbol.length + 1),
                                    "$DELETE_SPACES_MESSAGE ${ISSUE.getExplanation(TextFormat.TEXT)}"
                                )
                            } else if (afterIndex > 0 && beforeIndex > 0) {
                                context.report(
                                    MaxLineLengthDetector.ISSUE, node,
                                    context.getRangeLocation
                                        (node.parent, beginPosition + beforeIndex, symbol.length + 2),
                                    "$DELETE_SPACES_MESSAGE ${ISSUE.getExplanation(TextFormat.TEXT)}"
                                )
                            }
                        }
                    }

                    if (line.contains(RIGHT_FUNCTIONS_OPEN_SCOPE_REGEX)) {
                        val index = line.indexOf(" (")
                        if (index > 0) {
                            context.report(
                                MaxLineLengthDetector.ISSUE, node,
                                context.getRangeLocation(node.parent, beginPosition + index, 2),
                                "$DELETE_SPACES_MESSAGE ${ISSUE.getExplanation(TextFormat.TEXT)}"
                            )
                        }
                    }

                    if (!line.contains(RIGHT_END_FUNCTION_DECLARATION_REGEX)
                        && line.contains(END_FUNCTION_DECLARATION_REGEX)
                        && !line.matches(END_FUNCTION_DECLARATION_REGEX)
                    ) {
                        context.report(
                            MaxLineLengthDetector.ISSUE, node,
                            context.getRangeLocation(node.parent, beginPosition + length - 1, 1),
                            ISSUE.getExplanation(TextFormat.TEXT)
                        )
                    }

                    beginPosition += length
                    beginPosition++ // for adding new string symbol
                }
            }
        }
    }
}
/* symbolsNotNeedAroundSpace.forEach { symbol ->

                      if (line.contains(Regex("""\s*$symbol"""))) {
//                            val index = line.indexOf(" $symbol")
//                            if (index > 0) {
                              context.report(
                                  MaxLineLengthDetector.ISSUE,
                                  node,
                                  context.getRangeLocation(node.parent, beginPosition , 2),
                                  ISSUE.getExplanation(TextFormat.TEXT)
                              )
//                            }
                      }


                      if (line.contains(Regex("""$symbol\s*"""))) {
                          val index = line.indexOf("$symbol ")
                          if (index > 0) {
                              context.report(
                                  MaxLineLengthDetector.ISSUE,
                                  node,
                                  context.getRangeLocation(node.parent, beginPosition + index, 2),
                                  ISSUE.getExplanation(TextFormat.TEXT)
                              )
                          }
                      }
                  }*/
