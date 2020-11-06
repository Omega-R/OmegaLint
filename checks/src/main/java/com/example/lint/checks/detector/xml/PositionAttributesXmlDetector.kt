package com.example.lint.checks.detector.xml

import com.android.resources.ResourceFolderType
import com.android.tools.lint.detector.api.*
import com.android.utils.forEach
import org.w3c.dom.Attr
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
        context.report(
            issue = NameIdentifierXmlDetector.ISSUE,
            scope = attribute,
            location = context.getLocation(attribute),
            message = attributeValue
        )

    }
}