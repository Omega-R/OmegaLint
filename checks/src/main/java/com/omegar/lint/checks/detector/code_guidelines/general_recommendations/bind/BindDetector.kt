package com.omegar.lint.checks.detector.code_guidelines.general_recommendations.bind

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import org.jetbrains.uast.UClass
import org.jetbrains.uast.UElement

@Suppress("UnstableApiUsage")
class BindDetector : Detector(), Detector.UastScanner {

    companion object {

        @JvmField
        val ISSUE: Issue = Issue.create(
            id = "OMEGA_KEEP_UNIQUE_ID_IN_BINDING",
            briefDescription = "Only unique id is allowed for each binding",
            explanation = """
                Replace repetitive id in duplicate binding.
            """,
            category = Category.CORRECTNESS,
            severity = Severity.WARNING,
            implementation = Implementation(
                BindDetector::class.java,
                Scope.JAVA_FILE_SCOPE
            )
        )

        private val BIND_PATTERN = Regex("bind *\\( *(R *. *id *\\. *[A-Za-z0-9_]+)( *,.*)?\\)")

    }

    override fun getApplicableUastTypes(): List<Class<out UElement?>> = listOf(UClass::class.java)

    override fun createUastHandler(context: JavaContext): UElementHandler? =
        object : UElementHandler() {
            override fun visitClass(node: UClass) = BIND_PATTERN
                .findAll(node.text)
                .map { item ->
                    item.groups[1]?.let { match ->
                        BindableItem(
                            match.range.first,
                            match.value.length,
                            match.value.filter { !it.isWhitespace() }
                        )
                    }
                }
                .filterNotNull()
                .groupBy { it.content }
                .forEach { (_, list) ->
                    if (list.size > 1) {
                        context.report(
                            ISSUE,
                            node,
                            context.getRangeLocation(
                                node as UElement,
                                list.last().startPosition,
                                list.last().length
                            ),
                            ISSUE.getExplanation(TextFormat.TEXT) + " ${list.last().content}"
                        )
                    }
                }
        }

    private data class BindableItem(
        val startPosition: Int,
        val length: Int,
        val content: String
    )
}