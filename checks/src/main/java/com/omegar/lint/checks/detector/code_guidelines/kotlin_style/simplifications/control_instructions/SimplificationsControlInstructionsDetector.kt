package com.omegar.lint.checks.detector.code_guidelines.kotlin_style.simplifications.control_instructions

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import com.omegar.lint.checks.detector.code_guidelines.kotlin_style.restrictions.line_length.MaxLineLengthDetector.Companion.MAX_LENGTH
import org.jetbrains.uast.*

class SimplificationsControlInstructionsDetector : Detector(), Detector.UastScanner {
	companion object {
		/** Issue describing the problem and pointing to the detector implementation */
		@JvmField
		val ISSUE: Issue = Issue.create(
			id = "OMEGA_CAN_USE_SIMPLIFICATION_OF_CONTROL_INSTRUCTIONS",
			briefDescription = "Place the short branches on the same line as the condition, without parentheses.",
			explanation = """
                  Place the short branches on the same line as the condition, without parentheses.
                  http://wiki.omega-r.club/dev-android-code#rec228389564
                    """,
			category = Category.CORRECTNESS,
			priority = 7,
			severity = Severity.INFORMATIONAL,
			implementation = Implementation(
				SimplificationsControlInstructionsDetector::class.java,
				Scope.JAVA_FILE_SCOPE
			)
		)

		private val EMPTY_BRANCH_REGEX = Regex("""\{\s*\}""")
		private val END_SPACE_REGEX = Regex("""\s*$""")
		private val MORE_THAN_ONE_SPACE_REGEX = Regex("""\s+""")

		private const val ELSE_LABEL = "-> {"
		private const val ELSE_TEXT = "else -> {"
		private const val DELTA = 2
		private const val OPEN_SCOPE_SYMBOL = "{"
		private const val CLOSE_SCOPE_SYMBOL = "}"
		private const val EQUALS_SYMBOL = "="
		private const val NEW_LINE_SYMBOL = "\n"
		private const val SPACE_SYMBOL = " "

	}

	override fun getApplicableUastTypes(): List<Class<out UElement?>> = listOf(USwitchClauseExpression::class.java)

	override fun createUastHandler(context: JavaContext): UElementHandler {
		return object : UElementHandler() {
			override fun visitSwitchClauseExpression(node: USwitchClauseExpression) {
				val renderText = node.asRenderString()

				if (renderText.trim().split("\n").size > 3 || renderText.contains(EMPTY_BRANCH_REGEX)) {
					return
				}

				val method = node.getContainingUMethod() ?: return
				val text = method.text ?: return
				var firstText = renderText.trim().lines().firstOrNull() ?: return

				if (firstText == ELSE_LABEL) {
					firstText = ELSE_TEXT
				}

				if (text.contains(firstText) && exceedMaxLineLength(renderText)) {
					context.report(
						ISSUE,
						node,
						context.getLocation(node),
						ISSUE.getExplanation(TextFormat.TEXT)
					)
				}
			}
		}
	}

	private fun exceedMaxLineLength(text: String): Boolean {
		val lines = text.lines()
		if (lines.size > 1) {
			return lines[0].length + lines[1].trim().length - DELTA < MAX_LENGTH
		}
		return false
	}

	private fun createFix(text: String): LintFix? {
		return fix()
			.replace()
			.text(text)
			.with(getSimpleString(text))
			.build()
	}

	private fun getSimpleString(text: String): String {
		return text
			.replace(OPEN_SCOPE_SYMBOL, EQUALS_SYMBOL)
			.replace(NEW_LINE_SYMBOL, "")
			.replace(CLOSE_SCOPE_SYMBOL, "")
			.replace(END_SPACE_REGEX, "")
			.replace(MORE_THAN_ONE_SPACE_REGEX, SPACE_SYMBOL)
	}

}
