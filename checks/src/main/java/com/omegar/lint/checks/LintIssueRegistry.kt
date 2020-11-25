package com.omegar.lint.checks

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.detector.api.CURRENT_API
import com.android.tools.lint.detector.api.Issue
import com.omegar.lint.checks.detector.code_guidelines.general_recommendations.parameter_passing.argument_bundle_for_fragments_creation.ArgumentsBundleKeyPrefixDetector
import com.omegar.lint.checks.detector.code_guidelines.general_recommendations.parameter_passing.intent_creation.IntentExtraParametersDetector
import com.omegar.lint.checks.detector.code_guidelines.kotlin_rules.exception.ExceptionCatchDetector
import com.omegar.lint.checks.detector.code_guidelines.kotlin_style.elements_formating.annotation.AnnotationDetector
import com.omegar.lint.checks.detector.code_guidelines.kotlin_style.elements_formating.lambda.LambdaDetector
import com.omegar.lint.checks.detector.code_guidelines.kotlin_style.name.`class`.NameFileSufixDetector
import com.omegar.lint.checks.detector.code_guidelines.kotlin_style.name.abbreviation.AbbreviationDetector
import com.omegar.lint.checks.detector.code_guidelines.kotlin_style.name.field.CompanionObjectFieldsDetector
import com.omegar.lint.checks.detector.code_guidelines.kotlin_style.order.file_class_interface.ComponentPositionDetector
import com.omegar.lint.checks.detector.code_guidelines.kotlin_style.order.function_params.PositionArgumentDetector
import com.omegar.lint.checks.detector.code_guidelines.kotlin_style.restrictions.class_length.MaxClassLengthDetector
import com.omegar.lint.checks.detector.code_guidelines.kotlin_style.restrictions.class_methods_count.MaxMethodCountDetector
import com.omegar.lint.checks.detector.code_guidelines.kotlin_style.restrictions.emptiness.EmptyBodyFunctionDetector
import com.omegar.lint.checks.detector.code_guidelines.kotlin_style.restrictions.function_length.MaxFunctionLengthDetector
import com.omegar.lint.checks.detector.code_guidelines.kotlin_style.restrictions.line_length.MaxLineLengthDetector
import com.omegar.lint.checks.detector.code_guidelines.kotlin_style.restrictions.params_count.MaxFunctionsArgumentsDetector
import com.omegar.lint.checks.detector.code_guidelines.kotlin_style.simplifications.control_instructions.SimplificationsControlInstructionsDetector
import com.omegar.lint.checks.detector.code_guidelines.kotlin_style.simplifications.function.SimplificationsFunctionDetector
import com.omegar.lint.checks.detector.code_guidelines.kotlin_style.use_spaces.around_operands.SpaceMethodDetector
import com.omegar.lint.checks.detector.code_guidelines.xml_style.attributes_order.AttributesPositionXmlDetector
import com.omegar.lint.checks.detector.code_guidelines.xml_style.name_resource.identifier.NameIdentifierXmlDetector
import com.omegar.lint.checks.detector.code_guidelines.xml_style.name_resource.resource.NameResourceStringXmlDetector
import com.omegar.lint.checks.detector.code_guidelines.xml_style.name_resource.theme_and_style.NameResourceStyleXmlDetector
import com.omegar.lint.checks.detector.project_guidelines.file_name.`class`.NameFileUpperCamelCaseDetector
import com.omegar.lint.checks.detector.project_guidelines.file_name.resource.layout.NameResourceLayoutDetector

class LintIssueRegistry : IssueRegistry() {
    override val issues: List<Issue>
        get() = listOf(
            NameFileUpperCamelCaseDetector.ISSUE,
            AbbreviationDetector.ISSUE,// TODO need testing
            PositionArgumentDetector.ISSUE,
            MaxFunctionsArgumentsDetector.ISSUE,
            ExceptionCatchDetector.ISSUE,
            ComponentPositionDetector.ISSUE,
            NameIdentifierXmlDetector.ISSUE,
            NameResourceStringXmlDetector.ISSUE,
            NameResourceStyleXmlDetector.ISSUE,
            MaxMethodCountDetector.ISSUE,
            EmptyBodyFunctionDetector.ISSUE,
            CompanionObjectFieldsDetector.ISSUE,
            MaxFunctionLengthDetector.ISSUE,
            MaxClassLengthDetector.ISSUE,
            SimplificationsFunctionDetector.ISSUE,
            MaxLineLengthDetector.ISSUE,
            AnnotationDetector.ISSUE,
            SpaceMethodDetector.ISSUE,
            NameFileSufixDetector.ISSUE,
            AttributesPositionXmlDetector.ISSUE,
//            MaxClassInPackageDetector.ISSUE,  TODO this working only for classes which user has visited after rebuild, need to fix, and count all classes
//            MaxPackageCountDetector.ISSUE,    TODO this working only for classes which user has visited after rebuild, need to fix, and count all classes
            SimplificationsControlInstructionsDetector.ISSUE, // TODO need testing
            IntentExtraParametersDetector.ISSUE,
            ArgumentsBundleKeyPrefixDetector.ISSUE,
            LambdaDetector.ISSUE,
            NameResourceLayoutDetector.ISSUE
        )

    override val api: Int
        get() = CURRENT_API
}
