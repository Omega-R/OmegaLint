package com.example.lint.checks.detector.xml

import com.android.resources.ResourceFolderType
import com.android.tools.lint.detector.api.*
import com.android.utils.forEach
import org.jetbrains.kotlin.konan.file.File
import org.w3c.dom.Attr
import org.w3c.dom.Document
import org.w3c.dom.Element

class PositionAttributesXmlDetector : ResourceXmlDetector() {

    companion object {
        val ISSUE = Issue.create(
            id = "PositionAttributesXml",
            briefDescription = "Detects usages of 'Okay' in string resources",
            explanation = "The word 'OK' should be used instead of 'Okay' in string resources",
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

    override fun getApplicableAttributes(): Collection<String>? {
        return XmlScannerConstants.ALL
    }

    override fun visitDocument(context: XmlContext, document: Document) {
        context.report(
            issue = NameIdentifierXmlDetector.ISSUE,
            scope = document,
            location = context.getNameLocation(document),
            message = document.localName
        )

    }
}