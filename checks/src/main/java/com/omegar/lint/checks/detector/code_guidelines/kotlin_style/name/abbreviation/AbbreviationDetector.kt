package com.omegar.lint.checks.detector.code_guidelines.kotlin_style.name.abbreviation

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import org.jetbrains.uast.UDeclaration
import org.jetbrains.uast.UElement

@Suppress("UnstableApiUsage")
class AbbreviationDetector : Detector(), Detector.UastScanner {
	companion object {
		/** Issue describing the problem and pointing to the detector implementation */
		@JvmField
		val ISSUE: Issue = Issue.create(
			id = "OMEGA_ABBREVIATION_AS_WORD",
			briefDescription = "Use this abbreviation does not match the coding convention",
			explanation = """
                  Don't use abbreviations.
                  http://wiki.omega-r.club/dev-android-code#rec228153340
                    """,
			category = Category.CORRECTNESS,
			severity = Severity.WARNING,
			implementation = Implementation(
				AbbreviationDetector::class.java,
				Scope.JAVA_FILE_SCOPE
			)
		)

		private val ABBREVIATION_REGEX = Regex("""[A-Z][A-Z]""")
		private val ANNOTATION_REGEX = Regex("""^@""")
		private const val OPEN_SCOPE_LABEL = "("
		private const val EQUAL_LABEL = "="
		private const val SPACE_LABEL = " "

		//exclusion
		private const val MILLISECONDS_LABEL = "MSec"
		private const val UELEMENT_LABEL = "UElement"
		private const val TODO_LABEL = "TODO"
		private const val CLASS_LABEL = "class"
		private const val ENUM_LABEL = "enum class"

		val exclusionsList = listOf(
			MILLISECONDS_LABEL,
			TODO_LABEL,
			UELEMENT_LABEL
		)

	}

	override fun getApplicableUastTypes(): List<Class<out UElement?>> = listOf(UDeclaration::class.java)

	override fun createUastHandler(context: JavaContext): UElementHandler {
		return object : UElementHandler() {
			override fun visitDeclaration(node: UDeclaration) {
				val parent = node.parent ?: return
				val fileLines = parent.text.lines()
				val nameLine = fileLines.firstOrNull { it.contains(CLASS_LABEL) }

				if (nameLine != null && nameLine.contains(ENUM_LABEL)) {
					return
				}

				val lines = node.text?.lines() ?: return

				var checkText = getNameString(lines) ?: return

				checkText = deleteAfterSymbol(checkText, EQUAL_LABEL)
				checkText = deleteAfterSymbol(checkText, OPEN_SCOPE_LABEL)

				checkText = deleteExclusions(checkText)

				if (checkText.contains(ABBREVIATION_REGEX) && !node.isStatic) {
					context.report(
						ISSUE,
						node as UElement,
						context.getNameLocation(node),
						checkText + "\n" + ISSUE.getExplanation(TextFormat.TEXT)
					)
				}
			}
		}

	}

	private fun deleteAfterSymbol(checkText: String, symbol: String): String {
		var text = checkText
		if (text.indexOf(symbol) > 0) {
			text = checkText.substring(0, checkText.indexOf(symbol))
		}
		return text
	}

	private fun getNameString(lines: List<String>): String? {
		lines.forEach { line ->
			if (!line.contains(ANNOTATION_REGEX)) {
				return line
			}
		}
		return null
	}

	private fun deleteExclusions(checkText: String): String {
		var resultText = checkText
		exclusionsList.forEach {
			resultText = resultText.replace(it, SPACE_LABEL)
		}
		return resultText
	}
}
