package com.omegar.lint.checks.detector.code_guidelines.kotlin_style.restrictions.class_methods_count

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import org.jetbrains.uast.*

@Suppress("UnstableApiUsage")
class MaxMethodCountDetector : Detector(), Detector.UastScanner {
	companion object {
		/** Issue describing the problem and pointing to the detector implementation */
		@JvmField
		val ISSUE: Issue = Issue.create(
			id = "OMEGA_NOT_EXCEED_MAX_METHODS_COUNT",
			briefDescription = "Class methods count does not match the coding convention",
			explanation = """
                  Class should has 30 methods or less.
                  http://wiki.omega-r.club/dev-android-code#rec228195879
                    """,
			category = Category.CORRECTNESS,
			priority = 7,
			severity = Severity.WARNING,
			implementation = Implementation(
				MaxMethodCountDetector::class.java,
				Scope.JAVA_FILE_SCOPE
			)
		)

		private const val DATA_CLASS_FUNCTION_VALUE = "public final fun copy"
		private const val VAR_LABEL = "var"
		private const val VAL_LABEL = "val"
		private const val METHOD_GET = "get"
		private const val METHOD_SET = "set"
		private const val MAX_METHOD_COUNT = 30
	}

	override fun getApplicableUastTypes(): List<Class<out UElement?>> = listOf(UClass::class.java)

	override fun createUastHandler(context: JavaContext) = object : UElementHandler() {
		override fun visitClass(node: UClass) {
			val resultMethods = mutableListOf<UMethod>()
			val text = node.uastDeclarations.distinctBy { it.text }
			var getSetCount = 0

			text.forEachIndexed { _, uDeclaration ->
				if (uDeclaration.text.contains(VAL_LABEL) && uDeclaration.text.contains(METHOD_GET)) {
					getSetCount++
				} else if (uDeclaration.text.contains(VAR_LABEL)) {
					if (uDeclaration.text.contains(METHOD_GET)) getSetCount++
					if (uDeclaration.text.contains(METHOD_SET)) getSetCount++
				}
			}
			node.methods.forEach {
				if (it.asRenderString().contains(DATA_CLASS_FUNCTION_VALUE)) {
					return@visitClass
				}
				if (!it.isVarArgs && !it.isConstructor) {
					resultMethods.add(it)
				}
			}
			if (resultMethods.size - getSetCount > MAX_METHOD_COUNT) {
				context.report(ISSUE, node, context.getNameLocation(node), ISSUE.getExplanation(TextFormat.TEXT))
			}
		}
	}
}