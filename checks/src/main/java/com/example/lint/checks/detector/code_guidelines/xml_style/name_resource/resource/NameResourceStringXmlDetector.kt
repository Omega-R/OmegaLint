package com.example.lint.checks.detector.code_guidelines.xml_style.name_resource.resource

import com.android.resources.ResourceFolderType
import com.android.tools.lint.detector.api.*
import org.w3c.dom.Element
import org.w3c.dom.Node

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
            severity = Severity.ERROR,
            implementation = Implementation(
                NameResourceStringXmlDetector::class.java,
                Scope.RESOURCE_FILE_SCOPE
            )
        )

        private const val APP_NAME = "app_name"
        private const val ERROR_PREFIX = "error"
        private const val MESSAGE_PREFIX = "message"
        private const val TITLE_PREFIX = "title"
        private const val LABEL_PREFIX = "label"
        private const val BUTTON_PREFIX = "button"
        private const val ACTION_PREFIX = "action"
        private const val HINT_PREFIX = "hint"

        private const val ATTRIBUTE_NAME_VAL = "name"//Attribute
    }

    override fun appliesTo(folderType: ResourceFolderType): Boolean {
        return folderType == ResourceFolderType.VALUES
    }

    override fun getApplicableElements(): Collection<String>? {
        return setOf("string")
    }

    override fun visitElement(context: XmlContext, element: Element) {
        if (!element.hasChildNodes()) {
            return
        }

        val textNode = element.firstChild ?: return
        if (textNode.nodeType != Node.TEXT_NODE) {
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
            ISSUE,
            element,
            context.getLocation(element.getAttributeNode(ATTRIBUTE_NAME_VAL)),
            ISSUE.getExplanation(TextFormat.TEXT)
        )
    }
}