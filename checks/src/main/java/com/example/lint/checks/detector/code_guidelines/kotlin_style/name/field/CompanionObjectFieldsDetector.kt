package com.example.lint.checks.detector.code_guidelines.kotlin_style.name.field

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*

import org.jetbrains.uast.UClass
import org.jetbrains.uast.UElement

class CompanionObjectFieldsDetector : Detector(), Detector.UastScanner {
    companion object {
        /** Issue describing the problem and pointing to the detector implementation */
        @JvmField
        val ISSUE: Issue = Issue.create(
            id = "OMEGA_NAME_CONSTANTS_CORRECTLY",
            briefDescription = "The line size does not match the coding convention",
            explanation = """
                  The immutable fields in the Companion Object and compile-time constants are named in the 
                  SCREAMING_SNAKE_CASE style.
                  http://wiki.omega-r.club/dev-android-code#rec226457239
                    """,
            category = Category.CORRECTNESS,
            priority = 7,
            severity = Severity.WARNING,
            implementation = Implementation(
                CompanionObjectFieldsDetector::class.java,
                Scope.JAVA_FILE_SCOPE
            )
        )

        private const val CONST_VAL_LABEL = "const val"
        private const val VAL_LABEL = "val"

        private val UPPER_REGEX = Regex("""^([A-Z]*_*)*$""")

    }

    override fun getApplicableUastTypes(): List<Class<out UElement?>>? {
        return listOf(UClass::class.java)
    }

    override fun createUastHandler(context: JavaContext): UElementHandler? {
        return object : UElementHandler() {
            override fun visitClass(node: UClass) {
                val innerClasses = node.innerClasses
                innerClasses.forEach { innerClass ->
                    val name = innerClass.name ?: return

                    if (name == "Companion") {

                        val methods = innerClass.uastDeclarations
                        val newList = methods.distinctBy { it.text }

                        newList.forEach { declaration ->
                            val text = declaration.text ?: return
                            val lines = text.lines()
                            lines.forEach { line ->
                                if (line.contains(CONST_VAL_LABEL)) {
                                    var substrings = line.split(" ")
                                    var valIndex = 0
                                    for (i: Int in substrings.indices) {
                                        if ((substrings[i] == VAL_LABEL) && (valIndex != substrings.size - 1)) {
                                            valIndex = i
                                        }
                                    }
                                    if (valIndex != 0) {
                                        val identifierName = substrings[valIndex + 1]
                                        if (!identifierName.matches(UPPER_REGEX)) {
                                            context.report(
                                                ISSUE, node, context.getNameLocation(declaration),
                                                ISSUE.getExplanation(TextFormat.TEXT))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

    }

}

