package com.omegar.lint.checks.detector.code_guidelines.kotlin_style.order.file_package

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiField
import org.jetbrains.uast.*

@Suppress("UnstableApiUsage")
class PackageComponentDetector : Detector(), Detector.UastScanner {

    companion object {
        @JvmField
        val ISSUE = Issue.create(
            id = "OMEGA_USE_FILE_COMPONENTS_IN_CORRECT_ORDER",
            briefDescription = "Place file components in correct order",
            explanation = """
                Order warning.
                  http://wiki.omega-r.club/dev-android-code#rec228155171
            """,
            category = Category.CORRECTNESS,
            priority = 7,
            severity = Severity.WARNING,
            implementation = Implementation(
                PackageComponentDetector::class.java,
                Scope.JAVA_FILE_SCOPE
            )
        )

        private const val CLASS_LABEL = "class"
        private const val CONSTANT_MESSAGE = "Constants should be positioned before classes, interfaces or any public elements"
        private const val ELEMENTS_MESSAGE = "All public elements should be positioned after classes and interfaces"
    }

    override fun getApplicableUastTypes(): List<Class<out UElement>> = listOf(UFile::class.java)

    override fun createUastHandler(context: JavaContext) = object : UElementHandler() {
        override fun visitFile(node: UFile) {
            val publicElements = node.classes.drop(1)
            val classes = node.classes.filter { !it.isInterface }.filter { !it.isEnum }
            val fields = node.classes[0].fields

            fields.forEach fieldScope@ { uField ->
                publicElements.forEach { uElement ->
                    if (uField.getStartPosition() > uElement.getStartPosition()) {
                        makeContextReport(context, node, uField, CONSTANT_MESSAGE)
                        return@fieldScope
                    }
                }
            }

            publicElements.forEach elementsScope@ { uElement ->
                classes.drop(1).forEach classesScope@ { uClass ->
                    if (!uClass.text.startsWith(CLASS_LABEL) && !uElement.text.startsWith(CLASS_LABEL)) {
                        return@classesScope
                    }
                    if (!uElement.isInterface && !uElement.text.startsWith(CLASS_LABEL) && uClass.getStartPosition() > uElement.getStartPosition()) {
                        makeContextReport(context, node, uElement, ELEMENTS_MESSAGE)
                        return@elementsScope
                    }
                }
            }
        }

        private fun PsiField.getStartPosition() = context.getNameLocation(this).start?.line ?: 0

        private fun PsiClass.getStartPosition() = context.getNameLocation(this).start?.line ?: 0

    }

    private fun makeContextReport(context: JavaContext, node: UFile, declaration: UElement, message: String) {
        context.report(
            ISSUE,
            node,
            context.getLocation(declaration),
            "$message ${ISSUE.getExplanation(TextFormat.TEXT)}"
        )
    }
}