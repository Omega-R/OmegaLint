package com.omegar.lint.checks.detector.project_guidelines.file_name.resource.layout

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import org.jetbrains.uast.UCallExpression
import org.jetbrains.uast.UElement
import org.jetbrains.uast.getContainingUFile
import org.jetbrains.uast.tryResolveNamed

class NameResourceLayoutDetector : Detector(), Detector.UastScanner {
	companion object {
		/** Issue describing the problem and pointing to the detector implementation */
		@JvmField
		val ISSUE: Issue = Issue.create(
			// ID: used in @SuppressLint warnings etc
			id = "OMEGA_NAME_RESOURCE_LAYOUT_CORRECTLY",
			// Title -- shown in the IDE's preference dialog, as category headers in the
			// Analysis results window, etc
			briefDescription = "Layout files must start with the name of the Android component they are intended for.",
			// Full explanation of the issue; you can use some markdown markup such as
			// `monospace`, *italic*, and **bold**.
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
		private const val UPPER_ACTIVITY = "Activity"
		private const val UPPER_FRAGMENT = "Fragment"
		private const val UPPER_DIALOG = "Dialog"
		private const val LOWER_ACTIVITY = "activity"
		private const val LOWER_FRAGMENT = "dialog"
		private const val LOWER_DIALOG = "fragment"
		private const val LAYOUT_PREFIX_VAL = "R.layout."

		private val PART_NAME_REGEX = Regex("""($UPPER_ACTIVITY|$UPPER_FRAGMENT|$UPPER_DIALOG)""")
		private val CAMEL_REGEX = Regex("(?<=[a-zA-Z])[A-Z]")
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

				when {
					name.contains(UPPER_ACTIVITY) || name == SET_CONTENT_VIEW_VAL -> {

						val newClassName = "$LOWER_ACTIVITY${className.replace(UPPER_ACTIVITY, "")}".camelToSnakeCase()
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

					(name.contains(UPPER_FRAGMENT) || name == INFLATE_VAL) && !name.contains(UPPER_DIALOG) -> {

						val newClassName = "$LOWER_FRAGMENT${className.replace(UPPER_FRAGMENT, "")}".camelToSnakeCase()
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

					name.contains(UPPER_DIALOG) || name == INFLATE_VAL -> {

						val newClassName = "$LOWER_DIALOG${className.replace(UPPER_DIALOG, "")}".camelToSnakeCase()
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

			// String extensions
			fun String.camelToSnakeCase(): String {
				return CAMEL_REGEX.replace(this) {
					"_${it.value}"
				}.toLowerCase()
			}
		}
	}
}

