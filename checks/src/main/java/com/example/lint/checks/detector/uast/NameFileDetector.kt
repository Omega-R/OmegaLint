package com.example.lint.checks.detector.uast

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import org.jetbrains.uast.UClass
import org.jetbrains.uast.UElement

@Suppress("UnstableApiUsage")
class NameFileDetector : Detector(), Detector.UastScanner {
    companion object {
        /** Issue describing the problem and pointing to the detector implementation */
        @JvmField
        val ISSUE: Issue = Issue.create(
            id = "FileName",
            briefDescription = "The file name does not match the coding convention",
            explanation = """
                  Class name should be recorded in UpperCamelCase and someone should has suffix. Rename this file.
                  http://wiki.omega-r.club/dev-android-code#rec226456384
                    """,
            category = Category.CORRECTNESS,
            priority = 6,
            severity = Severity.WARNING,
            implementation = Implementation(
                NameFileDetector::class.java,
                Scope.JAVA_FILE_SCOPE
            )
        )

        private const val ACTIVITY_VALUE = "Activity"
        private const val FRAGMENT_VALUE = "Fragment"
        private const val VIEW_VALUE = "View"
        private const val SERVICE_VALUE = "Service"
        private const val PRESENTER_VALUE = "Presenter"
        private const val PROVIDER_VALUE = "Provider"

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
                 * UpperCamelCase check
                 */
                val name = node.name ?: return

                if (name.contains(Regex("[A-Z][A-Z]")))
                    context.report(
                        ISSUE, node, context.getNameLocation(node),
                        ISSUE.getExplanation(TextFormat.TEXT),
                        createFix(name)
                    )


                /**
                 * Parent suffix check
                 */
                val superClass = node.javaPsi.superClass ?: return

                if (!superClass.isInterface && (superClass.name != OBJECT_VALUE)) {
                    val part = superClass.name ?: return

                    when {
                        part.matches(Regex(".*${ACTIVITY_VALUE}$")) ->
                            if (!name.matches(Regex(".*${ACTIVITY_VALUE}$"))) {
                                makeContextReport(value = ACTIVITY_VALUE, node)
                            }

                        part.matches(Regex(".*${FRAGMENT_VALUE}$")) ->
                            if (!name.matches(Regex(".*${FRAGMENT_VALUE}$"))) {
                                makeContextReport(value = FRAGMENT_VALUE, node)
                            }

                        part.matches(Regex(".*${VIEW_VALUE}$")) ->
                            if (!name.matches(Regex(".*${VIEW_VALUE}$"))) {
                                makeContextReport(value = VIEW_VALUE, node)
                            }

                        part.matches(Regex(".*${SERVICE_VALUE}$")) ->
                            if (!name.matches(Regex(".*${SERVICE_VALUE}$"))) {
                                makeContextReport(value = SERVICE_VALUE, node)
                            }

                        part.matches(Regex(".*${PRESENTER_VALUE}$")) ->
                            if (!name.matches(Regex(".*${PRESENTER_VALUE}$"))) {
                                makeContextReport(value = PRESENTER_VALUE, node)
                            }

                        part.matches(Regex(".*${PROVIDER_VALUE}$")) ->
                            if (!name.matches(Regex(".*${PROVIDER_VALUE}$"))) {
                                makeContextReport(value = PROVIDER_VALUE, node)
                            }

                        else -> if (!name.contains(part)) {
                            makeContextReport(part, node)
                        }
                    }
                }

            }

            private fun makeContextReport(value: String, node: UClass) {
                return context.report(
                    ISSUE,
                    node,
                    context.getNameLocation(node),
                    "$REPORT_MESSAGE_BEGIN $value. \n${ISSUE.getExplanation(TextFormat.TEXT)}"
                )
            }

            private fun createFix(string: String): LintFix? {
                return fix().replace().text(string).build()
            }
        }
    }
}
