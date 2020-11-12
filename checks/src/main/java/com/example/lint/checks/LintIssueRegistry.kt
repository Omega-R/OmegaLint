package com.example.lint.checks

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.detector.api.CURRENT_API
import com.android.tools.lint.detector.api.Issue
import com.example.lint.checks.detector.uast.*
import com.example.lint.checks.detector.xml.*

class LintIssueRegistry : IssueRegistry() {
    override val issues: List<Issue>
        get() = listOf(
            MaxLineLengthDetector.ISSUE,
            NameFileDetector.ISSUE,
            AbbreviationDetector.ISSUE,
            PositionArgumentDetector.ISSUE,
            MaxFunctionsArgumentsDetector.ISSUE,
            ExceptionCatchDetector.ISSUE,
            ComponentPositionDetector.ISSUE,
            NameIdentifierXmlDetector.ISSUE,
            NameResourceStringXmlDetector.ISSUE,
            NameResourceStyleXmlDetector.ISSUE,
            MaxMethodCountDetector.ISSUE,
            EmptyBodyFunctionDetector.ISSUE,
            NameValuesXmlDetector.ISSUE,
            NameDrawableXmlDetector.ISSUE,
            CompanionObjectFieldsDetector.ISSUE,
            MaxFunctionLengthDetector.ISSUE,
            MaxClassLengthDetector.ISSUE,
            NameResourceLayoutDetector.ISSUE
        )

    override val api: Int
        get() = CURRENT_API
}