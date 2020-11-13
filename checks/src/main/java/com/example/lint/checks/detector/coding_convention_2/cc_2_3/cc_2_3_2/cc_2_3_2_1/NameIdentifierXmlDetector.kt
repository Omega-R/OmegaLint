package com.example.lint.checks.detector.coding_convention_2.cc_2_3.cc_2_3_2.cc_2_3_2_1

import com.android.resources.ResourceFolderType
import com.android.tools.lint.detector.api.*
import org.w3c.dom.Attr

class NameIdentifierXmlDetector : ResourceXmlDetector() {

    companion object {
        val ISSUE = Issue.create(
            id = "NameIdentifierXml",
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

        const val REPORT_MESSAGE = "Wrong prefix of identifier name. Should begin with: "

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
    }


    override fun appliesTo(folderType: ResourceFolderType): Boolean {
        // Return true if we want to analyze resource files in the specified resource
        // folder type. In this case we only need to analyze layout resource files.
        return folderType == ResourceFolderType.LAYOUT
    }

    override fun getApplicableAttributes(): Collection<String>? {
        // Return the set of attribute names we want to analyze. The `visitAttribute` method
        // below will be called each time lint sees one of these attributes in a
        // layout XML resource file. In this case, we want to analyze every attribute
        // in every layout XML resource file.
        return XmlScannerConstants.ALL
    }

    override fun visitAttribute(context: XmlContext, attribute: Attr) {
        attribute.ownerElement ?: return
        val attributeValue = attribute.nodeValue ?: return
        if (attribute.name == "android:id") {
            when (attribute.ownerElement.tagName) {
                TEXT_VIEW_ELEMENT -> {
                    if (!attributeValue.contains(TEXT_VIEW_PREFIX)) {
                        context.report(
                            issue = ISSUE,
                            scope = attribute,
                            location = context.getValueLocation(attribute),
                            message = "$REPORT_MESSAGE $TEXT_VIEW_PREFIX\n${ISSUE.getExplanation(TextFormat.TEXT)}"
                        )
                    }
                }

                IMAGE_VIEW_ELEMENT -> {
                    if (!attributeValue.contains(IMAGE_VIEW_PREFIX)) {
                        context.report(
                            issue = ISSUE,
                            scope = attribute,
                            location = context.getValueLocation(attribute),
                            message = "$REPORT_MESSAGE $IMAGE_VIEW_PREFIX\n${ISSUE.getExplanation(TextFormat.TEXT)}"
                        )
                    }
                }

                BUTTON_ELEMENT -> {
                    if (!attributeValue.contains(BUTTON_PREFIX)) {
                        context.report(
                            issue = ISSUE,
                            scope = attribute,
                            location = context.getValueLocation(attribute),
                            message = "$REPORT_MESSAGE $BUTTON_PREFIX\n${ISSUE.getExplanation(TextFormat.TEXT)}"
                        )
                    }
                }

                EDIT_TEXT_ELEMENT -> {
                    if (!attributeValue.contains(EDIT_TEXT_PREFIX)) {
                        context.report(
                            issue = ISSUE,
                            scope = attribute,
                            location = context.getValueLocation(attribute),
                            message = "$REPORT_MESSAGE $EDIT_TEXT_PREFIX\n${ISSUE.getExplanation(TextFormat.TEXT)}"
                        )
                    }
                }

                LAYOUT_ELEMENT, TABLE_LAYOUT_ELEMENT, LINEAR_LAYOUT_ELEMENT -> {
                    if (!attributeValue.contains(LAYOUT_PREFIX)) {
                        context.report(
                            issue = ISSUE,
                            scope = attribute,
                            location = context.getValueLocation(attribute),
                            message = "$REPORT_MESSAGE $LAYOUT_PREFIX\n${ISSUE.getExplanation(TextFormat.TEXT)}"
                        )
                    }
                }

                FLOATING_ACTION_BUTTON_ELEMENT -> {
                    if (!attributeValue.contains(FLOATING_ACTION_BUTTON_PREFIX)) {
                        context.report(
                            issue = ISSUE,
                            scope = attribute,
                            location = context.getValueLocation(attribute),
                            message = "$REPORT_MESSAGE $FLOATING_ACTION_BUTTON_PREFIX\n${ISSUE.getExplanation(TextFormat.TEXT)}"
                        )
                    }
                }

                IMAGE_BUTTON_ELEMENT -> {
                    if (!attributeValue.contains(IMAGE_BUTTON_PREFIX)) {
                        context.report(
                            issue = ISSUE,
                            scope = attribute,
                            location = context.getValueLocation(attribute),
                            message = "$REPORT_MESSAGE $IMAGE_BUTTON_PREFIX\n${ISSUE.getExplanation(TextFormat.TEXT)}"
                        )
                    }
                }
            }
        }
    }
}