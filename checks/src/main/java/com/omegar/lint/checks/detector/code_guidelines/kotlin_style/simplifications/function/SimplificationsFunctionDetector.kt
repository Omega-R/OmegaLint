package com.omegar.lint.checks.detector.code_guidelines.kotlin_style.simplifications.function

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import com.omegar.lint.checks.detector.code_guidelines.kotlin_style.restrictions.line_length.MaxLineLengthDetector.Companion.MAX_LENGTH
import org.jetbrains.uast.UElement
import org.jetbrains.uast.UMethod

class SimplificationsFunctionDetector : Detector(), Detector.UastScanner {
	companion object {
		/** Issue describing the problem and pointing to the detector implementation */
		@JvmField
		val ISSUE: Issue = Issue.create(
            "OMEGA_CAN_USE_EXPRESSION_FUNCTION",
            "When a function contains only one expression, it can be represented as an \"expression function\".",
            """
                  You can change it to "expression function"
                  http://wiki.omega-r.club/dev-android-code#rec228389255
                    """,
            Category.CORRECTNESS,
            7,
            Severity.INFORMATIONAL,
            Implementation(
                SimplificationsFunctionDetector::class.java,
                Scope.JAVA_FILE_SCOPE
            )
        )

		private val ONE_EXPRESSION_REGEX = Regex("""\{\s*return\s*.*""")
		private val RETURN_REGEX = Regex("""\s*return""")
		private const val MAX_LINE_COUNT_IN_EXPRESSION_FUNCTION = 3

		private const val OPEN_SCOPE_SYMBOL = "{"
		private const val CLOSE_SCOPE_SYMBOL = "}"
		private const val EQUALS_SYMBOL = "="
		private const val NEW_LINE_SYMBOL = "\n"
		private const val SPACE_SYMBOL = " "

		private val END_SPACE_REGEX = Regex("""\s*$""")
		private val MORE_THAN_ONE_SPACE_REGEX = Regex("""\s+""")
	}

	override fun getApplicableUastTypes(): List<Class<out UElement?>> = listOf(UMethod::class.java)

	override fun createUastHandler(context: JavaContext): UElementHandler {
		return object : UElementHandler() {
			override fun visitMethod(node: UMethod) {
				val text = node.text ?: return
				val linesCount = text.count { it == '\n' } + 1
				if (linesCount <= MAX_LINE_COUNT_IN_EXPRESSION_FUNCTION
					&& text.contains(ONE_EXPRESSION_REGEX)
				) {
					val newText = getSimpleFunctionString(text)

					if (newText.length > MAX_LENGTH) return

					context.report(
                        ISSUE,
                        node,
                        context.getLocation(node),
                        ISSUE.getExplanation(TextFormat.TEXT),
                        createFix(text, newText)
                    )
				}
			}
		}
	}

	private fun createFix(text: String, newText: String): LintFix? {
		return fix()
			.replace()
			.text(text)
			.with(newText)
			.build()
	}

	private fun getSimpleFunctionString(text: String): String {
		return text
			.replace(OPEN_SCOPE_SYMBOL, EQUALS_SYMBOL)
			.replace(RETURN_REGEX, "")
			.replace(NEW_LINE_SYMBOL, "")
			.replace(CLOSE_SCOPE_SYMBOL, "")
			.replace(END_SPACE_REGEX, "")
			.replace(MORE_THAN_ONE_SPACE_REGEX, SPACE_SYMBOL)
	}

}