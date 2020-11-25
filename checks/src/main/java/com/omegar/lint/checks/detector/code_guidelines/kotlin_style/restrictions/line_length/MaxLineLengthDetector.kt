package com.omegar.lint.checks.detector.code_guidelines.kotlin_style.restrictions.line_length

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*

import org.jetbrains.uast.UClass
import org.jetbrains.uast.UElement

class MaxLineLengthDetector : Detector(), Detector.UastScanner {
	companion object {
		/** Issue describing the problem and pointing to the detector implementation */
		@JvmField
		val ISSUE: Issue = Issue.create(
            id = "OMEGA_NOT_EXCEED_MAX_LINE_LENGTH",
            briefDescription = "The line size does not match the coding convention",
            explanation = """
                  Line should has 130 symbols or less. Divide this line.
                  http://wiki.omega-r.club/dev-android-code#rec228180723
                    """,
            category = Category.CORRECTNESS,
            priority = 7,
            severity = Severity.WARNING,
            implementation = Implementation(
                MaxLineLengthDetector::class.java,
                Scope.JAVA_FILE_SCOPE
            )
        )

		private const val IMPORT_VAL = "import"
		private const val PACKAGE_VAL = "package"

		internal const val MAX_LENGTH = 130
	}

	override fun getApplicableUastTypes(): List<Class<out UElement?>>? {
		return listOf(UClass::class.java)
	}

	override fun createUastHandler(context: JavaContext): UElementHandler? {
		return object : UElementHandler() {
			override fun visitClass(node: UClass) {
				val text = node.parent.text
				val lines = text.lines()
				var beginPosition = 0

				lines.forEach { line ->
					val length = line.length

					if (length > MAX_LENGTH && !line.contains(IMPORT_VAL) && !line.contains(PACKAGE_VAL)) {
						context.report(
                            ISSUE,
                            node,
                            context.getRangeLocation(node.parent, beginPosition, length),
                            ISSUE.getExplanation(TextFormat.TEXT)
                        )
					}

					beginPosition += length
					beginPosition++ // for adding new string symbol
				}
			}
		}
	}
}

