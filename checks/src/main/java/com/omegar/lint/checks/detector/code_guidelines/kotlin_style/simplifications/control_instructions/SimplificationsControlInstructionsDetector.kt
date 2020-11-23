package com.omegar.lint.checks.detector.code_guidelines.kotlin_style.simplifications.control_instructions

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
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

		private val WHEN_REGEX = Regex("""^(switch|when)""")
		private val BEGIN_BRANCH_OF_WHEN_REGEX = Regex("""->\s*\{""")
		private val END_BRANCH_OF_WHEN_REGEX = Regex("""^\s*\}""")

		private const val ELSE_LABEL = "-> {"
		private const val ELSE_TEXT = "else -> {"

	}

	override fun getApplicableUastTypes(): List<Class<out UElement?>>? {
		return listOf(USwitchClauseExpression::class.java)
	}

	override fun createUastHandler(context: JavaContext): UElementHandler? {
		return object : UElementHandler() {
			override fun visitSwitchClauseExpression(node: USwitchClauseExpression) {
				val renderText = node.asRenderString()

				if (renderText.trim().split("\n").size != 3) {
					return
				}

				val method = node.getContainingUMethod() ?: return
				val text = method.text ?: return
				var firstText = renderText.trim().split("\n").firstOrNull() ?: return

				if (firstText == ELSE_LABEL) {
					firstText = ELSE_TEXT
				}

				if (text.contains(firstText))
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
