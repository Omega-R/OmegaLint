package com.omegar.lint.checks.detector.code_guidelines.general_recommendations.parameter_passing.argument_bundle_for_fragments_creation

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import org.jetbrains.uast.UCallExpression
import org.jetbrains.uast.UElement
import org.jetbrains.uast.getContainingUFile

class ArgumentsBundleKeyPrefixDetector : Detector(), Detector.UastScanner {
	companion object {
		/** Issue describing the problem and pointing to the detector implementation */
		@JvmField
		val ISSUE: Issue = Issue.create(
            // ID: used in @SuppressLint warnings etc
            id = "OMEGA_USE_KEY_PREFIX_FOR_FRAGMENT_IN_ARGUMENTS_BUNDLE_PARAMS",
            // Title -- shown in the IDE's preference dialog, as category headers in the
            // Analysis results window, etc
            briefDescription = "Use KEY prefix for Fragment in  Arguments Bundle param.",
            // Full explanation of the issue; you can use some markdown markup such as
            // `monospace`, *italic*, and **bold**.
            explanation = """
                  Use EXTRA prefix for intent arguments
                  http://wiki.omega-r.club/dev-android-code#rec228392168
                    """,
            category = Category.CORRECTNESS,
            priority = 7,
            severity = Severity.WARNING,
            implementation = Implementation(
                _root_ide_package_.com.omegar.lint.checks.detector.code_guidelines.general_recommendations.parameter_passing.argument_bundle_for_fragments_creation.ArgumentsBundleKeyPrefixDetector::class.java,
                Scope.JAVA_FILE_SCOPE
            )
        )

		val KEY_PREFIX_REGEX = Regex("""^KEY_""")
		val PUT_PARCELABLE_METHOD_REGEX = Regex("""^putParcelable$""")
		val FRAGMENT_REGEX = Regex("""Fragment$""")
	}

	override fun getApplicableUastTypes(): List<Class<out UElement?>>? {
		return listOf(UCallExpression::class.java)
	}

	override fun createUastHandler(context: JavaContext): UElementHandler? {
		return object : UElementHandler() {
			override fun visitCallExpression(node: UCallExpression) {
				val file = node.getContainingUFile() ?: return
				val className = file.classes.firstOrNull()?.name ?: return
				val name = node.methodName ?: return
				if (className.contains(_root_ide_package_.com.omegar.lint.checks.detector.code_guidelines.general_recommendations.parameter_passing.argument_bundle_for_fragments_creation.ArgumentsBundleKeyPrefixDetector.Companion.FRAGMENT_REGEX)) {
					if (name.matches(_root_ide_package_.com.omegar.lint.checks.detector.code_guidelines.general_recommendations.parameter_passing.argument_bundle_for_fragments_creation.ArgumentsBundleKeyPrefixDetector.Companion.PUT_PARCELABLE_METHOD_REGEX)) {
						val firstParam = node.valueArguments.firstOrNull() ?: return
						val extraParam = firstParam.asRenderString()
						if (!extraParam.contains(_root_ide_package_.com.omegar.lint.checks.detector.code_guidelines.general_recommendations.parameter_passing.argument_bundle_for_fragments_creation.ArgumentsBundleKeyPrefixDetector.Companion.KEY_PREFIX_REGEX)) {
							context.report(_root_ide_package_.com.omegar.lint.checks.detector.code_guidelines.general_recommendations.parameter_passing.argument_bundle_for_fragments_creation.ArgumentsBundleKeyPrefixDetector.Companion.ISSUE, node, context.getLocation(firstParam), _root_ide_package_.com.omegar.lint.checks.detector.code_guidelines.general_recommendations.parameter_passing.argument_bundle_for_fragments_creation.ArgumentsBundleKeyPrefixDetector.Companion.ISSUE.getExplanation(TextFormat.TEXT))
						}
					}
				}
			}
		}
	}
}

