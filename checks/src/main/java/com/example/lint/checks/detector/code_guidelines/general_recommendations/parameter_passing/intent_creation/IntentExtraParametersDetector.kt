package com.example.lint.checks.detector.code_guidelines.general_recommendations.parameter_passing.intent_creation

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import org.jetbrains.uast.UCallExpression
import org.jetbrains.uast.UElement

class IntentExtraParametersDetector : Detector(), Detector.UastScanner {
    companion object {
        /** Issue describing the problem and pointing to the detector implementation */
        @JvmField
        val ISSUE: Issue = Issue.create(
            // ID: used in @SuppressLint warnings etc
            id = "OMEGA_USE_EXTRA_PREFIX_FOR_INTENT_PARAMS",
            // Title -- shown in the IDE's preference dialog, as category headers in the
            // Analysis results window, etc
            briefDescription = "Use EXTRA prefix for intent arguments.",
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
                IntentExtraParametersDetector::class.java,
                Scope.JAVA_FILE_SCOPE
            )
        )

        val EXTRA_PREFIX_REGEX = Regex("""^EXTRA_""")
        val PUT_EXTRA_METHOD_REGEX = Regex("""^putExtra$""")
    }

    override fun getApplicableUastTypes(): List<Class<out UElement?>>? {
        return listOf(UCallExpression::class.java)
    }

    override fun createUastHandler(context: JavaContext): UElementHandler? {
        return object : UElementHandler() {
            override fun visitCallExpression(node: UCallExpression) {
                val name = node.methodName ?: return

                 if(name.matches(PUT_EXTRA_METHOD_REGEX)) {
                     val firstParam = node.valueArguments.firstOrNull() ?: return
                     val extraParam = firstParam.asRenderString()
                     if(!extraParam.contains(EXTRA_PREFIX_REGEX))  {
                         context.report(ISSUE, node, context.getLocation(firstParam), ISSUE.getExplanation(TextFormat.TEXT))
                     }
                 }
            }
        }
    }
}

