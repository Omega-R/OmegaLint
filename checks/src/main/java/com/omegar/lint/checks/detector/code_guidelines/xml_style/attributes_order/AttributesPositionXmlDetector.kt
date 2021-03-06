package com.omegar.lint.checks.detector.code_guidelines.xml_style.attributes_order

import com.android.resources.ResourceFolderType
import com.android.tools.lint.detector.api.*
import org.w3c.dom.Document
import java.lang.Integer.min

class AttributesPositionXmlDetector : ResourceXmlDetector() {

	companion object {
		val ISSUE = Issue.create(
			id = "OMEGA_USE_ATTRIBUTES_IN_CORRECT_ORDER",
			briefDescription = "Generally, you should try to group similar attributes together.",
			explanation = """
                You should try to group similar attributes together.
                http://wiki.omega-r.club/dev-android-code#rec228391990
                """,
			category = Category.CORRECTNESS,
			severity = Severity.WARNING,
			implementation = Implementation(
				AttributesPositionXmlDetector::class.java,
				Scope.RESOURCE_FILE_SCOPE
			)
		)

		private const val BEGIN_TAG_SYMBOL = "<"

		private val ANDROID_PREFIX_REGEX = Regex("""^\s*android:""")
		private val APP_PREFIX_REGEX = Regex("""^\s*app:""")
		private val TOOLS_PREFIX_REGEX = Regex("""^\s*tools:""")

		/** 0. XMLN for main layout */
		private val XMLNS_PREFIX_REGEX = Regex("""^\s*xmlns:""")
		private const val XMLNS_PREFIX_RANK = 0
		private const val XMLNS_MESSAGE = "xmlns attribute should stay before all tags."

		/** 1. View Id**/
		private val VIEW_ID_VAL = Regex("""^\s*android:id""")
		private const val ID_RANK = 1
		private const val ID_MESSAGE = "View Id should be the first attribute(if tag not contains \"xmlns\")."

		/** 2. Style**/
		private val STYLE_PREFIX_REGEX = Regex("""^\s*style""")
		private const val STYLE_RANK = 2
		private const val STYLE_MESSAGE = "Style attribute should be after View Id."

		/** 3. Layout width and layout height **/
		private const val LAYOUT_WIDTH_VAL = "layout_width"
		private const val LAYOUT_HEIGHT_VAL = "layout_height"
		private const val LAYOUT_H_W_RANK = 3
		private const val LAYOUT_H_W_MESSAGE = "Only View Id and Style can stay before layout_width or layout_height."

		/** 4. Another layout attributes sorted alphabetically **/
		private const val LAYOUT_VAL = "layout"
		private const val LAYOUT_RANK = 4
		private const val LAYOUT_MESSAGE =
			"Layout attribute should be after View Id, Style, layout_width or layout_height before another attributes " +
					"which not applicable to layout."
		private const val LAYOUT_ALPHABETICALLY_MESSAGE = "Layout attribute group should be sorted alphabetically."

		/** 5. Another attributes sorted alphabetically **/
		private const val ANOTHER_ATTRIBUTES_RANK = 5
		private const val ANOTHER_ATTRIBUTES_MESSAGE = "This attribute should be in last group."
		private const val ANOTHER_ATTRIBUTES_ALPHABETICALLY_MESSAGE = "Another attribute group should be sorted alphabetically."

		/**
		 * Checking get 2 params current rank and current string
		 * currentRank can be 1..5 wich corresponds last getted line
		 * Ranks [
		 * 1. View Id
		 * 2. Style
		 * 3 Layout width and layout height
		 * 4. Another layout attributes sorted alphabetically
		 * 5. Another attributes sorted alphabetically
		 * ]
		 *
		 *      RANK LOGIC      *
		 * If rank of next line will be less than current rank(current rank = previos rank), then lint will underline this Line
		 * If rank of next line will be equal than current rank(current rank = previos rank), then lint will sorted alphabetically
		 * If rank of next line will be more than current rank(current rank = previos rank), then change the current rank to
		 * rank of next line
		 *
		 */
	}

	override fun appliesTo(folderType: ResourceFolderType): Boolean = folderType == ResourceFolderType.LAYOUT

	override fun visitDocument(context: XmlContext, document: Document) {
		val text = document.textContent ?: return
		val tags = text.split(BEGIN_TAG_SYMBOL)
		var beginPosition = 0

		tags.forEach {
			val tag = it
			var currentRank = 0
			var previousAttribute = ""

			val attributes = tag.split("\n")

			attributes.forEach { attribute ->
				if (attribute.isNotEmpty()) {
					when {
						attribute.contains(XMLNS_PREFIX_REGEX) && currentRank != XMLNS_PREFIX_RANK -> {
							makeContextReport(context, document, beginPosition, XMLNS_MESSAGE, attribute)
						}

						attribute.contains(VIEW_ID_VAL) -> {
							currentRank =
								checkOrder(Params(context, document, beginPosition, ID_MESSAGE, attribute, currentRank, ID_RANK))
						}

						attribute.contains(STYLE_PREFIX_REGEX) -> {
							currentRank =
								checkOrder(
									Params(context, document, beginPosition, STYLE_MESSAGE, attribute, currentRank, STYLE_RANK)
								)
						}

						attribute.contains(LAYOUT_HEIGHT_VAL) || attribute.contains(LAYOUT_WIDTH_VAL) -> {
							currentRank =
								checkOrder(
									Params(
										context,
										document,
										beginPosition,
										LAYOUT_H_W_MESSAGE,
										attribute,
										currentRank,
										LAYOUT_H_W_RANK
									)
								)
						}

						attribute.contains(LAYOUT_VAL) &&
								(attribute.contains(ANDROID_PREFIX_REGEX) || attribute.contains(STYLE_PREFIX_REGEX)) -> {
							when {
								currentRank < LAYOUT_RANK -> {
									currentRank = LAYOUT_RANK
									previousAttribute = attribute
								}

								currentRank == LAYOUT_RANK -> {
									/** alphabet*/
									if (!isOrderedAlphabetically(attribute, previousAttribute)) {
										makeContextReport(
											context,
											document,
											beginPosition,
											LAYOUT_ALPHABETICALLY_MESSAGE,
											attribute
										)
									}
									previousAttribute = attribute
								}

								else -> {
									makeContextReport(context, document, beginPosition, LAYOUT_MESSAGE, attribute)
								}
							}
						}

						attribute.contains(ANDROID_PREFIX_REGEX)
								|| attribute.contains(APP_PREFIX_REGEX)
								|| attribute.contains(TOOLS_PREFIX_REGEX) -> {
							when {
								currentRank < ANOTHER_ATTRIBUTES_RANK -> {
									currentRank = ANOTHER_ATTRIBUTES_RANK
									previousAttribute = ""
								}

								currentRank == ANOTHER_ATTRIBUTES_RANK -> {
									/** alphabet*/
									if (!isOrderedAlphabetically(attribute, previousAttribute)) {
										makeContextReport(
											context,
											document,
											beginPosition,
											ANOTHER_ATTRIBUTES_ALPHABETICALLY_MESSAGE,
											attribute
										)
									}
									previousAttribute = attribute
								}

								else -> {
									makeContextReport(context, document, beginPosition, ANOTHER_ATTRIBUTES_MESSAGE, attribute)
								}
							}
						}
					}
				}
				beginPosition++ // for adding new string symbol+
				beginPosition += attribute.length

			}
		}
	}

	private fun checkOrder(params: Params): Int {
		if (params.currentRank <= params.viewRank) {
			return params.viewRank
		}

		makeContextReport(params.context, params.document, params.beginPosition, params.message, params.attribute)
		return params.currentRank
	}

	private fun makeContextReport(
		context: XmlContext,
		document: Document,
		beginPosition: Int,
		message: String,
		attribute: String
	) {
		context.report(
			ISSUE,
			document,
			context.getLocation(document, beginPosition, beginPosition + attribute.length),
			"$message\n${ISSUE.getExplanation(TextFormat.TEXT)}"
		)
	}

	private fun isOrderedAlphabetically(attribute: String, previousAttribute: String): Boolean {
		if (previousAttribute.isEmpty()) {
			return true
		}
		val attributeCharArray = attribute.toCharArray()
		val previousAttributeCharArray = previousAttribute.toCharArray()

		for (i in 0 until min(attributeCharArray.size, previousAttributeCharArray.size)) {
			if (attributeCharArray[i] < previousAttributeCharArray[i]) {
				return false
			} else if (attributeCharArray[i] != previousAttributeCharArray[i]) {
				return true
			}
		}
		return false
	}

	class Params(
		val context: XmlContext,
		val document: Document,
		val beginPosition: Int,
		val message: String,
		val attribute: String,
		val currentRank: Int,
		val viewRank: Int
	)
}