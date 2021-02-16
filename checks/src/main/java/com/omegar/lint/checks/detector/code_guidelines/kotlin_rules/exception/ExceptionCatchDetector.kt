package com.omegar.lint.checks.detector.code_guidelines.kotlin_rules.exception

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import org.jetbrains.uast.UCatchClause
import org.jetbrains.uast.UElement

@Suppress("UnstableApiUsage")
class ExceptionCatchDetector : Detector(), Detector.UastScanner {
	companion object {
		/** Issue describing the problem and pointing to the detector implementation */
		@JvmField
		val ISSUE: Issue = Issue.create(

			id = "OMEGA_NOT_IGNORE_EXCEPTIONS",
			briefDescription = "Catch body is empty, it not match the coding convention",
			explanation = """
                   Catch body is empty. Add exception handling.
                   http://wiki.omega-r.club/dev-android-code#rec226449864
                   """,
			category = Category.CORRECTNESS,
			priority = 7,
			severity = Severity.WARNING,
			implementation = Implementation(
				ExceptionCatchDetector::class.java,
				Scope.JAVA_FILE_SCOPE
			)
		)

		private val EMPTY_BODY_REGEX = Regex("""\{\s*}""")

		private const val GENERALIZED_EXCEPTION_VAL = "java.lang.Exception"
		private const val THROW_VAL = "throw"
		private const val GENERALIZED_EXCEPTION_MESSAGE = """Catch generalized exception.
			 Should throw specific exception in catch body. 
			http://wiki.omega-r.club/dev-android-code#rec226454364
			"""
	}

	override fun getApplicableUastTypes(): List<Class<out UElement?>> = listOf(UCatchClause::class.java)

	override fun createUastHandler(context: JavaContext): UElementHandler {
		return object : UElementHandler() {
			override fun visitCatchClause(node: UCatchClause) {
				val body = node.body
				val string = body.asRenderString()

				if (string.matches(EMPTY_BODY_REGEX)) {
					context.report(
						ISSUE,
						body,
						context.getNameLocation(body),
						ISSUE.getExplanation(TextFormat.TEXT),
						createEmptyBodyFix()
					)
				}
				val parameters = node.parameters
				parameters.forEach {
					if (it.type.canonicalText == GENERALIZED_EXCEPTION_VAL) {
						if (!string.contains(THROW_VAL)) {
							context.report(
								ISSUE,
								body,
								context.getLocation(body),
								GENERALIZED_EXCEPTION_MESSAGE,
								createEmptyBodyFix()
							)
						}
					}
				}
			}
		}
	}

	private fun createEmptyBodyFix(): LintFix {
		return LintFix.create()
			.replace()
			.text("}")
			.with("	throw //something\n		}")
			.build()
	}

}
