package com.omegar.lint.checks.detector.code_guidelines.kotlin_style.restrictions.function_length

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import org.jetbrains.uast.UClass
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

		private const val WHEN_VAL = "switch"
		private const val CLASS_VAL = "class"
		private const val MAX_FUNCTION_LINES_COUNT = 30
		private const val MAX_FUNCTION_LINES_COUNT_WITH_WHEN = 40
		private const val DELTA = 2
	}

	override fun getApplicableUastTypes(): List<Class<out UElement?>>? {
		return listOf(UMethod::class.java)
	}

	override fun createUastHandler(context: JavaContext): UElementHandler? {
		return object : UElementHandler() {
			override fun visitMethod(node: UMethod) {
				val text = node.text ?: return
				if(isClass(text.split("\n").firstOrNull())) {
					return
				}
				val body = node.uastBody ?: return
				var currentMax = MAX_FUNCTION_LINES_COUNT

				if (text.contains(WHEN_VAL)) {
					currentMax = MAX_FUNCTION_LINES_COUNT_WITH_WHEN
				}
				val lines = getLines(text.lines())


				/** Need to delete 2 strings, because body has "{ }" */
				val size = lines.size - DELTA

				if (size > currentMax)  {
					context.report(ISSUE, node, context.getLocation(body), "FUN SIZE: $size\n${ISSUE.getExplanation(TextFormat.TEXT)}")
				}
			}
		}
	}

	private fun getLines(lines: List<String>): List<String> {
		var resultLines = mutableListOf<String>()
		lines.forEach {
			if(!it.trim().isNullOrEmpty()) {
				resultLines.add(it)
			}
		}
		return  resultLines
	}

	private fun isClass(firstString: String?): Boolean {
		if(firstString == null) {
			return false
		}
		if(firstString.contains(CLASS_VAL)) {
			return true
		}
		return false
	}
}