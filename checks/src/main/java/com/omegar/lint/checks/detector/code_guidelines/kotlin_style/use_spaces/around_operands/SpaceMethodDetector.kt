package com.omegar.lint.checks.detector.code_guidelines.kotlin_style.use_spaces.around_operands

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
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
		private val POINT_BEGIN_REGEX = Regex("""^\s*\.""")
		private val QUESTION_MARK_POINT_BEGIN_REGEX = Regex("""^\s*\?\.""")

		private const val DELETE_SPACES_MESSAGE = "Remove extra spaces."
		private const val FUNCTION_VALUE = "fun"
		private const val OPEN_SCOPE_VALUE = "("

		private val CHAR_ARRAY = arrayOf(".", "::", "?.")
		private val REGEXPS = CHAR_ARRAY.map { it to Regex("""\s*$it\s""") }.toMap()
	}

	override fun getApplicableUastTypes(): List<Class<out UElement?>>? {
		return listOf(UClass::class.java)
	}

	override fun createUastHandler(context: JavaContext): UElementHandler? {
		return object : UElementHandler() {

			override fun visitClass(node: UClass) {
				val text = node.parent.text ?: return
				val lines = text.lines()
				var beginPosition = 0
				lines.forEach { line ->
					val length = line.length

					REGEXPS.forEach { pair ->
						if (line.contains(pair.value)) {

							val beforeIndex = line.indexOf(" ${pair.key}")
							val afterIndex = line.indexOf("${pair.key} ")

							when {
								beforeIndex > 0
										&& afterIndex <= 0
										&& !line.contains(POINT_BEGIN_REGEX)
										&& !line.contains(QUESTION_MARK_POINT_BEGIN_REGEX) -> {
									makeContextReport(node, beginPosition + beforeIndex, pair.key.length + 1)
								}

								afterIndex > 0 && beforeIndex <= 0 -> {
									makeContextReport(node, beginPosition + afterIndex, pair.key.length + 1)
								}

								afterIndex > 0 && beforeIndex > 0 -> {
									makeContextReport(node, beginPosition + beforeIndex, pair.key.length + 2)
								}
							}
						}
					}

					if (line.contains(RIGHT_FUNCTIONS_OPEN_SCOPE_REGEX) && line.contains(FUNCTION_VALUE)) {
						val functionLine = line.substring(0 , line.indexOf(OPEN_SCOPE_VALUE) + 1)
						val index = functionLine.indexOf(" (")
						if (index > 0) {
							makeContextReport(node, beginPosition + index, 2)
						}
					}

					if (!line.contains(RIGHT_END_FUNCTION_DECLARATION_REGEX)
						&& line.contains(END_FUNCTION_DECLARATION_REGEX)
						&& !line.matches(END_FUNCTION_DECLARATION_REGEX)
					) {
						makeContextReport(node, beginPosition + length - 1, 1)
					}

					beginPosition += length
					beginPosition++ // for adding new string pair.key
				}
			}

			private fun makeContextReport(node: UClass, beginPosition: Int, length: Int) {
				context.report(
					ISSUE,
					node,
					context.getRangeLocation(node.parent, beginPosition, length),
					ISSUE.getExplanation(TextFormat.TEXT)
				)
			}
		}
	}
}