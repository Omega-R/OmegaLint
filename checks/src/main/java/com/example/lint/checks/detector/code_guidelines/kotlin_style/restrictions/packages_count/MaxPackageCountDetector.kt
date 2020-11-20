package com.example.lint.checks.detector.code_guidelines.kotlin_style.restrictions.packages_count

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*

import org.jetbrains.uast.UClass
import org.jetbrains.uast.UElement

class MaxPackageCountDetector : Detector(), Detector.UastScanner {
    companion object {
        /** Issue describing the problem and pointing to the detector implementation */
        @JvmField
        val ISSUE: Issue = Issue.create(
            id = "OMEGA_NOT_EXCEED_MAX_PACKAGE_COUNT",
            briefDescription = "The number of packages should be no more than 30.",
            explanation = """
                  The number of packages should be no more than 30.
                  http://wiki.omega-r.club/dev-android-code#rec228200275
                    """,
            category = Category.CORRECTNESS,
            priority = 7,
            severity = Severity.WARNING,
            implementation = Implementation(
                MaxPackageCountDetector::class.java,
                Scope.JAVA_FILE_SCOPE
            )
        )

        private const val PACKAGE_VAL = "package"

        private const val MAX_CLASSES_IN_PACKAGE_COUNT = 30

        private var packageMap = mutableMapOf<String, ArrayList<String>>()
    }

    override fun getApplicableUastTypes(): List<Class<out UElement?>>? {
        return listOf(UClass::class.java)
    }

    override fun createUastHandler(context: JavaContext): UElementHandler? {
        return object : UElementHandler() {
            override fun visitClass(node: UClass) {
                val file = node.uastParent ?: return
                val lines = file.asRenderString().split("\n")
                val packageString = lines.firstOrNull { it.contains(PACKAGE_VAL) } ?: return

                if (packageString.isNotEmpty()) {
                    val path = packageString.replace(PACKAGE_VAL, "").trim()
                    val packagesList = path.split(".")
                    for (i in packagesList.indices) {
                        val currentPackage = packagesList[i]
                        if (i < packagesList.size - 1) {
                            val nextPackagesList = packageMap[currentPackage]
                            val nextPackage = packagesList[i + 1]
                            if (nextPackagesList != null) {

                                if (!nextPackagesList.contains(nextPackage)) {
                                    nextPackagesList.add(nextPackage)
                                    packageMap.replace(currentPackage, nextPackagesList)
                                    if (nextPackagesList.size > MAX_CLASSES_IN_PACKAGE_COUNT) {
                                        context.report(
                                            ISSUE,
                                            node,
                                            context.getRangeLocation(file, 0, packageString.length),
                                            ISSUE.getExplanation(TextFormat.TEXT)
                                        )
                                    }
                                }
                            } else {
                                packageMap[currentPackage] = arrayListOf(nextPackage)
                            }
                        }
                    }
                }
            }
        }
    }
}

