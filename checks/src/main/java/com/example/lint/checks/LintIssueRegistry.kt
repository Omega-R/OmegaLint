package com.example.lint.checks

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.detector.api.CURRENT_API
import com.android.tools.lint.detector.api.Issue
import com.example.lint.checks.detector.uast.AbbreviationDetector
import com.example.lint.checks.detector.uast.ExceptionCatchDetector
import com.example.lint.checks.detector.uast.MaxArgumentsCountDetector
import com.example.lint.checks.detector.uast.MaxLineLengthDetector
import com.example.lint.checks.detector.uast.NameFileDetector
import com.example.lint.checks.detector.uast.PositionArgumentDetector

class LintIssueRegistry : IssueRegistry() {

    var listOfIssues = mutableListOf<Issue>()

    init {
        listOfIssues.add(NameFileDetector.ISSUE)
        listOfIssues.add(AbbreviationDetector.ISSUE)
        listOfIssues.add(PositionArgumentDetector.ISSUE)
        listOfIssues.add(MaxLineLengthDetector.ISSUE)
        listOfIssues.add(MaxArgumentsCountDetector.ISSUE)
        listOfIssues.add(ExceptionCatchDetector.ISSUE)
    }

    override val issues = listOfIssues

    override val api: Int
        get() = CURRENT_API
}