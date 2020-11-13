package com.example.lint.checks.detector.coding_convention_1.cc_1_1.cc_1_1_1

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import org.jetbrains.uast.UClass
import org.jetbrains.uast.UElement

@Suppress("UnstableApiUsage")
class NameFileUpperCamelCaseDetector : Detector(), Detector.UastScanner {
    companion object {
        /** Issue describing the problem and pointing to the detector implementation */
        @JvmField
        val ISSUE: Issue = Issue.create(
            id = "FileName",
            briefDescription = "The file name does not match the coding convention",
            explanation = """
                  Class name should be recorded in UpperCamelCase. Rename this file.
                  http://wiki.omega-r.club/dev-android-code#rec226456384
                    """,
            category = Category.CORRECTNESS,
            priority = 6,
            severity = Severity.WARNING,
            implementation = Implementation(
                NameFileUpperCamelCaseDetector::class.java,
                Scope.JAVA_FILE_SCOPE
            )
        )
    }

    override fun getApplicableUastTypes(): List<Class<out UElement?>>? {
        return listOf(UClass::class.java)
    }

    override fun createUastHandler(context: JavaContext): UElementHandler? {
        return object : UElementHandler() {
            override fun visitClass(node: UClass) {
                /**
                 * UpperCamelCase check
                 */
                val name = node.name ?: return

                if (name.contains(Regex("[A-Z][A-Z]")))
                    context.report(
                        ISSUE, node, context.getNameLocation(node),
                        ISSUE.getExplanation(TextFormat.TEXT)
                    )
            }
        }
    }
}
