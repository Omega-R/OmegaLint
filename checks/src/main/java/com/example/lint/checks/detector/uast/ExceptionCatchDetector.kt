package com.example.lint.checks.detector.uast

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import org.jetbrains.uast.UCatchClause
import org.jetbrains.uast.UElement

@Suppress("UnstableApiUsage")
class ExceptionCatchDetector : Detector(), Detector.UastScanner {
    companion object {
        /** Issue describing the problem and pointing to the detector implementation */
        @JvmField
        val ISSUE: Issue = Issue.create(

            id = "ExceptionCatch",
            briefDescription = "Catch body is empty, it  not match the coding convention",
            explanation = """
                  Don't leave blank catch body.
                    """,
            category = Category.CORRECTNESS,
            priority = 7,
            severity = Severity.WARNING,
            implementation = Implementation(
                ExceptionCatchDetector::class.java,
                Scope.JAVA_FILE_SCOPE
            )
        )
    }

    override fun getApplicableUastTypes(): List<Class<out UElement?>>? {
        return listOf(UCatchClause::class.java)
    }

    override fun createUastHandler(context: JavaContext): UElementHandler? {
        return object : UElementHandler() {
            override fun visitCatchClause(node: UCatchClause) {
                val body = node.body
                val string = body.asRenderString()
                if (string == "{\n}") { //string.matches(Regex("{*\\b/\n\\b.*}))) {
                    context.report(
                        ISSUE,
                        body,
                        context.getNameLocation(body),
                        "Catch body is empty. Add exception handling."
                    )
                }
                val parameters = node.parameters
                parameters.forEach {
                    if (it.type.canonicalText == "java.lang.Exception") {
                        if (!string.contains("throw")) {
                            context.report(
                                ISSUE,
                                body,
                                context.getNameLocation(it),
                                "Catch generalized exception. Should throw specific exception in catch body"
                            )
                        }
                    }
                }
            }
        }
    }

}
