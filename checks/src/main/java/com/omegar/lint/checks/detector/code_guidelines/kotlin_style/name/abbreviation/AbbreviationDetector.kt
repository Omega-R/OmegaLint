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
		private const val OPEN_SCOPE_LABEL = "("
		private const val EQUAL_LABEL = "="

		//exclusion
		private const val MILLISECONDS_LABEL = "MSec"
		private const val RETROFIT_GET_LABEL = "@retrofit2.http.GET"
		private const val RETROFIT_POST_LABEL = "@retrofit2.http.POST"
		private const val RETROFIT_PUT_LABEL = "@retrofit2.http.PUT"
		private const val RETROFIT_DELETE_LABEL = "@retrofit2.http.DELETE"

		val exclusionsList = listOf(
			MILLISECONDS_LABEL,
			RETROFIT_GET_LABEL,
			RETROFIT_POST_LABEL,
			RETROFIT_PUT_LABEL,
			RETROFIT_DELETE_LABEL
		)

	}

	override fun getApplicableUastTypes(): List<Class<out UElement?>> {
		return listOf(
			UDeclaration::class.java
		)
	}

	override fun createUastHandler(context: JavaContext): UElementHandler {
		return object : UElementHandler() {
			override fun visitDeclaration(node: UDeclaration) {
				val renderText = node.text?.lines()?.firstOrNull() ?: return
				var checkText = renderText

				checkText = deleteAfterSymbol(checkText, EQUAL_LABEL)
				checkText = deleteAfterSymbol(checkText, OPEN_SCOPE_LABEL)

				checkText = deleteExclusions(checkText)

				if (checkText.contains(ABBREVIATION_REGEX) && !node.isStatic) {
					context.report(
						ISSUE,
						node as UElement,
						context.getNameLocation(node),
						checkText
					)
				}
			}

			private fun deleteAfterSymbol(checkText: String, symbol: String): String {
				var text = checkText
				if (text.indexOf(symbol) > 0) {
					text = checkText.substring(0, checkText.indexOf(symbol))
				}
				return text
			}
		}

	}

	private fun deleteExclusions(checkText: String): String {
		var resultText = checkText
		exclusionsList.forEach {
			resultText = resultText.replace(it, " ")
		}
		return resultText
	}


}
