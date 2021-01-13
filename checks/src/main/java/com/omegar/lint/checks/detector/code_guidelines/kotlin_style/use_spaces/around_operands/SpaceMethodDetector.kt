package com.omegar.lint.checks.detector.code_guidelines.kotlin_style.use_spaces.around_operands

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import org.jetbrains.uast.UClass
import org.jetbrains.uast.UElement

open class SpaceMethodDetector : Detector(), Detector.UastScanner {
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
		private val RIGHT_END_FUNCTION_DECLARATION_REGEX =
			Regex("""([a-z]|[A-Z]|"|'|\)|=|>|\?)\s\{$""")

		private val RIGHT_FUNCTIONS_OPEN_SCOPE_REGEX = Regex("""([a-z]|[A-Z]|\d)\s*\(""")

		private const val FUNCTION_VALUE = "fun"
		private const val OPEN_SCOPE_VALUE = "("
		private const val QUOTE_VALUE = "\""

		private val OPEN_BRACE_REGEX = Regex("""\s*\{""")
		private val OPEN_SCOPE_REGEX = Regex("""\s*\(""")

		private val KEY_SYMBOLS_ARRAY = arrayOf(".", "::", "?.")
		private val REGEXPS = KEY_SYMBOLS_ARRAY.map { it to Regex("""\s*$it\s*""") }.toMap()

		private val COMMENTS_REGEX = Regex("""([//]|[/\*])""")
	}

	override fun getApplicableUastTypes(): List<Class<out UElement?>> = listOf(UClass::class.java)

	override fun createUastHandler(context: JavaContext): UElementHandler {
		return object : UElementHandler() {

			override fun visitClass(node: UClass) {
				val text = node.parent?.text ?: return
				val lines = text.lines()
				var beginPosition = 0
				lines.forEach { line ->
					val length = line.length

					REGEXPS.forEach { pair ->
						if (line.contains(pair.value) && !line.contains(COMMENTS_REGEX)) {

							val beforeIndex = line.indexOf(" ${pair.key}")
							val afterIndex = line.indexOf("${pair.key} ")
							val match = pair.value.find(line)

							if (match != null  && (beforeIndex > 0 || afterIndex > 0)) {

								val index = if(beforeIndex > 0) {beforeIndex} else {afterIndex}

								makeContextReport(
									Params(
										context,
										node,
										beginPosition + 1,
										line.length - 1,
										line,
										0,
										"${pair.key} $beforeIndex $afterIndex ${match.value} \n $line \n $index"
									)
								)

								if (index > 0) {
									makeContextReport(
										Params(
											context,
											node,
											beginPosition + index,
											match.value.length,
											line,
											index
										)
									)
								}
							}
						}
					}

					checkSpaceForScopes(context, line, node, beginPosition)

					beginPosition += length
					beginPosition++ // for adding new string pair.key
				}
			}
		}
	}


	private fun checkSpaceForScopes(
		context: JavaContext,
		line: String,
		node: UClass,
		beginPosition: Int
	) {
		val length = line.length
		if (line.contains(RIGHT_FUNCTIONS_OPEN_SCOPE_REGEX) && line.contains(FUNCTION_VALUE)) {
			val functionLine = line.substring(0, line.indexOf(OPEN_SCOPE_VALUE) + 1)
			val spaceIndex = functionLine.indexOf(" $OPEN_SCOPE_VALUE")
			if (spaceIndex > 0) {
				val match = OPEN_SCOPE_REGEX.find(line)
				if (match != null) {
					val index = spaceIndex - match.value.length + 2
					makeContextReport(Params(context, node, beginPosition + index, match.value.length, line, index))
				}
			}
		}

		if (!line.contains(RIGHT_END_FUNCTION_DECLARATION_REGEX)
			&& line.contains(END_FUNCTION_DECLARATION_REGEX)
			&& !line.matches(END_FUNCTION_DECLARATION_REGEX)
		) {
			val match = OPEN_BRACE_REGEX.find(line)
			if (match != null) {
				val index = length - match.value.length
				makeContextReport(Params(context, node, beginPosition + index, match.value.length, line, index, " "))
			}
		}
	}

	private fun makeContextReport(params: Params) {
		if (!isInQuote(params.line, params.index)) {

			params.context.report(
				ISSUE,
				params.node,
				params.context.getRangeLocation(
					params.node.parent,
					params.beginPosition,
					params.length
				),
				params.toReplaceString/*ISSUE.getExplanation(TextFormat.TEXT)*/,
				createLintFix(params.line, params.index, params.length, params.toReplaceString)
			)
		}
	}

	private fun isInQuote(line: String, index: Int): Boolean {
		val chars = line.toCharArray()
		var inside = false
		for (i in chars.indices) {
			val char = chars[i]
			if (char.toString() == QUOTE_VALUE) {
				inside = !inside
			}

			if (i == index) {
				return inside
			}
		}

		return false
	}

	private fun createLintFix(line: String, index: Int, length: Int, toReplaceString: String): LintFix {
		val substringWithSpace = line.substring(index, index + length)
		return LintFix.create()
			.replace()
			.text(substringWithSpace)
			.with(substringWithSpace.trim() + toReplaceString)
			.build()
	}

	class Params(
		val context: JavaContext,
		val node: UClass,
		val beginPosition: Int,
		val length: Int,
		val line: String,
		val index: Int,
		val toReplaceString: String = ""
	)
}