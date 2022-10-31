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

        private val BIND_PATTERN = Regex("bind(.*)(\\()(.*)(R.id.).*(\\))")

        // add underscore to regular expression
        private val ID_PATTERN = Regex("R.id.[A-Za-z]*")

    }

    override fun getApplicableUastTypes(): List<Class<out UElement?>> = listOf(UClass::class.java)

    override fun createUastHandler(context: JavaContext): UElementHandler? =
        object : UElementHandler() {
            override fun visitClass(node: UClass) {

                val text = node.text
                val items = BIND_PATTERN
                    .findAll(text)
                    .map { bindItem ->
                        val match = ID_PATTERN.find(bindItem.value)!!
                        BindableItem(
                            bindItem.range.first,
                            bindItem.value.length,
                            match.value
                        )
                    }
                    .groupBy { it.content }

                items.forEach { (_, list) ->
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
        }

    private data class BindableItem(
        val startPosition: Int,
        val length: Int,
        val content: String
    )
}