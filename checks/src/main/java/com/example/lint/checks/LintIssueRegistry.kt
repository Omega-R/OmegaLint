package com.example.lint.checks

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.detector.api.CURRENT_API
import com.android.tools.lint.detector.api.Issue
import com.example.lint.checks.detector.project_guidelines.file_name.`class`.NameFileUpperCamelCaseDetector
import com.example.lint.checks.detector.project_guidelines.file_name.resource.layout.NameResourceLayoutDetector
import com.example.lint.checks.detector.code_guidelines.kotlin_rules.exception.ExceptionCatchDetector
import com.example.lint.checks.detector.code_guidelines.kotlin_style.name.`class`.NameFileSufixDetector
import com.example.lint.checks.detector.code_guidelines.kotlin_style.name.field.CompanionObjectFieldsDetector
import com.example.lint.checks.detector.code_guidelines.kotlin_style.name.abbreviation.AbbreviationDetector
import com.example.lint.checks.detector.code_guidelines.kotlin_style.order.file_class_interface.ComponentPositionDetector
import com.example.lint.checks.detector.code_guidelines.kotlin_style.order.function_params.PositionArgumentDetector
import com.example.lint.checks.detector.code_guidelines.kotlin_style.restrictions.line_length.MaxLineLengthDetector
import com.example.lint.checks.detector.code_guidelines.kotlin_style.restrictions.params_count.MaxFunctionsArgumentsDetector
import com.example.lint.checks.detector.code_guidelines.kotlin_style.restrictions.function_length.MaxFunctionLengthDetector
import com.example.lint.checks.detector.code_guidelines.kotlin_style.restrictions.emptiness.EmptyBodyFunctionDetector
import com.example.lint.checks.detector.code_guidelines.kotlin_style.restrictions.class_length.MaxClassLengthDetector
import com.example.lint.checks.detector.code_guidelines.kotlin_style.restrictions.class_methods_count.MaxMethodCountDetector
import com.example.lint.checks.detector.code_guidelines.kotlin_style.use_spaces.around_operands.SpaceMethodDetector
import com.example.lint.checks.detector.code_guidelines.kotlin_style.simplifications.function.SimplificationsFunctionDetector
import com.example.lint.checks.detector.code_guidelines.kotlin_style.elements_formating.annotation.AnnotationDetector
import com.example.lint.checks.detector.code_guidelines.xml_style.name_resource.identifier.NameIdentifierXmlDetector
import com.example.lint.checks.detector.code_guidelines.xml_style.name_resource.resource.NameResourceStringXmlDetector
import com.example.lint.checks.detector.code_guidelines.xml_style.name_resource.theme_and_style.NameResourceStyleXmlDetector
import com.example.lint.checks.detector.code_guidelines.xml_style.attributes_order.AttributesPositionXmlDetector

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
            MaxMethodCountDetector.ISSUE, //
            EmptyBodyFunctionDetector.ISSUE,
            CompanionObjectFieldsDetector.ISSUE,
            MaxFunctionLengthDetector.ISSUE,
            MaxClassLengthDetector.ISSUE,
            SimplificationsFunctionDetector.ISSUE,
            AnnotationDetector.ISSUE,
            SpaceMethodDetector.ISSUE,
            NameFileSufixDetector.ISSUE,
            AttributesPositionXmlDetector.ISSUE,
            NameResourceLayoutDetector.ISSUE
        )

    override val api: Int
        get() = CURRENT_API
}