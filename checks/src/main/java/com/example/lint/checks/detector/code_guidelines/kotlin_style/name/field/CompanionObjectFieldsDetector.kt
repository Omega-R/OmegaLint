package com.example.lint.checks.detector.code_guidelines.kotlin_style.name.field

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*

import org.jetbrains.uast.UClass
import org.jetbrains.uast.UElement

class CompanionObjectFieldsDetector : Detector(), Detector.UastScanner {
	companion object {
		/** Issue describing the problem and pointing to the detector implementation */
		@JvmField
		val ISSUE: Issue = Issue.create(
			id = "OMEGA_NAME_CONSTANTS_CORRECTLY",
			briefDescription = "The line size does not match the coding convention",
			explanation = """
                  The immutable fields in the Companion Object and compile-time constants are named in the 
                  SCREAMING_SNAKE_CASE style.
                  http://wiki.omega-r.club/dev-android-code#rec226457239
                    """,
			category = Category.CORRECTNESS,
			priority = 7,
			severity = Severity.WARNING,
			implementation = Implementation(
				CompanionObjectFieldsDetector::class.java,
				Scope.JAVA_FILE_SCOPE
			)
		)

		private const val CONST_VAL_LABEL = "const val"
		private const val VAL_LABEL = "val"
		private const val COMPANION_NAME_LABEL = "Companion"


		private val UPPER_REGEX = Regex("""^([A-Z]*_*)*$""")

	}

	override fun getApplicableUastTypes(): List<Class<out UElement?>>? {
		return listOf(UClass::class.java)
	}

	override fun createUastHandler(context: JavaContext): UElementHandler? {
		return object : UElementHandler() {
			override fun visitClass(node: UClass) {

				val innerClass = node.innerClasses.firstOrNull() ?: return
				val name = innerClass.name ?: return

				if (name != COMPANION_NAME_LABEL) {
					return
				}

				val declarations = innerClass.uastDeclarations.distinctBy { it.text }

				declarations.forEach { declaration ->
					val text = declaration.text ?: return
					val lines = text.lines()
					lines.forEach { line ->
						if (isIncorrectConstantName(line)) {
							context.report(
								ISSUE,
								node,
								context.getNameLocation(declaration),
								"$line\n${ISSUE.getExplanation(TextFormat.TEXT)}"
							)
						}
					}
				}
			}
		}
	}

	private fun isIncorrectConstantName(line: String): Boolean {
		if (!line.contains(CONST_VAL_LABEL)) {
			return false
		}
		val substrings = line.split(" ")

		val valIndex = substrings.indexOf(VAL_LABEL)
		if (valIndex == substrings.size - 1 || valIndex <= 0) {
			return false
		}

		val identifierName = substrings[valIndex + 1]

		return !identifierName.matches(UPPER_REGEX)
	}
}

