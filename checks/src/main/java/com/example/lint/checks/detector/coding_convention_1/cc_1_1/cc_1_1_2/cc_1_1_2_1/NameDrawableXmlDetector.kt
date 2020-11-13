package com.example.lint.checks.detector.coding_convention_1.cc_1_1.cc_1_1_2.cc_1_1_2_1

import com.android.manifmerger.XmlNode
import com.android.resources.ResourceFolderType
import com.android.tools.lint.detector.api.*
import org.w3c.dom.Document

class NameDrawableXmlDetector : ResourceXmlDetector() {

    companion object {
        val ISSUE = Issue.create(
            id = "NameResourceStyleXml",
            briefDescription = "Inheritance warning",
            explanation = """
                Resource files in the values folder must be multiple.
                http://wiki.omega-r.club/dev-android-code#rec226446713
                """,
            category = Category.CORRECTNESS,
            severity = Severity.INFORMATIONAL,
            implementation = Implementation(
                NameDrawableXmlDetector::class.java,
                Scope.RESOURCE_FILE_SCOPE
            )
        )
    }

    override fun appliesTo(folderType: ResourceFolderType): Boolean {
        return folderType == ResourceFolderType.DRAWABLE
    }


    override fun visitDocument(context: XmlContext, document: Document) {
//        val name = document.nodeName ?: return

        /*  val name = document.nodeName ?: return

          context.report(
              ISSUE,
              document,
              context.getNameLocation(document),
              name
          )
         */
    }
}