package com.omegar.lint.checks.detector.code_guidelines.kotlin_style.elements_formating.lambda

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import com.omegar.lint.checks.detector.code_guidelines.kotlin_style.restrictions.line_length.MaxLineLengthDetector.Companion.MAX_LENGTH

import org.jetbrains.uast.UClass
import org.jetbrains.uast.UElement

class LambdaDetector : Detector(), Detector.UastScanner {
	companion object {
		/** Issue describing the problem and pointing to the detector implementation */
		@JvmField
		val ISSUE: Issue = Issue.create(
			id = "OMEGA_USE_MULTI_LINE_LAMBDA_IN_ONE_LINE",
			briefDescription = "When declaring parameter names in a multi-line lambda, put the names on the first line, followed by an arrow and a new line:",
			explanation = """
                  When declaring parameter names in a multi-line lambda, put the names on the first line, followed by an arrow and a new line:
                  http://wiki.omega-r.club/dev-android-code#rec228389710
                    """,
			category = Category.CORRECTNESS,
			priority = 7,
			severity = Severity.WARNING,
			implementation = Implementation(
				LambdaDetector::class.java,
				Scope.JAVA_FILE_SCOPE
			)
		)

		private const val LAMBDA_VAL = "->"
		private const val CLOSE_SCOPE_VAL = "}"
		private const val OPEN_SCOPE_VAL = "{"
		private val EMPTY_ARROW_REGEX = Regex("""^\s*->""")
		private val SWITCH_VAL = Regex("""(switch|when)""")
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

				for (i in lines.indices) {
					val line = lines[i]
					val length = line.length

					if (line.contains(LAMBDA_VAL)) {
						if ((line.matches(EMPTY_ARROW_REGEX))) {
							context.report(
								ISSUE,
								node,
								context.getRangeLocation(node.parent, beginPosition, length),
								line + "\n" + ISSUE.getExplanation(TextFormat.TEXT)
							)
						} else if (i - 1 >= 0) {
							val previousLine = lines[i - 1]
							if (previousLine.contains(OPEN_SCOPE_VAL)
								&& !line.contains(CLOSE_SCOPE_VAL)
								&& !previousLine.contains(CLOSE_SCOPE_VAL)
								&& !previousLine.contains(SWITCH_VAL)
								&& previousLine.length + line.trim().length < MAX_LENGTH
							) {
								context.report(
									ISSUE,
									node,
									context.getRangeLocation(node.parent, beginPosition, length),
									previousLine + "\n" + previousLine.contains(CLOSE_SCOPE_VAL) + "\n" + ISSUE.getExplanation(TextFormat.TEXT)
								)
							}
						}
					}

					beginPosition += length
					beginPosition++ // for adding new string symbol
				}
			}
		}
	}
}

