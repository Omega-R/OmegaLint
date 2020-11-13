package com.example.lint.checks.detector.coding_convention_1.cc_1_1.cc_1_1_2.cc_1_1_2_3

import com.android.resources.ResourceFolderType
import com.android.tools.lint.detector.api.*
import com.example.lint.checks.detector.coding_convention_2.cc_2_3.cc_2_3_2.cc_2_3_2_2.NameResourceStringXmlDetector
import com.intellij.psi.PsiElement
import org.w3c.dom.Document

class NameValuesXmlDetector : ResourceXmlDetector() {

    companion object {
        val ISSUE = Issue.create(
            id = "NameResourceStyleXml",
            briefDescription = "Inheritance warning",
            explanation = """
                Resource files in the values folder must be multiple.
                http://wiki.omega-r.club/dev-android-code#rec226446713
                """,
            category = Category.CORRECTNESS,
            severity = Severity.WARNING,
            implementation = Implementation(
                NameValuesXmlDetector::class.java,
                Scope.RESOURCE_FILE_SCOPE
            )
        )
    }

    override fun appliesTo(folderType: ResourceFolderType): Boolean {
        return folderType == ResourceFolderType.VALUES
    }

    override fun visitDocument(context: XmlContext, document: Document) {
/*
        val s = document.
            ?: return
        context.report(
            NameResourceStringXmlDetector.ISSUE,
            document,
            context.getLocation(document),
            s
        )

 */
    }
}