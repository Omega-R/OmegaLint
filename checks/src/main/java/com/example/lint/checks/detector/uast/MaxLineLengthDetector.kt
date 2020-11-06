package com.example.lint.checks.detector.uast

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*

import org.jetbrains.uast.UClass
import org.jetbrains.uast.UElement

class MaxLineLengthDetector : Detector(), Detector.UastScanner {
    companion object {
        /** Issue describing the problem and pointing to the detector implementation */
        @JvmField
        val ISSUE: Issue = Issue.create(
            // ID: used in @SuppressLint warnings etc
            id = "MaxLineLength",
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
                MaxLineLengthDetector::class.java,
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
                val text = node.parent.text
                val lines = text.lines()
                var beginPosition = 0

                lines.forEach { line ->
                    val length = line.length

                    if ((length > 130) && !(line.contains("import")) && !(line.contains("package"))) {
                        context.report(
                            ISSUE, node, context.getRangeLocation(node.parent, beginPosition, length),
                            "The line contains more than 130 symbols. Divide this line."
                        )
                    }

                    beginPosition += length
                    beginPosition++ // add new string sym
                }
            }
        }
    }
}

