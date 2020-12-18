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
			"When declaring parameter names in a multi-line lambda," +
					" put the names on the first line, followed by an arrow and a new line:",
			explanation = """
                  When declaring parameter names in a multi-line lambda, 
                  put the names on the first line, followed by an arrow and a new line:
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
		private const val QUOTE_VAL = "\""
		private val EMPTY_ARROW_REGEX = Regex("""^\s*->""")
		private val SWITCH_VAL = Regex("""(switch|when)""")
	}

	override fun getApplicableUastTypes(): List<Class<out UElement?>> = listOf(UClass::class.java)

	override fun createUastHandler(context: JavaContext): UElementHandler {
		return object : UElementHandler() {
			override fun visitClass(node: UClass) {
				val text = node.parent?.text ?: return
				val lines = text.lines()
				var beginPosition = 0

				for (i in lines.indices) {
					val line = lines[i]
					val length = line.length

					if (line.contains(LAMBDA_VAL) && !line.contains(QUOTE_VAL)) {
						val params = Params(context, node, lines, line, beginPosition, i)
						checkLambda(params)
					}

					beginPosition += length
					beginPosition++ // for adding new string symbol
				}
			}
		}
	}

	private fun checkLambda(params: Params) {
		val length = params.line.length
		if ((params.line.matches(EMPTY_ARROW_REGEX))) {
			params.context.report(
				ISSUE,
				params.node,
				params.context.getRangeLocation(params.node.parent, params.beginPosition, length),
				ISSUE.getExplanation(TextFormat.TEXT)
			)
		} else if (params.index - 1 >= 0) {
			val previousLine = params.lines[params.index - 1]
			if (previousLine.contains(OPEN_SCOPE_VAL)
				&& !previousLine.contains(CLOSE_SCOPE_VAL)
				&& !previousLine.contains(SWITCH_VAL)
				&& previousLine.length + params.line.trim().length < MAX_LENGTH
				&& !params.line.contains(OPEN_SCOPE_VAL)
			) {
				params.context.report(
					ISSUE,
					params.node,
					params.context.getRangeLocation(params.node.parent, params.beginPosition, length),
					ISSUE.getExplanation(TextFormat.TEXT)
				)
			}
		}
	}

	private class Params(
		val context: JavaContext,
		val node: UClass,
		val lines: List<String>,
		val line: String,
		val beginPosition: Int,
		val index: Int
	)
}

