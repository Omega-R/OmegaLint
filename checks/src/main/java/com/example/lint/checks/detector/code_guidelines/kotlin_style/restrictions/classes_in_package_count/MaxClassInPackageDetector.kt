package com.example.lint.checks.detector.code_guidelines.kotlin_style.restrictions.classes_in_package_count

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*

import org.jetbrains.uast.UClass
import org.jetbrains.uast.UElement

class MaxClassInPackageDetector : Detector(), Detector.UastScanner {
    companion object {
        /** Issue describing the problem and pointing to the detector implementation */
        @JvmField
        val ISSUE: Issue = Issue.create(
            id = "OMEGA_NOT_EXCEED_MAX_CLASSES_IN_PACKAGE_COUNT",
            briefDescription = "The line size does not match the coding convention",
            explanation = """
                  The number of classes in the package should be no more than 30.
                  http://wiki.omega-r.club/dev-android-code#rec228196079
                    """,
            category = Category.CORRECTNESS,
            priority = 7,
            severity = Severity.WARNING,
            implementation = Implementation(
                MaxClassInPackageDetector::class.java,
                Scope.JAVA_FILE_SCOPE
            )
        )

        private const val PACKAGE_VAL = "package"

        private const val MAX_CLASSES_IN_PACKAGE_COUNT = 5

        private var classesMap = mutableMapOf<String, ArrayList<String>>()
    }

    override fun getApplicableUastTypes(): List<Class<out UElement?>>? {
        return listOf(UClass::class.java)
    }

    override fun createUastHandler(context: JavaContext): UElementHandler? {
        return object : UElementHandler() {
            override fun visitClass(node: UClass) {
                val file = node.uastParent ?: return
                val name = node.name ?: return
                val lines = file.asRenderString().split("\n")
                val packageString = lines.firstOrNull { it.contains(PACKAGE_VAL) } ?: return

                if (packageString.isNotEmpty()) {
                    val value = classesMap[packageString]
                    if (value == null) {
                        classesMap[packageString] = arrayListOf(name)
                    } else {
                        if (!value.contains(name)) {
                            value.add(name)
                            classesMap[packageString] = value
                        }

                        if (value.size > MAX_CLASSES_IN_PACKAGE_COUNT) {
                            context.report(
                                ISSUE,
                                node,
                                context.getNameLocation(node),
                                "${value.size} ${ISSUE.getExplanation(TextFormat.TEXT)}"
                            )
                        }
                    }
                }
            }
        }
    }
}

