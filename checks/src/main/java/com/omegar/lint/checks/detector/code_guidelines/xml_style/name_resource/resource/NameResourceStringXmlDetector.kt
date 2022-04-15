package com.omegar.lint.checks.detector.code_guidelines.xml_style.name_resource.resource

import com.android.resources.ResourceFolderType
import com.android.tools.lint.detector.api.*
import org.w3c.dom.Element
import org.w3c.dom.Node

@Suppress("UnstableApiUsage")
class NameResourceStringXmlDetector : ResourceXmlDetector() {

	companion object {
		val ISSUE = Issue.create(
			id = "OMEGA_NAME_RESOURCE_STRING_CORRECTLY",
			briefDescription = "String resource should begin with prefix",
			explanation = """
                String resource should begin with prefix which defines the group to which they belong.
                http://wiki.omega-r.club/dev-android-code#rec228390838
                """,
			category = Category.CORRECTNESS,
			severity = Severity.WARNING,
			implementation = Implementation(
				NameResourceStringXmlDetector::class.java,
				Scope.RESOURCE_FILE_SCOPE
			)
		)

		private const val APP_NAME = "app_name"

		private val CORRECT_PREFIXES_LIST = listOf(
			"error",
			"message",
			"title",
			"label",
			"button",
			"action",
			"hint",
			"format",
			"mask"
		)

		private const val ATTRIBUTE_NAME_VAL = "name" //Attribute
	}

	override fun appliesTo(folderType: ResourceFolderType): Boolean = folderType == ResourceFolderType.VALUES

	override fun getApplicableElements(): Collection<String> = setOf("string")

	override fun visitElement(context: XmlContext, element: Element) {
		if (!element.hasChildNodes()) {
			return
		}

		val textNode = element.firstChild ?: return
		if (textNode.nodeType != Node.TEXT_NODE) {
			return
		}

		val stringText = element.getAttribute(ATTRIBUTE_NAME_VAL) ?: return

		if ((stringText == APP_NAME) || CORRECT_PREFIXES_LIST.firstOrNull { stringText.contains(it) } != null) {
			return
		}

		context.report(
			ISSUE,
			element,
			context.getLocation(element.getAttributeNode(ATTRIBUTE_NAME_VAL)),
			ISSUE.getExplanation(TextFormat.TEXT),
			createFix(stringText)
		)
	}

	private fun createFix(stringText: String): LintFix {
		val groupFix = fix().group()
		CORRECT_PREFIXES_LIST.forEach {
			groupFix.add(
				fix().replace().text(stringText).with("${it}_$stringText").build()
			)
		}
		return groupFix.build()

	}
}