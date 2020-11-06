package com.example.lint.checks.detector.xml

import com.android.resources.ResourceFolderType
import com.android.tools.lint.detector.api.*
import org.w3c.dom.Element
import org.w3c.dom.Node

class NameResourceStringXmlDetector : ResourceXmlDetector() {

    companion object {
        val ISSUE = Issue.create(
            id = "NameResourceStringXml",
            briefDescription = "Detects usages of 'Okay' in string resources",
            explanation = "The word 'OK' should be used instead of 'Okay' in string resources",
            category = Category.CORRECTNESS,
            severity = Severity.ERROR,
            implementation = Implementation(
                NameResourceStringXmlDetector::class.java,
                Scope.RESOURCE_FILE_SCOPE
            )
        )

        const val APP_NAME = "app_name"
        const val ERROR_PREFIX = "error"
        const val MESSAGE_PREFIX = "message"
        const val TITLE_PREFIX = "title"
        const val LABEL_PREFIX = "label"
        const val BUTTON_PREFIX = "button"
        const val ACTION_PREFIX = "action"
        const val HINT_PREFIX = "hint"

        const val ATTRIBUTE_NAME_VAL = "name"//Attribute
    }

    override fun appliesTo(folderType: ResourceFolderType): Boolean {
        // Return true if we want to analyze resource files in the specified resource
        // folder type. In this case we only need to analyze strings in the 'values'
        // resource folder.
        return folderType == ResourceFolderType.VALUES
    }

    override fun getApplicableElements(): Collection<String>? {
        // Return the set of elements we want to analyze. In this case we want to
        // analyze every `<string>` element that is declared in XML.
        return setOf("string")
    }

    override fun visitElement(context: XmlContext, element: Element) {
        if (!element.hasChildNodes()) {
            // <string> elements should always have a single child node
            // (the string text), but double check just to be safe.
            return
        }

        val textNode = element.firstChild ?: return
        if (textNode.nodeType != Node.TEXT_NODE) {
            // The first child of a `<string>` element should always be a text
            // node, but double check just to be safe.
            return
        }

        val stringText = element.getAttribute(ATTRIBUTE_NAME_VAL) ?: return

        if ((stringText == APP_NAME) ||
            (stringText.contains(ERROR_PREFIX)) ||
            (stringText.contains(MESSAGE_PREFIX)) ||
            (stringText.contains(TITLE_PREFIX)) ||
            (stringText.contains(LABEL_PREFIX)) ||
            (stringText.contains(BUTTON_PREFIX)) ||
            (stringText.contains(ACTION_PREFIX)) ||
            (stringText.contains(HINT_PREFIX))

        ) {
            return
        }
        context.report(
            issue = ISSUE,
            scope = element,
            location = context.getLocation(element.getAttributeNode(ATTRIBUTE_NAME_VAL)),
            message = "String resource should begin with prefix which defines the group to which they belong."
        )

    }
}