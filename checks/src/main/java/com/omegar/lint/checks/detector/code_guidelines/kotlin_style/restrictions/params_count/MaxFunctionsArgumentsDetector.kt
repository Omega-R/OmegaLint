package com.omegar.lint.checks.detector.code_guidelines.kotlin_style.restrictions.params_count

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import org.jetbrains.uast.UElement
import org.jetbrains.uast.UMethod

class MaxFunctionsArgumentsDetector : Detector(), Detector.UastScanner {
	companion object {
		/** Issue describing the problem and pointing to the detector implementation */
		@JvmField
		val ISSUE: Issue = Issue.create(
            id = "OMEGA_NOT_EXCEED_MAX_ARGUMENTS_COUNT_IN_FUNCTION",
            briefDescription = "Arguments count does not match the coding convention. Function body should not be empty.",
            explanation = """
                  Method has too much arguments. In functions, the number of parameters must not exceed 5.
                  http://wiki.omega-r.club/dev-android-code#rec228191623
                    """,
            category = Category.CORRECTNESS,
            priority = 7,
            severity = Severity.WARNING,
            implementation = Implementation(
                MaxFunctionsArgumentsDetector::class.java,
                Scope.JAVA_FILE_SCOPE
            )
        )

		private const val MAX_COUNT_OF_ARGUMENTS = 5
	}

	override fun getApplicableUastTypes(): List<Class<out UElement?>>? {
		return listOf(UMethod::class.java)
	}

	override fun createUastHandler(context: JavaContext): UElementHandler? {
		return object : UElementHandler() {
			override fun visitMethod(node: UMethod) {
				val params = node.uastParameters
				if (params.size > MAX_COUNT_OF_ARGUMENTS) {
					context.report(
                        ISSUE,
                        node,
                        context.getNameLocation(node),
                        ISSUE.getExplanation(TextFormat.TEXT)
                    )
				}
			}
		}
	}
}