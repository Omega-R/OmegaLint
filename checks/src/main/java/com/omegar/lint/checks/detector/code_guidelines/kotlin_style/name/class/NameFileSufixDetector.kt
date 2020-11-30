package com.omegar.lint.checks.detector.code_guidelines.kotlin_style.name.`class`

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import org.jetbrains.uast.UClass
import org.jetbrains.uast.UElement

@Suppress("UnstableApiUsage")
class NameFileSufixDetector : Detector(), Detector.UastScanner {
	companion object {
		/** Issue describing the problem and pointing to the detector implementation */
		@JvmField
		val ISSUE: Issue = Issue.create(
			id = "OMEGA_USE_PARENT_NAME_AS_SUFFIX_FOR_CHILD",
			briefDescription = "The file name does not match the coding convention",
			explanation = """
                  Class name should has parent name in sufix. Rename this file.
                  http://wiki.omega-r.club/dev-android-code#rec226456384
                    """,
			category = Category.CORRECTNESS,
			priority = 6,
			severity = Severity.WARNING,
			implementation = Implementation(
				NameFileSufixDetector::class.java,
				Scope.JAVA_FILE_SCOPE
			)
		)

		private const val ACTIVITY_VALUE = "Activity"
		private val ACTIVITY_VALUE_REGEX = Regex(".*${ACTIVITY_VALUE}$")

		private const val FRAGMENT_VALUE = "Fragment"
		private val FRAGMENT_VALUE_REGEX = Regex(".*${FRAGMENT_VALUE}$")

		private const val VIEW_VALUE = "View"
		private val VIEW_VALUE_REGEX = Regex(".*${VIEW_VALUE}$")

		private const val SERVICE_VALUE = "Service"
		private val SERVICE_VALUE_REGEX = Regex(".*${SERVICE_VALUE}$")

		private const val PRESENTER_VALUE = "Presenter"
		private val PRESENTER_VALUE_REGEX = Regex(".*${PRESENTER_VALUE}$")

		private const val PROVIDER_VALUE = "Provider"
		private val PROVIDER_VALUE_REGEX = Regex(".*${PROVIDER_VALUE}$")

		private const val OBJECT_VALUE = "Object"
		private const val REPORT_MESSAGE_BEGIN = "Class name should end with"
	}

	override fun getApplicableUastTypes(): List<Class<out UElement?>>? {
		return listOf(UClass::class.java)
	}

	override fun createUastHandler(context: JavaContext): UElementHandler? {
		return object : UElementHandler() {
			override fun visitClass(node: UClass) {
				/**
				 * Parent suffix check
				 */
				val name = node.name ?: return

				val superClass = node.javaPsi.superClass ?: return

				if (!superClass.isInterface && (superClass.name != OBJECT_VALUE)) {
					val part = superClass.name ?: return

					when {
						part.matches(ACTIVITY_VALUE_REGEX) ->
							if (!name.matches(ACTIVITY_VALUE_REGEX)) {
								makeContextReport(context, ACTIVITY_VALUE, node)
							}

						part.matches(FRAGMENT_VALUE_REGEX) ->
							if (!name.matches(FRAGMENT_VALUE_REGEX)) {
								makeContextReport(context, FRAGMENT_VALUE, node)
							}

						part.matches(VIEW_VALUE_REGEX) ->
							if (!name.matches(VIEW_VALUE_REGEX)) {
								makeContextReport(context, VIEW_VALUE, node)
							}

						part.matches(SERVICE_VALUE_REGEX) ->
							if (!name.matches(SERVICE_VALUE_REGEX)) {
								makeContextReport(context, SERVICE_VALUE, node)
							}

						part.matches(PRESENTER_VALUE_REGEX) ->
							if (!name.matches(PRESENTER_VALUE_REGEX)) {
								makeContextReport(context, PRESENTER_VALUE, node)
							}

						part.matches(PROVIDER_VALUE_REGEX) ->
							if (!name.matches(PROVIDER_VALUE_REGEX)) {
								makeContextReport(context, PROVIDER_VALUE, node)
							}
					}
				}

			}
		}
	}

	private fun makeContextReport(context: JavaContext, value: String, node: UClass) {
		return context.report(
			ISSUE,
			node,
			context.getNameLocation(node),
			"$REPORT_MESSAGE_BEGIN $value.\n${ISSUE.getExplanation(TextFormat.TEXT)}"
		)
	}
}
