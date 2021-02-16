package com.omegar.lint.checks.detector.code_guidelines.xml_style.name_resource.identifier

import com.android.resources.ResourceFolderType
import com.android.tools.lint.detector.api.*
import com.omegar.lint.checks.detector.project_guidelines.file_name.resource.layout.NameResourceLayoutDetector
import org.w3c.dom.Attr

class NameIdentifierXmlDetector : ResourceXmlDetector() {

	companion object {
		val ISSUE = Issue.create(
			id = "OMEGA_NAME_VARIABLES_CORRECTLY",
			briefDescription = "Detects wrongs name of view's identifier",
			explanation = """
                    Name of identifier should begin with prefix, which depends of view name.
                    http://wiki.omega-r.club/dev-android-code#rec228390320
                    """,
			category = Category.CORRECTNESS,
			priority = 6,
			severity = Severity.INFORMATIONAL,
			implementation = Implementation(
				NameIdentifierXmlDetector::class.java,
				Scope.RESOURCE_FILE_SCOPE
			)
		)

		private val CAMEL_REGEX = Regex("(?<=[a-zA-Z])[A-Z]")
		private const val REPORT_MESSAGE = "Wrong prefix of identifier name. Should begin with: "

		//elements
		private const val TEXT_VIEW_ELEMENT = "TextView"
		private const val IMAGE_VIEW_ELEMENT = "ImageView"
		private const val BUTTON_ELEMENT = "Button"
		private const val EDIT_TEXT_ELEMENT = "EditText"
		private const val LAYOUT_ELEMENT = "Layout"
		private const val TABLE_LAYOUT_ELEMENT = "TableLayout"
		private const val LINEAR_LAYOUT_ELEMENT = "LinearLayout"
		private const val FLOATING_ACTION_BUTTON_ELEMENT = "com.google.android.material.floatingactionbutton.FloatingActionButton"
		private const val IMAGE_BUTTON_ELEMENT = "ImageButton"

		//prefixes
		private const val TEXT_VIEW_PREFIX = "@+id/text"
		private const val IMAGE_VIEW_PREFIX = "@+id/image"
		private const val BUTTON_PREFIX = "@+id/button"
		private const val EDIT_TEXT_PREFIX = "@+id/input"
		private const val LAYOUT_PREFIX = "@+id/layout"
		private const val FLOATING_ACTION_BUTTON_PREFIX = "@+id/fab"
		private const val IMAGE_BUTTON_PREFIX = "@+id/button"
		private const val IDENTIFIER_PREFIX = "@+id/"
	}


	override fun appliesTo(folderType: ResourceFolderType): Boolean = folderType == ResourceFolderType.LAYOUT

	override fun getApplicableAttributes(): Collection<String>? = XmlScannerConstants.ALL

	override fun visitAttribute(context: XmlContext, attribute: Attr) {
		attribute.ownerElement ?: return
		val attributeValue = attribute.nodeValue ?: return
		if (attribute.name == "android:id") {
			when (attribute.ownerElement.tagName) {
				TEXT_VIEW_ELEMENT -> {
					if (!attributeValue.contains(TEXT_VIEW_PREFIX)) {
						makeContextReport(context, attribute, TEXT_VIEW_PREFIX)
					}
				}

				IMAGE_VIEW_ELEMENT -> {
					if (!attributeValue.contains(IMAGE_VIEW_PREFIX)) {
						makeContextReport(context, attribute, IMAGE_VIEW_PREFIX)
					}
				}

				BUTTON_ELEMENT -> {
					if (!attributeValue.contains(BUTTON_PREFIX)) {
						makeContextReport(context, attribute, BUTTON_PREFIX)
					}
				}

				EDIT_TEXT_ELEMENT -> {
					if (!attributeValue.contains(EDIT_TEXT_PREFIX)) {
						makeContextReport(context, attribute, EDIT_TEXT_PREFIX)
					}
				}

				LAYOUT_ELEMENT, TABLE_LAYOUT_ELEMENT, LINEAR_LAYOUT_ELEMENT -> {
					if (!attributeValue.contains(LAYOUT_PREFIX)) {
						makeContextReport(context, attribute, LAYOUT_PREFIX)
					}
				}

				FLOATING_ACTION_BUTTON_ELEMENT -> {
					if (!attributeValue.contains(FLOATING_ACTION_BUTTON_PREFIX)) {
						makeContextReport(context, attribute, FLOATING_ACTION_BUTTON_PREFIX)
					}
				}

				IMAGE_BUTTON_ELEMENT -> {
					if (!attributeValue.contains(IMAGE_BUTTON_PREFIX)) {
						makeContextReport(context, attribute, IMAGE_BUTTON_PREFIX)
					}
				}

				else -> {
					val tagName = IDENTIFIER_PREFIX + attribute.ownerElement.tagName.split(".").last().replace("View", "")
					val prefix = tagName.convertCamelToSnakeCase()
					if (!attributeValue.contains(prefix)) {
						makeContextReport(context, attribute, prefix)
					}
				}
			}
		}
	}

	// String extensions
	private fun String.convertCamelToSnakeCase(): String {
		return CAMEL_REGEX.replace(this) {
			it.value
		}.toLowerCase()
	}

	private fun makeContextReport(context: XmlContext, attribute: Attr, message: String) {
		context.report(
			issue = ISSUE,
			scope = attribute,
			location = context.getValueLocation(attribute),
			message = "$REPORT_MESSAGE $message\n${ISSUE.getExplanation(TextFormat.TEXT)}",
			quickfixData = createFix(attribute.nodeValue, message)
		)
	}

	private fun createFix(attributeValue: String, correctPrefix: String): LintFix {
		val attributeValueWithoutPrefix = attributeValue.replace(IDENTIFIER_PREFIX, "")
		return fix()
			.replace()
			.text(attributeValue)
			.with("${correctPrefix}_$attributeValueWithoutPrefix")
			.build()
	}
}