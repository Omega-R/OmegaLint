package com.example.lint.checks.detector.xml

import com.android.resources.ResourceFolderType
import com.android.tools.lint.detector.api.*
import com.android.utils.forEach
import org.w3c.dom.Document

class PositionAttributesXmlDetector : ResourceXmlDetector() {

    companion object {
        val ISSUE = Issue.create(
            id = "PositionAttributesXml",
            briefDescription = "Something",
            explanation = "Something",
            category = Category.CORRECTNESS,
            severity = Severity.WARNING,
            implementation = Implementation(
                PositionAttributesXmlDetector::class.java,
                Scope.RESOURCE_FILE_SCOPE
            )
        )
    }

    override fun appliesTo(folderType: ResourceFolderType): Boolean {
        return folderType == ResourceFolderType.LAYOUT
    }


    override fun visitDocument(context: XmlContext, document: Document) {

        val some = document.namespaceURI ?: return
        val el = document.documentElement ?: return
        val nodeList = el.childNodes ?: return

    /*nodeList.forEach {
          if (it != null) {
            context.report(
                issue = NameIdentifierXmlDetector.ISSUE,
                location = context.getNameLocation(el),
                message = some
            )
        }
      }
    */}
}