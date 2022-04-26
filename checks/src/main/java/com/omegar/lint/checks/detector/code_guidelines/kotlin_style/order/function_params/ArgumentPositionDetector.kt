package com.omegar.lint.checks.detector.code_guidelines.kotlin_style.order.function_params

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import org.jetbrains.uast.UElement
import org.jetbrains.uast.UMethod
import org.jetbrains.uast.UParameter

@Suppress("UnstableApiUsage")
class ArgumentPositionDetector : Detector(), Detector.UastScanner {
	companion object {
		/** Issue describing the problem and pointing to the detector implementation */
		@JvmField
		val ISSUE: Issue = Issue.create(
			id = "OMEGA_USE_CORRECT_PARAMETERS_FUNCTION_POSITION",
			briefDescription = "The file name does not match the coding convention",
			explanation = """
                  Wrong order.
                  http://wiki.omega-r.club/dev-android-code#rec228180045
                    """,
			category = Category.CORRECTNESS,
			priority = 6,
			severity = Severity.WARNING,
			implementation = Implementation(
				ArgumentPositionDetector::class.java,
				Scope.JAVA_FILE_SCOPE
			)
		)

		private const val CONTEXT_CORRECTLY_NAME = "context"
		private const val CONTEXT_REPORT_MESSAGE = "Context argument should be the first"

		private const val CALLBACK_NAME = "callback"
		private const val CALLBACK_REPORT_MESSAGE = "Callback argument should be the last."
		private const val FUNCTION_CALL_PART = "this$"
	}

	override fun getApplicableUastTypes(): List<Class<out UElement?>> = listOf(UParameter::class.java)

	override fun createUastHandler(context: JavaContext): UElementHandler {
		return object : UElementHandler() {
			override fun visitParameter(node: UParameter) {
				val parent = node.uastParent as? UMethod ?: return
				val params = parent.uastParameters
				var indexOfFirstElement = 0 // for case SomeClass.someFun(context: Context)

				params.forEach {
					if (it.name.contains(FUNCTION_CALL_PART)) {
						indexOfFirstElement++
					}
				}

				if (params[indexOfFirstElement] != node && (node.name == CONTEXT_CORRECTLY_NAME)) {
					contextReport(context, node, CONTEXT_REPORT_MESSAGE)
				}

				if ((params[params.size - 1] != node) && (node.name == CALLBACK_NAME)) {
					contextReport(context, node, CALLBACK_REPORT_MESSAGE)
				}
			}
		}
	}

	private fun contextReport(context: JavaContext, node: UParameter, message: String) {
		context.report(
			ISSUE,
			node as UElement,
			context.getNameLocation(node),
			"$message\n${ISSUE.getExplanation(TextFormat.TEXT)}"
		)
	}
}
