package com.example.lint.checks.detector.code_guidelines.kotlin_style.simplifications.control_instructions

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import org.jetbrains.uast.UElement
import org.jetbrains.uast.UExpression

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

		private val WHEN_REGEX = Regex("""^switch""")
		private val BEGIN_BRANCH_OF_WHEN_REGEX = Regex("""->\s*\{""")
		private val END_BRANCH_OF_WHEN_REGEX = Regex("""^\s*\}""")

	}

	override fun getApplicableUastTypes(): List<Class<out UElement?>>? {
		return listOf(UExpression::class.java)
	}

	override fun createUastHandler(context: JavaContext): UElementHandler? {
		return object : UElementHandler() {
			override fun visitExpression(node: UExpression) {
				val text = node.asRenderString()
				if (text.contains(WHEN_REGEX)) {
					val body = text.split("\n")
					var beginPosition = 0

					for (i in body.indices) {
						val line = body[i]
						if (line.contains(BEGIN_BRANCH_OF_WHEN_REGEX)) {
							if (i + 2 < body.size) {
								val endBodyLine = body[i + 2]
								if (endBodyLine.contains(END_BRANCH_OF_WHEN_REGEX)) {
									context.report(
										ISSUE,
										node,
										context.getRangeLocation(node, beginPosition - 1, line.trim().length),
										ISSUE.getExplanation(TextFormat.TEXT)
									)
								}
							}
						}
						beginPosition += line.length
						beginPosition++
					}

				}
			}
		}
	}
}