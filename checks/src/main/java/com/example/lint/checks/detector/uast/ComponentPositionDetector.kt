package com.example.lint.checks.detector.uast

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import org.jetbrains.uast.*
import java.lang.Integer.min

class ComponentPositionDetector : Detector(), Detector.UastScanner {
    companion object {
        /** Issue describing the problem and pointing to the detector implementation */
        @JvmField
        val ISSUE: Issue = Issue.create(
            // ID: used in @SuppressLint warnings etc
            id = "ComponentPositionDetector",
            // Title -- shown in the IDE's preference dialog, as category headers in the
            // Analysis results window, etc
            briefDescription = "The line size does not match the coding convention",
            // Full explanation of the issue; you can use some markdown markup such as
            // `monospace`, *italic*, and **bold**.
            explanation = """
                  Line should has 130 symbols or less
                    """,
            category = Category.CORRECTNESS,
            priority = 7,
            severity = Severity.WARNING,
            implementation = Implementation(
                ComponentPositionDetector::class.java,
                Scope.JAVA_FILE_SCOPE
            )
        )

        const val COMPANION_NAME = "Companion"

        // 1. companion object
        val COMPANION_OBJECT = Regex("^companion object")
        const val COMPANION_OBJECT_POSITION = 1
        const val COMPANION_OBJECT_MESSAGE = "companion object should be the first"

        // 2. val + var variables
        const val VAL = "val"
        const val VAR = "var"
        const val VARIABLES_POSITION = 2
        const val VARIABLES_MESSAGE =
            "Variables should be earlier than constructors, functions, enums, interfaces and classes"

        // 3. constructors and inits
        const val CONSTRUCTOR = "constructor"
        const val CONSTRUCTOR_POSITION = 3
        const val CONSTRUCTOR_MESSAGE = "Constructor should be earlier than functions, enums, interfaces and classes"

        // 4. functions
        const val FUNCTION = "fun"
        const val FUNCTION_POSITION = 4
        const val FUNCTION_MESSAGE = "Functions should be earlier than  enums, interfaces and classes"

        // 5. enums
        const val ENUM = "enum"
        const val ENUM_POSITION = 5
        const val ENUM_MESSAGE = "Enum should be earlier than interfaces and classes"

        // 6. interfaces
        const val INTERFACE = "interface"
        const val INTERFACE_POSITION = 6
        const val INTERFACE_MESSAGE = "Enum should be earlier than classes"

        // 7. classes
        const val CLASS = "class"
        const val CLASS_POSITION = 7

    }

    override fun getApplicableUastTypes(): List<Class<out UElement?>>? {
        return listOf(UClass::class.java)
    }

    override fun createUastHandler(context: JavaContext): UElementHandler? {
        // Note: Visiting UAST nodes is a pretty general purpose mechanism;
        // Lint has specialized support to do common things like "visit every class
        // that extends a given super class or implements a given interface", and
        // "visit every call site that calls a method by a given name" etc.
        // Take a careful look at UastScanner and the various existing lint check
        // implementations before doing things the "hard way".
        // Also be aware of context.getJavaEvaluator() which provides a lot of
        // utility functionality.

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
                        str += declaration.text + "!!!\n"
                        val text = declaration.text
                        if (text != null) {

                            /** 1) it's can find companion object*/

                            if (text.contains(COMPANION_OBJECT)) {
                                if (currentPosition <= COMPANION_OBJECT_POSITION) {
                                    currentPosition = COMPANION_OBJECT_POSITION
                                } else {
                                    context.report(ISSUE, node, context.getNameLocation(declaration), COMPANION_OBJECT_MESSAGE)
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
                                            ISSUE, node, context.getNameLocation(declaration), VARIABLES_MESSAGE +
                                                    currentPosition.toString()
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
                                            VARIABLES_MESSAGE + currentPosition.toString()
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
                                            ISSUE, node, context.getNameLocation(declaration), CONSTRUCTOR_MESSAGE +
                                                    currentPosition.toString()
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
                                        context.report(ISSUE, node, context.getNameLocation(declaration), FUNCTION_MESSAGE)
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
                                        context.report(ISSUE, node, context.getNameLocation(declaration), ENUM_MESSAGE)
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
                                        context.report(ISSUE, node, context.getNameLocation(declaration), INTERFACE_MESSAGE)
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
                //    context.report(ISSUE, node, context.getNameLocation(node), str)
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


