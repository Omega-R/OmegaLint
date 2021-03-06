package com.omegar.lint.checks.detector.code_guidelines.kotlin_style.restrictions.emptiness

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import org.jetbrains.uast.UElement
import org.jetbrains.uast.UMethod
import org.jetbrains.uast.USwitchClauseExpression

class EmptyBodyFunctionDetector : Detector(), Detector.UastScanner {
	companion object {
		/** Issue describing the problem and pointing to the detector implementation */
		@JvmField
		val ISSUE: Issue = Issue.create(
			id = "OMEGA_NOT_EMPTY_BODY",
			briefDescription =
			"Function or branch body is empty.It does not match the coding convention. Function body should not be empty.",
			explanation = """
                  Function or branch body is empty. Write function body or add comment.
                  http://wiki.omega-r.club/dev-android-code#rec228194993
                    """,
			category = Category.CORRECTNESS,
			priority = 7,
			severity = Severity.WARNING,
			implementation = Implementation(
				EmptyBodyFunctionDetector::class.java,
				Scope.JAVA_FILE_SCOPE
			)
		)

		private val EMPTY_BODY_REGEX = Regex("""\{\s*}""")
		private val EMPTY_BRANCH_REGEX = Regex("""(\{\s*break\s*\})|(\{\s*})""")
	}

	override fun getApplicableUastTypes(): List<Class<out UElement?>> {
		return listOf(
			UMethod::class.java,
			USwitchClauseExpression::class.java
		)
	}

	override fun createUastHandler(context: JavaContext): UElementHandler {
		return object : UElementHandler() {
			override fun visitMethod(node: UMethod) {
				val body = node.uastBody ?: return
				val text = node.text ?: return

				if (text.contains(EMPTY_BODY_REGEX) && (body.asRenderString().matches(EMPTY_BODY_REGEX))) {
					context.report(ISSUE, node, context.getLocation(body), ISSUE.getExplanation(TextFormat.TEXT))
				}
			}

			override fun visitSwitchClauseExpression(node: USwitchClauseExpression) {
				val renderText = node.asRenderString()

				if (renderText.contains(EMPTY_BRANCH_REGEX)) {
					context.report(ISSUE, node, context.getLocation(node), ISSUE.getExplanation(TextFormat.TEXT))
				}
			}
		}

	}
}
