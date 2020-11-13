package com.example.lint.checks

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.detector.api.CURRENT_API
import com.android.tools.lint.detector.api.Issue
import com.example.lint.checks.detector.coding_convention_1.cc_1_1.cc_1_1_1.NameFileUpperCamelCaseDetector
import com.example.lint.checks.detector.coding_convention_1.cc_1_1.cc_1_1_2.cc_1_1_2_1.NameDrawableXmlDetector
import com.example.lint.checks.detector.coding_convention_1.cc_1_1.cc_1_1_2.cc_1_1_2_2.NameResourceLayoutDetector
import com.example.lint.checks.detector.coding_convention_1.cc_1_1.cc_1_1_2.cc_1_1_2_3.NameValuesXmlDetector
import com.example.lint.checks.detector.coding_convention_2.cc_2_1.cc_2_1_1_and_2_1_2.ExceptionCatchDetector
import com.example.lint.checks.detector.coding_convention_2.cc_2_2.cc_2_2_1.сс_2_2_1_2.NameFileSufixDetector
import com.example.lint.checks.detector.coding_convention_2.cc_2_2.cc_2_2_1.сс_2_2_1_3.CompanionObjectFieldsDetector
import com.example.lint.checks.detector.coding_convention_2.cc_2_2.cc_2_2_1.сс_2_2_1_6.AbbreviationDetector
import com.example.lint.checks.detector.coding_convention_2.cc_2_2.cc_2_2_2.cc_2_2_2_1_and_2_2_2_2_and_2_2_2_3.ComponentPositionDetector
import com.example.lint.checks.detector.coding_convention_2.cc_2_2.cc_2_2_2.cc_2_2_2_4.PositionArgumentDetector
import com.example.lint.checks.detector.coding_convention_2.cc_2_2.cc_2_2_3.cc_2_2_3_1.MaxLineLengthDetector
import com.example.lint.checks.detector.coding_convention_2.cc_2_2.cc_2_2_3.cc_2_2_3_2.MaxFunctionsArgumentsDetector
import com.example.lint.checks.detector.coding_convention_2.cc_2_2.cc_2_2_3.cc_2_2_3_4.MaxFunctionLengthDetector
import com.example.lint.checks.detector.coding_convention_2.cc_2_2.cc_2_2_3.cc_2_2_3_5.EmptyBodyFunctionDetector
import com.example.lint.checks.detector.coding_convention_2.cc_2_2.cc_2_2_3.cc_2_2_3_6.MaxClassLengthDetector
import com.example.lint.checks.detector.coding_convention_2.cc_2_2.cc_2_2_3.cc_2_2_3_7.MaxMethodCountDetector
import com.example.lint.checks.detector.coding_convention_2.cc_2_2.cc_2_2_4.cc_2_2_4_2.SpaceMethodDetector
import com.example.lint.checks.detector.coding_convention_2.cc_2_2.cc_2_2_5.cc_2_2_5_1.SimplificationsFunctionDetector
import com.example.lint.checks.detector.coding_convention_2.cc_2_2.cc_2_2_6.cc_2_2_6_2.AnnotationDetector
import com.example.lint.checks.detector.coding_convention_2.cc_2_3.cc_2_3_2.cc_2_3_2_1.NameIdentifierXmlDetector
import com.example.lint.checks.detector.coding_convention_2.cc_2_3.cc_2_3_2.cc_2_3_2_2.NameResourceStringXmlDetector
import com.example.lint.checks.detector.coding_convention_2.cc_2_3.cc_2_3_2.cc_2_3_2_3.NameResourceStyleXmlDetector

class LintIssueRegistry : IssueRegistry() {
    override val issues: List<Issue>
        get() = listOf(
            MaxLineLengthDetector.ISSUE,
            NameFileUpperCamelCaseDetector.ISSUE,
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
            SimplificationsFunctionDetector.ISSUE,
            AnnotationDetector.ISSUE,
            SpaceMethodDetector.ISSUE,
            NameFileSufixDetector.ISSUE,
            NameResourceLayoutDetector.ISSUE
        )

    override val api: Int
        get() = CURRENT_API
}