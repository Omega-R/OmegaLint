package com.example.lint.checks.detector.coding_convention_2.cc_2_2.cc_2_2_2.cc_2_2_2_1_and_2_2_2_2_and_2_2_2_3

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import org.jetbrains.uast.*
import java.lang.Integer.min

class ComponentPositionDetector : Detector(), Detector.UastScanner {
    companion object {
        /** Issue describing the problem and pointing to the detector implementation */
        @JvmField
        val ISSUE: Issue = Issue.create(
            id = "ComponentPosition",
            briefDescription = "The line size does not match the coding convention",
            explanation = """
                  Order warning.
                  http://wiki.omega-r.club/dev-android-code#rec228155171
                    """,
            category = Category.CORRECTNESS,
            priority = 7,
            severity = Severity.WARNING,
            implementation = Implementation(
                ComponentPositionDetector::class.java,
                Scope.JAVA_FILE_SCOPE
            )
        )

        private const val COMPANION_NAME = "Companion"

        // 1. companion object
        private val COMPANION_OBJECT = Regex("^companion object")
        private const val COMPANION_OBJECT_POSITION = 1
        private const val COMPANION_OBJECT_MESSAGE = "companion object should be the first"

        // 2. val + var variables
        private const val VAL = "val"
        private const val VAR = "var"
        private const val VARIABLES_POSITION = 2
        private const val VARIABLES_MESSAGE =
            "Variables should be earlier than constructors, functions, enums, interfaces and classes"

        // 3. constructors and inits
        private const val CONSTRUCTOR = "constructor"
        private const val CONSTRUCTOR_POSITION = 3
        private const val CONSTRUCTOR_MESSAGE = "Constructor should be earlier than functions, enums, interfaces and classes"

        // 4. functions
        private const val FUNCTION = "fun"
        private const val FUNCTION_POSITION = 4
        private const val FUNCTION_MESSAGE = "Functions should be earlier than  enums, interfaces and classes"

        // 5. enums
        private const val ENUM = "enum"
        private const val ENUM_POSITION = 5
        private const val ENUM_MESSAGE = "Enum should be earlier than interfaces and classes"

        // 6. interfaces
        private const val INTERFACE = "interface"
        private const val INTERFACE_POSITION = 6
        private const val INTERFACE_MESSAGE = "Enum should be earlier than classes"

        // 7. classes
        private const val CLASS = "class"
        private const val CLASS_POSITION = 7

    }

    override fun getApplicableUastTypes(): List<Class<out UElement?>>? {
        return listOf(UClass::class.java)
    }

    override fun createUastHandler(context: JavaContext): UElementHandler? {
        return object : UElementHandler() {
            override fun visitClass(node: UClass) {
                val listUDeclaration = node.uastDeclarations
                val list = mutableListOf<UDeclaration>()
                val name = node.name ?: return

                val lines = node.parent.text.lines()
                /**
                 *  sorting declarations by file's lines
                 * node.uastDeclarations give elements in wrong order
                 * https://github.com/JetBrains/intellij-community/blob/master/uast/uast-java/src/org/jetbrains/uast/java/declarations/JavaUClass.kt
                 */
                lines.forEach { line ->
                    if (line != "") {
                        listUDeclaration.forEach { declaration ->
                            val dt = declaration.text
                            if (dt != null) {
                                if ((dt.substring(0, min(dt.length, line.length)).trim() == line.trim()) &&
                                    !(line.contains("class $name"))
                                ) {
                                    list.add(declaration)
                                }
                            }
                        }
                    }
                }

                var str = ""
                val newList = list.distinctBy { it.text }

                if (name != COMPANION_NAME) {
                    var currentPosition = 0
                    newList.forEach { declaration ->
                        val text = declaration.text ?: return

                        /** 1) it's can find companion object*/

                        if (text.contains(COMPANION_OBJECT)) {
                            if (currentPosition <= COMPANION_OBJECT_POSITION) {
                                currentPosition = COMPANION_OBJECT_POSITION
                            } else {
                                context.report(
                                    ISSUE,
                                    node,
                                    context.getNameLocation(declaration),
                                    COMPANION_OBJECT_MESSAGE + ISSUE.getExplanation(TextFormat.TEXT)
                                )
                            }
                        }

                        /** 2) Variables*/

                        val valList = makeRegexList(VAL)
                        valList.forEach {
                            if (text.contains(it)) {
                                if (currentPosition <= VARIABLES_POSITION) {
                                    currentPosition = VARIABLES_POSITION
                                } else {
                                    context.report(
                                        ISSUE,
                                        node,
                                        context.getNameLocation(declaration),
                                        VARIABLES_MESSAGE + ISSUE.getExplanation(TextFormat.TEXT)
                                    )
                                }
                            }
                        }

                        val varList = makeRegexList(VAR)
                        varList.forEach {
                            if (text.contains(it)) {
                                if (currentPosition <= VARIABLES_POSITION) {
                                    currentPosition = VARIABLES_POSITION
                                } else {
                                    context.report(
                                        ISSUE,
                                        node,
                                        context.getNameLocation(declaration),
                                        VARIABLES_MESSAGE + ISSUE.getExplanation(TextFormat.TEXT)
                                    )
                                }
                            }
                        }

                        /** 3) Constructor */

                        val constructorRegexList = makeRegexList(CONSTRUCTOR)
                        constructorRegexList.forEach {
                            if (text.contains(it)) {
                                if (currentPosition <= CONSTRUCTOR_POSITION) {
                                    currentPosition = CONSTRUCTOR_POSITION

                                } else {
                                    context.report(
                                        ISSUE,
                                        node,
                                        context.getNameLocation(declaration),
                                        CONSTRUCTOR_MESSAGE + ISSUE.getExplanation(TextFormat.TEXT)
                                    )
                                }
                            }
                        }

                        /** 4 Function */

                        val functionRegexList = makeRegexList(FUNCTION)
                        functionRegexList.forEach {
                            if (text.contains(it)) {
                                if (currentPosition <= FUNCTION_POSITION) {
                                    currentPosition = FUNCTION_POSITION
                                } else {
                                    context.report(
                                        ISSUE,
                                        node,
                                        context.getNameLocation(declaration),
                                        FUNCTION_MESSAGE + ISSUE.getExplanation(TextFormat.TEXT)
                                    )
                                }
                            }
                        }

                        /** 5 Enum */

                        val enumRegexList = makeRegexList(ENUM)
                        enumRegexList.forEach {
                            if (text.contains(it)) {
                                if (currentPosition <= ENUM_POSITION) {
                                    currentPosition = ENUM_POSITION
                                } else {
                                    context.report(
                                        ISSUE,
                                        node,
                                        context.getNameLocation(declaration),
                                        ENUM_MESSAGE + ISSUE.getExplanation(TextFormat.TEXT)
                                    )
                                }
                            }
                        }

                        /** 6) Interface */

                        val interfaceRegexList = makeRegexList(INTERFACE)
                        interfaceRegexList.forEach {
                            if (text.contains(it)) {
                                if (currentPosition <= INTERFACE_POSITION) {
                                    currentPosition = INTERFACE_POSITION
                                } else {
                                    context.report(
                                        ISSUE,
                                        node,
                                        context.getNameLocation(declaration),
                                        INTERFACE_MESSAGE + ISSUE.getExplanation(TextFormat.TEXT)
                                    )
                                }
                            }
                        }
                        /** 7) Class */

                        val classRegexList = makeRegexList(CLASS)
                        classRegexList.forEach {
                            if (text.contains(it)) {
                                currentPosition = CLASS_POSITION
                            }
                        }
                    }
                }
            }


            fun makeRegexList(value: String): List<Regex> {
                return listOf(
                    Regex("^abstract $value"),
                    Regex("^override $value"),
                    Regex("^public $value"),
                    Regex("^internal $value"),
                    Regex("^protected $value"),
                    Regex("^private $value"),
                    Regex("^${value}")
                )
            }
        }

    }
}


