package com.omegar.lint.checks.detector.code_guidelines.kotlin_style.elements_formating.annotation

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import org.jetbrains.uast.UClass
import org.jetbrains.uast.UElement

class AnnotationDetector : Detector(), Detector.UastScanner {
	companion object {
		/** Issue describing the problem and pointing to the detector implementation */
		@JvmField
		val ISSUE: Issue = Issue.create(
			"OMEGA_SINGLE_LINE_ANNOTATION",
			"If there are multiple annotations for a class / field / method, place each annotation on a new line",
			"""
                  If there are multiple annotations for a class / field / method, place each annotation on a new line.
                  http://wiki.omega-r.club/dev-android-code#rec228389852
                    """,
			Category.CORRECTNESS,
			7,
			Severity.INFORMATIONAL,
			Implementation(
				AnnotationDetector::class.java,
				Scope.JAVA_FILE_SCOPE
			)
		)

		private val ONE_EXPRESSION_REGEX = Regex("""\s*@\s*([a-z]|[A-Z]|["]|[']|[(]|[)]|[=]|[\s]|[_])*@""")
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

					if (line.contains(ONE_EXPRESSION_REGEX)) {
						context.report(
							ISSUE,
							node,
							context.getRangeLocation(node.parent, beginPosition, length),
							ISSUE.getExplanation(TextFormat.TEXT),
							createFix(line)
						)
					}

					beginPosition += line.length
					beginPosition++ // for adding new string symbol

				}
			}
		}
	}

	private fun createFix(line: String): LintFix {
		var newLine = ""
		val charArray = line.toCharArray()
		charArray.forEach {
			if (it == '@') {
				newLine += "\n"
			}
			newLine += it
		}
		return fix()
			.replace()
			.text(line)
			.with(newLine)
			.build()
	}
}