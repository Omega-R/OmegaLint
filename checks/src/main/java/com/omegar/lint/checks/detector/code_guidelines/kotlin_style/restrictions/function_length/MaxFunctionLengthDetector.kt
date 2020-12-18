package com.omegar.lint.checks.detector.code_guidelines.kotlin_style.restrictions.function_length

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import com.intellij.util.containers.ContainerUtil.findAll
import org.jetbrains.uast.UElement
import org.jetbrains.uast.UMethod

class MaxFunctionLengthDetector : Detector(), Detector.UastScanner {
	companion object {
		/** Issue describing the problem and pointing to the detector implementation */
		@JvmField
		val ISSUE: Issue = Issue.create(
			id = "OMEGA_NOT_EXCEED_MAX_FUNCTION_LENGTH",
			briefDescription =
			"The size of a function should be no more than 30 lines, excluding blank lines and comments." +
					" Large functions are difficult to read, modify, and test",
			explanation = """
                  Function size must be less than 30 lines(max value change to 40 lines if you use "when")
                  http://wiki.omega-r.club/dev-android-code#rec228194333
                    """,
			category = Category.CORRECTNESS,
			priority = 7,
			severity = Severity.WARNING,
			implementation = Implementation(
				MaxFunctionLengthDetector::class.java,
				Scope.JAVA_FILE_SCOPE
			)
		)

		private val WHEN_VAL = Regex("""(switch|when)""")
		private const val CLASS_VAL = "class"
		private const val MAX_FUNCTION_LINES_COUNT = 30
		private const val MAX_FUNCTION_LINES_COUNT_WITH_WHEN = 40
		private const val ANNOTATION_SYMBOL = "@"
		private const val DELTA = 2
		private val COMMENT_REGEX = Regex("""(/\*|//)""")
		private val COMMENTS_INSIDE_REGEX = Regex("""(//.*\n|/\*(.|\n)*\*/|/(.|\n)*/)""")
	}

	override fun getApplicableUastTypes(): List<Class<out UElement?>> = listOf(UMethod::class.java)


	override fun createUastHandler(context: JavaContext): UElementHandler {
		return object : UElementHandler() {
			override fun visitMethod(node: UMethod) {
				val text = node.text ?: return

				val textWithoutComments = deleteComments(text)

				if (isClass(textWithoutComments.lines())) {
					return
				}
				val body = node.uastBody ?: return
				var currentMax = MAX_FUNCTION_LINES_COUNT

				if (textWithoutComments.contains(WHEN_VAL)) {
					currentMax = MAX_FUNCTION_LINES_COUNT_WITH_WHEN
				}
				val lines = getLines(textWithoutComments.lines())

				/** Need to delete 2 strings, because body has "{ }" */
				val size = lines.size - DELTA

				if (size > currentMax) {
					context.report(
						ISSUE,
						node,
						context.getLocation(body),
						"FUN SIZE: $size\n${ISSUE.getExplanation(TextFormat.TEXT)}"
					)
				}
			}
		}
	}

	private fun deleteComments(text: String): String {
		val commentsList = COMMENTS_INSIDE_REGEX.findAll(text)
		var resultText = ""
		commentsList.forEach {
			resultText = text.replace(it.value, "")
		}
		return resultText
	}


	private fun getLines(lines: List<String>): List<String> {
		val resultLines = mutableListOf<String>()
		lines.forEach {
			if (it.trim().isNotEmpty()) {
				resultLines.add(it)
			}
		}
		return resultLines
	}

	private fun isClass(lines: List<String>): Boolean {
		var firstLine = lines.firstOrNull() ?: return false
		if (firstLine.contains(ANNOTATION_SYMBOL)) {
			firstLine = lines[1]
		}
		if (firstLine.contains(CLASS_VAL) || firstLine.contains(COMMENT_REGEX)) {
			return true
		}
		return false
	}
}