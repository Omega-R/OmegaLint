package com.omegar.lint.checks.detector.code_guidelines.kotlin_style.order.file_class_interface

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import org.jetbrains.uast.*
import java.lang.Integer.min

class ComponentPositionDetector : Detector(), Detector.UastScanner {
    companion object {
        /** Issue describing the problem and pointing to the detector implementation */
        @JvmField
        val ISSUE: Issue = Issue.create(
            id = "OMEGA_USE_COMPONENTS_IN_CORRECT_ORDER",
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
        private const val COMPANION_OBJECT = "companion object"
        private const val COMPANION_OBJECT_RANK = 1
        private const val COMPANION_OBJECT_MESSAGE = "companion object should be the first"
        private val COMPANION_OBJECT_REGEX_LIST = makeRegexList(COMPANION_OBJECT)
        private val COMPANION_OBJECT_PARAMS = Params(COMPANION_OBJECT_REGEX_LIST, COMPANION_OBJECT_RANK, COMPANION_OBJECT_MESSAGE)

        // 2. val + var variables
        private const val VAL = "val"
        private const val VAR = "var"
        private const val VARIABLES_RANK = 2
        private const val VARIABLES_MESSAGE =
            "Variables should be earlier than constructors, functions, enums, interfaces and classes"
        private val VAL_REGEX_LIST = makeRegexList(VAL)
        private val VAR_REGEX_LIST = makeRegexList(VAR)
        private val VAL_PARAMS = Params(VAL_REGEX_LIST, VARIABLES_RANK, VARIABLES_MESSAGE)
        private val VAR_PARAMS = Params(VAR_REGEX_LIST, VARIABLES_RANK, VARIABLES_MESSAGE)

        // 3. constructors and inits
        private const val CONSTRUCTOR = "constructor"
        private const val CONSTRUCTOR_RANK = 3
        private const val CONSTRUCTOR_MESSAGE = "Constructor should be earlier than functions, enums, interfaces and classes"
        private val CONSTRUCTOR_REGEX_LIST = makeRegexList(CONSTRUCTOR)
        private val CONSTRUCTOR_PARAMS = Params(CONSTRUCTOR_REGEX_LIST, CONSTRUCTOR_RANK, CONSTRUCTOR_MESSAGE)

        // 4. functions
        private const val FUNCTION = "fun"
        private const val FUNCTION_RANK = 4
        private const val FUNCTION_MESSAGE = "Functions should be earlier than  enums, interfaces and classes"
        private val FUNCTION_REGEX_LIST = makeRegexList(FUNCTION)
        private val FUNCTION_PARAMS = Params(FUNCTION_REGEX_LIST, FUNCTION_RANK, FUNCTION_MESSAGE)

        // 5. enums
        private const val ENUM = "enum"
        private const val ENUM_RANK = 5
        private const val ENUM_MESSAGE = "Enum should be earlier than interfaces and classes"
        private val ENUM_REGEX_LIST = makeRegexList(ENUM)
        private val ENUM_PARAMS = Params(ENUM_REGEX_LIST, ENUM_RANK, ENUM_MESSAGE)

        // 6. interfaces
        private const val INTERFACE = "interface"
        private const val INTERFACE_RANK = 6
        private const val INTERFACE_MESSAGE = "Enum should be earlier than classes"
        private val INTERFACE_REGEX_LIST = makeRegexList(INTERFACE)
        private val INTERFACE_PARAMS = Params(INTERFACE_REGEX_LIST, INTERFACE_RANK, INTERFACE_MESSAGE)

        // 7. classes
        private const val CLASS = "class"
        private const val CLASS_RANK = 7
        private val CLASS_REGEX_LIST = makeRegexList(CLASS)
        private val CLASS_PARAMS = Params(CLASS_REGEX_LIST, CLASS_RANK, "")

        private fun makeRegexList(value: String): List<Regex> {
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

    /**
     * Sorting declarations by file's lines
     * node.uastDeclarations give elements in wrong order
     * https://github.com/JetBrains/intellij-community/blob/master/uast/uast-java/src/org/jetbrains/uast/java/declarations/JavaUClass.kt
     */

    override fun getApplicableUastTypes(): List<Class<out UElement?>>? {
        return listOf(UClass::class.java)
    }

    override fun createUastHandler(context: JavaContext): UElementHandler? {
        return object : UElementHandler() {
            override fun visitClass(node: UClass) {
                val listUDeclaration = node.uastDeclarations
                val name = node.name ?: return
                val lines = node.parent?.text?.lines() ?: return
                val sortedDeclarationList = getSortedDeclarationList(lines, listUDeclaration, name)

                if (name != COMPANION_NAME) {
                    var currentRank = 0
                    sortedDeclarationList.forEach { declaration ->
                        val text = declaration.text ?: return

                        /** 1) it's can find companion object*/
                        currentRank = checkOrder(text, currentRank, node, declaration, COMPANION_OBJECT_PARAMS)

                        /** 2) Variables*/
                        currentRank = checkOrder(text, currentRank, node, declaration, VAL_PARAMS)

                        currentRank = checkOrder(text, currentRank, node, declaration, VAR_PARAMS)

                        /** 3) Constructor */
                        currentRank = checkOrder(text, currentRank, node, declaration, CONSTRUCTOR_PARAMS)

                        /** 4 Function */
                        currentRank = checkOrder(text, currentRank, node, declaration, FUNCTION_PARAMS)

                        /** 5 Enum */
                        currentRank = checkOrder(text, currentRank, node, declaration, ENUM_PARAMS)

                        /** 6) Interface */
                        currentRank = checkOrder(text, currentRank, node, declaration, INTERFACE_PARAMS)

                        /** 7) Class */
                        val classRegex = CLASS_REGEX_LIST.firstOrNull { text.contains(it) }
                        if (classRegex != null) {
                            currentRank = CLASS_RANK
                        }
                    }
                }
            }

            private fun getSortedDeclarationList(
                lines: List<String>,
                listUDeclaration: List<UDeclaration>,
                name: String
            ): List<UDeclaration> {
                val list = mutableListOf<UDeclaration>()
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

                return list.distinctBy { it.text }
            }

            private fun checkOrder(text: String, currentRank: Int, node: UClass, declaration: UDeclaration, params: Params): Int {
                var isVariable = params.regexList.firstOrNull { text.contains(it) }
                if (isVariable != null) {
                    if (currentRank <= params.rank) {
                        return params.rank
                    } else {
                        makeContextReport(node, declaration, params.message)
                    }
                }
                return currentRank
            }

            private fun makeContextReport(node: UClass, declaration: UDeclaration, message: String) {
                context.report(
                    ISSUE,
                    node,
                    context.getNameLocation(declaration),
                    "$message ${ISSUE.getExplanation(TextFormat.TEXT)}"
                )
            }

        }
    }

    class Params(
        val regexList: List<Regex>,
        val rank: Int,
        val message: String
    )
}


