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
import com.omegar.lint.checks.detector.code_guidelines.kotlin_style.order.class_interface.ComponentPositionDetector
import com.omegar.lint.checks.detector.code_guidelines.kotlin_style.order.file_package.PackageComponentDetector
import com.omegar.lint.checks.detector.code_guidelines.kotlin_style.order.function_params.ArgumentPositionDetector
import com.omegar.lint.checks.detector.code_guidelines.kotlin_style.restrictions.class_length.MaxClassLengthDetector
import com.omegar.lint.checks.detector.code_guidelines.kotlin_style.restrictions.class_methods_count.MaxMethodCountDetector
import com.omegar.lint.checks.detector.code_guidelines.kotlin_style.restrictions.classes_in_package_count.MaxClassInPackageDetector
import com.omegar.lint.checks.detector.code_guidelines.kotlin_style.restrictions.emptiness.EmptyBodyFunctionDetector
import com.omegar.lint.checks.detector.code_guidelines.kotlin_style.restrictions.function_length.MaxFunctionLengthDetector
import com.omegar.lint.checks.detector.code_guidelines.kotlin_style.restrictions.line_length.MaxLineLengthDetector
import com.omegar.lint.checks.detector.code_guidelines.kotlin_style.restrictions.packages_count.MaxPackageCountDetector
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

@Suppress("UnstableApiUsage")
class LintIssueRegistry : IssueRegistry() {
	override val issues: List<Issue>
		get() = listOf(
            NameFileUpperCamelCaseDetector.ISSUE,
            AbbreviationDetector.ISSUE, // TODO need rewriting & lint quick fix
            ArgumentPositionDetector.ISSUE, // TODO add lint quick fix
            MaxFunctionsArgumentsDetector.ISSUE, // TODO ??lint quick fix??
            ExceptionCatchDetector.ISSUE,
            ComponentPositionDetector.ISSUE, //TODO add lint quick fix for
            PackageComponentDetector.ISSUE,
            NameIdentifierXmlDetector.ISSUE,
            NameResourceStringXmlDetector.ISSUE,
            NameResourceStyleXmlDetector.ISSUE, //TODO change lint quick fix for
            MaxMethodCountDetector.ISSUE, // TODO ??lint quick fix??
            EmptyBodyFunctionDetector.ISSUE, //TODO add lint quick fix for
            CompanionObjectFieldsDetector.ISSUE,
            MaxFunctionLengthDetector.ISSUE, // TODO ??lint quick fix??
            MaxClassLengthDetector.ISSUE, // TODO ??lint quick fix??
            SimplificationsFunctionDetector.ISSUE,
            MaxLineLengthDetector.ISSUE, // TODO ??lint quick fix??
            AnnotationDetector.ISSUE,
            SpaceMethodDetector.ISSUE,
            NameFileSufixDetector.ISSUE,
            AttributesPositionXmlDetector.ISSUE,
            MaxClassInPackageDetector.ISSUE, // TODO ??lint quick fix??
            MaxPackageCountDetector.ISSUE,   // TODO ??lint quick fix??
            SimplificationsControlInstructionsDetector.ISSUE, //TODO add lint quick fix for
            IntentExtraParametersDetector.ISSUE,
            ArgumentsBundleKeyPrefixDetector.ISSUE,
            LambdaDetector.ISSUE, //TODO add lint quick fix for
            NameResourceLayoutDetector.ISSUE //TODO add lint quick fix for
        )

	override val api: Int
		get() = CURRENT_API
}
