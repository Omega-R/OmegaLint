package com.example.lint.checks.detector.uast

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import org.jetbrains.uast.*

class NameResourceLayoutDetector : Detector(), Detector.UastScanner {
    companion object {
        /** Issue describing the problem and pointing to the detector implementation */
        @JvmField
        val ISSUE: Issue = Issue.create(
            // ID: used in @SuppressLint warnings etc
            id = "NameResourceLayout",
            // Title -- shown in the IDE's preference dialog, as category headers in the
            // Analysis results window, etc
            briefDescription = "Something",
            // Full explanation of the issue; you can use some markdown markup such as
            // `monospace`, *italic*, and **bold**.
            explanation = """
                  Something
                    """,
            category = Category.CORRECTNESS,
            priority = 7,
            severity = Severity.WARNING,
            implementation = Implementation(
                NameResourceLayoutDetector::class.java,
                Scope.JAVA_FILE_SCOPE
            )
        )
    }

    override fun getApplicableUastTypes(): List<Class<out UElement?>>? {
        return listOf(UCallExpression::class.java)
    }

    override fun createUastHandler(context: JavaContext): UElementHandler? {
        return object : UElementHandler() {
            override fun visitCallExpression(node: UCallExpression) {
                val file = node.getContainingUFile() ?: return
                var className = file.classes.firstOrNull()?.name ?: return
                val name = node.methodName ?: return
                if (name == "setContentView") {
                    var layoutName = node.valueArguments.firstOrNull()?.asRenderString() ?: return
                    layoutName = layoutName.replace("R.layout.", "")
                    if(className.contains("Activity")) {
                        className = "activity${className.replace("Activity", "")}"
                        if(className.camelToSnakeCase() != layoutName)
                        context.report(ISSUE, node, context.getNameLocation(node.valueArguments.first()), "Wrong layout name.")
                    }
                }

            }

            val camelRegex = "(?<=[a-zA-Z])[A-Z]".toRegex()
            val snakeRegex = "_[a-zA-Z]".toRegex()

            // String extensions
            fun String.camelToSnakeCase(): String {
                return camelRegex.replace(this) {
                    "_${it.value}"
                }.toLowerCase()
            }

            fun String.snakeToLowerCamelCase(): String {
                return snakeRegex.replace(this) {
                    it.value.replace("_", "")
                        .toUpperCase()
                }
            }

            fun String.snakeToUpperCamelCase(): String {
                return this.snakeToLowerCamelCase().capitalize()
            }


        }

    }
}

