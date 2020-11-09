package com.example.lint.checks

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.detector.api.CURRENT_API
import com.android.tools.lint.detector.api.Issue
import com.example.lint.checks.detector.uast.*
import com.example.lint.checks.detector.xml.NameIdentifierXmlDetector
import com.example.lint.checks.detector.xml.NameResourceStringXmlDetector
import com.example.lint.checks.detector.xml.NameResourceStyleXmlDetector
import com.example.lint.checks.detector.xml.PositionAttributesXmlDetector

class LintIssueRegistry : IssueRegistry() {
    override val issues: List<Issue>
        get() = listOf(
            MaxLineLengthDetector.ISSUE,
            NameFileDetector.ISSUE,
            AbbreviationDetector.ISSUE,
            PositionArgumentDetector.ISSUE,
            FunctionsDetector.ISSUE,
            ExceptionCatchDetector.ISSUE,
            ComponentPositionDetector.ISSUE,
            NameIdentifierXmlDetector.ISSUE,
            NameResourceStringXmlDetector.ISSUE,
            NameResourceStyleXmlDetector.ISSUE,
            PositionAttributesXmlDetector.ISSUE,
            MaxMethodCountDetector.ISSUE,
            NameResourceLayoutDetector.ISSUE
        )

    override val api: Int
        get() = CURRENT_API
}