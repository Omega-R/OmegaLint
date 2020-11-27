package com.omegar.lint.checks.detector.project_guidelines.file_name.resource.layout

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import org.jetbrains.uast.*

class NameResourceLayoutDetector : Detector(), Detector.UastScanner {
	companion object {
		/** Issue describing the problem and pointing to the detector implementation */
		@JvmField
		val ISSUE: Issue = Issue.create(
			id = "OMEGA_NAME_RESOURCE_LAYOUT_CORRECTLY",
			briefDescription = "Layout files must start with the name of the Android component they are intended for.",
			explanation = """
                  Wrong layout name.
                  http://wiki.omega-r.club/dev-android-code#rec226396979
                    """,
			category = Category.CORRECTNESS,
			priority = 7,
			severity = Severity.WARNING,
			implementation = Implementation(
				NameResourceLayoutDetector::class.java,
				Scope.JAVA_FILE_SCOPE
			)
		)

		private const val SET_CONTENT_VIEW_VAL = "setContentView"
		private const val INFLATE_VAL = "inflate"
		private const val ON_CREATE_VAL = "onCreate"
		private const val ON_CREATE_VIEW_VAL = "onCreateView"

		private const val UPPER_ACTIVITY = "Activity"
		private const val UPPER_FRAGMENT = "Fragment"
		private const val UPPER_DIALOG = "Dialog"
		private const val LOWER_ACTIVITY = "activity"
		private const val LOWER_FRAGMENT = "fragment"
		private const val LOWER_DIALOG = "dialog"
		private const val LAYOUT_PREFIX_VAL = "R.layout."

		private val PART_NAME_REGEX = Regex("""($UPPER_ACTIVITY|$UPPER_FRAGMENT|$UPPER_DIALOG)""")
		private val CAMEL_REGEX = Regex("(?<=[a-zA-Z])[A-Z]")
		private val FUNCTION_REGEX = Regex("""($SET_CONTENT_VIEW_VAL|$INFLATE_VAL)""")
		private val FUNCTION_PARENT_REGEX = Regex("""($ON_CREATE_VAL|$ON_CREATE_VIEW_VAL|return)""")

	}

	override fun getApplicableUastTypes(): List<Class<out UElement?>>? {
		return listOf(
			UCallExpression::class.java
		)
	}

	override fun createUastHandler(context: JavaContext): UElementHandler? {
		return object : UElementHandler() {
			override fun visitCallExpression(node: UCallExpression) {
				val name = node.tryResolveNamed()?.name ?: return
				val arguments = node.valueArguments
				val file = node.getContainingUFile() ?: return
				val className = file.classes.firstOrNull()?.name ?: return

				if (!className.contains(PART_NAME_REGEX)) {
					return
				}

				if (name.matches(FUNCTION_REGEX)) {
					checkSetLayoutFunction(node, className, arguments)
				}

				findLayout(name, className, arguments, node)
			}

			// String extensions
			private fun String.convertCamelToSnakeCase(): String {
				return CAMEL_REGEX.replace(this) {
					"_${it.value}"
				}.toLowerCase()
			}

			private fun checkSetLayoutFunction(node: UCallExpression, className: String, arguments: List<UExpression>) {
				val parentFirstLine = node.uastParent?.uastParent?.asRenderString()?.split("\n")?.firstOrNull()

				if (parentFirstLine != null && parentFirstLine.contains(FUNCTION_PARENT_REGEX)) {
					findLayout(className, className, arguments, node)
				}
			}

			private fun findLayout(name: String, className: String, arguments: List<UExpression>, node: UCallExpression) {
				when {
					name.contains(UPPER_ACTIVITY) -> {
						val newClassName = "$LOWER_ACTIVITY${className.replace(UPPER_ACTIVITY, "")}".convertCamelToSnakeCase()
						checkLayoutName(arguments, newClassName, node)
					}

					name.contains(UPPER_FRAGMENT) && !name.contains(UPPER_DIALOG) -> {
						val newClassName = "$LOWER_FRAGMENT${className.replace(UPPER_FRAGMENT, "")}".convertCamelToSnakeCase()
						checkLayoutName(arguments, newClassName, node)
					}

					name.contains(UPPER_DIALOG) -> {
						val nameWithoutFragment = className.replace(UPPER_FRAGMENT, "")
						val newClassName = "$LOWER_DIALOG${nameWithoutFragment.replace(UPPER_DIALOG, "")}".convertCamelToSnakeCase()
						checkLayoutName(arguments, newClassName, node)
					}
				}
			}

			private fun checkLayoutName(arguments: List<UExpression>, newClassName: String, node: UCallExpression) {
				arguments.forEach { argument ->
					val argumentName = argument.asRenderString()
					if (argumentName.contains(LAYOUT_PREFIX_VAL) && LAYOUT_PREFIX_VAL + newClassName != argumentName) {
						context.report(
							ISSUE,
							node,
							context.getNameLocation(argument),
							"$LAYOUT_PREFIX_VAL$newClassName\n${ISSUE.getExplanation(TextFormat.TEXT)}"
						)
					}
				}
			}
		}
	}
}

